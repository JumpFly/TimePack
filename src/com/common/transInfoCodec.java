package com.common;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.common.Msg.Msg;
import com.common.Msg.WorkMsg;
import com.common.Tools.MsgFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

public class transInfoCodec extends  MessageToMessageCodec<ByteBuf, Msg>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Msg msg, List<Object> out) throws Exception {
		byte[] jsonbytes=JSON.toJSONBytes(msg);
		byte[] $_bytes="$_".getBytes();
		ByteBuf buf1=Unpooled.buffer(jsonbytes.length+$_bytes.length);
		buf1.writeBytes(jsonbytes);
		buf1.writeBytes($_bytes);
		out.add(buf1);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msgBuf, List<Object> out) throws Exception {
		byte[] bys=new byte[msgBuf.readableBytes()]; 
		msgBuf.readBytes(bys);
		Msg mm=JSON.parseObject(bys, Msg.class);
// 		String queueName=null,funName=null;
		mm=MsgFactory.JudgeMsg(bys, mm);
// 		System.out.println(queueName+","+funName+","+mm);
 		out.add(mm);
		
	} 
}