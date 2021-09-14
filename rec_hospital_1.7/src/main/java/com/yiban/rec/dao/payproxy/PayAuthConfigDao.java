package com.yiban.rec.dao.payproxy;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.payproxy.PayAuthConfig;

/**
 * @author swing
 * @date 2018年7月5日 上午9:47:50 类说明 认证授权配置dao
 */
public interface PayAuthConfigDao extends JpaRepository<PayAuthConfig, Long>, JpaSpecificationExecutor<PayAuthConfig> {
	@Modifying
	@Query("update PayAuthConfig p set p.state=?2 where p.id=?1")
	void updateState(long id,int state);
	PayAuthConfig findByApiKey(String key);
	Page<PayAuthConfig> findByClientNameContaining(String clientName,Pageable pageable);
}
