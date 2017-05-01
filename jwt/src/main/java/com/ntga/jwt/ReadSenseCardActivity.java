package com.ntga.jwt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ntga.bean.KeyValueBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalMethod;
import com.ntga.thread.QueryDrvVehThread;
import com.sdses.bean.ID2Data;


public class ReadSenseCardActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    boolean bOpenReadCard = false;
    MainActivityReceiver mActivity;

    private EditText editSfzh;
    private TextView mTVHint2, tvSfxx, tvBjxx;

    private Button btnQuery, btnCancel, btnClear;
    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.zhcx_idcard_bdbj);
        editSfzh = (EditText) findViewById(R.id.edit_sfzh);
        mTVHint2 = (TextView) findViewById(R.id.tv_cardxx);
        tvSfxx = (TextView) findViewById(R.id.tv_sfxx);
        tvBjxx = (TextView) findViewById(R.id.tv_bdxx);
        btnQuery = (Button) findViewById(R.id.btn_query);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnQuery.setOnClickListener(clQuery);
        btnCancel.setOnClickListener(clQuery);
        btnClear.setOnClickListener(clQuery);

        mActivity = new MainActivityReceiver();
        // 创建IntentFilter对象
        IntentFilter filter = new IntentFilter();
        // 注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
        filter.addAction("com.sdses.activity");
        // 注册Broadcast Receiver
        registerReceiver(mActivity, filter);
        setCardStatus("请按F1开启读卡");
        startService(new Intent(ReadSenseCardActivity.this, ReadID2Card.class));
        clearInfo();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // mManageReadIDCard.readID2CardClose();
        sendBroadcast(new Intent().setAction("com.sdses.ReadID2CardService")
                .putExtra("command", 3));
        stopService(new Intent(ReadSenseCardActivity.this, ReadID2Card.class));
        unregisterReceiver(mActivity);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.i(TAG, "event.getKeyCode()=" + event.getKeyCode());
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_F1:
                    if (bOpenReadCard) {
                        Log.i(TAG, "关闭读卡");
                        setCardStatus("关闭读卡...按F1开启读卡");
                        sendBroadcast(new Intent().setAction(
                                "com.sdses.ReadID2CardService").putExtra("command",
                                3));
                    } else {
                        Log.i(TAG, "开启读卡");
                        setCardStatus("开启读卡...按F1关闭读卡");
                        sendBroadcast(new Intent().setAction(
                                "com.sdses.ReadID2CardService").putExtra("command",
                                2));
                    }
                    bOpenReadCard = !bOpenReadCard;
                    break;
                case KeyEvent.KEYCODE_1:
                    break;
                case KeyEvent.KEYCODE_PROG_RED:
                    if (bOpenReadCard) {
                        Log.i(TAG, "关闭读卡");
                        setCardStatus("关闭读卡...按F1开启读卡");
                        sendBroadcast(new Intent().setAction(
                                "com.sdses.ReadID2CardService").putExtra("command",
                                3));
                    } else {
                        Log.i(TAG, "开启读卡");
                        setCardStatus("开启读卡... 按F1关闭读卡");
                        sendBroadcast(new Intent().setAction(
                                "com.sdses.ReadID2CardService").putExtra("command",
                                2));
                    }
                    bOpenReadCard = !bOpenReadCard;
                    break;
                case KeyEvent.KEYCODE_PROG_YELLOW:
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case RESULT_OK:
                Bundle b = data.getExtras(); // data为B中回传的Intent
                boolean _b = b.getBoolean("IOControl");
                int _times = b.getInt("waittimes");
                // 设置参数
                sendBroadcast(new Intent().setAction("").putExtra("IOControl", _b)
                        .putExtra("WaitTimes", _times));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private String showAll(ID2Data _ID2Data) {
        String sfzh = _ID2Data.getmID2Txt().getmID2Num().trim();
        editSfzh.setText(sfzh);
        String sfxx = "";
        sfxx += "\n　　姓名：" + _ID2Data.getmID2Txt().getmName().trim();
        sfxx += "\n　　身份证号：" + sfzh;
        sfxx += "\n　　户籍地址：" + _ID2Data.getmID2Txt().getmAddress().trim();
        sfxx += "\n　　性别：" + _ID2Data.getmID2Txt().getmGender().trim();
        sfxx += "\n　　户籍区划：" + _ID2Data.getmID2Txt().getmIssue().trim();
        sfxx += "\n　　有效期：" + _ID2Data.getmID2Txt().getmBegin().trim() + "--"
                + _ID2Data.getmID2Txt().getmEnd().trim();
        setSfxx(sfxx);
        return sfzh;

    }

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
                setBdxx("\n　　" + kv.getValue());
            } else {
                setBdxx("\n　　" + kv.getValue() + "无比中信息");
            }
        }
    };

    private void clearInfo() {
        setSfxx("");
        setBdxx("");
    }

    private void setCardStatus(String st) {
        mTVHint2.setText("读卡器状态：" + st);
    }

    private void setSfxx(String st) {
        tvSfxx.setText("身份信息：" + st);
    }

    private void setBdxx(String st) {
        tvBjxx.setText("比对信息：" + st);
    }

    private class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("com.sdses.activity")) {
                Bundle bundle = intent.getExtras();
                int cmd = bundle.getInt("command");// 获取Extra信息
                switch (cmd) {
                    case 1:
                        Log.i(TAG, "接收到读卡成功消息");
                        setCardStatus("读卡成功，" + (bOpenReadCard ? "按F1关闭读卡" : "按F1开启读卡"));
                        String sfzh = showAll((ID2Data) bundle.getSerializable("id2data"));
                        QueryDrvVehThread thread = new QueryDrvVehThread(bjbdHander, QueryDrvVehThread.OPER_BJBD, new String[]{sfzh}, self);
                        thread.doStart();
                        break;
                    case 2:
                        Log.i(TAG, "选卡成功");
                        setCardStatus("正在读卡...");
                        clearInfo();
                        break;
                    case 3:
                        setCardStatus("读卡失败");
                        break;
                    case 4://打开串口失败
                        setCardStatus("打开串口失败");
                        break;
                    case 5:////上电失败
                        setCardStatus("读卡模块上电失败");
                        break;
                    case 6://初始化SAM模块失败
                        setCardStatus("初始化SAM模块失败");
                        break;
                    case 7://获取SAM模块号失败
                        setCardStatus("获取SAM模块号失败");
                        break;
                    case 8://获取SAM模块号失败
                        setCardStatus("关闭读卡成功--按F1开启读卡");
                        break;
                    case 9://获取SAM模块号失败
                        setCardStatus("掉电失败");
                        break;
                    case 10://打开读卡成功
                        setCardStatus("开启读卡成功--按F1关闭读卡");
                        break;
                    case 11://打开读卡成功
                        setCardStatus("关闭读卡未知错误");
                        break;
                    default:
                        break;
                }
            }
        }
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
                clearInfo();
                editSfzh.setText("");
                //mConnected = false;
            }
        }
    };
}

