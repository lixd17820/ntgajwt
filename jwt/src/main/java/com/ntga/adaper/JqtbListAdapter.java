package com.ntga.adaper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ntga.bean.JqtbBean;
import com.ntga.jwt.R;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JqtbListAdapter extends ArrayAdapter<JqtbBean> {

	Activity context;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd");

	public JqtbListAdapter(Activity _context, List<JqtbBean> objects) {
		super(_context, R.layout.jqtb_list_item, objects);
		this.context = _context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.jqtb_list_item, null);
		}
		JqtbBean kv = getItem(position);
		if (kv != null) {
			ImageView imgForce = (ImageView) row.findViewById(R.id.img_force);
			ImageView imgState = (ImageView) row.findViewById(R.id.img_state);
			ImageView imgIsFile = (ImageView) row.findViewById(R.id.img_attach);
			TextView title = (TextView) row.findViewById(R.id.tv_title);
			TextView content = (TextView) row.findViewById(R.id.tv_content);
			TextView sendDate = (TextView) row.findViewById(R.id.tv_date);
			imgState.setVisibility("1".equals(kv.getReadBj()) ? View.INVISIBLE
					: View.VISIBLE);
			imgIsFile.setVisibility("1".equals(kv.getIsFile()) ? View.VISIBLE
					: View.INVISIBLE);
			title.setText(kv.getTitle());
			content.setText(kv.getContent());
			String sendD = kv.getSendDate();
			if ("0".equals(kv.getForce()))
				imgForce.setImageDrawable(context.getResources().getDrawable(
						R.drawable.mess));
			if (!TextUtils.isEmpty(sendD) && sendD.length() == 16) {
				if (sdfShort.format(new Date()).equals(sendD.substring(0, 10))) {
					sendDate.setText(sendD.substring(11));
				} else {
					sendDate.setText(sendD.substring(5));
				}
			}

			// Drawable draw = TextUtils.equals("1", kv.getReadBj()) ? context
			// .getResources().getDrawable(
			// R.drawable.conversation_item_background_read)
			// : context.getResources().getDrawable(
			// R.drawable.conversation_item_background_unread);
			// row.setBackgroundDrawable(draw);
		}
		return row;

	}
}
