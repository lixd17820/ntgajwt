package com.ntga.thread;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ntga.bean.SpringDjItf;
import com.ntga.bean.SpringKcdjBean;
import com.ntga.bean.SpringWhpdjBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.database.MessageDao;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

public class UploadSpringThread extends Thread {
	public static final String UPLOAD_RESULT = "uploadResult";

	private Handler mHandler;
	private SpringDjItf dj;
	private Context context;

	public UploadSpringThread(Handler handler, SpringDjItf dj, Context context) {
		this.mHandler = handler;
		this.dj = dj;
		this.context = context;
	}

	/**
	 * 线程运行
	 */
	@Override
	public void run() {
		MessageDao mdao = new MessageDao(context);
		RestfulDao dao = RestfulDaoFactory.getDao();
		WebQueryResult<ZapcReturn> rs = null;
		if (dj.getDjlx() == 0) {
			SpringKcdjBean kcdj = mdao.queryKcdjById(dj.getId());
			rs = dao.uploadSpringKcdj(kcdj);
		} else {
			SpringWhpdjBean whpdj = mdao.queryWhpdjById(dj.getId());
			rs = dao.uploadSpringWhpdj(whpdj);
		}
		mdao.closeDb();
		Message msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putSerializable(UPLOAD_RESULT, rs);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}
}
