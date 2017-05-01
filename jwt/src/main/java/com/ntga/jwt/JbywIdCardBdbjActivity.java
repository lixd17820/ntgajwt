package com.ntga.jwt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ntga.bean.KeyValueBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.card.Error;
import com.ntga.card.PersonInfo;
import com.ntga.card.ReadCardThread;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ZaPcdjDao;
import com.ntga.thread.QueryDrvVehThread;
import com.ntga.zhcx.ZhcxThread;
import com.ydjw.pojo.GlobalQueryResult;

import org.apache.http.HttpStatus;


public class JbywIdCardBdbjActivity extends ActionBarActivity {

    private EditText editSfzh;
    private TextView tvCard, tvSfxx, tvBjxx;

    private Button btnQuery, btnCancel, btnClear;
    private Context self;
    private BluetoothAdapter mbta;
    private ReadCardThread mrcThread;
    private PersonInfo mPerson;
    private boolean mConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.zhcx_idcard_bdbj);
        editSfzh = (EditText) findViewById(R.id.edit_sfzh);
        tvCard = (TextView) findViewById(R.id.tv_cardxx);
        tvSfxx = (TextView) findViewById(R.id.tv_sfxx);
        tvBjxx = (TextView) findViewById(R.id.tv_bdxx);
        btnQuery = (Button) findViewById(R.id.btn_query);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnQuery.setOnClickListener(clQuery);
        btnCancel.setOnClickListener(clQuery);
        btnClear.setOnClickListener(clQuery);
        initCardReader();
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
            setCardStatus("无身份证读卡器");
            return false;
        } else {
            setCardStatus(cardName);
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
            Log.e("JbywIdCard", "status: " + msg.what);
            switch (msg.what) {
                case ReadCardThread.WM_CLEARSCREEN:
                    // mPerson.Empty();
                    setCardStatus("正在读卡...");
                    // mView.invalidate();
                    break;
                case ReadCardThread.WM_READCARD:
                    mPerson = (PersonInfo) msg.obj;
                    setCardStatus("读卡成功！");
                    mConnected = true;
                    if (mPerson != null) {
                        // 启动异步查询线程
                        editSfzh.setText(mPerson.getIdNum());
                        boolean isLoc = mPerson.getAuthority().startsWith("南通市")
                                || mPerson.getAuthority().startsWith("通州市")
                                || mPerson.getAuthority().startsWith("如皋市")
                                || mPerson.getAuthority().startsWith("如东")
                                || mPerson.getAuthority().startsWith("启东")
                                || mPerson.getAuthority().startsWith("海安")
                                || mPerson.getAuthority().startsWith("海门");
                        //queryXxByShzh(mPerson.getIdNum(), isLoc);
                        String sfxx = "";
                        sfxx += "\n姓名：" + mPerson.getName();
                        sfxx += "\n身份证号：" + mPerson.getIdNum();
                        sfxx += "\n户籍地址：" + mPerson.getAddress();
                        sfxx += "\n性别：" + mPerson.getSex();
                        sfxx += "\n户籍区划：" + mPerson.getAuthority();
                        sfxx += "\n有效期：" + mPerson.getValidStart() + "至" + mPerson.getValidEnd();
                        setSfxx(sfxx);
                        QueryDrvVehThread thread = new QueryDrvVehThread(bjbdHander, QueryDrvVehThread.OPER_BJBD, new String[]{mPerson.getIdNum()}, self);
                        thread.doStart();
                    }
                    // mView.invalidate();
                    break;
                case ReadCardThread.WM_ERROR:
                    if (msg.arg2 == com.ntga.card.Error.ERR_FIND) {
                        //if (!mConnected) {
                        setCardStatus("请放卡...");
                        mConnected = true;
                        //}
                    } else {
                        if (msg.arg2 == Error.ERR_PORT) {
                            mConnected = false;
                            setCardStatus("无设备");
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

    private Handler bjbdHander = new Handler() {

        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            WebQueryResult<KeyValueBean> re = (WebQueryResult<KeyValueBean>) b.getSerializable(QueryDrvVehThread.RESULT_BJBD);
            String err = GlobalMethod.getErrorMessageFromWeb(re);
            if (!TextUtils.isEmpty(err)) {
                GlobalMethod.showErrorDialog(err, self);
                return;
            }
            KeyValueBean kv = re.getResult();
            if (kv == null) {
                GlobalMethod.showErrorDialog("服务器出现错误，请联系管理员", self);
                return;
            }
            if (TextUtils.equals("1", kv.getKey())) {
                setBdxx("\n" + kv.getValue());
            } else {
                setBdxx("\n" + kv.getValue() + "无比中信息");
            }
        }
    };

    public void queryXxByShzh(String gmsfhm, boolean isLoc) {
        ZhcxThread thread = new ZhcxThread(cxryHandler);
        if (!isLoc)
            thread.doStart(self, "Q003", "SFZH='" + gmsfhm.toUpperCase() + "'");
        else
            thread.doStart(self, "C005", "gmsfhm='" + gmsfhm.toUpperCase()
                    + "'");
    }

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
                        String[] names = zhcx.getNames();
                        String[] content = zhcx.getContents()[0];
                        // 根据查询的内容对界面赋值
                        String sfxx = "";
                        int pos = -1;
                        pos = GlobalMethod.getPositionFromArray(names, "XM");
                        sfxx += "\n姓名：" + (pos > -1 ? content[pos] : "");
                        if (TextUtils.equals(zhcx.getCxid(), "Q003")) {
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "ZZXZ");
                            sfxx += "\n住址：" + (pos > -1 ? content[pos] : "");
                            pos = GlobalMethod
                                    .getPositionFromArray(names, "MZ");
                            sfxx += "\n民族：" + GlobalMethod.getStringFromKVListByKey(ZaPcdjDao.zapcDic.get(ZaPcdjDao.MZ), (pos > -1 ? content[pos] : ""));
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "HYZK");
                            sfxx += "\n婚姻：" + GlobalMethod.getStringFromKVListByKey(ZaPcdjDao.zapcDic.get(ZaPcdjDao.HYZK), (pos > -1 ? content[pos] : ""));
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "BYQK");
                            sfxx += "\n兵役：" + GlobalMethod.getStringFromKVListByKey(ZaPcdjDao.zapcDic.get(ZaPcdjDao.BYZK), (pos > -1 ? content[pos] : ""));
                        } else {
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "HJXZ");
                            sfxx += "\n住址：" + (pos > -1 ? content[pos] : "");
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "WHCD");
                            sfxx += "\n文化：" + GlobalMethod.getStringFromKVListByKey(ZaPcdjDao.zapcDic.get(ZaPcdjDao.WHCD), (pos > -1 ? content[pos] : ""));
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "LXDH");
                            sfxx += "\n电话：" + (pos > -1 ? content[pos] : "");
                            pos = GlobalMethod
                                    .getPositionFromArray(names, "MZ1");
                            sfxx += "\n民族：" + (pos > -1 ? content[pos] : "");
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "HYZK");
                            sfxx += "\n婚姻：" + GlobalMethod.getStringFromKVListByKey(ZaPcdjDao.zapcDic.get(ZaPcdjDao.HYZK), (pos > -1 ? content[pos] : ""));
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "BYZK");
                            sfxx += "\n兵役：" + GlobalMethod.getStringFromKVListByKey(ZaPcdjDao.zapcDic.get(ZaPcdjDao.BYZK), (pos > -1 ? content[pos] : ""));
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "HJQH");
                            sfxx += "\n户籍：" + (pos > -1 ? content[pos] : "");
                        }
                        setSfxx(sfxx);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnected) {
            mrcThread.StopReadCard();
            mrcThread.cancel();
        }
    }


    private void setCardStatus(String st) {
        tvCard.setText("读卡器状态：" + st);
    }

    private void setSfxx(String st) {
        tvSfxx.setText("身份信息：" + st);
    }

    private void setBdxx(String st) {
        tvBjxx.setText("比对信息：" + st);
    }

    private View.OnClickListener clQuery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnQuery) {
                String sfzh = editSfzh.getText().toString().toUpperCase();
                //ZhcxThread thread = new ZhcxThread(cxryHandler);
                //thread.doStart(self, "Q003", "SFZH='" + sfzh.toUpperCase() + "'");
                if (TextUtils.getTrimmedLength(sfzh) != 18) {
                    GlobalMethod.showErrorDialog("身份证号不是十八位", self);
                    return;
                }
                QueryDrvVehThread bdThread = new QueryDrvVehThread(bjbdHander, QueryDrvVehThread.OPER_BJBD, new String[]{sfzh}, self);
                bdThread.doStart();
            } else if (v == btnCancel) {
                finish();
            } else if (v == btnClear) {
                setSfxx("");
                setBdxx("");
                setCardStatus("");
                editSfzh.setText("");
                //mConnected = false;
            }
        }
    };

}
