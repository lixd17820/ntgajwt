package com.ntga.jwt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.ntga.bean.BkPicMesBean;
import com.ntga.dao.GlobalMethod;
import com.ydjw.web.RestfulDaoFactory;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;

public class BkPicMesShowActivity extends ListActivity implements
		View.OnClickListener {

	private PicBkBroadcastReceiver receiver;
	private ArrayAdapter<BkPicMesBean> ad;
	private ArrayList<BkPicMesBean> bks;
	private Button btnSave, btnShowPic;
	private ProgressDialog progressDialog;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comm_two_btn_show_list);
		setTitle("显示布控图片");
		String svcName = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) getSystemService(svcName);
		nm.cancel(MainReferService.NOT_ID);
		IntentFilter filter = new IntentFilter("com.ntga.jwt.main.server");
		receiver = new PicBkBroadcastReceiver();
		registerReceiver(receiver, filter);
		bks = (ArrayList<BkPicMesBean>) getIntent().getSerializableExtra("bks");
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		if (ad == null) {
			ad = new ArrayAdapter<BkPicMesBean>(this,
					android.R.layout.simple_list_item_single_choice, bks);
			setListAdapter(ad);
		} else
			ad.notifyDataSetChanged();
		btnShowPic = (Button) findViewById(R.id.btn_left);
		btnShowPic.setText("显示图片");
		btnSave = (Button) findViewById(R.id.btn_right);
		btnSave.setText("保存图片");
		btnShowPic.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		// progressDialog = new ProgressDialog(this);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	class PicBkBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(MainReferService.NOT_ID);
		}

	}

	@Override
	public void onClick(View v) {
		int position = getListView().getCheckedItemPosition();
		if (position < 0) {
			GlobalMethod.showErrorDialog("请选择一条记录操作", this);
			return;
		}
		BkPicMesBean bk = bks.get(position);
		String picId = bk.getPicId();
		File file = new File("/sdcard", picId + ".jpg");
		if (v.getId() == R.id.btn_left) {
			// 显示图片
			if (file.exists()) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "image/*");
				startActivity(intent);
			} else {
				GlobalMethod.showErrorDialog("请首先保存图片到SD卡中！", this);
			}
		} else if (v.getId() == R.id.btn_right) {
			// 保存图片
			DownPicThread thread = new DownPicThread(downFileHandler, file,
					picId);
			thread.doStart();
		}

	}

	private Handler downFileHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (progressDialog.isShowing())
				progressDialog.dismiss();
			Bundle data = msg.getData();
			long length = data.getLong("fileLen", 0);
			String fileName = data.getString("fileName");
			if (length > 0) {
				GlobalMethod.showDialog("系统提示", "图片保存在SD卡根目录中，文件名为" + fileName,
						"知道了", BkPicMesShowActivity.this);
			} else {
				GlobalMethod.showDialog("系统提示", "图片保存失败", "知道了",
						BkPicMesShowActivity.this);
			}
		}

	};

	class DownPicThread extends Thread {
		Handler mHandler;
		File file;
		String picId;

		public DownPicThread(Handler mHandler, File file, String picId) {
			this.mHandler = mHandler;
			this.file = file;
			this.picId = picId;
		}

		public void doStart() {
			progressDialog = ProgressDialog.show(BkPicMesShowActivity.this,
					"提示", "正在下载图片,请稍等...", true);
			progressDialog.setCancelable(true);
			this.start();
		}

		@Override
		public void run() {
			long length = downLoadPic(file, picId);
			Message msg = mHandler.obtainMessage();
			Bundle data = new Bundle();
			data.putLong("fileLen", length);
			data.putString("fileName", file.getName());
			msg.setData(data);
			mHandler.sendMessage(msg);
		}

	}

	private long downLoadPic(File file, String picId) {
		if (file.exists()) {
			return file.length();
		}
		byte[] b = new byte[1024];
		try {
			URL url = new URL(RestfulDaoFactory.getDao().getJqtbFileUrl()
					+ "?id=" + picId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			FileOutputStream out = new FileOutputStream(file);
			int len = -1;
			while ((len = is.read(b)) > 0) {
				out.write(b, 0, len);
			}
			out.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file.exists()) {
			return file.length();
		}
		return 0;
	}
}
