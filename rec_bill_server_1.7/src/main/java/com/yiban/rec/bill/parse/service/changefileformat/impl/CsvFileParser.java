package com.yiban.rec.bill.parse.service.changefileformat.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yiban.rec.bill.parse.service.changefileformat.FileParserable;

public class CsvFileParser implements FileParserable {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private String charset = "UTF-16";
	
	public CsvFileParser() {
	}

	public CsvFileParser(String charset) {
		this.charset = charset;
	}
	
	@Override
	public List<String> fileToList(File file) {
		List<String> strList = null;
		if (file.isFile() && file.exists()) {
			log.info("######准备读取CSV账单文件########");
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,charset);
				BufferedReader bufferdReader=new BufferedReader(inputStreamReader);
				String line = "";
				strList = new ArrayList<>();
				while((line=bufferdReader.readLine())!=null){
					strList.add(line);
				}
				bufferdReader.close();
				fileInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("########## 返回账单数据量为："+strList.size());
		}else {
			log.error("#######系统找不到指定账单文件#########");
		}
		return strList;
	}

	public static void main(String[] args) {
		CsvFileParser csvFileParser = new CsvFileParser();
		List<String> list = csvFileParser
				.fileToList(new File("D:\\AggregateData\\20190327\\微信当日交易_20190327.csv"));
		System.out.println(list);
		for (String s : list) {
			String arr[] = s.replaceAll("'", "").split("\t");
			for (int i = 0, len = arr.length; i < len; i++) {
				System.out.println(i + "=" + arr[i]);
			}
		}
	}

}
