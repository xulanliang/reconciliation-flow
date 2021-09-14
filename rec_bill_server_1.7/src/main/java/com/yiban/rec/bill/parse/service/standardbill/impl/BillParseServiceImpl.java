package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.utils.date.DateUtils;
import com.yiban.rec.bill.parse.dao.ParseTaskConfigDao;
import com.yiban.rec.bill.parse.domain.ParseTaskConfig;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.BillParseService;
import com.yiban.rec.bill.parse.service.standardbill.BillParserable;
import com.yiban.rec.bill.parse.service.standardbill.HisReportSummaryService;
import com.yiban.rec.dao.RecLogDao;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.LogCons;

/**
 * @author swing
 * @date 2018年8月3日 上午10:12:14 类说明 账单获取统一接口实现(将多种账单来源的实现聚合一起执行)
 */

@Service
public class BillParseServiceImpl implements BillParseService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ParseTaskConfigDao parseTaskConfigDao;

    private Map<String, BillParserable> parserMap = new TreeMap<String, BillParserable>();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RecLogDao recLogDao;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private HisReportSummaryService hisReportSummaryService;

    private ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 初始化所有账单解析实现类
     *
     * @return
     */
//	@PostConstruct
    private Map<String, BillParserable> initTask() throws Exception {
        parserMap.clear();
        List<ParseTaskConfig> taskConfigList = parseTaskConfigDao.findByActive(ActiveEnum.YES.getValue());
        for (ParseTaskConfig config : taskConfigList) {
            String className = config.getTarget();
            if (StringUtils.isNotBlank(className)) {
                Class<?> clazz = Class.forName(className);
                BillParserable service = (BillParserable) clazz.newInstance();
                parserMap.put(config.getId() + "," + config.getPayType(), service);
            }
        }
        log.info("--账单链-->>" + parserMap);
        return parserMap;
    }

    /**
     * 执行账单解析链
     */
    @Override
    @Transactional()
    public void parse(String orgCode, String date) throws BillParseException {
        RecLog recLog = recLogDao.findByOrderDateAndOrgCode(date, orgCode);
        if (null == recLog) {
            recLog = new RecLog();
        }
        recLog.setRecResult(LogCons.REC_SUCCESS);
        recLog.setCreatedDate(DateUtil.getCurrentDateTime());
        recLog.setOrgCode(orgCode);
        recLog.setOrderDate(date);
        // 初始化
        try {
            initTask();
        } catch (Exception e1) {
            log.error("初始化解析实现类异常, {}", e1);
        }
        try {
            for (Map.Entry<String, BillParserable> entry : parserMap.entrySet()) {
                log.info("解析器： {} {}", entry.getValue(), entry.getKey());
                entry.getValue().parse(orgCode, date, entityManager, entry.getKey().split(",")[1]);
            }
        } catch (BillParseException e) {
            recLog.setRecResult(LogCons.REC_FAIL);
            log.error("拉取解析账单发生异常：", e.getMessage());
            throw e;
        } finally {

            // 异步 账单汇总
            // TODO 1、只拉取渠道账单   2、有些医院不需要拉取账单  、时 his要不要重新汇总
            executor.execute(new Runnable() {
                public void run() {
                    // 对账汇总
                    hisReportSummaryService.summary(orgCode, date);
                }
            });

            executor.execute(new Runnable() {
                public void run() {
                    // his报表汇总
                    hisReportSummaryService.hisSummary(orgCode, date);
                }
            });

            recLogDao.saveAndFlush(recLog);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult startParse(String orgCode, String beginDate, String endDate) throws BillParseException {
        int d = 0;
        if (StringUtils.isBlank(orgCode)) {
            return ResponseResult.failure("机构编码不能为空");
        }
        if (StringUtils.isBlank(beginDate)) {
            return ResponseResult.failure("开始时间不能为空");
        }
        if (StringUtils.isBlank(endDate)) {
            return ResponseResult.failure("结束时间不能为空");
        }
        if (!isDate(beginDate)) {
            return ResponseResult.failure("开始日期格式不符合:yyyy-MM-dd");
        }
        if (!isDate(endDate)) {
            return ResponseResult.failure("结束日期格式不符合:yyyy-MM-dd");
        }
        Date d1 = DateUtils.transferStringToDate("yyyy-MM-dd", beginDate);
        Date d2 = DateUtils.transferStringToDate("yyyy-MM-dd", endDate);
        if (d1.after(d2)) {
            return ResponseResult.failure("开始日期不能大于结束日期");
        }
        List<String> dateList = new ArrayList<>();

        while (true) {
            String date = DateUtils.getSpecifiedDayAfter(beginDate, d);
            d++;
            dateList.add(date);
            if (date.equals(endDate)) {
                break;
            }
        }
        try {
            for (String date : dateList) {
                log.info("======机构编码:{},账单日期:{}======", orgCode, date);
                Set<String> sets = getAllChildrenOrgs(orgCode);
                for (String org : sets) {
                    this.parse(org, date);
                }
            }
        } catch (Exception e) {
            log.error("拉取账单异常", e);
            throw e;
        }
        return ResponseResult.success("账单拉取成功");
    }

    /**
     * 获取所有子机构和本身机构的集合
     *
     * @param orgCode
     * @return List<String>
     */
    private Set<String> getAllChildrenOrgs(String orgCode) {
        Set<String> sets = new HashSet<>();
        List<Organization> orgList = organizationService.findByParentCode(orgCode);
        for (Organization o : orgList) {
            String org = o.getCode();
            sets.add(org);
        }
        sets.add(orgCode);
        return sets;
    }

    private boolean isDate(String date) {
        /**
         * 判断日期格式和范围
         */
        String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(date);
        boolean dateType = mat.matches();
        return dateType;
    }

}
