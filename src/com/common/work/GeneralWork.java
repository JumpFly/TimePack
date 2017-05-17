package com.common.work;

import java.util.Set;

public class GeneralWork {
	public GeneralWork(){}
	protected String sender=null;
	protected String msgData=null;
	protected String send_time=null;
//	protected Set<String> receivers;
	/*if groupMsg==true ,receivers[0]=someone chatGroup
		else receiver[] means some user's name*/
	protected boolean groupMsg;
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMsgData() {
		return msgData;
	}
	public void setMsgData(String msgData) {
		this.msgData = msgData;
	}
	public String getSend_time() {
		return send_time;
	}
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}
//	public Set<String> getReceivers() {
//		return receivers;
//	}
//	public void setReceivers(Set<String> receivers) {
//		this.receivers = receivers;
//	}
	public boolean isGroupMsg() {
		return groupMsg;
	}
	public void setGroupMsg(boolean groupMsg) {
		this.groupMsg = groupMsg;
	}
	
}
