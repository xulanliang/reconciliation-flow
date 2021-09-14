package com.yiban.rec.web.baseinfo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.util.ErrorValidate;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.rec.domain.baseinfo.ShopInfo;
import com.yiban.rec.service.ShopInfoService;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.StringUtil;


/**
 * @ClassName: ShopInfoController
 * @Description: 商户信息表
 * @author tuchun@clearofchina.com
 * @date 2017-03-22 下午4:59:51
 * @version V1.0
 * 
 */
@Controller
@RequestMapping("/admin/shopInfo")
public class ShopInfoController extends CurrentUserContoller {

	@Autowired
	private ShopInfoService shopInfoService;
	@Autowired
	private OrganizationService organizationService;
	

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(ModelMap mode) {
		mode.put("orgMap", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(organizationService.getOrgMap())));
		return autoView("baseinfo/shopInfo");
	}
	@RestController
	@RequestMapping({"/admin/shopInfo/data"})
	class ShopInfoDataController extends BaseController {
		@GetMapping
		public WebUiPage<Map<String, Object>> pageList(@RequestParam(value = "orgNo", required = false) String orgNo) throws BusinessException {
			PageRequest pagerequest = this.getRequestPageable();
			Page<Map<String,Object>> data = shopInfoService.getShopInfoList(pagerequest,orgNo);
			return toWebUIPage(data);
		}
		
		/*@RequestMapping(value = "/getShopInfo", method = RequestMethod.POST)
		@ResponseBody
		public ShopInfo getShopInfo(@RequestParam(value = "id", required = true) Long id) {
			ShopInfo shopInfo = shopInfoService.getShopInfoById(id);
			return shopInfo;
		}*/
		@GetMapping("/{id}/org")
		public String getShopOrgId(String code) {
			Organization org = organizationService.findByCode(code);
			if(null != org) {
				return org.getId().toString();
			}
			return null;
		}
		
		/**
		* @date：2017年10月31日 
		* @Description：用户导入账单明细时选择商户信息
		* @return: 返回结果描述
		* @return List<ShopInfo>: 返回值类型
		* @throws
		 */
		@GetMapping("/shopInfoList")
		public List<ShopInfo> getShopInfoList(@RequestParam(value = "payName", required = false) String payName) {
			String hospitalId = Configure.getPropertyBykey("yiban.projectid");
			List<ShopInfo> shopInfoList = shopInfoService.getShopInfoByOrgNo(hospitalId,payName);
			return shopInfoList;
		}
		
		@Logable( operation = "新增商户信息")
		@PutMapping("/save")
		public ResponseResult save(ShopInfo shopInfo, BindingResult result) throws BusinessException {
			if (result.hasErrors()) {
				String message = ErrorValidate.convertErrorMessage(result);
				return ResponseResult.failure(message).debugMessage(result.toString());
			}
			if(!StringUtil.isNullOrEmpty(shopInfo.getId())){
				ShopInfo shopInfoOld=shopInfoService.getShopInfoById(shopInfo.getId());
				if(shopInfoOld!=null){
					return ResponseResult.failure("商户信息已经存在");
				}
			}
			String orgNo=shopInfo.getOrgNo();
			String applyId=shopInfo.getApplyId();
			String payShopNo=shopInfo.getPayShopNo();
			ShopInfo shopInfoDb=shopInfoService.findShopInfoByOrgNoAndApplyIdAndPayShopNo(orgNo, applyId, payShopNo);
			if(shopInfoDb!=null){
				return ResponseResult.failure("机构名称、应用ID、商户号三者必须唯一");
			}
			ResponseResult res = shopInfoService.save(shopInfo);
			return res;
		}

		@Logable(operation = "修改商户信息")
		@PutMapping("/update")
		public ResponseResult update(ShopInfo shopInfo, BindingResult result) throws BusinessException {
			if (result.hasErrors()) {
				String message = ErrorValidate.convertErrorMessage(result);
				return ResponseResult.failure(message).debugMessage(result.toString());
			}
			return shopInfoService.update(shopInfo);
		}

		@Logable( operation = "删除商户信息")
		@DeleteMapping("/{id}")
		public ResponseResult delete(@RequestParam(value = "id", required = true) Long id)
				throws BusinessException {
			return shopInfoService.delete(id);
		}
	}
}
