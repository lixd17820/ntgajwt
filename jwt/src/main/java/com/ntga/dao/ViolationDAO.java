package com.ntga.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.provider.fixcode.Fixcode;
import com.android.provider.flashcode.Flashcode;
import com.android.provider.userdata.Userdata;
import com.android.provider.wfdmcode.Wfdmcode;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.VioViolation;
import com.ntga.bean.WebQueryResult;
import com.ntga.bean.WfdmBean;
import com.ntga.bean.WfxwForceBean;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ViolationDAO {

    public static ArrayList<KeyValueBean> getAllFrmCode(String lb,
                                                        ContentResolver resolver, String[] select, String where,
                                                        String order) {
        ArrayList<KeyValueBean> list = new ArrayList<KeyValueBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.fixcode/item/" + lb);
        Cursor cs = resolver.query(CONTENT_URI, select, where, null, order);
        if (cs.moveToFirst()) {
            do {
                list.add(new KeyValueBean(cs.getString(0), cs.getString(1)));
            } while (cs.moveToNext());
        }
        cs.close();
        return list;
    }

    /**
     * 根据发证机关代码查询城市车管所
     *
     * @param fzjg
     * @param resolver
     * @return
     */
    public static String getFzjgChinaName(String fzjg, ContentResolver resolver) {
        String name = "无";
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.fixcode/item/"
                        + GlobalConstant.CHENSHI);
        String where = Fixcode.FrmCode.DMZ + "='" + fzjg + "'";
        String[] select = new String[]{Fixcode.FrmCode.DMSM1};
        Cursor cs = resolver.query(CONTENT_URI, select, where, null, null);
        if (cs.moveToFirst()) {
            do {
                name = cs.getString(0);
            } while (cs.moveToNext());
        }
        cs.close();
        return name;
    }

    public static List<KeyValueBean> getPartFrmCodeByWhere(String lb,
                                                           ContentResolver resolver, String[] select, String where) {
        List<KeyValueBean> list = new ArrayList<KeyValueBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.fixcode/item/" + lb);
        Cursor cs = resolver.query(CONTENT_URI, select, where, null, null);
        if (cs.moveToFirst()) {
            do {
                list.add(new KeyValueBean(cs.getString(0), cs.getString(1)));
            } while (cs.moveToNext());
        }
        cs.close();
        return list;
    }

    /**
     * 根据违法代码查询相关依据和处理信息
     *
     * @param wfdm
     * @param resolver
     * @return WfdmBean
     */
    public static WfdmBean queryWfxwByWfdm(String wfdm, ContentResolver resolver) {
        WfdmBean wfxw = null;
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.wfdmcode/item/" + wfdm);
        Cursor cs = resolver.query(CONTENT_URI, null, null, null, null);
        if (cs.moveToFirst()) {
            wfxw = new WfdmBean();
            saveWfdmFromCursor(wfxw, cs);
        }
        cs.close();
        return wfxw;
    }

    /**
     * 验证违法代码和人员分类逻辑关系
     *
     * @param wfdm
     * @param ryfl
     * @param resolver
     * @return
     */
    public static boolean checkWfxwAndRyfl(String wfdm, int ryfl,
                                           ContentResolver resolver) {
        WfdmBean wf = queryWfxwByWfdm(wfdm, resolver);
        if (wf != null) {
            int wfzl = wf.getDmzl();
            // 人员分类是行人和乘车人
            if (ryfl == 1 && wfzl == GlobalConstant.WFDMZL_XRCCR) {
                return true;
            } else if (ryfl == 2 && wfzl == GlobalConstant.WFDMZL_FJDC) {
                return true;
            } else if ((ryfl == 4 || ryfl == 3 || ryfl == 7)
                    && (wfzl == GlobalConstant.WFDMZL_JDC || wfzl == GlobalConstant.WFDMZL_OTHER)) {
                return true;
            } else if (wfzl == GlobalConstant.WFDMZL_SG) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询强制措施依据
     *
     * @param wfdm
     * @param resolver
     * @return
     */
    public static String queryQzcsYj(String wfdm, ContentResolver resolver) {
        String qzyj = "";
        if (TextUtils.isEmpty(wfdm))
            return qzyj;
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.wfdmcode/qzyj/" + wfdm);
        // String where = Wfdmcode.VioCodeWfdm.CSBJ + "='1' ";
        Cursor cs = resolver.query(CONTENT_URI,
                new String[]{Wfdmcode.ForceCode.QZYJ}, null, null, null);
        if (cs.moveToFirst()) {
            qzyj = cs.getString(0);
        }
        cs.close();
        return qzyj;
    }

    /**
     * 根据条件查询强制措施依据
     *
     * @param cond
     * @param resolver
     * @return
     */
    public static List<WfxwForceBean> queryQzcsByCond(String cond,
                                                      ContentResolver resolver) {
        List<WfxwForceBean> list = new ArrayList<WfxwForceBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.wfdmcode/force/");
        // String where = Wfdmcode.VioCodeWfdm.CSBJ + "='1' ";
        Cursor cs = resolver.query(CONTENT_URI, null, cond, null, null);
        if (cs.moveToFirst()) {
            do {
                WfxwForceBean force = new WfxwForceBean();
                force.setWfdm(cs.getString(0));
                force.setWfxw(cs.getString(1));
                force.setQzyj(cs.getString(2));
                force.setZt(cs.getString(3));
                force.setGxsj(cs.getString(4));
                force.setBz(cs.getString(5));
                list.add(force);
            } while (cs.moveToNext());
        }
        cs.close();
        return list;
    }

    /**
     * 模糊查询违法代码
     *
     * @param where
     * @param resolver
     * @return
     */
    public static List<WfdmBean> queryWfxwByCondition(String where,
                                                      ContentResolver resolver) {
        List<WfdmBean> wfxws = new ArrayList<WfdmBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.wfdmcode/item/");
        Cursor cs = resolver.query(CONTENT_URI, null, where, null,
                Wfdmcode.VioCodeWfdm.WFXW);
        if (cs.moveToFirst()) {
            do {
                WfdmBean wfxw = new WfdmBean();
                saveWfdmFromCursor(wfxw, cs);
                wfxws.add(wfxw);
            } while (cs.moveToNext());
        }
        cs.close();
        return wfxws;
    }

    private static void saveWfdmFromCursor(WfdmBean wfxw, Cursor cs) {
        wfxw.setWfxw(cs.getString(0));
        wfxw.setDmzl(cs.getInt(1));
        wfxw.setDmfl(cs.getString(2));
        wfxw.setWfms(cs.getString(3));
        wfxw.setWfnr(cs.getString(4));
        wfxw.setWfgd(cs.getString(5));
        wfxw.setFltw(cs.getString(6));
        wfxw.setWfjfs(cs.getString(7));
        wfxw.setFkjeMin(cs.getString(8));
        wfxw.setFkjeMax(cs.getString(9));
        wfxw.setFkjeDut(cs.getString(10));
        wfxw.setZkysMin(cs.getString(11));
        wfxw.setZkysMax(cs.getString(12));
        wfxw.setZkysDut(cs.getString(13));
        wfxw.setJlsjMin(cs.getString(14));
        wfxw.setJlsjMax(cs.getString(15));
        wfxw.setJlsjDut(cs.getString(16));
        wfxw.setQzcslx(cs.getString(17));
        wfxw.setJgbj(cs.getString(18));
        wfxw.setFkbj(cs.getString(19));
        wfxw.setZkbj(cs.getString(20));
        wfxw.setDxbj(cs.getString(21));
        wfxw.setJlbj(cs.getString(22));
        wfxw.setCxvbj(cs.getString(23));
        wfxw.setCxdbj(cs.getString(24));
        wfxw.setGb(cs.getString(25));
        wfxw.setYxqs(cs.getString(26));
        wfxw.setYxqz(cs.getString(27));
        wfxw.setGlbm(cs.getString(28));
        wfxw.setJyw(cs.getString(29));
        wfxw.setCsbj(cs.getString(30));
    }

    /**
     * 保存当场处罚决定书
     *
     * @param v
     * @param resolver
     * @return
     */
    public static Uri saveViolationIntoDB(VioViolation v,
                                          ContentResolver resolver) {
        Uri url = Uri
                .parse("content://com.android.provider.flashcode/insertviolation");
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.VioViolation.JDSBH, v.getJdsbh());
        cv.put(Flashcode.VioViolation.WSLB, v.getWslb());
        cv.put(Flashcode.VioViolation.RYFL, v.getRyfl());
        cv.put(Flashcode.VioViolation.JSZH, v.getJszh());
        cv.put(Flashcode.VioViolation.DABH, v.getDabh());
        cv.put(Flashcode.VioViolation.FZJG, v.getFzjg());
        cv.put(Flashcode.VioViolation.ZJCX, v.getZjcx());
        cv.put(Flashcode.VioViolation.DSR, v.getDsr());
        cv.put(Flashcode.VioViolation.ZSXZQH, v.getZsxzqh());
        cv.put(Flashcode.VioViolation.ZSXXDZ, v.getZsxxdz());
        cv.put(Flashcode.VioViolation.DH, v.getDh());
        cv.put(Flashcode.VioViolation.LXFS, v.getLxfs());
        cv.put(Flashcode.VioViolation.CLFL, v.getClfl());
        cv.put(Flashcode.VioViolation.HPZL, v.getHpzl());
        cv.put(Flashcode.VioViolation.HPHM, v.getHphm());
        cv.put(Flashcode.VioViolation.JTFS, v.getJtfs());
        cv.put(Flashcode.VioViolation.WFSJ, v.getWfsj());
        cv.put(Flashcode.VioViolation.WFDD, v.getWfdd());
        cv.put(Flashcode.VioViolation.WFDZ, v.getWfdz());
        cv.put(Flashcode.VioViolation.WFXW1, v.getWfxw1());
        cv.put(Flashcode.VioViolation.WFXW2, v.getWfxw2());
        cv.put(Flashcode.VioViolation.WFXW3, v.getWfxw3());
        cv.put(Flashcode.VioViolation.WFXW4, v.getWfxw4());
        cv.put(Flashcode.VioViolation.WFXW5, v.getWfxw5());
        cv.put(Flashcode.VioViolation.WFJFS, v.getWfjfs());
        cv.put(Flashcode.VioViolation.FKJE, v.getFkje());
        cv.put(Flashcode.VioViolation.ZQMJ, v.getZqmj());
        cv.put(Flashcode.VioViolation.JKFS, v.getJkfs());
        cv.put(Flashcode.VioViolation.FXJG, v.getFxjg());
        cv.put(Flashcode.VioViolation.CFZL, v.getCfzl());
        cv.put(Flashcode.VioViolation.JKBJ, v.getJkbj());
        cv.put(Flashcode.VioViolation.JKRQ, v.getJkrq());
        cv.put(Flashcode.VioViolation.JSJQBJ, v.getJsjqbj());
        cv.put(Flashcode.VioViolation.QZCSLX, v.getQzcslx());
        cv.put(Flashcode.VioViolation.GXSJ, v.getGxsj());
        cv.put(Flashcode.VioViolation.CLSJ, v.getClsj());
        cv.put(Flashcode.VioViolation.SJXM, v.getSjxm());
        cv.put(Flashcode.VioViolation.SJXMMC, v.getSjxmmc());
        cv.put(Flashcode.VioViolation.KLWPCFD, v.getKlwpcfd());
        cv.put(Flashcode.VioViolation.SJWPCFD, v.getSjwpcfd());
        cv.put(Flashcode.VioViolation.SCBJ, v.getScbj());
        cv.put(Flashcode.VioViolation.CWXX, v.getCwxx());
        cv.put(Flashcode.VioViolation.GZXM, v.getGzxm());
        cv.put(Flashcode.VioViolation.GZXMMC, v.getGzxmmc());
        cv.put(Flashcode.VioViolation.HDID, v.getHdid());
        cv.put(Flashcode.VioViolation.BZZ, v.getBzz());
        cv.put(Flashcode.VioViolation.SCZ, v.getScz());
        Uri row = resolver.insert(url, cv);
        return row;
    }

    public static int delViolationFromDB(VioViolation vio,
                                         ContentResolver resolver) {
        Uri url = Uri
                .parse("content://com.android.provider.flashcode/delviolation");
        String where = Flashcode.VioViolation.ID + "=" + vio.getId();
        int row = resolver.delete(url, where, null);
        return row;
    }

    /**
     * 从本地存中查询违法决定书列表
     *
     * @return
     */
    public static List<VioViolation> getViolations(ContentResolver resolver) {
        List<VioViolation> list = new ArrayList<VioViolation>();
        Uri url = Uri
                .parse("content://com.android.provider.flashcode/queryviolation");
        String where = Flashcode.VioViolation.ZQMJ + "='"
                + GlobalData.grxx.get(GlobalConstant.YHBH) + "'";
        Cursor cs = resolver.query(url, null, where, null,
                Flashcode.VioViolation.ID + " desc");
        if (cs.moveToFirst()) {
            do {
                VioViolation v = new VioViolation();
                insertVio(v, cs);
                list.add(v);
            } while (cs.moveToNext());
        }

        cs.close();
        return list;
    }

    /**
     * 根据决定书编号取得决定书
     *
     * @param jdsbh
     * @param resolver
     * @return
     */
    public static VioViolation getViolationByJdsbh(String jdsbh,
                                                   ContentResolver resolver) {
        VioViolation v = null;
        Uri url = Uri
                .parse("content://com.android.provider.flashcode/queryviolation");
        String where = Flashcode.VioViolation.JDSBH + "='" + jdsbh + "'";
        Cursor cs = resolver.query(url, null, where, null, null);
        if (cs.moveToFirst()) {
            v = new VioViolation();
            do {
                insertVio(v, cs);
            } while (cs.moveToNext());
        }

        cs.close();
        return v;
    }

    /**
     * 根据条件查询决定书
     *
     * @param where
     * @param resolver
     * @return
     */
    public static List<VioViolation> getViolationByConds(String where,
                                                         ContentResolver resolver) {
        List<VioViolation> list = new ArrayList<VioViolation>();
        Uri url = Uri
                .parse("content://com.android.provider.flashcode/queryviolation");
        Cursor cs = resolver.query(url, null, where, null,
                Flashcode.VioViolation.ID + " desc");
        if (cs != null) {
            if (cs.moveToFirst()) {
                do {
                    VioViolation v = new VioViolation();
                    insertVio(v, cs);
                    list.add(v);
                } while (cs.moveToNext());
            }
            cs.close();
        }
        return list;
    }

    /**
     * 处罚决定书是否重复，条件：同一时间，同一地点，同一违法行为，同一号牌，同一人员
     *
     * @param v
     * @param resolver
     * @return
     */
    public static boolean isViolationDuplicate(VioViolation v,
                                               ContentResolver resolver) {
        String where = Flashcode.VioViolation.WFSJ + "='" + v.getWfsj()
                + "' and ";
        where += Flashcode.VioViolation.WFDD + "='" + v.getWfdd() + "' and ";
        where += Flashcode.VioViolation.WFXW1 + "='" + v.getWfxw1() + "' and ";
        where += Flashcode.VioViolation.HPZL + "='" + v.getHpzl() + "' and ";
        where += Flashcode.VioViolation.HPHM + "='" + v.getHphm() + "' and ";
        where += Flashcode.VioViolation.JSZH + "='" + v.getJszh() + "'";
        List<VioViolation> list = getViolationByConds(where, resolver);

        return list != null && !list.isEmpty();
    }

    /**
     * 将游标数据转换为对象
     *
     * @param v
     * @param cs
     */
    private static void insertVio(VioViolation v, Cursor cs) {
        v.setId(cs.getString(0));
        v.setJdsbh(cs.getString(1));
        v.setWslb(cs.getString(2));
        v.setRyfl(cs.getString(3));
        v.setJszh(cs.getString(4));
        v.setDabh(cs.getString(5));
        v.setFzjg(cs.getString(6));
        v.setZjcx(cs.getString(7));
        v.setDsr(cs.getString(8));
        v.setZsxzqh(cs.getString(9));
        v.setZsxxdz(cs.getString(10));
        v.setDh(cs.getString(11));
        v.setLxfs(cs.getString(12));
        v.setClfl(cs.getString(13));
        v.setHpzl(cs.getString(14));
        v.setHphm(cs.getString(15));
        v.setJtfs(cs.getString(16));
        v.setWfsj(cs.getString(17));
        v.setWfdd(cs.getString(18));
        v.setWfdz(cs.getString(19));
        v.setWfxw1(cs.getString(20));
        v.setWfxw2(cs.getString(21));
        v.setWfxw3(cs.getString(22));
        v.setWfxw4(cs.getString(23));
        v.setWfxw5(cs.getString(24));
        v.setWfjfs(cs.getString(25));
        v.setFkje(cs.getString(26));
        v.setZqmj(cs.getString(27));
        v.setJkfs(cs.getString(28));
        v.setFxjg(cs.getString(29));
        v.setCfzl(cs.getString(30));
        v.setJkbj(cs.getString(31));
        v.setJkrq(cs.getString(32));
        v.setJsjqbj(cs.getString(33));
        v.setQzcslx(cs.getString(34));
        v.setGxsj(cs.getString(35));
        v.setClsj(cs.getString(36));
        v.setSjxm(cs.getString(37));
        v.setSjxmmc(cs.getString(38));
        v.setKlwpcfd(cs.getString(39));
        v.setSjwpcfd(cs.getString(40));
        v.setScbj(cs.getString(41));
        v.setCwxx(cs.getString(42));
        v.setGzxm(cs.getString(43));
        v.setGzxmmc(cs.getString(44));
        v.setHdid(cs.getString(45));
        v.setBzz(cs.getString(46));
        v.setScz(cs.getString(47));
    }

    /**
     * 从数据库中查找民警个人信息以及默认打
     *
     * @return
     */
    public static HashMap<String, String> getMjgrxx(ContentResolver resolver) {
        HashMap<String, String> grxx = new HashMap<String, String>();
        Uri CONTENT_URI = Uri.parse("content://" + Userdata.AUTHORITY
                + "/querysyscode");
        Cursor cs = resolver.query(CONTENT_URI, null, null, null, null);
        if (cs != null) {
            if (cs.moveToFirst()) {
                do {
                    grxx.put(cs.getString(0), cs.getString(1));
                } while (cs.moveToNext());
            }
            cs.close();
        }
        return grxx;
    }

    /**
     * 从系统数据库获取登录时从服务器传来的数据
     *
     * @param resolver
     * @return
     */
    public static HashMap<String, String> getLoginInfo(ContentResolver resolver) {
        HashMap<String, String> map = new HashMap<String, String>();
        //Uri CONTENT_URI = Uri
         //       .parse("content://com.google.provider.SysData/querySys");
        Uri url = Uri.parse("content://" + Userdata.AUTHORITY
                + "/querysyscode");
        Cursor c = resolver.query(url, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    map.put(c.getString(0), c.getString(1));
                    Log.e(c.getString(0), c.getString(1));
                } while (c.moveToNext());
                c.close();
            }
        }
        return map;
    }

    /**
     * 查询存在本地的警员编号
     *
     * @param resolver
     * @return
     */
    public static String getMjgrxxJh(ContentResolver resolver) {
        String jybh = "3206";
        Uri CONTENT_URI = Uri.parse("content://" + Userdata.AUTHORITY
                + "/querysyscode");
        String where = Userdata.SysCode.CODE_NAME + "='" + GlobalConstant.YHBH
                + "'";
        Cursor cs = resolver.query(CONTENT_URI, null, where, null, null);
        if (cs.moveToFirst()) {
            do {
                jybh = cs.getString(1);
            } while (cs.moveToNext());
        }
        cs.close();
        return jybh;
    }

    /**
     * 更新或加入民警个人信息
     *
     * @param grxx
     * @param resolver
     */
    public static void saveMjgrxxIntoDB(Map<String, String> grxx,
                                        ContentResolver resolver) {
        Uri url = Uri.parse("content://" + Userdata.AUTHORITY
                + "/updatesyscode");
        Set<Entry<String, String>> set = grxx.entrySet();
        for (Entry<String, String> entry : set) {
            ContentValues cv = new ContentValues();
            cv.put(Userdata.SysCode.CODE_NAME, entry.getKey());
            cv.put(Userdata.SysCode.CODE_VALUE, entry.getValue());
            resolver.update(url, cv, null, null);
        }
    }

    /**
     * 加入或更新GPS上传频率
     *
     * @param freq
     * @param resolver
     */
    public static void saveGpsUploadFreq(int freq, ContentResolver resolver) {
        Uri url = Uri.parse("content://" + Userdata.AUTHORITY
                + "/updatesyscode");
        ContentValues cv = new ContentValues();
        cv.put(Userdata.SysCode.CODE_NAME, GlobalConstant.GPS_FREQ);
        cv.put(Userdata.SysCode.CODE_VALUE, freq);
        resolver.update(url, cv, null, null);
    }

    /**
     * 查询GPS发送频率
     *
     * @param resolver
     * @return
     */
    public static int queryGpsUploadFreq(ContentResolver resolver) {
        int freq = -1;
        Uri uri = Uri
                .parse("content://" + Userdata.AUTHORITY + "/querysyscode");
        Cursor c = resolver.query(uri,
                new String[]{Userdata.SysCode.CODE_VALUE},
                Userdata.SysCode.CODE_NAME + "=?",
                new String[]{GlobalConstant.GPS_FREQ}, null);
        if (c.moveToFirst()) {
            freq = c.getInt(0);
        }
        c.close();
        return freq;
    }

    /**
     * 根据数据库中强制措施代码成功字符串
     *
     * @param qzcslx
     * @return
     */
    public static String getQzcslxMs(String qzcslx) {
        String lx = "";
        if (!TextUtils.isEmpty(qzcslx)) {
            for (int i = 0; i < qzcslx.length(); i++) {
                String key = qzcslx.substring(i, i + 1);
                lx += GlobalMethod.getStringFromKVListByKey(
                        GlobalData.qzcslxList, key) + ", ";
            }
        }
        return lx;
    }

    /**
     * 检验是否为有效期内的违法代码
     *
     * @param wfdm
     * @return
     */
    public static boolean isYxWfdm(WfdmBean wfdm) {
        boolean res = true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date yxqs = sdf.parse(wfdm.getYxqs());
            Date yxqz = sdf.parse(wfdm.getYxqz());
            Date now = new Date();
            if (now.getTime() < yxqs.getTime()
                    || now.getTime() > yxqz.getTime())
                res = false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 取得所有决定书
     *
     * @param contentResolver
     * @return
     */
    public static List<VioViolation> getUnloadViolations(
            ContentResolver contentResolver) {
        List<VioViolation> list = new ArrayList<VioViolation>();
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY
                + "/queryviolation");
        String where = Flashcode.VioViolation.SCBJ + "='0' and "
                + Flashcode.VioViolation.ZQMJ + "='"
                + GlobalData.grxx.get(GlobalConstant.YHBH) + "'";
        Cursor cs = contentResolver.query(uri, null, where, null, null);
        if (cs.moveToFirst()) {
            do {
                VioViolation v = new VioViolation();
                insertVio(v, cs);
                list.add(v);
            } while (cs.moveToNext());
        }

        cs.close();
        return list;
    }

    public static WebQueryResult<ZapcReturn> uploadViolation(
            VioViolation vioViolation) {
        RestfulDao dao = RestfulDaoFactory.getDao();
        WebQueryResult<ZapcReturn> re = dao.uploadViolation(vioViolation);
        return re;
    }

    public static int uploadViolationSaveIt(
            VioViolation vioViolation, Context context, ConnCata conn) {
        RestfulDao dao = RestfulDaoFactory.getDao(conn);
        WebQueryResult<ZapcReturn> rs = dao.uploadViolation(vioViolation);
        String err = GlobalMethod.getErrorMessageFromWeb(rs);
        if (TextUtils.isEmpty(err)) {
            ZapcReturn upRe = rs.getResult();
            if (upRe != null && TextUtils.equals(upRe.getCgbj(), "1")
                    && upRe.getPcbh() != null && upRe.getPcbh().length > 0) {
                ViolationDAO.setVioUploadStatus(upRe.getPcbh()[0], true,
                        context.getContentResolver());
                return 1;
            }
        }
        return 0;
    }

    /**
     * 将违法记录打上是否上传的标记
     *
     * @param jdsbh
     * @param isOk
     * @param resolver
     * @return
     */
    public static int setVioUploadStatus(String jdsbh, boolean isOk,
                                         ContentResolver resolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY
                + "/updateviolation");
        String where = Flashcode.VioViolation.JDSBH + "='" + jdsbh + "'";
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.VioViolation.SCBJ, isOk ? "1" : "0");
        int row = resolver.update(uri, cv, where, null);
        return row;
    }

    /**
     * 删除过多的决定书
     *
     * @param row      保留的行数
     * @param resolver
     */
    public static void delOldViolation(int row, ContentResolver resolver) {
        // 测试时可以删除未上传的,发布时加上
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWDELSQL);
        String sql = "DELETE FROM VIO_VIOLATION WHERE id<=(SELECT max(id)-"
                + row + " from VIO_VIOLATION) ";
        // + " AND scbj=1"
        resolver.delete(uri, sql, null);
    }

    /**
     * 取最条一张决定书的违法地点，用于下一个处罚
     *
     * @param resolver
     * @return
     */
    public static KeyValueBean getLastVioWfdd(ContentResolver resolver) {
        KeyValueBean kv = new KeyValueBean("", "");
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWQUERY);
        String sql = "SELECT WFDD,WFDZ FROM VIO_VIOLATION WHERE id=(SELECT max(id) FROM VIO_VIOLATION)";
        Cursor cs = resolver.query(uri, null, sql, null, null);
        if (cs.moveToFirst()) {
            kv.setKey(cs.getString(cs.getColumnIndex("WFDD")));
            kv.setValue(cs.getString(cs.getColumnIndex("WFDZ")));
        }
        cs.close();
        return kv;
    }

    /**
     * 删除错误的警告数据，条件文书类别为0，处罚种类为1
     *
     * @param resolver
     */
    public static void delErrorJwjg(ContentResolver resolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWDELSQL);
        String sql = "DELETE FROM VIO_VIOLATION WHERE "
                + Flashcode.VioViolation.WSLB + "=0 and "
                + Flashcode.VioViolation.CFZL + "=1";
        resolver.delete(uri, sql, null);
    }

    public static int uploadViolationRkxx(String jdsbh, String cwms,
                                          ContentResolver resolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY
                + "/updateviolation");
        String where = Flashcode.VioViolation.JDSBH + "='" + jdsbh + "'";
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.VioViolation.CWXX, cwms);
        int row = resolver.update(uri, cv, where, null);
        return row;

    }

    public static String saveIntoJsonStr(String... s) {
        if (s == null || s.length == 0 || s.length % 2 != 0) return null;
        JSONObject obj = new JSONObject();
        for (int i = 0; i < s.length / 2; i++) {
            try {
                if (!TextUtils.isEmpty(s[i * 2 + 1])) {
                    obj.put(s[i * 2], s[i * 2 + 1]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return obj.toString();
    }

    /**
     * 获取JSON中的数据
     *
     * @param s
     * @return
     */
    public static String[] getJsonStrs(String... s) {
        if (s == null || s.length < 2)
            return null;
        String[] re = new String[s.length - 1];
        try {
            JSONObject obj = new JSONObject(s[0]);
            for (int i = 0; i < re.length; i++) {
                re[i] = obj.optString(s[i + 1], "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            for (int i = 0; i < re.length; i++) {
                re[i] = "";
            }
        }

        return re;
    }

}
