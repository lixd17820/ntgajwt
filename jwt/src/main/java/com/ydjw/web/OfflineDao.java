package com.ydjw.web;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import android.os.Handler;
import android.util.Log;

import com.ntga.bean.WebQueryResult;

public class OfflineDao extends RestfulDao {

	public OfflineDao() {
		Log.e("OfflineDao", "OfflineDao");
	}

	@Override
	public String getPicUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileUrl() {
		return getUrl() + "ydjw/DownloadFile?pack=";
	}

	@Override
	public String getJqtbFileUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebQueryResult<String> restfulQuery(String url,
			List<NameValuePair> params, int method, boolean isXml) {
		return new WebQueryResult<String>(HttpStatus.SC_BAD_REQUEST);
	}

	@Override
	public WebQueryResult<String> restfulQuery(String url,
			List<NameValuePair> params, int method) {
		return new WebQueryResult<String>(HttpStatus.SC_BAD_REQUEST);
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebQueryResult<String> uploadByte(String url, byte[] data,
			InputStream in, long inLength, Handler handler) {
		return new WebQueryResult<String>(HttpStatus.SC_BAD_REQUEST);
	}

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return "OfflineDao";
	}

}
