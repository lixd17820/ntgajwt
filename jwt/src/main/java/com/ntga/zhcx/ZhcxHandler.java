package com.ntga.zhcx;

import org.apache.http.HttpStatus;

import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalMethod;
import com.ntga.jwt.ZhcxOneRecordListActivity;
import com.ntga.jwt.ZhcxQueryResultActivity;
import com.ydjw.pojo.GlobalQueryResult;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class ZhcxHandler extends Handler {
	Context context;

	public ZhcxHandler(Context context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message m) {
		Bundle b = m.getData();
		WebQueryResult<GlobalQueryResult> webResult = (WebQueryResult<GlobalQueryResult>) b
				.getSerializable("queryResult");
		if (webResult.getStatus() == HttpStatus.SC_OK) {
			if (webResult.getResult() == null
					|| webResult.getResult().getContents() == null
					|| webResult.getResult().getContents().length == 0)
				GlobalMethod.showDialog("提示信息", "没有查询到对应的结果", "确定", context);
			else {
				GlobalQueryResult zhcx = webResult.getResult();
				if (zhcx != null) {
					if (zhcx.getCxid().startsWith("P")) {
						String[] contents = zhcx.getContents()[0];
						if (contents != null && contents.length > 0
								&& !TextUtils.isEmpty(contents[0])) {
							String url = contents[0] ;
							//GlobalMethod.writeInDisk("/sdcard/pic.txt", url);
							//Log.e("zhcxhander", url);
							GlobalMethod.showPicDialog(url, context);

						} else {
							GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！",
									"确定", context);
						}
					} else {
						Intent intent = new Intent(context,
								ZhcxQueryResultActivity.class);
						if (zhcx.getContents().length == 1)
							intent = new Intent(context,
									ZhcxOneRecordListActivity.class);
						intent.putExtra("zhcx", zhcx);
						context.startActivity(intent);
					}
				}
			}
		} else if (webResult.getStatus() == 204) {
			GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！", "确定", context);
		} else if (webResult.getStatus() == 500) {
			GlobalMethod.showDialog("提示信息", "该查询在服务器不能实现，请与管理员联系！", "确定",
					context);
		} else {
			GlobalMethod.showDialog("提示信息", "网络连接失败，请检查配查或与管理员联系！", "确定",
					context);
		}
	}

}
