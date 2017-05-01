package com.zxk.jtbz.pojo;


import android.text.TextUtils;

public class CrossBean implements Comparable<CrossBean> {

	private String id;
	private String weRoad;
	private String nsRoad;
	private String crossName;
	private String crossOther;
	private String jd;
	private String wd;
	private String glbm;
	private String roadCount;
	private String eastSide;
	private String westSide;
	private String southSide;
	private String northSide;
	private String gxsj;
	private String crossLx;
	private String roadWidth;
	private String roadLength;

	public CrossBean() {
	}

	public CrossBean(String weRoad, String nsRoad, String crossName,
			String crossOther, String jd, String wd, String glbm,
			String roadCount, String eastSide, String westSide,
			String southSide, String northSide) {
		this.weRoad = weRoad;
		this.nsRoad = nsRoad;
		this.crossName = crossName;
		this.crossOther = crossOther;
		this.jd = jd;
		this.wd = wd;
		this.glbm = glbm;
		this.roadCount = roadCount;
		this.eastSide = eastSide;
		this.westSide = westSide;
		this.southSide = southSide;
		this.northSide = northSide;
	}

	public CrossBean(String weRoad, String nsRoad, String crossName,
			String crossOther, String jd, String wd, String glbm,
			String roadCount, String eastSide, String westSide,
			String southSide, String northSide, String crossLx) {
		this.weRoad = weRoad;
		this.nsRoad = nsRoad;
		this.crossName = crossName;
		this.crossOther = crossOther;
		this.jd = jd;
		this.wd = wd;
		this.glbm = glbm;
		this.roadCount = roadCount;
		this.eastSide = eastSide;
		this.westSide = westSide;
		this.southSide = southSide;
		this.northSide = northSide;
		this.crossLx = crossLx;
	}

	public CrossBean(String weRoad, String northSide) {
		this.weRoad = weRoad;
		this.northSide = northSide;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWeRoad() {
		return weRoad;
	}

	public void setWeRoad(String weRoad) {
		this.weRoad = weRoad;
	}

	public String getNsRoad() {
		return nsRoad;
	}

	public void setNsRoad(String nsRoad) {
		this.nsRoad = nsRoad;
	}

	public String getCrossName() {
		return crossName;
	}

	public void setCrossName(String crossName) {
		this.crossName = crossName;
	}

	public String getCrossOther() {
		return crossOther;
	}

	public void setCrossOther(String crossOther) {
		this.crossOther = crossOther;
	}

	public String getJd() {
		return jd;
	}

	public void setJd(String jd) {
		this.jd = jd;
	}

	public String getWd() {
		return wd;
	}

	public void setWd(String wd) {
		this.wd = wd;
	}

	public String getGlbm() {
		return glbm;
	}

	public void setGlbm(String glbm) {
		this.glbm = glbm;
	}

	public String getRoadCount() {
		return roadCount;
	}

	public void setRoadCount(String roadCount) {
		this.roadCount = roadCount;
	}

	public String getEastSide() {
		return eastSide;
	}

	public void setEastSide(String eastSide) {
		this.eastSide = eastSide;
	}

	public String getWestSide() {
		return westSide;
	}

	public void setWestSide(String westSide) {
		this.westSide = westSide;
	}

	public String getSouthSide() {
		return southSide;
	}

	public void setSouthSide(String southSide) {
		this.southSide = southSide;
	}

	public String getNorthSide() {
		return northSide;
	}

	public void setNorthSide(String northSide) {
		this.northSide = northSide;
	}

	public String getGxsj() {
		return gxsj;
	}

	public void setGxsj(String gxsj) {
		this.gxsj = gxsj;
	}

	public String getCrossLx() {
		return crossLx;
	}

	public void setCrossLx(String crossLx) {
		this.crossLx = crossLx;
	}

	public String getRoadWidth() {
		return roadWidth;
	}

	public void setRoadWidth(String roadWidth) {
		this.roadWidth = roadWidth;
	}

	public String getRoadLength() {
		return roadLength;
	}

	public void setRoadLength(String roadLength) {
		this.roadLength = roadLength;
	}


	@Override
	public String toString() {
		return " id:" + id + "," + " weRoad:" + weRoad + "," + " nsRoad:"
				+ nsRoad + "," + " crossName:" + crossName + ","
				+ " crossOther:" + crossOther + "," + " jd:" + jd + ","
				+ " wd:" + wd + "," + " glbm:" + glbm + "," + " roadCount:"
				+ roadCount + "," + " eastSide:" + eastSide + ","
				+ " westSide:" + westSide + "," + " southSide:" + southSide
				+ "," + " northSide:" + northSide + "," + " gxsj:" + gxsj;
	}

	@Override
	public boolean equals(Object obj) {
		return id.equals(((CrossBean) obj).getId());
	}

	public int compareTo(CrossBean o) {
		if ("0".equals(crossLx) && "0".equals(o.getCrossLx())) {
			// 比较标准路口
			if (weRoad.equals(o.getWeRoad())) {
				if (TextUtils.equals(westSide, o.getId())
						|| TextUtils.equals(o.getEastSide(), id)) {
					return 1;
				}
				if (TextUtils.equals(eastSide, o.getId())
						|| TextUtils.equals(o.getWestSide(), id)) {
					return -1;
				}
			} else if (nsRoad.equals(o.getNsRoad())) {
				if (TextUtils.equals(southSide, o.getId())
						|| TextUtils.equals(o.getNorthSide(), id)) {
					return 1;
				}
				if (TextUtils.equals(northSide, o.getId())
						|| TextUtils.equals(o.getSouthSide(), id)) {
					return -1;
				}
			}
		} else if ("1".equals(crossLx) && "1".equals(o.getCrossLx())) {

		} else if ("2".equals(crossLx) && "2".equals(o.getCrossLx())) {
			int rl = roadLength == null || "".equals(roadLength) ? 0 : Integer
					.valueOf(roadLength);
			int youRl = o.getRoadLength() == null
					|| "".equals(o.getRoadLength()) ? 0 : Integer.valueOf(o
					.getRoadLength());
			boolean isNs = weRoad == null || "".equals(weRoad);
			long myRoad = Long.valueOf(isNs ? northSide : eastSide);
			long youRoad = Long.valueOf(isNs ? o.getNorthSide() : o
					.getEastSide());
			if (myRoad != youRoad)
				return (int) (myRoad - youRoad);
			return rl - youRl;
		}
		return 0;
	}

}
