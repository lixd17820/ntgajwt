package com.ntga.jwt;

import java.io.File;

import com.ntga.bean.JqtbBean;
import com.ntga.dao.GlobalMethod;
import com.ntga.database.MessageDao;
import com.ntga.tools.MimeType;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class JqtbDetailActivity extends Activity implements
		View.OnClickListener {
	private TextView tbXtbh, tvTitle, tvContent, tvSender, tvSendDate,
			tvRecDate, tvFile, tvForce;
	private Button btnDel, btnDownFile, btnShowFile;
	private JqtbBean jqtb;
	private Activity self;
	private MessageDao dao;

	MainReferService mrService;
	private DownloadOverBroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		dao = new MessageDao(self);
		setContentView(R.layout.jqtb_detail);
		jqtb = (JqtbBean) getIntent().getSerializableExtra("jqtb");
		String jqtbId = getIntent().getStringExtra("jqtbId");
		if (!TextUtils.isEmpty(jqtbId)) {
			// 从下载框中转来
			Log.e("JQTB DETAIL", " " + jqtbId);
			jqtb = dao.getJqtbById(jqtbId);
		}
		initView();
		if (jqtb != null) {
			showJqtb();
		} else {
			emptyView();
		}
		btnDel.setOnClickListener(this);
		btnDownFile.setOnClickListener(this);
		btnShowFile.setOnClickListener(this);
		// 消除通知栏
		// boolean isCancel = getIntent().getBooleanExtra("cancel", false);
		// if (isCancel) {
		// synchronized (MainReferService.jqtbIsDown) {
		// MainReferService.jqtbIsDown = false;
		// }
		// } else {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(MainReferService.JQTB_DOWN_NOTICE_ID);
		// }
	}

	private void initView() {
		tbXtbh = (TextView) findViewById(R.id.tv_jqtb_xtbh);
		tvTitle = (TextView) findViewById(R.id.tv_jqtb_title);
		tvContent = (TextView) findViewById(R.id.tv_jqtb_content);
		tvSender = (TextView) findViewById(R.id.tv_jqtb_sender);
		tvSendDate = (TextView) findViewById(R.id.tv_jqtb_send_date);
		tvRecDate = (TextView) findViewById(R.id.tv_jqtb_recv_date);
		tvFile = (TextView) findViewById(R.id.tv_jqtb_file);
		tvForce = (TextView) findViewById(R.id.tv_jqtb_force);
		btnDel = (Button) findViewById(R.id.btn_del_jqtb);
		btnDownFile = (Button) findViewById(R.id.btn_down_file);
		btnShowFile = (Button) findViewById(R.id.btn_show_file);
	}

	private void emptyView() {
		tbXtbh.setText("        编号：");
		tvTitle.setText("        标题：");
		tvContent.setText("        内容：");
		tvSender.setText("发送单位：");
		tvSendDate.setText("发送时间：");
		tvRecDate.setText("接收时间：");
		tvFile.setText("附件状态：");
		tvForce.setText("文件来源：");
		btnDel.setEnabled(false);
		btnDownFile.setEnabled(false);
		btnShowFile.setEnabled(false);
	}

	private void showJqtb() {
		tbXtbh.setText("        编号：交巡第" + jqtb.getSysId() + "号");
		tvTitle.setText("        标题：" + jqtb.getTitle());
		tvContent.setText("        内容：" + GlobalMethod.ifNull(jqtb.getContent()));
		tvSender.setText("发送单位：" + jqtb.getSender());
		tvSendDate.setText("发送时间：" + jqtb.getSendDate());
		tvRecDate.setText("接收时间：" + jqtb.getRecDate());
		tvForce.setText("文件来源："
				+ ("0".equals(jqtb.getForce()) ? "警情通报" : "文件收发"));
		String fileState = "";
		if ("1".equals(jqtb.getIsFile())) {
			String fs = "附件类型：" + jqtb.getFileCata() + "\n附件大小："
					+ (Integer.valueOf(jqtb.getFileSize()) / 1024) + "KB";
			if (!TextUtils.isEmpty(jqtb.getFileLocation())) {
				File f = new File(jqtb.getFileLocation());
				if (f.exists())
					fileState = "附件已下载已保存";
				else
					fileState = "附件未下载";
			}
			fileState += "\n" + fs;
		} else {
			fileState = "无附件";
			btnDownFile.setEnabled(false);
			btnShowFile.setEnabled(false);
		}
		tvFile.setText("附件状态：" + fileState);

	}

	@Override
	public void onClick(View v) {
		if (v == btnDel) {
			GlobalMethod.showDialogTwoListener("系统提示", "删除操作将不能恢复，是否确定删除",
					"删除", "取消", delJqtbListener, self);
		} else if (v == btnDownFile) {
			if (hasFile()) {
				//mrService.downloadJqtb(jqtb);
				// progressDialog = new ProgressDialog(self);
				// progressDialog.setTitle("提示");
				// progressDialog.setMessage("正在下载...");
				// progressDialog.setCancelable(true);
				// progressDialog
				// .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				// progressDialog.setMax(Integer.valueOf(jqtb.getFileSize()));
				// progressDialog.show();
				// new DownPicThread(downFileHandler, f, jqtb.getSysId(),
				// jqtb.getForce()).start();
			}
		} else if (v == btnShowFile) {
			if (hasFile() && !TextUtils.isEmpty(jqtb.getFileLocation())) {
				File file = new File(jqtb.getFileLocation());
				if (file.exists()) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file),
							MimeType.getMIMEType(file));
					startActivity(intent);
					return;
				}
			}
			GlobalMethod.showErrorDialog("请首先保存附件到SD卡中！", this);
		}
	}

	private DialogInterface.OnClickListener delJqtbListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dao.delJqtb(jqtb.getId());
			setResult(RESULT_OK);
			finish();
		}
	};

	private boolean hasFile() {
		return "1".equals(jqtb.getIsFile())
				&& TextUtils.isDigitsOnly(jqtb.getFileSize())
				&& Integer.valueOf(jqtb.getFileSize()) > 0;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
		unbindService(serviceConn);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent serviceIntent = new Intent(this, MainReferService.class);
		bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
		IntentFilter filter = new IntentFilter(
				MainReferService.JQTB_DOWNLOAD_BROADCAST);
		receiver = new DownloadOverBroadcastReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dao.closeDb();
	}

	private ServiceConnection serviceConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mrService = ((MainReferService.DownLoadServiceBinder) service)
					.getService();

		}
	};

	class DownloadOverBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (jqtb != null) {
				jqtb = dao.getJqtbById(jqtb.getId());
				showJqtb();
				// NotificationManager nm = (NotificationManager)
				// getSystemService(Context.NOTIFICATION_SERVICE);
				// nm.cancel(MainReferService.JQTB_DOWN_NOTICE_ID);
			}

		}

	}
}
