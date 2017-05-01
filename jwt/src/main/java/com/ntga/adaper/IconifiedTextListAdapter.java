package com.ntga.adaper;

import java.util.ArrayList;
import java.util.List;

import com.ntga.bean.IconifiedText;
import com.ntga.jwt.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//使用BaseAdapter来存储取得的文件
public class IconifiedTextListAdapter extends ArrayAdapter<IconifiedText> {
	private Activity mContext = null;
	// 用于显示文件的列表
	private List<IconifiedText> mItems = new ArrayList<IconifiedText>();

	public IconifiedTextListAdapter(Activity context,
			List<IconifiedText> objects) {
		super(context, R.layout.one_row_img_txt_item, objects);
		mContext = context;
		mItems = objects;

	}

	// 添加一项（一个文件）
	public void addItem(IconifiedText it) {
		mItems.add(it);
	}

	// 设置文件列表
	public void setListItems(List<IconifiedText> lit) {
		mItems = lit;
	}

	// 得到文件的数目,列表的个数
	public int getCount() {
		return mItems.size();
	}

	// 得到一个文件
	public IconifiedText getItem(int position) {
		return mItems.get(position);
	}

	// 能否全部选中
	public boolean areAllItemsSelectable() {
		return false;
	}

	// 判断指定文件是否被选中
	public boolean isSelectable(int position) {
		return mItems.get(position).isSelectable();
	}

	// 得到一个文件的ID
	public long getItemId(int position) {
		return position;
	}

	// 重写getView方法来返回一个IconifiedTextView（我们自定义的文件布局）对象
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
			row = inflater.inflate(R.layout.one_row_img_txt_item, null);
		}
		IconifiedText kv = getItem(position);
		if (kv != null) {
			TextView tv1 = (TextView) row.findViewById(R.id.textView1);
			tv1.setText(kv.getText());
			tv1.setCompoundDrawablesWithIntrinsicBounds(kv.getIcon(), null,
					null, null);
		}
		return row;
	}
}
