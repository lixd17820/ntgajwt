package com.ntga.jwt;

import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.bean.QueryResultBean;
import com.ntga.dao.GlobalMethod;
import com.ntga.zhcx.ZhcxHandler;
import com.ntga.zhcx.ZhcxOneRecordListAdapter;
import com.ntga.zhcx.ZhcxThread;
import com.ydjw.pojo.Glcx;
import com.ydjw.pojo.GlobalQueryResult;

import java.util.ArrayList;
import java.util.List;

public class ZhcxOneRecordListActivity extends ActionBarListActivity {

	protected static final int MENU_COPY_VALUE = 0;
	private GlobalQueryResult zhcx;
	private Glcx[] glcxs;
	private List<QueryResultBean> qrs;
	private Context self;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comm_no_button_list);
		self = this;
		zhcx = (GlobalQueryResult) getIntent().getSerializableExtra("zhcx");
		setTitle(zhcx.getCxms());
		String[][] contents = zhcx.getContents();
		String[] comment = zhcx.getComments();
		String[] names = zhcx.getNames();

		qrs = new ArrayList<QueryResultBean>();
		for (int i = 0; i < names.length; i++) {
			qrs.add(new QueryResultBean(names[i], comment[i], contents[0][i]));
		}
		ZhcxOneRecordListAdapter adapter = new ZhcxOneRecordListAdapter(this,
				qrs);
		getListView().setAdapter(adapter);
		glcxs = zhcx.getGlcxs();
		if (zhcx != null && !TextUtils.isEmpty(zhcx.getBdxx())) {
			GlobalMethod.showDialog("系统比对信息", zhcx.getBdxx(), "确定", self);
		}
		// 设置右键菜单
		getListView().setOnCreateContextMenuListener(
				new View.OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu,
							View arg1, ContextMenuInfo menuInfo) {
						AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfo;
						int pos = mi.position;
						if (pos > -1) {
							menu.setHeaderTitle("请选择");
							menu.add(Menu.NONE, MENU_COPY_VALUE, Menu.NONE,
									"复制内容");
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (glcxs != null && glcxs.length > 0) {
			for (int i = 0; i < glcxs.length; i++) {
				menu.add(0, i, Menu.NONE, glcxs[i].getGlcxms());
			}
			return true;
		}
		return false;
	}

	private String getContentsValue(String sourField) {
		for (QueryResultBean q : qrs) {
			if (q.getField().toUpperCase().equals(sourField.toUpperCase()))
				return q.getValue();
		}
		return "";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id > -1) {
			String cxid = glcxs[id].getGlcxid();
			String[] destFields = glcxs[id].getDestField().split(",");
			String[] sourFields = glcxs[id].getSourField().split(",");
			String where = "";
			for (int i = 0; i < sourFields.length; i++) {
				where += " AND " + destFields[i] + "='"
						+ getContentsValue(sourFields[i]) + "'";
			}
			if (!TextUtils.isEmpty(where)) {
				where = where.substring(5);
				ZhcxHandler handler = new ZhcxHandler(self);
				ZhcxThread thread = new ZhcxThread(handler);
				thread.doStart(self, cxid, where);
				Log.e("where", where);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final int pos = mi.position;
		if (pos > -1) {
			ZhcxOneRecordListAdapter adapter = (ZhcxOneRecordListAdapter) getListView()
					.getAdapter();
			QueryResultBean qr = adapter.getItem(pos);
			String value = qr.getValue();
			if (item.getItemId() == MENU_COPY_VALUE) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(value);
				Toast.makeText(self, value + " 已复制", Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}

}
