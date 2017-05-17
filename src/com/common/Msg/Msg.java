package com.common.Msg;

import com.common.MsgType;

public  class Msg {
	public Msg(){}
	private  MsgType mType;

	public MsgType getmType() {
		return mType;
	}
	public void setmType(MsgType mType) {
		this.mType = mType;
	}
	public String toString() {
	 return mType+"";
	}
}
