package com.yiban.rec.web.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.Platformflow;
import com.yiban.rec.domain.vo.CashQueryVo;
import com.yiban.rec.domain.vo.TradeDetailQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;

/**
 * 
 * <p>
 * 文件名称:YyLogController.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:对账管理--->银医日志交易流水查询
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2017年3月21日下午2:40:16
 * </p>
 * <p>
 * 
 * @author fangzuxing
 */
@Controller
@RequestMapping("/admin/yylog")
public class YyLogController extends CurrentUserContoller {
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	
	@Autowired
	private ReconciliationService reconciliationService;
	
	@GetMapping
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("org", EnumType.DATA_SOURCE_TYPE_ORG_CHANNEL.getValue());
		model.put("accountStartDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("accountEndDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag",CommonConstant.ALL_ID);
		return autoView("reconciliation/yylog");
	}
	
	@RestController
	@RequestMapping("/admin/yylog/data")
	class DataControllder extends FrameworkController{
		@InitBinder
	    public void intDate(WebDataBinder dataBinder){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    }
		@GetMapping
		public WebUiPage<Platformflow> recPlattradeQuery(TradeDetailQueryVo cqvo) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			User user = currentUser();
			if(StringUtils.isNotBlank(cqvo.getStartDate())) {
				cqvo.setStartDate(cqvo.getStartDate() + " 00:00:00");
			} else if(null == cqvo.getStartDate()) {
				String accountStartDate = DateUtil.getSpecifiedDayBefore(new Date()) + " 00:00:00";
				cqvo.setStartDate(accountStartDate);
			}
			if(StringUtils.isNotBlank(cqvo.getEndDate())) {
				cqvo.setEndDate(cqvo.getEndDate() + " 23:59:59");
			} else if(null == cqvo.getEndDate()) {
				String accountEndDate = DateUtil.getSpecifiedDayBefore(new Date()) + " 23:59:59";
				cqvo.setEndDate(accountEndDate);
			}
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Page<Platformflow> platPage = reconciliationService.getPlatPage(cqvo,orgListTemp,this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}      
		
		@Logable(operation = "导出交易明细")
		@GetMapping("/export")
		public ModelAndView toDcExcel(CashQueryVo cqvo,ModelMap model, HttpServletRequest request){ 
			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
			User user = currentUser();
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			ops.add(new ExcelDecoratedEntry("tradeDatatime", "账单日期"));
			ops.add(new ExcelDecoratedEntry("orgName", "机构名称"));
			ops.add(new ExcelDecoratedEntry("tradeCodeName", "交易类型"));
			ops.add(new ExcelDecoratedEntry("payBusinessTypeName", "业务类型"));
			ops.add(new ExcelDecoratedEntry("payTypeName", "支付类型"));
			ops.add(new ExcelDecoratedEntry("paySourceName", "支付来源"));
			ops.add(new ExcelDecoratedEntry("paySerNo", "支付终端号"));
			ops.add(new ExcelDecoratedEntry("payTermNo", "设备编码"));
			ops.add(new ExcelDecoratedEntry("custName", "客户名称"));
			ops.add(new ExcelDecoratedEntry("custIdentifyTypeName", "客户标识"));
			ops.add(new ExcelDecoratedEntry("businessFlowNo", "his交易流水号"));
			ops.add(new ExcelDecoratedEntry("payAccount", "支付账号"));
			ops.add(new ExcelDecoratedEntry("payAmount", "金额(单位：元)"));
			ops.add(new ExcelDecoratedEntry("payBatchNo", "支付批次号"));
			ops.add(new ExcelDecoratedEntry("payFlowNo", "支付流水号"));
			ops.add(new ExcelDecoratedEntry("tradeToName", "交易目的"));
			ops.add(new ExcelDecoratedEntry("orderStateName", "订单状态"));
			ExcelResult viewExcel = null;
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
			List<Platformflow> hisList = reconciliationService.getRecCashPlatList(cqvo,orgListTemp,page);
			viewExcel = new ExcelResult(hisList, ops);
			viewExcel.setFileName(DateUtil.getCurrentDateString()+"支付通道明细");
			return new ModelAndView(viewExcel); 
		}
	}
}
