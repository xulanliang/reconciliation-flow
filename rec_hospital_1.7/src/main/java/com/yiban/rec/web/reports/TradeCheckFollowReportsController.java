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
			//???????????????????????????????????????????????????
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
		 * 2?????????
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
			// ????????????????????????
			vo.setCheckState(CommonEnum.BillBalance.zp.getValue());
			Page<TradeCheckFollow> platPage = tradeCheckFollowService.findAllHisPayPageByNotZP(vo,orgListTemp, this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}
	
	/**
	 * 3?????????
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
	 * ????????????????????????
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
	@Logable(operation = "????????????")
	@GetMapping("/dcExcel")
	public ResponseResult recGatherExport(@RequestParam("orgNo") String orgNo,@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime,@RequestParam("orgName") String orgName,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			request.setCharacterEncoding("utf-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			String fileName = startTime+"???"+endTime+orgName+"??????????????????";
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
			Sheet sheet = ee.getSheet(wb, orgName+"??????????????????"+startTime+"???"+endTime);
			//????????????
			CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)16, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
			Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
			
			ee.getCell(titleRow, 0, cellStyle, orgName+"??????????????????"+startTime+"???"+endTime);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
			
			//????????????
			CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
			//????????????
			Row brRow = ee.getRow(sheet, 5, null, 35);
			ee.getCell(brRow, 0, ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)16, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0), "????????????");
			sheet.addMergedRegion(new CellRangeAddress(5,5,0,9));
			
			Row hjRow = ee.getRow(sheet, 6, null, 20);
			ee.getCell(hjRow, 0, cellStyleHead, "?????????????????????");
			ee.getCell(hjRow, 1, cellStyleHead, "????????????");
			ee.getCell(hjRow, 2, cellStyleHead, "his??????");
			ee.getCell(hjRow, 3, cellStyleHead, "???????????? ");
			ee.getCell(hjRow, 4, cellStyleHead, "??????????????????");
			ee.getCell(hjRow, 5, cellStyleHead, "????????????");
			ee.getCell(hjRow, 6, cellStyleHead, "????????????");
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
	
	
	
	
		@Logable(operation = "????????????")
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
			String fileName = tcqvo.getStartTime()+"???"+tcqvo.getEndTime()+organization.getName() + "????????????????????????";
			response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"iso-8859-1") + ".xls");
			// ????????????????????????
			tcqvo.setCheckState(CommonEnum.BillBalance.zp.getValue());
			List<TradeCheckFollow> tradeCheckFollowList = tradeCheckFollowService.findAllHisPayPageByNotZP(tcqvo, orgListTemp, sort);
			ExportExcel ee = new ExportExcel();
			Workbook wb = new HSSFWorkbook();
			
			Sheet sheet = ee.getSheet(wb, fileName);
			
			//????????????
			CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
			Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
			ee.getCell(titleRow, 0, cellStyle, fileName);
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
			
			//????????????
			CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
			Row checkTimeRow = ee.getRow(sheet, 2, cellStyleHead, 20);
			ee.getCell(checkTimeRow, 0, cellStyleHead, "???????????????"+DateUtil.transferDateToString("yyyy/MM/dd", tcqvo.getStartDate()) +" - "+DateUtil.transferDateToString("yyyy/MM/dd", tcqvo.getEndDate()));
			//?????????
			ee.getCell(checkTimeRow, 5, cellStyleHead, "????????????"+user.getName());
			
			//????????????
			CellStyle cellTableStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
			Row headRow = ee.getRow(sheet, 4, null, 20);
			
			//??????
			ee.getCell(headRow, 0, cellTableStyleHead, "????????????");
			ee.getCell(headRow, 1, cellTableStyleHead, "????????????");
			ee.getCell(headRow, 2, cellTableStyleHead, "???????????????");
			ee.getCell(headRow, 3, cellTableStyleHead, "HIS????????????");
			ee.getCell(headRow, 4, cellTableStyleHead, "??????????????????");
			ee.getCell(headRow, 5, cellTableStyleHead, "??????(???)");
			ee.getCell(headRow, 6, cellTableStyleHead, "?????????????????????");
	
			CellStyle cellStyleContent = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short) 12, false, false), CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, (short) 1, (short) 1, (short) 1, (short) 1);
			// ??????
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
			// ??????
			Row totalTimeRow = ee.getRow(sheet, tradeCheckFollowList.size() + 5, cellStyleHead, 20);
			ee.getCell(totalTimeRow, 0, cellTableStyleHead, "");
			ee.getCell(totalTimeRow, 1, cellTableStyleHead, "??????");
			ee.getCell(totalTimeRow, 2, cellTableStyleHead, "");
			ee.getCell(totalTimeRow, 3, cellTableStyleHead, "");
			ee.getCell(totalTimeRow, 4, cellTableStyleHead, "");
			ee.getCell(totalTimeRow, 5, cellTableStyleHead, String.valueOf(totalAmount));
			ee.getCell(totalTimeRow, 6, cellTableStyleHead, "");
			sheet.setColumnWidth(0, "????????????".getBytes().length * 2 * 256);// ?????????????????????
	        sheet.autoSizeColumn((short)1); // ?????????????????????
	        sheet.autoSizeColumn((short)2); // ?????????????????????
	        sheet.autoSizeColumn((short)3); // ?????????????????????
	        sheet.autoSizeColumn((short)4); // ?????????????????????
	        sheet.setColumnWidth(5, "??????/?????????".getBytes().length * 2 * 128); // ?????????????????????
	        sheet.autoSizeColumn((short)6); // ?????????????????????
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			return null;
		}
	}
}
