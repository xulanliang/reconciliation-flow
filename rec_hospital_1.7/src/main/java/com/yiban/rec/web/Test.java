package com.yiban.rec.web;




import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.BillParseService;
import com.yiban.rec.domain.basicInfo.HisChannelParaSendInfo;
import com.yiban.rec.emailbill.service.ThirdBillService;
import com.yiban.rec.reconciliations.ReconciliationsService;
import com.yiban.rec.service.RecLogDetailsService;
import com.yiban.rec.service.settlement.RecHisSettlementService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.JsonUtil;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("/implement/bill")
public class Test  extends FrameworkController {
	@Autowired
	private RecHisSettlementService recHisSettlementService;
	
	@Autowired
	private ReconciliationsService reconciliationsService;
	
	@Autowired
	private RecLogDetailsService recLogDetailsService;
	
	@Autowired
    private BillParseService billParseService;
	
	@Autowired
	private ThirdBillService thirdBillService;
	
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	
	@RequestMapping(value = "sum", method = RequestMethod.GET)
	public ResponseResult ttt(String sd,String ed) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			for(int i=0;i<=DateUtil.daysBetween2Date(sdf.parse(sd), sdf.parse(ed));i++) {
				String time = DateUtil.getSpecifiedDayAfter(sd,i);
				try {
					recHisSettlementService.getAndSaveHisOrders(time);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(time+"汇总失败,原因:==="+e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseResult.success("操作成功");
	}
	@RequestMapping(value = "reconciliation", method = RequestMethod.GET)
	public ResponseResult ttt2(String orgCode,String sd,String ed) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			for(int i=0;i<=DateUtil.daysBetween2Date(sdf.parse(sd), sdf.parse(ed));i++) {
				String time = DateUtil.getSpecifiedDayAfter(sd,i);
				try {
					// 清空历史日志
					recLogDetailsService.deleteByOrderDateAndOrgCode(time, orgCode);
					
					// 清除渠道所有账单数据
					try {
						boolean billDataParseDeleteFlag = Boolean.valueOf(propertiesConfigService.findValueByPkey(ProConstants.billDataParseDeleteFlag,
								ProConstants.DEFAULT.get(ProConstants.billDataParseDeleteFlag)));
						if(billDataParseDeleteFlag ) {
							logger.info("拉取账单先删除:当前所有数据");
							thirdBillService.delete(time+" 00:00:00", time+" 23:59:59", orgCode);
							logger.info("拉取账单先删除:当前所有数据成功");
						}
					} catch (Exception e) {
						return ResponseResult.failure(time+":拉取账单删除数据异常：" + e.getMessage());
					}
					boolean tangduHisbillFirst = Boolean
							.valueOf(propertiesConfigService.findValueByPkey(ProConstants.tangduHisbillFirst,
									ProConstants.DEFAULT.get(ProConstants.tangduHisbillFirst)));
					if (tangduHisbillFirst) {
						getHisBill(orgCode, time);
					}
					
					// 拉取账单
					try {
					    billParseService.parse(orgCode, time);
		            } catch (BillParseException e) {
		                logger.error("拉取账单异常：", e);
		                return ResponseResult.failure("拉取账单异常：" + e.getMessage());
		            }

					if (!tangduHisbillFirst) {
						getHisBill(orgCode, time);
					}
					//对账
					reconciliationsService.compareBill(orgCode,time);
					reconciliationsService.compareHealthBill(orgCode, time);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(time+"对账失败,原因:==="+e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseResult.success("操作成功");
	}
	
	private void getHisBill(String orgCode, String orderDate) {
		/**
		 * 通过银行获取his账单数据，暂时处理方案，后续推荐在账单解析逻辑实现 
		 * 在汇总后面执行，因为异步逻辑，汇总是上次的获取数据
		 */
		try {
			CharSequence yinyiBillFlag = propertiesConfigService.findValueByPkey(ProConstants.donetBillUrl);
			// 如果银医接口地址为空则不通过银医接口拉取数据
			if (StringUtils.isNotBlank(yinyiBillFlag)) {
				insertBillByYinYi(orgCode, orderDate);
			}
		} catch (Exception e) {
			logger.error("调用银医获取his账单数据接口发生异常：", e);
		}
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
        logger.info("调用银医接口插入HIS数据, 请求参数：" + message);
        IPaymentService iPaymentService = null;
        try {
            iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
        } catch (ServiceException e) {
            logger.error("调用银医接口发生异常：", e);
        }
        String result = null;
        try {
            result = iPaymentService.entrance(message);
        } catch (RemoteException e) {
            logger.error("调用银医接口发生异常：", e);
        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        logger.info("调用银医获取账单接口reloadData"+jsonObject.toString());
    }
}
