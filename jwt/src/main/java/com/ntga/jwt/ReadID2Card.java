package com.ntga.jwt;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.sdses.bean.ID2Data;
import com.sdses.common.CardBoard;
import com.sdses.common.impl.ReadCardFromSerialport;
import com.sdses.manage.ManageReadIDCard;

public class ReadID2Card extends Service {

    public static final String TAG = ReadID2Card.class.getSimpleName();
    boolean bIOControl = false; // 读卡过程是否控制GPIO
    boolean bExit = false; // 读卡线程退出
    int iWaitTimes = 200; // 读卡线程
    ReadID2CardReceiver mReadID2CardReceiver = null;
    ManageReadIDCard mManageReadIDCard;
    Thread readID2CardThread;

    //	Bundle mID2Bundle;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mReadID2CardReceiver = new ReadID2CardReceiver();

        IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
        // 注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
        filter.addAction("com.sdses.ReadID2CardService");
        // 注册Broadcast Receiver
        registerReceiver(mReadID2CardReceiver, filter);

        mManageReadIDCard = new ManageReadIDCard();
        mManageReadIDCard.getID2DataController("com.sdses.bean.ID2Data");
        mManageReadIDCard
                .getID2CardReader("com.sdses.common.impl.ReadCardFromSerialport");
//		mID2Bundle = new Bundle();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReadID2CardReceiver);
        Log.i(TAG, "服务取消");
    }

    private boolean openRFtoTypeB() {
        byte[] temp = new byte[256];
        int back = mManageReadIDCard.Command(0xFF0F, temp, 0, 11, 1, 200);
        if (1 == back) {
            if (temp[7] == (byte) 0x00 && temp[8] == (byte) 0x00
                    && temp[9] == (byte) 0x90) {
                Log.w(TAG, "设置TypeB模式成功");
                // System.arraycopy(temp, 10, dst, 0, 5);
                // dstStr = new String(dst);
                return true;
            }
        }
        return false;
    }

    private boolean closeRF() {
        byte[] temp = new byte[256];
        int back = mManageReadIDCard.Command(0xFF10, temp, 0, 11, 1, 200);
        if (1 == back) {
            if (temp[7] == (byte) 0x00 && temp[8] == (byte) 0x00
                    && temp[9] == (byte) 0x90) {
                Log.w(TAG, "关射频成功");
                // System.arraycopy(temp, 10, dst, 0, 5);
                // dstStr = new String(dst);
                return true;
            }
        }
        return false;
    }

    private class ReadCardThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.w(TAG, "in Thread ReadCardThread()");
            ((ReadCardFromSerialport) mManageReadIDCard.getmIReadIDCard()).setmBaurd(115200);
            ((ReadCardFromSerialport) mManageReadIDCard.getmIReadIDCard()).setmComPath("/dev/ttyHSL1");
            // 硬件初始化
            int b = mManageReadIDCard.readID2CardOpen();
            switch (b) {
                case 1:    //上电成功
                    CardBoard _cardBoard = mManageReadIDCard.getCardBoardInfo();
                    Log.i(TAG, "硬件版本" + _cardBoard.getCardBoardHardwareVersion());
                    Log.i(TAG, "软件版本" + _cardBoard.getCardBoardSoftVersion());
                    Log.i(TAG, "SAM模块号" + _cardBoard.getSAMSN());
                    Log.i(TAG, "读卡板序号" + _cardBoard.getCardBoardSN());

                    //提示用户打开读卡成功，可以读卡了。
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 10));
                    break;
                case 2:    //打开串口失败
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 4));
                    b = mManageReadIDCard.readID2CardClose();
                    Log.i(TAG, "关闭读卡=" + b);
                    return;
                case 3://上电失败
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 5));
                    b = mManageReadIDCard.readID2CardClose();
                    Log.i(TAG, "上电失败=" + b);
                    return;
                case 4://初始化SAM模块失败
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 6));
                    b = mManageReadIDCard.readID2CardClose();
                    Log.i(TAG, "初始化SAM模块失败=" + b);
                    return;
                case 5:    //获取SAM模块号失败
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 7));
                    b = mManageReadIDCard.readID2CardClose();
                    Log.i(TAG, "获取SAM模块号失败=" + b);
                    return;
            }
            Log.i(TAG, "读卡时间间隔=" + iWaitTimes);
            while (!bExit) {
                // 读卡
                if (mManageReadIDCard.searchID2Card() && mManageReadIDCard.selectID2Card()) {
                    //选卡成功---可以提示用户“正在读卡...”
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 2));
                    if (mManageReadIDCard.readID2Card()) {
                        Log.i(TAG, "读卡成功");
                        ((ID2Data) mManageReadIDCard.getmID2DataRAW()).rePackage();
                        sendBroadcast(new Intent()
                                .setAction("com.sdses.activity")
                                .putExtra("command", 1)
                                .putExtra("id2data", (ID2Data) mManageReadIDCard.getmID2DataRAW()));

                    } else {
                        Log.i(TAG, "读卡异常 读卡串口发生数据收发异常");//提示用户读卡失败--一般情况下是串口异常造成的（500BH）
                        sendBroadcast(new Intent().setAction("com.sdses.activity")
                                .putExtra("command", 3));
                    }

                } else {
                    //寻卡失败/选卡失败
                }
                // 休眠一段时间
                SystemClock.sleep(iWaitTimes);
            }
            // 释放资源
            b = mManageReadIDCard.readID2CardClose();
            Log.i(TAG, "关闭读卡=" + b);
            switch (b) {
                case 1://关闭读卡成功
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 8));
                    break;
                case 2://掉电失败
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 9));
                    break;
                default:
                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                            .putExtra("command", 11));
                    break;
            }
            Log.w(TAG, "读卡线程退出");
        }
    }

    private class ReadID2CardReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(
                    "com.sdses.ReadID2CardService")) {
                Bundle bundle = intent.getExtras();
                int cmd = bundle.getInt("command");// 获取Extra信息
                switch (cmd) {
                    case 1:
                        Log.w(TAG, "参数设置");
                        // 是否控制GPIO、设置读卡时间间隔
                        bIOControl = bundle.getBoolean("GPIO");
                        iWaitTimes = bundle.getInt("Waittime");
                        Log.i(TAG, "bIOControl=" + bIOControl);
                        Log.i(TAG, "iWaitTimes=" + iWaitTimes);
                        break;
                    case 2:
                        bExit = false;
                        // 开启读卡线程
                        readID2CardThread = new Thread(new ReadCardThread());//.start();
                        Log.i(TAG, "开启读卡线程ID= " + readID2CardThread.getId());
                        readID2CardThread.start();

                        break;
                    case 3:
                        if (!bExit) {
                            bExit = true;
                        } else {
                            int b = mManageReadIDCard.readID2CardClose();
                            Log.i(TAG, "关闭读卡线程ID=" + b);
                            switch (b) {
                                case 1://关闭读卡成功
                                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                                            .putExtra("command", 8));
                                    break;
                                case 2://掉电失败
                                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                                            .putExtra("command", 9));
                                    break;
                                default:
                                    sendBroadcast(new Intent().setAction("com.sdses.activity")
                                            .putExtra("command", 11));
                                    break;
                            }
                        }
                        if (readID2CardThread != null)
                            Log.i(TAG, "关闭读卡= " + readID2CardThread.getId());
                        // 关闭读卡线程
//					mManageReadIDCard.readID2CardClose();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
