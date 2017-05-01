package com.ntga.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MjgrxxBean implements Serializable {

	private String mjjh;
	private String mjxm;
	private String fxjg;
	private String fyjg;
	private String ssjg;
	private String fxjgqc;
	private String jkyh;

	public String getJkyh() {
		return jkyh;
	}

	public void setJkyh(String jkyh) {
		this.jkyh = jkyh;
	}

	public String getFxjgqc() {
		return fxjgqc;
	}

	public void setFxjgqc(String fxjgqc) {
		this.fxjgqc = fxjgqc;
	}

	public String getMjjh() {
		return mjjh;
	}

	public void setMjjh(String mjjh) {
		this.mjjh = mjjh;
	}

	public String getMjxm() {
		return mjxm;
	}

	public void setMjxm(String mjxm) {
		this.mjxm = mjxm;
	}

	public String getFxjg() {
		return fxjg;
	}

	public void setFxjg(String fxjg) {
		this.fxjg = fxjg;
	}

	public String getFyjg() {
		return fyjg;
	}

	public void setFyjg(String fyjg) {
		this.fyjg = fyjg;
	}

	public String getSsjg() {
		return ssjg;
	}

	public void setSsjg(String ssjg) {
		this.ssjg = ssjg;
	}

}
