package com.yiban.framework.account.controller.admin;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.UserService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.service.SessionUserService;


/**
 * 用户管理控制类
 *
 * @author tantian
 * @date   2018-01-23
 *
 */
@Controller
@RequestMapping("/admin/user/main")
public class UserController extends FrameworkController {

	@Autowired
	UserService userService;

	@Autowired
	AccountService accountService;

	@Autowired
	SessionUserService sessionUserService;
	@Autowired
	OrganizationService organizationService;

	@GetMapping
	public String main() {
		return autoView("admin/user/main");
	}


	@RestController
	@RequestMapping({"/admin/user"})
	class UserDataController extends BaseController {

		@Logable(operation="新增用户")
		@PostMapping
		public ResponseResult saveUser(@RequestParam(value = "orgId",required = false) Long orgId, @Valid User user) {

			User existUser = accountService.findValideUserByLoginName(user.getLoginName());
			if (existUser != null) {
				return ResponseResult.failure("登录名为[" + user.getLoginName() + "]已经存在,请重新输入.");
			} else if (!accountService.findValideUserByEmail(user.getEmail(), (Long)null).booleanValue()) {
				return ResponseResult.failure("email为[" + user.getEmail() + "]已经存在,请重新输入.");
			} else if (!accountService.findValideUserByPhone(user.getMobilePhone(), (Long)null).booleanValue()) {
				return ResponseResult.failure("手机号为[" + user.getMobilePhone() + "]已经存在,请重新输入.");
			} else {
				try {
					this.prepareUser(user);
					if (orgId != null && orgId.longValue() != 0L) {
						accountService.registerUserAndAddToOrganization(user, orgId);
					} else {
						accountService.registerUser(user);
					}
					return ResponseResult.success();
				} catch (Exception var6) {
					logger.error("saveUser", var6);
					return var6 instanceof DataIntegrityViolationException ? ResponseResult.failure("保存出错， 确认是否填入已经存在的登录名、邮箱或手机号?") : this.exceptionAsResult(var6);
				}
			}
		}

		private void prepareUser(User user) {
			if (com.google.common.base.Objects.equal(user.getEmail(), "")) {
				user.setEmail((String)null);
			}

			if (Objects.equal(user.getMobilePhone(), "")) {
				user.setMobilePhone((String)null);
			}

		}

		@Logable(operation="修改用户")
		@PutMapping
		public ResponseResult updateUser(@RequestParam(value = "orgId",required = false) Long orgId,@Valid User user) {
			if (!accountService.findValideUserByPhone(user.getMobilePhone(), (Long)user.getId()).booleanValue()) {
				return ResponseResult.failure("手机号为[" + user.getMobilePhone() + "]已经存在,请重新输入.");
			}if (!accountService.findValideUserByEmail(user.getEmail(), (Long)user.getId()).booleanValue()) {
				return ResponseResult.failure("email为[" + user.getEmail() + "]已经存在,请重新输入.");
			} else {
				User entity = accountService.findUserById(user.getId());
				if (entity == null) {
					return ResponseResult.failure("用户不存在");
				} else {
					User existUser = accountService.findValideUserByLoginName(user.getLoginName());
					if (existUser != null && !entity.getLoginName().equals(user.getLoginName())) {
						return ResponseResult.failure("登录名为[" + user.getLoginName() + "]已经存在,请重新输入.");
					} else {
						try {
							this.prepareUser(user);
							entity.setEmail(user.getEmail());
							entity.setGender(user.getGender());
							entity.setLoginName(user.getLoginName());
							entity.setMobilePhone(user.getMobilePhone());
							entity.setName(user.getName());
							entity.setRemark(user.getRemark());
							entity.setPosition(user.getPosition());
							entity.setBirthday(user.getBirthday());
							accountService.updateUserAndChangeOrganization(entity, orgId);
							return ResponseResult.success();
						} catch (Exception e) {
							logger.error("updateUser", e.getMessage());
							return e instanceof DataIntegrityViolationException ? 
									ResponseResult.failure("保存出错， 确认是否填入已经存在的登录名、邮箱或手机号?") : ResponseResult.failure("更新有误").debugMessage(e.getMessage());
						}
					}
				}
			}
		
		}

		@Logable(operation="修改状态")
		@PutMapping("/{id}/status")
		public ResponseResult changeUserStatus(@PathVariable Long id) {
			if (accountService.isSupervisor(id)) {
				return ResponseResult.failure("不能修改超级管理员的状态");
			} else {
				try {
					accountService.updateStatus(id);
					return ResponseResult.success();
				} catch (Exception e) {
					logger.error("changeUserStatus", e);
					return this.exceptionAsResult(e);
				}
			}
		}
		
		
		@Logable(operation="删除用户")
		@DeleteMapping("/{id}")
		public ResponseResult deleteUser(@PathVariable Long id) {
			User user = accountService.findUserById(id);
			if (user == null) {
				return ResponseResult.failure("用户不存在");
			}
			try {
				accountService.deleteUser(id);
				return ResponseResult.success();
			} catch (Exception var4) {
				logger.error("deleteUser", var4);
				return ResponseResult.failure("删除有误").debugMessage(var4.toString());
			}
			
		}
		
		/**
		 * 编辑用户角色
		 */
		@PostMapping("/{id}/role")
		public ResponseResult editRole(@RequestParam("userId") Long userId, @RequestParam("roleIds") String roleIds) {
			try {
				//删除用户所有角色再重新赋值
				int num=accountService.editUserRole(userId,roleIds);
				if(num==1) {
					return ResponseResult.success();
				}
			} catch (Exception e) {
				e.printStackTrace();
				ResponseResult.failure("角色添加异常");
			}
			return ResponseResult.failure().message("角色添加失败");
		}
		

		@Logable(operation="重置密码")
		@PutMapping({"/{id}/password"})
		public ResponseResult changePassword(@RequestParam("id") Long id, @RequestParam("newPassword") String newPassword) {
			if (Strings.isNullOrEmpty(newPassword)) {
				newPassword="123456";
			} else if (accountService.isSupervisor(id) && !accountService.isSupervisor((Long)sessionUserService.getCurrentSessionUser().getId())) {
				return ResponseResult.failure("只有超级管理员能修改自己的密码");
			} else {
				try {
					accountService.changeUserPassword(id, newPassword);
					return ResponseResult.success();
				} catch (Exception var4) {
					logger.error("changePassword", var4);
					return this.exceptionAsResult(var4);
				}
			}
			return null;
		}

	
		@GetMapping
		public WebUiPage<User> users(@RequestParam(value = "orgId",required = false) Long orgId,UserService.userQuery query) {
			PageRequest pageable = this.getRequestPageabledWithInitSort(this.getIdDescSort());
			Page<User> data;

			if (orgId == null) {
				data = accountService.findAllUsers(this.getSearchFilters(), pageable,query.getName());
			} else if (orgId.longValue() == 0L) {
				data = accountService.findAllUsersNotInOrganizations(pageable);
			} else {
				data = accountService.findUsersByOrganization(orgId, pageable);
			}

			return this.toWebUIPage(data);
		}

		//获取用户角色
		@GetMapping("/{id}/role")
		public Iterable<Role> userRoles(@RequestParam("id") Long id) {
			Iterable<Role> data = accountService.getUserRoles(id);
			return data;

		}

		@GetMapping("/roles")
		public List<Role> rolesForSelect() {
			List<Role> data = accountService.findAllRoles(this.getSearchFilters());
			return data;

		}

		//获取用户所属机构
		@GetMapping("/{id}/org")
		public ResponseResult userOrg(@RequestParam("id") Long id) {
			return ResponseResult.success().data(accountService.getUserOrganizationId(id));
		}
	}
}

