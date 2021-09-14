package com.yiban.framework.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yiban.framework.account.domain.User;

import java.util.List;

public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByLoginName(String loginName);

    List<User> findByName(String name);

    @Query("select u from User u where u.isDeleted = 0 and (u.loginName = :loginID or u.email = :loginID or u.mobilePhone = :loginID)")
    User findUserByLoginID(@Param("loginID") String loginID);
    
    @Modifying
    @Query("update User u set u.isDeleted = 1 where u.id = ?1")
    void deleteUser(Long id);
    
    @Modifying
    @Query("update User u set u.status = ?1 where u.id = ?2")
    void updateStatus(Long status,Long id);
    
    
    @Query("select u from User u where u.isDeleted = 0 and u.loginName=?1")
    User findValideUserByLoginName(String loginName);
    
    @Query("select u from User u where u.isDeleted = 0 and u.email=?1")
    User findValideUserByEmail(String email);
    
    @Query("select u from User u where u.isDeleted = 0 and u.email=?1 and u.id!=?2")
    User findValideUserByEmail(String email,Long id);
    
    @Query("select u from User u where u.isDeleted = 0 and u.mobilePhone=?1")
    User findValideUserByPhone(String mobilePhone);
    
    @Query("select u from User u where u.isDeleted = 0 and u.mobilePhone=?1 and u.id!=?2")
    User findValideUserByPhone(String mobilePhone,Long id);
    
    @Query("select u from User u where u.isDeleted = 0 and u.password=?1 and u.loginName!=?2")
    User isCurrentUserPassword(String password,String userName);
    
    @Query("select u from User u where u.isDeleted = 0 and u.id=?1")
    User findById(Long id);
}
