package com.ntga.thread;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ntga.bean.SeriousStreetBean;
import com.ntga.bean.THmb;
import com.ntga.bean.WebQueryResult;
import com.ntga.bean.WfxwCllxCheckBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.GlobalSystemParam;
import com.ntga.dao.WfddDao;
import com.ntga.dao.WsglDAO;
import com.ntga.database.FxczfDao;
import com.ntga.database.MessageDao;
import com.ntga.tools.ZipUtils;
import com.ntga.xml.CommParserXml;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import org.apache.http.HttpStatus;

import java.io.File;
import java.util.List;

public class UpdateInfoThread extends Thread {
    private Context self;
    private Handler mHandler;
    public static final String BOOL_OPER_GPS = "isOpenGps";

    public UpdateInfoThread(Context context, Handler handler) {
        self = context;
        mHandler = handler;
    }

    private void synZhcxMenu(RestfulDao dao, File zhcxFile, String jh) {
        String xml = GlobalMethod.readFileContent(zhcxFile);
        if (!TextUtils.isEmpty(xml) && checkMd5(dao, xml, jh, "1001")) {
            return;
        }
        WebQueryResult<String> mu = dao.getSystemMenu();
        String err2 = GlobalMethod.getErrorMessageFromWeb(mu);
        if (TextUtils.isEmpty(err2) && !TextUtils.isEmpty(mu.getResult())) {
            GlobalMethod
                    .writeInDisk(zhcxFile.getAbsolutePath(), mu.getResult());
        }
    }

    @Override
    public void run() {
        //验证自选路段
        WfddDao.checkFavorWfld(self.getContentResolver());

        int crossRow = WfddDao.testCrossCount(self.getContentResolver());
        Log.e("UpThread", "路口测试数据" + crossRow);

        File innDir = self.getFilesDir();
        if (!innDir.exists())
            innDir.mkdirs();
        File zhcxFile = new File(innDir, "zhcx.xml");
        File wfxwCllxFile = new File(innDir, "wfxw.xml");
        String jh = GlobalData.grxx.get(GlobalConstant.YHBH);
        RestfulDao dao = RestfulDaoFactory.getDao();
        if(dao == null){
            return;
        }
        // 与服务器同步决定书编号
        synJdsHmb(dao, jh);
        // synJdsHmb("002", dao, jh);
        // 同步综合查询菜单
        synZhcxMenu(dao, zhcxFile, jh);

        // 更新违法行为和车辆类型关系模型
        synWfxwAndCllx(dao, wfxwCllxFile, jh);
        //同步严管违停
        synSeriousStreet();

        // 验证民警非现场是否完整
        WebQueryResult<ZapcReturn> re = dao.checkFxcZqmjAllUpload(jh);
        if (re != null && re.getStatus() == HttpStatus.SC_OK && re.getResult() != null && "1".equals(re.getResult().getCgbj())) {
            String[] phs = re.getResult().getPcbh();
            if (phs != null && phs.length > 0) {
                FxczfDao fxcDao = new FxczfDao(self);
                for (String ph : phs) {
                    fxcDao.setXtbhScbj(ph, "0");
                }
                fxcDao.closeDb();
            }
        }

        // 从服务器更新GPS开关和身份证等信息
        GlobalMethod.updateSysConfig();
        GlobalMethod.saveParam(self);
        // String pf = Environment.getExternalStorageDirectory()
        // .getAbsolutePath() + "/jwtdb/param.xml";
        // GlobalMethod.saveParam(self, pf);
        // 无需上专GPS，GPS设备已打开，则关闭
        //if (GlobalSystemParam.isGpsUpload) {
            LocationManager locm = (LocationManager) self
                    .getSystemService(Context.LOCATION_SERVICE);
            if (!locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putBoolean(BOOL_OPER_GPS, true);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        //}
    }

    private void synSeriousStreet() {
        MessageDao dao = new MessageDao(self);
        int version = dao.getSeriousVersion();
        RestfulDao rd = RestfulDaoFactory.getDao();
        WebQueryResult<List<SeriousStreetBean>> we = rd.getSeriousStreet("" + version);
        if (we.getStatus() == HttpStatus.SC_OK && we.getResult() != null) {
            List<SeriousStreetBean> list = we.getResult();
            if (!list.isEmpty()) {
                int del = dao.delAllSerious();
                int add = dao.addSeriousList(list);
                Log.e("UpdateThread serious", "更新严管违停：删除" + del + "新增" + add);
            }
        }
        dao.closeDb();

    }

    private boolean checkMd5(RestfulDao dao, String xml, String jh,
                             String catalog) {
        String md5 = ZipUtils.getMD5(xml);
        WebQueryResult<String> checkMd5 = dao.checkUserMd5(jh,
                GlobalData.serialNumber, md5, "10008");
        String err = GlobalMethod.getErrorMessageFromWeb(checkMd5);
        if (TextUtils.isEmpty(err) && !TextUtils.isEmpty(checkMd5.getResult())
                && TextUtils.equals(checkMd5.getResult(), md5)) {
            return true;
        }
        return false;
    }

    private void synWfxwAndCllx(RestfulDao dao, File wfxwCllxFile, String jh) {
        String xml = GlobalMethod.readFileContent(wfxwCllxFile);
        if (!TextUtils.isEmpty(xml) && checkMd5(dao, xml, jh, "1008")) {
            return;
        }
        int row = 0;
        WebQueryResult<String> str = dao.getAllWfxwCllxCheck();
        WebQueryResult<List<WfxwCllxCheckBean>> wfre = GlobalMethod
                .webXmlStrToListObj(str, WfxwCllxCheckBean.class);
        String err = GlobalMethod.getErrorMessageFromWeb(wfre);
        if (TextUtils.isEmpty(err) && wfre.getResult() != null) {
            List<WfxwCllxCheckBean> wfxws = wfre.getResult();
            MessageDao mdao = new MessageDao(self);
            for (WfxwCllxCheckBean w : wfxws) {
                mdao.delWfxwCllx(w.getWfxw());
                row += mdao.addWfxwCllx(w);
            }
            mdao.closeDb();
            GlobalMethod.writeInDisk(wfxwCllxFile.getAbsolutePath(),
                    str.getResult());
        }
        Log.e("UpdateInfoThread", "update wfxw cllx: " + row);

    }

    private void synJdsHmb(RestfulDao dao, String jh) {
        if(TextUtils.isEmpty(jh)){
            return;
        }
        WebQueryResult<List<THmb>> webHmbs = dao.hqVioWs(jh, "");
        if (!TextUtils.isEmpty(GlobalMethod.getErrorMessageFromWeb(webHmbs)))
            return;
        List<THmb> hmbs = webHmbs.getResult();
        if (hmbs == null || hmbs.isEmpty())
            return;
        String[] hds = {"1", "3", "9"};
        // 检查目前号码表是否在服务器获取的列表中，
        // 如果不在，则删除，不上交，由服务器决定是否已调动单位
        for (String hd : hds) {
            List<THmb> curJds = WsglDAO.getJdsListByHdzl(hd, jh,
                    self.getContentResolver());
            if (curJds == null || curJds.isEmpty())
                continue;
            for (THmb tHmb : curJds) {
                if (!checkHmbInWebHmbList(tHmb, hmbs)) {
                    WsglDAO.delHmbByHdid(tHmb.getHdid(),
                            self.getContentResolver());
                }
            }
        }
        // 与本地的号码表进行比对, 相同则不做更新
        for (THmb hmb : hmbs) {
            // 转换服务器种类为警务通种类
            try {
                Log.e("UpdateInfoThread", CommParserXml.objToXml(hmb));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String jwthd = GlobalConstant.hdzh.get(hmb.getHdzl());
            if (TextUtils.equals("009", jwthd))
                jwthd = "9";
            Log.e("UpdateInfoThread", "jwthd " + jwthd);
            hmb.setHdzl(jwthd);
            THmb curHmb = WsglDAO.getHmbByHdId(hmb.getHdid(), hmb.getHdzl(),
                    self.getContentResolver());
            if (curHmb == null) {
                // 该民警还未获取法律文书，直接将获取的文书写入到表中
                WsglDAO.saveHmb(hmb, self.getContentResolver());
                Log.e("UpdateInfoThread", "cyrHmb is null and save. " + jwthd);
            } else {
                long serverDqhm = Long.valueOf(hmb.getDqhm());
                long clientDqhm = Long
                        .valueOf(curHmb.getDqhm());
                if (serverDqhm > clientDqhm) {
                    // 服务器大则更新本地，
                    WsglDAO.saveHmb(hmb, self.getContentResolver());
                    Log.e("UpdateInfoThread", "服务器大则更新本地. " + jwthd);
                } else if (serverDqhm < clientDqhm) {
                    // 服务器小则更新服务器，当前值减一
                    curHmb.setDqhm((Long.valueOf(curHmb.getDqhm()) - 1) + "");
                    Log.e("UpdateInfoThread", "服务器小则更新服务器，当前值减一. " + jwthd);
                    dao.synVioWs(curHmb, jh);
                } else {
                    // 全部相符合，不更新本地的数据
                    Log.e("UpdateInfoThread", "全部相符合，不更新本地的数据. " + jwthd);
                }
            }
        }

    }

    private boolean checkHmbInWebHmbList(THmb tHmb, List<THmb> webHmbs) {
        for (THmb h : webHmbs) {
            if (TextUtils.equals(tHmb.getHdid(), h.getHdid()))
                return true;
        }
        return false;
    }

}
