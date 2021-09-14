package com.yiban.rec.util;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

public class ExportDataUtil {
	
	private int height;
	private int lastCol;
//	String[] secondTitleArray = { "", "微信", "支付宝", "银联", "现金", "聚合支付", "医保", "" };
//	int[] secondTitleIndexArray = { 0, 1, 2, 5, 7, 9, 11, 12 };
	private String[] thirdTitleArray;
	private String[] cellValue;
	
	private String[] sumValue;
	private String[] amountValue;
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getLastCol() {
		return lastCol;
	}

	public void setLastCol(int lastCol) {
		this.lastCol = lastCol;
	}
	
	public String[] getThirdTitleArray() {
		return thirdTitleArray;
	}

	public void setThirdTitleArray(String[] thirdTitleArray) {
		this.thirdTitleArray = thirdTitleArray;
	}

	public String[] getCellValue() {
		return cellValue;
	}

	public void setCellValue(String[] cellValue) {
		this.cellValue = cellValue;
	}
	
	public ExportDataUtil() {
		this.height = 18;
		this.lastCol = 12;
	}
	public ExportDataUtil(int height,int lastCol,String[] thirdTitleArray,String[] cellValue) {
		this.height = height;
		this.lastCol = lastCol;
		this.thirdTitleArray=thirdTitleArray;
		this.cellValue=cellValue;
	}
	
	public ExportDataUtil(int height,int lastCol,String[] thirdTitleArray,String[] cellValue,String[] amountValue,String[] sumValue) {
		this.height = height;
		this.lastCol = lastCol;
		this.thirdTitleArray=thirdTitleArray;
		this.cellValue=cellValue;
		this.amountValue=amountValue;
		this.sumValue=sumValue;
	}

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

	public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
			HttpServletResponse response, List<Map<String, Object>> dataList) throws Exception {
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

		// 创建一个sheet
		Sheet sheet = ee.getSheet(wb, workSheetName);
		// 第一行标题, 合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCol));
		Row titleRow = ee.getRow(sheet, 0, null, height);
		ee.getCell(titleRow, 0, fontStyle, fileName);
		// 创建等多的单元格，解决合并单元格的问题
		for (int i = 1; i < lastCol+1; i++) {
			ee.getCell(titleRow, i, fontStyle, "");
		}

		// 第二行标题、支付类型
		/*for (int i = 0; i < lastCol/2; i++) {
			sheet.addMergedRegion(new CellRangeAddress(1, 1, i*2+1, i*2+2));
		}
		
		Row secondTitleRow = ee.getRow(sheet, 1, null, height);
		for (int i = 0, len = secondTitleArray.length; i < len; i++) {
			ee.getCell(secondTitleRow, secondTitleIndexArray[i], fontStyle, secondTitleArray[i]);
		}*/

		// 第三行标题、业务类型
		Row thirdTitleRow = ee.getRow(sheet, 1, null, height);
		for (int i = 0, len = thirdTitleArray.length; i < len; i++) {
			if (i == len - 1) {
				sheet.setColumnWidth(i, thirdTitleArray[i].getBytes().length * 200);
			}
			ee.getCell(thirdTitleRow, i, fontStyle, thirdTitleArray[i]);
		}

		if (dataList.size() > 0) {
			Map<String, Object> colMap = null;
			CellStyle doubleStyle = wb.createCellStyle();
			DataFormat df = wb.createDataFormat();
			doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
			setBorderStyle(doubleStyle);
			for (int i = 0, len = dataList.size(); i < len; i++) {

				Row dataRow = ee.getRow(sheet, i + 2, null, height);
				colMap = dataList.get(i);
				//数据封装
				/*for (int j = 0; j < cellValue.length; j++) {
					ee.getCell(dataRow, j, borderStyle,colMap.get(cellValue[j]) == null? "":colMap.get(cellValue[j]).toString());

					Cell cell = dataRow.createCell(j);
					cell.setCellStyle(borderStyle);
					cell.setCellValue(colMap.get(cellValue[j]) == null? "":colMap.get(cellValue[j]).toString());
				}*/
				if((i<=dataList.size()-3)||(sumValue==null&&amountValue==null)) {//减3的意思是合计多了2个map,正式map集合应该减去2,,应为从0开始所以在加1
					for (int j = 0; j < cellValue.length; j++) {
						ee.getCell(dataRow, j, borderStyle,colMap.get(cellValue[j]) == null? "":colMap.get(cellValue[j]).toString());

						Cell cell = dataRow.createCell(j);
						cell.setCellStyle(borderStyle);
						cell.setCellValue(colMap.get(cellValue[j]) == null? "":colMap.get(cellValue[j]).toString());
					}
				}else {
					//合并单元格
					sheet.addMergedRegion(new CellRangeAddress(i+2, i+2, 0, lastCol));
					//合计等循环
					if(sumValue!=null&&amountValue!=null) {
						String amountName="";
						for(int j = 0; j < amountValue.length; j++) {
							if(StringUtils.isNotBlank(amountName)) {
								amountName=amountName+","+colMap.get(amountValue[j]).toString();
							}else {
								amountName=colMap.get(amountValue[j]) == null? "":colMap.get(amountValue[j]).toString();
							}
						}
						String name="";
						for(int j = 0; j < sumValue.length; j++) {
							if(StringUtils.isNotBlank(name)) {
								name=name+","+colMap.get(sumValue[j]).toString();
							}else {
								name=colMap.get(sumValue[j]) == null? "":colMap.get(sumValue[j]).toString();
							}
						}
						if(StringUtils.isNotBlank(name)) {
							ee.getCell(dataRow, 0, null,name);
						}
						if(StringUtils.isNotBlank(amountName)) {
							ee.getCell(dataRow, 0, null,amountName);
						}
						
					}
				}
			}
		}
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
	}
}
