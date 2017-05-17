package com.server;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.common.MsgType;
import com.common.Msg.Msg;
import com.common.Tools.MsgFactory;
import com.common.Tools.configUtils;
import com.server.Tools.LogUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter{
	  private  final  Logger logger = LogManager.getLogger(HeartBeatServerHandler.class.getName());
	  private volatile int HeartBeatCount=0; 
	  private Msg heartBeatMsg,HeartPongMsg,connMSG;
//	  private AttributeKey<String> pNameKey=AttributeKey.valueOf("pName");
	  public HeartBeatServerHandler(){
		    String type=configUtils.getHeartBeatMsgArgs();
		  	heartBeatMsg=MsgFactory.createMsg(type);
		  	type=configUtils.getHeartPongMsgArgs();
		  	HeartPongMsg=MsgFactory.createMsg(type);
		  	type=configUtils.getConnectMsgArgs();
		  	connMSG=MsgFactory.createMsg(type);
		     
	  }
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		  super.channelActive(ctx);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg)throws Exception{
		 this.HeartBeatCount=0;
		Msg tMsg=(Msg)msg;
		switch (tMsg.getmType()) {
		case connect:{
			 logger.info(ctx.channel().remoteAddress()+" Linked");
			
		}break;
		case heartBeat:{
			 ReferenceCountUtil.release(msg);  
			 ctx.writeAndFlush(HeartPongMsg);
		}break;
		case Quit:{
			 ctx.close();
		}break;
		default: 
			ctx.fireChannelRead(msg);
			break;
		}
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	//	String clientPName=ctx.channel().attr(pNameKey).get();
		if(HeartBeatCount<=3){
			LogUtils.QuitDealLog(logger,ctx.channel().remoteAddress(), MsgType.Quit);
		}else{
			LogUtils.QuitDealLog(logger,ctx.channel().remoteAddress(), MsgType.heartDeadQuit);
		}
		super.channelInactive(ctx);
	}
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		  if (evt instanceof IdleStateEvent) {  
	            IdleState state = ((IdleStateEvent) evt).state();  
	            if (state == IdleState.ALL_IDLE) {  
	            	if(++HeartBeatCount<=3)
	                ctx.writeAndFlush(heartBeatMsg);  
	            	else {
	            	//	String clientPName=ctx.channel().attr(pNameKey).get();
	            		ctx.close();
	            	}
	            }  
	        } else {  
	            super.userEventTriggered(ctx, evt);  
	        }
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		ctx.close();
	}
}
