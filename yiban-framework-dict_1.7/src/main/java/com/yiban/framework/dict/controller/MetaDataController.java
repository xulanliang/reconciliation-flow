package com.yiban.framework.dict.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.dict.service.MetaDataTypeService;

@Controller
@RequestMapping("/admin/dict/main")
public class MetaDataController extends FrameworkController {

	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private MetaDataTypeService metaDataTypeService;

	@GetMapping
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataTypeService.valueAsList())));
		return autoView("admin/dict/main");
	}

	@RestController
	@RequestMapping("/admin/dict")
	class DataController extends FrameworkController {
		// 分页查询
		@GetMapping
		public WebUiPage<MetaData> findMetaData(Integer dictType) {
			Sort sort = new Sort(new Order(Direction.DESC, "id"), new Order(Direction.ASC, "name"));
			Page<MetaData> metaData = metaDataService.findAll(dictType, getRequestPageabledWithInitSort(sort));
			return toWebUIPage(metaData);
		}

		// 根据某一个类型的字典
		@GetMapping("/typeValue")
		public List<MetaData> getMetaDatas(String typeValue, boolean isIncludeAll) {
			List<MetaData> list = metaDataService.findMetaDataByDataTypeValue(typeValue);
			if (isIncludeAll) {
				MetaData metaData = new MetaData();
				metaData.setId(0L);
				metaData.setName("全部");
				metaData.setValue("全部");
				list.add(0, metaData);
			}
			return list;
		}

		@Logable(operation = "新增字典数据")
		@PostMapping
		public ResponseResult save(@Valid MetaData data) {
			if (metaDataService.findMetaDataByName(data.getName()) != null&&metaDataService.findMetaDataByName(data.getName()).size()>0) {
				return ResponseResult.failure().message("字典名称已经存在");
			}
			// 同组下键唯一
			if (metaDataService.findMetaDataByValueAndTypeId(data.getValue(), data.getDictType().getId()) != null) {
				return ResponseResult.failure().message("该类型的字典数据已经存在");
			}
			try {
				metaDataService.save(data);
			} catch (Exception e) {
				return ResponseResult.failure("新增失败");
			}
			return ResponseResult.success("新增成功");
		}

		@Logable(operation = "修改字典数据")
		@PutMapping
		public ResponseResult update(@Valid MetaData data, @RequestParam("id") Long id) {
			MetaData metaData = metaDataService.findMetaDataById(data.getId());
			if (metaData == null) {
				return ResponseResult.failure().message("数据字典不存在");
			}
			List<MetaData> list = metaDataService.findMetaDataByName(data.getName());
			for(MetaData tempMetaData:list) {
				if (tempMetaData != null && tempMetaData.getId().longValue() != metaData.getId()&&tempMetaData.getDictType().equals(metaData.getDictType())) {
					return ResponseResult.failure().message("字典名称已经存在");
				}
				tempMetaData = metaDataService.findMetaDataByValueAndTypeId(data.getValue(), data.getDictType().getId());
				if (tempMetaData != null && tempMetaData.getId().longValue() != metaData.getId()) {
					return ResponseResult.failure().message("该类型的字典数据已经存在");
				}
			}
			metaData.setName(data.getName());
			metaData.setValue(data.getValue());
			metaData.setDescription(data.getDescription());
			metaData.setDictType(data.getDictType());
			metaData.setSort(data.getSort());
			metaData.setIsActived(data.getIsActived());
			try {

				metaDataService.save(metaData);
			} catch (Exception e) {
				return ResponseResult.failure("修改失败");
			}
			return ResponseResult.success("修改成功");
		}
	}
}
