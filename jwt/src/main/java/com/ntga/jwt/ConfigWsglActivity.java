package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.WsglListAdapter;
import com.ntga.bean.THmb;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.WsglDAO;

import android.app.ListActivity;
import android.os.Bundle;

public class ConfigWsglActivity extends ActionBarListActivity {

	// private ArrayList<KeyValueBean> kvs;
	// private Spinner spWslb;
	// private int maxWssl = 50;
	// private Context self;
	private List<THmb> jdsList;

	// private boolean isOldJwt;

	private String zqmj;

	// 0 警示卡
	// 1 简易处罚决定书
	// 2 行政处罚决定书
	// 3 强制措施凭证
	// 4 撤销决定书
	// 5 转递通知书
	// 9 其他
	// 6 违法处理通知书
	// 7 违停告知单

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comm_no_button_list);
		zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);
		setTitle("文书领用查看");
		// self = this;
		// kvs = ViolationDAO.getAllFrmCode(GlobalConstant.WSLB,
		// getContentResolver(), new String[] { Fixcode.FrmCode.DMZ,
		// Fixcode.FrmCode.DMSM1 }, Fixcode.FrmCode.DMZ
		// + " IN ('1','3')", null);
		// kvs.add(new KeyValueBean("9", "交通事故简易程序"));
		jdsList = new ArrayList<THmb>();
		changeList();
		// isOldJwt = true;

		// spWslb = (Spinner) findViewById(R.id.Spin_wslb);
		// GlobalMethod.changeAdapter(spWslb, kvs, this);

		// 获取按扭的动作监听
		// findViewById(R.id.But_request_ws).setOnClickListener(
		// new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// isOldJwt = true;
		// String hdzl = ((KeyValueBean) spWslb.getSelectedItem())
		// .getKey();
		// // 将系统内的文书种类转换成移动警务的文书种类
		// String jwtHdzl = GlobalConstant.hdzh.get(hdzl);
		// if (TextUtils.equals(jwtHdzl, "009")) {
		// isOldJwt = false;
		// }
		// // -----------------
		// int dqWssl = WsglDAO.getJdsCount(hdzl, zqmj,
		// getContentResolver());
		// if (dqWssl > maxWssl) {
		// // 还没有达到可以领取文书的标准
		// GlobalMethod.showErrorDialog("该类文书需小于" + maxWssl
		// + "张方可领取新文书!", self);
		// return;
		// }
		// // -------------------------------
		// String glbm = GlobalData.grxx.get(GlobalConstant.YBMBH)
		// .substring(0, 6);
		// if (isOldJwt) {
		// new VioHqwsTask().execute(zqmj, jwtHdzl);
		// } else {
		// new AcdHqwsTask().execute(zqmj, glbm, hdzl);
		// }
		//
		// }
		// });
		// // 上交文书,也就是将本地的文书删除
		// // 应该有一个方法与服务器更新
		// findViewById(R.id.But_send_ws).setOnClickListener(
		// new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (!jdsList.isEmpty()) {
		// if (WsglDAO.hmNotEqDw(jdsList)) {
		// backBillAction();
		// } else {
		// GlobalMethod.showDialog("系统提示",
		// "没有调动单位，无需上交文书", "确定", self);
		// }
		// } else {
		// GlobalMethod.showErrorDialog("没有文书可能上交!", self);
		// }
		// }
		// });
		//
		// // 改变文书类别时的监听
		// spWslb.setOnItemSelectedListener(new OnSpinnerItemSelected() {
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view,
		// int position, long id) {
		// changeList();
		// }
		// });
	}

	// private void backBillAction() {
	// String hdzl = ((KeyValueBean) spWslb.getSelectedItem()).getKey();
	// String hds = "";
	// String dqhms = "";
	// for (THmb hms : jdsList) {
	// hds += hms.getHdid() + ",";
	// dqhms += hms.getDqhm() + ",";
	// }
	// if (!TextUtils.isEmpty(dqhms))
	// dqhms = dqhms.substring(0, dqhms.length() - 1);
	// if (!TextUtils.isEmpty(hds))
	// hds = hds.substring(0, hds.length() - 1);
	// if ("1234".indexOf(hdzl) > -1) {
	// new BackVioWsTask().execute(jdsList);
	// // String backBillXml = String.format(
	// // GlobalConstant.backBillXmlMb, hds, dqhms);
	// // Log.e("ConfigWsgl", backBillXml);
	// // Log.e("ConfigWsgl", GlobalData.visitorXML);
	// // Log.e("ConfigWsgl", "" + hdzl);
	// // new BackWsTask().execute(GlobalData.visitorXML, backBillXml,
	// // hdzl);
	// } else if ("9".indexOf(hdzl) > -1) {
	// new BackAcdWsTask().execute(hds, dqhms, hdzl);
	// }
	// }

	private void changeList() {
		jdsList.addAll(WsglDAO
				.getJdsListByHdzl("1", zqmj, getContentResolver()));
		jdsList.addAll(WsglDAO
				.getJdsListByHdzl("3", zqmj, getContentResolver()));
		jdsList.addAll(WsglDAO
				.getJdsListByHdzl("9", zqmj, getContentResolver()));
		WsglListAdapter wsAdapter = new WsglListAdapter(
				ConfigWsglActivity.this, jdsList);
		getListView().setAdapter(wsAdapter);
	}

	// private void operWs(WebQueryResult<LoginMessage> lm, String hdzl) {
	// if (lm == null || lm.getStatus() != HttpStatus.SC_OK) {
	// // 服务器返回数据正确性验证， 网络状态正常
	// GlobalMethod.showDialog("提示信息", "网络状态异常", "确定", self);
	// return;
	// }
	// int code = lm.getResult().getCode();
	// // 返回数据不正确,显示错误信息并退出程序
	// // 需要重新对话框的确定按扭监听
	// if (code != 0) {
	// GlobalMethod.showDialog("提示信息", lm.getResult().getMessage(), "确定",
	// self);
	// return;
	// }
	// // 号段个数
	// int hmSize = lm.getResult().getValues().size();
	// int fieldSize = lm.getResult().getFields().size();
	// if (fieldSize < 1) {
	// GlobalMethod.showDialog("提示信息", "编号获取失败", "确定", self);
	// return;
	// }
	//
	// if (hmSize < 1) {
	// GlobalMethod.showDialog("提示信息", "未能正确获取文书,按确定退出重新登录", "确定", self);
	// return;
	// }
	//
	// if (hmSize % fieldSize != 0) {
	// GlobalMethod.showDialog("提示信息", "数据传输错误,不能正常处罚,按确定退出重新登录", "确定",
	// self);
	// return;
	// }
	//
	// // 返回数据正确则将其存入数据库并加入个人信息变量
	// for (int r = 0; r < (hmSize / fieldSize); r++) {
	// THmb hm = new THmb();
	// hm.setHdzl(hdzl);
	// for (int i = 0; i < lm.getResult().getFields().size(); i++) {
	// String field = lm.getResult().getFields().get(i);
	// String value = lm.getResult().getValues()
	// .get(r * fieldSize + i);
	// if (TextUtils.equals(GlobalConstant.WSBHID, field))
	// hm.setHdid(value);
	// else if (TextUtils.equals(GlobalConstant.WSDQBH, field)) {
	// hm.setDqhm(value);
	// } else if (TextUtils.equals(GlobalConstant.WSZDBH, field))
	// hm.setJshm(value);
	// }
	// WsglDAO.saveHmb(hm, getContentResolver());
	// }
	// changeList();
	// }

	// private void operAcdWs(WebQueryResult<THmb> lm, String hdzl) {
	// if (lm == null || lm.getStatus() != HttpStatus.SC_OK) {
	// // 服务器返回数据正确性验证， 网络状态正常
	// GlobalMethod.showDialog("提示信息", "网络状态异常", "确定", self);
	// return;
	// }
	// if (lm.getResult() == null) {
	// GlobalMethod.showDialog("提示信息", "编号获取失败", "确定", self);
	// return;
	// }
	//
	// WsglDAO.saveHmb(lm.getResult(), getContentResolver());
	// changeList();
	// }

	/**
	 * 验证号码并保存号码
	 * 
	 * @param result
	 */
	// private void operVioWs(WebQueryResult<List<THmb>> result) {
	// if (result == null || result.getStatus() != HttpStatus.SC_OK) {
	// // 服务器返回数据正确性验证， 网络状态正常
	// GlobalMethod.showDialog("提示信息", "网络状态异常", "确定", self);
	// return;
	// }
	// List<THmb> hms = result.getResult();
	// if (hms == null || hms.size() < 1) {
	// GlobalMethod.showDialog("提示信息", "文书获取失败", "确定", self);
	// return;
	// }
	// for (THmb hm : hms) {
	// hm.setHdzl( GlobalConstant.hdzh.get(hm.getHdzl()));
	// WsglDAO.saveHmb(hm, getContentResolver());
	// }
	// changeList();
	// }

	/**
	 * 回收文书的方法
	 * 
	 * @param lm
	 * @param hdzl
	 */
	// private void backWs(WebQueryResult<LoginMessage> lm, String hdzl) {
	// String err = GlobalMethod.getErrorMessage(lm);
	// if (!TextUtils.isEmpty(err)) {
	// GlobalMethod.showDialog("提示信息", err, "确定", self);
	// return;
	// }
	// int row = WsglDAO.delHmb(hdzl, getContentResolver());
	// if (row > 0) {
	// changeList();
	// GlobalMethod.showDialog("提示信息", "文书上交成功", "确定", self);
	// }
	// }

	/**
	 * 上交成功后删除表中的简易程序处罚文书
	 * 
	 * @param result
	 */
	// private void operBackVioWs(List<WebQueryResult<ZapcReturn>> hmbs) {
	// int row = 0;
	// for (WebQueryResult<ZapcReturn> webQueryResult : hmbs) {
	// String err = GlobalMethod.getErrorMessageFromWeb(webQueryResult);
	// if (!TextUtils.isEmpty(err)) {
	// GlobalMethod.showDialog("提示信息", err, "确定", self);
	// return;
	// } else {
	//
	// ZapcReturn re = webQueryResult.getResult();
	// if (re != null) {
	// if (TextUtils.equals("1", re.getCgbj())
	// && re.getPcbh() != null && re.getPcbh().length > 0) {
	// row += WsglDAO.delHmbByHdid(re.getPcbh()[0],
	// getContentResolver());
	// }
	// }
	// }
	// }
	// if (row > 0) {
	// changeList();
	// GlobalMethod.showDialog("提示信息", "文书上交成功", "确定", self);
	// }
	// }

	/**
	 * 删除事故文书
	 * 
	 * @param lm
	 * @param hdzl
	 */
	// private void backAcdWs(WebQueryResult<String> lm, String hdzl) {
	// String err = GlobalMethod.getErrorMessageFromWeb(lm);
	// if (!TextUtils.isEmpty(err)) {
	// GlobalMethod.showDialog("提示信息", err, "确定", self);
	// return;
	// } else {
	// int row = 0;
	// String[] hdids = lm.getResult().split(",");
	// for (int i = 0; i < hdids.length; i++) {
	// row += WsglDAO.delHmbByHdid(hdids[i], getContentResolver());
	// }
	// if (row > 0) {
	// changeList();
	// GlobalMethod.showDialog("提示信息", "文书上交成功", "确定", self);
	// }
	// }
	// }

	/**
	 * 
	 * @author lixd
	 * 
	 */
	// private class BackAcdWsTask extends
	// AsyncTask<String, Void, WebQueryResult<String>> {
	// private ProgressDialog progressDialog;
	// private String hdzl;
	//
	// @Override
	// protected WebQueryResult<String> doInBackground(String... params) {
	// this.hdzl = params[2];
	// RestfulDao dao = RestfulDaoFactory.getDao();
	// WebQueryResult<String> lm = dao.sjAcdWs(params[0], params[1]);
	// return lm;
	// }
	//
	// @Override
	// protected void onPostExecute(WebQueryResult<String> result) {
	// progressDialog.cancel();
	// backAcdWs(result, hdzl);
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// progressDialog = ProgressDialog.show(self, "提示",
	// "正在向服务器请求数据,请稍等...", true);
	// progressDialog.setCancelable(true);
	// }
	//
	// }
	//
	// private class BackVioWsTask extends
	// AsyncTask<List<THmb>, Void, List<WebQueryResult<ZapcReturn>>> {
	// private ProgressDialog progressDialog;
	//
	// @Override
	// protected List<WebQueryResult<ZapcReturn>> doInBackground(
	// List<THmb>... params) {
	// String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
	// List<WebQueryResult<ZapcReturn>> re = new
	// ArrayList<WebQueryResult<ZapcReturn>>();
	// RestfulDao dao = RestfulDaoFactory.getDao();
	// for (THmb hmb : params[0]) {
	// WebQueryResult<ZapcReturn> lm = dao.backVioWs(hmb, jybh);
	// re.add(lm);
	// }
	// return re;
	// }
	//
	// @Override
	// protected void onPostExecute(List<WebQueryResult<ZapcReturn>> result) {
	// progressDialog.cancel();
	// operBackVioWs(result);
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// progressDialog = ProgressDialog.show(self, "提示",
	// "正在请求WebService,请稍等...", true);
	// progressDialog.setCancelable(true);
	// }
	//
	// }
	//
	// /**
	// * 在618服务器上获取事故文书的异步任务
	// *
	// * @author lixd
	// *
	// */
	// private class AcdHqwsTask extends
	// AsyncTask<String, Void, WebQueryResult<THmb>> {
	//
	// private ProgressDialog progressDialog;
	// private String hdzl;
	//
	// @Override
	// protected WebQueryResult<THmb> doInBackground(String... params) {
	// this.hdzl = params[2];
	// RestfulDao dao = RestfulDaoFactory.getDao();
	// WebQueryResult<THmb> lm = dao.hqAcdWs(params[0], params[1],
	// params[2]);
	// return lm;
	// }
	//
	// @Override
	// protected void onPostExecute(WebQueryResult<THmb> result) {
	// progressDialog.cancel();
	// operAcdWs(result, hdzl);
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// progressDialog = ProgressDialog.show(self, "提示",
	// "正在请求WebService,请稍等...", true);
	// progressDialog.setCancelable(true);
	// }
	//
	// @Override
	// protected void onProgressUpdate(Void... values) {
	// // TODO Auto-generated method stub
	// super.onProgressUpdate(values);
	// }
	//
	// }
	//
	// private class VioHqwsTask extends
	// AsyncTask<String, Void, WebQueryResult<List<THmb>>> {
	//
	// private ProgressDialog progressDialog;
	//
	// @Override
	// protected WebQueryResult<List<THmb>> doInBackground(String... params) {
	// RestfulDao dao = RestfulDaoFactory.getDao();
	// WebQueryResult<List<THmb>> lm = dao.hqVioWs(params[0], params[1]);
	// return lm;
	// }
	//
	// @Override
	// protected void onPostExecute(WebQueryResult<List<THmb>> result) {
	// progressDialog.cancel();
	// operVioWs(result);
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// progressDialog = ProgressDialog.show(self, "提示",
	// "正在请求WebService,请稍等...", true);
	// progressDialog.setCancelable(true);
	// }
	//
	// @Override
	// protected void onProgressUpdate(Void... values) {
	// // TODO Auto-generated method stub
	// super.onProgressUpdate(values);
	// }
	//
	// }

}
