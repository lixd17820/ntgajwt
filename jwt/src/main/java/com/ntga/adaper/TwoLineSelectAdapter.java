package com.ntga.adaper;

import java.util.List;

import com.ntga.bean.TwoLineSelectBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TwoLineListItem;

public class TwoLineSelectAdapter extends ArrayAdapter<TwoLineSelectBean> {

	private int resourceId;
	private boolean isImage;

	public TwoLineSelectAdapter(Context context, int textViewResourceId,
			List<TwoLineSelectBean> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		isImage = true;
	}

	public TwoLineSelectAdapter(Context context, int textViewResourceId,
			List<TwoLineSelectBean> objects, boolean isImage) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		this.isImage = isImage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TwoLineListItem view;
		if (convertView == null) {
			view = (TwoLineListItem) inflater
					.inflate(resourceId, parent, false);
		} else {
			view = (TwoLineListItem) convertView;
		}
		TwoLineSelectBean content = getItem(position);
		if (content != null) {
			if (view.getText1() != null) {
				view.getText1().setText(content.getText1());
			}
			if (view.getText2() != null) {
				view.getText2().setText(content.getText2());
			}

			ImageView img = (ImageView) view
					.findViewById(android.R.id.selectedIcon);
			if (isImage) {
				if (content.isSelect())
					img.setImageResource(android.R.drawable.btn_star_big_on);
				else
					img.setImageResource(android.R.drawable.btn_star_big_off);
			}else{
				img.setImageBitmap(null);
			}
		}
		return view;
	}

}
