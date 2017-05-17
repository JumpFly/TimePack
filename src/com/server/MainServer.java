package com.server;


import java.util.concurrent.TimeUnit;
import com.common.transInfoCodec;
import com.common.Tools.configUtils;
import com.server.Tools.DealWorkQueueRun;
import com.server.Tools.PollQueueRun;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class MainServer {

	public MainServer( ){
		
	}
	public void bind(int port) throws Exception{
		
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try {
			
			ServerBootstrap sBootstrap=new ServerBootstrap();
			sBootstrap.group(bossGroup, workerGroup)
					  .channel(NioServerSocketChannel.class)
					  .option(ChannelOption.SO_BACKLOG, 1024)
					  .childHandler(new ChildChannelHandler());
			
			ChannelFuture cFuture=sBootstrap.bind(port).sync();
			
			cFuture.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			ChannelPipeline pipeline=arg0.pipeline();
			ByteBuf delimiter=Unpooled.copiedBuffer("$_".getBytes());
			pipeline.addLast(new IdleStateHandler(0, 0, 35, TimeUnit.MINUTES));
			pipeline.addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
			pipeline.addLast(new transInfoCodec());
			pipeline.addLast(new HeartBeatServerHandler());
			pipeline.addLast(new WorkMsgsServerHandler());
		}
		
	}
 
	public static void main(String[] args) throws Exception {
		Thread tt=new Thread(new DealWorkQueueRun());
		tt.start();
		Thread tt2=new Thread(new PollQueueRun());
		tt2.start();
		
		int port=Integer.parseInt(configUtils.getServerPort());
		new MainServer().bind(port);
	}
	 
}
