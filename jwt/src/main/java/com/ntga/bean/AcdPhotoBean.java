package com.ntga.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AcdPhotoBean implements Serializable {

	private int id;
	private String sgsj;
	private String sgdddm;
	private String sgdd;
	private String sgbh;
	private String xtbh;
	private List<String> photo;
	private int scbj;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSgsj() {
		return sgsj;
	}

	public void setSgsj(String sgsj) {
		this.sgsj = sgsj;
	}

	public String getSgdddm() {
		return sgdddm;
	}

	public void setSgdddm(String sgdddm) {
		this.sgdddm = sgdddm;
	}

	public String getSgdd() {
		return sgdd;
	}

	public void setSgdd(String sgdd) {
		this.sgdd = sgdd;
	}

	public String getSgbh() {
		return sgbh;
	}

	public void setSgbh(String sgbh) {
		this.sgbh = sgbh;
	}

	public String getXtbh() {
		return xtbh;
	}

	public void setXtbh(String xtbh) {
		this.xtbh = xtbh;
	}

	public List<String> getPhoto() {
		if(photo == null)
			photo = new ArrayList<String>();
		return photo;
	}

	public void setPhoto(List<String> photo) {
		this.photo = photo;
	}

	public int getScbj() {
		return scbj;
	}

	public void setScbj(int scbj) {
		this.scbj = scbj;
	}

}
