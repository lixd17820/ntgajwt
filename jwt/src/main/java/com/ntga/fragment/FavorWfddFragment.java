package com.ntga.fragment;

import java.util.ArrayList;
import java.util.List;

import com.ntga.adaper.OneLineSelectAdapter;
import com.ntga.bean.FavorWfddBean;
import com.ntga.bean.TwoLineSelectBean;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.WfddDao;
import com.ntga.jwt.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class FavorWfddFragment extends ListFragment {

	private ArrayList<TwoLineSelectBean> oneLineList = null;
	private ContentResolver resolver;

	/** 用户自定义执勤地点数组 */
	List<FavorWfddBean> favorWfddList;
	List<String> favorWfddStrList;
	private Activity self;
	private Button buttonDetail, favorOKBt, delFavorButton;

	@Override
	public View onCreateView(LayoutInflater in, ViewGroup c, Bundle si) {
		return in.inflate(R.layout.wfdd_fever_list, c, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		self = getActivity();
		resolver = self.getContentResolver();
		delFavorButton = (Button) self.findViewById(R.id.delButton);
		buttonDetail = (Button) self.findViewById(R.id.detailButton);
		// 自选地点页面标签"确定"按扭动作
		favorOKBt = (Button) self.findViewById(R.id.favorOKButton);
		changeFavorWfddList();
		buttonDetail.setOnClickListener(clickListener);
		favorOKBt.setOnClickListener(clickListener);
		delFavorButton.setOnClickListener(clickListener);
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int postion = getSelectItem();
			if (postion < 0) {
				Toast.makeText(self, "请选择一条违法地点", Toast.LENGTH_LONG).show();
				return;
			}
			FavorWfddBean wfdd = favorWfddList.get(postion);
			if (v == buttonDetail) {
				String content = "地点名称：" + wfdd.getFavorLdmc() + "\n";
				content += "系统名称：" + wfdd.getSysLdmc() + "\n";
				content += "行政区划：" + wfdd.getXzqh() + "\n";
				content += "道路名称：" + wfdd.getDldm() + "\n";
				content += "路段(公里)：" + wfdd.getLddm() + "\n";
				content += "路段(米)：" + wfdd.getMs();
				GlobalMethod.showDialog("自定义地点详细信息", content, "返回", self);
			} else if (v == favorOKBt) {
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putString(
						"wfddDm",
						wfdd.getXzqh() + wfdd.getDldm() + wfdd.getLddm()
								+ wfdd.getMs());
				b.putString("wfddMc", wfdd.getFavorLdmc());
				i.putExtras(b);
				self.setResult(Activity.RESULT_OK, i);
				self.finish();
			} else if (v == delFavorButton) {
				WfddDao.delFavorWfddById(wfdd.getId(), resolver);
				changeFavorWfddList();
			}
		}
	};

	private void changeFavorWfddList() {
		favorWfddList = WfddDao.getAllFavorWfdd(resolver);
		if (oneLineList == null)
			oneLineList = new ArrayList<TwoLineSelectBean>();
		else
			oneLineList.clear();
		if (favorWfddList != null) {
			for (FavorWfddBean fwfdd : favorWfddList) {
				oneLineList.add(new TwoLineSelectBean(fwfdd.getFavorLdmc(),
						fwfdd.getDldm()));
			}
		}
		OneLineSelectAdapter ard = (OneLineSelectAdapter) this.getListAdapter();
		if (ard == null) {
			ard = new OneLineSelectAdapter(self, R.layout.one_row_select_item,
					oneLineList);
			this.getListView().setAdapter(ard);
		}
		// favorWfddStrList = WfddDao.getListFromFavorWfddBeans(favorWfddList);
		// ard.clear();
		// for (String s : favorWfddStrList) {
		// ard.add(s);
		// }
		// if (ard.getCount() > 0)
		// listViewFavorWfdd.clearChoices();
		ard.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		for (int i = 0; i < oneLineList.size(); i++) {
			TwoLineSelectBean c = oneLineList.get(i);
			if (i == position)
				c.setSelect(!c.isSelect());
			else
				c.setSelect(false);
		}
		OneLineSelectAdapter ad = (OneLineSelectAdapter) this.getListView()
				.getAdapter();
		ad.notifyDataSetChanged();
	}


	private int getSelectItem() {
		int position = -1;
		int i = 0;
		while (oneLineList.size() > 0 && i < oneLineList.size()) {
			if (oneLineList.get(i).isSelect()) {
				position = i;
				break;
			}
			i++;
		}
		return position;
	}

}
