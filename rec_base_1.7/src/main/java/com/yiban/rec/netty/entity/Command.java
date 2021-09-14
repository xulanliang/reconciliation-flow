package com.yiban.rec.netty.entity;

import com.yiban.rec.netty.util.StringEncode;
import com.yiban.rec.netty.util.StringUtil;

/**
 * 
 * @ClassName: Command
 * @Description: 命令结构
 * @author chuntu tuchun168@163.com
 * @date 2016年5月3日 上午11:26:22
 *
 */
public class Command {
	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Command [data=" + data + "]";
	}

	public int statBytesCounts() {
		int rslt = 0;
		rslt += StringUtil.getStringProtocolLenINT(data, StringEncode.UTF8);
		return rslt;
	}

}
