package com.ntga.jwt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.ntga.bean.UpdateFile;
import com.ntga.bean.VioFxczfBean;
import com.ntga.bean.VioViolation;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.ConnCata;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.GlobalSystemParam;
import com.ntga.dao.ViolationDAO;
import com.ntga.database.FxczfDao;
import com.ntga.thread.FxcListUploadThread;
import com.ntga.thread.LogoutJwtNewThread;
import com.ntga.tools.GpsUtils;
import com.ntga.tools.TypeCenvert;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import org.apache.http.HttpStatus;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainReferService extends Service {

    private String TAG = "MainReferService";

    public static final int NOT_ID = 1;
    public static final int JQTB_DOWN_NOTICE_ID = 9876;
    public static final String SERVER_BROADCAST = "com.ntga.jwt.main.server";
    public static final String JQTB_DOWNLOAD_BROADCAST = "com.ntga.jwt.main.jqtb";

    private boolean isGpsComeIn = false;
    private LocationManager locm;
    public static Location location = null;
    private long preLocationTime = 0;
    private NotificationManager noticeManager;
    private Notification notification, jqtbDownNotification;

    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ConnCata serverConnCatalog;

    public static Boolean jqtbIsDown = true;

    @Override
    public IBinder onBind(Intent arg0) {
        return serviceBinder;
    }

    public class DownLoadServiceBinder extends Binder {
        public MainReferService getService() {
            return MainReferService.this;
        }
    }

    private Binder serviceBinder = new DownLoadServiceBinder();

    /**
     * 登出系统
     */
    public void logoutJwt() {
        LogoutJwtNewThread logout = new LogoutJwtNewThread(logoutHandler);
        logout.doStart();
    }


    private Handler logoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int log = msg.what;
            Toast.makeText(MainReferService.this,
                    log == 0 ? "未能正常退出，可重新登录后退出" : "警务系统正常退出",
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        noticeManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 初始化全局数据
        if (!GlobalData.isInitLoadData)
            GlobalData.initGlobalData(getContentResolver());
        GlobalMethod.readParam(this);
        //测试网络
        new Timer("testNetwrok").scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                testNetwork();
            }
        }, 10 * 60 * 1000, 60 * 60 * 1000);


        // 初如化位置管理
        initLocation();
        //定期管理违法行为定时器
        new Timer("uploadViolation").scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                backgroundRefreshMessage();
            }
        }, 20 * 60 * 1000, 60 * 60 * 1000);

        // 与服务器进行UDP通讯，发送心跳包或GPS包
        new Timer("getMessage").scheduleAtFixedRate(new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            sendGpsUpdateInfo();
                                                        }
                                                    }, 60 * 1000,
                GlobalSystemParam.uploadFreq * 60 * 1000
        );
    }

    private void testNetwork() {
        serverConnCatalog = ConnCata.UNKNOW;
        RestfulDao dao = RestfulDaoFactory.getDao(ConnCata.JWTCONN);
        boolean isOK = dao.testNetwork();
        if (isOK) {
            this.serverConnCatalog = ConnCata.JWTCONN;
            return;
        }
        dao = RestfulDaoFactory.getDao(ConnCata.OUTSIDECONN);
        isOK = dao.testNetwork();
        if (isOK) {
            this.serverConnCatalog = ConnCata.OUTSIDECONN;
            return;
        }
        dao = RestfulDaoFactory.getDao(ConnCata.INSIDECONN);
        isOK = dao.testNetwork();
        if (isOK) {
            this.serverConnCatalog = ConnCata.INSIDECONN;
            return;
        }
        if (serverConnCatalog != null)
            Log.e(TAG, "后台服务连接类型为：" + serverConnCatalog.getName());
    }

    /**
     * 发送GPS更新信息
     */
    private void sendGpsUpdateInfo() {
        if (serverConnCatalog == ConnCata.UNKNOW || serverConnCatalog == ConnCata.OFFCONN)
            return;
        boolean isGpsEnable = locm
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GlobalSystemParam.isGpsUpload || !isGpsEnable || !isGpsComeIn
                || location == null || !isLocationIsChange())
            return;
        preLocationTime = location.getTime();
        String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
        byte[] b = new GpsUtils().getByteFromGps(jybh,
                location.getLongitude(), location.getLatitude(), (byte) 0,
                (byte) 0, (short) 0, 0, new Date());
        InetAddress curIp = getIpAddress();
        b = TypeCenvert.addByte(b,
                TypeCenvert.ip2Byte(curIp.getHostAddress()));
        b = TypeCenvert.addByte(b,
                TypeCenvert.long2Byte(GlobalData.loginStatus));
        RestfulDaoFactory.getDao(serverConnCatalog).uploadGpsInfo(b);
        Log.e("MainReferService", "gps package length: " + b.length);
    }

    GpsStatus.Listener stlist = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int st) {
            if (st == GpsStatus.GPS_EVENT_STARTED) {
            } else if (st == GpsStatus.GPS_EVENT_STOPPED) {
            } else if (st == GpsStatus.GPS_EVENT_FIRST_FIX) {
                isGpsComeIn = true;
            } else if (st == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            }
        }
    };

    LocationListener ll = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onLocationChanged(Location loc) {
            location = loc;
        }
    };

    private void initLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setBearingRequired(true);
        criteria.setAltitudeRequired(true);
        criteria.setCostAllowed(true);
        String serverName = Context.LOCATION_SERVICE;
        locm = (LocationManager) getSystemService(serverName);
        locm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 10, ll);
        locm.addGpsStatusListener(stlist);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        locm.removeUpdates(ll);
        locm.removeGpsStatusListener(stlist);
        super.onDestroy();
    }


    /**
     * 比较两个位置是否有变化
     *
     * @return
     */
    private boolean isLocationIsChange() {
        if (preLocationTime == 0)
            return true;
        return location.getTime() > preLocationTime;
    }

    private void checkFileNeedUpdate() {
        WebQueryResult<List<UpdateFile>> re = RestfulDaoFactory.getDao(serverConnCatalog).updateInfoRestful();
        int updateCount = 0;
        if (re != null && re.getStatus() == HttpStatus.SC_OK && re.getResult() != null) {
            List<UpdateFile> ufs = re.getResult();
            for (UpdateFile uf : ufs) {
                String content = uf.getFileName();
                double serVersion = GlobalMethod.str2Double(uf.getId());
                content += "服务器版本：" + serVersion;
                double version = GlobalMethod.getApkVerionName(uf.getPackageName(), this);
                content += "当前版本：" + version;
                content += " "
                        + ((version < serVersion) ? " X" : " √");
                Log.e("MainReferService uf", content);
                if (Math.floor(version) < Math.floor(serVersion)) {
                    updateCount++;
                }
            }
        }
        if (updateCount > 0) {
            NotificationCompat.Builder nb = new NotificationCompat.Builder(
                    MainReferService.this);
            nb.setSmallIcon(R.drawable.warn_red);
            nb.setTicker("警务通需要更新");
            nb.setContentTitle("警务通需要更新");
            nb.setContentText("警务通有" + updateCount + "文件需要更新，请重新登录下载更新");
            nb.setAutoCancel(true);
            nb.setWhen(System.currentTimeMillis());
            ComponentName comp = new ComponentName("com.jwt.update",
                    "com.jwt.update.ConfigUpdateActivity");
            Intent intent = new Intent();
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, intent, 0);
            nb.setContentIntent(contentIntent);
            Notification ni = nb.build();
            ni.defaults |= Notification.DEFAULT_SOUND;
            noticeManager.notify(1, ni);
        }
    }

    /**
     * 每60分种运行一次
     */
    protected void backgroundRefreshMessage() {
        Log.e("MainReferService", "60 min run");
        ViolationDAO.delOldViolation(GlobalConstant.MAX_RECORDS,
                getContentResolver());
        // 检查自选路段是否合法改在主程序验证
        //WfddDao.checkFavorWfld(getContentResolver());
        if (serverConnCatalog == ConnCata.UNKNOW || serverConnCatalog == ConnCata.OFFCONN)
            return;
        GlobalMethod.updateSysConfig(serverConnCatalog);
        //检查版本状态
        checkFileNeedUpdate();
        // 从服务器更新系统参数，GPS未打开的，打开GPS
        if (GlobalSystemParam.isGpsUpload
                && !locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GlobalMethod.toggleGPS(this);
            Toast.makeText(this, "支队通知：因工作需要，请打开GPS！", Toast.LENGTH_LONG).show();
            Log.e("MainReferService", "toggleGPS");
        }
        // 取得所有未上传的决定书
        List<VioViolation> vios = ViolationDAO
                .getUnloadViolations(getContentResolver());
        // 无需检测空
        for (int i = 0; i < vios.size(); i++) {
            ViolationDAO.uploadViolationSaveIt(vios.get(i), this, serverConnCatalog);
        }
        //上传所有不完整上传的非现场
        //FxczfDao fxczfDao = new FxczfDao(MainReferService.this);
        //List<VioFxczfBean> fxcs = fxczfDao.queryAllUncompleteUploadFxczf();
        //fxczfDao.closeDb();
        //if (fxcs != null && !fxcs.isEmpty()) {
        //    FxcListUploadThread thread = new FxcListUploadThread(MainReferService.this, null,
         //           fxcs, false);
        //    thread.doStart();
       // }
    }


    /**
     * 取出目前的IP地址
     */
    public InetAddress getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress.getHostAddress().startsWith("10")) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }


}
