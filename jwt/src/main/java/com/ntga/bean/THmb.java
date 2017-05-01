package com.ntga.bean;

/**
 * AbstractTHmb entity provides the base persistence definition of the THmb
 * entity. @author MyEclipse Persistence Tools
 */

@SuppressWarnings("serial")
public class THmb implements java.io.Serializable {

	// Fields

	private String id;
	private String hdid;
	private String jshm;
	private String dqhm;
	private String hdzl;

	// Constructors

	/** default constructor */
	public THmb() {
	}

	/** full constructor */
	public THmb(String id, String hdid, String jshm, String dqhm, String hdzl) {
		this.id = id;
		this.hdid = hdid;
		this.jshm = jshm;
		this.dqhm = dqhm;
		this.hdzl = hdzl;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHdid() {
		return this.hdid;
	}

	public void setHdid(String hdid) {
		this.hdid = hdid;
	}

	public String getJshm() {
		return this.jshm;
	}

	public void setJshm(String jshm) {
		this.jshm = jshm;
	}

	public String getDqhm() {
		return this.dqhm;
	}

	public void setDqhm(String dqhm) {
		this.dqhm = dqhm;
	}

	public String getHdzl() {
		return this.hdzl;
	}

	public void setHdzl(String hdzl) {
		this.hdzl = hdzl;
	}

}