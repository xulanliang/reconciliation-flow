package com.yiban.rec.web.admin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.HospitalConfiguration;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.util.FieldMetaUtil;

/**
 * 医院配置信息
 * 
 * @author huangguojie
    modify by swing 2018-07-23
 */
@Controller
@RequestMapping("/admin/hospitalConfig")
public class HospitalConfigController extends CurrentUserContoller {

	@Autowired
	private HospitalConfigService hospitalConfigService;
	private final Gson gson = new Gson();

	@GetMapping
	public String index(ModelMap model) {
		AppRuntimeConfig webConfig = hospitalConfigService.loadConfig();
		List<Map<String, Object>> configMetaList = FieldMetaUtil.getConfigMeta();
		model.addAttribute("metaList", configMetaList);
		model.addAttribute("webConfig", gson.toJson(webConfig));
		return autoView("hospitalconfig/hospitalconifg");
	}

	@Logable(operation = "更新医院配置信息")
	@PostMapping
	@ResponseBody
	public ResponseResult update(AppRuntimeConfig config) {
		Class<AppRuntimeConfig> c = AppRuntimeConfig.class;
		Field[] fieldArr = c.getDeclaredFields();
		List<HospitalConfiguration> configList = new ArrayList<>(fieldArr.length);
		for (Field f : fieldArr) {
			f.setAccessible(true);
			Object objVal = null;
			try {
				objVal = f.get(config);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseResult.failure("更新失败");
			}
			HospitalConfiguration dbConfig = new HospitalConfiguration();
			dbConfig.setKeyWord(f.getName());
			if (!StringUtils.isEmpty(objVal)) {
				dbConfig.setKeyValue(objVal.toString());
			}
			configList.add(dbConfig);
		}

		hospitalConfigService.batchSave(configList);
		return ResponseResult.success();
	}

}
