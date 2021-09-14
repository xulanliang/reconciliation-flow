package com.yiban.rec.bill.parse.service.changefileformat.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.yiban.rec.bill.parse.service.changefileformat.FileParserable;

/**
 * 普通的文本文件解析
 * 
 * @author clearofchina
 *
 */
public class TextFileParser implements FileParserable {

	private String charset = "UTF-8";

	public TextFileParser() {
	}

	public TextFileParser(String charset) {
		this.charset = charset;
	}

	@Override
	public List<String> fileToList(File file) {
		List<String> fileList = new ArrayList<String>();

		if (file.exists() && file.isFile()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
				String line = "";
				while ((line = reader.readLine()) != null) {
					fileList.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return fileList;
	}

	public String fileToString(File file) {
		StringBuilder builder = new StringBuilder();

		if (file.exists() && file.isFile()) {
			BufferedReader reader = null;
			FileInputStream is = null;
			try {
				is = new FileInputStream(file);
				reader = new BufferedReader(new InputStreamReader(is, charset));
				String line = "";
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return String.valueOf(builder);
	}

	public static void main(String[] args) {
		String filePath = "D:\\myz\\工作\\文档\\阳新县人民医院\\银行卡账单\\CCB20190520105420280620003.txt";
		List<String> fileList = new TextFileParser("GB2312").fileToList(new File(filePath));
		String[] lines = null;
		for (String line : fileList) {
			if (StringUtils.isEmpty(line))
				continue;
			lines = line.split("\\|");
			String title[] = "银行卡号|  |交易类型|金额|参考号|流水号|  |  | |".split("\\|");
			for (int i = 0, len = lines.length; i < len; i++) {
				System.out.println(i + "-" + title[i] + ":" + lines[i]);
			}
		}

	}
}
