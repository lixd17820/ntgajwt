package com.ntga.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BkPicMesBean implements Serializable {

	private String mesId;
	private String picId;
	private String title;
	private String bksj;

	public String getMesId() {
		return mesId;
	}

	public void setMesId(String mesId) {
		this.mesId = mesId;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBksj() {
		return bksj;
	}

	public void setBksj(String bksj) {
		this.bksj = bksj;
	}

	@Override
	public String toString() {
		String s = "内容：" + title + "\n" + bksj;
		return s;
	}

}
