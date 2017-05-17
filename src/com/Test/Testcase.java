package com.Test;



import org.dom4j.io.SAXReader;
import org.junit.Test; 
import com.alibaba.fastjson.JSON;
import com.common.Msg.Msg;
import com.common.MsgType;
import com.common.Msg.*;
import com.common.Tools.MsgFactory;
import com.common.Tools.configUtils;
import com.common.work.EmailWork;
import com.common.work.GeneralWork;
import com.server.Tools.RedisUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.*;


public class Testcase {
	@Test
	public static void testWork(){
		
		
	}
	
	@Test
	public  void testSetnx() {
		
		 
	}
	@Test
	public void testLog() {
		Logger logger=LogManager.getLogger(Testcase.class.getName());
		System.out.println("hello World"+new Date());
		logger.info("hello World");
	}
	@Test
	public void test5() {
		
		Msg heartBeatMsg,HeartPongMsg,connMSG;
		 String type=configUtils.getHeartBeatMsgArgs();
		  	heartBeatMsg=MsgFactory.createMsg(type);
		  	type=configUtils.getHeartPongMsgArgs();
		  	HeartPongMsg=MsgFactory.createMsg(type);
		  	type=configUtils.getConnectMsgArgs();
		  	connMSG=MsgFactory.createMsg(type);
		  	
		  	String s1=configUtils.getServerAddr();
		  	String s2=configUtils.getServerPort();
		  	System.out.println(heartBeatMsg.getmType());
		  	System.out.println(HeartPongMsg.getmType());
		  	System.out.println(connMSG.getmType());
		  	System.out.println(s1);
		  	System.out.println(s2);
	}
	
	@Test
	public void test4() {
		  try {
		 		File file=new File("config.xml");
		 		SAXReader reader = new SAXReader();
				Document doc=reader.read(file);
				Element root = doc.getRootElement();
				Element foo ,foo2;
				foo=root.element("MsgArgs");
				List<Element> nodes=foo.elements();
				for(int j=0;j<nodes.size();j++){
					foo2=nodes.get(j);
					System.out.println(foo2.getName());
					System.out.println(foo2.getText());
				}
				 
		 	} catch (Exception e) {
		 		e.printStackTrace();
		 	}
	}
	@Test
	public void test() {
		HeartBeatMsg heartBeatMsg=HeartBeatMsg.getInstance();
		String st1=JSON.toJSONString(heartBeatMsg);
		System.out.println(st1);
		
		HeartBeatMsg heartBeatMsg2=JSON.parseObject(st1, HeartBeatMsg.class);
		System.out.println(heartBeatMsg2.getmType());
		
	}
	@Test
	public void tese2(){
		HeartBeatMsg heartBeatMsg=HeartBeatMsg.getInstance();
		String st1=toJsonString(heartBeatMsg);
		ByteBuf msgbuf=Unpooled.buffer();
		msgbuf.writeInt(1);
		msgbuf.writeBytes(st1.getBytes());
		parseObject(msgbuf);
		
	}
	@Test
	public void tese3(){
	   WorkMsg workMsg=new WorkMsg();
	   workMsg.setData("hello");
	   workMsg.setDelay(50);
	   workMsg.setmType(MsgType.sendEmail);
		String st1=toJsonString(workMsg);
		ByteBuf msgbuf=Unpooled.buffer();
		msgbuf.writeBytes(st1.getBytes());
		parseObject2(msgbuf);
		
	}
	
	 
 	private String toJsonString(Msg msg) {
		return JSON.toJSONString(msg);
	}
	public void parseObject(ByteBuf msg){
 		System.out.println(msg.readableBytes());
 		int msgTypeOrdinal=msg.readInt();
 		byte[] bys=new byte[msg.readableBytes()]; 
 		System.out.println(msg.readableBytes());
 		msg.readBytes(bys);
 		System.out.println(new String(bys));
 		Msg mm=JSON.parseObject(bys, Msg.class);
 		System.out.println(mm);
 
	} 
 	public void parseObject2(ByteBuf msg){
 		byte[] bys=new byte[msg.readableBytes()]; 
 		msg.readBytes(bys);
 		Msg mm=JSON.parseObject(bys, Msg.class);
 		System.out.println(mm);
 		String queueName=null,funName=null;
 		switch (mm.getmType()) {
		case sendEmail:
			mm=JSON.parseObject(bys, WorkMsg.class);
			funName="deal"+mm.getmType();
			queueName=mm.getmType().toString();
			break;
		default:
			break;
		}
 		System.out.println(queueName+","+funName+","+mm);
 	} 
}
