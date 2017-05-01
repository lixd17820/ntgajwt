package com.ntga.jwt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.android.provider.wfdmcode.Wfdmcode;
import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.TwoLineSelectAdapter;
import com.ntga.bean.TwoLineSelectBean;
import com.ntga.bean.WfdmBean;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ConfigWfdmActivity extends ActionBarListActivity {

	private EditText dm;
	private EditText fk;
	private EditText fz;
	private EditText hz;
	private Button butSearch;
	private Button butWfdmDetail;
	private Button buWfxwOk;
	private ContentResolver resolver;
	private List<WfdmBean> wfxws = null;
	private List<TwoLineSelectBean> contents = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resolver = this.getContentResolver();
		setContentView(R.layout.jwt_search_wfdm);
		setTitle("查询违法代码");
		dm = (EditText) findViewById(R.id.Edit_dm);
		fk = (EditText) findViewById(R.id.Edit_fk);
		fz = (EditText) findViewById(R.id.Edit_fz);
		hz = (EditText) findViewById(R.id.Edit_hz);
		butSearch = (Button) findViewById(R.id.Butt_search);
		butWfdmDetail = (Button) findViewById(R.id.Butt_wfdm_detail);
		buWfxwOk = (Button) findViewById(R.id.Butt_wfdm_ok);

		int comefrom = getIntent().getIntExtra("comefrom", 0);
		if (comefrom == 0) {
			buWfxwOk.setVisibility(Button.INVISIBLE);
		}
		createContents();
		TwoLineSelectAdapter ad = new TwoLineSelectAdapter(this,
				R.layout.two_line_list_item, contents);
		getListView().setAdapter(ad);
		butSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String where = "";
				// 加入代码条件
				if (!TextUtils.isEmpty(dm.getText()))
					where += " AND " + Wfdmcode.VioCodeWfdm.WFXW + " like '%"
							+ dm.getText() + "%'";
				if (!TextUtils.isEmpty(fk.getText()))
					where += " AND " + Wfdmcode.VioCodeWfdm.FKJE_DUT + "="
							+ fk.getText();
				if (!TextUtils.isEmpty(fz.getText()))
					where += " AND " + Wfdmcode.VioCodeWfdm.WFJFS + "="
							+ fz.getText();
				// 加入违法行为汉字描述
				Editable qhz = hz.getText();
				if (!TextUtils.isEmpty(qhz)) {
					String[] z = qhz.toString().trim().split(" ");
					if (z.length > 0) {
						where += " AND " + Wfdmcode.VioCodeWfdm.WFMS
								+ " like '";
						for (String s : z) {
							where += "%" + s + "%";
						}
						where += "'";
					}
				}
				// 罚款标记
				if (((CheckBox) findViewById(R.id.ChBox_fkbj)).isChecked()) {
					where += " AND " + Wfdmcode.VioCodeWfdm.FKBJ + "='1'";
				}
				// 警告标记
				if (((CheckBox) findViewById(R.id.ChBox_jgbj)).isChecked()) {
					where += " AND " + Wfdmcode.VioCodeWfdm.JGBJ + "='1'";
				}
				// 强制措施
				if (((CheckBox) findViewById(R.id.ChBox_qzcxbj)).isChecked()) {
					where += " AND " + Wfdmcode.VioCodeWfdm.QZCSLX
							+ " is not null";
				}
				if (((CheckBox) findViewById(R.id.ChBox_yxqx)).isChecked()) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String yxqz = sdf.format(new Date());
					where += " AND " + Wfdmcode.VioCodeWfdm.YXQZ + " > '"
							+ yxqz + "'";
				}
				if (TextUtils.isEmpty(where)) {
					GlobalMethod.showErrorDialog("请输入至少一个条件!",
							ConfigWfdmActivity.this);
					return;
				} else {
					where = where.substring(5);
				}

				wfxws = ViolationDAO.queryWfxwByCondition(where, resolver);
//				// 如果有效期限已选择,过滤有效的
//				if (((CheckBox) findViewById(R.id.ChBox_yxqx)).isChecked()) {
//					int index = 0;
//					while (index < wfxws.size()) {
//						if (!ViolationDAO.isYxWfdm(wfxws.get(index)))
//							wfxws.remove(index);
//						else
//							index++;
//					}
//				}
				if (wfxws != null && wfxws.size() > 0) {
					Toast.makeText(ConfigWfdmActivity.this,
							"查到" + wfxws.size() + "个符合条件的违法代码",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(ConfigWfdmActivity.this, "没找到相应的违法代码",
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
					WfdmBean w = wfxws.get(pos);
					StringBuilder sb = new StringBuilder();
					sb.append("违法代码：").append(w.getWfxw()).append("\n");
					sb.append("违法描述：").append(w.getWfms()).append("\n");
					sb.append("处罚依据：").append(w.getFltw()).append("\n");
					sb.append("罚款金额：").append(w.getFkjeDut()).append("\n");
					sb.append("违法记分：").append(w.getWfjfs()).append("\n");
					sb.append("可否罚款：").append(
							Integer.valueOf(w.getFkbj()) > 0 ? "可以罚款" : "不可罚款")
							.append("\n");
					sb.append("可否警告：").append(
							Integer.valueOf(w.getJgbj()) > 0 ? "可以警告" : "不可警告")
							.append("\n");
					sb.append("强制措施：").append(
							TextUtils.isEmpty(w.getQzcslx()) ? "无"
									: ViolationDAO.getQzcslxMs(w.getQzcslx()))
							.append("\n");
					sb.append("是否有效：").append(
							ViolationDAO.isYxWfdm(w) ? "有效代码" : "无效代码");
					GlobalMethod.showDialog("代码详细描述", sb.toString(), "确定",
							ConfigWfdmActivity.this);
				} else {
					Toast.makeText(ConfigWfdmActivity.this, "请选择一个违法代码",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		buWfxwOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos = getSelectItem();
				if (pos > -1) {
					WfdmBean w = wfxws.get(pos);
					if (!ViolationDAO.isYxWfdm(w)) {
						GlobalMethod.showErrorDialog("不是有效代码,不可以处罚!",
								ConfigWfdmActivity.this);
						return;
					}
					Intent i = new Intent();
					i.putExtra(Wfdmcode.VioCodeWfdm.WFXW, w.getWfxw());
					setResult(RESULT_OK, i);
					finish();
				} else {
					Toast.makeText(ConfigWfdmActivity.this, "请选择一个违法代码",
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
			for (WfdmBean w : wfxws) {
				TwoLineSelectBean ts = new TwoLineSelectBean();
				ts.setText1(w.getWfxw() + ":" + w.getWfms());
				ts.setText2(" 罚款" + w.getFkjeDut() + "元记" + w.getWfjfs() + "分");
				contents.add(ts);
			}
		}
	}

}
