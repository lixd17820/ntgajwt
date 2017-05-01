package com.ntga.jwt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.dao.GlobalMethod;
import com.ntga.jwt.R;
import com.ntga.xml.CommParserXml;
import com.ydjw.pojo.CxMenus;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ZhcxMainActivity extends ActionBarListActivity {
	private Context self;
	private List<CxMenus> zhcxMenus;
	private ArrayAdapter<String> adapter;
	private List<String> strList;

	// private Map<String, Integer> imgs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comm_no_button_list);
		self = this;
		setTitle("综合查询");
		createZhcxMenus();
		strList = new ArrayList<String>();
		if (zhcxMenus != null && !zhcxMenus.isEmpty()) {
			for (CxMenus m : zhcxMenus) {
				strList.add(m.getCxMenuName());
			}
		}
		adapter = new ArrayAdapter<String>(self,
				android.R.layout.simple_list_item_1, strList);
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				if (position < 0)
					return;
				CxMenus m = zhcxMenus.get(position);
				Intent intent = new Intent(self, ZhcxConditionActivity.class);
				intent.putExtra("zhcxItem", m);
				startActivity(intent);
			}
		});
	}

	private void createZhcxMenus() {
		File innDir = self.getFilesDir();
		if (!innDir.exists())
			innDir.mkdirs();
		File zhcxMunuXml = new File(innDir, "zhcx.xml");
		if (!zhcxMunuXml.exists())
			return;
		String xml = GlobalMethod.readFileContent(zhcxMunuXml);
		try {
			zhcxMenus = CommParserXml.ParseXmlToListObj(xml, CxMenus.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}