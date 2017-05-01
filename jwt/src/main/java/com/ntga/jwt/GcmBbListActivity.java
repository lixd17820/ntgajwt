package com.ntga.jwt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.CommTwoRowUploadSelectListAdapter;
import com.ntga.bean.CommKeySelectedBean;
import com.ntga.bean.GcmBbInfoBean;
import com.ntga.database.MessageDao;

import java.util.ArrayList;
import java.util.List;

public class GcmBbListActivity extends ActionBarListActivity {
	private List<CommKeySelectedBean> bbInfo;
	private Context self;
	private MessageDao dao;
	private Button btnQuite, btnNewBb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.comm_two_btn_show_list);
		setTitle(getIntent().getStringExtra("title"));
		dao = new MessageDao(self);
		referList();
		btnQuite = (Button) findViewById(R.id.btn_left);
		btnNewBb = (Button) findViewById(R.id.btn_right);
		btnQuite.setText("退出");
		btnNewBb.setText("新增报备");
		btnQuite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		btnNewBb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(self, JbywGcmBBActivity.class);
				startActivityForResult(intent, 0);
			}
		});

	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0 && resultCode == RESULT_OK){
			referList();
		}
	}



	@Override
	protected void onDestroy() {
		dao.closeDb();
		super.onDestroy();
	}

	private void referList() {
		List<GcmBbInfoBean> list = dao.getAllGcmBbInfo();
		if (bbInfo == null)
			bbInfo = new ArrayList<CommKeySelectedBean>();
		bbInfo.clear();
		for (GcmBbInfoBean info : list) {
			info.setBbmc(dao.getGcmBbmc(info.getBbmc()));
			CommKeySelectedBean kv = new CommKeySelectedBean(info, false);
			bbInfo.add(kv);
		}
		CommTwoRowUploadSelectListAdapter adapter = (CommTwoRowUploadSelectListAdapter) getListView()
				.getAdapter();
		if (adapter == null) {
			adapter = new CommTwoRowUploadSelectListAdapter(this, bbInfo);
			getListView().setAdapter(adapter);
		}
		adapter.notifyDataSetChanged();
	}

}
