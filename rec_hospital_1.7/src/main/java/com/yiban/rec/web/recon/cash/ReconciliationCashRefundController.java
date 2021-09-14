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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.Reconciliation;
import com.yiban.rec.domain.vo.RecQueryVo;
import com.yiban.rec.service.AutoReconciliationService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;

import net.sf.json.JSONObject;

/**
 * 
 * <p>
 * 文件名称:ReconciliationCashRefundController.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:现金对账管理--->现金对账
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
@RequestMapping("/admin/cashrefund")
public class ReconciliationCashRefundController extends CurrentUserContoller {

	@Autowired
	private ReconciliationService reconciliationService;

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	
	@Autowired
	private AutoReconciliationService autoReconciliationService;

	
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JSONObject.fromObject(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JSONObject.fromObject(gatherService.getOrgMap()));
		model.put("cashCode", EnumType.PAY_CODE_CASH.getValue());
		model.put("accountDate", DateUtil.getCurrentDateString());
		model.put("orgNo", Configure.getPropertyBykey("yiban.projectid"));
		model.put("flag",CommonConstant.ALL_ID);
		return autoView("cash/cashrefund");
	}
	
	@RestController
	@RequestMapping({"/admin/cashrefund/data"})
	class CashDataController extends BaseController {
		@GetMapping
		public WebUiPage<Reconciliation> recDetailQuery(RecQueryVo rqvo) {
			Sort sort = new Sort(Direction.DESC, "orgAmount");
//			User user = currentUser();
			if(StringUtils.isBlank(rqvo.getPayType())) {
				rqvo.setPayType(EnumType.PAY_CODE_CASH.getValue());
			}
			//时间转换
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				if(StringUtils.isNotBlank(rqvo.getStartTime())) {
					rqvo.setStartDate(sdf.parse(rqvo.getStartTime()));
				}
				if(StringUtils.isNotBlank(rqvo.getEndTime())) {
					rqvo.setEndDate(sdf.parse(rqvo.getEndTime()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Page<Reconciliation> recPage = reconciliationService.getRecpage(rqvo,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(recPage);
		}
		
		/**
		* @date：2017年12月4日 
		* @Description：现金对账
		* @param orgNo
		* @param recDate
		* @return
		* @throws BusinessException: 返回结果描述
		* @return ResponseResult: 返回值类型
		* @throws
		 */
		@Logable(operation = "现金对账")
		@PostMapping
		public ResponseResult cashRec(String orgNo,
				String recDate)throws BusinessException{
			return autoReconciliationService.isAutoRecCashSuccess(orgNo, recDate);
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
		@Logable(operation = "导出现金退费明细")
		@GetMapping("/dcExcel")
		public ModelAndView toDcExcel(RecQueryVo rqv,ModelMap model, HttpServletRequest request){ 
			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
//			User user = currentUser();
//			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			ops.add(new ExcelDecoratedEntry("payDateStamName", "账单日期"));
			ops.add(new ExcelDecoratedEntry("isDifferentValue", "账平标记"));
			ops.add(new ExcelDecoratedEntry("orgName", "机构名称"));
			ops.add(new ExcelDecoratedEntry("payFlowNo", "支付流水号"));
			/*ops.add(new ExcelDecoratedEntry("payBusinessTypeName", "业务类型"));*/
			ops.add(new ExcelDecoratedEntry("payTypeName", "支付类型"));
			ops.add(new ExcelDecoratedEntry("custName", "患者名称"));
			ops.add(new ExcelDecoratedEntry("orgAmount", "his金额(元)","#.##"));
			ops.add(new ExcelDecoratedEntry("platformAmount", "平台金额(元)","#.##"));
			ExcelResult viewExcel = null;
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				if(StringUtils.isNotBlank(rqv.getStartTime())) {
					rqv.setStartDate(sdf.parse(rqv.getStartTime()+" 00:00:00"));
				}else {
					rqv.setStartDate(sdf.parse(DateUtil.getSpecifiedDayBefore(new Date())+" 00:00:00"));
				}
				if(StringUtils.isNotBlank(rqv.getEndTime())) {
					rqv.setEndDate(sdf.parse(rqv.getEndTime()+" 23:59:59"));
				}else {
					rqv.setStartDate(sdf.parse(DateUtil.getSpecifiedDayBefore(new Date())+" 23:59:59"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Sort sort = new Sort(Direction.DESC, "orgAmount");
			Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
			List<Reconciliation> cashRecList = reconciliationService.getRecDetailList(rqv,page);
			viewExcel = new ExcelResult(cashRecList, ops);
			viewExcel.setFileName(DateUtil.getCurrentDateString()+EnumType.DATA_SOURCE_PLAT_CASH.getName());
			return new ModelAndView(viewExcel); 
		}
		
		
		
	}

	
	
	
	
	

}
