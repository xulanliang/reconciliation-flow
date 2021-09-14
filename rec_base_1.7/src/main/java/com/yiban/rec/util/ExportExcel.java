package com.yiban.rec.util;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 导出Excel
 * @author Administrator
 *
 */
public class ExportExcel {
	//获取一个单元格
	public Cell getCell(Row row,int column,CellStyle cellStyle,String value){
		Cell cell = null ;
		if(row!=null){
			cell = row.createCell(column);
			if(cellStyle!=null){
				cell.setCellStyle(cellStyle);
			}
			//cell.setCellValue(new HSSFRichTextString(value));
			cell.setCellValue(value);
		}
		return cell ;
	}
	/**
	 * 获取行元素
	 * @param sheet
	 * @param rownum
	 * @param cellStyle
	 * @param height 2012-08-10宽度为2500
	 * @return
	 */
	public Row getRow(Sheet sheet,int rownum,CellStyle cellStyle,int height){
		Row row = null ;
		if(sheet!=null){
			row = sheet.createRow(rownum);
			if(cellStyle!=null){
				row.setRowStyle(cellStyle);
			}
			row.setHeightInPoints(height);
		}
		return row ;
	}
	
	public Sheet getSheet(Workbook wb,String sheetName){
		Sheet sheet = null ;
		if(wb!=null){
			sheet = wb.createSheet(sheetName);
		}
		return sheet ;
	}
	
	public CellStyle getCellStyle(Workbook wb,Font font,short alignment,short verticalment,short bottom,short left,short right,short top){
		CellStyle cellStyle = null ;
		if(wb!=null){
			cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(alignment);
			cellStyle.setVerticalAlignment(verticalment);
			
			cellStyle.setBorderBottom(bottom);
			cellStyle.setBorderLeft(left);
			cellStyle.setBorderRight(right);
			cellStyle.setBorderTop(top);
			
			if(font!=null){
				cellStyle.setFont(font);
			}
		}
		return cellStyle;
	} 
	
	public Font getFont(Workbook wb,String fontName,short fontPoints,boolean isItalic,boolean isStrikeout){
		Font font = null ;
		if(wb!=null){
			font = wb.createFont();
			font.setFontHeightInPoints(fontPoints);
			font.setFontName(fontName);
			font.setItalic(isItalic);//斜体
			font.setStrikeout(isStrikeout);//中划线
		}
		return font ;
	}
	
	public void test01() throws IOException{
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = this.getSheet(wb, "每日对账汇总");
		
		//标题样式
		CellStyle cellStyle = this.getCellStyle(wb, this.getFont(wb, "Courier New", (short)18, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
		Row titleRow = this.getRow(sheet, 0, cellStyle, 30);
		this.getCell(titleRow, 0, cellStyle, "医院端交易明细");
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,5));
		
		//头部样式
		CellStyle cellStyleHead = this.getCellStyle(wb, this.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
		Row headRow = this.getRow(sheet, 1, cellStyleHead, 20);
		
		this.getCell(headRow, 0, cellStyleHead,"日期");
		this.getCell(headRow, 1, cellStyleHead,"卡号");
		this.getCell(headRow, 2, cellStyleHead,"交易类型");
		this.getCell(headRow, 3, cellStyleHead,"银行流水号");
		this.getCell(headRow, 4, cellStyleHead,"金额");
		this.getCell(headRow, 5, cellStyleHead,"帐平标识");
		
		//sheet.autoSizeColumn(0);
		//sheet.autoSizeColumn(1);
		//sheet.autoSizeColumn(2);
		//sheet.autoSizeColumn(3);
		//sheet.autoSizeColumn(4);
		//sheet.autoSizeColumn(5);
		
		sheet.setColumnWidth(0, 2500);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 3000);
		sheet.setColumnWidth(3, 3600);
		sheet.setColumnWidth(4, 2500);
		sheet.setColumnWidth(5, 3000);
		FileOutputStream fos = new FileOutputStream("c:\\c.xls");
		wb.write(fos);
		fos.close();
		
	}
	
	public static void main(String[] args) throws Exception {
		new ExportExcel().test01();
		/*Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("对账");
		sheet.setColumnWidth(1, 3700);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(50);
		row.createCell(0).setCellValue("哈哈哈哈");
		CreationHelper createHelper = wb.getCreationHelper();
		row.createCell(1).setCellValue(createHelper.createRichTextString("什么情况啊啊"));
		FileOutputStream fos = new FileOutputStream("c:\\a.xls");
		wb.write(fos);
		fos.close();*/
	}
}
