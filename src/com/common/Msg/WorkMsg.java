package com.common.Msg;


public final class WorkMsg extends Msg{
	  public WorkMsg(){}
	  private String data;
	  private int delay;
	  private Class dataClass;
	  public String getData() {
			return data;
	  }
	  public void setData(String data) {
			this.data = data;
	  }
	  public int getDelay() {
		return delay;
	  }
	  public void setDelay(int delay) {
			this.delay = delay;
	  }
	  public Class getDataClass() {
			return dataClass;
	  }
	  public void setDataClass(Class dataClass) {
			this.dataClass = dataClass;
	  }
	@Override
	public String toString() {
		return "WorkMsg [data=" + data+ ", delay=" + delay + ", dataClass=" + dataClass + "]";
	}
	  
}
