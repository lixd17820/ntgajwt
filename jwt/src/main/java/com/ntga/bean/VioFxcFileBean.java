package com.ntga.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VioFxcFileBean implements Serializable{

	private String id;
	private String fxcId;
	private String wjdz;
	private String scbj;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFxcId() {
		return fxcId;
	}

	public void setFxcId(String fxcId) {
		this.fxcId = fxcId;
	}

	public String getWjdz() {
		return wjdz;
	}

	public void setWjdz(String wjdz) {
		this.wjdz = wjdz;
	}

	public String getScbj() {
		return scbj;
	}

	public void setScbj(String scbj) {
		this.scbj = scbj;
	}

}
