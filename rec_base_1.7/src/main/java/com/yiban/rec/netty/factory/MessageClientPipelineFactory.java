package com.yiban.rec.netty.factory;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.yiban.rec.netty.handle.CommandDecoder;
import com.yiban.rec.netty.handle.CommandEncoder;

/**
 * 
 * @ClassName: MessageClientPipelineFactory
 * @Description: 工厂
 * @author chuntu tuchun168@163.com
 * @date 2016年5月3日 上午11:27:00
 *
 */
public class MessageClientPipelineFactory implements ChannelPipelineFactory {
	private SimpleChannelUpstreamHandler obj;
	public MessageClientPipelineFactory(SimpleChannelUpstreamHandler obj){
		this.obj=obj;
	}
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();

		pipeline.addLast("decoder", new CommandDecoder());
		pipeline.addLast("encoder", new CommandEncoder());
		pipeline.addLast("handler", obj);

		return pipeline;
	}
}