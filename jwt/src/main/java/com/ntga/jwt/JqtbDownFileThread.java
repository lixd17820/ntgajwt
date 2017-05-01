package com.ntga.jwt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ntga.bean.JqtbBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalMethod;
import com.ntga.database.MessageDao;
import com.ydjw.web.RestfulDaoFactory;

public class JqtbDownFileThread extends Thread {
	private Handler mHandler;
	private JqtbBean jqtb;
	private Context context;

	public JqtbDownFileThread(Context context, Handler mHandler, JqtbBean jqtb) {
		this.context = context;
		this.mHandler = mHandler;
		this.jqtb = jqtb;
	}

	@Override
	public void run() {
		downLoadPic();
	}

	private void downLoadPic() {
		int fileSize = Integer.valueOf(jqtb.getFileSize());
		int id = Integer.valueOf(jqtb.getId());
		String fileLoc = jqtb.getSysId() + "." + jqtb.getFileCata();
		File f = new File(getFileDir(jqtb.getForce()), fileLoc);
		byte[] b = new byte[1024];
		int sendCount = 1;
		int je = fileSize / 10;
		try {
			URL url = new URL(RestfulDaoFactory.getDao().getJqtbFileUrl()
					+ "?id=" + jqtb.getSysId() + "&force=" + jqtb.getForce());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			FileOutputStream out = new FileOutputStream(f);
			int countLen = 0;
			int len = -1;
			while ((len = is.read(b)) > 0) {
				if (!MainReferService.jqtbIsDown) {
					sendData(GlobalConstant.DOWNLOADERR, 0, fileSize);
					out.close();
					is.close();
					return;
				}
				out.write(b, 0, len);
				countLen += len;
				if (countLen > je * sendCount) {
					sendData(GlobalConstant.DOWNLOADING, countLen, fileSize);
					sendCount++;
				}
			}
			out.close();
			is.close();
			MessageDao dao = new MessageDao(context);
			dao.saveFileLocation(String.valueOf(id), f.getAbsolutePath());
			dao.closeDb();
			GlobalMethod.sendInfoToHandler(mHandler, jqtb.getSysId(),
					GlobalConstant.DOWNLOADOK, 0);
		} catch (Exception e) {
			e.printStackTrace();
			sendData(GlobalConstant.DOWNLOADERR, 0, fileSize);
		}

	}

	private void sendData(int what, int arg1, int arg2) {
		Message m = mHandler.obtainMessage();
		m.what = what;
		m.arg1 = arg1;
		m.arg2 = arg2;
		mHandler.sendMessage(m);
	}

	private File getFileDir(String force) {
		File f = new File("/sdcard/jqtb");
		if ("1".equals(force))
			f = new File("/sdcard/wfcs");
		if (!f.exists())
			f.mkdirs();
		return f;
	}
}
