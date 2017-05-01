package com.ntga.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CheckResult implements Serializable {
	private String id;
	private String type;
	private String com;
	private String cxid;
	private String wt;
	private String tj;
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getCxid() {
		return cxid;
	}

	public void setCxid(String cxid) {
		this.cxid = cxid;
	}

	public String getWt() {
		return wt;
	}

	public void setWt(String wt) {
		this.wt = wt;
	}

	public String getTj() {
		return tj;
	}

	public void setTj(String tj) {
		this.tj = tj;
	}

}
