package com.yiban.framework.dict.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.dict.domain.MetaData;

public interface MetaDataService {

	void save(MetaData metaData);

	void update(MetaData metaData);

	MetaData findMetaDataById(long id);

	List<MetaData> findMetaDataByName(String name);
	

	MetaData findMetaDataByValue(String value);
	
	//根据字典值和字典类型id获取字典
	MetaData findMetaDataByValueAndTypeId(String value, Long typeId);
	
	//根据字典值和字典类型值获取字典
	MetaData findMetaDataByValueAndTypeValue(String value, String typeVlue);
	
	Page<MetaData> findAll(Integer dictType, Pageable pageable);
	
	Page<MetaData> findAll(Collection<SearchFilter> searchFilters, Pageable pageable);
	
	void delete(long id);
	
	List<ValueTextable<String>> valueAsList();
	
	List<ValueTextable<String>> NameAsList();

	List<MetaData> findMetaDataByDataTypeValue(String typeValue);
	
	List<MetaData> findAllMetaData();
	
	List<ValueTextable<String>> asValue();
	
	//获取name和value值
	List<ValueTextable<String>> getNameValueAsList();
	
	List<MetaData> findMetaDataByTypeId(Long id);
	
	List<MetaData> findByTypeIdOrderBySort(String typeValue);
}
