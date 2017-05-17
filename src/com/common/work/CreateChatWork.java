package com.common.work;

import java.util.Set;

public class CreateChatWork extends GeneralWork{
	private Set<String> receivers=null;
	public CreateChatWork(){
		setGroupMsg(true);
	}
	public Set<String> getReceivers() {
		return receivers;
	}
	public void setReceivers(Set<String> receivers) {
		this.receivers = receivers;
	}

}
