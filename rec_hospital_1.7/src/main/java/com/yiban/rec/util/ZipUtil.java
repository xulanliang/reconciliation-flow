package com.yiban.rec.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ZipUtil {

	/**
	 * 
	 * @param zipFilePath
	 *            压缩文件名
	 * @param chartSet
	 *            字符编码
	 * @return 文件字符串
	 * @throws Exception
	 *             重新抛出异常
	 */
	public static String unZipFiles(String zipFilePath, String chartSet) throws Exception {
		ZipFile zipFile = null;
		try {
			File f = new File(zipFilePath);
			if ((!f.exists()) && (f.length() <= 0)) {
				throw new RuntimeException("要解压的文件不存在!");
			}
			// 一定要加上编码，之前解压另外一个文件，没有加上编码导致不能解压
			zipFile = new ZipFile(f, chartSet);
			Enumeration<ZipEntry> e = zipFile.getEntries();
			StringBuilder sb = new StringBuilder();
			while (e.hasMoreElements()) {
				// 使用ant.jar 的解压缩工具包代替jdk自带的
				org.apache.tools.zip.ZipEntry zipEnt = e.nextElement();
				if (!zipEnt.isDirectory()) {
					// 读写文件
					InputStream is = zipFile.getInputStream(zipEnt);
					BufferedInputStream bis = new BufferedInputStream(is);
					List<String> list = IOUtils.readLines(bis, Charset.forName(chartSet));
					for (String str : list) {
						sb.append(str);
					}
					bis.close();
					is.close();
				}
			}
			return sb.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
		}
	}

}
