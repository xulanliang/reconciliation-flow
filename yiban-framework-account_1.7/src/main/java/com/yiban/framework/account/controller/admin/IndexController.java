package com.yiban.framework.account.controller.admin;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiban.framework.account.domain.Menu;
import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.account.service.MenuService;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.AppConfig;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.service.SessionUserService;

/**
 * @author swing
 * @date 2018年1月8日 上午10:44:28 类说明 管理后台框架首页
 */
@Controller
public class IndexController extends FrameworkController {
	@Autowired
	private SessionUserService sessionUserService;
	@Autowired
	private MenuService menuService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private AccountService accountService;
	
	// 跟路径访问转发到首页
	@GetMapping("/")
	public String index() {
		return "redirect:/admin";
	}

	// 用户登录后跳转页面
	@GetMapping("/admin")
	public String main(Model m) {
		
		SessionUser user = sessionUserService.getCurrentSessionUser();
		if(user.getLoginName().equals("admin")){
			m.addAttribute("roleName","超级管理员");
		}else{
			Role role=null;
			Iterable<Role> roleList =accountService.getUserRoles(user.getId());
			Iterator<Role> ite =roleList.iterator();
			if(ite.hasNext()){
				role =ite.next();
			}
			if(role !=null){
				m.addAttribute("roleName",role.getName());
			}else{
				m.addAttribute("roleName","未知");
			}
		}
	    m.addAttribute("currentUser", user.getLoginName());
	    m.addAttribute("appConfig", appConfig);
		return autoView("admin/index");
	}
	
	//左边导航菜单列表
	@GetMapping(value="/admin/navigation",produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseResult indexData(){
		List<Menu> menuList = menuService.findAllTopMenus();
		menuService.filterMenusByCurrentUserPerms(menuList);
		return ResponseResult.success().data(menuList);
	}

}
