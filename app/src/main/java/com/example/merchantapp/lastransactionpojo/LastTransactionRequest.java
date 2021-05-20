package com.example.merchantapp.lastransactionpojo;

import com.google.gson.annotations.SerializedName;

public class LastTransactionRequest{

	@SerializedName("F062")
	private String f062;

	@SerializedName("F041")
	private String f041;

	@SerializedName("F042")
	private String f042;

	@SerializedName("F011")
	private String f011;

	@SerializedName("MsgType")
	private String msgType;

	@SerializedName("F003")
	private String f003;

	public void setF062(String f062){
		this.f062 = f062;
	}

	public String getF062(){
		return f062;
	}

	public void setF041(String f041){
		this.f041 = f041;
	}

	public String getF041(){
		return f041;
	}

	public void setF042(String f042){
		this.f042 = f042;
	}

	public String getF042(){
		return f042;
	}

	public void setF011(String f011){
		this.f011 = f011;
	}

	public String getF011(){
		return f011;
	}

	public void setMsgType(String msgType){
		this.msgType = msgType;
	}

	public String getMsgType(){
		return msgType;
	}

	public void setF003(String f003){
		this.f003 = f003;
	}

	public String getF003(){
		return f003;
	}
}