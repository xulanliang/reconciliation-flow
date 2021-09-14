package com.yiban.framework.account.controller.admin;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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

import com.google.common.base.Objects;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;

/**
 * @author swing
 * @date 2018年2月8日 上午10:22:14 类说明 机构管理
 */
@Controller
@RequestMapping("/admin/organization/main")
public class OrganizationController extends FrameworkController {

	@GetMapping
	public String main() {
		return autoView("admin/organization/main");
	}

	@RestController
	@RequestMapping("/admin/organization")
	class DataController extends FrameworkController {
		@Autowired
		private OrganizationService organizationService;
		
		/**
		 * 构造字典数据
		 * @return
		 * List<Map<String,String>>
		 */
		@GetMapping("data")
        public List<Map<String,Object>> findAllCodeAndName() {
		    return organizationService.findAllCodeAndName();
		}
		
		/**
		 * 构造字典数据
		 * @return
		 * List<Map<String,String>>
		 */
		@GetMapping("self/data")
		public List<Map<String,Object>> findAllData() {
		    return organizationService.findAllCodeAndName();
		}
		
		@GetMapping
		public List<Organization> list(String name) {
		    if(StringUtils.isBlank(name)) {
		        return	organizationService.findAllOrganizations();
		    }
			return organizationService.findByNameLike(name);
		}

		// 新增
		@Logable(operation = "新增组织结构")
		@PostMapping
		public ResponseResult save(@Valid Organization organization) {
			Organization dbOrg = organizationService.findByName(organization.getName());
			if (dbOrg != null) {
				return ResponseResult.failure().message("组织结构已经存在");
			}
			if (StringUtils.isNotEmpty(organization.getCode())) {
				Organization temOrg = organizationService.findByCode(organization.getCode());
				if (temOrg != null) {
					return ResponseResult.failure("机构编码不能重复");
				}
			}
			try {

				organizationService.createOrganization(organization);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseResult.failure("新增失败");
			}
			return ResponseResult.success("新增成功");
		}

		// 修改
		@Logable(operation = "修改组织结构")
		@PutMapping
		public ResponseResult update(@RequestParam("id") Long id, @Valid Organization organization) {
			Organization dbOrganization = organizationService.findOrganizationById(id);
			if (dbOrganization == null) {
				return ResponseResult.failure().message("组织结构不存在");
			}
			if (StringUtils.isNotEmpty(organization.getCode())) {
				Organization temOrg = organizationService.findByCode(organization.getCode());
				if (temOrg != null && !temOrg.getCode().equals(dbOrganization.getCode())) {
					return ResponseResult.failure().message("机构编码已经存在,请现有新的编码");
				}
			}
			Organization tempOrganization = organizationService.findByName(organization.getName());
			if (tempOrganization != null
					&& tempOrganization.getId().longValue() != dbOrganization.getId().longValue()) {
				return ResponseResult.failure().message("组织机构已存在,请使用新的名称");
			}

			Organization parentOrg = organization.getParent();
			if (parentOrg != null) {
				if (Objects.equal(dbOrganization.getId(), parentOrg.getId())) {
					return ResponseResult.failure("上级组织机构不能是自身");
				}
				if (parentOrg.getParent() != null
						&& parentOrg.getParent().getId() == dbOrganization.getId().longValue()) {
					return ResponseResult.failure("上级组织机构的父机构不能为自身");
				}
			}

			dbOrganization.setParent(organization.getParent());
			dbOrganization.setName(organization.getName());
			dbOrganization.setContactPhone(organization.getContactPhone());
			dbOrganization.setContactUser(organization.getContactUser());
			dbOrganization.setDescription(organization.getDescription());
			if(StringUtils.isNotEmpty(organization.getCode())){
				dbOrganization.setCode(organization.getCode());
			}
			try {
				organizationService.updateOrganization(dbOrganization);
			} catch (Exception e) {
				return ResponseResult.failure().message("修改失败");
			}
			return ResponseResult.success("修改成功");
		}

		@Logable(operation = "删除组织结构")
		// 删除
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			Organization org = organizationService.findOrganizationById(id);
			if (org == null) {
				return ResponseResult.failure("组织机构不存在");
			}

			if (org.getChildren() != null && org.getChildren().size() > 0) {
				return ResponseResult.failure("请先删除子机构");
			}
			try {
				organizationService.deleteOrganization(id);
			} catch (Exception e) {
				return ResponseResult.failure("删除失败");
			}
			return ResponseResult.success("删除成功");
		}
	}
	
	
	
}
