package com.ntga.jwt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpStatus;

import com.ntga.bean.WebQueryResult;
import com.ntga.card.PersonInfo;
import com.ntga.card.ReadCardThread;
import com.ntga.card.Error;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ZaPcdjDao;
import com.ntga.tools.IDCard;
import com.ntga.zapc.ZapcGzxxBean;
import com.ntga.zapc.ZapcRyjbxxBean;
import com.ntga.zapc.ZapcRypcxxBean;
import com.ntga.zhcx.ZhcxHandler;
import com.ntga.zhcx.ZhcxThread;
import com.ydjw.pojo.GlobalQueryResult;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

public class ZapcRyxxActivity extends ActionBarActivity {

	private Context self;
	private Spinner spPcyy, spXb, spWhcd, spMz, spByzk, spHy, spZjxy, spJccfx,
			spPcbdjg, spPccljg;
	private EditText edPcdd, edSfzh, edXm, edCsrq, edSg, edHjqh, edXxzz,
			edFwcs, edLxdh;
	private CheckBox chkQgrk;

	//
	private ZapcRypcxxBean pcryxx;
	// 相关联的工作信息
	private ZapcGzxxBean gzxx;

	// 查看模式，新增模式，或是编辑模式
	private static final int MODREADONLY = 0;
	private static final int MODINSERT = 1;
	private static final int MODMODIFY = 2;
	// 户籍区划请求码
	protected static final int REQ_HJQH = 3;
	public static final String HJQH_EXTRA = "hjqh";

	// 人员模式，用于决定菜单的生成
	private int ryMod = MODREADONLY;

	private SimpleDateFormat sdfShort = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdfNormal = new SimpleDateFormat("yyyy-MM-dd");
	private BluetoothAdapter mbta;
	private ReadCardThread mrcThread;
	private boolean mConnected;
	private static final String TITLE = "治安盘查人员信息";
	protected static final String TAG = "ZapcRyxxActivity";
	private static final int MENU_CARD_DETAIL = 100;
	private static final int MENU_PERSON_PHOTO = 101;
	private PersonInfo mPerson;

	// private Button btnCardDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.zapc_pcryxx);
		setTitle(TITLE);
		// 初如化控件
		initViewFromXml();
		//btnCardDetail.setEnabled(false);
		// 查看模式和编辑模式
		pcryxx = (ZapcRypcxxBean) getIntent().getSerializableExtra("pcryxx");
		gzxx = (ZapcGzxxBean) getIntent().getSerializableExtra("gzxx");
		if (pcryxx == null) {
			pcryxx = new ZapcRypcxxBean();
			ryMod = MODINSERT;
			// 查询上一个盘查的点，直接加到这一个地点中
			edPcdd.setText(ZaPcdjDao.queryLastRypcxx(getContentResolver()));
			findViewById(R.id.But_save_ryxx).setOnClickListener(saveClick);
			findViewById(R.id.But_end_pc).setOnClickListener(quitePcry);
			// 读卡器
			initCardReader();
			//btnCardDetail.setOnClickListener(cardDetailClick);
		} else {
			ryMod = MODREADONLY;
			setViewValueFromRyjbxx(pcryxx);
			LinearLayout line = (LinearLayout) findViewById(R.id.bottom_but);
			RelativeLayout main = (RelativeLayout) findViewById(R.id.main_relative_layout);
			main.removeView(line);

		}
		findViewById(R.id.But_pc_cxry).setOnClickListener(cxryxxListener);
		findViewById(R.id.But_pc_hjqh).setOnClickListener(changeHjqhClick);
		findViewById(R.id.But_pc_csrq).setOnClickListener(changeCsrqListener);
	}

	private boolean initCardReader() {
		mbta = BluetoothAdapter.getDefaultAdapter();
		if (mbta == null)
			return false;
		String cardName = GlobalData.grxx
				.get(GlobalConstant.GRXX_CARD_READER_NAME);
		String carAddress = GlobalData.grxx
				.get(GlobalConstant.GRXX_CARD_READER_ADDRESS);
		if (TextUtils.isEmpty(cardName) || TextUtils.isEmpty(carAddress)) {
			setTitle(TITLE + " -- 无身份证读卡器");
			return false;
		} else {
			setTitle(TITLE + " -- " + cardName);
		}
		if (mbta.getState() == BluetoothAdapter.STATE_OFF)// 开蓝牙
			mbta.enable();
		BluetoothDevice device = mbta.getRemoteDevice(carAddress);
		// mHandler = new MessageHandler(self);
		mrcThread = new ReadCardThread(self, readCardHander, device);
		mrcThread.start();
		return true;
	}

	private Handler readCardHander = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ReadCardThread.WM_CLEARSCREEN:
				// mPerson.Empty();
				setTitle(TITLE + " -- 正在读卡...");
				// mView.invalidate();
				break;
			case ReadCardThread.WM_READCARD:
				mPerson = (PersonInfo) msg.obj;
				setTitle(TITLE + " --读卡成功！");
				mConnected = true;
				if (mPerson != null) {
					// 启动异步查询线程
					edSfzh.setText(mPerson.getIdNum());
					boolean isLoc = mPerson.getAuthority().startsWith("南通市")
							|| mPerson.getAuthority().startsWith("通州市")
							|| mPerson.getAuthority().startsWith("如皋市")
							|| mPerson.getAuthority().startsWith("如东")
							|| mPerson.getAuthority().startsWith("启东")
							|| mPerson.getAuthority().startsWith("海安")
							|| mPerson.getAuthority().startsWith("海门");
					queryXxByShzh(mPerson.getIdNum(), isLoc);
				}
				// mView.invalidate();
				break;
			case ReadCardThread.WM_ERROR:
				if (msg.arg2 == Error.ERR_FIND) {
					if (!mConnected) {
						setTitle(TITLE + " --请放卡...");
						mConnected = true;
					}
				} else {
					if (msg.arg2 == Error.ERR_PORT) {
						mConnected = false;
						setTitle(TITLE + " --无设备");
					}
					// mtvError.setText(Error.GetErrorText(msg.arg2));
				}
				break;
			/*
			 * case 10: mtvDebug.setText((String)msg.obj); break; case 11:
			 * mtvDebug2.setText((String)msg.obj); break;
			 */
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, MENU_CARD_DETAIL, Menu.NONE, "二代证详细信息");
		menu.add(0, MENU_PERSON_PHOTO, Menu.NONE, "被盘查人照片");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_CARD_DETAIL:
			if (mPerson != null && !TextUtils.isEmpty(mPerson.getIdNum())) {
				GlobalMethod.showCardReadDialog(mPerson, self);
			} else {
				GlobalMethod.showErrorDialog("没有读卡，无法显示信息", self);
			}
			return true;
		case MENU_PERSON_PHOTO:
			String sfzh = edSfzh.getText().toString();
			if (TextUtils.isEmpty(sfzh)) {
				GlobalMethod.showErrorDialog("身份证号不能为空", self);
				return true;
			}
			String where = "SFZH='" + sfzh.toUpperCase() + "'";
			ZhcxHandler handler = new ZhcxHandler(self);
			ZhcxThread thread = new ZhcxThread(handler);
			thread.doStart(self, "P001", where);
			Log.e("where", where);
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mConnected) {
			mrcThread.StopReadCard();
			mrcThread.cancel();
		}
	}

	public void queryXxByShzh(String gmsfhm, boolean isLoc) {
		ZhcxThread thread = new ZhcxThread(cxryHandler);
		if (!isLoc)
			thread.doStart(self, "Q003", "SFZH='" + gmsfhm.toUpperCase() + "'");
		else
			thread.doStart(self, "C005", "gmsfhm='" + gmsfhm.toUpperCase()
					+ "'");
		// 根据身份证号取性别和出生日期
		if (Integer.valueOf(gmsfhm.substring(16, 17)) % 2 == 2) {
			spXb.setSelection(
					GlobalMethod.getPositionByKey(
							ZaPcdjDao.zapcDic.get(ZaPcdjDao.XB), "0"), true);
		} else {
			spXb.setSelection(
					GlobalMethod.getPositionByKey(
							ZaPcdjDao.zapcDic.get(ZaPcdjDao.XB), "1"), true);
		}
		String csrq = gmsfhm.substring(6, 14);
		if (!TextUtils.isEmpty(csrq) && csrq.length() == 8) {
			try {
				edCsrq.setText(sdfNormal.format(sdfShort.parse(csrq)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据身份证号查询本市人员的信息
	 */
	private OnClickListener cxryxxListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			String gmsfhm = edSfzh.getText().toString().trim();
			if (!TextUtils.isEmpty(gmsfhm) && gmsfhm.length() == 18
					&& IDCard.Verify(gmsfhm)) {
				queryXxByShzh(gmsfhm, !chkQgrk.isChecked());
			} else {
				GlobalMethod.showErrorDialog("身份证号不正确", self);
			}
		}
	};

	// 修改出生日期
	private OnClickListener changeCsrqListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final Calendar calendar = Calendar.getInstance();
			String csrq = edCsrq.getText().toString();
			if (!TextUtils.isEmpty(csrq) && csrq.length() == 10) {
				try {
					calendar.setTime(sdfNormal.parse(csrq));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				calendar.setTime(new Date());
			}
			new DatePickerDialog(self,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							calendar.set(Calendar.YEAR, year);
							calendar.set(Calendar.MONTH, monthOfYear);
							calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
							edCsrq.setText(sdfNormal.format(calendar.getTime()));
						}
					}, calendar.get(Calendar.YEAR), calendar
							.get(Calendar.MONTH), calendar
							.get(Calendar.DAY_OF_MONTH)).show();
		}
	};

	private OnClickListener changeHjqhClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(self, ZapcXzqhListActitivy.class);
			if (!TextUtils.isEmpty(edHjqh.getText()))
				intent.putExtra(HJQH_EXTRA, edHjqh.getText().toString());
			startActivityForResult(intent, REQ_HJQH);
		}
	};

	/**
	 * 查询人员返回后所做的动作
	 */
	private Handler cxryHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			WebQueryResult<GlobalQueryResult> webResult = (WebQueryResult<GlobalQueryResult>) b
					.getSerializable("queryResult");
			if (webResult.getStatus() == HttpStatus.SC_OK) {
				if (webResult.getResult() == null
						|| webResult.getResult().getContents() == null
						|| webResult.getResult().getContents().length == 0)
					GlobalMethod.showDialog("提示信息", "没有相应的查询结果！", "确定", self);
				else {
					GlobalQueryResult zhcx = webResult.getResult();
					if (zhcx != null) {
						String bdxx = zhcx.getBdxx();
						if (!TextUtils.isEmpty(bdxx)) {
							GlobalMethod.showDialog("系统比对信息", bdxx, "确定", self);
						}
						String[] names = zhcx.getNames();
						String[] content = zhcx.getContents()[0];
						// 根据查询的内容对界面赋值
						int pos = -1;
						if (chkQgrk.isChecked()) {
							pos = GlobalMethod.getPositionFromArray(names,
									"ZZXZ");
							edXxzz.setText(pos > -1 ? content[pos] : "");

							pos = GlobalMethod
									.getPositionFromArray(names, "MZ");
							spMz.setSelection(GlobalMethod.getPositionByValue(
									ZaPcdjDao.zapcDic.get(ZaPcdjDao.MZ),
									(pos > -1 ? content[pos] : "")), true);

							pos = GlobalMethod.getPositionFromArray(names,
									"HYZK");
							spHy.setSelection(GlobalMethod.getPositionByValue(
									ZaPcdjDao.zapcDic.get(ZaPcdjDao.HYZK),
									(pos > -1 ? content[pos] : "")), true);

							pos = GlobalMethod.getPositionFromArray(names,
									"BYQK");
							spByzk.setSelection(GlobalMethod
									.getPositionByValue(ZaPcdjDao.zapcDic
											.get(ZaPcdjDao.BYZK),
											(pos > -1 ? content[pos] : "")),
									true);

						} else {
							pos = GlobalMethod.getPositionFromArray(names,
									"HJXZ");
							edXxzz.setText(pos > -1 ? content[pos] : "");

							pos = GlobalMethod.getPositionFromArray(names,
									"WHCD");
							spWhcd.setSelection(GlobalMethod.getPositionByKey(
									ZaPcdjDao.zapcDic.get(ZaPcdjDao.WHCD),
									(pos > -1 ? content[pos] : "")), true);
							pos = GlobalMethod.getPositionFromArray(names,
									"LXDH");
							edLxdh.setText(pos > -1 ? content[pos] : "");

							pos = GlobalMethod
									.getPositionFromArray(names, "MZ");
							spMz.setSelection(GlobalMethod.getPositionByKey(
									ZaPcdjDao.zapcDic.get(ZaPcdjDao.MZ),
									(pos > -1 ? content[pos] : "")), true);

							pos = GlobalMethod.getPositionFromArray(names,
									"HYZK");
							spHy.setSelection(GlobalMethod.getPositionByKey(
									ZaPcdjDao.zapcDic.get(ZaPcdjDao.HYZK),
									(pos > -1 ? content[pos] : "")), true);

							pos = GlobalMethod.getPositionFromArray(names,
									"BYZK");
							spByzk.setSelection(GlobalMethod.getPositionByKey(
									ZaPcdjDao.zapcDic.get(ZaPcdjDao.BYZK),
									(pos > -1 ? content[pos] : "")), true);

							pos = GlobalMethod.getPositionFromArray(names,
									"ZJXY");
							spZjxy.setSelection(GlobalMethod.getPositionByKey(
									ZaPcdjDao.zapcDic.get(ZaPcdjDao.ZJXY),
									(pos > -1 ? content[pos] : "")), true);

							pos = GlobalMethod.getPositionFromArray(names,
									"HJQH");
							edHjqh.setText(pos > -1 ? content[pos] : "");

						}

						pos = GlobalMethod.getPositionFromArray(names, "XM");
						edXm.setText(pos > -1 ? content[pos] : "");

						pos = GlobalMethod.getPositionFromArray(names, "SG");
						edSg.setText(pos > -1 ? content[pos] : "");
						pos = GlobalMethod.getPositionFromArray(names, "FWCS");
						edFwcs.setText(pos > -1 ? content[pos] : "");

					}
				}
			} else if (webResult.getStatus() == 204) {
				GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！", "确定", self);
			} else if (webResult.getStatus() == 500) {
				GlobalMethod.showDialog("提示信息", "该查询在服务器不能实现，请与管理员联系！", "确定",
						self);
			} else {
				GlobalMethod.showDialog("提示信息", "网络连接失败，请检查配查或与管理员联系！", "确定",
						self);
			}
		}

	};

	/**
	 * 将盘查人员信息中的数据填充到控件中
	 * 
	 * @param ryxx
	 */
	private void setViewValueFromRyjbxx(ZapcRypcxxBean ryxx) {
		ZapcRyjbxxBean ryjbxx = ryxx.getRyjbxx();
		edPcdd.setText(ZaPcdjDao.ifNull(ryxx.getRypcdd()));
		edSfzh.setText(ZaPcdjDao.ifNull(ryjbxx.getGmsfhm()));
		edXm.setText(ZaPcdjDao.ifNull(ryjbxx.getXm()));
		// 对出生日期的格式进行转换
		if (TextUtils.isEmpty(ryjbxx.getCsrq()))
			edCsrq.setText("");
		else {
			if (ryjbxx.getCsrq().length() == 8) {
				try {
					String dt = sdfNormal.format(sdfShort.parse(ryjbxx
							.getCsrq()));
					edCsrq.setText(dt);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		edSg.setText(ZaPcdjDao.ifNull(ryjbxx.getSg()));
		edFwcs.setText(ZaPcdjDao.ifNull(ryjbxx.getFwcs()));
		edXxzz.setText(ZaPcdjDao.ifNull(ryjbxx.getXzzxz()));
		edHjqh.setText(ZaPcdjDao.ifNull(ryjbxx.getHjqh()));
		edLxdh.setText(ZaPcdjDao.ifNull(ryjbxx.getLxdh()));
		spPcyy.setSelection(GlobalMethod.getPositionByKey(
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCYY), ryxx.getRypcyy()), true);
		spXb.setSelection(GlobalMethod.getPositionByKey(
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.XB), ryxx.getRyjbxx().getXb()),
				true);
		spWhcd.setSelection(GlobalMethod.getPositionByKey(ZaPcdjDao.zapcDic
				.get(ZaPcdjDao.WHCD), ryxx.getRyjbxx().getWhcd()), true);
		spMz.setSelection(GlobalMethod.getPositionByKey(
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.MZ), ryxx.getRyjbxx().getMz()),
				true);
		spByzk.setSelection(GlobalMethod.getPositionByKey(ZaPcdjDao.zapcDic
				.get(ZaPcdjDao.BYZK), ryxx.getRyjbxx().getByzk()), true);
		spHy.setSelection(GlobalMethod.getPositionByKey(ZaPcdjDao.zapcDic
				.get(ZaPcdjDao.HYZK), ryxx.getRyjbxx().getHyzk()), true);
		spZjxy.setSelection(GlobalMethod.getPositionByKey(ZaPcdjDao.zapcDic
				.get(ZaPcdjDao.ZJXY), ryxx.getRyjbxx().getZjxy()), true);
		spJccfx.setSelection(GlobalMethod.getPositionByKey(
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.JCCFX), ryxx.getJccfx()), true);
		spPcbdjg.setSelection(
				GlobalMethod.getPositionByKey(
						ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCBDJG),
						ryxx.getRybdjg()), true);
		spPccljg.setSelection(
				GlobalMethod.getPositionByKey(
						ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCCLJG),
						ryxx.getRycljg()), true);

	}

	/**
	 * 将控件中的内容保存到对象中
	 */
	private void saveViewToRyxx() {
		pcryxx.setRypcdd(edPcdd.getText().toString());
		pcryxx.getRyjbxx().setGmsfhm(edSfzh.getText().toString());
		pcryxx.getRyjbxx().setXm(edXm.getText().toString());

		String dt = edCsrq.getText().toString().replaceAll("-", "");
		pcryxx.getRyjbxx().setCsrq(dt);

		pcryxx.getRyjbxx().setSg(edSg.getText().toString());
		pcryxx.getRyjbxx().setFwcs(edFwcs.getText().toString());
		pcryxx.getRyjbxx().setXzzxz(edXxzz.getText().toString());
		pcryxx.getRyjbxx().setHjqh(edHjqh.getText().toString());
		pcryxx.getRyjbxx().setLxdh(edLxdh.getText().toString());
		pcryxx.setRypcyy(GlobalMethod.getKeyFromSpinnerSelected(spPcyy,
				GlobalConstant.KEY));
		pcryxx.getRyjbxx().setXb(
				GlobalMethod
						.getKeyFromSpinnerSelected(spXb, GlobalConstant.KEY));
		pcryxx.getRyjbxx().setWhcd(
				GlobalMethod.getKeyFromSpinnerSelected(spWhcd,
						GlobalConstant.KEY));
		pcryxx.getRyjbxx().setMz(
				GlobalMethod
						.getKeyFromSpinnerSelected(spMz, GlobalConstant.KEY));
		pcryxx.getRyjbxx().setByzk(
				GlobalMethod.getKeyFromSpinnerSelected(spByzk,
						GlobalConstant.KEY));
		pcryxx.getRyjbxx().setHyzk(
				GlobalMethod
						.getKeyFromSpinnerSelected(spHy, GlobalConstant.KEY));
		pcryxx.getRyjbxx().setZjxy(
				GlobalMethod.getKeyFromSpinnerSelected(spZjxy,
						GlobalConstant.KEY));
		pcryxx.setJccfx(GlobalMethod.getKeyFromSpinnerSelected(spJccfx,
				GlobalConstant.KEY));
		pcryxx.setRybdjg(GlobalMethod.getKeyFromSpinnerSelected(spPcbdjg,
				GlobalConstant.KEY));
		pcryxx.setRycljg(GlobalMethod.getKeyFromSpinnerSelected(spPccljg,
				GlobalConstant.KEY));
		// pcryxx.getRyjbxx().setRybh(rybh);
		pcryxx.setRypcsj(ZaPcdjDao.sdfDpt.format(new Date()));
	}

	/**
	 * 验证人员盘查信息，盘查地点、盘查原因、身份证号、姓名、比对结果、处理结果、性别、详细住址
	 * 
	 * @return
	 */
	private String checkPcryxx() {
		// 首先验证身份证的正确性
		if (TextUtils.isEmpty(pcryxx.getRyjbxx().getGmsfhm())
				|| !IDCard.Verify(pcryxx.getRyjbxx().getGmsfhm()))
			return "身份证号码录入错误";
		if (TextUtils.isEmpty(pcryxx.getRypcdd()))
			return "盘查地点不能为空";
		if (TextUtils.isEmpty(pcryxx.getRypcyy()))
			return "盘查原因不能为空";
		if (TextUtils.isEmpty(pcryxx.getRyjbxx().getXm()))
			return "被盘查人姓名不能为空";
		if (TextUtils.isEmpty(pcryxx.getRyjbxx().getXb()))
			return "被盘查人性别不能为空";
		if (TextUtils.isEmpty(pcryxx.getRyjbxx().getXzzxz()))
			return "被盘查人住址不能为空";
		if (TextUtils.isEmpty(pcryxx.getRybdjg()))
			return "盘查比对结果不能为空";
		if (TextUtils.isEmpty(pcryxx.getRycljg()))
			return "盘查处理结果不能为空";
		return null;
	}

	private OnClickListener saveClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			saveViewToRyxx();
			String errInfo = checkPcryxx();
			if (errInfo == null) {
				// 没有发现错误，保存人员盘查信息，
				// 在上传以前，工作编号对应ID,上传后对应工作编号
				pcryxx.setGzbh(gzxx.getId());
				ZaPcdjDao.insertPcryxx(pcryxx, getContentResolver());
				Intent i = new Intent();
				setResult(RESULT_OK, i);
				finish();
			} else
				GlobalMethod.showErrorDialog(errInfo, self);
		}
	};

	private OnClickListener cardDetailClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (mPerson != null && !TextUtils.isEmpty(mPerson.getIdNum())) {
				GlobalMethod.showCardReadDialog(mPerson, self);
			}
		}
	};

	private OnClickListener quitePcry = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (ryMod == MODINSERT || ryMod == MODMODIFY) {
				GlobalMethod.showDialogTwoListener("系统提示", "盘查人员信息没有保存，是否退出",
						"退出", "取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								finish();
							}
						}, self);
			} else {
				finish();
			}
		}
	};

	@Override
	public void onBackPressed() {
		if (ryMod == MODINSERT || ryMod == MODMODIFY) {
			GlobalMethod.showDialogTwoListener("系统提示", "盘查人员信息没有保存，是否退出", "退出",
					"取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							finish();
						}
					}, self);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQ_HJQH) {
				edHjqh.setText(data.getStringExtra(HJQH_EXTRA));
			}
		}
	}

	/**
	 * 从布局文件中初始化控件，对下拉框赋选择值
	 */
	private void initViewFromXml() {
		//btnCardDetail = (Button) findViewById(R.id.But_card);
		edPcdd = (EditText) findViewById(R.id.Edit_pc_pcdd);
		edSfzh = (EditText) findViewById(R.id.Edit_pc_sfzh);
		edXm = (EditText) findViewById(R.id.Edit_pc_xm);
		edCsrq = (EditText) findViewById(R.id.Edit_pc_csrq);
		edSg = (EditText) findViewById(R.id.Edit_pc_sg);
		edHjqh = (EditText) findViewById(R.id.edit_pcry_hjqh);
		edXxzz = (EditText) findViewById(R.id.Edit_pc_xxzz);
		edFwcs = (EditText) findViewById(R.id.Edit_pc_fwcs);
		edLxdh = (EditText) findViewById(R.id.Edit_pc_lxdh);
		chkQgrk = (CheckBox) findViewById(R.id.CheckBox_country);

		spPcyy = (Spinner) findViewById(R.id.Spin_pc_pcyy);
		GlobalMethod.changeAdapter(spPcyy,
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCYY), this);

		spXb = (Spinner) findViewById(R.id.Spin_pc_xb);
		GlobalMethod.changeAdapter(spXb, ZaPcdjDao.zapcDic.get(ZaPcdjDao.XB),
				this);

		spWhcd = (Spinner) findViewById(R.id.Spin_pc_whcd);
		GlobalMethod.changeAdapter(spWhcd,
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.WHCD), this);

		spMz = (Spinner) findViewById(R.id.Spin_pc_mz);
		GlobalMethod.changeAdapter(spMz, ZaPcdjDao.zapcDic.get(ZaPcdjDao.MZ),
				this);

		spByzk = (Spinner) findViewById(R.id.Spin_pc_byzk);
		GlobalMethod.changeAdapter(spByzk,
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.BYZK), this);

		spHy = (Spinner) findViewById(R.id.Spin_pc_hy);
		GlobalMethod.changeAdapter(spHy, ZaPcdjDao.zapcDic.get(ZaPcdjDao.HYZK),
				this);

		spZjxy = (Spinner) findViewById(R.id.Spin_pc_zjxy);
		GlobalMethod.changeAdapter(spZjxy,
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.ZJXY), this);

		spJccfx = (Spinner) findViewById(R.id.Spin_pc_jcfx);
		GlobalMethod.changeAdapter(spJccfx,
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.JCCFX), this);

		spPcbdjg = (Spinner) findViewById(R.id.Spin_pc_bdjg);
		GlobalMethod.changeAdapter(spPcbdjg,
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCBDJG), this);

		spPccljg = (Spinner) findViewById(R.id.Spin_pc_cljg);
		GlobalMethod.changeAdapter(spPccljg,
				ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCCLJG), this);
	}
}
