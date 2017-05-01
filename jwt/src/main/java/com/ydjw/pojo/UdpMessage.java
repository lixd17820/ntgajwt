package com.ydjw.pojo;

import java.io.Serializable;

public class UdpMessage implements Serializable {
	private int id;
	private String sender;
	private String recive;
	private String message;
	private String recRiqi;
	private int fsbj;// 发送标记，0接收，1发送
	private int ydbj;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecive() {
		return recive;
	}

	public void setRecive(String recive) {
		this.recive = recive;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecRiqi() {
		return recRiqi;
	}

	public void setRecRiqi(String recRiqi) {
		this.recRiqi = recRiqi;
	}

	public int getFsbj() {
		return fsbj;
	}

	public void setFsbj(int fsbj) {
		this.fsbj = fsbj;
	}

	public int getYdbj() {
		return ydbj;
	}

	public void setYdbj(int ydbj) {
		this.ydbj = ydbj;
	}

}
