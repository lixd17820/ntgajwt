package com.ntga.bean;

import java.io.Serializable;

/**
 * 本类用于存取解析从服务器中获取的版本更新信息
 * 
 * @author ntgajwt
 * 
 */
public class VersionInfoBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 11167875L;
	// 服务器返回的状态
	private int code;
	// 服务器返回信息
	private String message;
	// 字段名称的描述
	
	private boolean isNewVersion;

	public boolean isNewVersion() {
		return isNewVersion;
	}

	public void setNewVersion(boolean isNewVersion) {
		this.isNewVersion = isNewVersion;
	}

	// 主版本变量
	private VersionMs mv;
	// 分版本
	private VersionMs cv;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public VersionMs getMv() {
		if(mv == null)
			mv = new VersionMs();
		return mv;
	}

	public void setMv(VersionMs mv) {
		this.mv = mv;
	}

	public VersionMs getCv() {
		if(cv == null)
			cv = new VersionMs();
		return cv;
	}

	public void setCv(VersionMs cv) {
		this.cv = cv;
	}

}


