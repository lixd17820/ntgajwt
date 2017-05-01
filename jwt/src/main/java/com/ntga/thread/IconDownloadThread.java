package com.ntga.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalMethod;
import com.ntga.tools.ZipUtils;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

public class IconDownloadThread extends Thread {

	public final static String ICON_STR = "icon";

	private Context context;
	private Handler mHandler;
	private ProgressDialog progressDialog = null;

	public void doStart(Handler _handler, boolean isShowProcess,
			Context _context) {
		this.mHandler = _handler;
		this.context = _context;
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("提示");
		progressDialog.setMessage("正在下载图标,请稍等...");
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
		progressDialog.setProgress(0);
		this.start();
	}

	@Override
	public void run() {
		downIcon();
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
	}

	private void sendData(int err, int what, int step, String mc) {
		Message msg = mHandler.obtainMessage();
		msg.arg1 = err;
		msg.what = what;
		msg.arg2 = step;
		if (!TextUtils.isEmpty(mc)) {
			Bundle b = new Bundle();
			b.putString(CWMS, mc);
			msg.setData(b);
		}
		mHandler.sendMessage(msg);
	}

	public static int ERR_NOT_NEED_DOWNLOAD = 11;
	public static int ERR_NETWORK_ERROR = 10;
	public static int ERR_NO_SD_CARD = 12;
	public static int ERR_NONE = 0;

	public static int WHAT_CREATE_ICON = 100;
	public static int WHAT_DOWNLONGING = 0;
	public static int WHAT_DOWNLOAD_DONE = 1;
	
	public static String CWMS = "mc";

	private void downIcon() {
		File innDir = context.getFilesDir();
		File iconDir = new File(innDir, "icon");
		if (!iconDir.exists())
			iconDir.mkdirs();
		RestfulDao dao = RestfulDaoFactory.getDao();
		String md5 = GlobalMethod.readIconMd5(context);

		md5 = TextUtils.isEmpty(md5) ? "" : md5;
		// 有文件，需要和服务器同步验证
		WebQueryResult<ZapcReturn> rs = dao.isDownloadIcon(md5);
		String err = GlobalMethod.getErrorMessageFromWeb(rs);
		if (!TextUtils.isEmpty(err)) {
			sendData(ERR_NETWORK_ERROR, 0, 0, err);
			return;
		}
		if (rs.getResult() == null
				|| TextUtils.equals("0", rs.getResult().getCgbj())) {
			sendData(ERR_NOT_NEED_DOWNLOAD, 0, 0, "无需下载图标文件");
			return;
		}

		String megId = rs.getResult().getCgbj();
		long size = Long.valueOf(rs.getResult().getPcbh()[0]);
		// 检查SD卡
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			sendData(ERR_NO_SD_CARD, 0, 0, "需加载SD卡");
			return;
		}
		// 没有文件或验证后需要下载的，下载文件到SD卡中
		String url = dao.getIconFileUrl() + "?megId=" + megId;
		Log.e("IconDownloadThread", url);
		File outSideDir = new File(Environment.getExternalStorageDirectory(),
				"jwtdb");
		if (!outSideDir.exists())
			outSideDir.mkdirs();

		File dest = new File(outSideDir, "icon_md5.zip");
		long down = downloadFile(url, dest, size);
		int downCount = 0;
		if (down > 0 && dest.exists() && dest.length() == down) {
			// 解压获取两个文件到个人文件夹中
			ZipUtils.unzipFile(dest.getAbsolutePath(), innDir.getAbsolutePath());
			File megFile = new File(innDir, "icon.meg");
			if (!megFile.exists()) {
				return;
			}
			// 解合并到图标文件夹中
			downCount = ZipUtils.unMegFile(megFile.getAbsolutePath(),
					iconDir.getAbsolutePath());
		}
		sendData(ERR_NONE, WHAT_CREATE_ICON, downCount, null);
	}
	
	private long downloadFile(String urlStr, File dest, long fileSize) {
		long count = 0;
		byte[] b = new byte[1024];
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			int code = conn.getResponseCode();
			if (code != HttpURLConnection.HTTP_OK)
				return 0;
			InputStream is = conn.getInputStream();
			FileOutputStream out = new FileOutputStream(dest);
			int len = -1;
			while ((len = is.read(b)) > 0) {
				out.write(b, 0, len);
				count += len;
				int step = (int) (count * 100 / fileSize);
				progressDialog.setProgress(step);
			}
			out.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
}
