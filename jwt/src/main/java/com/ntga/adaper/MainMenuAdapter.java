package com.ntga.adaper;

import java.util.List;

import com.ntga.bean.MenuOptionBean;
import com.ntga.dao.GlobalMethod;
import com.ntga.jwt.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuAdapter extends ArrayAdapter<MenuOptionBean> {
	Activity context;

	public MainMenuAdapter(Activity _context, List<MenuOptionBean> objects) {
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

		MenuOptionBean menu = getItem(position);

		ImageView iv = (ImageView) row.findViewById(R.id.image_item);
		TextView tv = (TextView) row.findViewById(R.id.text_item);
		//iv.setImageDrawable();
		iv.setImageBitmap(GlobalMethod.getIconByName(menu.getImg(), context));
		//iv.setImageDrawable(GlobalMethod.getIconByName(menu.getImg(), context));
		//iv.setImageResource(menu.getImg());
		tv.setText(menu.getMenuName());
		return row;

	}

}
