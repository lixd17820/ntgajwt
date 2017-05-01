package com.ntga.adaper;

import java.util.List;

import com.ntga.bean.CommKeySelectedBean;
import com.ntga.jwt.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommTwoRowUploadSelectListAdapter extends
		ArrayAdapter<CommKeySelectedBean> {

	Activity context;

	public CommTwoRowUploadSelectListAdapter(Activity _context,
			List<CommKeySelectedBean> objects) {
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
		CommKeySelectedBean kv = getItem(position);
		if (kv != null) {
			ImageView imgUp = (ImageView) row.findViewById(R.id.imageView1);
			ImageView imgDown = (ImageView) row.findViewById(R.id.imageView2);
			TextView tv1 = (TextView) row.findViewById(R.id.textView1);
			TextView tv2 = (TextView) row.findViewById(R.id.textView2);
			tv1.setText(kv.getKey().getUpText());
			tv2.setText(kv.getKey().getDownText());
			imgUp.setImageResource(kv.getKey().isUploaded() ? android.R.drawable.ic_menu_agenda
					: android.R.drawable.ic_menu_upload);
			imgDown.setImageResource(kv.isSelected() ? android.R.drawable.button_onoff_indicator_on
					: android.R.drawable.button_onoff_indicator_off);
		}
		return row;

	}
}
