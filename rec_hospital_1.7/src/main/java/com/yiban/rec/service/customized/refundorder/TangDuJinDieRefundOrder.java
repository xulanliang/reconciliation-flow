package com.yiban.rec.service.customized.refundorder;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.RefundEnumType;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.util.JDomXmlParseUtil;

/**
 * 唐都金蝶退款账单查询器
 * 
 * @author clearofchina
 *
 */
public class TangDuJinDieRefundOrder {
	
	private static Logger logger = LoggerFactory.getLogger(TangDuJinDieRefundOrder.class);

//	private static final String refundQueryUrl = "http://14.14.14.2:6888/interface-service-v3/mobileApi?service=order.refundQuery";

	public TangDuJinDieRefundOrder(MixRefundDetails vo) throws Exception {
		query(vo);
	}

	private void query(MixRefundDetails vo) throws Exception {
		String requestXml = formatRequestXml(vo.getTsnOrderNo(), vo.getRefundOrderNo());
		String refundQueryUrl = PropertyUtil.getProperty("tangdu.refund.query", "http://14.14.14.2:6888/interface-service-v3/mobileApi?service=order.refundQuery");
		
		logger.info("唐都金蝶退费查询请求参数：requestXml = " + requestXml + ", refundQueryUrl = " + refundQueryUrl);
		String result = HttpClientUtil.doPostXml(refundQueryUrl, requestXml);
		logger.info("退费结果查询返回 result = " + result);

		Document doc = JDomXmlParseUtil.strXmlToDocument(result);
		String resultCode = JDomXmlParseUtil.getValueByElementName(doc, "resultCode");
		String refundResult = JDomXmlParseUtil.getValueByElementName(doc, "refundResult");
		if (!"0".equals(resultCode)) {
			throw new Exception(JDomXmlParseUtil.getValueByElementName(doc, "resultDesc"));
		} else if (!"1".equals(refundResult) && !"5".equals(refundResult)) {
			vo.setRefundState(RefundEnumType.REFUND_NO_EXCEPTION.getId());
			throw new Exception("退款结果未知查询中");
		}
	}

	private String formatRequestXml(String orderId, String hisRefundId) {
		String requestXml = String.format(
				"<?xml version=”1.0” encoding=”UTF-8”?>" + 
				"<req>" + 
					"<orderId>%s</orderId>" + 
					"<hisRefundId>%s</hisRefundId>" + 
				"</req>", orderId, hisRefundId);
		return requestXml;
	}
}
