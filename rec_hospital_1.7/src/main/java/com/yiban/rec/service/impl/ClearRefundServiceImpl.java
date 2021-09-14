package com.yiban.rec.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.rec.domain.vo.RefundVo;
import com.yiban.rec.service.ClearRefundService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.RestUtil;

import net.sf.json.JSONObject;

@Service
public class ClearRefundServiceImpl implements ClearRefundService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PropertiesConfigService propertiesConfigService;

	public void ClearRefund(RefundVo vo) throws Exception {
		if(vo.getOrgCode().equals("11942")) {
			//仙桃市第一人民医院(退费)
			refundThird(vo);
		}else {
			refund(vo);
		}
	}

	private void refund(RefundVo vo) throws Exception {
		String payCenterUrl = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		// url="http://192.168.19.207:8068/order/refund";
		String url = payCenterUrl + "/order/refund";
		logger.info("地址为：" + url);
		Map<String, Object> map = new HashMap<String, Object>(10);
		map.put("reason", vo.getReason());
		// if(StringUtils.isNotBlank(vo.getOrderNo()))map.put("orderNo",
		// vo.getOrderNo());
		if (StringUtils.isNotBlank(vo.getTsn()))
			map.put("tsn", vo.getTsn());
		map.put("orgCode", vo.getOrgCode());
		if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getPayCode())) {
			map.put("payCode", vo.getPayCode());
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getBatchRefundNo())) {
			map.put("batchRefundNo", vo.getBatchRefundNo());
		} else {
			// 如果退款批次号为空那么生成一个
			String batchRefundNo = RandomStringUtils.randomNumeric(6);
			map.put("batchRefundNo", batchRefundNo);
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getTradeAmount())) {
			map.put("refundAmount", vo.getTradeAmount());
		}
		JSONObject jsonObject = JSONObject.fromObject(map);
		String retStr = null;
		retStr = new RestUtil().doPostJson(url, jsonObject.toString(), CommonConstant.CODING_FORMAT);
		logger.info("退款返回结果：{}", retStr);
		JSONObject json = JSONObject.fromObject(retStr);
		if (!json.getBoolean("success")) {
			Exception exception = new Exception(json.getString("message"));
			throw exception;
		} else {
			vo.setPaymentFlow(json.getString("refundOrderNo"));
		}
	}

	/**
	 * 不走综合支付平台的第三方退费
	 * @param vo
	 * @throws Exception
	 */
	private void refundThird(RefundVo vo) throws Exception {
		String payCenterUrl = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		String url=payCenterUrl+"/order/hospitalRefund";
		Map<String,Object> map = new HashMap<String,Object>(10);
		map.put("reason", vo.getReason());
		//if(StringUtils.isNotBlank(vo.getOrderNo()))map.put("orderNo", vo.getOrderNo());
		if(StringUtils.isNotBlank(vo.getTsn())) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			String payNo=vo.getTsn();
			if (vo.getTsn().length() == 28
					&& vo.getTsn().substring(0, 2).equals("42")) {// 微信
				map.put("tsn", vo.getTsn());
			}else if (payNo.length() == 28
					&& payNo.substring(0, 4).equals(sdf.format(new Date()))) {// 支付宝
				map.put("tsn", vo.getTsn());
			}else {
				map.put("orderNo", vo.getTsn());
			}
		}
		map.put("orgCode", vo.getOrgCode());
		if(org.apache.commons.lang.StringUtils.isNotBlank(vo.getPayCode())) {
			map.put("payCode", vo.getPayCode());
		}
		// 退费金额
		map.put("refundAmount", vo.getTradeAmount());
		// 订单金额
		map.put("payAmount", vo.getPayAmount());
		JSONObject jsonObject = JSONObject.fromObject(map);
		logger.info("##### 退款url:{} 入参为：{}", url, jsonObject.toString());
		String retStr=null;
		retStr = new RestUtil().doPostJson(url, jsonObject.toString(), CommonConstant.CODING_FORMAT);
        logger.info("##### 退款结果为：{}", retStr);
		JSONObject json =JSONObject.fromObject(retStr);
		if(!json.getBoolean("success")) {
			Exception exception=new Exception(json.getString("message"));
			throw exception;
		}else {
			vo.setPaymentFlow(json.getString("refundOrderNo"));
		}
	}
}
