package com.yiban.rec.web.task;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.rec.domain.task.ChannelScheduleInfo;
import com.yiban.rec.service.ChannelScheduleInfoService;
import com.yiban.rec.util.StringUtil;

/**
 * @ClassName: ChannelScheduleInfoController
 * @Description: 获取渠道数据任务
 * @author tuchun@clearofchina.com
 * @date 2017年3月28日 下午4:59:51
 * @version V1.0
 * 
 */
@Controller
@RequestMapping("/admin/channelScheduleInfo")
public class ChannelScheduleInfoController extends FrameworkController {

	@Autowired
	private ChannelScheduleInfoService channelScheduleInfoService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(ModelMap mode) {
		return autoView("task/channelScheduleInfo");
	}
	@RestController
	@RequestMapping("/admin/channelScheduleInfo/data")
	class DataController extends FrameworkController {
		@GetMapping
		@ResponseBody
		public WebUiPage<Map<String, Object>> pageList(@RequestParam(value = "orgId", required = false) Long orgId)throws BusinessException {
			OprPageRequest pagerequest = super.URL2PageRequest();
			Page<Map<String, Object>> data = channelScheduleInfoService.getChannelScheduleInfoList(pagerequest, orgId);
			return toWebUIPage(data);
		}

		@Logable(operation = "新增院内定时任务")
		@PostMapping
		public ResponseResult save(@Valid ChannelScheduleInfo channelScheduleInfo)throws BusinessException {
			if (!StringUtil.isNullOrEmpty(channelScheduleInfo.getJobName())) {
				ChannelScheduleInfo ChannelScheduleInfoOld = channelScheduleInfoService.getChannelScheduleInfoByName(channelScheduleInfo.getJobName());
				if (ChannelScheduleInfoOld != null) {
					return ResponseResult.failure("院内定时任务已经存在");
				}
			}
			return channelScheduleInfoService.save(channelScheduleInfo);
		}

		@Logable(operation = "修改院内定时任务")
		@PutMapping
		public ResponseResult update(@Valid ChannelScheduleInfo ChannelScheduleInfo)throws BusinessException {
			return channelScheduleInfoService.update(ChannelScheduleInfo);
		}

		@Logable(operation = "状态修改")
		@PutMapping("/{id}/status/{status}")
		@ResponseBody
		public ResponseResult update(@PathVariable Long id,@PathVariable Integer status) throws BusinessException {
			return channelScheduleInfoService.updateStatus(status, id);
		}

		@Logable(operation = "删除院内定时任务")
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			return channelScheduleInfoService.delete(id);
		}
	}
}
