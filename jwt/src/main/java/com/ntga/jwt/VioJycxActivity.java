package com.ntga.jwt;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ntga.bean.THmb;
import com.ntga.bean.VioViolation;
import com.ntga.bean.WfdmBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.VerifyData;
import com.ntga.dao.ViolationDAO;
import com.ntga.dao.WsglDAO;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * 简易程序处罚，父类为处罚类
 * 
 * @author lixd
 * 
 */
public class VioJycxActivity extends ViolationActivity {

	private static final String TAG = "VioJycxActivity";

	private Spinner spJkfs;

	private Context self;

	private ContentResolver resolver;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle ss) {
        super.onRestoreInstanceState(ss);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "VIOLATION ON CREATE");
		// 常量赋值
		resolver = this.getContentResolver();
		self = this;

		spJkfs = (Spinner) findViewById(R.id.Sp_jkfs);
		// 设置交款方式
		GlobalMethod.changeAdapter(spJkfs, GlobalData.jkfsList, this, true);

		jdsbh = WsglDAO.getCurrentJdsbh(GlobalConstant.JYCFJDS, zqmj, resolver);
		if (jdsbh == null) {
			GlobalMethod.showDialogWithListener("提示信息",
					"没有相应的处罚编号，请到文书管理中获取编号", "确定", exitSystem, self);
			return;
		}

		if (WsglDAO.hmNotEqDw(jdsbh)) {
			GlobalMethod.showDialogWithListener("提示信息",
					"当前文书编号与处罚机关不符，请上交文书后重新获取", "确定", exitSystem, self);
			return;
		}

		// 设置标题
		setTitle(getActivityTitle());
		textJdsbh.setText("决定书编号："+ jdsbh.getDqhm());
		// 罚款
		initViolation();
        cfzl = "2";
        wslb = "1";
		violation.setCfzl(cfzl);
		violation.setWslb(wslb);
		// 移除多个违法列表
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_wfxw_list);
		rl.removeAllViewsInLayout();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.punish_menu, menu);
		return true;
	}

	@Override
	protected String saveAndCheckVio() {
		getViolationFromView(violation);
		String err = VerifyData.verifyCommVio(violation,self);
		if (!TextUtils.isEmpty(err))
			return err;
		// 缴款方式
		violation.setJkfs(GlobalMethod.getKeyFromSpinnerSelected(spJkfs,
				GlobalConstant.KEY));
		String wfxwdm = edWfxw.getText().toString().trim();
		WfdmBean wfxwBean = ViolationDAO.queryWfxwByWfdm(wfxwdm, resolver);
		if (wfxwBean == null) {
			return "错误的违法代码!";
		}
		if (!ViolationDAO.isYxWfdm(wfxwBean)) {
			return "违法代码不在有效期内!";
		}
		if (TextUtils.equals(wfxwBean.getFkbj(), "0"))
			return "此违法行为不能用于罚款";
		violation.setWfxw1(wfxwBean.getWfxw());

		violation.setFkje(wfxwBean.getFkjeDut());
		// 缴款方式为当场交款,则交款标记为已交款
		if (TextUtils.equals(violation.getJkfs(), "1")) {
			violation.setJkbj("1");
			violation.setJkrq(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
		} else {
			violation.setJkbj("0");
			violation.setJkrq("");
		}
		err = VerifyData.verifyJycxVio(violation, self);
		return err;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case (R.id.save_quite):
			return menuSaveViolation();
		case (R.id.print_preview):
			// 预览打印
			return menuPreviewViolation();

		case (R.id.pre_print):
			// 单据已保存，打印决定书
			return menuPrintViolation();
		case R.id.con_vio:
			if (violation != null && isViolationSaved)
				showConVio(violation);
			else
				GlobalMethod.showToast("请保存当前决定书", self);
			return true;
		case R.id.sys_config:
			Intent intent = new Intent(self, ConfigParamSetting.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	protected String showWfdmDetail(WfdmBean w) {
		String s = w.getWfxw() + ": " + w.getWfms();
		// 简易程序将显示是否可罚款或警告
		s += ", 罚款" + w.getFkjeDut() + "元, 记" + w.getWfjfs() + "分";
		s += "| 罚款 " + (TextUtils.equals(w.getFkbj(), "1") ? "是" : "否");
		s += "| 警告 " + (TextUtils.equals(w.getJgbj(), "1") ? "是" : "否");
		s += "|" + (ViolationDAO.isYxWfdm(w) ? "有效代码" : "无效代码");
		return s;
	}

	@Override
	protected String getViolationTitle() {
		return "简易程序";
	}

}
