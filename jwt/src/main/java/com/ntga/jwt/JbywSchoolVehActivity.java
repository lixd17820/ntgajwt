package com.ntga.jwt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ntga.bean.SchoolZtzBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.thread.QueryDrvVehThread;

import java.io.File;
import java.lang.ref.WeakReference;


public class JbywSchoolVehActivity extends ActionBarActivity {

    private static final int QRCODE_SCAN = 1000;
    private static final int HAS_INSTALL = 100;

    private EditText editTczbh;
    private Button btnQuery, btnQrCode, btnCancel;
    private TextView tvSchoolInfo;
    private Context self;
    private QueryDrvVehThread downloadThread;
    private File outSideDir;
    private String apkFile = "qrcode.4.7.3.apk";

    private View.OnClickListener clQuery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnQuery) {
                String code = editTczbh.getText().toString();
                if (TextUtils.isEmpty(code) || code.length() != 9) {
                    GlobalMethod.showErrorDialog("停车证号码长度不正确", self);
                    return;
                }
                querySchoolTcz(code);
            } else if (v == btnCancel) {
                finish();
            } else if (v == btnQrCode) {
                if (!hasApk()) {
                    GlobalMethod.showDialogTwoListener("系统提示", "未安装二维码扫描程序，请下载安装", "下载", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadThread = new QueryDrvVehThread(new DownloadQrcodeHandler(
                                    JbywSchoolVehActivity.this),
                                    QueryDrvVehThread.DOWNLOAD_QRCODE,
                                    new String[]{"/ydjw/DownloadNormalFile?file=qrcode.4.7.3.apk", new File(outSideDir,apkFile).getAbsolutePath()}, self
                            );
                            downloadThread.doStart();
                        }
                    }, self);
                    //GlobalMethod.showErrorDialog("未安装二维码扫描程序，请下载安装", self);
                    return;
                }
                Intent intent = new Intent();
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("CHARACTER_SET", "utf-8");
                startActivityForResult(intent, QRCODE_SCAN);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.jbyw_school_veh);
        editTczbh = (EditText) findViewById(R.id.edit_school_tczbh);
        tvSchoolInfo = (TextView) findViewById(R.id.school_info);
        btnQuery = (Button) findViewById(R.id.btn_left);
        btnQrCode = (Button) findViewById(R.id.btn_center);
        btnCancel = (Button) findViewById(R.id.btn_right);
        btnQuery.setOnClickListener(clQuery);
        btnCancel.setOnClickListener(clQuery);
        btnQrCode.setOnClickListener(clQuery);
        String od = Environment.getExternalStorageDirectory().getPath()
                + "/jwtdb/";
        outSideDir = new File(od);
        if (!outSideDir.exists())
            outSideDir.mkdirs();
    }

    private void querySchoolTcz(String code) {
        QueryDrvVehThread thread = new QueryDrvVehThread(new SchoolThread(
                JbywSchoolVehActivity.this),
                QueryDrvVehThread.QUERY_SCHOOL,
                new String[]{code}, self
        );
        thread.doStart();
    }

    private boolean hasApk(){
        PackageManager pm = self.getPackageManager();
        boolean hasQr = false;
        try {
            PackageInfo info = pm.getPackageInfo("com.google.zxing.client.android", 0);
            hasQr = info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return hasQr;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == QRCODE_SCAN) {
                String code = data.getStringExtra("SCAN_RESULT");
                if (!TextUtils.isEmpty(code)) {
                    editTczbh.setText(code);
                    querySchoolTcz(code);
                }
            }else if(requestCode == HAS_INSTALL){
                if(!hasApk()){
                    GlobalMethod.showErrorDialog("扫描程序未能正常安装，请重试",self);
                }
            }
        }
    }


    private void querySchoolHandler(Message msg) {
        tvSchoolInfo.setText("");
        Bundle b = msg.getData();
        WebQueryResult<SchoolZtzBean> re = (WebQueryResult<SchoolZtzBean>) b
                .getSerializable(QueryDrvVehThread.RESULT_SCHOOL);

        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (TextUtils.isEmpty(err)) {
            SchoolZtzBean tcz = re.getResult();
            if (tcz != null) {
                String s = "准停证信息：\n";
                s += "　　停车证号：" + tcz.getTczph().substring(1) + "\n";
                s += "　　学校：" + tcz.getSchoolName() + "\n";
                s += "　　班级：" + getChinaBanji(tcz.getBanji()) + "\n";
                s += "　　号牌种类：" + GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList, tcz.getHpzl()) + "\n";
                s += "　　号牌号码：" + tcz.getHphm() + "\n";
                s += "　　准停证种类：" + tcz.getZtzmc() + "\n";
                s += "　　准停时间：" + tcz.getTime1() + "\n";
                s += "　　准停时间：" + tcz.getTime2();
                tvSchoolInfo.setText(s);
            }
        } else {
            GlobalMethod.showErrorDialog(err, self);
            tvSchoolInfo.setText("准停证信息：\n　　无相关登记信息");
        }
    }

    private String getChinaBanji(String s) {
        String nj = s.substring(0, 1);
        String bj = s.substring(1);
        String c = nj + "年级" + Integer.valueOf(bj) + "班";
        return c;
    }

    private void installQrcode(Message msg) {
        Bundle b = msg.getData();
        int what = b.getInt("length");
        if (what == 0) {
            int step = b.getInt("step");
            downloadThread.setProgressText("文件下载进度"+ step + "%");
        } else if (what == 1) {
            File f = new File(outSideDir, apkFile);
            if(f.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + f),
                        "application/vnd.android.package-archive");
                startActivityForResult(intent, HAS_INSTALL);
            }
        }
    }

    static class DownloadQrcodeHandler extends Handler {

        private final WeakReference<JbywSchoolVehActivity> myActivity;

        public DownloadQrcodeHandler(JbywSchoolVehActivity activity) {
            myActivity = new WeakReference<JbywSchoolVehActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            JbywSchoolVehActivity ac = myActivity.get();
            if (ac != null) {
                ac.installQrcode(msg);
            }
        }
    }

    static class SchoolThread extends Handler {

        private final WeakReference<JbywSchoolVehActivity> myActivity;

        public SchoolThread(JbywSchoolVehActivity activity) {
            myActivity = new WeakReference<JbywSchoolVehActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            JbywSchoolVehActivity ac = myActivity.get();
            if (ac != null) {
                ac.querySchoolHandler(msg);
            }
        }
    }
}
