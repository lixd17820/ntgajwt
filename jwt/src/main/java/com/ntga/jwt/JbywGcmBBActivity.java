package com.ntga.jwt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ntga.bean.GcmBbInfoBean;
import com.ntga.bean.GcmBbddBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.GlobalSystemParam;
import com.ntga.database.MessageDao;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class JbywGcmBBActivity extends ActionBarActivity {
	protected static final int REQ_START_GPS = 0;

	private Button btnUpdateDd, btnUploadBb, btnChangeTime,btnChangeDate;

	private TextView tvInfo;

	private Spinner spinBbdd, spinFjrs, spinLxfs;
	private EditText editLxhm, editGpsId, editBbsj;
	private CheckBox chkIsOtherGps;
	private MessageDao dao;
	private List<KeyValueBean> dds;
	private boolean isSave = false;
	private boolean isUpload = false;
	Context self;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private GcmBbInfoBean info;
	private String[] lxfs = new String[] { "手机", "电台", "其它" };
	private List<String> fjrs = new ArrayList<String>();
	private LocationManager locm;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		dao = new MessageDao(self);
		setContentView(R.layout.jbyw_gcm_bb);
		spinBbdd = (Spinner) findViewById(R.id.spin_gcm_bbdd);
		btnUpdateDd = (Button) findViewById(R.id.btn_gcm_dd);
		btnUploadBb = (Button) findViewById(R.id.btn_gcm_up);
		btnChangeTime = (Button) findViewById(R.id.btn_gcm_change_time);
		btnChangeDate = (Button) findViewById(R.id.btn_gcm_change_date);
		spinFjrs = (Spinner) findViewById(R.id.spin_gcm_fjrs);
		spinLxfs = (Spinner) findViewById(R.id.spin_gcm_lxfs);
		editLxhm = (EditText) findViewById(R.id.edit_gcm_lxhm);
		editGpsId = (EditText) findViewById(R.id.edit_gcm_gps_id);
		chkIsOtherGps = (CheckBox) findViewById(R.id.chk_gcm_is_owner);
		editBbsj = (EditText) findViewById(R.id.edit_gcm_bbsj);
		editBbsj.setText(sdf.format(new Date()));
		editGpsId.setVisibility(View.INVISIBLE);
		tvInfo = (TextView) findViewById(R.id.tv_gcm_info);
		tvInfo.setText("");
		for (int i = 0; i < 20; i++) {
			fjrs.add(i + "");
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(self,
				android.R.layout.simple_spinner_item, lxfs);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(self,
				android.R.layout.simple_spinner_item, fjrs);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinLxfs.setAdapter(adapter);
		spinFjrs.setAdapter(adapter2);
		referGcmdd();
		initLocation();
		GlobalSystemParam.isGpsUpload = true;
		if (!locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			GlobalMethod.showDialogWithListener("系统提示", "请打开GPS定位设备", "打开",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(
									Settings.ACTION_SECURITY_SETTINGS);

							startActivityForResult(intent, REQ_START_GPS);
						}
					}, self);
		}

		btnUpdateDd.setOnClickListener(updateDd);
		btnUploadBb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSave && isUpload) {
					GlobalMethod.showErrorDialog("报备已上传，无需重复报备", self);
					return;
				}
				if (isSave || saveBbInfo()) {
					UploadBbInfoThread thread = new UploadBbInfoThread(
							uploadHander);
					thread.doStart();
				}

			}

		});

		chkIsOtherGps.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				editGpsId.setVisibility(isChecked ? View.VISIBLE
						: View.INVISIBLE);
			}
		});
		btnChangeTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GlobalMethod.changeTime(editBbsj, self);
			}
		});
		btnChangeDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GlobalMethod.changeDate(editBbsj, self);
			}
		});

	}

	protected boolean saveBbInfo() {
		if (spinBbdd.getSelectedItemPosition() < 0) {
			GlobalMethod.showErrorDialog("报备地点不能为空，请点屏幕下方按扭从服务器上获取报备地点", self);
			return false;
		}
		String gpsId = editGpsId.getText().toString();
		String lxhm = editLxhm.getText().toString();
		if (chkIsOtherGps.isChecked()) {
			if (TextUtils.isEmpty(gpsId) || !TextUtils.isDigitsOnly(gpsId)
					|| gpsId.length() != 8) {
				GlobalMethod.showErrorDialog("GPS设备号码不正确", self);
				return false;
			}
		} else {
			gpsId = GlobalData.grxx.get(GlobalConstant.YHBH);
		}
		if (TextUtils.isEmpty(lxhm)) {
			GlobalMethod.showErrorDialog("联系号码不能为空", self);
			return false;
		}
		info = new GcmBbInfoBean();
		info.setBbmc(GlobalMethod.getKeyFromSpinnerSelected(spinBbdd,
				GlobalConstant.KEY));
		info.setJybh(GlobalData.grxx.get(GlobalConstant.YHBH));
		info.setFjrs((String) spinFjrs.getSelectedItem());
		info.setKssj(editBbsj.getText().toString());
		info.setGpsId(gpsId);
		info.setLxfs((String) spinLxfs.getSelectedItem());
		info.setLxhm(lxhm);
		info.setDjsj(sdf2.format(new Date()));
		long id = dao.insertGcmBbInfo(info);
		if (id > 0) {
			isSave = true;
			info.setId("" + id);
			return true;
		}
		return false;
	}

	OnClickListener updateDd = new OnClickListener() {

		@Override
		public void onClick(View v) {
			BbddGetThread thread = new BbddGetThread(updateBbddHander);
			thread.doStart();

		}
	};

	private String uploadBbInfo() {
		RestfulDao wrdao = RestfulDaoFactory.getDao();
		WebQueryResult<ZapcReturn> re = wrdao.uploadGcmBb(info);
		String err = GlobalMethod.getErrorMessageFromWeb(re);
		if (!TextUtils.isEmpty(err)) {
			return err;
		}
		if (TextUtils.equals("1", re.getResult().getCgbj())) {
			dao.updateGcmBdInfoScbj(info.getId());
			isUpload = true;
		} else {
			return "报备上传失败";
		}
		return null;

	}

	private void referGcmdd() {
		List<GcmBbddBean> temp = dao.getAllGcmBbdd();
		if (dds == null)
			dds = new ArrayList<KeyValueBean>();
		dds.clear();
		for (GcmBbddBean gd : temp) {
			dds.add(new KeyValueBean(gd.getId(), gd.getMc()));
		}
		GlobalMethod.changeAdapter(spinBbdd, dds, (Activity) self);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		dao.closeDb();
		super.onDestroy();
	}

	private void initLocation() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(true);
		criteria.setBearingRequired(true);
		criteria.setAltitudeRequired(true);
		criteria.setCostAllowed(true);
		String serverName = Context.LOCATION_SERVICE;
		locm = (LocationManager) getSystemService(serverName);
	}

	class BbddGetThread extends Thread {
		private Handler mHandler;

		public BbddGetThread(Handler mHandler) {
			this.mHandler = mHandler;
		}

		public void doStart() {
			// 显示进度对话框
			if (progressDialog == null)
				progressDialog = new ProgressDialog(self);
			progressDialog.setTitle("提示");
			progressDialog.setMessage("正在更新报备地点请稍等...");
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			this.start();
		}

		@Override
		public void run() {
			RestfulDao wrdao = RestfulDaoFactory.getDao();
			WebQueryResult<List<GcmBbddBean>> re = wrdao.getAllGcmDd();
			String err = GlobalMethod.getErrorMessageFromWeb(re);
			int row = 0;
			if (TextUtils.isEmpty(err)) {
				List<GcmBbddBean> dds = re.getResult();
				row = dds.size();
				if (dds != null && !dds.isEmpty()) {
					dao.delAllGcmBbdd();
					for (GcmBbddBean gcmBbddBean : dds) {
						dao.insertGcmBbdd(gcmBbddBean);
					}

				}
			}
			mHandler.sendEmptyMessage(row);
		}

	}

	Handler updateBbddHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int row = msg.what;
			referGcmdd();
			progressDialog.dismiss();
			GlobalMethod.showDialog("系统提示", "共更新" + row + "条关城门报备地点", "知道了",
					self);
		}

	};

	class UploadBbInfoThread extends Thread {
		private Handler mHandler;

		public UploadBbInfoThread(Handler mHandler) {
			this.mHandler = mHandler;
		}

		public void doStart() {
			// 显示进度对话框
			if (progressDialog == null)
				progressDialog = new ProgressDialog(self);
			progressDialog.setTitle("提示");
			progressDialog.setMessage("正在上传报备地点请稍等...");
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			this.start();
		}

		@Override
		public void run() {
			String err = uploadBbInfo();
			Bundle data = new Bundle();
			data.putString("err", err);
			Message msg = mHandler.obtainMessage();
			msg.setData(data);
			mHandler.sendMessage(msg);
		}

	}

	Handler uploadHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			Bundle d = msg.getData();
			if (d != null) {
				if (TextUtils.isEmpty(d.getString("err"))) {
					GlobalMethod.showDialogWithListener("系统提示", "报备上传成功", "确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									setResult(RESULT_OK);
									finish();
								}
							}, self);
				} else {
					GlobalMethod.showErrorDialog(d.getString("err"), self);
				}
			} else {
				GlobalMethod.showErrorDialog("上传出现错误，请关闭重试", self);
			}
		}

	};

}
