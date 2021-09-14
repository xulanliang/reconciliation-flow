package com.yiban.framework.dict.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.dict.domain.MetaDataType;
import com.yiban.framework.dict.service.MetaDataTypeService;

@Controller
@RequestMapping("/admin/dictType/main")
public class MetaDataTypeController extends FrameworkController {

	@Autowired
	private MetaDataTypeService metaDataTypeService;

	@GetMapping
	public String main() {
		return autoView("admin/dictType/main");
	}

	@RestController
	@RequestMapping("/admin/dictType")
	class DataController extends FrameworkController {

		@GetMapping
		public WebUiPage<MetaDataType> page() {
			Sort sort = new Sort(new Order(Direction.DESC, "id"), new Order(Direction.ASC, "name"));
			Page<MetaDataType> page = metaDataTypeService.findAll(this.getSearchFilters(),
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(page);
		}

		@GetMapping("/combolist")
		public List<MetaDataType> list(Boolean placeholder) {
			List<MetaDataType> dataList = metaDataTypeService.findAll();
			return dataList;
		}

		@Logable(operation = "新增字典类型")
		@PostMapping
		public ResponseResult save(@Valid MetaDataType dataType) {
			MetaDataType metaDataTypeTemp = metaDataTypeService.findMetaDataTypeByName(dataType.getName());
			if (metaDataTypeTemp != null) {
				return ResponseResult.failure().message("字典类型已经存在");
			}
			try {
				metaDataTypeService.save(dataType);
			} catch (Exception e) {
				e.printStackTrace();
				ResponseResult.failure("新增失败");
			}
			return ResponseResult.success("新增成功");
		}

		@Logable(operation = "修改字典类型")
		@PutMapping
		public ResponseResult update(@Valid MetaDataType dataType) {
			MetaDataType metaDataType = metaDataTypeService.findMetaDataTypeById(dataType.getId());
			if (metaDataType == null) {
				ResponseResult.failure("字典类型不存在");
			}
			MetaDataType metaDataTypeTemp = metaDataTypeService.findMetaDataTypeByName(dataType.getName());
			if (metaDataTypeTemp != null && !metaDataTypeTemp.getId().equals(dataType.getId())) {
				return ResponseResult.failure().message("字典类型已经存在");
			}
			try {
				metaDataType.setName(dataType.getName());
				metaDataType.setValue(dataType.getValue());
				metaDataType.setDescription(dataType.getDescription());
				metaDataTypeService.update(metaDataType);
				return ResponseResult.success();
			} catch (Exception e) {
				e.printStackTrace();
				ResponseResult.failure("修改失败 ");
			}
			return ResponseResult.success("修改成功");
		}

		@Logable(operation = "删除字典类型")
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			try {
				metaDataTypeService.delete(id);
			} catch (Exception e) {
				e.printStackTrace();
				if(e instanceof DataIntegrityViolationException){
					return ResponseResult.failure("删除失败,请先删除该类型下的字典");
				}else{
					return ResponseResult.failure("删除失败,服务器错误");
				}
			}
			return ResponseResult.success("删除成功");
		}
	}

}
