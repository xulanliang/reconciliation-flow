package com.yiban.rec.emailbill.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.emailbill.service.ThirdBillBlbService;
import com.yiban.rec.service.ReportSummaryService;
import com.yiban.rec.service.base.BaseOprService;

/**
 * @Description ThirdBillBlbServiceImpl
 * @Author liugenlai
 * @Date 2020/5/12 17:59
 * @Version 1.0
 */
@Service
public class ThirdBillBlbServiceImpl extends BaseOprService implements ThirdBillBlbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdBillBlbServiceImpl.class);

    @PersistenceContext
    protected EntityManager em;
    @Autowired
    private ThirdBillDao thirdBillDao;

    @Autowired
    private OrganizationService organizationService;

    @Override
    public List<Map<String, Object>> findAllOfSummaryByPayType(ReportSummaryService.SummaryQuery query) {
        // 机构处理
        Set<String> orgCodeSet = initOrgCodeSet(query.getOrgCode());
        String orgCode = StringUtils.join(orgCodeSet.toArray(), ",");
        //时间处理
        String startTime = query.getBeginTime().trim() + " 00:00:00";
        String endTime = query.getEndTime().trim() + " 23:59:59";
        // 查询sql拼接
        String sql = String.format(
                " select *, "
                + "	(wechatAmountAdd-wechatAmountSub) wechatAmount, "
                + " (zfbAmountAdd-zfbAmountSub) zfbAmount "
                + " from ( select  "
                + query.getColumnSql() + " businessType,"
                + " sum(case when pay_type = '0249' and order_state = '0156' then abs(Pay_Amount) else 0 end) wechatAmountAdd, "
                + " sum(case when pay_type = '0249' and order_state = '0256' then abs(Pay_Amount) else 0 end) wechatAmountSub, "
                + " sum(case when pay_type = '0249' then 1 else 0 end ) wechatAccount, "

                + " SUM( CASE WHEN pay_type = '0349' AND order_state = '0156' THEN abs( Pay_Amount ) ELSE 0 END ) zfbAmountAdd, "
                + " SUM( CASE WHEN pay_type = '0349' AND order_state = '0256' THEN abs( Pay_Amount ) ELSE 0 END ) zfbAmountSub, "
                + " SUM( CASE WHEN pay_type = '0349' THEN 1 ELSE 0 END ) zfbAcount "

                + " FROM t_thrid_bill_blb "
                + " WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no IN(%s)"
                + " GROUP BY businessType	"

                + " UNION "

                + " SELECT "
                + " '合计' businessType,"
                + " SUM(CASE WHEN pay_type='0249' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountAdd,"
                + " SUM(CASE WHEN pay_type='0249' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) wechatAmountSub,"
                + " SUM(CASE WHEN pay_type='0249' THEN 1 ELSE 0 END) wechatAcount,"

                + " SUM(CASE WHEN pay_type='0349' and order_state='0156' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountAdd,"
                + " SUM(CASE WHEN pay_type='0349' and order_state='0256' THEN abs(Pay_Amount) ELSE 0 END) zfbAmountSub,"
                + " SUM(CASE WHEN pay_type='0349' THEN 1 ELSE 0 END) zfbAcount"

                + " FROM t_thrid_bill_blb "
                + " WHERE Trade_datatime >= '%s' and Trade_datatime<='%s' AND org_no IN(%s)"
                + "  ) t "
                , startTime, endTime, orgCode, startTime, endTime, orgCode);
        LOGGER.info(" 病历本汇总报表 sql: " + sql);
        return super.queryList(sql, null, null);
    }

    /**
     * 获取子机构编码（如果有）
     */
    private Set<String> initOrgCodeSet(String orgCode) {
        Set<String> orgCodeSet = new HashSet<>();
        if (orgCode != null) {
            orgCodeSet.add(orgCode);
            Organization org = organizationService.findByCode(orgCode);
            if (org != null) {
                for (Organization child : org.getChildren()) {
                    orgCodeSet.add(child.getCode());
                }
            }
        }
        return orgCodeSet;
    }
}
