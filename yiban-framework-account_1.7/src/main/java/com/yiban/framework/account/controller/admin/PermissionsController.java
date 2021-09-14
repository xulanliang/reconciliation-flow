package com.yiban.framework.account.controller.admin;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.yiban.framework.account.domain.Permissions;
import com.yiban.framework.account.domain.PrivilegeMethod;
import com.yiban.framework.account.service.PermissionsService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.util.YiBanEntityUtils;

@Controller
@RequestMapping("/admin/permission/main")
public class PermissionsController extends FrameworkController {
	@Autowired
	private PermissionsService permissionsService;

	@GetMapping
	public String main() {
		return autoView("admin/permission/main");
	}

	@RestController
	@RequestMapping("/admin/permission")
	class DataController extends FrameworkController {
		@GetMapping
		public List<Permissions> list() {
			List<Permissions> list  = permissionsService.findAll(getSearchFilters());
			YiBanEntityUtils.filterChild(list);
			return list;
		}
		

		// 新增
		@Logable(operation = "新增权限")
		@PostMapping
		public ResponseResult save(@Valid Permissions permissions) {
			preMethods(permissions);
            if(permissionsService.findByPermissionName(permissions.getName()) != null){
            	return ResponseResult.failure("权限名称已经存在");
            }
            try{
            	permissionsService.saveOrUpdate(permissions);
            }catch(Exception e){
            	 e.printStackTrace();
            	 return ResponseResult.failure("新增失败");
            }
            return ResponseResult.success("新增成功");
		}

		// 修改
		@Logable(operation = "修改权限")
		@PutMapping
		public ResponseResult update(@RequestParam("id") Long id,@Valid Permissions permissions) {
			Permissions entity = permissionsService.findById(id);
			if (entity == null) {
				logger.info("权限不存在");
				return ResponseResult.failure("权限不存在");
			}
			if (entity.isReadOnly()) {
				    logger.info("只读的权限不能通过web端进行修改！");
		            return ResponseResult.failure("只读的权限不能通过web端进行修改！");
		    }
			Permissions temPermission =permissionsService.findByPermissionName(permissions.getName());
			if( temPermission!= null && temPermission.getId().longValue() != permissions.getId() .longValue()){
				return ResponseResult.failure("权限已经存在");
			}
			preMethods(permissions);
			Permissions parentPermission=permissions.getParent();
			if (parentPermission!= null) {
				if(entity.getId().longValue() == permissions.getParent().getId().longValue()){
					logger.info("上级权限不能是自身");
					return ResponseResult.failure("上级权限不能是自身");
				}
				if(parentPermission.getParent() !=null && parentPermission.getParent().getId() == entity.getId().longValue()){
					return ResponseResult.failure("上级权限的父权限不能为自身");
				}
			}
			
			entity.setName(permissions.getName());
			entity.setType(permissions.getType());
			entity.setTarget(permissions.getTarget());
			entity.setMethod(permissions.getMethod());
			entity.setParent(permissions.getParent());
			entity.setSort(permissions.getSort());
			try {
				permissionsService.saveOrUpdate(entity);
			} catch (Exception e) {
				logger.error("update Privilege", e);
				return ResponseResult.failure("修改失败");
			}
			return ResponseResult.success("修改成功 ");
		}

		@Logable(operation = "删除权限")
		// 删除
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			Permissions p = permissionsService.findById(id);
			if (p == null) {
				return ResponseResult.failure("此配置已不存在");
			}
			if(p.getChildren() != null && p.getChildren().size() >0){
				return ResponseResult.failure("请先删除子权限");
			}
			try {
				permissionsService.delete(id);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return ResponseResult.failure("权限已经被角色授权,无法删除");
			}
			return ResponseResult.success("删除成功");
		}

		private void preMethods(Permissions permissions) {
			if (!Strings.isNullOrEmpty(permissions.getMethod())&& permissions.getMethod().contains(PrivilegeMethod.all.toString())) {
				permissions.setMethod(PrivilegeMethod.all.toString());
			}
		}
	}
}
