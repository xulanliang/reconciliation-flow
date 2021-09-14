package com.yiban.rec.bill.parse.service.standardbill.impl.config;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.core.util.StringUtil;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.BeanUtil;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.bill.parse.util.XmlUtil;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.WebServiceFieldMappingEntity;

/**
 * 通用版webservice方式拉取账单映射到实体
 * 
 * @author clearofchina
 *
 */
public class WebServiceBillParser extends AbstractBillParser<HisTransactionFlow> {

	@Override
	protected List<HisTransactionFlow> doParse(String orgCode, String date) throws BillParseException {
		String response = null;
		try {
			String requestUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisOrderWebserviceUrl);
			String requestParams = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisOrderWebserviceParam);
			response = HttpClientUtil.doPostXml(requestUrl, requestParams);
			return mapping(response);
		} catch (Exception e) {
			logger.error("调用his接口异常：" + e);
		}
		return null;
	}

	public List<HisTransactionFlow> mapping(String response) throws Exception {
		ArrayList<HisTransactionFlow> hisList = new ArrayList<>();

		String keys = ProConfigManager.getValueByPkey(entityManager, ProConstants.xmlNodes);
		// 解析xml
		JSONArray jsonArray = XmlUtil.parse2JSONArray(response, keys);
		if (jsonArray == null || jsonArray.length() == 0) {
			return hisList;
		}

		// 映射关系
		ArrayList<WebServiceFieldMappingEntity> mappings = this.getMappings();

		for (int i = 0, len = jsonArray.length(); i < len; i++) {
			JSONObject obj = jsonArray.getJSONObject(i);
			HisTransactionFlow his = new HisTransactionFlow();

			// 遍历映射关系
			for (WebServiceFieldMappingEntity entity : mappings) {

				String dataFieldName = entity.getDataFieldName();
				String classFieldName = entity.getClassFieldName();
				String defaultValue = entity.getDefaultValue();

				String value = !obj.has(dataFieldName) || StringUtil.isNullOrEmpty(obj.get(dataFieldName))
						? defaultValue
						: obj.get(dataFieldName).toString();

				if (StringUtil.isEmpty(value)) {
					continue;
				}

				// 赋值操作
				BeanUtil.setFieldValue(his, classFieldName, value);
			}

			hisList.add(his);
		}

		return hisList;
	}

	/**
	 * 获取映射关系
	 * 
	 * @return
	 */
	public ArrayList<WebServiceFieldMappingEntity> getMappings() {
		Session session = super.entityManager.unwrap(Session.class);
		String sql = "select * from t_field_mapping ";
		SQLQuery query = session.createSQLQuery(sql).addEntity(WebServiceFieldMappingEntity.class);
		@SuppressWarnings("unchecked")
		ArrayList<WebServiceFieldMappingEntity> list = (ArrayList<WebServiceFieldMappingEntity>) query.list();
		return list;
	}
}
