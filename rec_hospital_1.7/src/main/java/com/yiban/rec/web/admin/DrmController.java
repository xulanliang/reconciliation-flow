package com.yiban.rec.web.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.service.DrmService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;

@Controller
@RequestMapping("/admin/drm")
public class DrmController extends CurrentUserContoller{
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private DrmService drmService;
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("dateTime", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag",CommonConstant.ALL_ID);
		return autoView("reconciliation/drm");
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<Map<String,Object>> index(@RequestParam(value = "bankTypeId", required = false) String bankTypeId
			,@RequestParam(value = "dataTime", required = false) String dataTime) throws BusinessException{
		PageRequest pagerequest = this.getRequestPageable();
		Page<Map<String,Object>> page = drmService.getDrmPageList(pagerequest, bankTypeId, dataTime);
		return toWebUIPage(page);
		
	}
	
	@Logable( operation = "??????????????????")
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView export(@RequestParam(value = "bankTypeId", required = false) String bankTypeId
			,@RequestParam(value = "dataTime", required = false) String dataTime,ModelMap model, HttpServletRequest request) throws BusinessException{
		List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
		ops.add(new ExcelDecoratedEntry("tradeDatatime", "????????????"));
		ops.add(new ExcelDecoratedEntry("orgName", "????????????"));
		ops.add(new ExcelDecoratedEntry("deviceNo", "????????????"));
		ops.add(new ExcelDecoratedEntry("deviceArea", "????????????"));
		ops.add(new ExcelDecoratedEntry("metaDataBankName", "????????????"));
		ops.add(new ExcelDecoratedEntry("payAmount", "????????????(???)","#.##"));
		ops.add(new ExcelDecoratedEntry("paySum", "????????????"));
		List<Map<String,Object>> list = drmService.getDrmList( bankTypeId, dataTime);
		ExcelResult viewExcel = new ExcelResult(list, ops);
		return new ModelAndView(viewExcel); 
		
	}

}
