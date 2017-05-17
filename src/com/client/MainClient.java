package com.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.common.transInfoCodec;
import com.common.Msg.Msg;
import com.common.Tools.MsgFactory;
import com.common.Tools.configUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class MainClient {
	
	private  volatile  int ReLinkCount=0;
	private volatile boolean ReLinked=false;
	private EventLoopGroup elgroup;
	private Bootstrap bootstrap;
	private ScheduledExecutorService executorService=Executors.newScheduledThreadPool(1);
	private boolean heartOn=false;
	
	public boolean isReLinked() {
		return ReLinked;
	}

	public void setReLinked(boolean reLinked) {
		ReLinked = reLinked;
	}
	public MainClient(boolean heartOn){
		this.heartOn=heartOn;
	}
	
	public void connect(int port,String host) throws Exception{
		
		 elgroup=new NioEventLoopGroup();
		try {
		    bootstrap=new Bootstrap();
			bootstrap.group(elgroup).channel(NioSocketChannel.class)
					 .option(ChannelOption.TCP_NODELAY, true)
					 .handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel arg0) throws Exception {
							ChannelPipeline pipeline=arg0.pipeline();
							ByteBuf delimiter=Unpooled.copiedBuffer("$_".getBytes());
							if(heartOn){
								pipeline.addLast(new IdleStateHandler(0, 0, 30, TimeUnit.MINUTES));
							}
							pipeline.addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
							pipeline.addLast(new transInfoCodec());
							pipeline.addLast(new HeartBeatClientHandler());
							pipeline.addLast(new WorkMsgsClientHandler());
						}
					});
			ChannelFuture cFuture=bootstrap.connect(host,port).sync();
			cFuture.channel().closeFuture().sync();
		} finally {
			elgroup.shutdownGracefully();
			if(!isReLinked()){
				return;
			}
			if(++ReLinkCount<10)
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						try {
							TimeUnit.SECONDS.sleep(10);
							connect(port, host);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				});
			else {
				System.out.println("重连10次失败,Client Quit");
			}
			
		}
	}
	private class HeartBeatClientHandler extends  SimpleChannelInboundHandler<Msg>{
		  private volatile int HeartBeatCount=0; 
		  private Msg heartBeatMsg,HeartPongMsg,connMSG;
		  public HeartBeatClientHandler(){
			    String type=configUtils.getHeartBeatMsgArgs();
			  	heartBeatMsg=MsgFactory.createMsg(type);
			  	type=configUtils.getHeartPongMsgArgs();
			  	HeartPongMsg=MsgFactory.createMsg(type);
			  	type=configUtils.getConnectMsgArgs();
			  	connMSG=MsgFactory.createMsg(type);
			
		  }
		    @Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
		    	ReLinkCount=0;//重连次数重置
		    	setReLinked(false);//断开重连标志重置
			    System.out.println("激活时间是："+new Date());  
			    ctx.writeAndFlush(connMSG);
				super.channelActive(ctx);
				
			}
			@Override
			protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
				 this.HeartBeatCount=0;
				switch (msg.getmType()) {
				case heartBeat:{
					 System.out.println("Receive a Heartbeat from server");
					 ReferenceCountUtil.release(msg);  
					 if(heartOn){
						 ctx.writeAndFlush(HeartPongMsg); 
				    	  System.out.println("Pong a  Heartbeat to server");
					 }
						
				}break;
				default: 
					ctx.fireChannelRead(msg);
					break;
				}
				 
			}
			@Override
			public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
				  if (evt instanceof IdleStateEvent) {  
			            IdleState state = ((IdleStateEvent) evt).state();  
			            if (state == IdleState.ALL_IDLE) {  
			            	if(++HeartBeatCount<=3)
			                ctx.writeAndFlush(heartBeatMsg);  
			            	else {
			            		setReLinked(true);
			            		ctx.close();
			            	}
			            }  
			        } else {  
			            super.userEventTriggered(ctx, evt);  
			        }
				
			}
			@Override
			public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("与服务器断开："+new Date());  
				super.channelInactive(ctx);
			}
		}
	
			public static void main(String[] args) throws Exception {
				int port=Integer.parseInt(configUtils.getServerPort());
				String serverAddr=configUtils.getServerAddr();
				Scanner scanner=new Scanner(System.in);
				System.out.println("是否启动心跳应答？：(1=yes, 0=no)");
				int heartFlag=scanner.nextInt();
				boolean heartOn=false;
				if(heartFlag>=1)
					heartOn=true;
				scanner.close();
				new MainClient(heartOn).connect(port, serverAddr);
			}
	}