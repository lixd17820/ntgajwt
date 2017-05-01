package com.ntga.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ntga.bean.KeyValueBean;
import com.ntga.bean.TruckCheckBean;
import com.ntga.bean.VioDrvBean;
import com.ntga.bean.VioVehBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.bean.WfxwCllxCheckBean;
import com.ntga.dao.ConnCata;
import com.ntga.dao.GlobalMethod;
import com.ydjw.pojo.GlobalQueryResult;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import java.util.List;

public class CommBackgroundThread extends Thread {
    public static final int TEST_NETWORK = 402;
    public static final String RESULT_TEST_NETWORK = "testNetwork";
    public static final String RESULT_CONN_CATALOG = "connCata";
    private final int queryCata;
    private Handler mHandler;
    private ConnCata conn;
    private String[] params;
    private Context context;

    public CommBackgroundThread(Handler mHandler, int queryCata, ConnCata conn, String[] params,
                                Context context) {
        this.mHandler = mHandler;
        this.queryCata = queryCata;
        this.conn = conn;
        this.params = params;
        this.context = context;
    }

    @Override
    public void run() {
        RestfulDao dao = RestfulDaoFactory.getDao(conn);
        Message msg = mHandler.obtainMessage();
        Bundle b = new Bundle();
        if (queryCata == TEST_NETWORK) {
            boolean isOK = dao.testNetwork();
            b.putBoolean(RESULT_TEST_NETWORK, isOK);
            b.putInt(RESULT_CONN_CATALOG, conn.getIndex());
        }
        msg.setData(b);
        mHandler.sendMessage(msg);
    }
}
