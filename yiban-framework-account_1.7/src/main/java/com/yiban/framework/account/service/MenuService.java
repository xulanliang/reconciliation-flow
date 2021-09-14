package com.yiban.framework.account.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.framework.account.domain.Menu;


public interface MenuService {

    void createMenu(Menu menu);

    Menu findMenuById(Long id);

    void deleteMenu(Long id);

    List<Menu> findAllTopMenus();
    List<Menu> findAll(Collection<SearchFilter> searchFilters);

    List<Menu> findAllTopMenusByPid(Long id);
    
    void filterMenusByCurrentUserPerms(List<Menu> menus);
    
   // List<Menu> findByUrlIsNotNull();
    
    Menu findByUrl(String url);
    Menu findByName(String name);
   // Page<Menu> findAllTopMenus(Collection<SearchFilter> searchFilters, Pageable pageable);
    //Page<Menu> findAllTopMenus(Collection<SearchFilter> searchFilters);
}
