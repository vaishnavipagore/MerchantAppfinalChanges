package com.example.merchantapp.Initializepojo;

import com.google.gson.annotations.SerializedName;

public class InitiliseApiRequest{

	@SerializedName("F061")
	private String f061;

	@SerializedName("F041")
	private String f041;

	@SerializedName("F042")
	private String f042;

	@SerializedName("MsgType")
	private String msgType;

	@SerializedName("F047")
	private String f047;

	public void setF061(String f061){
		this.f061 = f061;
	}

	public String getF061(){
		return f061;
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

	public void setMsgType(String msgType){
		this.msgType = msgType;
	}

	public String getMsgType(){
		return msgType;
	}

	public void setF047(String f047){
		this.f047 = f047;
	}

	public String getF047(){
		return f047;
	}
}