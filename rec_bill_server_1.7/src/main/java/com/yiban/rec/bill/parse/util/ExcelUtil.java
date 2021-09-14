package com.yiban.rec.bill.parse.util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

    
    /**
     * 
     * @param filePath  地址
     * @param rows  第几行开始
     * @param line  第几列开始
     * @param str   要过滤的字段
     * @return
     */
    public List<Map<String,String>> analysis(String filePath,int rows,int line,String str){
        Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String,String>> list = null;
        String cellData = null;
        wb = readExcel(filePath);
        if(wb != null){
            //用来存放表中数据
            list = new ArrayList<Map<String,String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(rows);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = rows-1; i<=rownum; i++) {
            	int num=0;
                Map<String,String> map = new LinkedHashMap<String,String>();
                row = sheet.getRow(i);
                if(row !=null){
                	//过滤定义的列
                	cellData = (String) getCellFormatValue(row.getCell(line-1));
                	if(StringUtils.isNotBlank(cellData)&&cellData.contains(str)) {
                    	continue;
                    }
                    for (int j=line-1;j<=colnum;j++){
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        if(StringUtils.isBlank(cellData)) {
                        	continue;
                        }
                        map.put(String.valueOf(num), cellData);
                        num++;
                    }
                }else{
                    break;
                }
                list.add(map);
            }
        }
		return list;
    }
    
    /**  
     * 判断合并了行  
     * @param sheet  
     * @param row  
     * @param column  
     * @return  
     */  
     private boolean isMergedRow(Sheet sheet,int row ,int column) {  
       
       int sheetMergeCount = sheet.getNumMergedRegions();  
       for (int i = 0; i < sheetMergeCount; i++) {  
         CellRangeAddress range = sheet.getMergedRegion(i);  
         int firstColumn = range.getFirstColumn();  
         int lastColumn = range.getLastColumn();  
         int firstRow = range.getFirstRow();  
         int lastRow = range.getLastRow();  
         if(row == firstRow && row == lastRow){  
             if(column >= firstColumn && column <= lastColumn){  
                 return true;  
             }  
         }  
       }  
       return false;  
     }  
    
    
    //读取excel
    public static Workbook readExcel(String filePath){
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }
    public static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC:{
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            }
            case Cell.CELL_TYPE_FORMULA:{
                //判断cell是否为日期格式
                if(DateUtil.isCellDateFormatted(cell)){
                    //转换为日期格式YYYY-mm-dd
                    cellValue = cell.getDateCellValue();
                }else{
                    //数字
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            }
            case Cell.CELL_TYPE_STRING:{
                cellValue = cell.getRichStringCellValue().getString();
                break;
            }
            default:
                cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }

}
