package com.yiban.framework.dict.dao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import com.yiban.framework.dict.domain.MetaDataType;

@RepositoryDefinition(domainClass=MetaDataType.class,idClass=Long.class)
public interface MetaDataTypeDao extends JpaRepository<MetaDataType, Long>,JpaSpecificationExecutor<MetaDataType>{
	
	
	@Query("SELECT metaDataType FROM MetaDataType metaDataType WHERE metaDataType.isDeleted=0 AND metaDataType.isActived=1 AND metaDataType.name=?1")
	MetaDataType findMetaDataTypeByName(String name);
	
	@Query("SELECT metaDataType FROM MetaDataType metaDataType WHERE metaDataType.isDeleted=0 AND metaDataType.isActived=1 AND metaDataType.value=?1")
	MetaDataType findMetaDataTypeByValue(String value);
	
	@Query("SELECT metaDataType FROM MetaDataType metaDataType WHERE metaDataType.isDeleted=0 AND metaDataType.id=?1")
	MetaDataType findMetaDataTypeById(long id);
	
	@Query("SELECT metaDataType FROM MetaDataType metaDataType WHERE metaDataType.isDeleted=0 AND metaDataType.isActived=1")
	List<MetaDataType> findAll();
	
	//去除逻辑删除，采用物理删除
	/*@Modifying
	@Query("UPDATE MetaDataType metaDataType SET metaDataType.isDeleted=1 WHERE metaDataType.id=?1")
	void delete(long id);*/
}
