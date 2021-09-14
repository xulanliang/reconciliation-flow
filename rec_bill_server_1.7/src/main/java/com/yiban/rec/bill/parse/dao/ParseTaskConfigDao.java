package com.yiban.rec.bill.parse.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yiban.rec.bill.parse.domain.ParseTaskConfig;

/**
 * @author swing
 * @date 2018年8月3日 下午1:47:03 类说明 账单解析任务配置dao
 */
public interface ParseTaskConfigDao extends JpaRepository<ParseTaskConfig, Long> {
	List<ParseTaskConfig> findByActive(int active);
}
