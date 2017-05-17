package com.common.Msg;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.MsgType;

public final class HeartBeatMsg extends Msg{
     private HeartBeatMsg(){
    	 setmType(MsgType.heartBeat);
     }
     @JSONField(serialize=false)
     private static HeartBeatMsg heartBeatMsg=new HeartBeatMsg();
     public static HeartBeatMsg getInstance(){
  	   return heartBeatMsg;
     }
}
