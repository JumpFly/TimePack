package com.server;

public class QueueBean {
	public QueueBean(){}
	private	String callBackFun;
	private Class dataClass;
	private String data;
	public String getCallBackFun() {
		return callBackFun;
	}
	public void setCallBackFun(String callBackFun) {
		this.callBackFun = callBackFun;
	}
	public Class getDataClass() {
		return dataClass;
	}
	public void setDataClass(Class dataClass) {
		this.dataClass = dataClass;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
