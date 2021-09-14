package com.yiban.framework.account.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.framework.account.domain.Menu;

public interface MenuDao extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {

    List<Menu> findByParentIsNull();

    List<Menu> findByParentId(Long id);
    
    List<Menu> findByUrlIsNotNull();
    
    List<Menu> findByParentIsNull(Sort sort);
    
    @Query("select m from Menu m where m.url = ?1 and m.isDeleted=0")
    Menu findByUrl(String url);
    
    @Query("select m from Menu m where m.name = ?1 and m.isDeleted=0")
    Menu findByName(String name);
    
    @Modifying
    @Query("update Menu u set u.isDeleted = 1 where u.id = ?1")
    void deleteMenu(Long id);
}
