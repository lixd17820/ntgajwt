package com.ntga.thread;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

public class LogoutJwtNewThread extends Thread {
	private Handler handler;

	public LogoutJwtNewThread(Handler handler) {
		this.handler = handler;
	}

	public void doStart() {
		this.start();
	}

	@Override
	public void run() {
		RestfulDao dao = RestfulDaoFactory.getDao();
		String jh = GlobalData.grxx.get(GlobalConstant.JH);
		WebQueryResult<String> result = dao.logoutJwt(jh,
				GlobalData.serialNumber);
		String err = GlobalMethod.getErrorMessageFromWeb(result);
		Message m = handler.obtainMessage();
		if (TextUtils.isEmpty(err) && result != null && TextUtils.equals(result.getResult(), "1")) {
			m.what = 1;
		} else {
			m.what = 0;
		}
		handler.sendMessage(m);
	}
}
