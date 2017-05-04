package com.ntga.jwt;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.ntga.bean.LoginResultBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.ConnCata;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ViolationDAO;
import com.ntga.login.DownFileThread;
import com.ntga.login.Encrypt;
import com.ntga.login.LoginDao;
import com.ntga.login.LoginMjxxBean;
import com.ntga.login.LoginUpdateThread;
import com.ntga.login.Md5;
import com.ntga.login.UpdateFile;
import com.ntga.xml.CommParserXml;

import org.apache.http.HttpStatus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LoginActivity extends ActionBarActivity {

    private static final int MENU_CHECK_UPDATE = 101;
    private static final int MENU_READ_SERIAL = 102;

    private Context self;
    // 需下载的文件列表
    private int returnCount = 0;
    public static String outSideDir = "";

    // 登录成功后返回的信息对象,要重新发送给主程序
    private List<UpdateFile> needUps, oldNeedUps;
    private String mjjh, mm;
    public static String phoneSerial = "";
    private String newMd5, oldMd5;
    private EditText editMjjh, editPasswd;
    private ContentResolver contentRes;
    private LinearLayout tv1;
    private long tvClickTime = 0;

    private boolean isLoging = false;

    public static int DOWNFILE = 0;
    public static int REALLOGIN = 1;
    public static int CHECKMD5 = 2;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);
        GlobalData.connCata = ConnCata.INSIDECONN;
        MainReferService.serverConnCatalog = GlobalData.connCata;
        outSideDir = Environment.getExternalStorageDirectory().getPath()
                + "/jwtdb/";
        File f = new File(outSideDir);
        if (!f.exists())
            f.mkdirs();
        self = this;

        mjjh = ViolationDAO
                .getMjgrxxJh(getContentResolver());
        // 关闭WIFI
        //disableWifi();
        // 以下为新增的测试模块
        contentRes = getContentResolver();
        // 结束测试

        setTitle("系统登录");
        phoneSerial = LoginDao.getSerial(self);
        tv1 = (LinearLayout) findViewById(R.id.scrollView1);
        // tvInfo = (TextView) findViewById(R.id.tv_info);
        editMjjh = (EditText) findViewById(R.id.Edit_login_mjjh);
        editPasswd = (EditText) findViewById(R.id.Edit_login_passwd);
        editMjjh.setText(mjjh);


        findViewById(R.id.but_login).setOnClickListener(loginClick);
        findViewById(R.id.but_login_cancel).setOnClickListener(cancelLogin);
        tv1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                long cu = System.currentTimeMillis();
                if (cu - tvClickTime > 500) {
                    tvClickTime = cu;
                } else {
                    copySerial();
                    tvClickTime = 0;
                }

            }
        });
        oldNeedUps = LoginDao.getOldCompareUps(self);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_CHECK_UPDATE, Menu.NONE, "检查更新");
        menu.add(Menu.NONE, MENU_READ_SERIAL, Menu.NONE, "读本机串号");
        return super.onCreateOptionsMenu(menu);
    }

    private void copySerial() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", phoneSerial);
        clipboard.setPrimaryClip(clip);
        GlobalMethod.showDialog("系统提示", "本机串号为：" + phoneSerial + "，并已复制到剪贴板，可直接粘贴。", "确定", self);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CHECK_UPDATE:
                //connCata = (ConnectionCatalog) spinConn.getSelectedItem();
                //Intent intent = new Intent(self, UpdateFileActivity.class);
                //startActivity(intent);
                break;
            case MENU_READ_SERIAL:
                copySerial();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 取消登录监听
     */
    private OnClickListener cancelLogin = new OnClickListener() {

        public void onClick(View arg0) {

            GlobalMethod.showDialogTwoListener("系统提示", "是否退出登录", "退出", "返回",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, self);
        }
    };

    private OnClickListener loginClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mjjh = editMjjh.getText().toString();
            Editable passwd = editPasswd.getText();
            if (TextUtils.isEmpty(mjjh) || mjjh.length() != 8) {
                GlobalMethod.showErrorDialog("警号不能为空或不是八位", self);
                return;
            }
            if (TextUtils.isEmpty(passwd) || passwd.length() != 6) {
                GlobalMethod.showErrorDialog("密码不能为空或不是六位", self);
                return;
            }
            Encrypt enc = Encrypt.getInstance();

            try {
                mm = enc.hashEncrypt(passwd.toString());
                Log.e("jwt passwd", mm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //
            returnCount = 0;
            needUps = new ArrayList<UpdateFile>();
            oldMd5 = LoginDao.getLoginMd5(self);
            // 开始登录，隐藏控件
            setVisibleView(true);
            // 启动线程
            //if (TextUtils.isEmpty(oldMd5) || oldNeedUps == null)
            new LoginUpdateThread(new MyHandler(LoginActivity.this,
                    REALLOGIN)).doStart(mjjh, mm, phoneSerial, false, self);
            //else
            //    new LoginUpdateThread(new MyHandler(ConfigUpdateActivity.this,
            //            CHECKMD5)).doStart(mjjh, mm, phonoSerial, true, self);
        }
    };

    private void setVisibleView(boolean isLog) {
        isLoging = isLog;
        findViewById(R.id.scrollView1).setVisibility(
                isLoging ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.bottom_but).setVisibility(
                isLoging ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("update active return", "" + requestCode + "/" + resultCode);
        if (requestCode == Integer.valueOf(needUps.get(returnCount).getId())) {
            returnCount++;
            if (returnCount >= needUps.size()) {
                if (LoginDao.checkApkUnInstall(self) > 0) {
                    exitLogin("未能安装所有更新，请重新登录下载");
                } else
                    startMainSystem();
            } else {
                installApk(needUps.get(returnCount));
            }
        }
    }

    private void installApk(UpdateFile apk) {
        File f = new File(outSideDir, apk.getFileName());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + f),
                "application/vnd.android.package-archive");
        // 安装的结果将返回进入验证
        startActivityForResult(intent, Integer.valueOf(apk.getId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if (progressDialog != null && progressDialog.isShowing())
        // progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        GlobalMethod.showDialogTwoListener("系统提示", "是否退出登录", "退出", "返回",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, self);
    }

    private void exitLogin(String err) {
        setVisibleView(false);
        GlobalMethod.showErrorDialog(err, self);
    }

    private void checkMd5Handler(Message msg) {
        @SuppressWarnings("unchecked")
        WebQueryResult<String> re = (WebQueryResult<String>) msg.getData()
                .getSerializable("login");
        if (re == null || re.getStatus() != HttpStatus.SC_OK
                || TextUtils.isEmpty(re.getResult())) {
            exitLogin("请检查网络配置");
            return;
        }
        String login = re.getResult();
        // 用逗号分隔的两个字符，第一个为0，第二个为MD5，第一个不为0，第二个为错误描述
        String[] ar = login.split(",");
        if (ar == null || ar.length != 2) {
            exitLogin("服务器错误，请与管理员联系");
            return;
        }
        if (!TextUtils.equals("0", ar[0])) {
            exitLogin(ar[1]);
            return;
        }
        newMd5 = ar[1];

        if (!TextUtils.equals(newMd5, oldMd5)) {
            new LoginUpdateThread(new MyHandler(LoginActivity.this,
                    REALLOGIN)).doStart(mjjh, mm, phoneSerial, false, self);
            return;
        }
        // 验证成功
        if (oldNeedUps != null && !oldNeedUps.isEmpty()) {
            needUps = oldNeedUps;
            startDownloadThread();
            return;
        }
        startMainSystem();
    }

    /**
     * 经过登录验证，MD5不匹配后重新登录的回调，包括检查安装APK
     */
    @SuppressWarnings("unchecked")
    private void realLoginHandler(Message msg) {
        WebQueryResult<String> re = (WebQueryResult<String>) msg.getData()
                .getSerializable("login");
        if (re == null || re.getStatus() != HttpStatus.SC_OK
                || TextUtils.isEmpty(re.getResult())) {
            exitLogin("登录网络连接错误");
            return;
        }
        LoginResultBean login = null;
        try {
            login = CommParserXml.parseXmlToObj(re.getResult(),
                    LoginResultBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (login == null) {
            exitLogin("未知错误");
            return;
        }
        if (!TextUtils.equals(login.getCode(), "1")) {
            exitLogin(login.getCwms());
            return;
        }
        // 保存登录信息的MD5，下次登录检查MD5即可，不同再调用登录
        newMd5 = Md5.getMD5(re.getResult());

        LoginDao.saveLoginMd5IntoDb(newMd5, contentRes);
        // 保存个人信息
        LoginMjxxBean mj = login.getMj();
        if (mj != null) {
            LoginDao.saveSerialIntoDb(phoneSerial, contentRes);
            LoginDao.saveMjInfoIntoDb(mj, contentRes);
            //LoginDao.saveMjInfoIntoDb(mj, ConfigUpdateActivity.this);
        }
        UpdateFile[] ufs = login.getUfs();
        if (ufs == null || ufs.length == 0) {
            exitLogin("服务器出现错误，请与管理员联系");
            return;
        }
        // 保存文件列表到数据库中
        LoginDao.saveUpdateFileListIntoDb(ufs, contentRes);
        needUps = LoginDao.compareOldAndNewVersion(ufs, self);
        if (needUps.isEmpty()) {
            startMainSystem();
            return;
        }
        // 检查SD卡
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            exitLogin("版本更新需加载SD卡");
            return;
        }
        // 启动下载线程
        startDownloadThread();
    }

    private void showProgress(String message) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(self);
        progressDialog.setTitle("提示");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMessage(message);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    private void startDownloadThread() {
        showProgress("共有" + needUps.size() + "个文件需要下载");
        DownFileThread dfThread = new DownFileThread(new MyHandler(
                LoginActivity.this, DOWNFILE));
        dfThread.doStart(needUps);
    }

    /**
     * 启动主程序
     */
    private void startMainSystem() {
        Intent intent = new Intent(self, MainTestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        //ComponentName comp = new ComponentName("com.ntga.jwt",
        //        "com.ntga.jwt.MainTestActivity");
        //Intent intent = new Intent();
        //intent.setComponent(comp);
        //intent.setAction("android.intent.action.VIEW");
        //intent.putExtra("connCata", connCata.getIndex());
        // startActivity(intent);
        // finish();
    }

    android.content.DialogInterface.OnClickListener exitSystem = new android.content.DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // if (progressDialog != null && progressDialog.isShowing())
            // progressDialog.dismiss();
            dialog.dismiss();
            finish();
        }
    };

    static class MyHandler extends Handler {
        private final WeakReference<LoginActivity> myActivity;
        private int cata;

        public MyHandler(LoginActivity activity, int cata) {
            myActivity = new WeakReference<LoginActivity>(activity);
            this.cata = cata;
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity ac = myActivity.get();
            if (ac != null) {
                if (cata == DOWNFILE)
                    ac.operDownFileMessage(msg);
                else if (cata == CHECKMD5)
                    ac.checkMd5Handler(msg);
                else if (cata == REALLOGIN)
                    ac.realLoginHandler(msg);
            }
        }
    }

    private void operDownFileMessage(Message msg) {
        int err = msg.arg1;
        int what = msg.what;
        int step = msg.arg2;
        if (err == DownFileThread.DOWNLOADING_APK) {
            progressDialog.setMessage("正在下载第" + (what + 1) + "个共"
                    + needUps.size() + "个文件");
            progressDialog.setProgress(step);
        } else {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (err == DownFileThread.DOWNLOAD_OK && needUps.size() > 0) {
                installApk(needUps.get(0));
            } else if (err == DownFileThread.DOWNLOAD_FAIL) {
                exitLogin("未能成功下载所有更新，请重新登录");
            }
        }
    }

    private void disableWifi() {
        WifiManager mwifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mwifi.isWifiEnabled())
            mwifi.setWifiEnabled(false);
    }

}
