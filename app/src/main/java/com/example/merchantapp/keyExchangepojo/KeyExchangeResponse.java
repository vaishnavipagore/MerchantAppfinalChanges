package com.example.merchantapp.keyExchangepojo;

import com.google.gson.annotations.SerializedName;

public class KeyExchangeResponse{

	@SerializedName("F039")
	private String f039;

	@SerializedName("F041")
	private String f041;

	@SerializedName("MsgType")
	private String msgType;

	@SerializedName("F003")
	private String f003;

	@SerializedName("F048")
	private String f048;

	public void setF039(String f039){
		this.f039 = f039;
	}

	public String getF039(){
		return f039;
	}

	public void setF041(String f041){
		this.f041 = f041;
	}

	public String getF041(){
		return f041;
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

	public void setF048(String f048){
		this.f048 = f048;
	}

	public String getF048(){
		return f048;
	}
}