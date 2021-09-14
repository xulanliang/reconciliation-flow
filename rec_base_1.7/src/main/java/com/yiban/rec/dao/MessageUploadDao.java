package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.MessageUpload;

public interface MessageUploadDao extends JpaRepository<MessageUpload, Long>, JpaSpecificationExecutor<MessageUpload>{
	
	List<MessageUpload> findByOrgNo(String orgNo);
	
	@Transactional
	@Modifying
	@Query("delete from MessageUpload where orgNo = ?1")
	void deleteByOrgNo(String orgNo);

}
