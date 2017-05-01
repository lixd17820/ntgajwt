package com.ntga.jwt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;

import com.android.provider.fixcode.Fixcode;
import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.OnSpinnerItemSelected;
import com.ntga.adaper.OneLineSelectAdapter;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.TwoLineSelectBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ViolationDAO;

import java.util.ArrayList;

public class ZapcXzqhListActitivy extends ActionBarListActivity {

	private Spinner spinShen;
	private Spinner spinCity;

	private Context self;
	private ArrayList<KeyValueBean> listShenfen = null;
	private ArrayList<KeyValueBean> listCity = null;
	private ArrayList<KeyValueBean> listHjqh = null;
	private ArrayList<TwoLineSelectBean> twoLineHjqh = null;
	private String hjqh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.zapc_xzqh);
		hjqh = getIntent().getStringExtra(ZapcRyxxActivity.HJQH_EXTRA);

		spinShen = (Spinner) findViewById(R.id.spin_shenfen);
		spinCity = (Spinner) findViewById(R.id.spin_city);
		listShenfen = ViolationDAO.getAllFrmCode(GlobalConstant.SHENFEN,
				getContentResolver(), new String[] { Fixcode.FrmCode.DMZ,
						Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		GlobalMethod.changeAdapter(spinShen, listShenfen, (Activity) self);

		if (!TextUtils.isEmpty(hjqh) && TextUtils.isDigitsOnly(hjqh)
				&& hjqh.length() == 6) {
			// 非新建，是修改
			String sfdm = hjqh.substring(0, 2);
			spinShen.setSelection(GlobalMethod.getPositionByKey(listShenfen,
					sfdm), false);
			boolean isZxs = changeAdpaterByShenfen(sfdm);
			if (!isZxs) {
				// 是省份，创建列表项并选择
				String cityQh = hjqh.substring(0, 4) + "00";
				spinCity.setSelection(GlobalMethod.getPositionByKey(listCity,
						cityQh), false);
				changeListByCityDm(cityQh);
			}
			// 是直辖市，直接在列表中选择
			if (listHjqh != null && listHjqh.size() > 0) {
				int pos = GlobalMethod.getPositionByKey(listHjqh, hjqh);
				if (pos > -1) {
					twoLineHjqh.get(pos).setSelect(true);
					OneLineSelectAdapter ad = (OneLineSelectAdapter)getListView(). getAdapter();
					ad.notifyDataSetChanged();
				}
			}
		}
		spinShen.setOnItemSelectedListener(shenSelectedListener);
		spinCity.setOnItemSelectedListener(citySelListener);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				// 单选,修改其他为不选
				for (int i = 0; i < twoLineHjqh.size(); i++) {
					TwoLineSelectBean c = twoLineHjqh.get(i);
					if (i == position)
						c.setSelect(!c.isSelect());
					else
						c.setSelect(false);
				}
				OneLineSelectAdapter ad = (OneLineSelectAdapter) parent
						.getAdapter();
				ad.notifyDataSetChanged();
			}
		});

		// 确定按扭监听
		findViewById(R.id.but_hjqh_ok).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						int pos = getSelectItem();
						if (pos > -1) {
							Intent i = new Intent();
							i.putExtra(ZapcRyxxActivity.HJQH_EXTRA, listHjqh
									.get(pos).getKey());
							setResult(RESULT_OK, i);
							finish();
						} else {
							GlobalMethod.showErrorDialog("请选择一个行政区划!", self);
						}
					}
				});
		findViewById(R.id.but_hjqh_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setResult(RESULT_CANCELED);
						finish();
					}
				});
	}

	private int getSelectItem() {
		int position = -1;
		int i = 0;
		while (twoLineHjqh.size() > 0 && i < twoLineHjqh.size()) {
			if (twoLineHjqh.get(i).isSelect()) {
				position = i;
				break;
			}
			i++;
		}
		return position;
	}

	private void syonList() {
		if (listHjqh != null && listHjqh.size() > 0) {
			twoLineHjqh = new ArrayList<TwoLineSelectBean>();
			for (KeyValueBean kv : listHjqh) {
				TwoLineSelectBean ts = new TwoLineSelectBean(kv.getValue()
						+ "(" + kv.getKey() + ")", kv.getKey());
				twoLineHjqh.add(ts);
			}
		}
	}

	private OnSpinnerItemSelected shenSelectedListener = new OnSpinnerItemSelected() {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view,
				int position, long i) {
			Log.e("shenSelectedListener", "is onItemSelected");
			if (position < 0)
				return;
			String sfdm = listShenfen.get(position).getKey();
			changeAdpaterByShenfen(sfdm);
		}
	};

	private OnSpinnerItemSelected citySelListener = new OnSpinnerItemSelected() {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view,
				int position, long i) {
			Log.e("citySelListener", "is onItemSelected");
			String cityDm;
			if (position < 0) {
				Log.e("citySelListener", "postion less then 0");
			} else {
				cityDm = listCity.get(position).getKey();
				changeListByCityDm(cityDm);
			}
		}
	};

	/**
	 * 查询城市，去除省和直辖市
	 * 
	 * @param sfdm
	 */
	private void queryCityListByShen(String sfdm) {
		listCity = ViolationDAO.getAllFrmCode(GlobalConstant.CHENSHI,
				getContentResolver(), new String[] { Fixcode.FrmCode.DMSM2,
						Fixcode.FrmCode.DMSM3 }, "DMSM4='" + sfdm
						+ "'  AND DMSM2 not LIKE '__0000'",
				Fixcode.FrmCode.DMSM2);
	}

	/**
	 * 查询所有行政区划
	 * 
	 * @param cityDm
	 *            城市代码的前四位，直辖市代码的前两位
	 */
	private void queryHjqhByCity(String cityDm) {
		listHjqh = ViolationDAO.getAllFrmCode(GlobalConstant.QGXZQH,
				getContentResolver(), new String[] { Fixcode.FrmCode.DMZ,
						Fixcode.FrmCode.DMSM1 }, Fixcode.FrmCode.DMZ
						+ " like '" + cityDm + "%'", Fixcode.FrmCode.DMZ);
		syonList();
	}

	/**
	 * 
	 * @param sfdm
	 * @return 是直辖市返回true 是省份返回false
	 */
	private boolean changeAdpaterByShenfen(String sfdm) {
		boolean result = true;
		queryCityListByShen(sfdm);
		GlobalMethod.changeAdapter(spinCity, listCity, (Activity) self);
		if (listCity == null || listCity.size() == 0) {
			// 这就是直辖市，直接查找行政区划
			queryHjqhByCity(sfdm);
			OneLineSelectAdapter lisAdapter = new OneLineSelectAdapter(self,
					R.layout.one_row_select_item, twoLineHjqh);
			setListAdapter(lisAdapter);
		} else
			result = false;
		return result;
	}

	private void changeListByCityDm(String cityDm) {
		queryHjqhByCity(cityDm.substring(0, 4));
		OneLineSelectAdapter lisAdapter = new OneLineSelectAdapter(self,
				R.layout.one_row_select_item, twoLineHjqh);
		setListAdapter(lisAdapter);
	}
}
