package com.yiban.rec.bill.parse.service.changefileformat.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yiban.rec.bill.parse.service.changefileformat.FileParserable;

public class ExcelFileParser implements FileParserable {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<String> fileToList(File file) {
		List<String> strList = null;
		// 判断文件是否存在
		if (file.isFile() && file.exists()) {
			Workbook wb = null; // Excel文件
			try (FileInputStream fis = new FileInputStream(file)) {

				String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
				// 根据文件后缀（xls/xlsx）进行判断
				if ("xls".equals(suffix)) {
					wb = new HSSFWorkbook(fis);
//					wb = new XSSFWorkbook(fis);
				} else if ("xlsx".equals(suffix)||"csv".equals(suffix)) {
					wb = new XSSFWorkbook(fis);
				} else {
					log.error("文件格式不正确");
					return strList;
				}

				// 开始解析
				Sheet sheet = wb.getSheetAt(0); // 读取sheet 0
				int firstRowIndex = sheet.getFirstRowNum();
				int lastRowIndex = sheet.getLastRowNum();

				StringBuilder builder = null;
				strList = new ArrayList<String>();
				// 遍历行
				for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {
					Row row = sheet.getRow(rIndex);
					if (row != null) {
						int firstCellIndex = row.getFirstCellNum();
						int lastCellIndex = row.getLastCellNum();

						builder = new StringBuilder();
						// 遍历列
						for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {
							Cell cell = row.getCell(cIndex);
							// 最后一列不需要加\t
							if (cIndex != lastCellIndex - 1) {
								builder.append(cell == null ? " " : cell.toString()).append("\t");
							} else {
								builder.append(cell == null ? " " : cell.toString());
							}
						}

						if (builder.length() > 0) {
							strList.add(builder.toString());
						}
					}
				}
			} catch (Exception e) {
				log.error("解析Excel文件异常：" + e);
			} finally {
				if (wb != null) {
					try {
						wb.close();
					} catch (IOException e) {
						log.error("关闭Excel文件：" + e);
					}
				}
			}
		}
		return strList;
	}
}
