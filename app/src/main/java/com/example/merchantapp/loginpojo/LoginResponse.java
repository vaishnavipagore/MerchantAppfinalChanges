package com.example.merchantapp.loginpojo;

import com.google.gson.annotations.SerializedName;

public class LoginResponse{

	@SerializedName("msg")
	private String msg;

	@SerializedName("mid")
	private String mid;

	@SerializedName("tid")
	private String tid;

	public void setMsg(String msg){
		this.msg = msg;
	}

	public String getMsg(){
		return msg;
	}

	public void setMid(String mid){
		this.mid = mid;
	}

	public String getMid(){
		return mid;
	}

	public void setTid(String tid){
		this.tid = tid;
	}

	public String getTid(){
		return tid;
	}
}