package com.ntga.bean;

import java.util.ArrayList;

import android.text.TextUtils;

public class VersionMs {
	private ArrayList<String> field;
	private ArrayList<ArrayList<String>> values;

	public ArrayList<String> getField() {
		if (field == null)
			field = new ArrayList<String>();
		return field;
	}

	public void setField(ArrayList<String> field) {
		this.field = field;
	}

	public ArrayList<ArrayList<String>> getValues() {
		if (values == null)
			values = new ArrayList<ArrayList<String>>();
		return values;
	}

	public void setValues(ArrayList<ArrayList<String>> values) {
		this.values = values;
	}

	public String getVersionInfo(int row, String fieldName) {
		if (field.isEmpty() || values.isEmpty())
			return null;
		ArrayList<String> vs = values.get(row);
		for (int i = 0; i < field.size(); i++) {
			if (TextUtils.equals(fieldName, field.get(i))) {
				return vs.get(i);
			}
		}
		return null;
	}

	public int getFieldSize() {
		return field == null ? 0 : field.size();
	}
	public int getValueSize() {
		return values == null ? 0 : values.size();
	}
}
