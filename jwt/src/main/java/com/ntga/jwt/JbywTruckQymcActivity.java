package com.ntga.jwt;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.OneLineSelectAdapter;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.TwoLineSelectBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalMethod;
import com.ntga.database.MessageDao;
import com.ntga.thread.QueryDrvVehThread;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class JbywTruckQymcActivity extends ActionBarListActivity implements
		OnClickListener {

	private EditText edQymc;
	private TextView tvCount;
	private Context self;
	private MessageDao mDao;
	private OneLineSelectAdapter adapter;
	private List<TwoLineSelectBean> beans;
	private Button btnQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jbyw_truck_qymc);
		self = this;
		mDao = new MessageDao(this);
		edQymc = (EditText) findViewById(R.id.edit_qymc);
		tvCount = (TextView) findViewById(R.id.tv_dw_count);
		int row = mDao.getQymcCount();
		tvCount.setText("共有单位" + row + "个");
		beans = new ArrayList<TwoLineSelectBean>();
		adapter = new OneLineSelectAdapter(self, R.layout.one_row_select_item,
				beans);
		getListView().setAdapter(adapter);
		List<KeyValueBean> kvs = mDao.queryQymc("");
		referListView(kvs);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		btnQuery = (Button) findViewById(R.id.btn_query_qymc);
		btnQuery.setOnClickListener(this);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			btnQuery.setVisibility(View.INVISIBLE);
			btnQuery.setWidth(0);
			btnQuery.setHeight(0);
			edQymc.addTextChangedListener(new TextWatcher() {
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					if (s.length() < 2)
						return;
					String mc = s.toString();
					List<KeyValueBean> list = mDao.queryQymc(mc);
					if (list != null && !list.isEmpty()) {
						referListView(list);
						tvCount.setText("共查询到" + list.size() + "个结果");
						return;
					}
					tvCount.setText("没有符合条件的记录");
				}
			});
		}
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				// 单选,修改其他为不选
				for (int i = 0; i < beans.size(); i++) {
					TwoLineSelectBean c = beans.get(i);
					if (i == position)
						c.setSelect(!c.isSelect());
					else
						c.setSelect(false);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void referListView(List<KeyValueBean> kvs) {
		beans.clear();
		if (!kvs.isEmpty()) {
			for (KeyValueBean kv : kvs) {
				TwoLineSelectBean bean = new TwoLineSelectBean(kv.getValue(),
						kv.getKey());
				beans.add(bean);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_query_qymc:
			String mc = edQymc.getText().toString();
			if (mc.length() < 2) {
				GlobalMethod.showErrorDialog("关键字不能小于两位", self);
				return;
			}
			referListView(new ArrayList<KeyValueBean>());
			List<KeyValueBean> list = mDao.queryQymc(mc);
			if (list != null && !list.isEmpty()) {
				referListView(list);
				tvCount.setText("共查询到" + list.size() + "个结果");
				return;
			}
			tvCount.setText("没有符合条件的记录");
			break;
		case R.id.btn_save:
			// 确定
			int pos = getSelectItem();
			if (pos > -1) {
				TwoLineSelectBean w = beans.get(pos);
				KeyValueBean kv = new KeyValueBean(w.getText2(), w.getText1());
				Intent i = new Intent();
				i.putExtra(QueryDrvVehThread.RESULT_QYMC, kv);
				setResult(RESULT_OK, i);
				finish();
			} else {
				Toast.makeText(self, "请选择一个公司名称", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btn_cancel:
			// 退出
			finish();
			break;
		default:
			break;
		}

	}

	private int getSelectItem() {
		int position = -1;
		int i = 0;
		while (beans.size() > 0 && i < beans.size()) {
			if (beans.get(i).isSelect()) {
				position = i;
				break;
			}
			i++;
		}
		return position;
	}

	static class QueryQymcHandler extends Handler {

		private final WeakReference<JbywTruckQymcActivity> myActivity;

		public QueryQymcHandler(JbywTruckQymcActivity activity) {
			myActivity = new WeakReference<JbywTruckQymcActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			JbywTruckQymcActivity ac = myActivity.get();
			if (ac != null) {
				ac.operQueryQymcHandler(msg);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void operQueryQymcHandler(Message msg) {
		Bundle b = msg.getData();
		WebQueryResult<List<KeyValueBean>> webResult = (WebQueryResult<List<KeyValueBean>>) b
				.getSerializable(QueryDrvVehThread.RESULT_QYMC);
		if (webResult.getStatus() == HttpStatus.SC_OK) {
			List<KeyValueBean> re = webResult.getResult();
			if (re != null && !re.isEmpty()) {
				mDao.addAllQymc(re);
				tvCount.setText("共有单位" + re.size() + "个");
				return;
			}
		}
		Toast.makeText(self, "查询结果错误", Toast.LENGTH_LONG).show();
		tvCount.setText("未能从服务器中获取数据");

	}

	@Override
	protected void onDestroy() {
		mDao.closeDb();
		super.onDestroy();
	}

}
