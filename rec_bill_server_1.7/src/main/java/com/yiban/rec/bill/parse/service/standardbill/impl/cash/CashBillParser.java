package com.yiban.rec.bill.parse.service.standardbill.impl.cash;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;

public class CashBillParser extends AbstractBillParser<RecCash> {

	@Override
	protected List<RecCash> doParse(String orgCode, String date) throws BillParseException {

		// 查询机构下边的子机构
		Set<String> orgCodeSet = new HashSet<String>();
		if (StringUtils.isNotBlank(orgCode) && !orgCode.equals("0")) {
			orgCodeSet.add(orgCode);
			Session session = entityManager.unwrap(Session.class);
			StringBuffer sb = new StringBuffer(" select * from t_organization t where t.code=" + orgCode);
			Query query = session.createSQLQuery(sb.toString()).addEntity(Organization.class);
			Organization org = (Organization) query.uniqueResult();
			if (org != null) {
				for (Organization child : org.getChildren()) {
					orgCodeSet.add(child.getCode());
				}
			}
		}
		String orgCodeSql = StringUtils.join(orgCodeSet, ",");
		List<Map<String, Object>> orderUploads = getCashOrderListByOrderUpload(orgCodeSql, date);
		List<Map<String, Object>> refundOrders = getCashOrderListByRefundUpload(orgCodeSql, date);
		orderUploads.addAll(refundOrders);
		List<RecCash> recCashs = convertToRecCash(orderUploads);
		return recCashs;
	}

	private List<RecCash> convertToRecCash(List<Map<String, Object>> orderUploads) {
		List<RecCash> recCashList = new ArrayList<>();
		if (orderUploads != null && orderUploads.size() > 0) {
			RecCash recCash = null;
			for (Map<String, Object> orderMap : orderUploads) {
				recCash = new RecCash();
				recCash.setOrgNo(orderMap.get("org_code")==null?null:orderMap.get("org_code").toString());
				recCash.setPayType(orderMap.get("pay_type")==null?null:orderMap.get("pay_type").toString());
				// 三方支付流水号
				recCash.setPayFlowNo(orderMap.get("tsn_order_no")==null?null:orderMap.get("tsn_order_no").toString());
				recCash.setPayAmount(new BigDecimal(orderMap.get("pay_amount")==null?null:orderMap.get("pay_amount").toString()));
				recCash.setTradeDatatime(orderMap.get("trade_date_time")==null?null:DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",
						orderMap.get("trade_date_time").toString()));
				// order_upload表所有的数据都默认为支付,refund_upload都是退款
				recCash.setOrderState(StringUtils.equals(EnumTypeOfInt.TRADE_TYPE_REFUND.getValue(),
						orderMap.get("order_state")==null?null:orderMap.get("order_state").toString()) ? EnumTypeOfInt.TRADE_TYPE_REFUND.getValue()
								: EnumTypeOfInt.TRADE_TYPE_PAY.getValue());
				recCash.setPayBusinessType(orderMap.get("pay_business_type")==null?null:orderMap.get("pay_business_type").toString());
				recCash.setPatientName(orderMap.get("patient_name")==null?null:orderMap.get("patient_name").toString());
				recCash.setPatientCardNo(orderMap.get("patient_card_no")==null?null:orderMap.get("patient_card_no").toString());
				recCash.setGoodInfoList(orderMap.get("good_info")==null?null:orderMap.get("good_info").toString());
				recCash.setBillSource(orderMap.get("bill_source")==null?null:orderMap.get("bill_source").toString());
				recCash.setPatType(orderMap.get("pat_type")==null?null:orderMap.get("pat_type").toString());
				recCash.setPayLocation(orderMap.get("pay_location") == null ? null : orderMap.get("pay_location").toString());

				recCashList.add(recCash);
			}
		}
		return recCashList;
	}

	/**
	 * 从order_upload获取现金支付账单列表
	 * 
	 * @param orgCodeSql
	 * @param payType
	 * @param date
	 * @return
	 */
	private List<Map<String, Object>> getCashOrderListByOrderUpload(String orgCodeSql, String date) {
		String sql = String.format(
				"SELECT * FROM t_order_upload t WHERE t.`org_code` IN(%s) AND t.`pay_type`='0049' AND t.`trade_date`='%s'",
				orgCodeSql, date);
		javax.persistence.Query query = entityManager.createNativeQuery(sql);
		query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> orderList = query.getResultList();
		return orderList;
	}
	/**
	 * 从refund_upload表获取退费账单列表
	 * 
	 * @param orgCodeSql
	 * @param date
	 * @return
	 */
	private List<Map<String, Object>> getCashOrderListByRefundUpload(String orgCodeSql, String date) {
		String sql = String.format("SELECT " + 
					"  t.`org_code`, " + 
					"  t.`pay_type`, " + 
					"  t.`tsn_order_no`, " + 
					"  tu.`refund_amount` pay_amount, " + 
					"  tu.`refund_date_time` trade_date_time, " + 
					"  '0256' order_state, " + 
					"  t.`pay_business_type`, " + 
					"  t.`patient_name`, " + 
					"  t.`patient_card_no`, " + 
					"  t.`good_info`, " + 
					"  t.`bill_source`, " + 
					"  t.`pat_type`, " + 
					"  t.`pay_location` " + 
					"  FROM t_order_upload t INNER JOIN t_refund_upload tu ON t.`tsn_order_no` = tu.`ori_tsn_order_no` " + 
					"  WHERE t.`org_code` IN (%s) " + 
					"  AND t.`pay_type` = '0049' " + 
					"  AND t.`trade_date` = '%s'",
				orgCodeSql, date);
		javax.persistence.Query query = entityManager.createNativeQuery(sql);
		query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> orderList = query.getResultList();
		return orderList;
	}
 
	@Override
	protected void clearBill(String orgCode, String date, EntityManager entityManager, String payType) {
    	String tableName = " t_rec_cash ";
    	String sDate = date + " 00:00:00";
		String eDate = date + " 23:59:59";
    	String sql = String.format("DELETE FROM %s "
    							 + "WHERE org_no = '%s' "
//    							 + "AND bill_source = 'self' "
    							 + "AND Trade_datatime >= '%s' "
    							 + "AND Trade_datatime <= '%s' AND pay_type = '%s'", 
    							    tableName,orgCode,sDate,eDate,payType);
    	
    	Session session = entityManager.unwrap(org.hibernate.Session.class);
		SQLQuery query = session.createSQLQuery(sql);
		int count = query.executeUpdate();
		logger.info("clearBill count = " + count + ", sql = " + sql);
	}
}
