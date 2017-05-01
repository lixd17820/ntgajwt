package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.bean.MenuGridBean;
import com.ntga.bean.MenuOptionBean;
import com.ntga.jwt.R;
import com.ntga.tools.MainLoading;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ConfigMainActivity extends ActionBarListActivity {
	private Context self;
	private ArrayAdapter<String> adapter;
	private List<String> strList;
	private ArrayList<MenuOptionBean> menus;

	// private Map<String, Integer> imgs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comm_no_button_list);
		self = this;
		setTitle("系统配置");
		strList = new ArrayList<String>();
		List<MenuGridBean> list = MainLoading.parseMenuXml(self);
		if (list != null && !list.isEmpty()) {
			MenuGridBean mg = list.get(2);
			if (mg != null && mg.getOptions() != null
					&& !mg.getOptions().isEmpty()) {
				menus = mg.getOptions();
				for (MenuOptionBean m : menus) {
					strList.add(m.getMenuName());
				}
			}
		}
		adapter = new ArrayAdapter<String>(self,
				android.R.layout.simple_list_item_1, strList);
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				if (position < 0 && menus != null && !menus.isEmpty())
					return;
				MenuOptionBean m = menus.get(position);
				Intent intent = null;
				if (!TextUtils.isEmpty(m.getPck())
						&& !TextUtils.isEmpty(m.getClassName())) {
					intent = new Intent();
					intent.setComponent(new ComponentName(m.getPck(), m
							.getClassName()));
					if (!TextUtils.isEmpty(m.getDataName())
							&& !TextUtils.isEmpty(m.getData())) {
						intent.putExtra(m.getDataName(), m.getData());
					}
					intent.putExtra("title", m.getMenuName());
					startActivity(intent);
				}

			}
		});
	}
}