package com.ntga.adaper;

import java.util.List;

import com.ntga.bean.TwoLineSelectBean;
import com.ntga.jwt.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OneLineSelectAdapter extends ArrayAdapter<TwoLineSelectBean> {

	private int resourceId;

	public OneLineSelectAdapter(Context context, int textViewResourceId,
			List<TwoLineSelectBean> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = convertView;
		if (row == null) {
			row = inflater.inflate(resourceId, parent, false);
		}
		TwoLineSelectBean content = getItem(position);
		if (content != null) {
			TextView tv = (TextView) row.findViewById(R.id.text1);
			tv.setText(content.getText1());
			ImageView img = (ImageView) row.findViewById(R.id.image1);
			if (content.isSelect())
				img.setImageResource(android.R.drawable.btn_star_big_on);
			else
				img.setImageResource(android.R.drawable.btn_star_big_off);
		}
		return row;
	}

}
