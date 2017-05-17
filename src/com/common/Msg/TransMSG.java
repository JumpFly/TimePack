package com.common.Msg;

public class TransMSG extends Msg{
	private  byte[] data;
	public TransMSG(){}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "[ "+super.getmType()+":"+new String(data)+" ]";
	}
 
}
