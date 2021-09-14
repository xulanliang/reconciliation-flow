package com.yiban.rec.netty.handle;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.yiban.rec.netty.common.GlobalConstValue;
import com.yiban.rec.netty.entity.Command;
import com.yiban.rec.netty.util.StringEncode;

public class CommandDecoder extends FrameDecoder {
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

		if (buf.readableBytes() < GlobalConstValue.FRM_BASIC_LENGTH) {
			return null;
		}
		// 防止socket字节流攻击
		if (buf.readableBytes() > 10240) {
			buf.skipBytes(buf.readableBytes());
		}
		// 记录包头开始的index
		int beginReader = buf.readerIndex();

		int dataLen = buf.readInt();
		// 判断请求数据包数据是否到齐
		if (buf.readableBytes() < dataLen) {
			// 还原指针
			buf.readerIndex(beginReader);
			return null;
		}
		// 读data数据
		byte[] data = new byte[dataLen];
		buf.readBytes(data);
		String dataStr = new String(data, StringEncode.UTF8.getEncoder());

		Command command = new Command();
		command.setData(dataStr);

		return command;

	}
}
