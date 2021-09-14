package com.yiban.rec.netty.handle;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.yiban.rec.netty.entity.Command;
import com.yiban.rec.netty.util.CommUtil;
import com.yiban.rec.netty.util.StringEncode;

/**
 * 
 * @ClassName: CommandEncoder
 * @Description: 编码器
 * @author chuntu tuchun168@163.com
 * @date 2016年5月3日 上午11:27:23
 *
 */
public class CommandEncoder extends SimpleChannelHandler {
	private static final Logger logger = Logger.getLogger(CommandEncoder.class);

	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) {
		try {
			Command command = (Command) e.getMessage();
			ChannelBuffer buf = ChannelBuffers.buffer(command.statBytesCounts());

			String data = command.getData();
			CommUtil.putArrTypeFieldNINT(data, buf, StringEncode.UTF8);

			Channels.write(ctx, e.getFuture(), buf);
			logger.info("server Send command:" + " command length>>" + command.statBytesCounts() + " data>>" + data);

		} catch (Exception e2) {
			e2.printStackTrace();
			logger.error("编码异常" + e.getMessage(), e2);
		}

	}
}
