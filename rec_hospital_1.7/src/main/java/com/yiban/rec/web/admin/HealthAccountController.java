package com.yiban.rec.web.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.OrganConfig;
import com.yiban.rec.domain.vo.HealthExceptionVo;
import com.yiban.rec.reconciliations.ReconciliationsService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HealthBillService;
import com.yiban.rec.service.OrganConfigService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.DayIterator;
import com.yiban.rec.util.ExportDataUtil;
import com.yiban.rec.util.NextDateProcess;

/**
 * 医保对账
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/admin/reconciliation/healthAccount")
public class HealthAccountController extends CurrentUserContoller {

	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private GatherService gatherService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private OrganConfigService organConfigService;

	@Autowired
	private ReconciliationsService reconciliationsService;

	@Autowired
	private HealthBillService healthBillService;
	@Autowired
	private ReconciliationService reconciliationService;

	@RequestMapping("")
	public String index(ModelMap model,String orgNo,String payDate) {
		//判断是否从其他页面点击链接进入，默认为双方对账，非现金对账
		int fag = 0;
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		if(orgNo != null || payDate != null) {
			fag = 1;
			model.put("tradeDate", payDate);
			model.put("orgNo", orgNo);
		}else {
			model.put("orgNo", orgCode);
			model.put("tradeDate", DateUtil.getSpecifiedDayBefore(new Date()));
		}
//		model.put("billSource", hConfig.getIsBillsSources());
//		model.put("patType", hConfig.getIsOutpatient());
		model.put("flag",fag);
		OrganConfig organConfig = organConfigService.getOrganConfigByOrgNo(orgCode);
		if(organConfig!=null)model.put("payModel",organConfig.getPayModel());
		return autoView("reconciliation/healthBill");
	}


	@RestController
	@RequestMapping({"/admin/healthAccount/data"})
	class HealthAccountDataController extends BaseController {


		/**
		 * 医保统计
		 */
		@GetMapping
		public ResponseResult healthcount(String orgNo,String orgCode,String startDate,String endDate,String payFlowNo,String orderState, String dataSource) {
			ResponseResult rs = ResponseResult.success();
			try {
				if(StringUtils.isBlank(startDate)&&StringUtils.isBlank(payFlowNo)&&StringUtils.isBlank(orderState)) {
					startDate=DateUtil.getSpecifiedDayBefore(new Date());
					endDate=DateUtil.getSpecifiedDayBefore(new Date());
				}
				if(orgNo == null || orgNo.trim().equals("")){
					String orgCodee = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
					orgNo = orgCodee;
				}
				if(StringUtils.isBlank(payFlowNo)&&StringUtils.isNotBlank(orderState)) {
					rs = ResponseResult.failure("医保中心流水号不能为空");
				}
				Map<String, Map<String, Object>> map = healthBillService.getCount(orgNo,orgCode,startDate,endDate,payFlowNo,orderState, dataSource);
				rs.data(map);
			} catch (Exception e) {
				rs = ResponseResult.failure("异常");
				e.printStackTrace();
				return rs;
			}
			return rs;
		}

		/**
		 * 医保异常账单
		 */
		@GetMapping("/exceptionList")
		public WebUiPage<Map<String, Object>> healthException(HealthExceptionVo vo) {
			Sort sort = new Sort(Direction.DESC, "createdDate");
			PageRequest pageable = this.getRequestPageabledWithInitSort(sort);
			if (StringUtils.isBlank(vo.getOrgNo())) {
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				vo.setOrgNo(orgCode);
			}
			if(StringUtils.isBlank(vo.getStartTime())) {
				vo.setStartTime(DateUtil.getSpecifiedDayBefore(new Date()));
				vo.setEndTime(DateUtil.getSpecifiedDayBefore(new Date()));
			}
			Page<Map<String, Object>> data = healthBillService.healthException(vo, pageable);
			return this.toWebUIPage(data);
		}

		@Logable(operation = "手动平账")
		@RequestMapping(value = "/dealFollow", method = RequestMethod.POST)
		@ResponseBody
		public ResponseResult dealFollow(String payFlowNo, String description,
										 @RequestParam(required = false) MultipartFile file, String checkState, String tradeAmount,
										 String orgCode, String tradeDatetime, String payType, String billSource, String patType) {
			if ((description == null || description.trim().equals("")) && file == null) {
				return ResponseResult.failure("请输入原因");
			}
			if (null != description && description.length() >= 200) {
				return ResponseResult.failure("请输入文字少于200个");
			}
			try {
				tradeDatetime = DateUtil.transferLongToDate(DateUtil.STANDARD_SHORT_FORMAT, Long.valueOf(tradeDatetime));
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("时间戳{}转换异常", tradeDatetime);
				return ResponseResult.failure("时间转换异常");
			}
			User user = currentUser();
			return reconciliationService.newDealFollow(user.getName(), payFlowNo, description, file, checkState, tradeAmount,
					orgCode, tradeDatetime, payType, billSource, patType, "", "");
		}


		@GetMapping("/exportDate")
		public void healthExportDate(HealthExceptionVo vo, HttpServletRequest request, HttpServletResponse response) {

			PageRequest pageable = new PageRequest(0, 200000, getSortFromDatagridOrElse(new Sort(Direction.DESC, "createdDate")));
			if (StringUtils.isBlank(vo.getOrgNo())) {
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				vo.setOrgNo(orgCode);
			}
			if(StringUtils.isBlank(vo.getStartTime())) {
				vo.setStartTime(DateUtil.getSpecifiedDayBefore(new Date()));
				vo.setEndTime(DateUtil.getSpecifiedDayBefore(new Date()));
			}
			Page<Map<String, Object>> data = healthBillService.healthException(vo, pageable);
			List<Map<String,Object>> listData =   data.getContent();
			String fileName = vo.getStartTime()+"~"+vo.getEndTime() + "医保异常账单明细";
			for (Map<String, Object> lineData : listData) {
				if ((lineData.get("billState").toString().equals("0"))) {
					lineData.put("costTotalInsurance", " -- ");
				}
				if ((lineData.get("hisState").toString().equals("0"))) {
					lineData.put("costTotalInsuranceHis", " -- ");
				}
			}
			String[] thirdTitleArray = {"结算业务序列号", "医保就诊流水号", "医保卡号", "医保电脑号", "挂号/缴费", "患者姓名", "医保合计金额（医保）", "医保合计金额（His）", "当前状态"};
			String[] cellValue = {"payFlowNo", "shopFlowNo", "healthCode", "socialComputerNumber", "busnessType", "patientName", "costTotalInsurance", "costTotalInsuranceHis", "state"};
			ExportDataUtil exportDataUtil = new ExportDataUtil(18, thirdTitleArray.length - 1, thirdTitleArray, cellValue);
			try {
				exportDataUtil.commonExportExcel(fileName, "医保异常账单明细", request, response, listData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 医保对账
		 * @param orgNo
		 * @param tradeDate
		 * @return
		 */
		@PostMapping("/account")
		public ResponseResult startRec(@RequestParam("orgNo") String orgNo,@RequestParam("accountDate") String accountDate ) {
			ResponseResult rs = ResponseResult.success("操作成功");
			new DayIterator(accountDate, accountDate).next(new NextDateProcess() {
				@Override
				public void process(String date) {
					try{
						//只对账医保
						reconciliationsService.compareHealthBill(orgNo, date);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			return rs;
		}
	}

}
