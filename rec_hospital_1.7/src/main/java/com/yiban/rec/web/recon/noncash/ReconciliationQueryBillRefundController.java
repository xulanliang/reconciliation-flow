package com.yiban.rec.web.recon.noncash;



import java.util.HashMap;
import java.util.Map;

import com.yiban.rec.dao.PayOrderUploadDao;
import com.yiban.rec.domain.PayorderUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.yiban.framework.core.service.SessionUserService;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.QueryBillRefundVo;
import com.yiban.rec.domain.vo.RefundVo;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.RefundService;
import com.yiban.rec.util.JsonChangeVo;
import com.yiban.rec.util.PayWayEnum;
import com.yiban.rec.util.RefundStateEnum;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.annotation.Resource;

@Controller
@RequestMapping("/admin/queryBillRefund")
public class ReconciliationQueryBillRefundController extends CurrentUserContoller {

	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private RefundService refundService;

	@Autowired
	private SessionUserService sessionService;

	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@Autowired
	private HospitalConfigService hospitalConfigService;

	@Resource
	private PayOrderUploadDao payOrderUploadDao;

	/**
	 * 进入页面
	 * @param model
	 * @return
	 */
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		return autoView("reconciliation/queryBillRefund");
	}

	@RestController
	@RequestMapping({"/admin/queryBillRefund/data"})
	class RefundDataController extends BaseController {
		/**
		 * 联系线上查询订单信息
		 */
		@PostMapping("/getDate")
		public ResponseResult getDate(QueryBillRefundVo qVo) {
			ResponseResult rs=ResponseResult.success();
			Map<String, Object> map=new HashMap<>(10);
			String payUrl = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl, ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
			String url=payUrl +"/pay/billLog/query/list";
			String onlineQueryUrl = payUrl +"/order/query";
			String requestUrl = null;
			Boolean isflag = false;
			try {
				String inputNo = qVo.getPayNo();
				//以411开头的订单使用综合支付查询api查询，其他的通过原来的接口查询
				if(inputNo.startsWith("411")){
					map.put("orderNo", inputNo);
					requestUrl = onlineQueryUrl;
					isflag = true;
				}else{
					String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
					map.put("tsn",	qVo.getPayNo());
					map.put("outTradeNo",	qVo.getPayNo());
					map.put("orgCode",	orgCode);
					requestUrl = url;
					isflag = false;
				}
				JSONObject json = JSONObject.fromObject(map);
				String rest = HttpClientUtil.doPostJson(requestUrl, json.toString());
				logger.info("-退款查询结果：--->>>{}",rest);
				if(StringUtils.isNotBlank(rest)&&!rest.equals("[]")) {
					if(isflag){
						JSONObject jsStr = JSONObject.fromObject(rest);
						Map<String,Object> responseMap = new HashMap<>();
						responseMap.put("out_trade_no", jsStr.getString("outTradeNo"));
						responseMap.put("tsn", jsStr.getString("tsn_order_no"));
						responseMap.put("pay_name", PayWayEnum.getPayWayTypeForCode(jsStr.getString("payCode")));
						responseMap.put("order_amount", jsStr.getString("orderAmount"));
						rs.data(JSONObject.fromObject(responseMap).toString());
						return rs;
					}
					JSONArray list=JSONArray.fromObject(rest);
					if(list.size()>1) {
						rs=ResponseResult.failure("该流水号存在多条订单,请提供其他单号（微信订单号/业务订单号）！");
					}else {
						rs.data(list.get(0).toString());
					}
				}else {
					//老版的查不到就去查下新版的 ，新版综合支付订单查询
					logger.info("新版订单查询t_payorder_upload表！");
					PayorderUpload payorderUpload = payOrderUploadDao.findByOutTradeNo(qVo.getPayNo());
					if(payorderUpload == null){
						rs=ResponseResult.failure("该流水号不存在,请确认！");
						return rs;
					}
					Map<String,Object> resultMap = new HashMap<>();
					resultMap.put("out_trade_no",payorderUpload.getMchOrderId());
					resultMap.put("order_amount",payorderUpload.getOrderAmt());
					resultMap.put("pay_name",payorderUpload.getPayType());
					resultMap.put("tsn",payorderUpload.getOutTradeNo());
                    rs.data(JSONObject.fromObject(resultMap).toString());
                    return rs;
				}
			} catch (Exception e) {
				rs=ResponseResult.failure(e.getMessage());
			}
			return rs;
		}

		/**
		 * 退费
		 */
		@PostMapping
		public ResponseResult rejectOrExamine(QueryBillRefundVo qVo) {
			User user = currentUser();
			AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
			try {
				//验证登录人操作密码
				String passWord=qVo.getPassWord();
				if (StringUtils.isBlank(passWord)||!sessionService.isCurrentUserPassword(passWord)) {
					return ResponseResult.failure().message("密码输入有误,请重新输入！");
				}
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				//退费
				RefundVo vo = new RefundVo();
				// 需要退款的订单号：tsn
				vo.setTsn(qVo.getPayNo());
				// 支付渠道：payCode
				vo.setPayCode(qVo.getPayCode());
				vo.setPayType(qVo.getPayCode());
				vo.setOrgCode(orgCode);
				//部分退款生成批次号
				if(qVo.getPayAmount().compareTo(qVo.getTradeAmount())>0) {
					vo.setBatchRefundNoNew(String.valueOf((int)(Math.random()*100)));
					vo.setBatchRefundNo(String.valueOf((int)(Math.random()*100)));
				}
				// 退款原因：reason
				vo.setReason("查账退款");
				// 订单来源：billSource（"self_jd","巨鼎" "self","银医" "third","第三方"）
				vo.setBillSource("self_jd");
				vo.setTradeAmount(qVo.getTradeAmount().toString());
				vo.setUser(user);
				vo.setExtendArea(JsonChangeVo.getJson(vo));
				//退款流程(医院属性配置中配置)
				if(hConfig.getIsRefundExamine().equals(RefundStateEnum.unExamine.getValue())){//需要审核
					vo.setBatchRefundNo("");
				}
				ResponseResult result = refundService.refundAll(vo);
				logger.error("退款结果：{}", result.getMessage());
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseResult.failure().message("退费异常");
			}
		}
	}
}
