package com.ntga.dao;

import org.apache.http.HttpStatus;

import com.ntga.bean.WebQueryResult;
import com.ntga.zapc.ZapcGzxxBean;
import com.ntga.zapc.ZapcReturn;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

public class UploadZapcGzxxHandler extends Handler {

	Context context;
	ZapcGzxxBean gzxx;

	public UploadZapcGzxxHandler(Context context, ZapcGzxxBean gzxx) {
		this.context = context;
		this.gzxx = gzxx;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		Bundle b = msg.getData();
		WebQueryResult<ZapcReturn> re = (WebQueryResult<ZapcReturn>) b.getSerializable("gzxxRe");
		if (re.getStatus() == HttpStatus.SC_OK) {
			ZapcReturn zr = re.getResult();
			gzxx.setCsbj(zr.getCgbj());
			if (Integer.valueOf(zr.getCgbj()) == 1 && zr.getPcbh() != null
					&& zr.getPcbh().length > 0) {
				gzxx.setGzxxbh(zr.getPcbh()[0]);
			}
			Toast.makeText(context, zr.getScms(), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(context, "网络连接失败", Toast.LENGTH_LONG).show();
		}
		if(TextUtils.isEmpty(gzxx.getId())){
			//这是新增的记录
			int id = ZaPcdjDao.getMaxGzxxId(context.getContentResolver());
			gzxx.setId(String.valueOf(id));
			ZaPcdjDao.insertGzxx(gzxx, context.getContentResolver());
		}else{
			ZaPcdjDao.updateGzxx(gzxx, context.getContentResolver());
		}
	}

}
