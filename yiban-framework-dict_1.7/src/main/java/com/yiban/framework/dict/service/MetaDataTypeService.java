package com.yiban.framework.dict.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.dict.domain.MetaDataType;

public interface MetaDataTypeService {
	void save(MetaDataType metaDataType);
	void update(MetaDataType metaDataType);
	MetaDataType findMetaDataTypeByName(String name);
	MetaDataType findMetaDataTypeByValue(String value);
	MetaDataType findMetaDataTypeById(long id);
	List<MetaDataType> findAll();
	List<ValueTextable<String>> valueAsList();
	Page<MetaDataType> findAll(Collection<SearchFilter> searchFilters, Pageable pageable);
	void delete(long id);
}
