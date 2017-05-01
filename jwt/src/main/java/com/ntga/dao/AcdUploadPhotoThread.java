package com.ntga.dao;

import java.io.File;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ntga.bean.AcdPhotoBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

public class AcdUploadPhotoThread extends Thread {
	private Handler mHandler;
	private AcdPhotoBean acd;

	// private ProgressDialog progressDialog;

	public AcdUploadPhotoThread(Handler mHandler, AcdPhotoBean acd) {
		this.mHandler = mHandler;
		this.acd = acd;
	}

	public void doStart() {
		// this.progressDialog = progressDialog;
		// maxStep = acd.getPhoto().size() + 1;
		// progressDialog.setTitle("提示");
		// progressDialog.setMessage("正在上传事故信息...");
		// progressDialog.setCancelable(false);
		// progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// progressDialog.setMax(maxStep * 25);
		// progressDialog.show();
		start();
	}

	private void sendData(String err, int what, int step) {
		Message m = mHandler.obtainMessage();
		m.what = what;
		m.arg1 = step;
		if (!TextUtils.isEmpty(err)) {
			Bundle data = new Bundle();
			data.putString("err", err);
			m.setData(data);
		}
		mHandler.sendMessage(m);
	}

	private void sendData(Bundle data, int what, int step) {
		Message m = mHandler.obtainMessage();
		m.what = what;
		m.arg1 = step;
		if (data != null)
			m.setData(data);
		mHandler.sendMessage(m);
	}

	@Override
	public void run() {
		int step = 0;
		RestfulDao dao = RestfulDaoFactory.getDao();
		WebQueryResult<ZapcReturn> rs = dao.uploadAcdRecode(acd);
		String err = GlobalMethod.getErrorMessageFromWeb(rs);
		if (!TextUtils.isEmpty(err)) {
			sendData(err, GlobalConstant.WHAT_ERR, 0);
			return;
		} else {
			sendData("记录上传成功", GlobalConstant.WHAT_RECODE_OK, ++step * 25);
		}
		Long recID = Long.valueOf(rs.getResult().getPcbh()[0]);
		List<String> files = acd.getPhoto();
		for (int i = 0; i < files.size(); i++) {
			File photo = new File(files.get(i));
			if (!photo.exists()) {
				sendData("上传文件不存在", GlobalConstant.WHAT_ERR, 0);
				return;
			}
			WebQueryResult<ZapcReturn> re = dao.uploadAcdPhoto(photo, recID);
			String photoErr = GlobalMethod.getErrorMessageFromWeb(re);
			if (!TextUtils.isEmpty(photoErr)) {
				sendData(photoErr, GlobalConstant.WHAT_ERR, 0);
				return;
			} else {
				sendData("图片上传成功", GlobalConstant.WHAT_PHOTO_OK, ++step * 25);
			}
		}
		Bundle data = new Bundle();
		data.putLong("xtbh", recID);
		data.putInt("acdID", acd.getId());
		sendData(data, GlobalConstant.WHAT_ALL_OK, ++step * 25);
	}

}
