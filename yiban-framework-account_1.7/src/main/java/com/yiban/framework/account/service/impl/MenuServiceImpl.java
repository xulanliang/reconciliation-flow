package com.yiban.framework.account.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;

import com.google.common.base.Strings;
import com.yiban.framework.account.dao.MenuDao;
import com.yiban.framework.account.domain.Menu;
import com.yiban.framework.account.service.MenuService;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;
import com.yiban.framework.core.service.BaseService;
import com.yiban.framework.core.service.SessionUserService;

@Service
public class MenuServiceImpl extends BaseService implements MenuService {
	@Autowired
	private MenuDao menuDao;
	
	@Autowired
	private SessionUserService sessionService;

	@Override
	@Transactional
	public void createMenu(Menu menu) {
		menuDao.save(menu);
	}

	@Override
	public Menu findMenuById(Long id) {
		return menuDao.findOne(id);
	}

	@Override
	@Transactional
	public void deleteMenu(Long id) {
		//menuDao.deleteMenu(id);
		//物理删除
		menuDao.delete(id);
	}

	@Override
	public List<Menu> findAllTopMenus() {
		Sort sort = new Sort(new Order(Direction.ASC, "sort"), new Order(Direction.ASC, "id"));
		List<Menu> list =menuDao.findByParentIsNull(sort);
		//过滤已经删除的，未激活的菜单
		filterMenusByCurrentUserPerms(list);
		return list;
	}


	//过来当前用户没有权限的，已经删除的，未激活的菜单
	@Override
	public void filterMenusByCurrentUserPerms(List<Menu> menus) {
		if (menus == null || menus.isEmpty()) {
			return;
		}
		Iterator<Menu> iter = menus.iterator();
		while (iter.hasNext()) {
			Menu menu = iter.next();
			String menuPerm = menu.getPerm();
			boolean noPerm = !Strings.isNullOrEmpty(menuPerm) && !sessionService.isPermitted(menu.getPerm());
			boolean isDeleted = false;
			if (menu.getIsDeleted() == DeleteEnum.YES.getValue() || menu.getIsActived() == ActiveEnum.NO.getValue()) {
				isDeleted = true;
			}
			if (noPerm || isDeleted) {
				iter.remove();
			} else {
				filterMenusByCurrentUserPerms(menu.getChildren());
			}
		}
	}

	@Override
	public List<Menu> findAll(Collection<SearchFilter> searchFilters) {
		Sort sort = new Sort(new Order(Direction.ASC, "sort"), new Order(Direction.ASC, "id"));
		Specifications<Menu> spec = Specifications.where(bySearchFilter(searchFilters, Menu.class)).and(builtinSpecs.notDelete());
		return menuDao.findAll(spec, sort);
	}

	@Override
	public List<Menu> findAllTopMenusByPid(Long id) {
		return menuDao.findByParentId(id);
	}

	@Override
	public Menu findByUrl(String url) {
		return menuDao.findByUrl(url);
	}

	@Override
	public Menu findByName(String name) {
		return menuDao.findByName(name);
	}
}
