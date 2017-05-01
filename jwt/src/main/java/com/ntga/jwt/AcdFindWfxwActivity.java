package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.android.provider.fixcode.Fixcode;
import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.OneLineSelectAdapter;
import com.ntga.bean.AcdWfxwBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.TwoLineSelectBean;
import com.ntga.dao.AcdSimpleDao;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ViolationDAO;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AcdFindWfxwActivity extends ActionBarListActivity {

	private ArrayList<TwoLineSelectBean> wfxwApapterList = null;
	private EditText editZwjz;
	private Spinner spinAcdWfxwfl;
	private List<KeyValueBean> wfxwflList;
	private ArrayList<KeyValueBean> wfxwList;
	private AcdWfxwBean acdWfxw;
	private String jtfs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acd_find_wfxw);
		Bundle b = getIntent().getExtras();
		jtfs = b.getString("jtfs");
		acdWfxw = (AcdWfxwBean) b.getSerializable("acdWfxw");

		editZwjz = (EditText) findViewById(R.id.edit_acd_zwjz);
		spinAcdWfxwfl = (Spinner) findViewById(R.id.spin_acd_wfxwfl);

		wfxwflList = new ArrayList<KeyValueBean>();
		wfxwflList.add(new KeyValueBean("0", "违法行为"));
		wfxwflList.add(new KeyValueBean("1", "非违法行为"));
		GlobalMethod.changeAdapter(spinAcdWfxwfl, wfxwflList, this);

		spinAcdWfxwfl.setSelection(1);

		wfxwList = createWfxwList(jtfs, "");
		changeListContent(wfxwList);

		// 查找符合条件的交通方式
		findViewById(R.id.but_acd_wfxw_find).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						wfxwList = createWfxwList(jtfs, editZwjz.getText()
								.toString().trim());
						changeListContent(wfxwList);
					}
				});
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
				OneLineSelectAdapter ad = (OneLineSelectAdapter) parent
						.getAdapter();
				ad.notifyDataSetChanged();
			}
		});
		findViewById(R.id.but_acd_wfxw_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						int postion = getSelectItem();
						if (postion > -1) {
							TwoLineSelectBean wfxw = wfxwApapterList
									.get(postion);
							Intent i = new Intent();
							Bundle b = new Bundle();
							String wfdm = wfxw.getText2();
							acdWfxw = AcdSimpleDao.getAcdWfxwByWfdm(wfdm, getContentResolver());
							b.putSerializable("acdWfxw", acdWfxw);
							i.putExtras(b);
							setResult(RESULT_OK, i);
							finish();
						} else {
							Toast.makeText(AcdFindWfxwActivity.this,
									"请选择一条违法行为", Toast.LENGTH_LONG).show();
						}
					}
				});

		findViewById(R.id.but_acd_wfxw_quite).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	/**
	 * 
	 * @param list
	 */
	private void changeListContent(ArrayList<KeyValueBean> list) {
		if (wfxwApapterList == null)
			wfxwApapterList = new ArrayList<TwoLineSelectBean>();
		else
			wfxwApapterList.clear();
		for (KeyValueBean kv : list) {
			wfxwApapterList.add(new TwoLineSelectBean(kv.getValue(), kv
					.getKey()));
		}
		OneLineSelectAdapter ard = (OneLineSelectAdapter) getListView()
				.getAdapter();
		if (ard == null) {
			ard = new OneLineSelectAdapter(this, R.layout.one_row_select_item,
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

//	private ArrayList<KeyValueBean> createWfxwList(String jtfs) {
//		int wfzl = 1;
//		// 改变违法行为选择项
//		int wfxwfl = spinAcdWfxwfl.getSelectedItemPosition();
//		if (wfxwfl > 0) {
//			// 非违法行为
//			wfzl = 6;
//		} else {
//			wfzl = getWfclflByJtfs(jtfs.substring(0, 1));
//		}
//		ArrayList<KeyValueBean> list = ViolationDAO.getAllFrmCode(
//				GlobalConstant.ACD_WFXW, getContentResolver(), new String[] {
//						Fixcode.FrmCode.DMZ, Fixcode.FrmCode.DMSM1 },
//				Fixcode.FrmCode.DMSM3 + "='" + wfzl + "'", Fixcode.FrmCode.DMZ);
//		return list;
//	}

	private ArrayList<KeyValueBean> createWfxwList(String jtfs, String china) {
		int wfzl = 1;
		// 改变违法行为选择项
		int wfxwfl = spinAcdWfxwfl.getSelectedItemPosition();
		if (wfxwfl > 0) {
			// 非违法行为
			wfzl = 6;
		} else {
			wfzl = getWfclflByJtfs(jtfs.substring(0, 1));
		}
		String where = Fixcode.FrmCode.DMSM3 + "='" + wfzl + "'";
		if (!TextUtils.isEmpty(china))
			where += " and " + Fixcode.FrmCode.DMSM1 + " like '%" + china
					+ "%'";
		ArrayList<KeyValueBean> list = ViolationDAO.getAllFrmCode(
				GlobalConstant.ACD_WFXW, getContentResolver(), new String[] {
						Fixcode.FrmCode.DMZ, Fixcode.FrmCode.DMSM1 }, where,
				Fixcode.FrmCode.DMZ);
		return list;
	}

	/**
	 * 根据交通方式决定其违法的车辆分类
	 * 
	 * @param jtfs
	 * @return 1 机动车 2 非机动车 3 步行或乘车 5 其他
	 */
	private int getWfclflByJtfs(String jtfs) {
		int wfzl = 1;
		if (TextUtils.equals(jtfs, "A") || TextUtils.equals(jtfs, "C")) {
			wfzl = 3;
		} else if (TextUtils.equals(jtfs, "F")) {
			wfzl = 2;
		} else if (TextUtils.equals(jtfs, "X")) {
			wfzl = 5;
		}
		return wfzl;
	}
}
