package com.ntga.jwt;

import java.util.ArrayList;

import com.acd.simple.provider.AcdSimple;
import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.TwoLineSelectAdapter;
import com.ntga.bean.AcdWftLawBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.TwoLineSelectBean;
import com.ntga.dao.AcdSimpleDao;
import com.ntga.dao.GlobalMethod;
import com.ntga.tools.ChangeIdNum;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AcdFindLawActivity extends ActionBarListActivity {

	private Spinner spinFl;
	private Context self;
	private EditText editFt, editFk, editFx, editWz;
	private Button btnFind, btnOk, btnDetail;
	private ArrayList<AcdWftLawBean> wftkList;
	private ArrayList<TwoLineSelectBean> wfxwApapterList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.acd_find_laws);
		wfxwApapterList = new ArrayList<TwoLineSelectBean>();
		spinFl = (Spinner) findViewById(R.id.spin_acd_wfxwfl);
		editFt = (EditText) findViewById(R.id.edit_acd_ft);
		editFk = (EditText) findViewById(R.id.edit_acd_fk);
		editFx = (EditText) findViewById(R.id.edit_acd_fx);
		editWz = (EditText) findViewById(R.id.edit_acd_zwjz);
		btnFind = (Button) findViewById(R.id.but_acd_wfxw_find);
		btnOk = (Button) findViewById(R.id.but_acd_wfxw_ok);
		btnDetail = (Button) findViewById(R.id.but_acd_wfxw_detail);
		GlobalMethod.changeAdapter(spinFl, createFl(), (Activity) self);
		btnFind.setOnClickListener(findFltkListener);
		btnOk.setOnClickListener(okListener);
		btnDetail.setOnClickListener(wfxwDetailListener);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				// 单选,修改其他为不选
				for (int i = 0; i < wfxwApapterList.size(); i++) {
					TwoLineSelectBean c = wfxwApapterList.get(i);
					if (i == position)
						c.setSelect(!c.isSelect());
					else
						c.setSelect(false);
				}
				TwoLineSelectAdapter ad = (TwoLineSelectAdapter) parent
						.getAdapter();
				ad.notifyDataSetChanged();
			}
		});
	}

	private ArrayList<KeyValueBean> createFl() {
		ArrayList<KeyValueBean> list = new ArrayList<KeyValueBean>();
		list.add(new KeyValueBean("", "全部"));
		list.add(new KeyValueBean("1", "中华人民共和国道路交通安全法"));
		list.add(new KeyValueBean("2", "中华人民共和国道路交通安全法实施条例"));
		list.add(new KeyValueBean("3", "江苏省道路交通安全条例"));
		list.add(new KeyValueBean("9", "机动车驾驶证申领和使用规定"));
		return list;
	}

	private OnClickListener findFltkListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String where = "";
			String ft = editFt.getText().toString();
			if (!TextUtils.isEmpty(ft)) {
				if (TextUtils.isDigitsOnly(ft))
					ft = ChangeIdNum.changToBignum(ft);
				ft = "'%" + ft + "条%'";
				where += " and " + AcdSimple.AcdLaws.TKMC + " like " + ft;
			}
			String fk = editFk.getText().toString();
			if (!TextUtils.isEmpty(fk)) {
				if (TextUtils.isDigitsOnly(fk))
					fk = ChangeIdNum.changToBignum(fk);
				fk = "'%" + fk + "款%'";
				where += " and " + AcdSimple.AcdLaws.TKMC + " like " + fk;
			}
			String fx = editFx.getText().toString();
			if (!TextUtils.isEmpty(fx)) {
				if (TextUtils.isDigitsOnly(fx))
					fx = ChangeIdNum.changToBignum(fx);
				fx = "'%" + fx + "款%'";
				where += " and " + AcdSimple.AcdLaws.TKMC + " like " + fx;
			}

			KeyValueBean kv = (KeyValueBean) spinFl.getSelectedItem();
			if (!TextUtils.isEmpty(kv.getKey())) {
				String lb = kv.getKey();
				where += " and " + AcdSimple.AcdLaws.XH + " like '" + lb + "%'";
			}
			String wz = editWz.getText().toString();
			if (!TextUtils.isEmpty(wz)) {
				wz = "'%" + wz + "%'";
				where += " and " + AcdSimple.AcdLaws.TKNR + " like " + wz;
			}
			if (!TextUtils.isEmpty(where)) {
				where = where.substring(5);
				wftkList = AcdSimpleDao.queryWftknrByCond(getContentResolver(),
						where);
				changeListContent();
			}
		}
	};

	private void createKvList() {
		if (wftkList != null && wftkList.size() > 0) {
			for (AcdWftLawBean wf : wftkList) {
				wfxwApapterList.add(new TwoLineSelectBean(wf.getXh(), wf
						.getTknr(), false));
			}

		}
	}

	private void changeListContent() {
		if (wfxwApapterList == null)
			wfxwApapterList = new ArrayList<TwoLineSelectBean>();
		else
			wfxwApapterList.clear();
		createKvList();
		TwoLineSelectAdapter ard = (TwoLineSelectAdapter) getListView()
				.getAdapter();
		if (ard == null) {
			ard = new TwoLineSelectAdapter(this, R.layout.two_line_list_item,
					wfxwApapterList);
			getListView().setAdapter(ard);
		}
		ard.notifyDataSetChanged();
	}

	private int getSelectItem() {
		int position = -1;
		int i = 0;
		while (wfxwApapterList.size() > 0 && i < wfxwApapterList.size()) {
			if (wfxwApapterList.get(i).isSelect()) {
				position = i;
				break;
			}
			i++;
		}
		return position;
	}

	private OnClickListener okListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int postion = getSelectItem();
			if (postion > -1) {
				AcdWftLawBean wfxw = wftkList.get(postion);
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putSerializable("wfxw", wfxw);
				i.putExtras(b);
				setResult(RESULT_OK, i);
				finish();
			} else {
				Toast.makeText(self, "请选择一条违法行为", Toast.LENGTH_LONG).show();
			}
		}
	};

	private OnClickListener wfxwDetailListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int postion = getSelectItem();
			if (postion > -1) {
				AcdWftLawBean wfxw = wftkList.get(postion);
				String message = "序号：" + wfxw.getXh() + "\n";
				message += "法律：" + wfxw.getFlmc() + "\n";
				message += "条款：" + wfxw.getTkmc() + "\n";
				message += "内容：" + wfxw.getTknr();
				GlobalMethod.showDialog("详细信息", message, "知道了", self);
			} else {
				Toast.makeText(self, "请选择一条违法行为", Toast.LENGTH_LONG).show();
			}

		}
	};

}
