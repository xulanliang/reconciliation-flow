package com.yiban.rec.web.admin.order;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.xml.rpc.ServiceException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.google.gson.Gson;
import com.ibm.icu.math.BigDecimal;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.rec.dao.PayTypeDao;
import com.yiban.rec.domain.PayType;
import com.yiban.rec.domain.basicInfo.OrderQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.JsonUtil;
import com.yiban.rec.util.RestUtil;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
*<p>文件名称:WeichatAccountController.java
*<p>
*<p>文件描述:本类描述
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:支付宝宝管理
</p>
*<p>其他说明:其它内容的说明
</p>
*@author fangzuxing
 */
@Controller
@RequestMapping("/admin/order")
public class OrderController extends CurrentUserContoller{

	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	private  String PAY_LIST_URL=null;
	private  String ORDER_LOG_URL=null;
	private  String PAY_DETAIL_URL=null;
	private  String PAY_REFUND_URL=null;
	private  String[] PAY_ENUM_URL=null;
	
//	@PostConstruct
	@ModelAttribute
	public void init(){
		String payCenterUrl = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl, ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		PAY_ENUM_URL=new String[]{	
				payCenterUrl +"/pay/enum/platform", //平台
                payCenterUrl+"/pay/enum/paycode",  //支付类型
                payCenterUrl+"/pay/enum/paystate",  //支付状态
                payCenterUrl+"/pay/enum/orderstate", //订单状态
                payCenterUrl+"/pay/enum/tsnstate",
                payCenterUrl+"/pay/enum/refundstate",};
		PAY_LIST_URL=payCenterUrl+"/pay/order/pageList";
		ORDER_LOG_URL=payCenterUrl+"/pay/order/log/pageList";
		PAY_DETAIL_URL=payCenterUrl+"/pay/order/detail";
		PAY_REFUND_URL=payCenterUrl+"/pay/order/refundDetail";
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(ModelMap model) {
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMapFromCode()));
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(userOrganizationPerService.asValueToName())));
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		return autoView("order/order");
	}
    
	/**
	 * 获取支付平台请求API的日志
	 * @param model
	 * @return
	 * String
	 */
	@RequestMapping(value = "log", method = RequestMethod.GET)
	public String orderLog(ModelMap model) {
	    return autoView("order/orderLog");
	}
	
	@RestController
	@RequestMapping("/admin/order/data")
	class DataController extends FrameworkController{
		
		@Autowired
		private PayTypeDao payTypeDao;
		
		@InitBinder
	    public void intDate(WebDataBinder dataBinder){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    }
		
		@GetMapping
		public String pageList(@Valid OrderQueryVo ovo) throws BusinessException {
			PageRequest pageRequest = getRequestPageable();
			String retStr = "";
			if(ovo.getOrderStartTime() == null){
				return retStr;
			}
			Gson gson = new Gson();
			try {
				if(null == ovo.getPlatFormType() || StringUtil.equals("全部", ovo.getPlatFormType())) {
					ovo.setPlatFormType("");
				}
				if(null == ovo.getOrderState() || StringUtil.equals("全部", ovo.getOrderState())) {
					ovo.setOrderState("");
					
				}
				if(null == ovo.getPayCode() || StringUtil.equals("全部", ovo.getPayCode())) {
					ovo.setPayCode("");
				}
				if(null == ovo.getPayState() || StringUtil.equals("全部", ovo.getPayState())) {
					ovo.setPayState("");
				}
				if(null == ovo.getRefundState() || StringUtil.equals("全部", ovo.getRefundState())) {
					ovo.setRefundState("");
				}
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("params", gson.toJson(ovo).toString());
				map.put("page",pageRequest.getPageNumber());
				map.put("rows", pageRequest.getPageSize());
				retStr = new RestUtil().doPost(PAY_LIST_URL, map, CommonConstant.CODING_FORMAT);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return retStr;
		}
		
		public String getTypeCodeMap(String value) {
			List<PayType> orgs = payTypeDao.findAll();
			for (PayType data : orgs) {
				if (StringUtil.equals(data.getCode(), value)) {
					return data.getOrderType();
				}
			}
			return null;
		}
		
		@GetMapping("/detail")
		public String orderDetail(@RequestParam(value = "orderId") Long orderId){
			String retStr = "";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("orderId", orderId);
			try {
				retStr = new RestUtil().doPost(PAY_DETAIL_URL, map, CommonConstant.CODING_FORMAT);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return retStr;
		}
		
		//重写下拉数据请求逻辑，将多个相同的请求合并一个，根据参数返回不同数据
		@GetMapping(value = "/combox")
		public String removeCoboxType(@RequestParam(value = "isIncludeAll") boolean isIncludeAll,int type){
			String url=PAY_ENUM_URL[type];
			String retStr = "";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("isIncludeAll", isIncludeAll);
			try {
				retStr = new RestUtil().doPost(url, map, CommonConstant.CODING_FORMAT);
				//将key转换成select2控件支持的id
				retStr=retStr.replaceAll("key", "id");
				retStr=retStr.replaceAll("9999", "");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return retStr;
		}
		
		@GetMapping(value = "/refundstate")
		public String refundStateToMap(@RequestParam(value = "isIncludeAll") boolean isIncludeAll){
			String payCenterUrl = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl, ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
			String url = payCenterUrl+"/pay/enum/refundstate";
			String retStr = "";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("isIncludeAll", isIncludeAll);
			try {
				retStr = new RestUtil().doPost(url, map, CommonConstant.CODING_FORMAT);
				//将key转换成select2控件支持的id
				retStr=retStr.replaceAll("key", "id");
				retStr=retStr.replaceAll("9999", "");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return retStr;
		}
		
		/**
		 * 请求支付平台日志分页数据
		 * @param beginDateTime 请求时间-查询开始时间
		 * @param endDateTime 请求时间-查询结束时间
		 * @param outTradeNo 业务订单号
		 * @param orderNo 平台订单号
		 * @param tsn 微信支付宝订单号
		 * @return String
		 */
        @GetMapping("log")
        public String logPagList(
                @RequestParam(name="requestDateTime", required=false) String requestDateTime, 
                @RequestParam(name="outTradeNo", required=false) String outTradeNo,
                @RequestParam(name="orderNo", required=false) String orderNo,
                @RequestParam(name="tsn", required=false)  String tsn
                ) {
            PageRequest pageRequest = getRequestPageable();
            String retStr = "";
            try {
                Map<String, Object> map = new HashMap<>(8);
                if(StringUtils.isNotBlank(outTradeNo)) {
                    map.put("outTradeNo",outTradeNo);
                }
                if(StringUtils.isNotBlank(orderNo)) {
                    map.put("orderNo",orderNo);
                }
                if(StringUtils.isNotBlank(tsn)) {
                    map.put("tsn",tsn);
                }
                if(StringUtils.isNotBlank(requestDateTime)) {
                    String[] requestDateTimes = requestDateTime.split("~");
                    map.put("beginDateTime",requestDateTimes[0].trim());
                    map.put("endDateTime",requestDateTimes[1].trim());
                }
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);

                map.put("pageNumber",pageRequest.getPageNumber());
                map.put("pageSize", pageRequest.getPageSize());
                map.put("orgCode", orgCode);
                
                retStr = new RestUtil().doPost(ORDER_LOG_URL, map, 
                        CommonConstant.CODING_FORMAT);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return retStr;
        }
        
        //获取订单在his的状态
        @GetMapping(value = "/getHisFollowStateByFollow")
  		public String getHisFollowStateByFollow(@RequestParam(name="outTradeNo", required=false)String outTradeNo){
  			String result = "";
  			String message = JsonUtil.bean2json("outTradeNo:"+outTradeNo);
			IPaymentService iPaymentService;
			try {
				iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_HISFollowService();
					result = iPaymentService.entrance(message);
			} catch (ServiceException e) {
				e.printStackTrace();
			}catch (RemoteException e) {
				e.printStackTrace();
			}
//			JSONObject jsonObject = JSONObject.fromObject(result);
  			return result;
  		}
        /**
         * 查询单条订单退费情况
         */
        @GetMapping(value = "/refundDetail")
  		public String refundDetail(@RequestParam(name="orderNo", required=false)String orderNo){
			String retStr = "";
			orderNo="41190227286904";
			BigDecimal amount=new BigDecimal(0);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("orderNo", orderNo);
			try {
				retStr = new RestUtil().doPost(PAY_REFUND_URL, map, CommonConstant.CODING_FORMAT);
				JSONArray list=JSONArray.fromObject(retStr);
				//计算金额
				for(int i=0;i<list.size();i++) {
					JSONObject vo=(JSONObject) list.get(i);
					amount.add(new BigDecimal(vo.getDouble("refundAmount")));
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			return amount.toString();
  		}
        
	}
}
