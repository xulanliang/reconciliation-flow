package com.yiban.rec.netty.entry;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.yiban.rec.netty.entity.Command;
import com.yiban.rec.netty.factory.MessageClientPipelineFactory;
import com.yiban.rec.netty.handle.MessageClientHandler;
import com.yiban.rec.util.Configure;

/**
 * 
 * @ClassName: MessageClient
 * @Description: 客服端启动类
 * @author chuntu tuchun168@163.com
 * @date 2016年5月3日 上午11:26:42
 *
 */
public class MessageClient {

	public static void main(String[] args) throws Exception {
		Command command = new Command();
		command.setData("心跳命令");
		sendMsg(command);
	}

	public static void sendMsg(Command command) {

		// Parse options.
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new MessageClientPipelineFactory(new MessageClientHandler(command)));
		// Start the connection attempt.
		String ip = Configure.getPropertyBykey("netty.ip");
		int port = Integer.parseInt(Configure.getPropertyBykey("netty.port"));
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(ip, port));
		// Wait until the connection is closed or the connection attempt fails.
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		// Shut down thread pools to exit.
		bootstrap.releaseExternalResources();
	}
}