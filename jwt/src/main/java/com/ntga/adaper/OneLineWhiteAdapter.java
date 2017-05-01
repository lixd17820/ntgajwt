package com.ntga.adaper;

import java.util.List;

import com.ntga.bean.JdsPrintBean;
import com.ntga.jwt.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OneLineWhiteAdapter extends ArrayAdapter<JdsPrintBean> {

	private int resourceId;

	public OneLineWhiteAdapter(Context context, int textViewResourceId,
			List<JdsPrintBean> objects) {
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
		JdsPrintBean jds = getItem(position);
		if (jds != null) {
			TextView tv = (TextView) row.findViewById(R.id.text1);
			tv.setText(jds.getContent());
			tv.setGravity(jds.getAlignMode());
		}
		return row;
	}

}
