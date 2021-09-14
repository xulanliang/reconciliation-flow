package com.yiban.rec.netty.common;

import java.nio.ByteBuffer;

public interface IOutput {
	/**
	 * 将对象转换成buffer
	 * @param buffer
	 */
	void objectToBuffer(ByteBuffer buffer);
	
	
	/**
	 * 计算数据部份的字节大小
	 * @return
	 */
	int calculateByteSize();
}
