package com.yiban.rec.bill.parse.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;






public class PdfUtil {

	/**
	 * 解析pdf文件，返回字符串列表
	 */
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
			stripper.setWordSeparator(",");
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
	
	/*public List<String> tt(String pathFile) {
		PDDocument document = null;
		try {
			document = PDDocument.load(pathFile);
			if(document.isEncrypted()) {
				try {
					document.decrypt("");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition( true );
			Rectangle rect = new Rectangle( 0, 0, 800, 2000);
			stripper.addRegion("class1",rect);
			List allPages = document.getDocumentCatalog().getAllPages();
			PDPage firstPage = (PDPage)allPages.get( 0 );
			stripper.extractRegions( firstPage );
			System.out.println( "Text in the area:" + rect );
			System.out.println( stripper.getTextForRegion( "class1" ) );
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if( document != null ){
				try {
					document.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}*/
	
	
	/**
     * 
     * @param filePath  地址
     * @param rows  第几行开始
     * @param lastRow  后几行不要
     * @param str   要过滤的字段
     * @return
     */
	public List<Map<String,String>> analysis(String filePath,int rows,int lastRow,String str){
		//解析pdf得到list
		List<String> list = fileToList(new File(filePath));
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for(int i=rows-1;i<list.size()-lastRow;i++) {
			int num=0;
			Map<String,String> map = new LinkedHashMap<String,String>();
			//单行数据
			String data = list.get(i);
			//过滤的字段
			if(StringUtils.isNotBlank(data)&&data.contains(str)) {
				continue;
			}
			String[] datas = data.split(",");
			for(String v:datas) {
				map.put(String.valueOf(num), v);
				num++;
			}
			mapList.add(map);
		}
		return mapList;
	}

	public static void main(String[] args) throws IOException {
		String pathFile = "D:\\billfile\\2019\\05\\07\\test3.pdf";
		List<Map<String, String>> list = new PdfUtil().analysis(pathFile, 5,11, "小计");
		System.out.println(list);

	}
}
