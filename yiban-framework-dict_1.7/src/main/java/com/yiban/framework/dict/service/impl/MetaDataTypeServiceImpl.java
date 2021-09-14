package com.yiban.framework.dict.service.impl;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;

import com.google.common.collect.Lists;
import com.yiban.framework.core.domain.base.ValueText;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.service.BaseService;
import com.yiban.framework.dict.dao.MetaDataTypeDao;
import com.yiban.framework.dict.domain.MetaDataType;
import com.yiban.framework.dict.service.MetaDataTypeService;
@Service
public class MetaDataTypeServiceImpl extends BaseService implements MetaDataTypeService {
	@Autowired
	private MetaDataTypeDao metaDataTypeDao;
	
	@Transactional
	@Override
	public void save(MetaDataType metaDataType) {
	     metaDataTypeDao.save(metaDataType);
	}
	
	@Transactional
	@Override
	public void update(MetaDataType metaDataType) {
		metaDataTypeDao.save(metaDataType);
	}
	
	@Override
	public MetaDataType findMetaDataTypeByName(String name) {
		
		return metaDataTypeDao.findMetaDataTypeByName(name);
	}

	@Override
	public MetaDataType findMetaDataTypeByValue(String value) {
		return metaDataTypeDao.findMetaDataTypeByValue(value);
	}

	/**
	 * 查找未删除的，已激活的字典类型
	 */
	@Override
	public List<MetaDataType> findAll() {
		List<MetaDataType> list= metaDataTypeDao.findAll();
		return list;
	}

	/**
	 * 查找未删除的字典类型
	 */
	@Override
	public Page<MetaDataType> findAll(Collection<SearchFilter> searchFilters, Pageable pageable) {
		 Specifications<MetaDataType> spec = Specifications.where(bySearchFilter(searchFilters, MetaDataType.class))
				 .and(builtinSpecs.notDelete()).and(builtinSpecs.isActived());
    	 return metaDataTypeDao.findAll(spec, pageable);
	}

	@Override
	public List<ValueTextable<String>> valueAsList() {
		List<ValueTextable<String>> result = Lists.newArrayList();
		List<MetaDataType> metaDatas = this.findAll();
		if (metaDatas != null && metaDatas.size() > 0) {
			for (MetaDataType metaData : metaDatas) {
				result.add(new ValueText<>(metaData.getId().toString(), metaData.getName()));
			}
		}
		return result;
	}

	@Transactional
	@Override
	public void delete(long id) {
		metaDataTypeDao.delete(id);
	}

	@Override
	public MetaDataType findMetaDataTypeById(long id) {
		return metaDataTypeDao.findMetaDataTypeById(id);
	}

}
