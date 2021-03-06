package com.yiban.rec.web.reports;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

import com.ibm.icu.math.BigDecimal;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.domain.vo.TradeCheckVo;
import com.yiban.rec.service.ElectronicRecService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.PayTypeEnum;

/**
 * @Description ??????????????????
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-02-26 10:19
 */
@Controller
@RequestMapping("")
public class UnusualBillController {
	@Autowired
	private PropertiesConfigService propertiesConfigService;
    @Autowired
    private GatherService gatherService;
    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private HospitalConfigService hospitalConfigService;
    @Autowired
    private ElectronicRecService electronicRecService;
    private final int height = 18;

    public void setResponseAdRequest(HttpServletRequest request, HttpServletResponse response, String fileName)
            throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        request.setCharacterEncoding("utf-8");
        response.addHeader("Content-disposition",
                "attachment; filename=" + new String(fileName.getBytes(), "iso-8859-1") + ".xls");
    }

    public void setBorderStyle(CellStyle cellStyle) {
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    }

    public String getNotNullStr(Object str) {
        return (str == null ? "0" : String.valueOf(str).trim());
    }

    /**
     * ????????????????????????controller
     */
    @Controller
    @RequestMapping(value = "admin/unusualBill")
    public class BusinessTypeSumaryReportsController extends CurrentUserContoller {
        /**
         * ????????????
         * @param model
         * @return
         */
        @RequestMapping(value = "/index")
        public String index(ModelMap model) {
        	String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			String electronicRecDetailButtonOnly = propertiesConfigService.findValueByPkey(
					ProConstants.electronicRecDetailButtonOnly,
					ProConstants.DEFAULT.get(ProConstants.electronicRecDetailButtonOnly));
			String electronicRecPatTypeDisplay = propertiesConfigService.findValueByPkey(
					ProConstants.electronicRecPatTypeDisplay,
					ProConstants.DEFAULT.get(ProConstants.electronicRecPatTypeDisplay));
			
        	// ??????????????????????????????????????? 0?????? 1?????????????????????
            AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
            model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
            model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
            model.put("orgCode", orgCode);
            model.put("beginTime", DateUtil.getSpecifiedDayBefore(new Date()));
            model.put("endTime", DateUtil.getSpecifiedDayBefore(new Date()));
            model.put("isRefundExamine", hConfig.getIsRefundExamine());
            model.put("electronicRecDetailButtonOnly",
    				StringUtils.isNotBlank(electronicRecDetailButtonOnly) && "true".equals(electronicRecDetailButtonOnly) ? electronicRecDetailButtonOnly : false);
    		model.put("electronicRecPatTypeDisplay",
    				"true".equals(electronicRecPatTypeDisplay) ? electronicRecPatTypeDisplay : false);
            return "reconciliation/unusualBill";
        }

        /**
         * ??????????????????
         * @param unusualBillVo
         * @return
         */
        @RequestMapping(value = "data")
        @ResponseBody
        public WebUiPage<TradeCheckFollow> data(TradeCheckFollowVo unusualBillVo) {

            PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Sort.Direction.DESC, "businessNo"));
            if (StringUtils.isBlank(unusualBillVo.getOrgNo())) {
            	String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
                unusualBillVo.setOrgNo(orgCode);
            }
            Page<TradeCheckFollow> data = electronicRecService.findDataByOrgNoAndTradeDate(unusualBillVo, pageable);
            return this.toWebUIPage(data);
        }
        
        /**
         * ???????????????
         * @param unusualBillVo
         * @return
         */
        @RequestMapping(value = "diffAmount")
        @ResponseBody
        public ResponseResult diffAmount(TradeCheckFollowVo unusualBillVo){
        	if (StringUtils.isBlank(unusualBillVo.getOrgNo())) {
        		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
                unusualBillVo.setOrgNo(orgCode);
            }
        	Map<String,Object> map = electronicRecService.getDiffAmount(unusualBillVo);
        	return ResponseResult.success().data(map);
        }
        @RequestMapping(value = "dcExcel")
        public void exportData(TradeCheckFollowVo unusualBillVo, String fileName, String workSheetName,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {
            PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Sort.Direction.DESC, "businessNo"));
            if (StringUtils.isBlank(unusualBillVo.getOrgNo())) {
            	String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
                unusualBillVo.setOrgNo(orgCode);
            }
            Page<TradeCheckFollow> data = electronicRecService.findDataByOrgNoAndTradeDate(unusualBillVo, pageable);
            // ????????????
            List<MetaData> metaDataList = metaDataService.findMetaDataByDataTypeValue("bill_source");
            List<TradeCheckFollow> listData = data.getContent();
            /**
             * ??????????????????????????????????????????
             */
            String isFilterDealBill = propertiesConfigService.findValueByPkey("is.filter.deal.bill", "false");
            if("true".equals(isFilterDealBill)){
            	listData = electronicRecService.filterDealBill(listData);
            }
            commonExportExcel(fileName, workSheetName, request, response, listData, metaDataList);
        }

        public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
                                      HttpServletResponse response, List<TradeCheckFollow> dataList, List<MetaData> metaDataList) throws Exception {
            setResponseAdRequest(request, response, fileName);
            ExportExcel ee = new ExportExcel();
            // workbook????????????Excel
            HSSFWorkbook wb = new HSSFWorkbook();

            // ??????????????????????????????:?????????,??????
            HSSFCellStyle fontStyle = wb.createCellStyle();
            fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            setBorderStyle(fontStyle);

            HSSFCellStyle borderStyle = wb.createCellStyle();
            setBorderStyle(borderStyle);
            String[] titleArray = { "??????????????????","???????????????", "HIS?????????","??????ID","????????????","????????????","?????????????????????","????????????","????????????","????????????", "????????????","????????????","            ??????            "};

            // ????????????sheet
            Sheet sheet = ee.getSheet(wb, workSheetName);
            // ???????????????, ???????????????
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleArray.length-1));
            Row titleRow = ee.getRow(sheet, 0, null, height);
            ee.getCell(titleRow, 0, fontStyle, fileName);
            // ?????????????????????????????????????????????????????????
            for (int i = 1; i < 9; i++) {
                ee.getCell(titleRow, i, fontStyle, "");
            }
            // ??????????????????????????????
            Row thirdTitleRow = ee.getRow(sheet, 1, null, height);
            for (int i = 0, len = titleArray.length; i < len; i++) {
                ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
            }
            if (dataList.size() >= 1) {
                TradeCheckFollow colMap = null;
                CellStyle doubleStyle = wb.createCellStyle();
                DataFormat df = wb.createDataFormat();
                doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
                setBorderStyle(doubleStyle);

                Map<String,TradeCheckVo> patIdMap = electronicRecService.getPatIdMap(dataList);
                String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
        		List<String> repeatList = new ArrayList<>();//??????????????????????????????list
        		List<TradeCheckFollow> tmpHisList = new ArrayList<>();
        		tmpHisList.addAll(dataList);
        		for (int i = 0; i < tmpHisList.size() - 1; i++) {
                    for (int j = tmpHisList.size() - 1; j > i; j--) {
                        if (tmpHisList.get(j).getBusinessNo().equals(tmpHisList.get(i).getBusinessNo()) 
                        		&& tmpHisList.get(j).getTradeAmount().equals(tmpHisList.get(i).getTradeAmount())) {
                        	if ((hisCheck.contains(tmpHisList.get(j).getOriCheckState())?"??????":"??????").equals("??????") 
                        			&& (hisCheck.contains(tmpHisList.get(i).getOriCheckState())?"??????":"??????").equals("??????")) {
                        		if (!"".equals(tmpHisList.get(j).getBusinessNo())) {
                        			repeatList.add(tmpHisList.get(j).getBusinessNo());//?????????????????????list(???????????????)
                                    tmpHisList.remove(j);//??????????????????
        						}
        					}else if((hisCheck.contains(tmpHisList.get(j).getOriCheckState())?"??????":"??????").equals("??????") 
                        			&& (hisCheck.contains(tmpHisList.get(i).getOriCheckState())?"??????":"??????").equals("??????")) {
        						if (!"".equals(tmpHisList.get(j).getBusinessNo())) {
                        			repeatList.add(tmpHisList.get(j).getBusinessNo());//?????????????????????list(???????????????)
                                    tmpHisList.remove(j);//??????????????????
        						}
        					}
                        }
                    }
                }
                for (int i = 0, len = dataList.size(); i < len; i++) {
                    Row dataRow = ee.getRow(sheet, i + 2, null, height);
                    colMap = dataList.get(i);
                    // ?????????????????????
                    ee.getCell(dataRow, 0, borderStyle, colMap.getBusinessNo());
                    // ???????????????
                    Cell cell1 = dataRow.createCell(1);
                    cell1.setCellStyle(borderStyle);
                    cell1.setCellValue(colMap.getShopFlowNo());
                    // HIS?????????
                    Cell cell2 = dataRow.createCell(2);
                    cell2.setCellStyle(borderStyle);
                    cell2.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? colMap.getHisFlowNo():patIdMap.get(colMap.getBusinessNo()).getHisFlowNo());
                    // ??????ID
                    Cell cell3 = dataRow.createCell(3);
                    cell3.setCellStyle(borderStyle);
                    cell3.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? null:patIdMap.get(colMap.getBusinessNo()).getPatId());
                    // ????????????
                    Cell cell4 = dataRow.createCell(4);
                    cell4.setCellStyle(borderStyle);
                    cell4.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? colMap.getPatientName():patIdMap.get(colMap.getBusinessNo()).getCustName());
                    // ????????????
                    Cell cell5 = dataRow.createCell(5);
                    cell5.setCellStyle(borderStyle);
                    cell5.setCellValue(EnumTypeOfInt.getByCode(colMap.getTradeName()).getCode());
                    // ????????????
                    Cell cell6 = dataRow.createCell(6);
                    cell6.setCellStyle(doubleStyle);
                    cell6.setCellValue(Double.parseDouble(getNotNullStr(colMap.getTradeAmount())));
                    
                    // ????????????
                    Cell cell7 = dataRow.createCell(7);
                    cell7.setCellStyle(doubleStyle);
                    SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    cell7.setCellValue(f.format(colMap.getTradeTime()));
                    // ????????????
                    Cell cell8 = dataRow.createCell(8);
                    cell8.setCellStyle(doubleStyle);
                    cell8.setCellValue(PayTypeEnum.getByCode(colMap.getPayName()).getName());
                    
                    // ????????????
                    Cell cell9 = dataRow.createCell(9);
                    cell9.setCellStyle(borderStyle);
                    String channelName = colMap.getBillSource();
                    for (MetaData metaData : metaDataList) {
                        if (metaData != null) {
                            if (metaData.getValue().equals(channelName)) {
                            	channelName = metaData.getName();
                                break;
                            }
                        }
                    }
                    cell9.setCellValue(channelName);
                    
                    // ????????????
                    Cell cell10 = dataRow.createCell(10);
                    cell10.setCellStyle(borderStyle);
                    cell10.setCellValue(hisCheck.contains(colMap.getOriCheckState()) ? "??????" : "??????");
                    
                    // ????????????
                    Cell cell11 = dataRow.createCell(11);
                    cell11.setCellStyle(doubleStyle);
                    cell11.setCellValue(colMap.getCheckStateValue());
                    // ??????
                    Cell cell12 = dataRow.createCell(12);
                    cell12.setCellStyle(doubleStyle);
                    cell12.setCellValue(getRemark(colMap,repeatList));
                }
            }
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
            out.close();
        }


        public int otherAcount(Map<String, Object> colMap) {
            int allAcount = Integer.parseInt(getNotNullStr(colMap.get("allAcount")));
            int registerAcount = Integer.parseInt(getNotNullStr(colMap.get("registerAcount")));
            int makeAppointmentAcount = Integer.parseInt(getNotNullStr(colMap.get("makeAppointmentAcount")));
            int payAcount = Integer.parseInt(getNotNullStr(colMap.get("payAcount")));
            int clinicAcount = Integer.parseInt(getNotNullStr(colMap.get("clinicAcount")));
            int prepaymentForHospitalizationAcount = Integer
                    .parseInt(getNotNullStr(colMap.get("prepaymentForHospitalizationAcount")));

            int otherAcount = allAcount - registerAcount - makeAppointmentAcount - payAcount - clinicAcount
                    - prepaymentForHospitalizationAcount;

            return otherAcount;
        }

        public BigDecimal otherAmount(Map<String, Object> colMap) {
            BigDecimal allAmount = new BigDecimal(getNotNullStr(colMap.get("allAmount")));
            BigDecimal registerAmount = new BigDecimal(getNotNullStr(colMap.get("registerAmount")));
            BigDecimal makeAppointmentAmount = new BigDecimal(getNotNullStr(colMap.get("makeAppointmentAmount")));
            BigDecimal payAmount = new BigDecimal(getNotNullStr(colMap.get("payAmount")));
            BigDecimal clinicAmount = new BigDecimal(getNotNullStr(colMap.get("clinicAmount")));
            BigDecimal prepaymentForHospitalizationAmount = new BigDecimal(getNotNullStr(colMap.get("prepaymentForHospitalizationAmount")));

            BigDecimal otherAmount = allAmount.subtract(registerAmount).subtract(makeAppointmentAmount)
                    .subtract(payAmount).subtract(clinicAmount).subtract(prepaymentForHospitalizationAmount);

            return otherAmount.setScale(2);
        }
        
        /**
    	 * ??????????????????
    	 * @param tradeName ????????????
    	 * @param oriCheckState ???????????????
    	 * @param channelName ????????????
    	 * @return
    	 */
    	private String getBillSource(String tradeName, String oriCheckState,String channelName) {
    		if (tradeName.equals("??????") && oriCheckState.equals("??????")) {
    			return channelName;
    		}
    		if (tradeName.equals("??????") && oriCheckState.equals("??????")) {
    			return "HIS";
    		}
    		if (tradeName.equals("??????") && oriCheckState.equals("??????")) {
    			return "HIS";
    		}
    		if (tradeName.equals("??????") && oriCheckState.equals("??????")) {
    			return channelName;
    		}
    		return null;
    	}

    	/**
    	 *  ????????????
    	 * @param repeatList 
    	 * @param tradeCheckFollow 
    	 * @param tradeName ????????????
    	 * @param oriCheckState ???????????????
    	 * @return
    	 */
    	private String getRemark(TradeCheckFollow tradeCheckFollow, List<String> repeatList) {
    		String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
    		String tradeName = EnumTypeOfInt.getByCode(tradeCheckFollow.getTradeName()).getCode();
    		String oriCheckState = hisCheck.contains(tradeCheckFollow.getOriCheckState())?"??????":"??????";
    		String remark = null;
    		if (tradeName.equals("??????") && oriCheckState.equals("??????")) {
    			remark = "???????????????HIS??????";
    		}
    		if (tradeName.equals("??????") && oriCheckState.equals("??????")) {
    			remark = "???????????????HIS??????";
    		}
    		if (tradeName.equals("??????") && oriCheckState.equals("??????")) {
    			remark = "???????????????HIS??????";
    		}
    		if (tradeName.equals("??????") && oriCheckState.equals("??????")) {
    			remark = "???????????????HIS??????";
    		}
    		if (repeatList.contains(tradeCheckFollow.getBusinessNo())&&StringUtils.isNotBlank(tradeCheckFollow.getBusinessNo())) {
    			return remark + "????????????";
    		}
    		return remark;
    	}
    }
}
