package com.yiban.rec.bill.parse.util;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.yiban.framework.account.domain.PropertiesConfig;
import com.yiban.rec.util.CommonEnum;

public class ProConfigManager {

	private static final Logger logger = LoggerFactory.getLogger(ProConfigManager.class);

	private static String printLogFlag;
	static {
		printLogFlag = CommonPropertiesUtils.getValue("print.log.flag", "false");
		if (!StringUtils.equals("false", printLogFlag) && !StringUtils.equals("true", printLogFlag)) {
			printLogFlag = "false";
		}
	}

	private ProConfigManager() {
	}

	public static String getValueByPkey(EntityManager entity, String pkey, String defaultValue) {
		String value = getValueByPkey(entity, pkey);
		if (StringUtils.isBlank(value)) {
			value = defaultValue;
		}
		return value;
	}

	public static String getValueByPkey(EntityManager entity, String pkey) {
		PropertiesConfig propertiesConfig = getInstance(entity, pkey);

		if (propertiesConfig == null) {
			return null;
		}
		String value = propertiesConfig.getPvalue();
		if (StringUtils.isBlank(value)) {
			value = propertiesConfig.getDefaultValue();
		}
		if (Boolean.valueOf(printLogFlag)) {
			logger.info("属性键【{}】，属性值为【{}】", pkey, value);
		}
		return value;
	}

	public static PropertiesConfig getInstance(EntityManager entity, String pkey) {
		String sql = String.format(
				"SELECT * FROM t_properties_config t WHERE 1 = 1 AND t.`pkey` = '%s' AND t.`is_actived`='%s' ", pkey,
				CommonEnum.IsActive.ISACTIVED.getValue());
		Session session = entity.unwrap(Session.class);
		Query query = session.createSQLQuery(sql).addEntity(PropertiesConfig.class);
		PropertiesConfig propertiesConfig = (PropertiesConfig) query.uniqueResult();

		if (Boolean.valueOf(printLogFlag)) {
			logger.info("属性键【{}】，数据库配置数据为【{}】", pkey, new Gson().toJson(propertiesConfig));
		}
		return propertiesConfig;
	}
}
