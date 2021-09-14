package com.yiban.rec.netty.common;

import java.nio.ByteBuffer;

public interface IInput {
	/**
	 * 将buffer转换成Object
	 * @param buffer
	 */
	void bufferToObject(ByteBuffer buffer);
}
