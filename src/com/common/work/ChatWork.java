package com.common.work;


public final class ChatWork extends GeneralWork{
	private long chatId;
//	private Set<String> receivers;
	 public ChatWork() {
		 setGroupMsg(true);
	 }
//	public Set<String> getReceivers() {
//			return receivers;
//	}
//	public void setReceivers(Set<String> receivers) {
//			this.receivers = receivers;
//	}
	
	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		this.chatId = chatId;
	}

	@Override
	public String toString() {
		return "ChatWork [chatId=" + chatId + ", sender=" + sender + ", msgData=" + msgData + ", send_time=" + send_time
				 + ", groupMsg=" + groupMsg + "]";
	}

}
