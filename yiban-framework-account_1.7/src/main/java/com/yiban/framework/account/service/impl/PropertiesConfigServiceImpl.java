package com.yiban.framework.account.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.yiban.framework.account.dao.PropertiesConfigDao;
import com.yiban.framework.account.domain.PropertiesConfig;
import com.yiban.framework.account.service.PropertiesConfigService;

@Service
public class PropertiesConfigServiceImpl implements PropertiesConfigService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PropertiesConfigDao propertiesConfigDao;

	@Value("${print.log.flag:false}")
	private String printLogFlag;
	{
		if (!StringUtils.equals("false", printLogFlag) && !StringUtils.equals("true", printLogFlag)) {
			printLogFlag = "false";
		}
	}

	@Override
	public Page<PropertiesConfig> findPage(String pkey, String type, String model, String description,
			Pageable pageable) {

		Specification<PropertiesConfig> spec = new Specification<PropertiesConfig>() {
			@Override
			public Predicate toPredicate(Root<PropertiesConfig> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if (StringUtils.isNotBlank(pkey)) {
					Path<String> keyExp = root.get("pkey");
					predicates.add(cb.like(keyExp, "%" + pkey + "%"));
				}
				if (StringUtils.isNotBlank(type)) {
					Path<String> typeExp = root.get("type");
					predicates.add(cb.equal(typeExp, type));
				}
				if (StringUtils.isNotBlank(model)) {
					Path<String> modelExp = root.get("model");
					predicates.add(cb.equal(modelExp, model));
				}
				if (StringUtils.isNotBlank(description)) {
					Path<String> descriptionExp = root.get("description");
					predicates.add(cb.like(descriptionExp, "%" + description + "%"));
				}

				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return propertiesConfigDao.findAll(spec, pageable);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdate(PropertiesConfig propertiesConfig) {
		propertiesConfig.setPkey(propertiesConfig.getPkey().trim());
		propertiesConfig.setPvalue(propertiesConfig.getPvalue().trim());
		propertiesConfigDao.save(propertiesConfig);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long id) {
		propertiesConfigDao.delete(id);
	}

	@Override
	public String findValueByPkey(String pkey, String defaultValue) {
		String value = this.findValueByPkey(pkey);
		if (StringUtils.isBlank(value)) {
			value = defaultValue;
		}
		if (Boolean.valueOf(printLogFlag)) {
			logger.info("属性键【{}】，属性值为【{}】", pkey, value);
		}
		return value;
	}

	@Override
	public String findValueByPkey(String pkey) {
		PropertiesConfig config = propertiesConfigDao.findOneByPkeyAndIsActived(pkey, 1);

		if (config == null) {
			return null;
		}

		if (Boolean.valueOf(printLogFlag)) {
			logger.info("属性键【{}】，数据库配置数据为【{}】", pkey, new Gson().toJson(config));
		}

		String value = config.getPvalue();
		if (StringUtils.isBlank(value)) {
			value = config.getDefaultValue();
		}
		return value;
	}

	@Override
	public PropertiesConfig findOneByPkey(String pkey) {
		return propertiesConfigDao.findOneByPkey(pkey);
	}

}
