package com.yiban.rec.service.customized.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.rec.bill.parse.util.RefundEnumType;
import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.service.customized.AbstractRefundClass;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.WebServiceClientUtil;

import net.sf.json.JSONObject;


public class JindieRefundClass extends AbstractRefundClass {

	public JindieRefundClass(MixRefundDetails vo, MixRefund mixRefund,BigDecimal sourceAmount) throws Exception {
		super(vo,sourceAmount);
		refund(vo,mixRefund,sourceAmount);
	}
	
	
	protected boolean isAllAmount() {
		return super.isAllAmount();
	}
	
	private void refund(MixRefundDetails vo,MixRefund mixRefund,BigDecimal sourceAmount) throws Exception {
		// 调金蝶退费接口用
		String endpoint = PropertyUtil.getProperty("tangdu.endpoint", "");
		String username = PropertyUtil.getProperty("tangdu.username", "");
		String password = PropertyUtil.getProperty("tangdu.password", "");
		String method = PropertyUtil.getProperty("tangdu.refund.method", "");
		Map<String, String> data = new HashMap<>();
		data.put("orderId", vo.getTsnOrderNo());
		data.put("refundTime", DateUtil.getCurrentTimeString());
		if(!isAllAmount()) {//部分退款
			vo.setRefundAmount(sourceAmount);
			data.put("refundFee", String.valueOf(sourceAmount.multiply(new BigDecimal(100)).intValue()));
		}else {
			vo.setRefundAmount(vo.getPayAmount());
			data.put("refundFee", String.valueOf(vo.getPayAmount().multiply(new BigDecimal(100)).intValue()));
		}
//		data.put("refundNo", vo.getTsnOrderNo());
		// 保持唯一性
		data.put("refundNo", mixRefund.getRefundOrderNo());
		data.put("reason", mixRefund.getRefundReason());
		String reqXml = null;
		try {
			reqXml = WebServiceClientUtil.mapToXml(data);
		} catch (Exception e) {//异常退费失败
			Exception exception=new Exception("mapToXml error: "+e);
			throw exception;
		}
		reqXml = reqXml.substring(reqXml.indexOf(">") + 1, reqXml.length());
		try {
			// String endpoint = "http://192.168.27.167:8080/himapws/CallServicePort?wsdl";
			// 直接引用远程的wsdl文件
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(endpoint);
			call.setOperationName(new QName("http://server.soap.himap.business.com/", "callService"));// WSDL里面描述的接口名称
			call.addParameter("arg0", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.addParameter("arg1", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.addParameter("arg2", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.addParameter("arg3", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.addParameter("arg4", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.setReturnType(XMLType.XSD_STRING);// 设置返回类型
			// String result = (String) call.invoke(new Object[] { orgCode,
			// "PROC_GETCURRENTDATE", "<request></request>", "admin",
			// "fr4t3t1y6s6s1e0502c4d5" });
			// 给方法传递参数，并且调用方法
			String result = (String) call.invoke(new Object[] { mixRefund.getOrgCode(), method, reqXml, username, password });
			JSONObject json =JSONObject.fromObject(result);
			if(!json.getBoolean("success")) {//正常退费失败
				Exception exception=new Exception(json.getString("message"));
				vo.setRefundState(RefundEnumType.REFUND_FAILURE.getId());
				throw exception;
			}else {
				vo.setRefundState(RefundEnumType.REFUND_SUCCESS.getId());
			}
		} catch (Exception e) {//程序异常也属于正常退费失败
			if(vo.getRefundState()!=RefundEnumType.REFUND_NO_EXCEPTION.getId()) {
				vo.setRefundState(RefundEnumType.REFUND_FAILURE.getId());
			}
			throw e;
		}
		
	}
	
}
