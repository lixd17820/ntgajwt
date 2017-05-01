package com.ntga.adaper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.ntga.database.MessageDao;
import com.ntga.jwt.R;
import com.ydjw.pojo.UdpMessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UdpMessageDetailListAdapter extends ArrayAdapter<UdpMessage> {

	private int resourceId;
	private MessageDao dao;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdfDate = new SimpleDateFormat("MM月dd日HH:mm");

	public UdpMessageDetailListAdapter(Context context, int textViewResourceId,
			List<UdpMessage> objects, MessageDao dao) {
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
		if (position % 2 == 0) {
			row.setBackgroundColor(0xffe3f7fb);
		}
		UdpMessage m = getItem(position);
		if (m != null) {
			TextView tv1 = (TextView) row.findViewById(R.id.textView1);
			TextView tv2 = (TextView) row.findViewById(R.id.textView2);
			tv1.setText(dao.getXmAndJybhByMes(m) + " : " + m.getMessage());
			String d = m.getRecRiqi();
			try {
				d = sdfDate.format(sdf.parse(m.getRecRiqi()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// 接收的短信
			tv2.setText((m.getFsbj() == 0 ? "接收时间 : " : "发送时间 : ") + d);
		}
		return row;
	}
}
