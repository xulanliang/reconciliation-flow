package com.yiban.rec.bill.parse.service.standardbill.impl.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yiban.rec.bill.parse.vo.ExtraParamVo;

/**
 * 从上送表查询扩展字段信息
 */
public class QueryUploadOrder implements QueryOrder {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private EntityManager entityManager;
	private String orgCode, date;

	public QueryUploadOrder(String orgCode, String date, EntityManager entityManager) {
		this.orgCode = orgCode;
		this.date = date;
		this.entityManager = entityManager;
	}

	// 方法入口
	public HashMap<String, ExtraParamVo> query() {
		return convert(getCashOrderListByOrderUpload());
	}

	/**
	 * 从order_upload获取账单列表
	 * 
	 * @param orgCodeSql
	 * @param payType
	 * @param date
	 * @return
	 */
	private List<Map<String, Object>> getCashOrderListByOrderUpload() {
		String sql = String.format(
				"SELECT * FROM t_order_upload t WHERE t.`org_code`= '%s' AND t.`pay_type`!='0049' AND t.`trade_date`='%s'",
				orgCode, date);
		javax.persistence.Query query = entityManager.createNativeQuery(sql);
		query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> orderList = query.getResultList();
		return orderList;
	}

	private HashMap<String, ExtraParamVo> convert(List<Map<String, Object>> orderUploads) {
		HashMap<String, ExtraParamVo> map = new HashMap<>();
		if (orderUploads == null || orderUploads.size() == 0) {
			return map;
		}

		for (Map<String, Object> orderMap : orderUploads) {
			ExtraParamVo vo = new ExtraParamVo();
			String payFlowNo = orderMap.get("tsn_order_no") == null ? "" : orderMap.get("tsn_order_no").toString();
			if (StringUtils.isBlank(payFlowNo)) {
				continue;
			}
			String bsName = orderMap.get("patient_name") == null ? "" : orderMap.get("patient_name").toString();
			String bsCardNo = orderMap.get("patient_card_no") == null ? "" : orderMap.get("patient_card_no").toString();
			if (StringUtils.isBlank(bsCardNo) && StringUtils.isBlank(bsName)) {
				continue;
			}
			vo.setBsName(bsName);
			vo.setBsCardNo(bsCardNo);

			map.put(payFlowNo, vo);
		}
		logger.info("从上送表获取扩展字段数据，size = {}", map.size());
		return map;
	}
}
