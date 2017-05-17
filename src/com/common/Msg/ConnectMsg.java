package com.common.Msg;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.MsgType;

public final class ConnectMsg extends Msg{
    private ConnectMsg(){
   	 setmType(MsgType.connect);
    }
    @JSONField(serialize=false)
    private static ConnectMsg connectMsg=new ConnectMsg();
    public static ConnectMsg getInstance(){
 	   return connectMsg;
    }
}