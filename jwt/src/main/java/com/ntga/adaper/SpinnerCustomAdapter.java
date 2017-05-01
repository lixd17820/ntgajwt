package com.ntga.adaper;

import java.util.List;

import com.ntga.bean.KeyValueBean;
import com.ntga.jwt.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerCustomAdapter extends ArrayAdapter<KeyValueBean> {

	Activity context;
	List<KeyValueBean> object;

	public SpinnerCustomAdapter(Activity _context, List<KeyValueBean> objects) {
		super(_context, R.layout.spinner_item, objects);
		this.context = _context;
		this.object = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.spinner_item, null);
		}
		KeyValueBean kv = getItem(position);
		TextView tv = (TextView) row.findViewById(R.id.text1);
		tv.setText(kv.getValue());
		return row;

	}
	
	public List<KeyValueBean> getArray(){
		return object;
	}
	
}
