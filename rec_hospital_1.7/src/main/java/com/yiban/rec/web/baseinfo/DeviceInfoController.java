package com.yiban.rec.web.baseinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.util.ErrorValidate;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.rec.domain.baseinfo.DeviceInfo;
import com.yiban.rec.service.impl.DeviceInfoService;
import com.yiban.rec.util.StringUtil;

@Controller
@RequestMapping("/admin/deviceInfo/main")
public class DeviceInfoController extends FrameworkController {

	@Autowired
	private DeviceInfoService deviceInfoService;
	@Autowired
	private OrganizationService organizationService;
	

	@GetMapping
	public String main() {
		return autoView("baseinfo/deviceInfo");
	}
	
	@RestController
	@RequestMapping("/admin/deviceInfo")
	class DataController extends FrameworkController {
		
		@GetMapping("/pageList")
		public WebUiPage<Map<String, Object>> pageList(@RequestParam(value = "deviceNo", required = false) String deviceNo,@RequestParam(value = "orgNo", required = false) String orgNo)
				throws BusinessException {
			PageRequest pagerequest = this.getRequestPageable();
			Page<Map<String, Object>> data = deviceInfoService.getDeviceInfoList(pagerequest, deviceNo,orgNo);
			return toWebUIPage(data);
		}
		
		@GetMapping("/deviceInfos")
		@ResponseBody
		public List<DeviceInfo> deviceInfos(String orgNo) throws BusinessException {
			return deviceInfoService.findByOrgNo(orgNo);
		}
	
		@GetMapping("/getDeviceInfo")
		public DeviceInfo getDeviceInfo(@RequestParam(value = "id", required = true) Long id) {
			DeviceInfo deviceInfo = deviceInfoService.getDeviceInfoById(id);
			return deviceInfo;
		}
		
		@GetMapping("/getDeviceInfos")
		public List<DeviceInfo> getDeviceInfos(boolean isIncludeAll) {
			List<DeviceInfo> deviceInfos = deviceInfoService.findDeviceInfo();
//			if(isIncludeAll){
//				DeviceInfo dv = new DeviceInfo();
//				dv.setDeviceNo("全部");
//				deviceInfos.add(0, dv);
//			}
			return deviceInfos;
		}
		@GetMapping("/{id}/org")
		public String getShopOrgId(String code) {
			Organization org = organizationService.findByCode(code);
			if(null != org) {
				return org.getId().toString();
			}
			return null;
		}
		
		@RequestMapping(value = "/{id}/deviceInfo", method = RequestMethod.GET)
		@ResponseBody
		public List<DeviceInfo> deviceInfo(String orgNo) {
			List<DeviceInfo> deviceInfos = deviceInfoService.findDeviceInfo();
			List<DeviceInfo> list=new ArrayList<DeviceInfo>();
			for(DeviceInfo v:deviceInfos) {
				if(v.getOrgNo().equals(orgNo)) {
					list.add(v);
				}
			}
			return list;
		}
		
		@Logable(operation = "新增设备信息")
		@PostMapping
		public ResponseResult save(@Valid DeviceInfo deviceInfo, BindingResult result) {
			try{
				if (result.hasErrors()) {
					String message = ErrorValidate.convertErrorMessage(result);
					return ResponseResult.failure(message).debugMessage(result.toString());
				}
				if (!StringUtil.isNullOrEmpty(deviceInfo.getId())) {
					DeviceInfo DeviceInfoOld = deviceInfoService.getDeviceInfoById(deviceInfo.getId());
					if (DeviceInfoOld != null) {
						return ResponseResult.failure("设备信息已经存在");
					}
				}
				DeviceInfo deviceInfoDb=deviceInfoService.findDeviceInfoByDeviceNo(deviceInfo.getDeviceNo());
				if (deviceInfoDb != null) {
					return ResponseResult.failure("设备编码必须唯一");
				}
				deviceInfoService.save(deviceInfo);
			    return ResponseResult.success();
			}catch(Exception e){
				e.printStackTrace();
				return ResponseResult.failure();
			}
		}
	
		@Logable(operation = "修改设备信息")
		@PutMapping
		public ResponseResult update(@Valid DeviceInfo deviceInfo, BindingResult result) throws BusinessException {
			if (result.hasErrors()) {
				String message = ErrorValidate.convertErrorMessage(result);
				return ResponseResult.failure(message).debugMessage(result.toString());
			}
			DeviceInfo deviceInfoDb = deviceInfoService.findDeviceInfoByDeviceNo(deviceInfo.getDeviceNo());
			if (deviceInfoDb != null && !deviceInfo.getId().toString().equals(deviceInfoDb.getId().toString())) {
				return ResponseResult.failure("设备编码必须唯一");
			}
			return deviceInfoService.update(deviceInfo);
		}
	
		@Logable(operation = "删除设备信息")
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			DeviceInfo p = deviceInfoService.getDeviceInfoById(id);
			if (p == null) {
				return ResponseResult.failure("此设备信息不存在");
			}
			try {
				deviceInfoService.delete(id);
				return ResponseResult.success();
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return ResponseResult.failure("删除失败");
			}
		}
	}
}
