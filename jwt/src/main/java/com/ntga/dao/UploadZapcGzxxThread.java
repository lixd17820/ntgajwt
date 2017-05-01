package com.ntga.dao;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.ntga.bean.WebQueryResult;
import com.ntga.zapc.ZapcGzxxBean;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

public class UploadZapcGzxxThread extends Thread {

	private Handler mHandler;
	private ProgressDialog progressDialog;
	private ZapcGzxxBean gzxx;

	public UploadZapcGzxxThread(Handler handler) {
		this.mHandler = handler;
	}

	/**
	 * 启动线程
	 */
	public void doStart(Context context, ZapcGzxxBean gzxx) {
		// 显示进度对话框
		this.gzxx = gzxx;
		progressDialog = ProgressDialog.show(context, "提示", "正在请求数据,请稍等...",
				true);
		progressDialog.setCancelable(true);
		this.start();
	}

	/**
	 * 线程运行
	 */
	@Override
	public void run() {
		RestfulDao dao = RestfulDaoFactory.getDao();
		WebQueryResult<ZapcReturn> re = dao.uploadZapcGzxx(gzxx);
		Message msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putSerializable("gzxxRe", re);
		b.putSerializable("gzxx", gzxx);
		msg.setData(b);
		mHandler.sendMessage(msg);
		progressDialog.dismiss();
		
	}
}
