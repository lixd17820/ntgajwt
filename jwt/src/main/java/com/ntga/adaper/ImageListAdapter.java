package com.ntga.adaper;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.ntga.jwt.R;

public class ImageListAdapter extends ArrayAdapter<Bitmap> {
	private int resourceId;

	public ImageListAdapter(Context context, int textViewResourceId,
			List<Bitmap> objects) {
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
		Bitmap content = getItem(position);
		if (content != null) {
			ImageView img = (ImageView) row.findViewById(R.id.image_item);
			img.setImageBitmap(content);
		}
		return row;
	}
}
