package com.ntga.adaper;

import java.util.List;

import com.ntga.bean.TwoColTwoSelectBean;
import com.ntga.jwt.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommTwoColTwoSelectListAdapter extends
		ArrayAdapter<TwoColTwoSelectBean> {

	Activity context;

	public CommTwoColTwoSelectListAdapter(Activity _context,
			List<TwoColTwoSelectBean> objects) {
		super(_context, R.layout.comm_two_line_select_item, objects);
		this.context = _context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.comm_two_line_select_item, null);
		}
		TwoColTwoSelectBean kv = getItem(position);
		if (kv != null) {
			ImageView imgUp = (ImageView) row.findViewById(R.id.imageView1);
			ImageView imgDown = (ImageView) row.findViewById(R.id.imageView2);
			TextView tv1 = (TextView) row.findViewById(R.id.textView1);
			TextView tv2 = (TextView) row.findViewById(R.id.textView2);
			tv1.setText(kv.getLeftText());
			tv2.setText(kv.getRightText());
			imgUp.setImageResource(kv.isSelectUp() ? R.drawable.ic_action_accept
					: R.drawable.ic_action_import_export);
			imgDown.setImageResource(kv.isSelectDown() ? android.R.drawable.button_onoff_indicator_on
					: android.R.drawable.button_onoff_indicator_off);
		}
		return row;

	}
}
