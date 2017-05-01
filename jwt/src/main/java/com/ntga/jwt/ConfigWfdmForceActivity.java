package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.android.provider.wfdmcode.Wfdmcode;
import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.TwoLineSelectAdapter;
import com.ntga.bean.TwoLineSelectBean;
import com.ntga.bean.WfxwForceBean;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ViolationDAO;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ConfigWfdmForceActivity extends ActionBarListActivity {

	private EditText dm, hz;
	private Button butSearch;
	private Button butWfdmDetail;
	private Button buWfxwOk;
	private ContentResolver resolver;
	private List<WfxwForceBean> wfxws = null;
	private List<TwoLineSelectBean> contents = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resolver = this.getContentResolver();
		setContentView(R.layout.jwt_force_wfdm);
		setTitle("查询强制措施代码");
		dm = (EditText) findViewById(R.id.Edit_dm);
		hz = (EditText) findViewById(R.id.Edit_hz);
		butSearch = (Button) findViewById(R.id.Butt_search);
		butWfdmDetail = (Button) findViewById(R.id.Butt_wfdm_detail);
		buWfxwOk = (Button) findViewById(R.id.Butt_wfdm_ok);

		int comefrom = getIntent().getIntExtra("comefrom", 0);
		if (comefrom == 0) {
			buWfxwOk.setVisibility(Button.INVISIBLE);
		}
		wfxws =ViolationDAO.queryQzcsByCond(null, resolver);
		createContents();
		TwoLineSelectAdapter ad = new TwoLineSelectAdapter(this,
				R.layout.two_line_list_item, contents);
		getListView().setAdapter(ad);
		butSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String where = "";
				// 加入代码条件
				if (!TextUtils.isEmpty(dm.getText()))
					where += " AND " + Wfdmcode.ForceCode.WFDM + " like '%"
							+ dm.getText() + "%'";
				// 加入违法行为汉字描述
				Editable qhz = hz.getText();
				if (!TextUtils.isEmpty(qhz)) {
					String[] z = qhz.toString().trim().split(" ");
					if (z.length > 0) {
						where += " AND " + Wfdmcode.ForceCode.WFXW + " like '";
						for (String s : z) {
							where += "%" + s + "%";
						}
						where += "'";
					}
				}

				if (!TextUtils.isEmpty(where)) {
					where = where.substring(5);
				}

				wfxws = ViolationDAO.queryQzcsByCond(where, resolver);
				// // 如果有效期限已选择,过滤有效的
				// if (((CheckBox) findViewById(R.id.ChBox_yxqx)).isChecked()) {
				// int index = 0;
				// while (index < wfxws.size()) {
				// if (!ViolationDAO.isYxWfdm(wfxws.get(index)))
				// wfxws.remove(index);
				// else
				// index++;
				// }
				// }
				if (wfxws != null && wfxws.size() > 0) {
					Toast.makeText(ConfigWfdmForceActivity.this,
							"查到" + wfxws.size() + "个符合条件的违法代码",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(ConfigWfdmForceActivity.this, "没找到相应的违法代码",
							Toast.LENGTH_LONG).show();
				}
				createContents();
				((TwoLineSelectAdapter) getListView().getAdapter())
						.notifyDataSetChanged();
			}
		});

		butWfdmDetail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int pos = getSelectItem();
				if (pos > -1) {
					WfxwForceBean w = wfxws.get(pos);
					StringBuilder sb = new StringBuilder();
					sb.append("违法代码：").append(w.getWfdm()).append("\n");
					sb.append("违法描述：").append(w.getWfxw()).append("\n");
					sb.append("处罚依据：").append(w.getQzyj()).append("\n");
					sb.append("强制项目：").append(w.getBz());
					GlobalMethod.showDialog("代码详细描述", sb.toString(), "确定",
							ConfigWfdmForceActivity.this);
				} else {
					Toast.makeText(ConfigWfdmForceActivity.this, "请选择一个违法代码",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		buWfxwOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos = getSelectItem();
				if (pos > -1) {
					WfxwForceBean w = wfxws.get(pos);
					Intent i = new Intent();
					i.putExtra(Wfdmcode.VioCodeWfdm.WFXW, w.getWfxw());
					setResult(RESULT_OK, i);
					finish();
				} else {
					Toast.makeText(ConfigWfdmForceActivity.this, "请选择一个违法代码",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				// 单选,修改其他为不选
				for (int i = 0; i < contents.size(); i++) {
					TwoLineSelectBean c = contents.get(i);
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

	private int getSelectItem() {
		int position = -1;
		int i = 0;
		while (contents.size() > 0 && i < contents.size()) {
			if (contents.get(i).isSelect()) {
				position = i;
				break;
			}
			i++;
		}
		return position;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// peccDb.close();
	}

	private void createContents() {
		if (contents == null)
			contents = new ArrayList<TwoLineSelectBean>();
		else
			contents.clear();
		if (wfxws != null && wfxws.size() > 0) {
			for (WfxwForceBean w : wfxws) {
				TwoLineSelectBean ts = new TwoLineSelectBean();
				ts.setText1(w.getWfdm() + "，强制措施：" + w.getBz());
				ts.setText2(w.getWfxw());
				contents.add(ts);
			}
		}
	}

}
