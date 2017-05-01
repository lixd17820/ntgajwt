package com.ntga.jwt;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ntga.adaper.MainMenuAdapter;
import com.ntga.bean.MenuGridBean;
import com.ntga.bean.MenuOptionBean;
import com.ntga.dao.ConnCata;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ViolationDAO;
import com.ntga.thread.UpdateInfoThread;
import com.ntga.tools.MainLoading;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class MainTestActivity extends ActionBarActivity {
    private Context self;
    private GridView gv;
    private MainReferService mrService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.main_grid);
        // 取出的登录信息是在登录界面中返回并保存的数据
        HashMap<String, String> logInfo = ViolationDAO
                .getLoginInfo(getContentResolver());
        if (logInfo != null) {
            // 保存数据在USER_DATA数据库中，原因可能是user_data是新装的
            ViolationDAO.saveMjgrxxIntoDB(logInfo, getContentResolver());
            // 重新加载内存中的民警个人信息,有可能变更的单位
            GlobalData.grxx = ViolationDAO.getMjgrxx(getContentResolver());
        }
        // 根据登录传来的数据,确定是否为离线模式
        //if (!getIntent().hasExtra("connCata"))
        //    finish();
        int connCata = getIntent().getIntExtra("connCata",
                ConnCata.UNKNOW.getIndex());
        GlobalData.connCata = ConnCata.getValByIndex(connCata);
        MainReferService.serverConnCatalog = GlobalData.connCata;

        setTitle("移动警务");

        gv = (GridView) findViewById(R.id.gridView1);

        List<MenuGridBean> list = MainLoading.parseMenuXml(self);
        // 根据权限过滤菜单
        list = MainLoading.filterMenuByQx(list, GlobalData.grxx.get("YHLX"));
        MainMenuAdapter ma = new MainMenuAdapter(MainTestActivity.this, list.get(0)
                .getOptions());

        gv.setAdapter(ma);
        setItemListeren();

        GlobalData.loginStatus = 2;
        if (!GlobalData.isInitLoadData)
            GlobalData.initGlobalData(getContentResolver());
        GlobalData.serialNumber = GlobalData.grxx
                .get(GlobalConstant.SERIAL_NUMBER);
        GlobalData.serialNumber = TextUtils.isEmpty(GlobalData.serialNumber) ? GlobalMethod
                .getSerial(self) : GlobalData.serialNumber;
        // 如果没有加载数据，则初始化数据

        // 运行服务
        if (!MainLoading.checkServerRunning(self, "com.ntga.jwt",
                "com.ntga.jwt.MainReferService")) {
            startService(new Intent(this, MainReferService.class));
        }
        // 启动下载线程和系统配置，需打开GPS时打开
        OperGpsHandler handler = new OperGpsHandler(this, 0);
        new UpdateInfoThread(self, handler).start();
        // 删除错误的警告
        ViolationDAO.delErrorJwjg(getContentResolver());

    }

    static class OperGpsHandler extends Handler {

        private final WeakReference<MainTestActivity> myActivity;
        private int cata;

        public OperGpsHandler(MainTestActivity activity, int cata) {
            myActivity = new WeakReference<MainTestActivity>(activity);
            this.cata = cata;
        }

        @Override
        public void handleMessage(Message msg) {
            MainTestActivity ac = myActivity.get();
            if (ac != null) {
                ac.operGpsStatus(msg);
            }
        }
    }

    private void operGpsStatus(Message msg) {
        Bundle data = msg.getData();
        if (data == null)
            return;
        boolean isGps = data.getBoolean(UpdateInfoThread.BOOL_OPER_GPS, false);
        if (isGps)
            GlobalMethod.toggleGPS(self);
    }

    /**
     * 设置各表格项中的监听
     */
    private void setItemListeren() {
        // 基本业务
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long arg3) {
                MainMenuAdapter ad = (MainMenuAdapter) adapter.getAdapter();
                MenuOptionBean m = ad.getItem(position);
                if (!TextUtils.isEmpty(m.getPck())
                        && !TextUtils.isEmpty(m.getClassName())) {
                    String dn = m.getDataName();
                    String data = m.getData();
                    if (TextUtils.equals(dn, "out")) {
                        Intent intent = new Intent(m.getClassName()); //广播内容
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(m.getPck(), m
                                .getClassName()));
                        if (!TextUtils.isEmpty(dn)
                                && !TextUtils.isEmpty(data)) {
                            intent.putExtra(m.getDataName(), m.getData());
                        }
                        intent.putExtra("title", m.getMenuName());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ad = new AlertDialog.Builder(self);
        ad.setTitle("系统确认");
        ad.setMessage("是否确定退出系统?");
        ad.setPositiveButton("确定", exitSystem);
        ad.setCancelable(false);
        ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        ad.show();
    }

    private DialogInterface.OnClickListener exitSystem = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            GlobalData.loginStatus = 1;
            mrService.logoutJwt();
            finish();
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(this, MainReferService.class);
        bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mrService = ((MainReferService.DownLoadServiceBinder) service)
                    .getService();

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // 打开照相机
            case R.id.menu_zhcx: {
                Intent intent = new Intent(self, ZhcxMainActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.menu_config: {
                Intent intent = new Intent(self, ConfigMainActivity.class);
                startActivity(intent);
            }
            return true;
            default:
                break;
        }
        return false;
    }


}
