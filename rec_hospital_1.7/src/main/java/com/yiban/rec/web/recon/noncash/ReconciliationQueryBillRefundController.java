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
	 * ????????????
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
		 * ??????????????????????????????
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
				//???411???????????????????????????????????????api?????????????????????????????????????????????
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
				logger.info("-?????????????????????--->>>{}",rest);
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
						rs=ResponseResult.failure("??????????????????????????????,???????????????????????????????????????/?????????????????????");
					}else {
						rs.data(list.get(0).toString());
					}
				}else {
					//??????????????????????????????????????? ?????????????????????????????????
					logger.info("??????????????????t_payorder_upload??????");
					PayorderUpload payorderUpload = payOrderUploadDao.findByOutTradeNo(qVo.getPayNo());
					if(payorderUpload == null){
						rs=ResponseResult.failure("?????????????????????,????????????");
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
		 * ??????
		 */
		@PostMapping
		public ResponseResult rejectOrExamine(QueryBillRefundVo qVo) {
			User user = currentUser();
			AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
			try {
				//???????????????????????????
				String passWord=qVo.getPassWord();
				if (StringUtils.isBlank(passWord)||!sessionService.isCurrentUserPassword(passWord)) {
					return ResponseResult.failure().message("??????????????????,??????????????????");
				}
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				//??????
				RefundVo vo = new RefundVo();
				// ???????????????????????????tsn
				vo.setTsn(qVo.getPayNo());
				// ???????????????payCode
				vo.setPayCode(qVo.getPayCode());
				vo.setPayType(qVo.getPayCode());
				vo.setOrgCode(orgCode);
				//???????????????????????????
				if(qVo.getPayAmount().compareTo(qVo.getTradeAmount())>0) {
					vo.setBatchRefundNoNew(String.valueOf((int)(Math.random()*100)));
					vo.setBatchRefundNo(String.valueOf((int)(Math.random()*100)));
				}
				// ???????????????reason
				vo.setReason("????????????");
				// ???????????????billSource???"self_jd","??????" "self","??????" "third","?????????"???
				vo.setBillSource("self_jd");
				vo.setTradeAmount(qVo.getTradeAmount().toString());
				vo.setUser(user);
				vo.setExtendArea(JsonChangeVo.getJson(vo));
				//????????????(???????????????????????????)
				if(hConfig.getIsRefundExamine().equals(RefundStateEnum.unExamine.getValue())){//????????????
					vo.setBatchRefundNo("");
				}
				ResponseResult result = refundService.refundAll(vo);
				logger.error("???????????????{}", result.getMessage());
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseResult.failure().message("????????????");
			}
		}
	}
}
