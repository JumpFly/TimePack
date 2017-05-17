package com.common.Tools;

import com.alibaba.fastjson.JSON;
import com.common.MsgType;
import com.common.Msg.ConnectMsg;
import com.common.Msg.HeartBeatMsg;
import com.common.Msg.HeartPongMsg;
import com.common.Msg.Msg;
import com.common.Msg.WorkMsg;
import com.common.work.GeneralWork;

public class MsgFactory {
  public static Msg createMsg(String type){
	  Msg msg=null;
	  switch (MsgType.valueOf(type)) {
	  case connect:
		  msg=ConnectMsg.getInstance();
		  break;
	  case heartBeat:
		  msg=HeartBeatMsg.getInstance();
		  break;
	  case heartPong:
		  msg=HeartPongMsg.getInstance();
		  break;

	  default:
		break;
	  }
	   
	  return msg;
  }
  public static WorkMsg createWorkMsg(String type,GeneralWork work){
	  return createWorkMsg(type, work, 0);
	  
  }
  public static WorkMsg createWorkMsg(String type,GeneralWork work,int delay){
	  WorkMsg msg=new WorkMsg();
		  msg.setmType(MsgType.valueOf(type));
		  msg.setDataClass(work.getClass());
		  msg.setData(JSON.toJSONString(work));
		  msg.setDelay(delay);
	  return msg;
	  
  }
  public static Msg JudgeMsg(byte[] buf,Msg msg){
	    Msg mm=msg;
		switch (msg.getmType()) {
		case sendEmail:
		case sendChat:
		case createChat:
			mm=JSON.parseObject(buf, WorkMsg.class);
			break;
		default:
			break;
		}
		
		return mm;
  }
}
