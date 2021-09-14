package com.yiban.rec.web.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.OrganConfig;
import com.yiban.rec.domain.Reconciliation;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.RecQueryVo;
import com.yiban.rec.domain.vo.TradeCheckFollowQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.OrganConfigService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.TradeCheckFollowService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;

@Controller
@RequestMapping("/admin/tradeCheckFollowReports")
public class TradeCheckFollowReportsController extends CurrentUserContoller {

	@Autowired
	private GatherService gatherService;
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private OrganConfigService organConfigService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	public TradeCheckFollowService tradeCheckFollowService;
	
	@Autowired
	private ReconciliationService reconciliationService;
	
	@RequestMapping("")
	public String index(ModelMap model) {
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		model.put("accountDate", DateUtil.getInputDateOnlyDay(new Date()));
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		OrganConfig config = organConfigService.getOrganConfigByOrgNo(orgCode);
		String recType ="second";
		if(config!=null) {
			//判断对账类型为三方对账还是两方对账
			if(EnumType.THIRD_HANDLE_TYPE.getValue().equals("" + config.getRecType())) {
				recType = "third";
			}
		}
		model.put("recType", recType);
		return autoView("reports/tradeCheckFollowReport");
	}
	
	@RestController
	@RequestMapping({"/admin/tradeCheckFollowReports/data"})
	class TradeCheckFollowReportsDataController extends BaseController {
		
		@Autowired
		private UserOrganizationPerService userOrganizationPerService;
		@Autowired
		public TradeCheckFollowService tradeCheckFollowService;
		@Autowired
		private OrganizationService organizationService;
		/**
		 * 2方对账
		 * @param vo
		 * @return
		 */
		@GetMapping
		public WebUiPage<TradeCheckFollow> recHistradeQuery(TradeCheckFollowQueryVo vo) {
			Sort sort = new Sort(Direction.DESC, "tradeTime");
			User user = currentUser();
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			try {
				Date date = DateUtil.getInputDateOnlyDay(new Date());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				if(StringUtils.isNotBlank(vo.getStartTime())) {
					vo.setStartDate(sdf.parse(vo.getStartTime()));
				} else if(null == vo.getStartTime()) {
					vo.setStartDate(date);
				}
				if(StringUtils.isNotBlank(vo.getEndTime())) {
					vo.setEndDate(sdf.parse(vo.getEndTime()));
				} else if(null == vo.getEndTime()) {
					vo.setEndDate(date);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Organization organization = organizationService.findByCode(vo.getOrgNo());
			if(organization == null){
				organization = organizationService.findOrganizationById(Long.valueOf(vo.getOrgNo()));
				vo.setOrgNo(organization.getCode());
			}
			// 除账平之外的数据
			vo.setCheckState(CommonEnum.BillBalance.zp.getValue());
			Page<TradeCheckFollow> platPage = tradeCheckFollowService.findAllHisPayPageByNotZP(vo,orgListTemp, this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}
	
	/**
	 * 3方对账
	 * @param vo
	 * @return
	 */
	@GetMapping("/thridBill")
	public WebUiPage<Reconciliation> recHistradeQueryThrid(RecQueryVo vo) {
		PageRequest pageable = this.getRequestPageabledWithInitSort(this.getIdDescSort());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = DateUtil.getInputDateOnlyDay(new Date());
			if(StringUtils.isNotBlank(vo.getStartTime())) {
				vo.setStartDate(sdf.parse(vo.getStartTime()));
			} else if(null == vo.getStartTime()) {
				vo.setStartDate(date);
			}
			if(StringUtils.isNotBlank(vo.getEndTime())) {
				vo.setEndDate(sdf.parse(vo.getEndTime()));
			} else if(null == vo.getEndTime()) {
				vo.setEndDate(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Organization organization = organizationService.findByCode(vo.getOrgNo());
		if(organization == null){
			organization = organizationService.findOrganizationById(Long.valueOf(vo.getOrgNo()));
			vo.setOrgNo(organization.getCode());
		}
		Page<Reconciliation> data = reconciliationService.getRecpage(vo, pageable);
		return this.toWebUIPage(data);
	}
	/**
	 * 三方对账异常明细
	 * @param orgNo
	 * @param startDate
	 * @param endDate
	 * @param orgName
	 * @param request
	 * @param response
	 * @param patType
	 * @param billSource
	 * @param correction
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@Logable(operation = "导出报表")
	@GetMapping("/dcExcel")
	public ResponseResult recGatherExport(@RequestParam("orgNo") String orgNo,@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime,@RequestParam("orgName") String orgName,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			request.setCharacterEncoding("utf-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			String fileName = startTime+"至"+endTime+orgName+"异常账单明细";
			response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"iso-8859-1") + ".xls");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Map<String, String> typeMap = ValueTexts.asMap(metaDataService.NameAsList());
			Sort sort = new Sort(Direction.DESC, "payFlowNo");
			RecQueryVo vo=new RecQueryVo();
			vo.setOrgNo(orgNo);
			vo.setStartDate(sdf.parse(startTime));
			vo.setEndDate(sdf.parse(endTime));
			List<Reconciliation>  list = reconciliationService.getRecpageNopage(vo,sort);
			ExportExcel ee = new ExportExcel();
			Workbook wb = new HSSFWorkbook();
			Sheet sheet = ee.getSheet(wb, orgName+"对账差错明细"+startTime+"至"+endTime);
			//标题样式
			CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)16, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
			Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
			
			ee.getCell(titleRow, 0, cellStyle, orgName+"对账差错明细"+startTime+"至"+endTime);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
			
			//头部样式
			CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
			//异常账单
			Row brRow = ee.getRow(sheet, 5, null, 35);
			ee.getCell(brRow, 0, ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)16, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0), "异常账单");
			sheet.addMergedRegion(new CellRangeAddress(5,5,0,9));
			
			Row hjRow = ee.getRow(sheet, 6, null, 20);
			ee.getCell(hjRow, 0, cellStyleHead, "支付商户流水号");
			ee.getCell(hjRow, 1, cellStyleHead, "支付类型");
			ee.getCell(hjRow, 2, cellStyleHead, "his金额");
			ee.getCell(hjRow, 3, cellStyleHead, "平台金额 ");
			ee.getCell(hjRow, 4, cellStyleHead, "支付渠道金额");
			ee.getCell(hjRow, 5, cellStyleHead, "患者名称");
			ee.getCell(hjRow, 6, cellStyleHead, "交易时间");
			if(!StringUtil.isNullOrEmpty(list)){
				for(int i=0;i<=list.size()-1;i++){
					Reconciliation rec = list.get(i);
					 Row hjMisRow = ee.getRow(sheet, i+7, null, 20);
						ee.getCell(hjMisRow, 0, cellStyleHead, rec.getPayFlowNo());
						ee.getCell(hjMisRow, 1, cellStyleHead, typeMap.get(rec.getPayType()));
						ee.getCell(hjMisRow, 2, cellStyleHead, rec.getOrgAmount().toString());
						ee.getCell(hjMisRow, 3, cellStyleHead, rec.getPlatformAmount().toString());
						ee.getCell(hjMisRow, 4, cellStyleHead, rec.getThirdAmount().toString());
						ee.getCell(hjMisRow, 5, cellStyleHead, rec.getCustName());
						ee.getCell(hjMisRow, 6, cellStyleHead, String.valueOf(DateUtil.transferDateToString("yyyy-MM-dd HH:mm:ss", rec.getReconciliationDate())));
				}
			}
			sheet.setColumnWidth(0, 8000);
			sheet.setColumnWidth(1, 4000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 4000);
			sheet.setColumnWidth(4, 5000);
			sheet.setColumnWidth(5, 3000);
			sheet.setColumnWidth(6, 8000);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			return null;
	}
	
	
	
	
		@Logable(operation = "导出报表")
		@RequestMapping("/exportReport")
		public ResponseResult export(Model model, TradeCheckFollowQueryVo tcqvo, HttpServletRequest request, HttpServletResponse response) throws BusinessException, ParseException, IOException {
			response.setContentType("application/vnd.ms-excel");
			response.setContentType("application/octet-stream;charset=utf-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			Sort sort = new Sort(Direction.DESC, "tradeTime");
			User user = currentUser();
			Organization organization = organizationService.findByCode(tcqvo.getOrgNo());
			if(organization == null){
				organization = organizationService.findOrganizationById(Long.valueOf(tcqvo.getOrgNo()));
				tcqvo.setOrgNo(organization.getCode());
			}
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			try {
				Date date = DateUtil.getInputDateOnlyDay(new Date());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (StringUtils.isNotBlank(tcqvo.getStartTime())) {
					tcqvo.setStartDate(sdf.parse(tcqvo.getStartTime()));
				} else if (null == tcqvo.getStartTime()) {
					tcqvo.setStartDate(date);
				}
				if (StringUtils.isNotBlank(tcqvo.getEndTime())) {
					tcqvo.setEndDate(sdf.parse(tcqvo.getEndTime()));
				} else if (null == tcqvo.getEndTime()) {
					tcqvo.setEndDate(date);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			organization = null;
			if(orgListTemp.size() > 1) {
				for(Organization entity : orgListTemp) {
					if(StringUtil.equals(tcqvo.getOrgNo(), entity.getCode())) {
						organization = entity;
						break;
					}
				}
			} else {
				organization = orgListTemp.get(0);
			}
			String fileName = tcqvo.getStartTime()+"至"+tcqvo.getEndTime()+organization.getName() + "异常账单差异明细";
			response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"iso-8859-1") + ".xls");
			// 除账平之外的数据
			tcqvo.setCheckState(CommonEnum.BillBalance.zp.getValue());
			List<TradeCheckFollow> tradeCheckFollowList = tradeCheckFollowService.findAllHisPayPageByNotZP(tcqvo, orgListTemp, sort);
			ExportExcel ee = new ExportExcel();
			Workbook wb = new HSSFWorkbook();
			
			Sheet sheet = ee.getSheet(wb, fileName);
			
			//标题样式
			CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
			Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
			ee.getCell(titleRow, 0, cellStyle, fileName);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
			
			//查询日期
			CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
			Row checkTimeRow = ee.getRow(sheet, 2, cellStyleHead, 20);
			ee.getCell(checkTimeRow, 0, cellStyleHead, "查询日期："+DateUtil.transferDateToString("yyyy/MM/dd", tcqvo.getStartDate()) +" - "+DateUtil.transferDateToString("yyyy/MM/dd", tcqvo.getEndDate()));
			//打印人
			ee.getCell(checkTimeRow, 5, cellStyleHead, "打印人："+user.getName());
			
			//头部样式
			CellStyle cellTableStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
			Row headRow = ee.getRow(sheet, 4, null, 20);
			
			//总账
			ee.getCell(headRow, 0, cellTableStyleHead, "机构名称");
			ee.getCell(headRow, 1, cellTableStyleHead, "客户姓名");
			ee.getCell(headRow, 2, cellTableStyleHead, "付款方账号");
			ee.getCell(headRow, 3, cellTableStyleHead, "HIS交易时间");
			ee.getCell(headRow, 4, cellTableStyleHead, "异常处理时间");
			ee.getCell(headRow, 5, cellTableStyleHead, "金额(元)");
			ee.getCell(headRow, 6, cellTableStyleHead, "支付商户流水号");
	
			CellStyle cellStyleContent = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short) 12, false, false), CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, (short) 1, (short) 1, (short) 1, (short) 1);
			// 合计
			BigDecimal totalAmount = BigDecimal.ZERO;
			if (!StringUtil.isNullOrEmpty(tradeCheckFollowList)) {
				for (int i = 0; i <= tradeCheckFollowList.size() - 1; i++) {
					TradeCheckFollow vo = tradeCheckFollowList.get(i);
					totalAmount = totalAmount.add(vo.getTradeAmount());
					Row platRow = ee.getRow(sheet, i + 5, null, 20);
					for(Organization entity : orgListTemp) {
						if(StringUtil.equals(vo.getOrgNo(), entity.getCode())) {
							ee.getCell(platRow, 0, cellStyleContent, entity.getName());
							break;
						}
					}
					ee.getCell(platRow, 1, cellStyleContent, String.valueOf(StringUtil.isNullOrEmpty(vo.getPatientName()) ? "" : vo.getPatientName()));
					String paymentAccount = "------";
					if(!StringUtil.isNullOrEmpty(vo.getPaymentAccount()) && !StringUtil.equals(vo.getPaymentAccount(), "(null)") && !StringUtil.equals(vo.getPaymentAccount(), "null")) {
						paymentAccount = vo.getPaymentAccount();
					}
					ee.getCell(platRow, 2, cellStyleContent, String.valueOf(paymentAccount));
					ee.getCell(platRow, 3, cellStyleContent, String.valueOf(DateUtil.transferDateToString("yyyy-MM-dd HH:mm:ss", vo.getTradeTime())));
					ee.getCell(platRow, 4, cellStyleContent, String.valueOf(DateUtil.transferDateToString("yyyy-MM-dd HH:mm:ss", vo.getCreatedDate())));
					ee.getCell(platRow, 5, cellStyleContent, String.valueOf(vo.getTradeAmount() == null ? "0" : vo.getTradeAmount()));
					ee.getCell(platRow, 6, cellStyleContent, String.valueOf(StringUtil.isNullOrEmpty(vo.getBusinessNo()) ? "" : vo.getBusinessNo()));
				}
			}
			// 合计
			Row totalTimeRow = ee.getRow(sheet, tradeCheckFollowList.size() + 5, cellStyleHead, 20);
			ee.getCell(totalTimeRow, 0, cellTableStyleHead, "");
			ee.getCell(totalTimeRow, 1, cellTableStyleHead, "合计");
			ee.getCell(totalTimeRow, 2, cellTableStyleHead, "");
			ee.getCell(totalTimeRow, 3, cellTableStyleHead, "");
			ee.getCell(totalTimeRow, 4, cellTableStyleHead, "");
			ee.getCell(totalTimeRow, 5, cellTableStyleHead, String.valueOf(totalAmount));
			ee.getCell(totalTimeRow, 6, cellTableStyleHead, "");
			sheet.setColumnWidth(0, "机构名称".getBytes().length * 2 * 256);// 调整第一列宽度
	        sheet.autoSizeColumn((short)1); // 调整第二列宽度
	        sheet.autoSizeColumn((short)2); // 调整第三列宽度
	        sheet.autoSizeColumn((short)3); // 调整第四列宽度
	        sheet.autoSizeColumn((short)4); // 调整第四列宽度
	        sheet.setColumnWidth(5, "应收/退金额".getBytes().length * 2 * 128); // 调整第八列宽度
	        sheet.autoSizeColumn((short)6); // 调整第九列宽度
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			return null;
		}
	}
}
