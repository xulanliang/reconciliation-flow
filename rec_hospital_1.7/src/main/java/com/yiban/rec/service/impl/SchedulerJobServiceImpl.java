package com.yiban.rec.service.impl;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.util.StringUtil;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.BillParseService;
import com.yiban.rec.dao.RecLogDao;
import com.yiban.rec.domain.basicInfo.HisChannelParaSendInfo;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.domain.task.ChannelScheduleInfo;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.emailbill.service.ThirdBillService;
import com.yiban.rec.reconciliations.ReconciliationsService;
import com.yiban.rec.service.ChannelScheduleInfoService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.SchedulerJobService;
import com.yiban.rec.service.settlement.RecHisSettlementService;
import com.yiban.rec.task.tracker.EmailBillDownloadService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.JsonUtil;
import com.yiban.rec.util.LogCons;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

import net.sf.json.JSONObject;

@Service
public class SchedulerJobServiceImpl implements SchedulerJobService{

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerJobServiceImpl.class);
	@Autowired
	private ChannelScheduleInfoService channelScheduleInfoService;
	
	@Autowired
	private MetaDataService metaDataService;
	
//	@Autowired
//	private AutoReconciliationService autoReconciliationService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	@Autowired
	private ReconciliationsService reconciliationsService;
	@Autowired
	private BillParseService billParseService;
	
	@Autowired
	private ThirdBillService thirdBillService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private RecLogDao recLogDao;
	
	@Autowired
	private RecHisSettlementService recHisSettlementService;

	@Autowired
	private EmailBillDownloadService emailBillDownloadService;
	
	@Override
	public void reChannelJobTask() {
		try {
			LOGGER.info("获取账单数据执行重试失败任务业务逻辑.........");
			// 获取his账单失败的任务
			List<RecLog> recFails = recLogDao.findByRecResult(LogCons.REC_FAIL);
			if(CollectionUtils.isEmpty(recFails)) {
			    LOGGER.info("无异常对账记录");
			    return;
			}
			for (RecLog recLog : recFails) {
			    HisChannelParaSendInfo hisChannelParaSendInfo = new HisChannelParaSendInfo();
                hisChannelParaSendInfo.setOrg_no(String.valueOf(recLog.getOrgCode()));
                hisChannelParaSendInfo.setPay_date(recLog.getOrderDate());
                hisChannelParaSendInfo.setTrade_code(EnumType.TRADE_CODE.getValue());
                String message = JsonUtil.bean2json(hisChannelParaSendInfo);
                LOGGER.info("message........."+message);
                IPaymentService iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
                String result = iPaymentService.entrance(message);
                LOGGER.info("获取账单数据执行重试任务返回........."+result);
            }
		} catch (Exception e) {
			LOGGER.info("reChannelTask Run job failed!", e);
		}
	}
	
	@Override
	public void channelJobTask() {
		try {
			LOGGER.info("执行获取HIS渠道账单任务业务逻辑.........");
			// 获取前天数据
			List<ChannelScheduleInfo> channelsOfYesterday = channelScheduleInfoService
					.getChannelScheduleInfosOfYesterday();
			Map<String,String> map = ValueTexts.asMap(metaDataService.valueAsList());
			if (channelsOfYesterday != null && channelsOfYesterday.size() > 0) {
				for (ChannelScheduleInfo channelScheduleInfo : channelsOfYesterday) {
					HisChannelParaSendInfo hisChannelParaSendInfo = new HisChannelParaSendInfo();
					hisChannelParaSendInfo.setOrg_no(String.valueOf(channelScheduleInfo.getOrgNo()));
					hisChannelParaSendInfo.setPay_type(map.get(String.valueOf(channelScheduleInfo.getMetaPayId())));
					hisChannelParaSendInfo.setPay_date(DateUtil.getSpecifiedDayBefore(new Date()));
					hisChannelParaSendInfo.setTrade_code(EnumType.TRADE_CODE.getValue());
					String message = JsonUtil.bean2json(hisChannelParaSendInfo);
					LOGGER.info("调用银医获取账单接口入参===="+message);
					IPaymentService iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
					String result = iPaymentService.entrance(message);
					LOGGER.info("调用银医获取账单接口===="+result);
				}
			} else {
				LOGGER.info("数据库表{t_rec_channel_schedule_cfg}中没有获取到HIS渠道账单任务.........");
			}
		} catch (Exception e) {
			LOGGER.info("channelTask Run job failed!", e);
		}
	}
	
	@Override
	public void autoRecJobTask() {
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		try {
			//机构的医保配置
			if(null != hConfig){
				LOGGER.info("电子两方账单自动对账定时任务开始执行.........");
				// 判断当前对账日期是否为周一
				boolean currDateIsMonday = currDateIsMonday(new Date());
				boolean autoRecCrossDayFlag = Boolean.valueOf(propertiesConfigService.findValueByPkey(
						ProConstants.autoRecCrossDayFlag, ProConstants.DEFAULT.get(ProConstants.autoRecCrossDayFlag)));
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				// 解决周末账单不上送问题，如市二中行账单，默认关闭，需要时开启
				if(autoRecCrossDayFlag  && currDateIsMonday){
					for (int i = 3; i > 0; i--) {
						// 双方对账（非医保）
						reconciliationsService.compareBill(orgCode , DateUtil.getSpecifiedDayBeforeDay(new Date(), i));
						// 双方对账（医保）
						reconciliationsService.compareHealthBill(orgCode, DateUtil.getSpecifiedDayBeforeDay(new Date(), i));
					}
				} else {
					// 双方对账（非医保）
					reconciliationsService.compareBill(orgCode, DateUtil.getSpecifiedDayBeforeDay(new Date(), Integer.parseInt(hConfig.getCheckTime())));
					// 双方对账（医保）
					reconciliationsService.compareHealthBill(orgCode, DateUtil.getSpecifiedDayBeforeDay(new Date(), Integer.parseInt(hConfig.getCheckTime())));
				}
			}
		} catch (Exception e) {
			LOGGER.error("online-autoRecJobRunner Run job failed!", e);
		}
	}

	/**
	 * 判断是否为周一
	 * @param date
	 * @return
	 */
	private boolean currDateIsMonday(Date date){
		boolean resultBoolean = false;
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		int week=cal.get(Calendar.DAY_OF_WEEK)-1 ;
		if(week == 1){
			resultBoolean = true;
		}
		return resultBoolean;
	}
	
	@Override
	public String billParseJobTask() {
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		String orderDate = DateUtil.getSpecifiedDayBeforeDay(new Date(),Integer.parseInt(hConfig.getCheckTime()));
		// 是否单独设置拉取账单的周期，将拉取账单的周期和对账周期分开
		String billParseTime = propertiesConfigService.findValueByPkey(ProConstants.billParseCycle);
		if(!StringUtil.isEmpty(billParseTime)){
			orderDate = DateUtil.getSpecifiedDayBeforeDay(new Date(),Integer.parseInt(billParseTime));
		}
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		// 清除渠道所有账单数据
		try {
		    boolean billDataParseDeleteFlag = Boolean.valueOf(propertiesConfigService.findValueByPkey(ProConstants.billDataParseDeleteFlag, 
		    		ProConstants.DEFAULT.get(ProConstants.billDataParseDeleteFlag)));
			if(billDataParseDeleteFlag ) {
		        thirdBillService.delete(orderDate+" 00:00:00", orderDate+" 23:59:59", orgCode);
		    }
		} catch (Exception e) {
		    LOGGER.error("删除数据发生异常：", e);
		}
		// 解析账单
		try {
		    billParseService.parse(orgCode, orderDate);
        } catch (BillParseException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	/**
     * 调用银医接口插入数据
     * @param orgCode
     * @param payDate
     */
    private void insertBillByYinYi(String orgCode, String payDate) {
        HisChannelParaSendInfo hcpsi = new HisChannelParaSendInfo();
        hcpsi.setOrg_no(orgCode);
        hcpsi.setPay_type("");
        hcpsi.setTrade_code(EnumType.TRADE_CODE.getValue());
        hcpsi.setPay_date(payDate);
        String message = JsonUtil.bean2json(hcpsi);
        IPaymentService iPaymentService = null;
        try {
            iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
        } catch (ServiceException e) {
            LOGGER.error("定时任务调用银医接口发生异常：", e);
        }
        String result = null;
        try {
            result = iPaymentService.entrance(message);
        } catch (RemoteException e) {
            LOGGER.error("调用银医接口发生异常：", e);
        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        LOGGER.info("定时任务调用银医获取账单接口reloadData"+jsonObject.toString());
    }

    @Override
    public void getHisBillJobTask() {
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String orderDate = DateUtil.getSpecifiedDayBeforeDay(new Date(),Integer.parseInt(hConfig.getCheckTime()));
        // 通过银行获取his账单数据，暂时处理方案，后续推荐在账单解析逻辑实现
        try {
        	String yinyiBillFlag = propertiesConfigService.findValueByPkey(ProConstants.donetBillUrl);
            // 如果银医接口地址为空则不通过银医接口拉取数据
            if(StringUtils.isNotBlank(yinyiBillFlag)) {
                String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				insertBillByYinYi(orgCode, orderDate);
            }
        } catch (Exception e) {
            LOGGER.error("调用银医获取his账单数据接口发生异常：", e);
        }
    }

    @Override
    public void getHisSettlementBillJobTask() {
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        String orderDate = DateUtil.getSpecifiedDayBeforeDay(new Date(), 
                Integer.parseInt(hConfig.getCheckTime()));
        try {
			recHisSettlementService.getAndSaveHisOrders(orderDate);
		} catch (Exception e) {
			LOGGER.error("调用银医获取his账单结算数据接口发生异常：", e);
		}
    }
    
    
    /**
     * 循环拉取email邮箱账单到本地
     */
    @Override
	public void emailBillLoopDownload() {
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		String orderDate = DateUtil.getSpecifiedDayBeforeDay(new Date(), Integer.parseInt(hConfig.getCheckTime()));
		try {
			emailBillDownloadService.download(orderDate);
		} catch (BillParseException e) {
			LOGGER.error("循环拉取email邮箱账单到本地 任务异常，" + e);
		}
	}
}
