package com.common.Msg;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.MsgType;

public final class HeartPongMsg extends Msg{
   private HeartPongMsg(){
	   setmType(MsgType.heartPong);
   }
   @JSONField(serialize=false)
   private static HeartPongMsg heartPongMsg=new HeartPongMsg();
   public static HeartPongMsg getInstance(){
	   return heartPongMsg;
   }
}
