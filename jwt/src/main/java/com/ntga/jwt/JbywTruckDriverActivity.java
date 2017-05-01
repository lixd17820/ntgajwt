package com.ntga.jwt;

import java.lang.ref.WeakReference;

import com.ntga.bean.TruckDriverBean;
import com.ntga.bean.TruckVehicleBean;
import com.ntga.bean.VioDrvBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalMethod;
import com.ntga.thread.CommUploadThread;
import com.ntga.thread.QueryDrvVehThread;
import com.ntga.zapc.ZapcReturn;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class JbywTruckDriverActivity extends ActionBarActivity implements
		View.OnClickListener {
	private EditText editDabh, editJszh, editXm, editLxdz, editSjhm, editFzjg,
			editZjcx;
	private Context self;
	public static final int HANDLER_QUERY_DRV = 100;
	public static final int HANDLER_UP_TRUCK_DRV = 101;
	private TruckDriverBean drv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jbyw_truck_driver);
		self = this;
		setTitle("重型车驾驶人登记");
		editDabh = (EditText) findViewById(R.id.edit_dabh);
		editJszh = (EditText) findViewById(R.id.edit_jszh);
		editXm = (EditText) findViewById(R.id.edit_xm);
		editLxdz = (EditText) findViewById(R.id.edit_lxdz);
		editSjhm = (EditText) findViewById(R.id.edit_sjhm);
		editFzjg = (EditText) findViewById(R.id.edit_fzjg);
		editZjcx = (EditText) findViewById(R.id.edit_zjcx);
		findViewById(R.id.btn_query_drv).setOnClickListener(this);
		findViewById(R.id.btn_query_fzjg).setOnClickListener(this);
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_right).setOnClickListener(this);
		drv = new TruckDriverBean();
		TruckVehicleBean truck = (TruckVehicleBean) getIntent()
				.getSerializableExtra(JbywTruckVehicleActivity.INTENT_VEH);
		if (truck != null) {
			drv.setVehId(truck.getId());
			((TextView) findViewById(R.id.tv_veh_info)).setText("车牌号码："
					+ truck.getHphm());
		}

	}

	protected String checkDrv(TruckDriverBean drv) {
		if (TextUtils.isEmpty(drv.getDabh()) || drv.getDabh().length() != 12) {
			return "档案编号错误或不是十二位";
		}
		if (TextUtils.isEmpty(drv.getFzjg())) {
			return "发证机关不能为空";
		}
		if (TextUtils.isEmpty(drv.getJszh()) || drv.getJszh().length() != 18) {
			return "驾驶证号错误";
		}
		if (TextUtils.isEmpty(drv.getSjhm()) || !drv.getSjhm().startsWith("1")
				|| drv.getSjhm().length() != 11) {
			return "手机号码错误";
		}
		if (TextUtils.isEmpty(drv.getLxdz())) {
			return "联系地址不能为空";
		}
		if (TextUtils.isEmpty(drv.getXm())) {
			return "姓名不能为空";
		}
		if (TextUtils.isEmpty(drv.getZjcx())) {
			return "准驾车型不能为空";
		}
		return "";
	}

	static class QueryHandler extends Handler {

		private final WeakReference<JbywTruckDriverActivity> myActivity;
		private int handlerCata;

		public QueryHandler(JbywTruckDriverActivity activity, int handlerCata) {
			myActivity = new WeakReference<JbywTruckDriverActivity>(activity);
			this.handlerCata = handlerCata;
		}

		@Override
		public void handleMessage(Message msg) {
			JbywTruckDriverActivity ac = myActivity.get();
			if (ac != null) {
				if (handlerCata == HANDLER_QUERY_DRV)
					ac.queryDrvHandler(msg);
				else if (handlerCata == HANDLER_UP_TRUCK_DRV)
					ac.uploadDrvHandler(msg);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void queryDrvHandler(Message msg) {
		Bundle b = msg.getData();
		WebQueryResult<VioDrvBean> re = (WebQueryResult<VioDrvBean>) b
				.getSerializable(QueryDrvVehThread.RESULT_DRV);
		String err = GlobalMethod.getErrorMessageFromWeb(re);
		if (TextUtils.isEmpty(err)) {
			VioDrvBean drv = re.getResult();
			if (drv != null) {
				editJszh.setText(drv.getSfzmhm());
				editFzjg.setText(drv.getFzjg());
				editXm.setText(drv.getXm());
				editLxdz.setText(drv.getLxzsxxdz());
				editSjhm.setText(drv.getLxdh());
				editZjcx.setText(drv.getZjcx());
			} else
				GlobalMethod.showErrorDialog("未查询到驾驶员信息", self);
		} else {
			GlobalMethod.showErrorDialog("未查询到驾驶员信息", self);
		}
	}

	@SuppressWarnings("unchecked")
	public void uploadDrvHandler(Message msg) {
		Bundle data = msg.getData();
		WebQueryResult<ZapcReturn> re = (WebQueryResult<ZapcReturn>) data
				.getSerializable(CommUploadThread.RESULT_UP_TRUCK_DRV);
		String err = GlobalMethod.getErrorMessageFromWeb(re);
		if (TextUtils.isEmpty(err)) {
			ZapcReturn zr = re.getResult();
			if (TextUtils.equals(zr.getCgbj(), "1")) {
				GlobalMethod.showDialogWithListener("系统提示", "上传登记信息成功", "确定",
						exitSystem, self);
				return;
			} else
				GlobalMethod.showDialog("系统提示", zr.getScms(), "确定", self);
		} else
			GlobalMethod.showErrorDialog(err, self);
	}

	DialogInterface.OnClickListener exitSystem = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			Intent i = new Intent();
			setResult(RESULT_OK, i);
			finish();
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == ViolationActivity.REQCODE_FZJG) {
				Bundle b = data.getExtras();
				String fzjg = b.getString("fzjg");
				editFzjg.setText(fzjg);
			}
		} else {
			// edWfdd.setText("");
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_query_drv) {

			String dabh = editDabh.getText().toString();
			if (TextUtils.isEmpty(dabh) || dabh.length() != 12) {
				GlobalMethod.showErrorDialog("档案编号不正确", self);
				return;
			}
			String[] params = new String[] { dabh, "" };
			QueryHandler handler = new QueryHandler(
					JbywTruckDriverActivity.this, HANDLER_QUERY_DRV);
			QueryDrvVehThread thread = new QueryDrvVehThread(handler,
					QueryDrvVehThread.QUERY_DRV_DABH_INFO, params, self);
			thread.doStart();
		} else if (view.getId() == R.id.btn_query_fzjg) {
			Intent intent = new Intent(self, JbywVioFzjgActivity.class);
			String fzjgmc = editFzjg.getText().toString();
			intent.putExtra("fzjg", fzjgmc);
			startActivityForResult(intent, ViolationActivity.REQCODE_FZJG);
		} else if (view.getId() == R.id.btn_left) {
			String fzjg = editFzjg.getText().toString();
			String dabh = editDabh.getText().toString();
			String jszh = editJszh.getText().toString().toUpperCase();
			String xm = editXm.getText().toString();
			String sjhm = editSjhm.getText().toString();
			String lxdz = editLxdz.getText().toString();
			String zjcx = editZjcx.getText().toString();
			drv.setDabh(dabh);
			drv.setFzjg(fzjg);
			drv.setJszh(jszh);
			drv.setLxdz(lxdz);
			drv.setSjhm(sjhm);
			drv.setXm(xm);
			drv.setZjcx(zjcx);
			String err = checkDrv(drv);
			if (!TextUtils.isEmpty(err)) {
				GlobalMethod.showErrorDialog(err, self);
				return;
			}
			QueryHandler uhandler = new QueryHandler(
					JbywTruckDriverActivity.this, HANDLER_UP_TRUCK_DRV);
			CommUploadThread th = new CommUploadThread(uhandler,
					CommUploadThread.UPLOAD_TRUCK_DRV, new Object[] { drv },
					self);
			th.doStart();
		} else if (view.getId() == R.id.btn_right) {
			setResult(RESULT_CANCELED);
			finish();
		}

	}

}
