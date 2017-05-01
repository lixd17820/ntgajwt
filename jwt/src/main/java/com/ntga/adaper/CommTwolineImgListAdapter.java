package com.ntga.adaper;

import java.util.List;

import com.ntga.bean.TwoLineSelectBean;
import com.ntga.jwt.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommTwolineImgListAdapter extends ArrayAdapter<TwoLineSelectBean> {

	Activity context;

	public CommTwolineImgListAdapter(Activity _context,
			List<TwoLineSelectBean> objects) {
		super(_context, R.layout.comm_img_two_line_list_item, objects);
		this.context = _context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.comm_img_two_line_list_item, null);
		}
		TwoLineSelectBean kv = getItem(position);
		if (kv != null) {
			ImageView img = (ImageView) row.findViewById(R.id.imageView1);
			TextView tv1 = (TextView) row.findViewById(R.id.TextView_left);
			TextView tv2 = (TextView) row.findViewById(R.id.TextView_right);
			tv1.setText(kv.getText1());
			tv2.setText(kv.getText2());

			if (kv.isSelect()) {
				img.setImageResource(R.drawable.ok);
			} else {
				img.setImageResource(R.drawable.warn_red);
			}
		}
		return row;

	}
}
