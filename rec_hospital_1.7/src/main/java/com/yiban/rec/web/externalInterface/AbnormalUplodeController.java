package com.yiban.rec.web.externalInterface;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.rec.domain.OrderAbnormalUplode;
import com.yiban.rec.domain.vo.OrderAbnormalUplodeVo;
import com.yiban.rec.service.OrderAbnormalUplodeService;
import com.yiban.rec.util.DateUtil;

@RestController
@RequestMapping("/abnormal")
public class AbnormalUplodeController extends FrameworkController {

	@Autowired
	private OrderAbnormalUplodeService orderAbnormalUplodeService;
	
	/**
	 * 异常数据保存
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "uplode", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	@ResponseBody
	public Map<String, Object> payInit(@Valid @RequestBody OrderAbnormalUplodeVo vo,BindingResult bindingResult) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("resultCode", "SUCCESS");
		map.put("resultMsg", "成功");
		if(bindingResult.hasErrors()) {
			map.put("resultCode", "FAIL");
			map.put("resultMsg", "请求入参非法:" 
		            + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return map;
        }
		if(!DateUtil.isDate(vo.getTradeDateTime())){
			map.put("resultCode", "FAIL");
			map.put("resultMsg", "请求入参开始时间格式不合法，格式应为：yyyy-MM-dd HH:ss:mm");
            return map;
		}
		try {
			OrderAbnormalUplode abnormal=new OrderAbnormalUplode();
			BeanUtils.copyProperties(vo,abnormal);
			abnormal.setCreateTime(new Date());
			orderAbnormalUplodeService.save(abnormal);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "数据保存失败";
			map.put("resultCode", "FAIL");
			map.put("resultMsg", msg + e.getMessage());
		}
		return map;
	}
}
