package com.ntga.adaper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ntga.database.MessageDao;
import com.ntga.jwt.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UdpMessageGroupListAdapter extends ArrayAdapter<String[]> {

	private int resourceId;
	private MessageDao dao;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdfSj = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat sdfriqi = new SimpleDateFormat("MM月dd日");

	public UdpMessageGroupListAdapter(Context context, int textViewResourceId,
			List<String[]> objects,MessageDao dao) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		this.dao = dao;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = convertView;
		if (row == null) {
			row = inflater.inflate(resourceId, parent, false);
		}
		String[] content = getItem(position);
		if (content != null) {
			TextView tv1 = (TextView) row.findViewById(R.id.textView1);
			TextView tv2 = (TextView) row.findViewById(R.id.textView2);
			TextView tv3 = (TextView) row.findViewById(R.id.textView3);
			ImageView img = (ImageView) row.findViewById(R.id.imageView1);
			tv1.setText(dao.getXmAndJybh(content[1]) + "(" + content[8]
					+ ")");
			tv3.setText(content[3]);
			boolean notAllRead = Integer.valueOf(content[8]) > Integer
					.valueOf(content[9]);
			if (notAllRead)
				img.setImageResource(android.R.drawable.btn_star_big_on);
			else
				img.setImageResource(android.R.drawable.btn_star_big_off);
			if (!TextUtils.isEmpty(content[4]) && content[4].length() == 19) {
				try {
					Date d = sdf.parse(content[4]);
					Date day = sdfDate.parse(sdfDate.format(new Date()));
					if (d.getTime() > day.getTime()) {
						// 今天
						tv2.setText(sdfSj.format(d));
					} else {
						tv2.setText(sdfriqi.format(d));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return row;
	}

}
