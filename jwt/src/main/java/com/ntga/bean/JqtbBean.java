package com.ntga.bean;

import java.io.Serializable;

/**
 * 警情通报实体类
 * 
 * @author lixd
 * 
 */
@SuppressWarnings("serial")
public class JqtbBean implements Serializable {

	private String id;
	private String sysId;
	private String title;
	private String sender;
	private String content;
	private String sendDate;
	private String recDate;
	private String isFile;// 0 无附件，1有附件
	private String fileSize;
	private String fileCata;
	private String fileLocation;
	private String readBj; // 0 未读， 1 已读
	private String delBj; // 0 未删除 1 已删除
	private String force;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getRecDate() {
		return recDate;
	}

	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}

	public String getIsFile() {
		return isFile;
	}

	public void setIsFile(String isFile) {
		this.isFile = isFile;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileCata() {
		return fileCata;
	}

	public void setFileCata(String fileCata) {
		this.fileCata = fileCata;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getReadBj() {
		return readBj;
	}

	public void setReadBj(String readBj) {
		this.readBj = readBj;
	}

	public String getDelBj() {
		return delBj;
	}

	public void setDelBj(String delBj) {
		this.delBj = delBj;
	}

	public String getForce() {
		return force;
	}

	public void setForce(String force) {
		this.force = force;
	}

}
