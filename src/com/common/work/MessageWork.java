package com.common.work;

public class MessageWork  extends GeneralWork{
	public MessageWork(){
		setGroupMsg(true);
	}
	private int messageId;

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	@Override
	public String toString() {
		return "MessageWork [messageId=" + messageId + ", sender=" + sender + ", msgData=" + msgData + ", send_time="
				+ send_time + ", groupMsg=" + groupMsg + "]";
	}
	
}
