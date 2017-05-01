package com.ntga.bean;

import java.io.Serializable;

/**
 * 查询结果返回的描述
 * @author 李小冬
 *
 */
public class QueryObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1999100205412770547L;
	private String id;
	private String tp;
	private String name;
	private String comment;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTp() {
		return tp;
	}

	public void setTp(String tp) {
		this.tp = tp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
