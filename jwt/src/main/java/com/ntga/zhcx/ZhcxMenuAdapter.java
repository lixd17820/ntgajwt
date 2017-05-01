package com.ntga.zhcx;

import java.util.List;

import com.ntga.dao.GlobalMethod;
import com.ntga.jwt.R;
import com.ydjw.pojo.CxMenus;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ZhcxMenuAdapter extends ArrayAdapter<CxMenus> {
	Activity context;

	public ZhcxMenuAdapter(Activity _context, List<CxMenus> objects) {
		super(_context, R.layout.grid_item, objects);
		this.context = _context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.grid_item, null);
		}

		CxMenus menu = getItem(position);

		ImageView iv = (ImageView) row.findViewById(R.id.image_item);
		TextView tv = (TextView) row.findViewById(R.id.text_item);
		iv.setImageResource(GlobalMethod.getImageResouseByName(menu.getImg()));
		tv.setText(menu.getCxMenuName());
		return row;

	}

}
