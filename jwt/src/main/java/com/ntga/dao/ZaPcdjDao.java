package com.ntga.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.android.provider.fixcode.Fixcode;
import com.android.provider.flashcode.Flashcode;
import com.android.provider.roadcode.Roadcode;
import com.ntga.bean.KeyValueBean;
import com.ntga.database.MessageDao;
import com.ntga.zapc.ZapcGzxxBean;
import com.ntga.zapc.ZapcRyjbxxBean;
import com.ntga.zapc.ZapcRypcxxBean;
import com.ntga.zapc.ZapcWppcxxBean;
import com.ntga.zapc.Zapcxx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ZaPcdjDao {

    /**
     * 治安盘查所有字典表的集合
     */
    public static Map<String, ArrayList<KeyValueBean>> zapcDic;
    /**
     * 治安盘查中字符常量的列表
     */
    public static List<String> zapcZfclb;

    // 治安盘查中用到的字符常量
    public static String XFFS = "XFFS";
    /**
     * 盘查原因
     */
    public static String PCYY = "PCYY";
    public static String XB = "XB";
    public static String WHCD = "WHCD";
    public static String MZ = "MZ";
    public static String BYZK = "BYZK";
    public static String HYZK = "HYZK";
    public static String ZJXY = "ZJXY";
    public static String SF = "SF";
    public static String JCCFX = "JCCFX";
    public static String PCBDJG = "PCBDJG";
    public static String PCCLJG = "PCCLJG";
    // public static String WPCLCS = "WPCLCS";

    /**
     * 大平台日期格式
     */
    public static SimpleDateFormat sdfDpt = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    /**
     * 普通格式转换 *
     */
    public static SimpleDateFormat sdfNor = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /**
     * 线路的字段数组
     */
    public static String[] select = new String[]{Fixcode.frmDptCode.DMZ,
            Fixcode.frmDptCode.DMSM1};
    public static String[] lxxxSel = new String[]{Roadcode.ZapcLxxx.XLDM,
            Roadcode.ZapcLxxx.XLXL};

    /**
     * 获取所有大平台的字典表
     *
     * @param lb
     * @param resolver
     * @param select
     * @param where
     * @param order
     * @return
     */
    private static ArrayList<KeyValueBean> getAllDptCode(String lb,
                                                         ContentResolver resolver, String[] select, String where,
                                                         String order) {
        ArrayList<KeyValueBean> list = new ArrayList<KeyValueBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.fixcode/dpt");
        where = Fixcode.frmDptCode.DMLB + "='" + lb + "'";
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
     * 初始化治安盘查字典表方法
     *
     * @param resolver
     */
    public static void initZapcData(ContentResolver resolver) {
        zapcDic = new HashMap<String, ArrayList<KeyValueBean>>();
        zapcDic.put(XFFS, getAllDptCode(XFFS, resolver, select, null, "dmz"));
        zapcDic.put(PCYY, getAllDptCode(PCYY, resolver, select, null, "dmz"));
        zapcDic.put(XB, getAllDptCode(XB, resolver, select, null, "dmz"));
        zapcDic.put(WHCD, getAllDptCode(WHCD, resolver, select, null, "dmz"));
        zapcDic.put(MZ, getAllDptCode(MZ, resolver, select, null, "dmz"));
        zapcDic.put(BYZK, getAllDptCode(BYZK, resolver, select, null, "dmz"));
        zapcDic.put(HYZK, getAllDptCode(HYZK, resolver, select, null, "dmz"));
        zapcDic.put(ZJXY, getAllDptCode(ZJXY, resolver, select, null, "dmz"));
        zapcDic.put(SF, getAllDptCode(SF, resolver, select, null, "dmz"));
        zapcDic.put(JCCFX, getAllDptCode(JCCFX, resolver, select, null, "dmz"));
        zapcDic.put(PCBDJG,
                getAllDptCode(PCBDJG, resolver, select, null, "dmz"));
        zapcDic.put(PCCLJG,
                getAllDptCode(PCCLJG, resolver, select, null, "dmz"));
        // zapcDic.put(WPCLCS,
        // getAllDptCode(WPCLCS, resolver, select, null, "dmz"));
        KeyValueBean nullKv = new KeyValueBean("", "");
        Set<Entry<String, ArrayList<KeyValueBean>>> set = zapcDic.entrySet();
        for (Entry<String, ArrayList<KeyValueBean>> entry : set) {
            entry.getValue().add(0, nullKv);
        }
    }

    /**
     * 获取路线信息
     *
     * @param dwdm
     * @param resolver
     * @return
     */
    public static ArrayList<KeyValueBean> getZapcLxxx(String dwdm,
                                                      ContentResolver resolver) {
        ArrayList<KeyValueBean> list = new ArrayList<KeyValueBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.roadcode/queryLxxx");
        String where = Roadcode.ZapcLxxx.TRFFBH + "='" + dwdm + "'";
        Cursor cs = resolver.query(CONTENT_URI, lxxxSel, where, null,
                Roadcode.ZapcLxxx.XLDM);
        if (cs.moveToFirst()) {
            do {
                list.add(new KeyValueBean(cs.getString(0), cs.getString(1)));
            } while (cs.moveToNext());
        }
        cs.close();
        return list;

    }

    /**
     * 查询工作信息
     *
     * @param where
     * @param resolver
     * @return
     */
    public static List<ZapcGzxxBean> getZapcGzxx(String where,
                                                 ContentResolver resolver) {
        List<ZapcGzxxBean> list = new ArrayList<ZapcGzxxBean>();
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.QUERY_GZXX_INFO);
        Cursor cs = resolver.query(CONTENT_URI, null, where, null,
                Flashcode.ZapcGzxx.ID + " desc");
        if (cs.moveToFirst()) {
            do {
                ZapcGzxxBean gzxx = new ZapcGzxxBean();
                gzxx.setId(ifNull(cs.getString(0)));
                gzxx.setGzxxbh(ifNull(cs.getString(1)));
                gzxx.setXffs(ifNull(cs.getString(2)));
                gzxx.setDjdw(ifNull(cs.getString(3)));
                gzxx.setJybh(ifNull(cs.getString(4)));
                gzxx.setXlmc(ifNull(cs.getString(5)));
                gzxx.setGzdd(ifNull(cs.getString(6)));
                gzxx.setFjrs(ifNull(cs.getString(7)));
                gzxx.setKssj(ifNull(cs.getString(8)));
                gzxx.setJssj(ifNull(cs.getString(9)));
                gzxx.setCsbj(ifNull(cs.getString(10)));
                gzxx.setZqmj(ifNull(cs.getString(11)));
                list.add(gzxx);
            } while (cs.moveToNext());
        }
        cs.close();
        return list;
    }

    /**
     * 删除一条工作信息
     *
     * @param gz
     * @param resolver
     * @return
     */
    public static int deleteGzxx(ZapcGzxxBean gz, ContentResolver resolver) {
        String where = Flashcode.ZapcGzxx.ID + "='" + gz.getId() + "'";
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.DELETE_GZXX);
        Uri rawDel = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWDELSQL);
        int row = resolver.delete(CONTENT_URI, where, null);
        resolver.delete(
                rawDel,
                "DELETE FROM zapc_jbryxx  WHERE rybh IN (SELECT pcrybh FROM zapc_pcryxx WHERE gzbh='"
                        + gz.getGzxxbh() + "')", null);
        resolver.delete(rawDel,
                "delete from zapc_pcryxx where gzbh='" + gz.getGzxxbh() + "'",
                null);
        return row;
    }

    /**
     * 删除已上传的工作信息
     *
     * @param resolver
     * @return
     */
    public static int deleteSendGzxx(ContentResolver resolver) {
        String where = "delete from " + Flashcode.ZapcGzxx.TABLE_NAME
                + " where " + Flashcode.ZapcGzxx.CSBJ + "='1'";
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWDELSQL);
        int row = resolver.delete(CONTENT_URI, where, null);
        return row;
    }

    /**
     * 取已保存工作信息的当前最大编号
     *
     * @param resolver
     * @return
     */
    public static int getMaxGzxxId(ContentResolver resolver) {
        int id = 0;
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWQUERY);
        String where = "SELECT seq FROM sqlite_sequence WHERE name='"
                + Flashcode.ZapcGzxx.TABLE_NAME + "'";
        Cursor cs = resolver.query(CONTENT_URI, null, where, null, null);
        if (cs.moveToFirst()) {
            id = cs.getInt(0);
        }
        cs.close();
        return id;
    }

    /**
     * 取物品信息下一个编号
     *
     * @param resolver
     * @return
     */
    public static int getMaxWpxxId(ContentResolver resolver) {
        int id = 0;
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.QUERY_WPXX_MAXID);
        Cursor cs = resolver.query(CONTENT_URI, null, null, null, null);
        if (cs.moveToFirst()) {
            id = cs.getInt(0);
        }
        cs.close();
        return id;
    }

    /**
     * 取人员信息的下一编号，基本信息和盘查信息共有一个编号
     *
     * @param resolver
     * @return
     */
    public static int getMaxRyxxId(ContentResolver resolver) {
        int id = 0;
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWQUERY);
        String where = "SELECT seq FROM sqlite_sequence WHERE name='"
                + Flashcode.ZapcPcryxx.TABLE_NAME + "'";
        Cursor cs = resolver.query(CONTENT_URI, null, where, null, null);
        if (cs.moveToFirst()) {
            id = cs.getInt(0);
        }
        cs.close();
        return id;
    }

    public static String ifNull(String s) {
        String n = s;
        if (TextUtils.isEmpty(s)) {
            n = "";
        }
        return n;
    }

    /**
     * 保存工作信息
     *
     * @param gzxx
     * @param contentResolver
     */
    public static void insertGzxx(ZapcGzxxBean gzxx,
                                  ContentResolver contentResolver) {
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.INSERT_GZXX);
        contentResolver.insert(CONTENT_URI, getValues(gzxx));
    }

    /**
     * 保存物品盘查信息
     *
     * @param wpxx
     * @param contentResolver
     */
    public static void insertWpxx(ZapcWppcxxBean wpxx,
                                  ContentResolver contentResolver, Context context) {
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.INSERT_WPXX);
        Uri sid = contentResolver.insert(CONTENT_URI, getValues(wpxx));
        String id = sid.getLastPathSegment();
        MessageDao dao = new MessageDao(context);
        dao.insertZapcWpxxAdd(Integer.valueOf(id), wpxx.getClpp(),
                wpxx.getSyr(), wpxx.getSfzmhm());
        dao.closeDb();
    }

    /**
     * 更新工作信息
     *
     * @param gzxx
     * @param contentResolver
     */
    public static void updateGzxx(ZapcGzxxBean gzxx,
                                  ContentResolver contentResolver) {
        String where = Flashcode.ZapcGzxx.ID + "=" + gzxx.getId();
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.UPDATE_GZXX);

        contentResolver.update(CONTENT_URI, getValues(gzxx), where, null);
    }

    /**
     * 结束一个工作信息
     *
     * @param gzId
     * @param contentResolver
     */
    public static void jsGzxx(String gzId, ContentResolver contentResolver) {
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.UPDATE_GZXX);
        String jssj = sdfDpt.format(new Date());
        String where = Flashcode.ZapcGzxx.ID + "=" + gzId;
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.ZapcGzxx.JSSJ, jssj);
        contentResolver.update(CONTENT_URI, cv, where, null);
    }

    public static void updateRyWpGzxxbh(ZapcGzxxBean gzxx,
                                        ContentResolver contentResolver) {
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.UPDATE_PCRYXX);
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.ZapcPcryxx.GZBH, gzxx.getGzxxbh());
        String where = Flashcode.ZapcPcryxx.GZBH + "='" + gzxx.getId() + "'";
        contentResolver.update(CONTENT_URI, cv, where, null);
        CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.UPDATE_WPXX);
        cv = new ContentValues();
        cv.put(Flashcode.ZapcWpxx.BPCWPGZQKBH, gzxx.getGzxxbh());
        where = Flashcode.ZapcWpxx.BPCWPGZQKBH + "='" + gzxx.getId() + "'";
        contentResolver.update(CONTENT_URI, cv, where, null);
    }

    /**
     * 将工作信息转为数据值
     *
     * @param gzxx
     * @return
     */
    private static ContentValues getValues(ZapcGzxxBean gzxx) {
        ContentValues cv = new ContentValues();
        // cv.put(Flashcode.ZapcGzxx.ID, ifNull(gzxx.getId()));
        cv.put(Flashcode.ZapcGzxx.GZXXBH, ifNull(gzxx.getGzxxbh()));
        cv.put(Flashcode.ZapcGzxx.XFFS, ifNull(gzxx.getXffs()));
        cv.put(Flashcode.ZapcGzxx.DJDW, ifNull(gzxx.getDjdw()));
        cv.put(Flashcode.ZapcGzxx.JYBH, ifNull(gzxx.getJybh()));
        cv.put(Flashcode.ZapcGzxx.XLMC, ifNull(gzxx.getXlmc()));
        cv.put(Flashcode.ZapcGzxx.GZDD, ifNull(gzxx.getGzdd()));
        cv.put(Flashcode.ZapcGzxx.FJRS, ifNull(gzxx.getFjrs()));
        cv.put(Flashcode.ZapcGzxx.KSSJ, ifNull(gzxx.getKssj()));
        cv.put(Flashcode.ZapcGzxx.JSSJ, ifNull(gzxx.getJssj()));
        cv.put(Flashcode.ZapcGzxx.CSBJ, ifNull(gzxx.getCsbj()));
        cv.put(Flashcode.ZapcGzxx.ZQMJ, ifNull(gzxx.getZqmj()));
        return cv;
    }

    /**
     * 将盘查信息转为数据值
     *
     * @param wpxx
     * @return
     */
    private static ContentValues getValues(ZapcWppcxxBean wpxx) {
        ContentValues cv = new ContentValues();
        // cv.put(Flashcode.ZapcWpxx.XLPCWPBH, ifNull(wpxx.getXlpcwpbh()));
        cv.put(Flashcode.ZapcWpxx.BPCWPGZQKBH, ifNull(wpxx.getBpcwpgzqkbh()));
        cv.put(Flashcode.ZapcWpxx.BPCWPRYBH, ifNull(wpxx.getBpcwprybh()));
        cv.put(Flashcode.ZapcWpxx.BPCWPLX, ifNull(wpxx.getBpcwplx()));
        cv.put(Flashcode.ZapcWpxx.BPCWPMC, ifNull(wpxx.getBpcwpmc()));
        cv.put(Flashcode.ZapcWpxx.BPCWPCP, ifNull(wpxx.getBpcwpcp()));
        cv.put(Flashcode.ZapcWpxx.BPCWPXH, ifNull(wpxx.getBpcwpxh()));
        cv.put(Flashcode.ZapcWpxx.CLXH, ifNull(wpxx.getClxh()));
        cv.put(Flashcode.ZapcWpxx.CLHPZL, ifNull(wpxx.getClhpzl()));
        cv.put(Flashcode.ZapcWpxx.BHY, ifNull(wpxx.getBhy()));
        cv.put(Flashcode.ZapcWpxx.BHE, ifNull(wpxx.getBhe()));
        cv.put(Flashcode.ZapcWpxx.BHS, ifNull(wpxx.getBhs()));
        cv.put(Flashcode.ZapcWpxx.BPCWPPCSJ, ifNull(wpxx.getBpcwppcsj()));
        cv.put(Flashcode.ZapcWpxx.BPCWPPCDD, ifNull(wpxx.getBpcwppcdd()));
        cv.put(Flashcode.ZapcWpxx.PCYY, ifNull(wpxx.getPcyy()));
        cv.put(Flashcode.ZapcWpxx.BPCWPCLJG, ifNull(wpxx.getBpcwpcljg()));
        cv.put(Flashcode.ZapcWpxx.BPCWPLB, ifNull(wpxx.getBpcwplb()));
        cv.put(Flashcode.ZapcWpxx.JCCFX, ifNull(wpxx.getJccfx()));
        // cv.put(Flashcode.ZapcWpxx.SCBJ, ifNull(wpxx.getScbj()));
        return cv;
    }

    /**
     * 将大平台紧密型日期改为普通格式
     *
     * @param sj
     * @return
     */
    public static String changeDptModNor(String sj) {
        String result = sj;
        try {
            result = sdfNor.format(sdfDpt.parse(sj));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将普通格式改为大平台格式
     *
     * @param sj
     * @return
     */
    public static String changeNorModDpt(String sj) {
        String result = sj;
        try {
            result = sdfDpt.format(sdfNor.parse(sj));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 保存人员信息到数据库中
     *
     * @param pcryxx
     * @param contentResolver
     * @return
     */
    public static int insertPcryxx(ZapcRypcxxBean pcryxx,
                                   ContentResolver contentResolver) {
        int row = 0;
        // int maxId = getMaxRyxxId(contentResolver);
        // pcryxx.setPcrybh(String.valueOf(maxId));
        // pcryxx.getRyjbxx().setRybh(String.valueOf(maxId));
        Uri CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.INSERT_PCRYXX);
        contentResolver.insert(CONTENT_URI, getPcRyxxValues(pcryxx));
        int maxId = getMaxRyxxId(contentResolver);
        pcryxx.getRyjbxx().setRybh(String.valueOf(maxId));
        CONTENT_URI = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.INSERT_JBRYXX);
        contentResolver.insert(CONTENT_URI, getJbRyxxValues(pcryxx));
        return row;
    }

    /**
     * 将盘查人员信息设置已上传标志
     *
     * @param id
     * @param contentResolver
     */
    public static void setPcryxxIsUpload(int id, ContentResolver contentResolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.UPDATE_PCRYXX);
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.ZapcPcryxx.SCBJ, "1");
        String where = Flashcode.ZapcPcryxx.PCRYBH + "='" + id + "'";
        contentResolver.update(uri, cv, where, null);
    }

    /**
     * 盘查物品人员编号设置
     *
     * @param ryid
     * @param glrybh
     * @param contentResolver
     */
    public static void setWpxxRybh(int ryid, String glrybh,
                                   ContentResolver contentResolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.UPDATE_WPXX);
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.ZapcWpxx.BPCWPRYBH, glrybh);
        String where = Flashcode.ZapcWpxx.BPCWPRYBH + "='" + ryid + "'";
        contentResolver.update(uri, cv, where, null);
    }

    /**
     * 将盘查物品信息打上已上传标记
     *
     * @param id
     * @param contentResolver
     */
    public static void setWpxxIsUpload(int id, ContentResolver contentResolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.UPDATE_WPXX);
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.ZapcWpxx.SCBJ, "1");
        String where = Flashcode.ZapcWpxx.XLPCWPBH + "='" + id + "'";
        contentResolver.update(uri, cv, where, null);
    }

    /**
     * 根据给定的工作编号，查询该工作信息下的盘查情况
     *
     * @param gzxx
     * @param contentResolver
     * @param context
     * @return
     */
    public static List<Zapcxx> getPcxxByGzbh(ZapcGzxxBean gzxx,
                                             ContentResolver contentResolver, Context context) {
        List<Zapcxx> list = new ArrayList<Zapcxx>();
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.QUERY_PCRYXX_INFO);
        // 区别对待上传与未上传的
        String where = Flashcode.ZapcPcryxx.GZBH + "='" + gzxx.getId() + "'";
        // if ("1".equals(gzxx.getCsbj()) &&
        // !TextUtils.isEmpty(gzxx.getGzxxbh())) {
        // where = Flashcode.ZapcPcryxx.GZBH + "='" + gzxx.getGzxxbh() + "'";
        // }
        Cursor c = contentResolver.query(uri, null, where, null,
                Flashcode.ZapcPcryxx.PCRYBH + " desc");
        if (c.moveToFirst()) {
            do {
                ZapcRypcxxBean pc = new ZapcRypcxxBean();
                getRypcxxByCursor(pc, c, contentResolver);
                list.add(pc);
            } while (c.moveToNext());
        }
        c.close();
        List<Zapcxx> wps = queryWpxxByGzbhAndRybh(gzxx, contentResolver,
                context);
        if (wps != null && wps.size() > 0)
            list.addAll(wps);
        return list;
    }

    /**
     * 从游标中取数据赋值对象,对象由外部传入
     *
     * @param pc
     * @param c
     * @param contentResolver
     */
    private static void getRypcxxByCursor(ZapcRypcxxBean pc, Cursor c,
                                          ContentResolver contentResolver) {
        pc.setPcrybh(c.getString(0));
        pc.setGzbh(c.getString(1));
        pc.setRycljg(c.getString(2));
        pc.setRypcyy(c.getString(3));
        pc.setRypcdd(c.getString(4));
        pc.setRybdfs(c.getString(5));
        pc.setRybdjg(c.getString(6));
        pc.setRypcsj(c.getString(7));
        pc.setJccfx(c.getString(8));
        pc.setScbj(c.getString(9));
        pc.setRyjbxx(queryRyjbxxByPcbh(pc.getPcrybh(), contentResolver));
    }

    /**
     * 统计所有未发上传的记录数，用于进度条指示
     *
     * @param contentResolver
     * @return
     */
    public static int getAllUnsendCount(ContentResolver contentResolver) {
        int row = 0;
        String sql = "SELECT count(*) FROM zapc_gzxx WHERE csbj=0 and jssj<>''";
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWQUERY);
        Cursor c = contentResolver.query(uri, null, sql, null, null);
        if (c.moveToFirst()) {
            row += c.getInt(0);
        }
        c.close();
        sql = "SELECT count(*) FROM zapc_gzxx g,zapc_pcryxx r WHERE r.gzbh=g.id AND g.csbj=0 AND g.jssj<>'' AND r.scbj=0";
        c = contentResolver.query(uri, null, sql, null, null);
        if (c.moveToFirst()) {
            row += c.getInt(0);
        }
        c.close();
        sql = "SELECT count(*) FROM zapc_gzxx g,zapc_wpxx w WHERE w.bpcwpgzqkbh=g.id AND g.csbj=0 AND g.jssj<>'' AND w.scbj=0";
        c = contentResolver.query(uri, null, sql, null, null);
        if (c.moveToFirst()) {
            row += c.getInt(0);
        }
        c.close();
        return row;
    }

    /**
     * 根据编号查询盘查人员信息
     *
     * @param id
     * @param contentResolver
     * @return
     */
    public static ZapcRypcxxBean queryRyxxById(int id,
                                               ContentResolver contentResolver) {
        ZapcRypcxxBean ryxx = null;
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.QUERY_PCRYXX_INFO);
        String where = Flashcode.ZapcPcryxx.PCRYBH + "=" + id;
        Cursor c = contentResolver.query(uri, null, where, null, null);
        if (c.moveToFirst()) {
            ryxx = new ZapcRypcxxBean();
            getRypcxxByCursor(ryxx, c, contentResolver);
        }
        c.close();
        return ryxx;
    }

    /**
     * 根据编号查物品信息
     *
     * @param id
     * @param contentResolver
     * @return
     */
    public static ZapcWppcxxBean queryWpxxById(int id,
                                               ContentResolver contentResolver, Context context) {
        ZapcWppcxxBean wpxx = null;
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.QUERY_WPXX_INFO);
        String where = Flashcode.ZapcWpxx.XLPCWPBH + "=" + id;
        Cursor c = contentResolver.query(uri, null, where, null, null);
        if (c.moveToFirst()) {
            wpxx = new ZapcWppcxxBean();
            getWpxxByCursor(c, wpxx, context);
        }
        c.close();
        return wpxx;
    }

    /**
     * 查询最后一条人员盘查地点
     *
     * @param contentResolver
     * @return
     */
    public static String queryLastRypcxx(ContentResolver contentResolver) {
        String pcdd = "";
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWQUERY);
        String where = "SELECT rypcdd FROM zapc_pcryxx WHERE pcrybh = (SELECT max(pcrybh) FROM zapc_pcryxx)";
        Cursor c = contentResolver.query(uri, null, where, null, null);
        if (c.moveToFirst()) {
            pcdd = c.getString(0);
        }
        c.close();
        return pcdd;
    }

    /**
     * 根据工作信息和人员编号查询对应的物品信息
     *
     * @param gzxx
     * @param contentResolver
     * @param context
     * @return
     */
    private static List<Zapcxx> queryWpxxByGzbhAndRybh(ZapcGzxxBean gzxx,
                                                       ContentResolver contentResolver, Context context) {
        List<Zapcxx> list = new ArrayList<Zapcxx>();
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.QUERY_WPXX_INFO);
        String where = Flashcode.ZapcWpxx.BPCWPGZQKBH + "='" + gzxx.getId()
                + "'";
        //if ("1".equals(gzxx.getCsbj()) && !TextUtils.isEmpty(gzxx.getGzxxbh())) {
        //	where = Flashcode.ZapcWpxx.BPCWPGZQKBH + "='" + gzxx.getGzxxbh()
        //			+ "'";
        //}
        Cursor c = contentResolver.query(uri, null, where, null,
                Flashcode.ZapcWpxx.BPCWPRYBH + " desc");
        if (c.moveToFirst()) {
            do {
                ZapcWppcxxBean wpxx = new ZapcWppcxxBean();
                getWpxxByCursor(c, wpxx, context);
                list.add(wpxx);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    /**
     * 删除人员盘查信息，人员基本信息，相关机动车盘查信息
     *
     * @param id
     * @param contentResolver
     */
    public static void delPcryxxById(int id, ContentResolver contentResolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWDELSQL);
        contentResolver.delete(uri,
                "DELETE FROM zapc_jbryxx  WHERE rybh=" + id, null);
        contentResolver.delete(uri, "delete from zapc_pcryxx where pcrybh="
                + id, null);
        contentResolver.delete(uri, "delete from "
                + Flashcode.ZapcWpxx.TABLE_NAME + " where "
                + Flashcode.ZapcWpxx.BPCWPRYBH + "='" + id + "'", null);
    }

    /**
     * 删除盘查物品
     *
     * @param id
     * @param contentResolver
     */
    public static void delPcwyxxById(int id, ContentResolver contentResolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.RAWDELSQL);
        contentResolver.delete(uri, "delete from "
                + Flashcode.ZapcWpxx.TABLE_NAME + " where "
                + Flashcode.ZapcWpxx.XLPCWPBH + "=" + id, null);
    }

    private static void getWpxxByCursor(Cursor c, ZapcWppcxxBean wpxx,
                                        Context context) {
        wpxx.setXlpcwpbh(c.getString(0));
        wpxx.setBpcwpgzqkbh(c.getString(1));
        wpxx.setBpcwprybh(c.getString(2));
        wpxx.setBpcwplx(c.getString(3));
        wpxx.setBpcwpmc(c.getString(4));
        wpxx.setBpcwpcp(c.getString(5));
        wpxx.setBpcwpxh(c.getString(6));
        wpxx.setClxh(c.getString(7));
        wpxx.setClhpzl(c.getString(8));
        wpxx.setBhy(c.getString(9));
        wpxx.setBhe(c.getString(10));
        wpxx.setBhs(c.getString(11));
        wpxx.setBpcwppcsj(c.getString(12));
        wpxx.setBpcwppcdd(c.getString(13));
        wpxx.setPcyy(c.getString(14));
        wpxx.setBpcwpcljg(c.getString(15));
        wpxx.setBpcwplb(c.getString(16));
        wpxx.setJccfx(c.getString(17));
        wpxx.setScbj(c.getString(18));
        MessageDao dao = new MessageDao(context);
        String[] s = dao.getZapcWpxxAdd(wpxx.getXlpcwpbh());
        if (s != null) {
            wpxx.setClpp(s[0]);
            wpxx.setSyr(s[1]);
            wpxx.setSfzmhm(s[2]);
        }
        dao.closeDb();
    }

    /**
     * 根据相同的人员编号查人员对象
     *
     * @param pcrybh
     * @param contentResolver
     * @return
     */
    private static ZapcRyjbxxBean queryRyjbxxByPcbh(String pcrybh,
                                                    ContentResolver contentResolver) {
        Uri uri = Uri.parse("content://" + Flashcode.AUTHORITY + "/"
                + Flashcode.QUERY_JBRYXX_INFO);
        String where = Flashcode.ZapcJbryxx.RYBH + "=" + pcrybh;
        ZapcRyjbxxBean jbxx = null;
        Cursor c = contentResolver.query(uri, null, where, null, null);
        if (c.moveToFirst()) {
            do {
                jbxx = new ZapcRyjbxxBean();
                jbxx.setRybh(c.getString(0));
                jbxx.setGmsfhm(c.getString(1));
                jbxx.setXm(c.getString(2));
                jbxx.setZjzl(c.getString(3));
                jbxx.setZjhm(c.getString(4));
                jbxx.setXb(c.getString(5));
                jbxx.setMz(c.getString(6));
                jbxx.setCsrq(c.getString(7));
                jbxx.setJgxz(c.getString(8));
                jbxx.setZjxy(c.getString(9));
                jbxx.setZzmm(c.getString(10));
                jbxx.setWhcd(c.getString(11));
                jbxx.setHyzk(c.getString(12));
                jbxx.setByzk(c.getString(13));
                jbxx.setSg(c.getString(14));
                jbxx.setSf(c.getString(15));
                jbxx.setZylb(c.getString(16));
                jbxx.setFwcs(c.getString(17));
                jbxx.setLxdh(c.getString(18));
                jbxx.setHjqh(c.getString(19));
                jbxx.setHjxz(c.getString(20));
                jbxx.setXxjb(c.getString(21));
                jbxx.setRylb(c.getString(22));
                jbxx.setRysx(c.getString(23));
                jbxx.setXzzqh(c.getString(24));
                jbxx.setXzzxz(c.getString(25));
                jbxx.setScbj(c.getString(26));
            } while (c.moveToNext());
        }
        c.close();
        return jbxx;
    }

    /**
     * 将人员盘查信息打包上传
     *
     * @param pcryxx
     * @return
     */
    private static ContentValues getPcRyxxValues(ZapcRypcxxBean pcryxx) {
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.ZapcPcryxx.GZBH, ifNull(pcryxx.getGzbh()));
        cv.put(Flashcode.ZapcPcryxx.JCCFX, ifNull(pcryxx.getJccfx()));
        // cv.put(Flashcode.ZapcPcryxx.PCRYBH, ifNull(pcryxx.getPcrybh()));
        cv.put(Flashcode.ZapcPcryxx.RYBDFS, ifNull(pcryxx.getRybdfs()));
        cv.put(Flashcode.ZapcPcryxx.RYBDJG, ifNull(pcryxx.getRybdjg()));
        cv.put(Flashcode.ZapcPcryxx.RYCLJG, ifNull(pcryxx.getRycljg()));
        cv.put(Flashcode.ZapcPcryxx.RYPCDD, ifNull(pcryxx.getRypcdd()));
        cv.put(Flashcode.ZapcPcryxx.RYPCSJ, ifNull(pcryxx.getRypcsj()));
        cv.put(Flashcode.ZapcPcryxx.RYPCYY, ifNull(pcryxx.getRypcyy()));
        // cv.put(Flashcode.ZapcPcryxx.SCBJ, ifNull(pcryxx.getScbj()));
        return cv;
    }

    /**
     * 人员基本信息打包
     */
    private static ContentValues getJbRyxxValues(ZapcRypcxxBean pcryxx) {
        ContentValues cv = new ContentValues();
        cv.put(Flashcode.ZapcJbryxx.BYZK, ifNull(pcryxx.getRyjbxx().getByzk()));
        cv.put(Flashcode.ZapcJbryxx.CSRQ, ifNull(pcryxx.getRyjbxx().getCsrq()));
        cv.put(Flashcode.ZapcJbryxx.FWCS, ifNull(pcryxx.getRyjbxx().getFwcs()));
        cv.put(Flashcode.ZapcJbryxx.GMSFHM, ifNull(pcryxx.getRyjbxx()
                .getGmsfhm()));
        cv.put(Flashcode.ZapcJbryxx.HJQH, ifNull(pcryxx.getRyjbxx().getHjqh()));
        cv.put(Flashcode.ZapcJbryxx.HJXZ, ifNull(pcryxx.getRyjbxx().getHjxz()));
        cv.put(Flashcode.ZapcJbryxx.HYZK, ifNull(pcryxx.getRyjbxx().getHyzk()));
        cv.put(Flashcode.ZapcJbryxx.JGXZ, ifNull(pcryxx.getRyjbxx().getJgxz()));
        cv.put(Flashcode.ZapcJbryxx.LXDH, ifNull(pcryxx.getRyjbxx().getLxdh()));
        cv.put(Flashcode.ZapcJbryxx.MZ, ifNull(pcryxx.getRyjbxx().getMz()));
        cv.put(Flashcode.ZapcJbryxx.RYBH, ifNull(pcryxx.getRyjbxx().getRybh()));
        cv.put(Flashcode.ZapcJbryxx.RYLB, ifNull(pcryxx.getRyjbxx().getRylb()));
        cv.put(Flashcode.ZapcJbryxx.RYSX, ifNull(pcryxx.getRyjbxx().getRysx()));
        // cv.put(Flashcode.ZapcJbryxx.SCBJ,
        // ifNull(pcryxx.getRyjbxx().getScbj()));
        cv.put(Flashcode.ZapcJbryxx.SF, ifNull(pcryxx.getRyjbxx().getSf()));
        cv.put(Flashcode.ZapcJbryxx.SG, ifNull(pcryxx.getRyjbxx().getSg()));
        cv.put(Flashcode.ZapcJbryxx.WHCD, ifNull(pcryxx.getRyjbxx().getWhcd()));
        cv.put(Flashcode.ZapcJbryxx.XB, ifNull(pcryxx.getRyjbxx().getXb()));
        cv.put(Flashcode.ZapcJbryxx.XM, ifNull(pcryxx.getRyjbxx().getXm()));
        cv.put(Flashcode.ZapcJbryxx.XXJB, ifNull(pcryxx.getRyjbxx().getXxjb()));
        cv.put(Flashcode.ZapcJbryxx.XZZQH,
                ifNull(pcryxx.getRyjbxx().getXzzqh()));
        cv.put(Flashcode.ZapcJbryxx.XZZXZ,
                ifNull(pcryxx.getRyjbxx().getXzzxz()));
        cv.put(Flashcode.ZapcJbryxx.ZJHM, ifNull(pcryxx.getRyjbxx().getZjhm()));
        cv.put(Flashcode.ZapcJbryxx.ZJXY, ifNull(pcryxx.getRyjbxx().getZjxy()));
        cv.put(Flashcode.ZapcJbryxx.ZJZL, ifNull(pcryxx.getRyjbxx().getZjzl()));
        cv.put(Flashcode.ZapcJbryxx.ZYLB, ifNull(pcryxx.getRyjbxx().getZylb()));
        cv.put(Flashcode.ZapcJbryxx.ZZMM, ifNull(pcryxx.getRyjbxx().getZzmm()));
        return cv;
    }

}
