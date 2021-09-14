package com.yiban.rec.web.admin;

import java.io.OutputStream;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.WindowCash;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.WindowCashCheckVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.WindowCashCheckService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.web.reports.UnusualBillController;

import net.sf.json.JSONObject;

/**
 * @Description
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-02-20 15:18
 */
@Controller
@RequestMapping(value = "admin/window")
public class WindowCashCheckController extends CurrentUserContoller {

    @Autowired
    private HospitalConfigService hospitalConfigService;
    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private GatherService gatherService;
    @Autowired
    private WindowCashCheckService windowCashCheckService;
    @Autowired
    private OrganizationService organizationService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
    private final int height = 18;

    /**
     * 窗口现金核对
     *
     * @param model
     * @param orgNo
     * @param date
     * @param Order_State
     * @return
     */
    @RequestMapping("/index")
    public String index(ModelMap model, String orgNo, String date, String Order_State) {
        AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
        model.put("typesJSON", JSONObject.fromObject(ValueTexts.asMap(metaDataService.valueAsList())));
        model.put("orgJSON", JSONObject.fromObject(gatherService.getOrgMap()));
        model.put("org", EnumType.DATA_SOURCE_TYPE_ORG_CHANNEL.getValue());
        model.put("accountDate", DateUtil.getCurrentDateString());
        model.put("flag", CommonConstant.ALL_ID);
        model.put("hConfig", hConfig);
        if (orgNo != null) {
            model.put("orgCode", orgNo);
        } else {
        	String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
            model.put("orgCode", orgCode);
        }
        model.put("orderState", Order_State);
        if (StringUtils.isNotEmpty(Order_State)) {
            model.put("date", DateUtil.getSpecifiedDayBeforeMonth(DateUtil.getCurrentDate(), 3));
        }
        model.put("isDisplay", StringUtils.isNotBlank(hConfig.getIsDisplay()) ? hConfig.getIsDisplay() : 0);
        return autoView("reconciliation/windowCashCheck");
    }

    @RestController
    @RequestMapping({"/admin/window/data"})
    class WindowCashDataController extends BaseController {
        /**
         * 窗口现金核对表单数据
         *
         * @param windowCashCheckVo
         * @return
         */
        @GetMapping("/cashCheckData")
        public WebUiPage<Map<String, Object>> getCashCheckData(WindowCashCheckVo windowCashCheckVo) {
            List<Organization> orgList = organizationService.findByParentCode(windowCashCheckVo.getOrgCode());
            Page cashCheckData = windowCashCheckService.getCashCheckData(windowCashCheckVo, orgList, this.getRequestPageabledWithInitSort(this.getIdDescSort()));
            return toWebUIPage(cashCheckData);
        }

        /**
         * 导出报表
         *
         * @param windowCashCheckVo
         * @param fileName
         * @param workSheetName
         * @param request
         * @param response
         * @throws Exception
         */
        @RequestMapping(value = "/exportData")
        public void exportData(WindowCashCheckVo windowCashCheckVo, String fileName, String workSheetName,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {
            List<Organization> orgList = organizationService.findByParentCode(windowCashCheckVo.getOrgCode());
            Page cashCheckData = windowCashCheckService.getCashCheckData(windowCashCheckVo, orgList, this.getRequestPageabledWithInitSort(this.getIdDescSort()));
            List<WindowCash> dataMap = cashCheckData.getContent();
            JSONObject orgJson = JSONObject.fromObject(gatherService.getOrgMap());
            commonExportExcel(fileName, workSheetName, request, response, dataMap, orgJson);
        }

        /**
         * 新增窗口现金核对数据
         *
         * @param windowCashCheckVo
         * @return
         */
        @PostMapping("/saveCashCheckData")
        public ResponseResult saveCashCheckData(WindowCashCheckVo windowCashCheckVo) {
            User currentUser = currentUser();
            String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
            windowCashCheckVo.setOrgCode(orgCode.trim());
            windowCashCheckService.saveCashCheckData(windowCashCheckVo, currentUser);
            return ResponseResult.success().data("");
        }

        /**
         * 异常单通过操作
         *
         * @param windowCashCheckVo
         * @return
         */
        @GetMapping("/updateCashCheckDataState")
        public ResponseResult updateCashCheckDataState(WindowCashCheckVo windowCashCheckVo) {
            User currentUser = currentUser();
            windowCashCheckService.updateCashCheckDataState(windowCashCheckVo, currentUser);
            return ResponseResult.success();
        }

        public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
//                                      HttpServletResponse response, Map<String, Object> dataList) throws Exception {
//                                      HttpServletResponse response, List<WindowCash> dataList, List<MetaData> metaDataList) throws Exception {
                                      HttpServletResponse response, List<WindowCash> dataList, JSONObject orgJson) throws Exception {
            UnusualBillController unusualBillController = new UnusualBillController();
            unusualBillController.setResponseAdRequest(request, response, fileName);
            ExportExcel ee = new ExportExcel();
            // workbook对应一个Excel
            HSSFWorkbook wb = new HSSFWorkbook();
            // 定义一个统一字体样式:、居中,边框
            HSSFCellStyle fontStyle = wb.createCellStyle();
            fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            unusualBillController.setBorderStyle(fontStyle);
            HSSFCellStyle borderStyle = wb.createCellStyle();
            unusualBillController.setBorderStyle(borderStyle);
            // 创建一个sheet
            Sheet sheet = ee.getSheet(wb, workSheetName);
            // 第一行标题, 合并单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
            Row titleRow = ee.getRow(sheet, 0, null, height);
            ee.getCell(titleRow, 0, fontStyle, fileName);
            // 创建等多的单元格，解决合并单元格的问题
            for (int i = 1; i < 7; i++) {
                ee.getCell(titleRow, i, fontStyle, "");
            }
            // 第三行标题、业务类型
            Row thirdTitleRow = ee.getRow(sheet, 1, null, height);
            String[] titleArray = {"机构名称", "存款日期", "应存金额（元）", "实存金额（元）", "垫付金额（元）", "存款人", "类型", "状态"};
            for (int i = 0, len = titleArray.length; i < len; i++) {
                ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
            }
            if (dataList.size() >= 1) {
                WindowCash colMap = null;
                CellStyle doubleStyle = wb.createCellStyle();
                DataFormat df = wb.createDataFormat();
                doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
                unusualBillController.setBorderStyle(doubleStyle);
                for (int i = 0, len = dataList.size(); i < len; i++) {
                    Row dataRow = ee.getRow(sheet, i + 2, null, height);
                    colMap = dataList.get(i);
                    // 机构名称
                    String orgCode = colMap.getOrgCode();
                    String name = orgJson.getString(orgCode);
                    ee.getCell(dataRow, 0, borderStyle, name);
                    // 存款日期
                    Cell cell1 = dataRow.createCell(1);
                    cell1.setCellStyle(borderStyle);
                    cell1.setCellValue(DateUtil.transferDateToString("yyyy-MM-dd HH:mm:ss", colMap.getCashDate()));
                    // 应存金额
                    Cell cell2 = dataRow.createCell(2);
                    cell2.setCellStyle(doubleStyle);
                    cell2.setCellValue(colMap.getHisAmount().toString());
                    // 实存金额
                    Cell cell3 = dataRow.createCell(3);
                    cell3.setCellStyle(borderStyle);
                    cell3.setCellValue(colMap.getChannelAmount().toString());
                    // 垫付金额
                    Cell cell4 = dataRow.createCell(4);
                    cell4.setCellStyle(doubleStyle);
                    cell4.setCellValue(colMap.getExceptionalAmount().toString());
                    // 存款人
                    Cell cell5 = dataRow.createCell(5);
                    cell5.setCellStyle(borderStyle);
                    cell5.setCellValue(colMap.getCashierAccount());
                    // 类型
                    Cell cell6 = dataRow.createCell(6);
                    cell6.setCellStyle(doubleStyle);
                    cell6.setCellValue(EnumTypeOfInt.getByCode(colMap.getBusinessType()).getCode());
                    // 状态
                    Cell cell7 = dataRow.createCell(7);
                    cell7.setCellStyle(borderStyle);
                    String state = colMap.getCashStatus();
                    cell7.setCellValue(CommonEnum.CashCheckState.getByCode(state).getText().toString());
                }
            }
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
            out.close();
        }
    }
}
