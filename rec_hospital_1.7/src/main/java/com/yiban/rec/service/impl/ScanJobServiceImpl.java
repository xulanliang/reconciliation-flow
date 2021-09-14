package com.yiban.rec.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.EmailConfig;
import com.yiban.rec.emailbill.service.EmailBillService;
import com.yiban.rec.emailbill.service.impl.EmailBillServiceImpl;
import com.yiban.rec.service.BlendRefundJobService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.SchedulerJobService;
import com.yiban.rec.util.DateUtil;

/**
 * 查询配置文件中的定时器配置
 */
@Service
public class ScanJobServiceImpl {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// scanJobTime定时器执行的频率，N分钟一次， RATE=N-1,目前只考虑一分钟一次的情况，其他情况有问题
	private static final Integer RATE = 1;

	// 记录服务器启动时间
	private static final String startTime = DateUtil.transferDateToDateFormat("yyyy-MM-dd HH:mm", new Date());

	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@Autowired
	private SchedulerJobService schedulerJobService;

	@Autowired
	private BlendRefundJobService blendRefundJobService;

	public void scanJobTask() {
		// 只比较时:分
		String nowHHMM = DateUtil.transferDateToString("HH:mm", new Date());
		// 自动拉取账单异常，重新拉取账单, hh:mm:ss
		String rechannelJobTime = propertiesConfigService.findValueByPkey(ProConstants.rechannelJobTime,
				ProConstants.DEFAULT.get(ProConstants.rechannelJobTime));
		if (StringUtils.isNotBlank(rechannelJobTime)) {
			reChannelJobTask(rechannelJobTime, nowHHMM);
		}

		// 拉取账单, hh:mm:ss
		String billParseTime = propertiesConfigService.findValueByPkey(ProConstants.billParseTime,
				ProConstants.DEFAULT.get(ProConstants.billParseTime));
		if (StringUtils.isNotBlank(billParseTime)) {
			billParseJobTask(billParseTime, nowHHMM);
		}

		// 拉取his账单，通过银医接口, hh:mm:ss
		String hisJobTime = propertiesConfigService.findValueByPkey(ProConstants.hisJobTime,
				ProConstants.DEFAULT.get(ProConstants.hisJobTime));
		if (StringUtils.isNotBlank(hisJobTime)) {
			getHisBillJobTask(hisJobTime, nowHHMM);
		}

		// 自动对账, hh:mm:ss
		String autoRecJobTime = propertiesConfigService.findValueByPkey(ProConstants.autoRecJobTime,
				ProConstants.DEFAULT.get(ProConstants.autoRecJobTime));
		if (StringUtils.isNotBlank(autoRecJobTime)) {
			autoRecJobTask(autoRecJobTime, nowHHMM);
		}

		// 获取his结算账单, hh:mm:ss
		String hisSettlementJobTime = propertiesConfigService.findValueByPkey(ProConstants.hisSettlementJobTime);
		if (StringUtils.isNotBlank(hisSettlementJobTime)) {
			getHisSettlementBillJobTask(hisSettlementJobTime, nowHHMM);
		}

		// 获取市二交易明细定时任务, hh:mm:ss
		String hisTradeDetailJobTime = propertiesConfigService.findValueByPkey(ProConstants.hisTradeDetailJobTime);
		if (StringUtils.isNotBlank(hisTradeDetailJobTime)) {
			getSEHisTradeDetailJobTask(hisTradeDetailJobTime, nowHHMM);
		}

		// 邮件账单解析任务, hh:mm:ss
		String emailbillTime = propertiesConfigService.findValueByPkey(ProConstants.emailbillTime);
		if (StringUtils.isNotBlank(emailbillTime)) {
			emailBillParseTask(emailbillTime, nowHHMM);
		}

//		public static final String exceptionRefundTime = "exceptionRefund.job.time";
		// 异常退费失败任务，隔N分钟执行一次，N>=1
		String normalRefundTime = propertiesConfigService.findValueByPkey(ProConstants.normalRefundTime);
		if (StringUtils.isNotBlank(normalRefundTime) && Integer.parseInt(normalRefundTime) >= 1) {
			blendNormalRefundJob(Integer.parseInt(normalRefundTime));
		}

		// 正常退费失败任务，隔N分钟执行一次，N>=1
		String exceptionRefundTime = propertiesConfigService.findValueByPkey(ProConstants.exceptionRefundTime);
		if (StringUtils.isNotBlank(exceptionRefundTime) && Integer.parseInt(exceptionRefundTime) >= 1) {
			blendExceptionRefundJob(Integer.parseInt(exceptionRefundTime));
		}

		// 提前循环去下载邮箱账单到本地系统
		String emailBillDownloadTime = propertiesConfigService.findValueByPkey(ProConstants.emailBillDownloadTime);
		if (StringUtils.isNotBlank(emailBillDownloadTime) && Integer.parseInt(emailBillDownloadTime) >= 1) {
			emailBillLoopDownload(Integer.parseInt(emailBillDownloadTime));
		}

		// 测试3分钟一次
//		Integer time = 3;
//		test(time);
	}

	public void test(Integer time) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar start = Calendar.getInstance();
		start.setTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm", startTime));

		Long diff = now.getTimeInMillis() - start.getTimeInMillis();
		Long minute = diff / 60 / 1000;
		logger.info("++++++++++++++++++ 相隔: {}, 取余等于：{}", minute, minute % time);
		if (minute % time >= RATE) {
			return;
		}
		logger.info("++++++++++++++++++ 执行逻辑 +++++++++++++++++++++++++++");
	}

	public void reChannelJobTask(String rechannelJobTime, String nowHHMM) {
		if (rechannelJobTime.startsWith(nowHHMM)) {
			schedulerJobService.reChannelJobTask();
		}
	}

	/**
	 * 自动对账定时任务
	 */
	public void autoRecJobTask(String autoRecJobTime, String nowHHMMSS) {
		if (autoRecJobTime.startsWith(nowHHMMSS)) {
			schedulerJobService.autoRecJobTask();
		}
	}

	/**
	 * 调用账单服务，获取所有账单
	 */
	public void billParseJobTask(String billParseTime, String nowHHMM) {
		if (billParseTime.startsWith(nowHHMM)) {
			schedulerJobService.billParseJobTask();
		}
	}

	/**
	 * 通过银医接口获取his账单 void
	 */
	public void getHisBillJobTask(String hisJobTime, String nowHHMM) {
		if (hisJobTime.startsWith(nowHHMM)) {
			schedulerJobService.getHisBillJobTask();
		}
	}

	/**
	 * 通过银医接口获取his结算清单数据 void
	 */
	public void getHisSettlementBillJobTask(String hisSettlementJobTime, String nowHHMM) {
		if (hisSettlementJobTime.startsWith(nowHHMM)) {
			schedulerJobService.getHisSettlementBillJobTask();
		}
	}

	public void getSEHisTradeDetailJobTask(String hisTradeDetailJobTime, String nowHHMM) {
		if (hisTradeDetailJobTime.startsWith(nowHHMM)) {
		}
	}

	public void emailBillParseTask(String emailbillTime, String nowHHMM) {
		if (!emailbillTime.startsWith(nowHHMM)) {
			return;
		}
		HospitalConfigService hospitalConfigService = SpringBeanUtil.getBean(HospitalConfigService.class);
		AppRuntimeConfig webConfig = hospitalConfigService.loadConfig();
		if (webConfig != null) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			String filePath = propertiesConfigService.findValueByPkey(ProConstants.applicationUpload, "c:/clear/bill/");
			EmailConfig emailConfig = new EmailConfig();
			emailConfig.setFrom(webConfig.getFrom());
			emailConfig.setHost(webConfig.getHost());
			emailConfig.setOrgCode(orgCode);
			emailConfig.setPassword(webConfig.getPassword());
			emailConfig.setPort(webConfig.getPort());
			emailConfig.setType(webConfig.getType());
			emailConfig.setUserName(webConfig.getUserName());
			EmailBillService emailParseService = new EmailBillServiceImpl(filePath, emailConfig);
			// 今天日期当做邮件日期
			String emailDate = propertiesConfigService.findValueByPkey(ProConstants.emailbillDate,
					DateUtil.getCurrentDateString());
			if (logger.isInfoEnabled()) {
				logger.info("开始银行账单解析任务,日期:{}", emailDate);
			}
			// 解析昨天的账单
			emailParseService.parseBill(emailDate, emailDate);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("解析失败，请检查邮件配置是否正确");
			}
		}
	}

	/**
	 * 异常退费失败逻辑
	 */
	public void blendNormalRefundJob(Integer normalRefundTime) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar start = Calendar.getInstance();
		start.setTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm", startTime));

		Long diff = now.getTimeInMillis() - start.getTimeInMillis();
		Long minute = diff / 60 / 1000;
		if (minute % normalRefundTime >= RATE) {
			return;
		}
		blendRefundJobService.BlendNormalRefundJob();
	}

	/**
	 * 正常退费失败逻辑
	 */
	public void blendExceptionRefundJob(Integer exceptionRefundTime) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar start = Calendar.getInstance();
		start.setTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm", startTime));

		Long diff = now.getTimeInMillis() - start.getTimeInMillis();
		Long minute = diff / 60 / 1000;
		if (minute % exceptionRefundTime >= RATE) {
			return;
		}
		blendRefundJobService.BlendExceptionRefundJob();
	}

	public void emailBillLoopDownload(Integer emailBillDownloadTime) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar start = Calendar.getInstance();
		start.setTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm", startTime));

		Long diff = now.getTimeInMillis() - start.getTimeInMillis();
		Long minute = diff / 60 / 1000;
		if (minute % emailBillDownloadTime >= RATE) {
			return;
		}

		// 只在凌晨三点到中午12点之间下载
		int hour = now.get(Calendar.HOUR_OF_DAY);
//		if (hour < 3 || hour > 12) {
//			return;
//		}
		schedulerJobService.emailBillLoopDownload();
	}
}
