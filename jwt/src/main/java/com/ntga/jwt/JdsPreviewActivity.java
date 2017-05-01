package com.ntga.jwt;

import java.util.ArrayList;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.OneLineWhiteAdapter;
import com.ntga.bean.JdsPrintBean;

import android.app.ListActivity;
import android.os.Bundle;

public class JdsPreviewActivity extends ActionBarListActivity {

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_pic_backgournd);
		ArrayList<JdsPrintBean> jds = (ArrayList<JdsPrintBean>) getIntent()
				.getSerializableExtra("jds");
//		Map<String, String> map = ViolationDAO.getMjgrxx(getContentResolver());
//		ArrayList<TwoLineSelectBean> list = changeMapIntoZh(map);
		OneLineWhiteAdapter ard = new OneLineWhiteAdapter(this,
				R.layout.one_row_white_item, jds);
		getListView().setAdapter(ard);
	}

//	private ArrayList<TwoLineSelectBean> changeMapIntoZh(Map<String, String> map) {
//		ArrayList<TwoLineSelectBean> list = new ArrayList<TwoLineSelectBean>();
//		Set<Entry<String, String>> set = map.entrySet();
//		for (Entry<String, String> entry : set) {
//			String zh = "";
//			if (TextUtils.equals(entry.getKey(), GlobalConstant.YHBH))
//				zh = "民警警号";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.YBMBH))
//				zh = "值勤机关代码";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.XM))
//				zh = "民警姓名";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.FYJG))
//				zh = "行政复议机关";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.SSJG))
//				zh = "行政诉讼机关";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.BMMC))
//				zh = "值勤机关名称";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.JKYH))
//				zh = "缴款银行";
//			else if (TextUtils.equals(entry.getKey(),
//					GlobalConstant.GRXX_PRINTER_NAME))
//				zh = "蓝牙打印机";
//			else if (TextUtils.equals(entry.getKey(),
//					GlobalConstant.GRXX_PRINTER_ADDRESS))
//				zh = "打印机地址";
//			if (!TextUtils.isEmpty(zh))
//				list.add(new TwoLineSelectBean(zh, entry.getValue(), true));
//		}
//		return list;
//	}

}
