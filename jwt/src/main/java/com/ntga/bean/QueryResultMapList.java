package com.ntga.bean;

import java.io.Serializable;
import java.util.ArrayList;

import android.text.TextUtils;

public class QueryResultMapList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1333165169247435463L;

	/** 列定义和列描述 */
	private ArrayList<KeyValueBean> fieldDef;
	/** 可能有的多行值 */
	private ArrayList<ArrayList<String>> lists;
	/** 级连查询 */
	private ArrayList<KeyValueBean> glcxs;
	/** 查询结果的总体描述 */
	private QueryObject qObj;

	public CheckResult getCheckResutl() {
		return checkResutl;
	}

	public void setCheckResutl(CheckResult checkResutl) {
		this.checkResutl = checkResutl;
	}

	private CheckResult checkResutl;

	public ArrayList<KeyValueBean> getGlcxs() {
		if (glcxs == null)
			glcxs = new ArrayList<KeyValueBean>();
		return glcxs;
	}

	public void setGlcxs(ArrayList<KeyValueBean> glcxs) {
		this.glcxs = glcxs;
	}

	public QueryObject getqObj() {
		if (qObj == null)
			qObj = new QueryObject();
		return qObj;
	}

	public void setqObj(QueryObject qObj) {
		this.qObj = qObj;
	}

	public ArrayList<KeyValueBean> getFieldDef() {
		if (fieldDef == null)
			fieldDef = new ArrayList<KeyValueBean>();
		return fieldDef;
	}

	public void setFieldDef(ArrayList<KeyValueBean> fieldDef) {
		this.fieldDef = fieldDef;
	}

	public ArrayList<ArrayList<String>> getLists() {
		if (lists == null)
			lists = new ArrayList<ArrayList<String>>();
		return lists;
	}

	public void setLists(ArrayList<ArrayList<String>> lists) {
		this.lists = lists;
	}

	public ArrayList<String> getListByRow(int row) {
		if (lists != null && lists.size() > 0) {
			ArrayList<String> al = lists.get(row);
			return al;
		}
		return null;
	}

	public int size() {
		if (lists != null && lists.size() > 0) {
			return lists.size();
		}
		return 0;
	}

	public ArrayList<QueryResultBean> getResultByRow(int row) {
		if (size() > 0) {
			ArrayList<String> temp = lists.get(row);
			ArrayList<QueryResultBean> r = new ArrayList<QueryResultBean>();
			for (int i = 0; i < fieldDef.size(); i++) {
				String value = temp.get(i);
				if (!TextUtils.isEmpty(value))
					r.add(new QueryResultBean(fieldDef.get(i).getKey(),
							fieldDef.get(i).getValue(), value));
			}
			return r;
		}
		return null;
	}

	public String findValueByField(int row, String field) {
		String value = "";
		ArrayList<QueryResultBean> listQrb = getResultByRow(row);
		if (listQrb != null && listQrb.size() > 0) {
			for (QueryResultBean q : listQrb) {
				if (TextUtils.equals(field.trim(), q.getField().trim())) {
					value = q.getValue();
					break;
				}
			}
		}
		return value;
	}

}
