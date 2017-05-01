package com.ydjw.web;

import android.util.Log;

public class GsmRestfulDao extends RestfulDao {

	public GsmRestfulDao() {
		Log.e("GsmRestfulDao", "GsmRestfulDao create");
	}
	
	@Override
	public String getUrl() {
		return "http://www.ntjxj.com";
	}

	@Override
	public String getPicUrl() {
		return getUrl() + PIC_URL;
	}

	@Override
	public String getJqtbFileUrl() {
		return getUrl() + JQTB_FILE_URL;
	}

	@Override
	public String getClassName() {
		return "GsmRestfulDao";
	}

}
