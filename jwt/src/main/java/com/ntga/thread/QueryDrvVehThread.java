package com.ntga.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ntga.bean.ClgjBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.SchoolZtzBean;
import com.ntga.bean.TruckCheckBean;
import com.ntga.bean.VioDrvBean;
import com.ntga.bean.VioVehBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.bean.WfxwCllxCheckBean;
import com.ntga.dao.GlobalMethod;
import com.ydjw.pojo.GlobalQueryResult;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import java.io.File;
import java.util.List;

public class QueryDrvVehThread extends Thread {
    public static final int QUERY_DRV_DABH_INFO = 100;
    public static final int QUERY_TRUCK_CHECK = 101;
    public static final int QUERY_VEH_HPHM_INFO = 200;
    public static final int QUERY_QYMC_INFO = 210;
    public static final int QUERY_ZHCX = 400;
    public static final int QUERY_WFXW_CLLX_CHECK = 401;
    public static final int QUERY_FXCZF_RKQK = 403;
    public static final int DEL_PHOTO_FILE = 404;
    public static final int OPER_BJBD = 405;
    public static final int JSON_QUERY_DRV = 701;
    public static final int JSON_QUERY_VEH = 702;
    public static final int QUERY_CLGJ = 505;
    public static final int QUERY_SCHOOL = 600;
    public static final int DOWNLOAD_QRCODE = 601;
    public static final int DOWNLOAD_CARD = 602;
    public static final String RESULT_QYMC = "qymc";
    public static final String RESULT_DRV = "drv";
    public static final String RESULT_VEH = "veh";
    public static final String RESULT_ZHCX = "queryResult";
    public static final String RESULT_TRUCK_CHECK = "truck";
    public static final String RESULT_WFXW_CLLX_CHECK = "wfxwCllx";
    public static final String RESULT_FXCZF_RKQK = "res_fxc_rkqk";
    public static final String RESULT_BJBD = "res_bjbd";
    public static final String RESULT_CLGJ = "re_clgj";
    public static final String RESULT_SCHOOL = "re_school";
    public static final String RESULT_DOWN_QRCODE = "download_qrcode";
    public static final String RESULT_DOWN_CARD = "download_card";
    //简易程序查询驾驶员返加JSON数据
    public static final String RESULT_JSON_DRV = "json_drv";
    public static final String RESULT_JSON_VEH = "json_veh";

    private Handler mHandler;
    private int queryCata;
    private String[] params;
    private ProgressDialog progressDialog;
    private Context context;

    public QueryDrvVehThread(Handler mHandler, int queryCata, String[] params,
                             Context context) {
        this.mHandler = mHandler;
        this.queryCata = queryCata;
        this.params = params;
        this.context = context;
    }

    public void doStart() {
        Log.e("QueryDrvVehThread", "do start");
        progressDialog = ProgressDialog.show(context, "提示", "正在操作,请稍等...",
                true);
        progressDialog.setCancelable(true);
        start();
    }

    public void setProgressText(String txt) {
        progressDialog.setMessage(txt);
    }

    @Override
    public void run() {
        RestfulDao dao = RestfulDaoFactory.getDao();
        Message msg = mHandler.obtainMessage();
        Bundle b = new Bundle();
        if (queryCata == QUERY_DRV_DABH_INFO) {
            WebQueryResult<VioDrvBean> re = dao.queryVioDrv(params[0],
                    TextUtils.equals(params[1], "苏F") ? "1" : "0");
            b.putSerializable(RESULT_DRV, re);
        } else if (queryCata == QUERY_VEH_HPHM_INFO) {
            WebQueryResult<VioVehBean> re = dao.queryVioVeh(params[0],
                    params[1]);
            b.putSerializable(RESULT_VEH, re);
        } else if (queryCata == QUERY_ZHCX) {
            WebQueryResult<GlobalQueryResult> webResult = dao.zhcxRestful(
                    params[0], params[1]);
            b.putSerializable(RESULT_ZHCX, webResult);
        } else if (queryCata == QUERY_QYMC_INFO) {
            WebQueryResult<List<KeyValueBean>> webResult = dao.queryAllQymc();
            b.putSerializable(RESULT_QYMC, webResult);
        } else if (queryCata == QUERY_TRUCK_CHECK) {
            WebQueryResult<TruckCheckBean> webResult = dao.queryTruckCheck(
                    params[0], params[1]);
            b.putSerializable(RESULT_TRUCK_CHECK, webResult);
        } else if (queryCata == QUERY_WFXW_CLLX_CHECK) {
            WebQueryResult<String> re = dao.getAllWfxwCllxCheck();
            WebQueryResult<List<WfxwCllxCheckBean>> wr = GlobalMethod
                    .webXmlStrToListObj(re, WfxwCllxCheckBean.class);
            b.putSerializable(RESULT_WFXW_CLLX_CHECK, wr);
        } else if (queryCata == QUERY_FXCZF_RKQK) {
            WebQueryResult<String> re = dao.queryFxcRkqk(params[0]);
            b.putSerializable(RESULT_FXCZF_RKQK, re);
        } else if (queryCata == DEL_PHOTO_FILE) {
            int row = 0;
            if (params != null && params.length > 0) {
                for (String fs : params) {
                    File f = new File(fs);
                    if (f.exists())
                        row += f.delete() ? 1 : 0;
                }
            }
            b.putInt("delRow", row);
            Log.e("QueryDrvVehThread", "del row: " + row);
        } else if (queryCata == OPER_BJBD) {
            WebQueryResult<KeyValueBean> re = dao.getBjbdBySfzh(params[0]);
            b.putSerializable(RESULT_BJBD, re);
        } else if (queryCata == QUERY_CLGJ) {
            WebQueryResult<List<ClgjBean>> re = dao.queryClgj(params[0], params[1], params[2], params[3]);
            b.putSerializable(RESULT_CLGJ, re);
        } else if (queryCata == QUERY_SCHOOL) {
            WebQueryResult<SchoolZtzBean> re = dao.querySchool(params[0]);
            b.putSerializable(RESULT_SCHOOL, re);
        } else if (queryCata == DOWNLOAD_QRCODE) {
            long bytes = dao.downloadFile(params[0], new File(params[1]), 800000, mHandler);
            b.putLong(RESULT_DOWN_QRCODE, bytes);
        } else if (queryCata == DOWNLOAD_CARD) {
            long bytes = dao.downloadFile(params[0], new File(params[1]), 250000, mHandler);
            b.putLong(RESULT_DOWN_CARD, bytes);
        }else if (queryCata == JSON_QUERY_DRV) {
            msg.what =JSON_QUERY_DRV;
            String json = dao.jycxQueryDrv(params[0], params[1]);
            b.putString(RESULT_JSON_DRV, json);
        } else if (queryCata == JSON_QUERY_VEH) {
            msg.what = JSON_QUERY_VEH;
            String json = dao.jycxQueryVeh(params[0], params[1]);
            b.putString(RESULT_JSON_VEH, json);
        }
        msg.setData(b);
        Log.e("QueryDrvVehThread", "over thread");
        mHandler.sendMessage(msg);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
