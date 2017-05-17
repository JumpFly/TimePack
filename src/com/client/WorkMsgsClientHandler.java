package com.client;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.common.MsgType;
import com.common.Msg.Msg;
import com.common.Tools.MsgFactory;
import com.common.work.ChatWork;
import com.common.work.CreateChatWork;
import com.common.work.EmailWork;
import com.common.work.GeneralWork;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class WorkMsgsClientHandler extends  SimpleChannelInboundHandler<Msg>{
	private  final  Logger logger = LogManager.getLogger(WorkMsgsClientHandler.class.getName());
	
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		CreateChatWork work=new CreateChatWork();
		work.setSender("jump");
		work.setMsgData("helloWorld");
		work.setGroupMsg(false);
		work.setSend_time(new Date().toString());
		Set<String> receivers=new HashSet<>();
		receivers.add("Barry");
		receivers.add("John");
		work.setReceivers(receivers);
 		Msg msg=MsgFactory.createWorkMsg(MsgType.createChat.toString(), work,20);
//		
//		GeneralWork work=new ChatWork();
//		work.setSender("jump");
//		work.setMsgData("helloWorld");
//		work.setGroupMsg(false);
//		work.setSend_time(new Date().toString());
//		Msg msg=MsgFactory.createWorkMsg(MsgType.sendChat.toString(), work,20);
		 
		EmailWork work2=new EmailWork();
		work2.setSender("Lee");
		work2.setMsgData("helloWorld");
		work2.setGroupMsg(false);
		Set<String> rec2 = new HashSet<String>();
		 rec2.add("Marry");
		work2.setReceivers(rec2);
		work2.setSend_time(new Date().toString());
		Msg msg2=MsgFactory.createWorkMsg(MsgType.sendEmail.toString(), work2,60);
		ctx.writeAndFlush(msg);
 		ctx.writeAndFlush(msg2);
		super.channelActive(ctx);
	}
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Msg arg1) throws Exception {
		/**
		 * do something
		 **/ 
		 
	}

}
