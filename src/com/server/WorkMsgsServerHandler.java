package com.server;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.common.MsgType;
import com.common.Msg.Msg;
import com.common.Msg.WorkMsg;
import com.server.Tools.LogUtils;
import com.server.Tools.WorkUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class WorkMsgsServerHandler extends ChannelInboundHandlerAdapter{
	  private  final  Logger logger = LogManager.getLogger(WorkMsgsServerHandler.class.getName());
	  public WorkMsgsServerHandler(){}
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			  super.channelActive(ctx);
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx,Object msg)throws Exception{
			Msg tMsg=(Msg)msg;
			switch (tMsg.getmType()) {
			case sendEmail:
			case sendChat:
			case createChat:{
				WorkMsg workM=(WorkMsg)msg;
				String dealFunName=WorkUtils.callBackFun(tMsg.getmType());
				String queueName=tMsg.getmType().toString();
				int delayTime=workM.getDelay();
				if(delayTime>60*60){
				//	�������ݿ� ��������Ҫ��ΪdataString ����SHA-1���ж��Ƿ��Ѵ��ڣ���->�������ݿ� ��
					System.out.println("暂不处理");
				}else {
					WorkUtils.execute_later(queueName,dealFunName,workM.getData(),workM.getDataClass(),workM.getDelay());
				}
			}break;
			
			default: 
				ctx.fireChannelRead(msg);
				break;
			}
		}
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			
			super.channelInactive(ctx);
		}

}
