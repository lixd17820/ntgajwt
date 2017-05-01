package com.ntga.adaper;

import java.util.List;

import com.ntga.jwt.R;
import com.ntga.zapc.Zapcxx;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ZapcRyWpxxListAdapter extends ArrayAdapter<Zapcxx> {

	Activity context;

	public ZapcRyWpxxListAdapter(Activity _context, List<Zapcxx> objects) {
		super(_context, R.layout.two_col_list_item, objects);
		this.context = _context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.two_col_list_item, null);
		}
		Zapcxx kv = getItem(position);
		if (kv != null) {
			TextView tv = (TextView) row.findViewById(R.id.TextView_left);
			TextView tv2 = (TextView) row.findViewById(R.id.TextView_right);
			tv.setText(kv.getPczlMs());
			// if ("1".equals(kv.getScbj())) {
			// img.setImageResource(R.drawable.ok);
			// } else {
			// img.setImageResource(R.drawable.warn_green);
			// }
			tv2.setText(kv.getXxms());
		}
		return row;

	}
}
