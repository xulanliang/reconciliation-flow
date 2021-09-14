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
 * @Description 异常账单查询
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
     * 异常账单查询汇总controller
     */
    @Controller
    @RequestMapping(value = "admin/unusualBill")
    public class BusinessTypeSumaryReportsController extends CurrentUserContoller {
        /**
         * 菜单页面
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
			
        	// 保存是否是其他页面入口进来 0不是 1是其他页面进来
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
         * 异常账单数据
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
         * 差异总金额
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
            // 账单来源
            List<MetaData> metaDataList = metaDataService.findMetaDataByDataTypeValue("bill_source");
            List<TradeCheckFollow> listData = data.getContent();
            /**
             * 是否过滤已经处理过的异常账单
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
            // workbook对应一个Excel
            HSSFWorkbook wb = new HSSFWorkbook();

            // 定义一个统一字体样式:、居中,边框
            HSSFCellStyle fontStyle = wb.createCellStyle();
            fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            setBorderStyle(fontStyle);

            HSSFCellStyle borderStyle = wb.createCellStyle();
            setBorderStyle(borderStyle);
            String[] titleArray = { "支付方流水号","商户流水号", "HIS流水号","患者ID","患者姓名","交易类型","交易金额（元）","交易时间","支付类型","渠道名称", "异常类型","当前状态","            原因            "};

            // 创建一个sheet
            Sheet sheet = ee.getSheet(wb, workSheetName);
            // 第一行标题, 合并单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleArray.length-1));
            Row titleRow = ee.getRow(sheet, 0, null, height);
            ee.getCell(titleRow, 0, fontStyle, fileName);
            // 创建等多的单元格，解决合并单元格的问题
            for (int i = 1; i < 9; i++) {
                ee.getCell(titleRow, i, fontStyle, "");
            }
            // 第三行标题、业务类型
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
        		List<String> repeatList = new ArrayList<>();//用于存放重复的元素的list
        		List<TradeCheckFollow> tmpHisList = new ArrayList<>();
        		tmpHisList.addAll(dataList);
        		for (int i = 0; i < tmpHisList.size() - 1; i++) {
                    for (int j = tmpHisList.size() - 1; j > i; j--) {
                        if (tmpHisList.get(j).getBusinessNo().equals(tmpHisList.get(i).getBusinessNo()) 
                        		&& tmpHisList.get(j).getTradeAmount().equals(tmpHisList.get(i).getTradeAmount())) {
                        	if ((hisCheck.contains(tmpHisList.get(j).getOriCheckState())?"短款":"长款").equals("短款") 
                        			&& (hisCheck.contains(tmpHisList.get(i).getOriCheckState())?"短款":"长款").equals("长款")) {
                        		if (!"".equals(tmpHisList.get(j).getBusinessNo())) {
                        			repeatList.add(tmpHisList.get(j).getBusinessNo());//把相同元素加入list(找出相同的)
                                    tmpHisList.remove(j);//删除重复元素
        						}
        					}else if((hisCheck.contains(tmpHisList.get(j).getOriCheckState())?"短款":"长款").equals("长款") 
                        			&& (hisCheck.contains(tmpHisList.get(i).getOriCheckState())?"短款":"长款").equals("短款")) {
        						if (!"".equals(tmpHisList.get(j).getBusinessNo())) {
                        			repeatList.add(tmpHisList.get(j).getBusinessNo());//把相同元素加入list(找出相同的)
                                    tmpHisList.remove(j);//删除重复元素
        						}
        					}
                        }
                    }
                }
                for (int i = 0, len = dataList.size(); i < len; i++) {
                    Row dataRow = ee.getRow(sheet, i + 2, null, height);
                    colMap = dataList.get(i);
                    // 支付方式流水号
                    ee.getCell(dataRow, 0, borderStyle, colMap.getBusinessNo());
                    // 商户流水号
                    Cell cell1 = dataRow.createCell(1);
                    cell1.setCellStyle(borderStyle);
                    cell1.setCellValue(colMap.getShopFlowNo());
                    // HIS流水号
                    Cell cell2 = dataRow.createCell(2);
                    cell2.setCellStyle(borderStyle);
                    cell2.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? colMap.getHisFlowNo():patIdMap.get(colMap.getBusinessNo()).getHisFlowNo());
                    // 病人ID
                    Cell cell3 = dataRow.createCell(3);
                    cell3.setCellStyle(borderStyle);
                    cell3.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? null:patIdMap.get(colMap.getBusinessNo()).getPatId());
                    // 患者姓名
                    Cell cell4 = dataRow.createCell(4);
                    cell4.setCellStyle(borderStyle);
                    cell4.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? colMap.getPatientName():patIdMap.get(colMap.getBusinessNo()).getCustName());
                    // 交易类型
                    Cell cell5 = dataRow.createCell(5);
                    cell5.setCellStyle(borderStyle);
                    cell5.setCellValue(EnumTypeOfInt.getByCode(colMap.getTradeName()).getCode());
                    // 交易金额
                    Cell cell6 = dataRow.createCell(6);
                    cell6.setCellStyle(doubleStyle);
                    cell6.setCellValue(Double.parseDouble(getNotNullStr(colMap.getTradeAmount())));
                    
                    // 交易时间
                    Cell cell7 = dataRow.createCell(7);
                    cell7.setCellStyle(doubleStyle);
                    SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    cell7.setCellValue(f.format(colMap.getTradeTime()));
                    // 支付类型
                    Cell cell8 = dataRow.createCell(8);
                    cell8.setCellStyle(doubleStyle);
                    cell8.setCellValue(PayTypeEnum.getByCode(colMap.getPayName()).getName());
                    
                    // 渠道名称
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
                    
                    // 异常类型
                    Cell cell10 = dataRow.createCell(10);
                    cell10.setCellStyle(borderStyle);
                    cell10.setCellValue(hisCheck.contains(colMap.getOriCheckState()) ? "短款" : "长款");
                    
                    // 当前状态
                    Cell cell11 = dataRow.createCell(11);
                    cell11.setCellStyle(doubleStyle);
                    cell11.setCellValue(colMap.getCheckStateValue());
                    // 原因
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
    	 * 获取账单来源
    	 * @param tradeName 交易类型
    	 * @param oriCheckState 长短款状态
    	 * @param channelName 渠道名称
    	 * @return
    	 */
    	private String getBillSource(String tradeName, String oriCheckState,String channelName) {
    		if (tradeName.equals("退费") && oriCheckState.equals("短款")) {
    			return channelName;
    		}
    		if (tradeName.equals("退费") && oriCheckState.equals("长款")) {
    			return "HIS";
    		}
    		if (tradeName.equals("缴费") && oriCheckState.equals("短款")) {
    			return "HIS";
    		}
    		if (tradeName.equals("缴费") && oriCheckState.equals("长款")) {
    			return channelName;
    		}
    		return null;
    	}

    	/**
    	 *  获取备注
    	 * @param repeatList 
    	 * @param tradeCheckFollow 
    	 * @param tradeName 交易类型
    	 * @param oriCheckState 长短款状态
    	 * @return
    	 */
    	private String getRemark(TradeCheckFollow tradeCheckFollow, List<String> repeatList) {
    		String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
    		String tradeName = EnumTypeOfInt.getByCode(tradeCheckFollow.getTradeName()).getCode();
    		String oriCheckState = hisCheck.contains(tradeCheckFollow.getOriCheckState())?"短款":"长款";
    		String remark = null;
    		if (tradeName.equals("退费") && oriCheckState.equals("短款")) {
    			remark = "商户已退，HIS未退";
    		}
    		if (tradeName.equals("退费") && oriCheckState.equals("长款")) {
    			remark = "商户未退，HIS已退";
    		}
    		if (tradeName.equals("缴费") && oriCheckState.equals("短款")) {
    			remark = "商户未缴，HIS已缴";
    		}
    		if (tradeName.equals("缴费") && oriCheckState.equals("长款")) {
    			remark = "商户已缴，HIS未缴";
    		}
    		if (repeatList.contains(tradeCheckFollow.getBusinessNo())&&StringUtils.isNotBlank(tradeCheckFollow.getBusinessNo())) {
    			return remark + "，已对冲";
    		}
    		return remark;
    	}
    }
}
