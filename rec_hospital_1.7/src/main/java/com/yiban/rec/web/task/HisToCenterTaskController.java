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
import com.yiban.rec.domain.task.HisToCenterTaskInfo;
import com.yiban.rec.service.HisToCenterTaskService;
import com.yiban.rec.util.StringUtil;

/**
 * @ClassName: HisToCenterTaskController
 * @Description: His到中心任务调度
 * @author tuchun@clearofchina.com
 * @date 2017-03-22 下午4:59:51
 * @version V1.0
 * modiby swing @ 2018-04-11 下午15:40:51
 */
@Controller
@RequestMapping("/admin/hisToCenterTask")
public class HisToCenterTaskController extends FrameworkController {
	@Autowired
	private HisToCenterTaskService hisToCenterTaskService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(ModelMap mode) {
		return autoView("task/hisToCenterTaskInfo");
	}

	@RestController
	@RequestMapping("/admin/hisToCenterTask/data")
	class DataController extends FrameworkController {
		@GetMapping
		@ResponseBody
		public WebUiPage<Map<String, Object>> pageList(@RequestParam(value = "orgId", required = false) Long orgId)throws BusinessException {
			OprPageRequest pagerequest = super.URL2PageRequest();
			Page<Map<String, Object>> data = hisToCenterTaskService.getHisToCenterTaskInfoList(pagerequest, orgId);
			return toWebUIPage(data);
		}

		@Logable(operation = "新增his到中心定时任务")
		@PostMapping
		public ResponseResult save(@Valid HisToCenterTaskInfo hisToCenterTask) {
			if (!StringUtil.isNullOrEmpty(hisToCenterTask.getJobname())) {
				HisToCenterTaskInfo hisToCenterTaskOld = hisToCenterTaskService.getHisToCenterTaskInfoByName(hisToCenterTask.getJobname());
				if (hisToCenterTaskOld != null) {
					return ResponseResult.failure("his到中心定时任务已经存在");
				}
			}
			return hisToCenterTaskService.save(hisToCenterTask);
		}

		@Logable(operation = "修改his到中心定时任务")
		@PutMapping
		public ResponseResult update(@Valid HisToCenterTaskInfo hisToCenterTask) {
			HisToCenterTaskInfo info = hisToCenterTaskService.getHisToCenterTaskInfoById(hisToCenterTask.getId());
			if(info == null){
				return ResponseResult.failure("该任务已经不存在");
			}
			return hisToCenterTaskService.update(hisToCenterTask);
		}

		@Logable(operation = "修改his到中心定时任务状态")
		@PutMapping("/{id}/status/{status}")
		@ResponseBody
		public ResponseResult update(@PathVariable Long id,@PathVariable Integer status) throws BusinessException {
			return hisToCenterTaskService.updateStatus(status, id);
		}

		@Logable(operation = "删除his到中心定时任务")
		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			return hisToCenterTaskService.delete(id);
		}
	}
}
