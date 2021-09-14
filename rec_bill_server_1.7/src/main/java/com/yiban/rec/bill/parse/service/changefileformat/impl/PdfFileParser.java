package com.yiban.rec.bill.parse.service.changefileformat.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.yiban.rec.bill.parse.service.changefileformat.FileParserable;

/**
 * @author swing
 * @date 2018年7月26日 上午11:01:29 类说明 pdf格式文件解析器
 */
public class PdfFileParser implements FileParserable {

	/**
	 * 解析pdf文件，返回字符串列表
	 */
	@Override
	public List<String> fileToList(File file) {
		String content = null;
		try {
			PDDocument document = PDDocument.load(file);
			// 获取页码
			int pages = document.getNumberOfPages();
			// 读文本内容
			PDFTextStripper stripper = new PDFTextStripper();
			// 设置按顺序输出
			stripper.setSortByPosition(true);
			stripper.setStartPage(1);
			stripper.setEndPage(pages);
			content = stripper.getText(document);
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> list = new ArrayList<>(100);
		if (StringUtils.isNotEmpty(content)) {
			String[] lines = content.split("\n");
			return Arrays.asList(lines);
		}
		return list;
	}
	

	public static void main(String[] args) throws IOException {
		String pathFile = "D:\\myz\\工作\\文档\\新乡中心医院\\账单\\41210101401190423100025.pdf";
		List<String> list = new PdfFileParser().fileToList(new File(pathFile));
		System.out.println(list);

	}
	
	
}
