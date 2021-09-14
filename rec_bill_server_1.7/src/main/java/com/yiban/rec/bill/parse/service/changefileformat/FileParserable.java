package com.yiban.rec.bill.parse.service.changefileformat;

import java.io.File;
import java.util.List;

/**
 * @author swing
 * @date 2018年7月26日 上午10:59:32 类说明 文件解析接口
 */
public interface FileParserable {
	/**
	 * 解析接口，返回字符串列表
	 * 
	 * @param file
	 * @return
	 */
	List<String> fileToList(File file);
}
