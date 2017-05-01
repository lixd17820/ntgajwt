package com.ntga.jwt;

import com.ntga.dao.ConnCata;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.GlobalSystemParam;
import com.ntga.dao.ViolationDAO;
import com.ntga.tools.MainLoading;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ConfigNetworkActivity extends Activity {

	private ToggleButton togb;
	private ToggleButton tobIsGps;
	private ToggleButton toCheckSfzh;
	private Spinner spinUpFreq;
	private Context self;
	private LocationManager locm;

	private final int REQ_START_GPS = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_network);
		self = this;
		String serverName = Context.LOCATION_SERVICE;
		locm = (LocationManager) getSystemService(serverName);

		tobIsGps = (ToggleButton) findViewById(R.id.tog_gps_upload);
		spinUpFreq = (Spinner) findViewById(R.id.spin_upload_sjjg);

		toCheckSfzh = (ToggleButton) findViewById(R.id.tog_check_sfzh);
		toCheckSfzh.setChecked(GlobalSystemParam.isCheckFjdcSfzm);
		toCheckSfzh.setOnCheckedChangeListener(checkSfzhListener);

		tobIsGps.setChecked(GlobalSystemParam.isGpsUpload);

		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item, new Integer[] { 1, 2, 3,
						4, 5 });
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinUpFreq.setAdapter(adapter);
		spinUpFreq.setSelection(GlobalSystemParam.uploadFreq - 1);

		togb = (ToggleButton) findViewById(R.id.TogButNetwork);
		togb.setChecked(GlobalMethod.isOnline());
		togb.setOnClickListener(netWorkClick);

		tobIsGps.setOnCheckedChangeListener(gpsTobChangeListener);

		spinUpFreq.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Integer freq = (Integer) spinUpFreq.getItemAtPosition(position);
				if (freq != GlobalSystemParam.uploadFreq) {
					ViolationDAO.saveGpsUploadFreq(freq, getContentResolver());
					// 改变了发送的频率
					if (MainLoading.checkServerRunning(self, "com.ntga.jwt",
							"com.ntga.jwt.MainReferService")) {
						stopService(new Intent(self, MainReferService.class));
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					startService(new Intent(self, MainReferService.class));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	// private String checkLogin(WebQueryResult<LoginMessage> lm) {
	// if (lm == null || lm.getStatus() != HttpStatus.SC_OK) {
	// return "网络连接错误,不能转为在线模式";
	// }
	// int code = lm.getResult().getCode();
	// // 验证未通过
	// if (code != 0) {
	// // GlobalMethod.showDialogWithListener("提示信息", lm.getResult()
	// // .getMessage(), "确定", exitSystem, self);
	// return lm.getResult().getMessage() + ",不能转为在线模式";
	// }
	// // 返回数据不正确,显示错误信息并退出程序
	// // 需要重新对话框的确定按扭监听
	// if (lm.getResult().getFields().size() != lm.getResult().getValues()
	// .size()) {
	// // GlobalMethod.showDialogWithListener("提示信息",
	// // "数据传输错误,不能正常处罚,按确定退出重新登录", "确定", exitSystem, self);
	// return "数据传输错误,,不能转为在线模式";
	// }
	// return null;
	// }

	OnClickListener changeMode = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// GlobalData.lineMode = GlobalConstant.OFFLINE;
			referTogButton();
		}
	};

	private void referTogButton() {
		togb.setChecked(GlobalMethod.isOnline());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_START_GPS) {
			if (locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				tobIsGps.setChecked(true);
				GlobalSystemParam.isGpsUpload = true;
			}
		}

	}

	private View.OnClickListener netWorkClick = new View.OnClickListener() {

		private OnClickListener changeOffLine = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				GlobalData.connCata = ConnCata.OFFCONN;
				referTogButton();
			}
		};

		@Override
		public void onClick(View v) {
			if (!GlobalMethod.isOnline()) {
				// 目前处于离线模式之下,需要改为在线模式，发送验证信息

			} else {
				GlobalMethod.showDialogTwoListener("提示信息",
						"是否确定将连接模式改为离线，这将导致许多功能无法使用！", "确定", "取消",
						changeOffLine, self);
			}
			referTogButton();
			// if (!GlobalData.isOnline()) {
			// 目前处于离线模式之下,需要改为在线模式
			// Intent intent = new Intent(self, LoginActivity.class);
			// startActivityForResult(intent,
			// MainPageTabActivity.LOGINREQUESTCODE);

			// } else
			// GlobalData.setLineMode(!GlobalData.isOnline());
		}
	};

	private OnCheckedChangeListener checkSfzhListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			GlobalSystemParam.isCheckFjdcSfzm = isChecked;
		}
	};

	private OnCheckedChangeListener gpsTobChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			if (isChecked) {
				// 如果需要打开GPS
				if (!locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					tobIsGps.setChecked(false);
					GlobalMethod.showDialogWithListener("系统提示", "请打开GPS定位设备",
							"打开", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Settings.ACTION_SECURITY_SETTINGS);

									startActivityForResult(intent,
											REQ_START_GPS);
								}
							}, self);
				} else {
					GlobalSystemParam.isGpsUpload = isChecked;
				}
			} else {
				GlobalSystemParam.isGpsUpload = isChecked;
			}
		}
	};

}
