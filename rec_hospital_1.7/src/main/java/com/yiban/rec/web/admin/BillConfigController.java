package com.yiban.rec.web.admin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.HospitalConfiguration;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.BillConfigVo;

@Controller
@RequestMapping(value = "/admin/billConfig")
public class BillConfigController extends CurrentUserContoller {

	
	@GetMapping
	public String index(ModelMap model) {
		return autoView("billconfig/typesBill");
	}
	
	
	@RestController
	@RequestMapping({"/admin/billConfig/data"})
	class NextDayAccountDataController extends BaseController {
		
		
		@PostMapping
		public ResponseResult save(BillConfigVo config) {
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
			//hospitalConfigService.batchSave(configList);
			return ResponseResult.success();
		}
	}
	
}
