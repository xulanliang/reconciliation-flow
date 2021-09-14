package com.yiban.framework.dict.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import com.yiban.framework.dict.domain.MetaData;

@RepositoryDefinition(domainClass = MetaData.class, idClass = Long.class)
public interface MetaDataDao extends JpaRepository<MetaData, Long>, JpaSpecificationExecutor<MetaData> {

	@Query("SELECT metaData FROM MetaData metaData WHERE metaData.isDeleted=0 AND isActived=1 AND metaData.name = ?1")
	List<MetaData> findMetaDataByName(String name);

	@Query("SELECT metaData FROM MetaData metaData WHERE metaData.isDeleted=0 AND isActived=1 AND metaData.value = ?1")
	MetaData findMetaDataByValue(String value);

	// 根据键值查找唯一数据
	@Query("SELECT metaData FROM MetaData metaData WHERE metaData.isDeleted=0 AND isActived=1 AND metaData.value = ?1 AND  metaData.dictType.id=?2")
	MetaData findMetaDataByValueAndTypeId(String value, Long typeId);

	@Query("SELECT metaData FROM MetaData metaData WHERE metaData.isDeleted=0 AND isActived=1 AND metaData.value = ?1 AND  metaData.dictType.value=?2")
	MetaData findMetaDataByValueAndTypeValue(String value, String typeVlue);
	
	/*@Modifying
	@Query("UPDATE MetaData metaData SET metaData.isDeleted=1 WHERE metaData.id=?1")
	void delete(long id);*/

	@Query("SELECT metaData FROM MetaData metaData WHERE metaData.isDeleted=0 AND isActived=1 AND metaData.dictType.id=?1 order by metaData.name")
	List<MetaData> findMetaDataByDataType(long id);
	
	@Query("SELECT metaData FROM MetaData metaData WHERE dictType.id=?1 AND isDeleted = 0 AND isActived = 1 order by sort asc ")
	List<MetaData> findByTypeIdOrderBySortAsc(Long typeId);
	
	@Query("SELECT metaData FROM MetaData metaData WHERE metaData.isDeleted=0 AND metaData.isActived=1 order by metaData.name")
	List<MetaData> findAllMetaData();
	
	@Query("SELECT metaData FROM MetaData metaData WHERE dictType.id=?1")
	List<MetaData> findMetaDataByTypeId(Long typeId);
	
}
