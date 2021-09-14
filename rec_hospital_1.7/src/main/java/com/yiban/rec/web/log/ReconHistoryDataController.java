package com.yiban.rec.web.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.BillParseService;
import com.yiban.rec.domain.basicInfo.HisChannelParaSendInfo;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.JsonUtil;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/admin/historydata")
public class ReconHistoryDataController extends CurrentUserContoller {
	
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	
	@Autowired
	private BillParseService billParseService;
	

	@RequestMapping("")
	public String index(ModelMap model) {
		 model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		 model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		return autoView("reconciliation/historydata");
	}
	
	@RestController
	@RequestMapping("/admin/historydata/data")
	class DataController extends FrameworkController{
		@InitBinder
	    public void intDate(WebDataBinder dataBinder){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    }
		
		
		@GetMapping
		public WebUiPage<RecLog> recHistoryQuery(String orgNo, String orderDate){
			Sort sort = new Sort(Direction.DESC, "orderDate");
			User user = currentUser();
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
//			Page<HistoryState> platPage = historyDataService.getHistoryData(orgNo,startTime, endTime,orgListTemp,
//					this.getRequestPageabledWithInitSort(sort));
//			return toWebUIPage(platPage);
			return null;
			
		}
		
		/**
		 * @date：2017年10月31日 
		 * @Description：获取隔日账单接口
		 * @param orgNo
		 * @param orderDate
		 * @return: 返回结果描述
		 * @return String: 返回值类型
		 * @throws
		 */
		@PostMapping
		public synchronized ResponseResult reloadData(@Valid RecLog recLog){
			HisChannelParaSendInfo hisChannelParaSendInfo = new HisChannelParaSendInfo();
			hisChannelParaSendInfo.setOrg_no(String.valueOf(recLog.getOrgCode()));
			hisChannelParaSendInfo.setPay_type("");
			hisChannelParaSendInfo.setTrade_code(EnumType.TRADE_CODE.getValue());
			
			HisChannelParaSendInfo hisChannelParaSendInfo2 = new HisChannelParaSendInfo();
	        hisChannelParaSendInfo2.setOrg_no(String.valueOf(recLog.getOrgCode()));
	        hisChannelParaSendInfo2.setPay_type("");
	        hisChannelParaSendInfo2.setTrade_code("00006");
			
			//按天获取账单
//			try {
//				Calendar c = Calendar.getInstance();
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				Date startDate = sdf.parse(recLog.getStartTime());
//				Date endDate = sdf.parse(recLog.getEndTime());
//				while(startDate.getTime() <= endDate.getTime()) {
//					//插入账单
//					hisChannelParaSendInfo.setPay_date(sdf.format(startDate));
//					hisChannelParaSendInfo2.setPay_date(sdf.format(startDate));
//					recLog.setOrderDate(startDate);
//					//调用账单解析服务获取所有账单
//					getBillParse(recLog,sdf.format(startDate),recLog.getOrgNo());
//					if(StringUtils.isNotBlank(getBillRefundUrl)) {
//						insertBillByYinYi(recLog,hisChannelParaSendInfo);
//						insertBillByYinYi(recLog, hisChannelParaSendInfo2);
//					}
//					//时间增加
//					c.setTime(startDate);
//					int day = c.get(Calendar.DATE);
//					c.set(Calendar.DATE, day + 1);
//					startDate = c.getTime();
//				}
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			return ResponseResult.success("账单获取完毕");
		}

		//调用银医接口插入数据
		private int insertBillByYinYi(RecLog hs, HisChannelParaSendInfo hisChannelParaSendInfo) {
			try {
				
				String message = JsonUtil.bean2json(hisChannelParaSendInfo);
				IPaymentService iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
				String result = iPaymentService.entrance(message);
				JSONObject jsonObject = JSONObject.fromObject(result);
				logger.info("调用银医获取账单接口reloadData"+jsonObject.toString());
				if(CommonConstant.TRADE_CODE_SUCCESS.equals(jsonObject.getString("Response_Code"))){
					
//					hs.setHistoryState(EnumType.HANDLE_SUCCESS.getValue());
//					historyDataService.saveHistoryDataState(hs);
					return 1;
				}else{
//					hs.setExceptionRemark(jsonObject.getString("Response_Msg"));
//					hs.setHistoryState(EnumType.HANDLE_FAIL.getValue());
//					historyDataService.saveHistoryDataState(hs); 
					return 0;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				return 0;
			}
			
		}
		
		private void getBillParse(RecLog hs,String Date, String orgCode){
			ResponseResult rs = null;
            try {
                rs = billParseService.startParse(orgCode, Date ,Date);
            } catch (BillParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			if(rs.isSuccess()){
//				hs.setExceptionRemark("拉取成功");
//				hs.setHistoryState(EnumType.HANDLE_SUCCESS.getValue());
			}else{
//				hs.setExceptionRemark("拉取失败");
//				hs.setHistoryState(EnumType.HANDLE_FAIL.getValue());
			}
//			historyDataService.saveHistoryDataState(hs); 
		}
		
	}
}
