package com.yiban.framework.account.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Splitter;
import com.yiban.framework.account.domain.Permissions;
import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.RolePermissions;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;

/**
 * 角色管理
 *
 * @author tantian
 * @date 2018-01-31
 */
@Controller
@RequestMapping("/admin/role/main")
public class RoleController extends FrameworkController {

	@GetMapping()
	public String index(ModelMap model) {
		return this.autoView("admin/role/main");
	}

	@RestController
	@RequestMapping({ "/admin/role" })
	class DataController extends BaseController {

		@Autowired
		private AccountService accountService;

		@Logable(operation = "保存角色")
		@PostMapping
		public ResponseResult save(@Valid Role role, BindingResult result) {
			if (accountService.findByRoleName(role.getName()) != null) {
				return ResponseResult.failure("角色已经存在");
			}
			try {
				accountService.createRole(role);
			} catch (Exception e) {
				logger.debug(e.getMessage());
				return ResponseResult.failure("添加角色失败");
			}
			return ResponseResult.success("添加角色成功");
		}

		@Logable(operation = "修改角色")
		@PutMapping
		public ResponseResult update(@Valid Role role) {
			Role entity = this.accountService.findRoleById(role.getId());
			if (entity == null) {
				return ResponseResult.failure("角色不存在");
			}
			Role tempRole = this.accountService.findByRoleName(role.getName());
			if (tempRole != null && !role.getId().equals(tempRole.getId())) {
				return ResponseResult.failure("角色已经存在");
			}
			entity.setName(role.getName());
			entity.setDescription(role.getDescription());
			entity.setRealm(role.getRealm());
			try {
				accountService.updateRole(entity);
			} catch (Exception e) {
				logger.debug(e.getMessage());
				return ResponseResult.failure("修改角色失败");
			}
			return ResponseResult.success("修改角色成功");
		}

		@Logable(operation = "删除角色")
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			try {
				accountService.deleteRole(id);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return ResponseResult.failure("删除失败");
			}
			return ResponseResult.success("删除成功");
		}

		@Logable(operation = "新增角色权限")
		@PostMapping({ "/{id}/modifyPermissions" })
		public ResponseResult addRolePermissions(@RequestParam("roleId") Long roleId,
				@RequestParam(value = "permissionsIds", required = false) String permissionsIds) {
			try {
				this.accountService.removeRolePermissionsByRoleId(roleId);
				List<RolePermissions> rolePermsList = new ArrayList<RolePermissions>();
				if (null != permissionsIds && !permissionsIds.isEmpty()) {
					Iterable<String> ids = Splitter.on(",").split(permissionsIds);
					Iterator<String> var5 = ids.iterator();

					while (var5.hasNext()) {
						String idStr = (String) var5.next();
						RolePermissions rolePerm = new RolePermissions();
						rolePerm.setRoleId(roleId);
						rolePerm.setPermissionsId(Long.parseLong(idStr));
						rolePermsList.add(rolePerm);
					}

					this.accountService.addPermissionsToRole(rolePermsList);
				}

				return ResponseResult.success();
			} catch (Exception var8) {
				logger.error("addRolePrivilege", var8);
				if (var8 instanceof ServiceException) {
					ServiceException es = (ServiceException) var8;
					return ResponseResult.failure("更新有误:" + es.getMessage())
							.debugMessage(var8.toString() + "请刷新权限后重试！");
				} else {
					return ResponseResult.failure("更新有误").debugMessage(var8.toString());
				}
			}
		}

		@GetMapping()
		public WebUiPage<Role> list() {
			Page<Role> data = this.accountService.findAllRoles(this.getSearchFilters(),
					this.getRequestPageabledWithInitSort(this.getIdAscSort()));
			return this.toWebUIPage(data);
		}

		@GetMapping("/{id}/permissionsData")
		public List<Permissions> permissionsData(@RequestParam("id") Long roleId) {
			List<Permissions> permissionsList = this.accountService.findPermissionsByRoleId(roleId,
					this.getSortFromDatagrid());
			this.filterList(permissionsList);
			List<Permissions> rolesList = this.accountService.findPermissionsByRole(roleId, this.getSortFromDatagrid());
			this.filterList(permissionsList, rolesList);
			return permissionsList;
		}

		/**
		 * 权限过滤
		 * 
		 * @param permissionsLis
		 */
		private void filterList(List<Permissions> permissionsLis) {
			Iterator<Permissions> ite = permissionsLis.iterator();

			while (ite.hasNext()) {
				Permissions entity = (Permissions) ite.next();
				if (entity.getIsActived() == ActiveEnum.NO.getValue()
						|| entity.getIsDeleted() == DeleteEnum.YES.getValue()) {
					ite.remove();
				}

				List<Permissions> childList = entity.getChildren();
				if (childList != null && childList.size() > 0) {
					this.filterList(childList);
				}
			}

		}

		/**
		 * 权限过滤
		 * 
		 * @param permissionsList
		 * @param rolesList
		 * @return
		 */
		private List<Permissions> filterList(List<Permissions> permissionsList, List<Permissions> rolesList) {
			Map<Long, Object> rolesMap = new HashMap<>();
			Iterator<Permissions> var4 = rolesList.iterator();

			Permissions entity;
			while (var4.hasNext()) {
				entity = (Permissions) var4.next();
				rolesMap.put(entity.getId(), entity.getName());
			}

			var4 = permissionsList.iterator();

			while (true) {
				while (var4.hasNext()) {
					entity = (Permissions) var4.next();
					if (rolesMap.get(entity.getId()) != null) {
						entity.setChecked(true);
					}

					if (null != entity.getChildren() && !entity.getChildren().isEmpty()) {
						this.filterList(entity.getChildren(), rolesList);
					} else if (rolesMap.get(entity.getId()) != null) {
						entity.setChecked(true);
					}
				}

				return permissionsList;
			}
		}
	}
}
