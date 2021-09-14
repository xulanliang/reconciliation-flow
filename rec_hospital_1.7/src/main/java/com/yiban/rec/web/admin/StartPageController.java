package com.yiban.rec.web.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.StartPageService;
import com.yiban.rec.service.WelcomePageService;
import com.yiban.rec.util.DateUtil;



/**
 * 启动页
 *
 */
@Controller
@RequestMapping("/admin")
public class StartPageController  extends CurrentUserContoller{
	
	@Autowired
	private StartPageService startPageService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	
	@Autowired
	private WelcomePageService welcomePageService;
	
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	@GetMapping("/welcome")
	public String index(ModelMap model) {
	    final String CRON_DATE_FORMAT = "HH:mm:ss";
        String autoRecJobTime = propertiesConfigService.findValueByPkey(ProConstants.autoRecJobTime, ProConstants.DEFAULT.get(ProConstants.autoRecJobTime));
		Date date = DateUtil.addMinute(DateUtil.stringLineToDateTime(autoRecJobTime , CRON_DATE_FORMAT), 10);
        String recHMS = DateUtil.transferDateToString(CRON_DATE_FORMAT, date);
        if(StringUtils.isNotBlank(recHMS)) {
            String[] ds = recHMS.split(" ");
            if(ds.length == 3) {
                recHMS = ds[2] + ":" + ds[1] + ":" + ds[0];
            }
            model.addAttribute("autoRecJobTime", recHMS);
            String recDate = welcomePageService.getRecDate();
            model.addAttribute("recDate",recDate);
            model.addAttribute("isShowTimeBox",date.getTime()>=DateUtil.transferDateToDate("HH-mm-ss",new Date()).getTime());
        }else {
            model.addAttribute("autoRecJobTime", "");
            model.addAttribute("recDate","");
        }
        List<String> dateList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            dateList.add(DateUtil.getSpecifiedDayBeforeDay(new Date(), i));
        }
        
        AppRuntimeConfig hospConfig = hospitalConfigService.loadConfig();
        String recType = hospConfig.getRecType();
        if(recType==null){
        	recType="";
        }
        List<MetaData> billSources = metaDataService.findByTypeIdOrderBySort("bill_source");
//        Collections.sort(billSources,new BeanComparator<>("sort"));
        model.addAttribute("billSources",billSources);
        model.addAttribute("hasCashRec",recType.contains("0049"));
        model.addAttribute("hasHealthCareRec",recType.contains("0559"));
        
        model.addAttribute("dateList", dateList);
        String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
        model.addAttribute("orgCode", orgCode);
        model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		return "admin/welcome";
	}
	
	@RestController
	@RequestMapping({"/admin/start/startPage/data"})
	class StartPageDoController extends BaseController {
		
	    /**
	     * 首页七天以及当天异常数据汇总
	     * @param orgNo
	     * @param date
	     * @return
	     * ResponseResult
	     */
		@GetMapping
		public ResponseResult getRecInfo(String orgNo, String date) {
		    Long beginTime = System.currentTimeMillis();
			User user = currentUser();
			ResponseResult rs = ResponseResult.success();
			Map<String, Object> resultData = new HashMap<>();
			final CountDownLatch countDownLatch = new CountDownLatch(2);
			try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, Object> dataOf7Day = startPageService.getRecInfo(orgNo,user);
                            resultData.putAll(dataOf7Day);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, Object> dateData = startPageService.getRecInfoByDay(date,orgNo);
                            resultData.putAll(dateData);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                }).start();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    logger.error("首页数据加载，执行线程发生异常: ",e);
                }				
				rs.data(resultData);
			} catch (Exception e) {
				rs = ResponseResult.failure("异常");
				logger.info("统计数据发生异常：", e);
				return rs;
			}
			Long endTime = System.currentTimeMillis();
	        logger.info("首页数据统计，总耗时： " + (endTime-beginTime) + " 毫秒");
			return rs;
		}
		
		/**
		 * 根据日期查询对账信息
		 * @param orgNo
		 * @param startDate,endDate
		 * @return
		 */
		@GetMapping("/date")
		public ResponseResult getRecInfoByDay(String orgNo, String date) {
			ResponseResult rs = ResponseResult.success();
			try {
			    Map<String, Object> data = startPageService.getRecInfoByDay(date,orgNo);
				rs.data(data);
			} catch (Exception e) {
				rs = ResponseResult.failure("异常");
				e.printStackTrace();
				return rs;
			}
			
			return rs;
		}
		
		/**
		 * 查询进三个月异常的日期
		 * @param orgNo
		 * @param startDate,endDate
		 * @return
		 */
		@GetMapping("/exceptionDate")
		public ResponseResult getExceptionDate(String orgNo) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			if(null == orgNo || "".equals(orgNo)){
				orgNo = orgCode;
			}
			String startDate = DateUtil.getSpecifiedDayBeforeMonth(new Date(),3);
			String endDate = DateUtil.getSpecifiedDayBefore(new Date());
			ResponseResult rs = ResponseResult.success();
			try {
				AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
				if(hConfig.getRecType() == null ){
					return ResponseResult.failure("请配置对账类型");
				}
				
//				hConfig.setOrgCode(orgNo);
				if(orgNo == null || orgNo.trim().equals("")){
					orgNo = orgCode;
				}
				List<RecLog> list = startPageService.getExceptionDate(startDate,endDate,hConfig);
				rs.data(list);
				
			} catch (Exception e) {
				rs = ResponseResult.failure("异常");
				e.printStackTrace();
				return rs;
			}
			
			return rs;
		}
		
		/**
		 * 查询每日异常信息
		 * @param orgNo
		 * @param startDate,endDate
		 * @return
		 */
		@PostMapping
		public ResponseResult getExceptionInfo(String orgNo , String date) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			if(null == orgNo || "".equals(orgNo)){
				orgNo = orgCode;
			}
			if(null == date || "".equals(date)){
				date = DateUtil.getSpecifiedDayBefore(new Date());
			}
			
			ResponseResult rs = ResponseResult.success();
			try {
				AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
				if(hConfig.getRecType() == null ){
					return ResponseResult.failure("请配置对账类型");
				}
				
//				hConfig.setOrgCode(orgNo);
				if(orgNo == null || orgNo.trim().equals("")){
					orgNo = orgCode;
				}
				rs.data(startPageService.getExceptionInfo(date,date,hConfig));
				
			} catch (Exception e) {
				rs = ResponseResult.failure("异常");
				e.printStackTrace();
				return rs;
			}
			
			return rs;
		}
		
		
	}
}
