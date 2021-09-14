package com.yiban.rec.web.recon.cash;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.service.SessionUserService;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.domain.vo.CashQueryVo;
import com.yiban.rec.domain.vo.TradeDetailQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;

import net.sf.json.JSONObject;

/**
 * 
 * <p>
 * 文件名称:ReconciliationController.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:对账管理--->现金交易查询
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
@RequestMapping("/admin/cash")
public class ReconciliationCashController extends FrameworkController {

	@Autowired
	private ReconciliationService reconciliationService;

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	@Autowired
	private SessionUserService sessionUserService;
	
	@RequestMapping(value="")
	public String index(ModelMap model) {
		model.put("orgJSON", JSONObject.fromObject(gatherService.getOrgMap()));
		model.put("typesJSON", JSONObject.fromObject(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("org", EnumType.HIS_CASH.getValue());
		model.put("cashCode", EnumType.PAY_CODE_CASH.getValue());
		model.put("accountStartDate", DateUtil.getSpecifiedDayBefore(new Date())+" 00:00:00");
		model.put("accountEndDate", DateUtil.getSpecifiedDayBefore(new Date())+" 23:59:59");
		model.put("flag",CommonConstant.ALL_ID);
		return autoView("cash/cash");
	}

	@RestController
	@RequestMapping({"/admin/cash/data"})
	class CashDataController extends BaseController {
		@GetMapping
		public WebUiPage<RecCash> recHisCashQuery(TradeDetailQueryVo cqvo,ModelMap model) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			User user = (User)sessionUserService.getCurrentSessionUser();
			if(StringUtils.isBlank(cqvo.getPayType())) {
				cqvo.setPayType(EnumType.PAY_CODE_CASH.getValue());
			}
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Page<RecCash> hisPage = reconciliationService.getTradeDetailPage(cqvo,orgListTemp,this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(hisPage);
		}
		
		/**
		* @date：2017年4月7日 
		* @Description：导出
		* @param model
		* @param request
		* @return: 返回结果描述
		* @return ModelAndView: 返回值类型
		* @throws
		 */
		@Logable(operation = "导出现金交易明细")
		@GetMapping("/dcExcel")
		public ModelAndView toDcExcel(CashQueryVo cqvo,ModelMap model, HttpServletRequest request){ 
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
			User user = (User)sessionUserService.getCurrentSessionUser();
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			ops.add(new ExcelDecoratedEntry("tradeDatatime", "账单日期"));
			ops.add(new ExcelDecoratedEntry("orgName", "机构名称"));
			ops.add(new ExcelDecoratedEntry("tradeCodeName", "交易类型"));
			ops.add(new ExcelDecoratedEntry("payBusinessTypeName", "业务类型"));
			ops.add(new ExcelDecoratedEntry("payTypeName", "支付类型"));
			ops.add(new ExcelDecoratedEntry("custName", "客户名称"));
			ops.add(new ExcelDecoratedEntry("custIdentifyTypeName", "客户标识"));
			ops.add(new ExcelDecoratedEntry("payFlowNo", "支付流水号"));
			ops.add(new ExcelDecoratedEntry("payAmount", "金额(元)","#.##"));
			ops.add(new ExcelDecoratedEntry("payTermNo", "终端号"));
			ops.add(new ExcelDecoratedEntry("paySourceName", "支付来源"));
			ops.add(new ExcelDecoratedEntry("orderStateName", "订单状态"));
			ExcelResult viewExcel = null;
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			try {
				if(StringUtils.isNotBlank(cqvo.getStartTime())) {
					cqvo.setStartDate(sdf.parse(cqvo.getStartTime()+" 00:00:00"));
				}
				if(StringUtils.isNotBlank(cqvo.getEndTime())) {
					cqvo.setEndDate(sdf.parse(cqvo.getEndTime()+" 23:59:59"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
			if(EnumType.HIS_CASH.getValue().equals(cqvo.getDataSource())){
				List<RecCash> hisList = reconciliationService.getRecCashHisList(cqvo,orgListTemp,page);
				viewExcel = new ExcelResult(hisList, ops);
			}else{
//				List<Platformflow> cashList = reconciliationService.getRecCashPlatList(cqvo,orgListTemp,sort);
//				viewExcel = new ExcelResult(cashList, ops);
			}
			if(EnumType.DATA_SOURCE_PLAT_CASH.getValue().equals(cqvo.getDataSource())){
				viewExcel.setFileName(DateUtil.getCurrentDateString()+EnumType.DATA_SOURCE_PLAT_CASH.getName());
			}else if(EnumType.HIS_CASH.getValue().equals(cqvo.getDataSource())){
				viewExcel.setFileName(DateUtil.getCurrentDateString()+EnumType.HIS_CASH.getName());
			}
			return new ModelAndView(viewExcel); 
		}
	}
	

}
