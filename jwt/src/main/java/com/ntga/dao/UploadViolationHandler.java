package com.ntga.dao;

import com.ntga.bean.VioViolation;
import com.ntga.bean.WebQueryResult;
import com.ntga.zapc.ZapcReturn;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class UploadViolationHandler extends Handler {
	Context self;
	
	public UploadViolationHandler(Context context,VioViolation violation){
		self = context;
	}
	
	@SuppressWarnings("unchecked")
	public void handleMessage(Message m) {
		Bundle b = m.getData();
		WebQueryResult<ZapcReturn> rs = (WebQueryResult<ZapcReturn>) b
				.getSerializable("queryResult");
		String err = GlobalMethod.getErrorMessageFromWeb(rs);
		if (TextUtils.isEmpty(err)) {
			ZapcReturn upRe = rs.getResult();
			if (upRe != null && TextUtils.equals(upRe.getCgbj(), "1")
					&& upRe.getPcbh() != null && upRe.getPcbh().length > 0) {
				GlobalMethod.showToast("决定书已上传", self);
				ViolationDAO.setVioUploadStatus(upRe.getPcbh()[0], true,
						self.getContentResolver());
			} else {
				GlobalMethod.showToast("文书上传失败", self);
			}
		} else {
			GlobalMethod.showToast(err, self);
		}
		// Bundle b = m.getData();
		// WebQueryResult<LoginMessage> rs = (WebQueryResult<LoginMessage>) b
		// .getSerializable("queryResult");
		// if (rs != null && rs.getStatus() == HttpStatus.SC_OK) {
		// if (rs.getResult().getCode() == 0) {
		// Toast.makeText(self, "决定书已上传", Toast.LENGTH_LONG).show();
		// ViolationDAO.setVioUploadStatus(violation, true,
		// self.getContentResolver());
		// } else {
		// Toast.makeText(self, rs.getResult().getMessage(),
		// Toast.LENGTH_LONG).show();
		// }
		// } else {
		// Toast.makeText(self, "网络连接失败", Toast.LENGTH_LONG).show();
		// }
	}
}
