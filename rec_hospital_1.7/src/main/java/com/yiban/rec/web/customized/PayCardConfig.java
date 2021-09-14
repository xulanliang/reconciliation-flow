package com.yiban.rec.web.customized;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.rec.bill.parse.util.HttpClientUtil;

@Controller
@RequestMapping("/admin/payCardConfig")
public class PayCardConfig extends CurrentUserContoller {
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(ModelMap mode)  {
		return autoView("customized/payCard");
	}
	
	@RestController
	@RequestMapping({"/admin/payCardConfig/data"})
	class PayCardConfigDataController extends BaseController {
		
		
		@PostMapping("/updateProjectStatus")
		public String updatePayCardStatus(String type) {
			String res ="";
			try {
				String url = propertiesConfigService.findValueByPkey(ProConstants.cardUrl, ProConstants.DEFAULT.get(ProConstants.cardUrl));
				res = HttpClientUtil.doPost(url +"/updateProjectStatus?type="+type);
			} catch (Exception e) {
				e.printStackTrace();
				res=e.getMessage();
			}
			return res;
		}
		
		@GetMapping("/projectStatus")
		public String payCardStatus() {
			String res ="";
			try {
				String url = propertiesConfigService.findValueByPkey(ProConstants.cardUrl, ProConstants.DEFAULT.get(ProConstants.cardUrl));
				res = HttpClientUtil.doGet(url+"/projectStatus");
			} catch (Exception e) {
				e.printStackTrace();
				res=e.getMessage();
			}
			return res;
		}
	}
	
	
}
