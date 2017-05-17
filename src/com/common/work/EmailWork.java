package com.common.work;

import java.util.Set;

public final class EmailWork extends GeneralWork{
	public EmailWork(){}
	private String filePath;
	private Set<String> receivers;
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Set<String> getReceivers() {
		return receivers;
	}
	public void setReceivers(Set<String> receivers) {
		this.receivers = receivers;
	}
	@Override
	public String toString() {
		return "EmailWork [filePath=" + filePath + ", sender=" + sender + ", msgData=" + msgData + ", send_time="
				+ send_time + ", receivers=" + receivers + ", groupMsg=" + groupMsg + "]";
	}

	
	
}
