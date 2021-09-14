package com.yiban.rec.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExportMergedUtils {
  public void addMergedRegion(Sheet sheet, int[] columns) {
    List<Map<Integer, Object>> body = getBody(sheet);
    List<CellRangeAddress> list = getCellRangeAddressList(body, columns, 0, body.size(), 0);
    for (CellRangeAddress cellRangeAddress : list)
      sheet.addMergedRegion(cellRangeAddress); 
  }
  
  public List<CellRangeAddress> getCellRangeAddressList(List<Map<Integer, Object>> body, int[] columns, int start, int end, int column) {
    int columnVal = columns[column];
    List<CellRangeAddress> list = new ArrayList<>();
    int a = start, b = end, c = columnVal, d = columnVal;
    for (int i = start; i < end; i++) {
      if (i < end - 1) {
        if (!((Map)body.get(i)).get(Integer.valueOf(columnVal)).toString().equals(((Map)body.get(i + 1)).get(Integer.valueOf(columnVal)).toString())) {
          b = i;
          if (a != b) {
            list.add(new CellRangeAddress(a + 2, b + 2, c, d));
            list.addAll(getCellRangeAddressList(body, columns, a, b + 1, column + 1));
          } 
          a = i + 1;
        } 
      } else {
        b = i;
        if (a != b) {
          list.add(new CellRangeAddress(a + 2, b + 2, c, d));
          list.addAll(getCellRangeAddressList(body, columns, a, b + 1, column + 1));
        } 
      } 
    } 
    return list;
  }
  
  public List<Map<Integer, Object>> getBody(Sheet sheet) {
    List<Map<Integer, Object>> all = new ArrayList<>();
    List<CellRangeAddress> cras = getCombineCell(sheet);
    List<Map<String, Object>> irs = new ArrayList<>();
    int count = sheet.getLastRowNum() + 1;
    for (int i = 2; i < count; i++) {
      Row row = sheet.getRow(i);
      Map<String, Object> map1 = new HashMap<>();
      List<Map<Integer, Object>> items = new ArrayList<>();
      if (isMergedRegion(sheet, i, 0)) {
        int lastRow = getRowNum(cras, sheet.getRow(i).getCell(0), sheet);
        for (; i <= lastRow; i++) {
          row = sheet.getRow(i);
          int cellNum = row.getLastCellNum();
          Map<Integer, Object> map2 = new HashMap<>();
          for (int ce = 0; ce < cellNum; ce++)
            map2.put(Integer.valueOf(ce), getCellValue(row.getCell(ce))); 
          items.add(map2);
        } 
        i--;
      } else {
        row = sheet.getRow(i);
        int cellNum = row.getLastCellNum();
        Map<Integer, Object> map3 = new HashMap<>();
        for (int ce = 0; ce < cellNum; ce++)
          map3.put(Integer.valueOf(ce), getCellValue(row.getCell(ce))); 
        items.add(map3);
      } 
      map1.put("cell", items);
      irs.add(map1);
    } 
    for (int a = 0; a < irs.size(); a++) {
      List<Map<Integer, Object>> list = (ArrayList)((Map)irs.get(a)).get("cell");
      if (list.size() > 0)
        for (int b = 0; b < list.size(); b++)
          all.add(list.get(b));  
    } 
    return all;
  }
  
  public List<CellRangeAddress> getCombineCell(Sheet sheet) {
    List<CellRangeAddress> list = new ArrayList<>();
    int sheetmergerCount = sheet.getNumMergedRegions();
    for (int i = 0; i < sheetmergerCount; i++) {
      CellRangeAddress ca = sheet.getMergedRegion(i);
      list.add(ca);
    } 
    return list;
  }
  
  private boolean isMergedRegion(Sheet sheet, int row, int column) {
    int sheetMergeCount = sheet.getNumMergedRegions();
    for (int i = 0; i < sheetMergeCount; i++) {
      CellRangeAddress range = sheet.getMergedRegion(i);
      int firstColumn = range.getFirstColumn();
      int lastColumn = range.getLastColumn();
      int firstRow = range.getFirstRow();
      int lastRow = range.getLastRow();
      if (row >= firstRow && row <= lastRow && 
        column >= firstColumn && column <= lastColumn)
        return true; 
    } 
    return false;
  }
  
  private int getRowNum(List<CellRangeAddress> listCombineCell, Cell cell, Sheet sheet) {
    int xr = 0;
    int firstC = 0;
    int lastC = 0;
    int firstR = 0;
    int lastR = 0;
    for (CellRangeAddress ca : listCombineCell) {
      firstC = ca.getFirstColumn();
      lastC = ca.getLastColumn();
      firstR = ca.getFirstRow();
      lastR = ca.getLastRow();
      if (cell.getRowIndex() >= firstR && cell.getRowIndex() <= lastR && 
        cell.getColumnIndex() >= firstC && cell.getColumnIndex() <= lastC)
        xr = lastR; 
    } 
    return xr;
  }
  
  public String getCellValue(Cell cell) {
    if (cell == null)
      return ""; 
    if (cell.getCellType() == 1)
      return cell.getStringCellValue(); 
    return "";
  }
}

