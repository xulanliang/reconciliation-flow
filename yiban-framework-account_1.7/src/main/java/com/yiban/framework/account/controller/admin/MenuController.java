package com.yiban.framework.account.controller.admin;

import com.google.common.base.Objects;
import com.yiban.framework.account.domain.Menu;
import com.yiban.framework.account.service.MenuService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.util.YiBanEntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

/**
 * 菜单管理
 *
 * @author tantian
 * @date 2018-01-30
 */
@Controller
@RequestMapping("/admin/menu/main")
public class MenuController extends FrameworkController {
	@Autowired
	private MenuService menuService;

	@GetMapping
	public String main() {
		return autoView("admin/menu/main");
	}

	@RestController
	@RequestMapping({ "/admin/menu" })
	class DataController extends BaseController {
		@Logable(operation = "保存菜单")
		@PostMapping
		public ResponseResult save(@Valid Menu menu) {
			if (menu.getName() != null && menu.getName().length() > 0 && menuService.findByName(menu.getName()) != null) {
				return ResponseResult.failure("已经存在相同名称的菜单");
			} else if (menu.getUrl() != null && menu.getUrl().length() > 0 && menuService.findByUrl(menu.getUrl()) != null) {
				return ResponseResult.failure("已经存在相同url的菜单");
			} else {
				try{
					menuService.createMenu(menu);
				}catch(Exception e){
					e.printStackTrace();
					return ResponseResult.failure("添加菜单失败");
				}
				return ResponseResult.success("添加菜单成功");
			}
		}

		@Logable(operation = "修改菜单")
		@PutMapping
		public ResponseResult update(@Valid Menu menu) {
			Menu m = null;
			Menu entity = menuService.findMenuById(menu.getId());
			if (entity == null) {
				return ResponseResult.failure("菜单不存在");
			}
			m = menuService.findByName(menu.getName());
			if (m != null && !menu.getId().equals(m.getId())) {
				return ResponseResult.failure("已经存在相同名称的菜单");
			}
			if (StringUtils.isNotEmpty(menu.getUrl())) {
				m = menuService.findByUrl(menu.getUrl());
				if (m != null && !menu.getId().equals(m.getId())) {
					return ResponseResult.failure("已经存在相同url的菜单");
				}
			}

			Menu parentMenu=menu.getParent();
			if (parentMenu!= null) {
				if(Objects.equal(entity.getId(), parentMenu.getId())){
					return ResponseResult.failure("上一级菜单不能设置为自身");
				}
				if(parentMenu.getParent() !=null && parentMenu.getParent().getId() == entity.getId().longValue()){
					return ResponseResult.failure("上级菜单的父菜单不能为自身");
				}
			}
			
			entity.setName(menu.getName());
			entity.setUrl(menu.getUrl());
			entity.setIconCls(menu.getIconCls());
			entity.setPerm(menu.getPerm());
			entity.setDescription(menu.getDescription());
			entity.setParent(menu.getParent());
			entity.setSort(menu.getSort());
			try{
				menuService.createMenu(entity);
			}catch(Exception e){
				return ResponseResult.failure("修改菜单失败");
			}
			return ResponseResult.success("修改菜单成功");
		}

		@Logable(operation = "删除菜单")
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			Menu menu = menuService.findMenuById(id);
			if (menu == null) {
				return ResponseResult.failure("菜单不存在");
			}
			if(menu.getChildren().size() >0){
				return ResponseResult.failure("请先删除子菜单");
			}
			try{
				menuService.deleteMenu(id);
			}catch(Exception e){
				e.printStackTrace();
				return ResponseResult.failure("删除失败");
			}
			return ResponseResult.success("删除成功");
		}

		// 树形表格
		@GetMapping()
		public List<Menu> list() {
			List<Menu> menuList = menuService.findAll(getSearchFilters());
		    YiBanEntityUtils.filterChild(menuList);
			return menuList;
		}

	}
}
