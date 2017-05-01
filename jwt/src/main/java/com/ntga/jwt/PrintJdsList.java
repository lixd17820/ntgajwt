package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.android.provider.flashcode.Flashcode;
import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.OnSpinnerItemSelected;
import com.ntga.adaper.TwoLineSelectAdapter;
import com.ntga.bean.JdsPrintBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.TwoLineSelectBean;
import com.ntga.bean.VioViolation;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.BlueToothPrint;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.PrintJdsTools;
import com.ntga.dao.ViolationDAO;
import com.ntga.zapc.ZapcReturn;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;

public class PrintJdsList extends ActionBarListActivity {

    private KeyValueBean printerInfo;
    private BlueToothPrint btp = null;
    public static final int REQUEST_ENABLE_BT = 2;
    private static final int MENU_PREVIEW = 10;
    private static final int MENU_PRINT = 11;
    private static final int MENU_UPLOAD = 12;
    private static final int MENU_DETAIL = 13;
    // private int printState = BlueToothPrint.BLUETOOTH_NONE;
    private ContentResolver resolver;
    // 在单选列表中显示的对象
    private List<TwoLineSelectBean> strList = null;
    private List<VioViolation> puList;
    // private TextView title;
    private String title;

    private List<KeyValueBean> wslbs;
    private Spinner spinWslb;

    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wslbs = createWslbs();
        self = this;
        resolver = getContentResolver();

        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.jwt_print_jds);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        // R.layout.jwt_punishment_title);
        // getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // 删除过多
        ViolationDAO.delOldViolation(GlobalConstant.MAX_RECORDS,
                getContentResolver());
        spinWslb = (Spinner) findViewById(R.id.spin_wszl);
        GlobalMethod.changeAdapter(spinWslb, wslbs, (Activity) self);
        puList = ViolationDAO.getViolationByConds(getQueryCond(), resolver);
        strList = new ArrayList<TwoLineSelectBean>();
        getList(puList);

        printerInfo = new KeyValueBean(
                GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME),
                GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS));

        // title = ((TextView) findViewById(R.id.title_left_text));
        title = "打印决定书-" + puList.size() + "条";
        // TextView t2 = (TextView) findViewById(R.id.title_right_text);
        TwoLineSelectAdapter ad = new TwoLineSelectAdapter(this,
                R.layout.two_line_list_item, strList);
        getListView().setAdapter(ad);
        setTitle(title);

        // 文书打印
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long arg3) {
                // 单选,修改其他为不选
                for (int i = 0; i < strList.size(); i++) {
                    TwoLineSelectBean c = strList.get(i);
                    if (i == position)
                        c.setSelect(!c.isSelect());
                    else
                        c.setSelect(false);
                }

                TwoLineSelectAdapter ad = (TwoLineSelectAdapter) parent
                        .getAdapter();
                ad.notifyDataSetChanged();
            }
        });
        spinWslb.setOnItemSelectedListener(new OnSpinnerItemSelected() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                puList = ViolationDAO.getViolationByConds(getQueryCond(),
                        resolver);
                getList(puList);
                ((TwoLineSelectAdapter) getListView().getAdapter())
                        .notifyDataSetChanged();
            }
        });
    }

    private String getQueryCond() {
        String where = "";
        String id = GlobalMethod.getKeyFromSpinnerSelected(spinWslb,
                GlobalConstant.KEY);
        if ("1".equals(id)) {
            where = Flashcode.VioViolation.WSLB + "='1' and "
                    + Flashcode.VioViolation.CFZL + "='2'";
        } else if ("2".equals(id))
            where = Flashcode.VioViolation.WSLB + "='1' and "
                    + Flashcode.VioViolation.CFZL + "='1'";
        else if ("3".equals(id))
            where = Flashcode.VioViolation.WSLB + "='3'";
        else if ("6".equals(id))
            where = Flashcode.VioViolation.WSLB + "='6'";
        return where;
    }

    private List<KeyValueBean> createWslbs() {
        List<KeyValueBean> list = new ArrayList<KeyValueBean>();
        list.add(new KeyValueBean("1", "简易处罚"));
        list.add(new KeyValueBean("2", "轻微警告"));
        list.add(new KeyValueBean("3", "强制措施"));
        list.add(new KeyValueBean("6", "违法通知"));
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_vio_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position = getSelectItem();
        if (position < 0) {
            GlobalMethod.showDialog("错误信息", "请选择一条记录打印!", "返回",
                    PrintJdsList.this);
            return true;
        }
        VioViolation punish = puList.get(position);
        switch (item.getItemId()) {
            case R.id.menu_jds_preview:
                ArrayList<JdsPrintBean> jds = PrintJdsTools.getPrintJdsContent(
                        punish, resolver);
                Intent intent = new Intent(self, JdsPreviewActivity.class);
                intent.putExtra("jds", jds);
                startActivity(intent);
                break;
            case R.id.menu_jds_print:
                printJdsBySelect(punish);
                break;
            case R.id.menu_jds_upload:
                if (TextUtils.equals(punish.getScbj(), "1")) {
                    GlobalMethod.showErrorDialog("文书已发送,无需重复发送", self);
                    return true;
                }
                UploadViolationThread thread = new UploadViolationThread(
                        uploadVioHandler, punish);
                thread.doStart();
                break;
            case R.id.menu_jds_detail:
                Intent intent2 = new Intent(self, PrintJdsDetailActivity.class);
                intent2.putExtra("jdsbh", punish.getJdsbh());
                startActivity(intent2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // @Override
    // public void onClick(View v) {
    // int position = getSelectItem();
    // if (position < 0) {
    // GlobalMethod.showDialog("错误信息", "请选择一条记录打印!", "返回",
    // PrintJdsList.this);
    // return;
    // }
    // VioViolation punish = puList.get(position);
    // if (v.getId() == R.id.Butt_print) {
    // printJdsBySelect(punish);
    // } else if (v.getId() == R.id.Butt_prev) {
    // ArrayList<JdsPrintBean> jds = PrintJdsTools.getPrintJdsContent(
    // punish, resolver);
    // Intent intent = new Intent(self, JdsPreviewActivity.class);
    // intent.putExtra("jds", jds);
    // startActivity(intent);
    // } else if (v.getId() == R.id.Butt_send) {
    // if (TextUtils.equals(punish.getScbj(), "1")) {
    // GlobalMethod.showErrorDialog("文书已发送,无需重复发送", self);
    // return;
    // }
    // UploadViolationThread thread = new UploadViolationThread(
    // uploadVioHandler, punish);
    // thread.doStart();
    // }
    // }

    /**
     * 上传处罚决定书的控制操作
     */
    private Handler uploadVioHandler = new Handler() {
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
                    puList = ViolationDAO.getViolationByConds(getQueryCond(),
                            resolver);
                    getList(puList);
                    ((TwoLineSelectAdapter) getListView().getAdapter())
                            .notifyDataSetChanged();
                } else {
                    GlobalMethod.showToast("文书上传失败", self);
                }

            } else {
                GlobalMethod.showErrorDialog(err, self);
            }
        }
    };

    private int getSelectItem() {
        int position = -1;
        int i = 0;
        while (strList.size() > 0 && i < strList.size()) {
            if (strList.get(i).isSelect()) {
                position = i;
                break;
            }
            i++;
        }
        return position;
    }

    /**
     * 异步上传违法决定书
     *
     * @author lenovo
     */
    private class UploadViolationThread extends Thread {

        private Handler mHandler;
        private ProgressDialog progressDialog;
        VioViolation vio;

        public UploadViolationThread(Handler handler, VioViolation vio) {
            this.mHandler = handler;
            this.vio = vio;
        }

        public void doStart() {
            // 显示进度对话框
            progressDialog = ProgressDialog.show(self, "提示", "正在发送文书,请稍等...",
                    true);
            progressDialog.setCancelable(true);
            this.start();
        }

        /**
         * 线程运行
         */
        @Override
        public void run() {
            // WebQueryResult<LoginMessage> rs =
            // ViolationDAO.uploadViolation(vio);
            WebQueryResult<ZapcReturn> rs = ViolationDAO.uploadViolation(vio);
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putSerializable("queryResult", rs);
            msg.setData(b);
            mHandler.sendMessage(msg);
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btp != null) {
            btp.closeConn();
        }
    }

    private void printJdsBySelect(VioViolation vio) {
        if (TextUtils.isEmpty(printerInfo.getValue())) {
            GlobalMethod.showDialog("错误信息", "没有配置默认打印机!", "返回", self);
            return;
        }
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            return;
        }
        if (btp == null)
            btp = new BlueToothPrint(printerInfo.getValue());
        if (btp.getBluetoothStatus() != BlueToothPrint.BLUETOOTH_STREAMED) {
            // 没有建立蓝牙串口流
            int errorStaus = btp.createSocket(btAdapter);
            if (errorStaus != BlueToothPrint.SOCKET_SUCCESS) {
                GlobalMethod.showErrorDialog(
                        btp.getBluetoothCodeMs(errorStaus), self);
                return;
            }
        }
        int status = btp.printJdsByBluetooth(vio, getContentResolver());
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    private void getList(List<VioViolation> puList) {
        strList.clear();
        for (VioViolation v : puList) {
            TwoLineSelectBean ts = new TwoLineSelectBean();
            String text1 = "";
            String text2 = v.getWfsj() + " 代码: " + v.getWfxw1() + " "
                    + v.getDsr();
            if (Integer.valueOf(v.getWslb()) == 1
                    && TextUtils.equals(v.getCfzl(), "1")) {
                text1 = "警告";
            } else if (Integer.valueOf(v.getWslb()) == 1) {
                text1 = "简易";
            } else if (Integer.valueOf(v.getWslb()) == 3) {
                text1 = "强制";
            } else if (Integer.valueOf(v.getWslb()) == 6) {
                text1 = "通知";
            }
            text1 += " " + v.getJdsbh()
                    + (Integer.valueOf(v.getScbj()) == 0 ? " 未上传" : " 已上传");
            ts.setText1(text1);
            ts.setText2(text2);
            ts.setSelect(false);
            strList.add(ts);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
