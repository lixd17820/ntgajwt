package com.ntga.adaper;

import java.util.ArrayList;
import java.util.List;

import com.ntga.bean.TwoColTwoSelectBean;

import android.app.ListActivity;
import android.view.View;
import android.widget.ListView;

public class CommTwoRowSelectListActivity extends ListActivity {

	protected List<TwoColTwoSelectBean> beanList = null;
	protected CommTwoColTwoSelectListAdapter commAdapter;
	protected int selectedIndex;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		selectedIndex = position;
		if (position > -1) {
			for (TwoColTwoSelectBean bean : beanList) {
				bean.setSelectDown(false);
			}
			beanList.get(position).setSelectDown(true);
			commAdapter.notifyDataSetChanged();
		}
	}

	public CommTwoColTwoSelectListAdapter getCommAdapter() {
		return commAdapter;
	}

	public void initView() {
		selectedIndex = -1;
		if (beanList == null)
			beanList = new ArrayList<TwoColTwoSelectBean>();
		commAdapter = new CommTwoColTwoSelectListAdapter(this, beanList);
		getListView().setAdapter(commAdapter);
	}

}
