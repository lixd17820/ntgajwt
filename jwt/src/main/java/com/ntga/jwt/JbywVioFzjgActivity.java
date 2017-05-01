package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.android.provider.fixcode.Fixcode;
import com.ntga.adaper.OnSpinnerItemSelected;
import com.ntga.bean.KeyValueBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ViolationDAO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

public class JbywVioFzjgActivity extends ActionBarActivity {

	private Spinner spShenFen, spChenShi;
	private EditText edFzjgms;
	private Context self;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jbyw_vio_fzjg);
		self = this;
		String oldFzjg = getIntent().getStringExtra("fzjg");
		Log.e("JbywVioFzjgActivity", oldFzjg);
		spShenFen = (Spinner) findViewById(R.id.sp_shen_fen);
		spChenShi = (Spinner) findViewById(R.id.sp_chen_shi);
		edFzjgms = (EditText) findViewById(R.id.edit_fzjg);
		edFzjgms.setKeyListener(null);
		// 设置省份列表的
		GlobalMethod.changeAdapter(spShenFen, GlobalData.sfList, this, true);
		// 省份变化监听
		spShenFen.setOnItemSelectedListener(shenfenChangeListener);
		// 城市变化监听
		spChenShi.setOnItemSelectedListener(chenshiChangeListener);
		if (!TextUtils.isEmpty(oldFzjg))
			changeFzjgCombox(oldFzjg);
		findViewById(R.id.btn_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String fzjg = edFzjgms.getText().toString();
						if (TextUtils.isEmpty(fzjg)) {
							GlobalMethod.showErrorDialog("发证机关不能为空", self);
							return;
						}
						Intent i = new Intent();
						i.putExtra("fzjg", fzjg);
						setResult(RESULT_OK, i);
						finish();
					}
				});

		findViewById(R.id.btn_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	private void changeFzjgCombox(String fzjg) {
		spShenFen.setOnItemSelectedListener(null);
		String wh = "dmz='" + fzjg + "'";
		ArrayList<KeyValueBean> fzList = ViolationDAO.getAllFrmCode(
				GlobalConstant.CHENSHI, getContentResolver(), new String[] {
						Fixcode.FrmCode.DMZ, Fixcode.FrmCode.DMSM3 }, wh, null);
		if (fzList != null && !fzList.isEmpty()) {
			GlobalMethod.changeSpinnerSelect(spShenFen, fzjg.substring(0, 1),
					GlobalConstant.VALUE);
			List<KeyValueBean> cityList = createCityList(fzjg.substring(0, 1));
			GlobalMethod.changeAdapter(spChenShi, cityList, this, true);
			GlobalMethod.changeSpinnerSelect(spChenShi, fzjg,
					GlobalConstant.KEY, true);
		}
		spShenFen.setOnItemSelectedListener(shenfenChangeListener);
	}

	private OnSpinnerItemSelected shenfenChangeListener = new OnSpinnerItemSelected() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1,
				int position, long arg3) {
			if (position < 0)
				return;
			KeyValueBean select = (KeyValueBean) spShenFen.getSelectedItem();
			String sf = select.getValue();
			if (TextUtils.isEmpty(sf)) {
				GlobalMethod.changeAdapter(spChenShi, null, (Activity) self,
						true);
				edFzjgms.setText("");
			} else {
				List<KeyValueBean> list = createCityList(sf);
				GlobalMethod.changeAdapter(spChenShi, list, (Activity) self,
						true);
				if (spChenShi.getCount() > 1)
					spChenShi.setSelection(1);
			}
		}
	};

	private OnSpinnerItemSelected chenshiChangeListener = new OnSpinnerItemSelected() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1,
				int position, long arg3) {
			if (position > -1) {
				edFzjgms.setText(((KeyValueBean) spChenShi.getSelectedItem())
						.getKey());
			}
		}
	};

	/**
	 * 创建城市列表，并更下拉框的内容
	 */
	private List<KeyValueBean> createCityList(String sf) {
		String wh = "substr(dmz,1,1)='" + sf + "'";
		List<KeyValueBean> cityList = ViolationDAO.getAllFrmCode(
				GlobalConstant.CHENSHI, getContentResolver(), new String[] {
						Fixcode.FrmCode.DMZ, Fixcode.FrmCode.DMSM3 }, wh, null);
		return cityList;
		// 生成列表
		// GlobalMethod.changeAdapter(spChenShi, cityList, this);
	}

}
