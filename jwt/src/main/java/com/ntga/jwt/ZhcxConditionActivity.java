package com.ntga.jwt;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.ntga.bean.KeyValueBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.jwt.R;
import com.ntga.zhcx.ZhcxHandler;
import com.ntga.zhcx.ZhcxThread;
import com.ydjw.pojo.CxItem;
import com.ydjw.pojo.CxMenus;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ZhcxConditionActivity extends ActionBarActivity {

	private CxMenus zhcxItem;
	private Context self;
	private View[] allViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.zhcx_item);
		zhcxItem = (CxMenus) getIntent().getSerializableExtra("zhcxItem");
		setTitle(zhcxItem.getCxMenuName());
		LinearLayout rlayout = (LinearLayout) findViewById(R.id.rootLayout);

		if (zhcxItem != null && zhcxItem.getCxItems() != null
				&& zhcxItem.getCxItems().length > 0) {
			CxItem[] items = zhcxItem.getCxItems();
			allViews = new View[items.length];
			for (int i = 0; i < items.length; i++) {
				TextView tv = new TextView(this);
				tv.setText(items[i].getItemLabel());
				tv.setTextSize(18);
				ViewGroup.MarginLayoutParams ml = new ViewGroup.MarginLayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				ml.setMargins(10, 0, 10, 0);
				rlayout.addView(tv, ml);
				if (items[i].getItemLx().equals("EditText")) {
					EditText edSfzh = new EditText(this);
					edSfzh.setLayoutParams(new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
					// 设置文本框的默认值
					edSfzh.setText(items[i].getItemDeValue());
					// 设置文本框的输入法
					edSfzh.setRawInputType(GlobalMethod
							.getInputMethodByName(items[i].getItemInMethod()));
					rlayout.addView(edSfzh);
					allViews[i] = edSfzh;
				} else if (items[i].getItemLx().equals("Spinner")) {
					Spinner spin = new Spinner(this);
					ArrayList<KeyValueBean> ar = getItemArray(items[i]
							.getItemArray());
					GlobalMethod.changeAdapter(spin, ar, this);
					spin.setLayoutParams(new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
					rlayout.addView(spin);
					allViews[i] = spin;
				}
			}

		}
		findViewById(R.id.okButton).setOnClickListener(queryButListener);
		findViewById(R.id.cancelButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public ArrayList<KeyValueBean> getItemArray(String zl) {
		Class<GlobalData> className = GlobalData.class;

		ArrayList<KeyValueBean> clbjs = new ArrayList<KeyValueBean>();
		Field[] fs = className.getDeclaredFields();
		for (Field field : fs) {
			if (zl.equals(field.getName())) {
				try {
					ArrayList<KeyValueBean> temp = (ArrayList<KeyValueBean>) field
							.get(className);
					for (KeyValueBean keyValueBean : temp) {
						clbjs.add(keyValueBean);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return clbjs;
	}

	private OnClickListener queryButListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String where = "";
			if (allViews != null && allViews.length > 0) {
				for (int i = 0; i < allViews.length; i++) {
					View allView = allViews[i];
					String clName = allView.getClass().getSimpleName();
					if (clName.equals("EditText")) {
						EditText ev = (EditText) allView;
						// 这是一个编辑框
						if (!TextUtils.isEmpty(ev.getText())) {
							where += " AND "
									+ zhcxItem.getCxItems()[i].getItemMc()

									+ getBjtj(
											zhcxItem.getCxItems()[i]
													.getItemBjgx(),
											ev.getText().toString());
						}
					} else if (clName.equals("Spinner")) {
						Spinner sp = (Spinner) allView;
						if (sp.getSelectedItemPosition() > -1) {
							String key = GlobalMethod
									.getKeyFromSpinnerSelected(sp,
											GlobalConstant.KEY);
							where += " AND "
									+ zhcxItem.getCxItems()[i].getItemMc()
									+ getBjtj(
											zhcxItem.getCxItems()[i]
													.getItemBjgx(),
											key);
						}
					}
				}
			}
			if (TextUtils.isEmpty(where)) {
				Toast.makeText(self, "查询内容不能全为空", 500).show();
			} else {
				where = where.substring(5);
				ZhcxHandler handler = new ZhcxHandler(self);
				ZhcxThread thread = new ZhcxThread(handler);
				thread.doStart(self, zhcxItem.getCxId(), where);
			}
		}
	};

	private String getBjtj(String bjgx, String tj) {
		String re = "";
		if (bjgx.equals("like")) {
			return " like '%" + tj + "%'";
		} else if (bjgx.equals("eq")) {
			return "= '" + tj + "'";
		} else if (bjgx.equals("gt")) {
			return "> " + tj + "";
		} else if (bjgx.equals("lt")) {
			return "< " + tj + "";
		}
		return re;
	}

}
