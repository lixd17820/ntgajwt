package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.ntga.adaper.JqtbListAdapter;
import com.ntga.bean.JqtbBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.database.MessageDao;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class JqtbListActivity extends ListActivity {

	private static final int REQ_JQTB_DETAIL = 100;
	private List<JqtbBean> jqtbs;
	private Activity self;
	private PicBkBroadcastReceiver receiver;
	private MessageDao dao;
	private ProgressDialog progressDialog;
	private Button btnJqtb, btnWjcs, btnDwwj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comm_three_btn_show_list);
		btnJqtb = (Button) findViewById(R.id.btn_left);
		btnJqtb.setText("刷警情通报");
		btnWjcs = (Button) findViewById(R.id.btn_center);
		btnWjcs.setText("个人文件收发");
		btnDwwj = (Button) findViewById(R.id.btn_right);
		btnDwwj.setText("单位文件收发");
		self = this;
		setTitle(getIntent().getStringExtra("title"));
		dao = new MessageDao(self);
		jqtbs = new ArrayList<JqtbBean>();
		referListView();
		String svcName = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) getSystemService(svcName);
		nm.cancel(MainReferService.NOT_ID);
		IntentFilter filter = new IntentFilter("com.ntga.jwt.main.server");
		receiver = new PicBkBroadcastReceiver();
		registerReceiver(receiver, filter);
		btnJqtb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(self);
					progressDialog.setTitle("提示");
					progressDialog.setMessage("正在获取警情通报...");
					progressDialog.setCancelable(true);
				}
				progressDialog.show();
				new ReferQtbjThread(referQtbjHandler, "0").start();

			}
		});

		btnWjcs.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(self);
					progressDialog.setTitle("提示");
					progressDialog.setMessage("正在获取文件收发...");
					progressDialog.setCancelable(true);
				}
				progressDialog.show();
				new ReferQtbjThread(referQtbjHandler, "1").start();

			}
		});
		btnDwwj.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(self);
					progressDialog.setTitle("提示");
					progressDialog.setMessage("正在获取文件收发...");
					progressDialog.setCancelable(true);
				}
				progressDialog.show();
				new ReferQtbjThread(referQtbjHandler, "2").start();

			}
		});

	}

	private void referListView() {
		changeJqtbList();
		JqtbListAdapter adapter = (JqtbListAdapter) getListAdapter();
		if (adapter == null) {
			adapter = new JqtbListAdapter(self, jqtbs);
			setListAdapter(adapter);
		}
		adapter.notifyDataSetChanged();

	}

	private List<JqtbBean> changeJqtbList() {
		jqtbs.clear();
		List<JqtbBean> list = dao.queryAllJqtb();
		jqtbs.addAll(list);
		return jqtbs;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (position > -1 && jqtbs != null && !jqtbs.isEmpty()) {
			JqtbBean jqtb = jqtbs.get(position);
			dao.readJqtb(jqtb.getId());
			Intent intent = new Intent(self, JqtbDetailActivity.class);
			intent.putExtra("jqtb", jqtb);
			startActivityForResult(intent, REQ_JQTB_DETAIL);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_JQTB_DETAIL) {
			referListView();
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		dao.closeDb();
		super.onDestroy();
	}

	class PicBkBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			referListView();
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(MainReferService.NOT_ID);
		}

	}

	private Handler referQtbjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (progressDialog.isShowing())
				progressDialog.dismiss();
			Bundle data = msg.getData();
			String info = data.getString("info");
			if (!TextUtils.isEmpty(info))
				Toast.makeText(self, info, Toast.LENGTH_LONG).show();
			referListView();
		}

	};

	class ReferQtbjThread extends Thread {
		Handler mHandler;
		String force;

		public ReferQtbjThread(Handler mHandler, String force) {
			this.mHandler = mHandler;
			this.force = force;
		}

		@Override
		public void run() {
			WebQueryResult<List<JqtbBean>> bks = RestfulDaoFactory.getDao()
					.queryBkPicMessage(
							GlobalData.grxx.get(GlobalConstant.YHBH), force);
			String errs = GlobalMethod.getErrorMessageFromWeb(bks);
			if (TextUtils.isEmpty(errs) && bks.getResult() != null) {
				ArrayList<JqtbBean> bkArray = (ArrayList<JqtbBean>) bks
						.getResult();
				int saveRow = 0;
				if (bkArray.size() > 0) {
					MessageDao dao = new MessageDao(self);
					for (JqtbBean jqtbBean : bkArray) {
						jqtbBean.setForce(force);
						if (dao.saveJqtb(jqtbBean) > 0)
							saveRow++;
					}
					dao.closeDb();
					GlobalMethod.sendInfoToHandler(mHandler, "加载" + saveRow
							+ "条数据", 0, 0);
				} else {
					GlobalMethod.sendInfoToHandler(mHandler, "没有数据需要加载", 0, 0);
				}
			} else {
				Log.e("MainReferService", errs);
				GlobalMethod.sendInfoToHandler(mHandler, errs, 0, 0);
			}
		}

	}

}
