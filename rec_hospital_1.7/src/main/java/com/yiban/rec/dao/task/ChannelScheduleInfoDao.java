package com.yiban.rec.dao.task;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.task.ChannelScheduleInfo;

public interface ChannelScheduleInfoDao
		extends JpaRepository<ChannelScheduleInfo, Long>, JpaSpecificationExecutor<ChannelScheduleInfo> {

	@Modifying
	@Query("update ChannelScheduleInfo c set c.jobsStatus = ?1 where c.id = ?2")
	void updateStatus(Integer status, Long id);
	
	@Query("select cs from ChannelScheduleInfo cs where cs.jobsStatus =1 AND cs.isDeleted=0 order by cs.createdDate asc")
    List<ChannelScheduleInfo> getChannelScheduleInfosOfYesterday();
	
	@Query("select cs from ChannelScheduleInfo cs where cs.jobsStatus =1 AND cs.isDeleted=0 AND cs.jobName=?1")
	ChannelScheduleInfo getChannelScheduleInfosByName(String jobName);
	
	@Query("select cs from ChannelScheduleInfo cs where cs.jobsStatus =1 AND cs.isDeleted=0 AND cs.startat < ?1 order by cs.createdDate asc")
    List<ChannelScheduleInfo> getChannelScheduleInfosOfBeforeTwoDay(Date beforeTwoDay);

}
