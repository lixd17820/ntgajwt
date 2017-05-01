package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.CommTwoRowUploadSelectListAdapter;
import com.ntga.bean.CommKeySelectedBean;
import com.ntga.bean.JdsPrintBean;
import com.ntga.bean.JdsUnjkPrintBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.BlueToothPrint;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.PrintJdsTools;
import com.ntga.tools.IDCard;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class JdsUnJkPrintListActivity extends ActionBarListActivity {

	private KeyValueBean printerInfo;
	private BlueToothPrint btp = null;
	private List<CommKeySelectedBean> jdsList;
	private List<JdsUnjkPrintBean> unjkList;
	private Context self;
	private String title;
	private EditText editSfzh, editHphm;
	private Spinner spinHpzl;
	private ArrayList<KeyValueBean> hpzls;
	// private CommTwoRowUploadSelectListAdapter adapter;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.jds_unjk_list);
		printerInfo = new KeyValueBean(
				GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME),
				GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS));

		title = "补打印决定书 ---- ";
		// 初始化打印机
		if (!TextUtils.isEmpty(printerInfo.getValue())) {
			title += printerInfo.getKey();
			btp = new BlueToothPrint(printerInfo.getValue());
		} else {
			title += "无打印机";
		}
		setTitle(title);
		editSfzh = (EditText) findViewById(R.id.edit_unjk_sfzh);
		editHphm = (EditText) findViewById(R.id.edit_unjk_hphm);
		spinHpzl = (Spinner) findViewById(R.id.spin_unjk_hpzl);

		// ---------------------------------------------------------
		hpzls = new ArrayList<KeyValueBean>();
		hpzls.add(new KeyValueBean("", "所有号牌种类"));
		for (KeyValueBean kv : GlobalData.hpzlList) {
			hpzls.add(kv);
		}
		GlobalMethod.changeAdapter(spinHpzl, hpzls, this);

		// 决定书预览
		findViewById(R.id.btn_unjk_preview).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						int pos = getSelectedItem();
						if (pos > -1) {
							JdsUnjkPrintBean v = unjkList.get(pos);
							ArrayList<JdsPrintBean> prints = PrintJdsTools
									.getPrintUnJkJdsContent(v,
											getContentResolver());
							Intent intent = new Intent(self,
									JdsPreviewActivity.class);
							intent.putExtra("jds", prints);
							startActivity(intent);
						} else {
							GlobalMethod.showErrorDialog("未选择记录", self);
						}
					}
				});

		findViewById(R.id.btn_unjk_query).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String sfzh = editSfzh.getText().toString()
								.toUpperCase();
						String hphm = editHphm.getText().toString()
								.toUpperCase();
						KeyValueBean kv = (KeyValueBean) spinHpzl
								.getSelectedItem();
						String hpzl = kv.getKey();
						if (TextUtils.isEmpty(sfzh) && TextUtils.isEmpty(hphm)) {
							GlobalMethod.showErrorDialog("查询条件不能全为空", self);
							return;
						}

						if (!TextUtils.isEmpty(sfzh) && !IDCard.Verify(sfzh)) {
							GlobalMethod.showErrorDialog("身份证号错误", self);
							return;
						}
						if (!TextUtils.isEmpty(hphm) && hphm.length() < 7) {
							GlobalMethod.showErrorDialog("车牌号码错误", self);
							return;
						}
						QueryUnjkJdsThread thread = new QueryUnjkJdsThread(
								uploadHandler, sfzh, hpzl, hphm);
						thread.doStart();
					}
				});

		findViewById(R.id.btn_unjk_print_jds).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						int pos = getSelectedItem();
						if (pos < 0) {
							GlobalMethod.showErrorDialog("未选择记录", self);
						} else {
							JdsUnjkPrintBean v = unjkList.get(pos);
							ArrayList<JdsPrintBean> conts = PrintJdsTools
									.getPrintUnJkJdsContent(v,
											getContentResolver());
							printJdsBySelect(conts);
						}
					}
				});
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setItemsUnSelected();
                jdsList.get(position).setSelected(true);
                ((CommTwoRowUploadSelectListAdapter) getListView().getAdapter())
                        .notifyDataSetChanged();
            }
        });
		findViewById(R.id.btn_unjk_quite).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		IsOpenThread opThread = new IsOpenThread(isOpenHandler);
		opThread.start();
	}

	/**
	 * 重新载入数据
	 */
	private void referListView() {
		if (unjkList == null)
			unjkList = new ArrayList<JdsUnjkPrintBean>();
		if (jdsList == null)
			jdsList = new ArrayList<CommKeySelectedBean>();
		jdsList.clear();
		if (!unjkList.isEmpty()) {
			for (JdsUnjkPrintBean jds : unjkList) {
				jdsList.add(new CommKeySelectedBean(jds, false));
			}
		}

		CommTwoRowUploadSelectListAdapter adapter = (CommTwoRowUploadSelectListAdapter) getListView()
				.getAdapter();
		if (adapter == null) {
			adapter = new CommTwoRowUploadSelectListAdapter(this, jdsList);
			getListView().setAdapter(adapter);
		}
		adapter.notifyDataSetChanged();
	}

	private int getSelectedItem() {
		int index = -1;
		if (jdsList == null || jdsList.isEmpty())
			return index;
		for (int i = 0; i < jdsList.size(); i++) {
			if (jdsList.get(i).isSelected())
				return i;
		}
		return index;
	}

	private void setItemsUnSelected() {
		for (CommKeySelectedBean ks : jdsList) {
			ks.setSelected(false);
		}
	}

	private void printJdsBySelect(List<JdsPrintBean> conts) {
		if (TextUtils.isEmpty(printerInfo.getValue())) {
			GlobalMethod.showDialog("错误信息", "没有配置默认打印机!", "返回", self);
			return;
		}
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableIntent);
			return;
		}
		if (btp == null)
			return;
		if (btp.getBluetoothStatus() != BlueToothPrint.BLUETOOTH_STREAMED) {
			// 没有建立蓝牙串口流
			int errorStaus = btp.createSocket(btAdapter);
			if (errorStaus != BlueToothPrint.SOCKET_SUCCESS) {
				GlobalMethod.showErrorDialog(
						btp.getBluetoothCodeMs(errorStaus), self);
				return;
			}
		}
		int status = btp.printJdsByBluetooth(conts);
		// 打印错误描述
		if (status != BlueToothPrint.PRINT_SUCCESS) {
			GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
		}
	}

	Handler isOpenHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			String error = b.getString("error");
			if (!TextUtils.equals(error, "1")) {
				GlobalMethod.showDialogWithListener("系统提示", "该模块正在建设中，暂时无法使用",
						"确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}, self);
			}
		}
	};

	class IsOpenThread extends Thread {
		private Handler mHandler;

		public IsOpenThread(Handler handler) {
			this.mHandler = handler;
		}

		@Override
		public void run() {
			String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
			RestfulDao dao = RestfulDaoFactory.getDao();
			WebQueryResult<String> wr = dao.isOpenUnjk(jybh);
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("error", wr.getResult());
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

	}

	class QueryUnjkJdsThread extends Thread {

		private Handler mHandler;
		private String sfzh, hpzl, hphm;

		public QueryUnjkJdsThread(Handler handler, String sfzh, String hpzl,
				String hphm) {
			this.mHandler = handler;
			this.sfzh = sfzh;
			this.hpzl = hpzl;
			this.hphm = hphm;
		}

		/**
		 * 启动线程
		 */
		public void doStart() {
			// 显示进度对话框
			progressDialog = new ProgressDialog(self);
			progressDialog.setTitle("提示");
			progressDialog.setMessage("系统正在查询请稍等...");
			progressDialog.setCancelable(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			this.start();
		}

		/**
		 * 线程运行，上传盘查，成功后发送信号给进度条
		 */
		@Override
		public void run() {
			RestfulDao dao = RestfulDaoFactory.getDao();
			WebQueryResult<List<JdsUnjkPrintBean>> wr = dao.getUnJkJds(sfzh,
					hpzl, hphm);
			String error = GlobalMethod.getErrorMessageFromWeb(wr);
			if (TextUtils.isEmpty(error)) {
				List<JdsUnjkPrintBean> result = wr.getResult();
				if (result != null && !result.isEmpty()) {
					unjkList = result;
					error = "共查询到" + unjkList.size() + "条记录";
				} else {
					error = "未查询到记录";
				}
			}
			if (progressDialog.isShowing())
				progressDialog.dismiss();
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("error", error);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

	}

	Handler uploadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			String totalError = b.getString("error");
			GlobalMethod.showDialog("系统提示", totalError, "确定", self);
			referListView();
		}
	};

}
