package com.yiban.rec.web.admin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.OrganConfig;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.reconciliations.ReconciliationsService;
import com.yiban.rec.service.FollowingRecService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.OrganConfigService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.DayIterator;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.NextDateProcess;
import com.yiban.rec.util.StringUtil;



/**
 * ?????????????????????his?????????
 * 1??????????????????????????? 2???????????????????????????
 * @author {author name}
 *
 */
@Controller
@RequestMapping("/admin/reconciliation/following")
public class NextDayAccountController  extends CurrentUserContoller{
	
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private FollowingRecService followingRecService;
	
	
	
	@Autowired
	private GatherService gatherService;
	
	
	@Autowired
	private ReconciliationService reconciliationService;
	
	@Autowired
	private OrganConfigService organConfigService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	@Autowired
	private ReconciliationsService reconciliationsService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	@RequestMapping("")
	public String index(ModelMap model,String orgNo,String payDate) {
		//???????????????????????????????????????????????????????????????????????????????????????
		int fag = 0;
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
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
		return autoView("reconciliation/following");
	}
	
	
	@RestController
	@RequestMapping({"/admin/nextDayAccount/data"})
	class NextDayAccountDataController extends BaseController {
		/**
		 * ??????????????????
		 * @param orgNo
		 * @param payDate
		 * @return
		 */
		@GetMapping
		public ResponseResult recGatherQuery(String orgNo,String startDate,String endDate ) {
			ResponseResult rs = ResponseResult.success();
			try {
				AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
//				hConfig.setOrgCode(orgNo);
				List<FollowRecResult> list = new ArrayList<FollowRecResult>();
				if(StringUtils.isBlank(startDate)) {
					startDate=DateUtil.getSpecifiedDayBefore(new Date());
				}
				if(StringUtils.isBlank(endDate)) {
					endDate=DateUtil.getSpecifiedDayBefore(new Date());
				}
				if(orgNo == null || orgNo.trim().equals("")){
					String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
					orgNo = orgCode;
				}
				List<FollowRecResult> parentList = followingRecService.getFollowRecMap(startDate,endDate,hConfig);
				list.addAll(parentList);
				rs.data(list);
				
			} catch (Exception e) {
				rs = ResponseResult.failure("??????");
				e.printStackTrace();
				return rs;
			}
			
			return rs;
		}
		/**
		 * ????????????????????????
		 * @param orgNo
		 * @param payDate
		 * @return
		 */
		@PostMapping
		public ResponseResult recGatherDetailQuery(String orgNo,String startDate,String endDate ) {
			ResponseResult rs = ResponseResult.success();
			try {
				AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
//				hConfig.setOrgCode(orgNo);
				if(StringUtils.isBlank(startDate)) {
					startDate=DateUtil.getSpecifiedDayBefore(new Date());
				}
				if(StringUtils.isBlank(endDate)) {
					endDate=DateUtil.getSpecifiedDayBefore(new Date());
				}
				if(orgNo == null || orgNo.trim().equals("")){
					String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
					orgNo = orgCode;
				}
				List<Map<String, Object>> list = followingRecService.getFollowRecMapDetail(startDate,endDate,hConfig);
				rs.data(list);
			} catch (Exception e) {
				rs = ResponseResult.failure("??????");
				e.printStackTrace();
				return rs;
			}
			
			return rs;
		}

		@GetMapping("/exceptionTrade")
		public WebUiPage<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo) {
			PageRequest pageable = this.getRequestPageabledWithInitSort(this.getIdDescSort());
			if (StringUtils.isBlank(vo.getOrgNo())) {
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				vo.setOrgNo(orgCode);
			}
			Page<TradeCheckFollow> data = followingRecService.findByOrgNoAndTradeDate(vo, pageable);
			for(TradeCheckFollow tcf : data.getContent()) {
				for(TradeCheckFollow tcf2 : data.getContent()) {
					try {
						if ((tcf.getTradeName() != null) && (tcf2.getTradeName() != null)) {
							if(tcf.getId() != tcf2.getId() && tcf.getBusinessNo().equals(tcf2.getBusinessNo()) && tcf.getTradeAmount().compareTo(tcf2.getTradeAmount()) == 0 && 
									((tcf.getTradeName().equals(EnumTypeOfInt.PAY_CODE.getValue()) &&  tcf2.getTradeName().equals(EnumTypeOfInt.REFUND_CODE.getValue())) || 
											(tcf.getTradeName().equals(EnumTypeOfInt.REFUND_CODE.getValue()) && tcf2.getTradeName().equals(EnumTypeOfInt.PAY_CODE.getValue())))) {
								tcf.setIsCorrection(EnumTypeOfInt.TRADE_CODE_REVERSAL.getValue());
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			return this.toWebUIPage(data);
		}
		
		/**
		 * ????????????
		 * @param orgNo
		 * @param tradeDate
		 * @return
		 */
		@Logable(operation = "??????????????????")
		@PostMapping("/account")
		public ResponseResult startRec(@RequestParam("orgNo") String orgNo,@RequestParam("startDate") String startDate ,
				@RequestParam("endDate") String endDate ) {
			ResponseResult rs = ResponseResult.success("????????????");
			new DayIterator(startDate, endDate).next(new NextDateProcess() {
				@Override
				public void process(String date) {
					try{
						reconciliationsService.compareBill(orgNo, date);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			return rs;
		}
		
		@Logable( operation = "??????????????????")
		@GetMapping("/dcExcel")
		public ResponseResult recGatherExport(@RequestParam("orgNo") String orgNo,@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,@RequestParam("orgName") String orgName,
				HttpServletRequest request, HttpServletResponse response,String patType,String billSource,String correction ) throws IOException, ParseException {
				response.setContentType("application/vnd.ms-excel;charset=utf-8");
				request.setCharacterEncoding("utf-8");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
				String fileName = startDate+"???"+endDate+orgName+"??????????????????";
				response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"iso-8859-1") + ".xls");
				AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
//				hConfig.setOrgCode(orgNo);
				List<FollowRecResult> platMap = followingRecService.getFollowRecMap( startDate,endDate,hConfig);
				List<MetaData> metaList = metaDataService.findAllMetaData();
				for(FollowRecResult followRecResult : platMap){
					for(MetaData m : metaList){
						if(followRecResult.getBillSource()!= null && followRecResult.getBillSource().equals(m.getValue())){
							followRecResult.setBillSource(m.getName());
						}
						if(followRecResult.getPatType()!= null && followRecResult.getPatType().equals(m.getValue())){
							followRecResult.setPatType(m.getName());
						}
					}
				}
				List<TradeCheckFollow> list = followingRecService.findByOrgNoAndTradeDateNoPage(orgNo, startDate, endDate, correction);
				ExportExcel ee = new ExportExcel();
				Workbook wb = new HSSFWorkbook();
				Sheet sheet = ee.getSheet(wb, startDate+"???"+endDate+orgName+"??????????????????");
				//????????????
				CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)16, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
				Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
				
				ee.getCell(titleRow, 0, cellStyle, startDate+"???"+endDate+orgName+"??????????????????");
				sheet.addMergedRegion(new CellRangeAddress(0,0,0,4));
				//????????????
				CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
				Row headRow = ee.getRow(sheet, 1, null, 20);
				
				
				//??????
				ee.getCell(headRow, 0, cellStyleHead, "????????????");
				ee.getCell(headRow, 1, cellStyleHead, "HIS???????????????(???)");
				ee.getCell(headRow, 2, cellStyleHead, "HIS???????????????(????????????)");
				ee.getCell(headRow, 3, cellStyleHead, "???????????????(???)");
				ee.getCell(headRow, 4, cellStyleHead, "????????????(???)");
				
				CellStyle cellStyleContent = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
				if(!StringUtil.isNullOrEmpty(platMap)){
					for(int i=0;i<=platMap.size()-1;i++){
						 FollowRecResult vo = platMap.get(i);
						 Row platRow = ee.getRow(sheet, i+2, null, 20);
						 ee.getCell(platRow, 0, cellStyleContent, String.valueOf(vo.getTradeDate()));
						 ee.getCell(platRow, 1, cellStyleContent, String.valueOf(vo.getHisAllAmount()==null?"0":vo.getHisAllAmount()));
						 ee.getCell(platRow, 2, cellStyleContent, String.valueOf(vo.getSettlementAmount()==null?"0":vo.getSettlementAmount()));
						 ee.getCell(platRow, 3, cellStyleContent, String.valueOf(vo.getPayAllAmount()==null?"0":vo.getPayAllAmount()));
						 ee.getCell(platRow, 4, cellStyleContent, String.valueOf(vo.getTradeDiffAmount()==null?"0":vo.getTradeDiffAmount()));
					}
				}
				//????????????
				Row brRow = ee.getRow(sheet, 3, null, 35);
				ee.getCell(brRow, 0, ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)16, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0), "????????????");
				sheet.addMergedRegion(new CellRangeAddress(3,3,0,10));
				
				Row hjRow = ee.getRow(sheet, 4, null, 20);
				ee.getCell(hjRow, 0, cellStyleHead, "??????");
				ee.getCell(hjRow, 1, cellStyleHead, "????????????");
				ee.getCell(hjRow, 2, cellStyleHead, "????????????");
				ee.getCell(hjRow, 3, cellStyleHead, "?????????????????????");
				ee.getCell(hjRow, 4, cellStyleHead, "??????");
				ee.getCell(hjRow, 5, cellStyleHead, "????????????");
				ee.getCell(hjRow, 6, cellStyleHead, "????????????");
				ee.getCell(hjRow, 7, cellStyleHead, "????????????");
				ee.getCell(hjRow, 8, cellStyleHead, "???????????? ");
				ee.getCell(hjRow, 9, cellStyleHead, "????????????");
				ee.getCell(hjRow, 10, cellStyleHead, "??????");
				
				
				if(!StringUtil.isNullOrEmpty(list)){
					for(int i=0;i<=list.size()-1;i++){
						TradeCheckFollow tradeCheckFollow = list.get(i);
						 Row hjMisRow = ee.getRow(sheet, i+5, null, 20);
						 	ee.getCell(hjMisRow, 0, cellStyleHead, tradeCheckFollow.getOrgNo());
						 	ee.getCell(hjMisRow, 1, cellStyleHead, tradeCheckFollow.getExceptionType());
						 	ee.getCell(hjMisRow, 2, cellStyleHead, tradeCheckFollow.getPayName().toString());
							ee.getCell(hjMisRow, 3, cellStyleHead, tradeCheckFollow.getBusinessNo());
							ee.getCell(hjMisRow, 4, cellStyleHead, String.valueOf(tradeCheckFollow.getTradeAmount()));
							ee.getCell(hjMisRow, 5, cellStyleHead, tradeCheckFollow.getPatientName());
							ee.getCell(hjMisRow, 6, cellStyleHead, String.valueOf(DateUtil.transferDateToString("yyyy-MM-dd HH:mm:ss", tradeCheckFollow.getTradeTime())));
							ee.getCell(hjMisRow, 7, cellStyleHead, formatterBusinessType(tradeCheckFollow.getBusinessType()));
							ee.getCell(hjMisRow, 8, cellStyleHead, tradeCheckFollow.getTradeName());
							ee.getCell(hjMisRow, 9, cellStyleHead, tradeCheckFollow.getTerminalNo());
							ee.getCell(hjMisRow, 10, cellStyleHead, tradeCheckFollow.getCheckStateValue());
					}
				}
				sheet.setColumnWidth(0, 9500);
				sheet.setColumnWidth(1, 8000);
				sheet.setColumnWidth(2, 6500);
				sheet.setColumnWidth(3, 6500);
				sheet.setColumnWidth(4, 6500);
				sheet.setColumnWidth(5, 5000);
				sheet.setColumnWidth(6, 5000);
				sheet.setColumnWidth(7, 5000);
				sheet.setColumnWidth(8, 5500);
				sheet.setColumnWidth(9, 5500);
				sheet.setColumnWidth(10, 5500);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.flush();
				out.close();
				return null;
		}
		
		/**
		 * ????????????
		 * @param payFlowNo, description ,file
		 * @param tradeDate
		 * @return
		 */
		@Logable(operation = "????????????")
		@PostMapping("/dealFollow")
		public ResponseResult dealFollow(String payFlowNo, String description ,@RequestParam(required = false) MultipartFile file) {
			if((description == null || description.trim().equals(""))  && file == null){
				return ResponseResult.failure("???????????????????????????????????????");
			}
			if(null != description && description.length() >= 200){
				return ResponseResult.failure("?????????????????????200???");
			}
			
			User user = currentUser();
			return reconciliationService.dealFollow(user.getName(),payFlowNo, description,file);
		}
		
		/**
		 * ????????????
		 * @param payFlowNo, description ,file
		 * @return
		 */
		@Logable(operation = "????????????")
		@GetMapping("/readImage")
		public void dealFollow(@RequestParam(required = true) String adress, HttpServletRequest request,
				HttpServletResponse response) {
			String fileLocation = Configure.getPropertyBykey("file.location");
			fileLocation = fileLocation + adress;
			File file = new File(fileLocation);
			if(file != null && file.exists()){
				try {
					response.getOutputStream().write(FileUtils.readFileToByteArray(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String formatterBusinessType(String code) {
		String name = "-";
		List<ValueTextable<String>> datas = metaDataService.NameAsList();
		for(ValueTextable<String> str : datas) {
			if(str.getValue().equals(code)) {
				name = str.getText();
			}
		}
		return name;	
	}
}
