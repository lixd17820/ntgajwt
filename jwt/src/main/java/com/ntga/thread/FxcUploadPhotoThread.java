package com.ntga.thread;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ntga.bean.VioFxcFileBean;
import com.ntga.bean.VioFxczfBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalMethod;
import com.ntga.database.FxczfDao;
import com.ntga.tools.ZipUtils;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FxcUploadPhotoThread extends Thread {
    private Handler mHandler;
    private VioFxczfBean fxc;
    private List<VioFxcFileBean> files;
    private Context context;

    // private ProgressDialog progressDialog;

    public FxcUploadPhotoThread(Context context, Handler mHandler,
                                VioFxczfBean fxc, List<VioFxcFileBean> files) {
        this.mHandler = mHandler;
        this.context = context;
        this.fxc = fxc;
        this.files = files;
    }

    public void doStart() {
        start();
    }

    private void sendData(String err, int what, int step) {
        Message m = mHandler.obtainMessage();
        m.what = what;
        m.arg1 = step;
        if (!TextUtils.isEmpty(err)) {
            Bundle data = new Bundle();
            data.putString("err", err);
            m.setData(data);
        }
        mHandler.sendMessage(m);
    }

    private void sendData(Bundle data, int what, int step) {
        Message m = mHandler.obtainMessage();
        m.what = what;
        m.arg1 = step;
        if (data != null)
            m.setData(data);
        mHandler.sendMessage(m);
    }

    @Override
    public void run() {
        int step = 0;
        double bc = (double) 100 / (double) (files.size() + 2);
        RestfulDao dao = RestfulDaoFactory.getDao();
        //修改，先上传照片，全部成功后上传文字，上传照片前获取MD5，上传中核对，保证数据完整性
        List<String> imageInfo = new ArrayList<String>();
        List<String> zpUpOk = new ArrayList<String>();
        for (int i = 0; i < files.size(); i++) {
            VioFxcFileBean zp = files.get(i);
            File photo = new File(zp.getWjdz());
            GlobalMethod.savePicLowFiftyByte(photo);
            //MD5
            String md5 = ZipUtils.getFileMd5(photo);
            String re = dao.uploadFxcPhotoAndMd5(photo, md5);
            String imageBh = GlobalMethod.getJsonField(re,"image_bh");
            if ( TextUtils.isEmpty(imageBh)) {
                sendData("图片上传错误", GlobalConstant.WHAT_ALL_ERR, 0);
                return;
            }
            zpUpOk.add(zp.getId());
            imageInfo.add( md5 + "," + imageBh);
            sendData("第" + (i + 1) + "图片上传成功", GlobalConstant.WHAT_PHOTO_OK, (int) (++step * bc));
        }
        String rs = dao.uploadFxczfJlAndImage(fxc, imageInfo);
        String xtbh = GlobalMethod.getJsonField(rs, "xtbh");
        if (TextUtils.isEmpty(xtbh)) {
            sendData("数据上传错误", GlobalConstant.WHAT_ALL_ERR, 0);
            return;
        }
        sendData("记录上传成功", GlobalConstant.WHAT_RECODE_OK, (int) (++step * bc));
        FxczfDao fxcDao = new FxczfDao(context);
        //上传成功后将照片打上成功标记
        //for (String zpId : zpUpOk) {
        //    fxcDao.setPhotoBj(zpId, "1");
        //}
        fxcDao.updateXtbhScbj(fxc.getId(), xtbh);
        fxcDao.closeDb();
        Bundle data = new Bundle();
        data.putString("xtbh", xtbh);
        data.putString("id", fxc.getId());
        sendData(data, GlobalConstant.WHAT_ALL_OK, (int) (++step * bc));
    }



}
