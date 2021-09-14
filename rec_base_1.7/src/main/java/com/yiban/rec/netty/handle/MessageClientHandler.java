package com.yiban.rec.netty.handle;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.yiban.rec.netty.entity.Command;

public class MessageClientHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = Logger.getLogger(MessageClientHandler.class.getName());
	private Command command;
	public MessageClientHandler(Command command) {
		this.command=command;
	}
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		/*Command command = new Command();
		command.setHead("*Q");
		command.setCmd("1");
		command.setData("心跳命令");*/
		// command.setDataLength((short)(command.statBytesCounts()));

		e.getChannel().write(command);

		// e.getChannel().close();
	}

	public ChannelFuture process(Channel channel, ByteBuffer requestParameter) {
		return channel.write(requestParameter);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		try {
			// Send back the received message to the remote peer.
			System.out.println("=======================>messageReceived send message " + e.getMessage());
			Command result = (Command) e.getMessage();
			System.out.println(result.getData());
		} catch (Exception ex) {
			ex.printStackTrace();
			e.getChannel().close();
		}
		e.getChannel().close();
		// e.getChannel().write(e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		// Close the connection when an exception is raised.
		logger.log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}
}