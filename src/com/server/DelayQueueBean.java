package com.server;

public class DelayQueueBean extends QueueBean{
	public DelayQueueBean(){}
	private String identifier;
	private String queueName;
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
}
