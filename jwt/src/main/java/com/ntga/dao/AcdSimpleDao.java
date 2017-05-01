package com.ntga.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.acd.simple.provider.AcdSimple;
import com.acd.simple.provider.AcdSimple.AcdPhotoRecode;
import com.android.provider.fixcode.Fixcode;
import com.ntga.bean.AcdPhotoBean;
import com.ntga.bean.AcdSimpleBean;
import com.ntga.bean.AcdSimpleHumanBean;
import com.ntga.bean.AcdWftLawBean;
import com.ntga.bean.AcdWfxwBean;

public class AcdSimpleDao {

    public static final int ACD_MOD_NEW = 0;
    public static final int ACD_MOD_SHOW = 1;
    public static final int ACD_MOD_MODITY = 2;
    public static final int ACD_MOD_PHOTO_NEW = 3;
    public static final String OPER_MOD = "oper_mod";

    public static final String PHOTO_BEAN = "photoBean";

    private static Uri createUri(String u) {
        return Uri.parse("content://" + AcdSimple.AUTHORITY + "/" + u);
    }

    /**
     * 查询所有事故照片
     *
     * @param resolver
     * @param where
     * @return
     */
    public static List<AcdPhotoBean> getAllAcdPhoto(ContentResolver resolver,
                                                    String where) {
        List<AcdPhotoBean> list = new ArrayList<AcdPhotoBean>();
        Uri uri = createUri(AcdSimple.QUERY_ACD_PHOTO);
        Cursor cs = resolver.query(uri, null, where, null,
                AcdPhotoRecode.ID + " desc");
        if (cs.moveToFirst()) {
            do {
                list.add(setAcdPhoto(cs));
            } while (cs.moveToNext());
        }
        cs.close();
        if (list.size() > 0) {
            for (AcdPhotoBean ab : list) {
                if (ab.getId() > 0) {
                    List<String> files = getAcdPhotoFiles(resolver, ab.getId());
                    ab.setPhoto(files);
                }
            }
        }
        return list;
    }

    /**
     * 查询对应ID的照片文件
     *
     * @param resolver
     * @param recID
     * @return
     */
    private static List<String> getAcdPhotoFiles(ContentResolver resolver,
                                                 int recID) {
        List<String> files = new ArrayList<String>();
        Uri uri = createUri(AcdSimple.QUERY_ACD_FILE);
        Cursor cs = resolver.query(uri, null, AcdSimple.AcdPhotoFile.REC_ID
                + "=" + recID, null, AcdSimple.AcdPhotoFile.ID + " desc");
        if (cs.moveToFirst()) {
            do {
                files.add(cs.getString(2));
            } while (cs.moveToNext());
        }
        cs.close();
        return files;
    }

    private static AcdPhotoBean setAcdPhoto(Cursor cs) {
        AcdPhotoBean ab = new AcdPhotoBean();
        ab.setId(cs.getInt(cs.getColumnIndex(AcdPhotoRecode.ID)));
        ab.setSgsj(cs.getString(cs
                .getColumnIndex(AcdPhotoRecode.SGSJ)));
        ab.setSgdddm(cs.getString(cs
                .getColumnIndex(AcdPhotoRecode.SGDDDM)));
        ab.setSgdd(cs.getString(cs
                .getColumnIndex(AcdPhotoRecode.SGDD)));
        ab.setSgbh(cs.getString(cs
                .getColumnIndex(AcdPhotoRecode.SGBH)));
        ab.setXtbh(cs.getString(cs
                .getColumnIndex(AcdPhotoRecode.XTBH)));
        ab.setScbj(cs.getInt(cs.getColumnIndex(AcdPhotoRecode.SCBJ)));
        return ab;
    }

    /**
     * 保存事故照片记录，包括照片和文字档
     *
     * @param resolver
     * @param apb
     * @return
     */
    public static String addAcdPhoto(ContentResolver resolver, AcdPhotoBean apb) {
        String id = "";
        ContentValues cv = new ContentValues();
        cv.put(AcdPhotoRecode.SGSJ, apb.getSgsj());
        cv.put(AcdPhotoRecode.SGDDDM, apb.getSgdddm());
        cv.put(AcdPhotoRecode.SGDD, apb.getSgdd());
        cv.put(AcdPhotoRecode.SGBH, apb.getSgbh());
        cv.put(AcdPhotoRecode.XTBH, apb.getXtbh());
        Uri uri = createUri(AcdSimple.ADD_ACD_PHOTO);
        Uri res = resolver.insert(uri, cv);
        if (!TextUtils.isEmpty(res.getLastPathSegment())) {
            id = res.getLastPathSegment();
            for (String file : apb.getPhoto()) {
                addAcdPhotoFile(resolver, file, Integer.valueOf(id));
            }
        }
        return id;
    }

    /**
     * 更新回传的系统编号
     *
     * @param resolver
     * @param xtbh
     * @param id
     * @return
     */
    public static int updateAcdPhotoRecode(ContentResolver resolver, long xtbh,
                                           long id) {
        Uri uri = createUri(AcdSimple.UPDATE_ACD_PHOTO);
        ContentValues cv = new ContentValues();
        cv.put(AcdPhotoRecode.XTBH, xtbh);
        String where = AcdPhotoRecode.ID + "=" + id;
        int r = resolver.update(uri, cv, where, null);
        return r;
    }

    public static int updateAcdPhotoRecodeScbj(ContentResolver resolver, long id) {
        Uri uri = createUri(AcdSimple.UPDATE_ACD_PHOTO);
        ContentValues cv = new ContentValues();
        cv.put(AcdPhotoRecode.SCBJ, 1);
        String where = AcdPhotoRecode.ID + "=" + id;
        int r = resolver.update(uri, cv, where, null);
        return r;
    }

    /**
     * 删除一条文字记录，同时删除相关的照片文件记录
     *
     * @param resolver
     * @param acd
     * @return
     */
    public static int delAcdPhotoRecode(ContentResolver resolver,
                                        AcdPhotoBean acd) {
        Uri uri = createUri(AcdSimple.DEL_ACD_PHOTO);
        int r = resolver.delete(uri,
                AcdPhotoRecode.ID + "=" + acd.getId(), null);
        if (r > 0) {
            uri = createUri(AcdSimple.DEL_ACD_FILE);
            r += resolver.delete(uri,
                    AcdSimple.AcdPhotoFile.REC_ID + "=" + acd.getId(), null);
        }
        return r;
    }

    /**
     * 将图片和记录号写入表中
     *
     * @param resolver
     * @param file
     * @param recID
     * @return
     */
    private static String addAcdPhotoFile(ContentResolver resolver,
                                          String file, int recID) {
        String id = "";
        ContentValues cv = new ContentValues();
        cv.put(AcdSimple.AcdPhotoFile.REC_ID, recID);
        cv.put(AcdSimple.AcdPhotoFile.PHOTO, file);
        Uri uri = createUri(AcdSimple.ADD_ACD_FILE);
        Uri res = resolver.insert(uri, cv);
        if (!TextUtils.isEmpty(res.getLastPathSegment())) {
            id = res.getLastPathSegment();
        }
        return id;
    }

    /**
     * 查询所有符合条件的事故记录
     *
     * @param resolver
     * @return
     */
    public static List<AcdSimpleBean> getAllAcd(ContentResolver resolver,
                                                String where) {
        List<AcdSimpleBean> list = new ArrayList<AcdSimpleBean>();
        Uri uri = createUri(AcdSimple.QUERY_ACD);
        Cursor cs = resolver.query(uri, null, where, null,
                AcdSimple.AcdDutySimple.SGBH + " desc");
        if (cs.moveToFirst()) {
            do {
                list.add(setAcd(cs));
            } while (cs.moveToNext());
        }
        cs.close();
        return list;
    }

    public static int saveAcdSimpleScbj(ContentResolver resolver, String id) {
        Uri uri = createUri(AcdSimple.UPDATE_ACD);
        ContentValues cv = new ContentValues();
        cv.put(AcdSimple.AcdDutySimple.SCBJ, "1");
        String where = AcdSimple.AcdDutySimple.SGBH + "='" + id + "'";
        int re = resolver.update(uri, cv, where, null);
        return re;
    }

    /**
     * 删除事故情况和人员情况
     *
     * @param resolver
     * @param sgbh
     */
    public static int delAcdAndHuman(ContentResolver resolver, String sgbh) {
        int row = 0;
        Uri uri = createUri(AcdSimple.DEL_ACD);
        row += resolver.delete(uri, AcdSimple.AcdDutySimple.SGBH + "=?",
                new String[]{sgbh});
        row += delHumans(resolver, sgbh);
        return row;
    }

    private static int delHumans(ContentResolver resolver, String sgbh) {
        int row = 0;
        Uri uri = createUri(AcdSimple.DEL_HUMAN);
        row += resolver.delete(uri, AcdSimple.AcdDutySimpleHuman.SGBH + "=?",
                new String[]{sgbh});
        return row;
    }

    /**
     * 根据特定的条件查询人员
     *
     * @param resolver
     * @param where
     * @return
     */
    public static ArrayList<AcdSimpleHumanBean> queryHumanByCond(
            ContentResolver resolver, String where) {
        ArrayList<AcdSimpleHumanBean> humans = new ArrayList<AcdSimpleHumanBean>();
        Uri uri = createUri(AcdSimple.QUERY_HUMAN);
        Cursor cs = resolver.query(uri, null, where, null,
                AcdSimple.AcdDutySimpleHuman.RYBH);
        if (cs.moveToFirst()) {
            do {
                humans.add(setAcdHuman(cs));
            } while (cs.moveToNext());
        }
        cs.close();
        return humans;
    }

    /**
     * 查询法律依据
     *
     * @param resolver
     * @param where
     * @return
     */
    public static ArrayList<AcdWftLawBean> queryWftknrByCond(
            ContentResolver resolver, String where) {
        ArrayList<AcdWftLawBean> laws = new ArrayList<AcdWftLawBean>();
        Uri uri = createUri(AcdSimple.QUERY_ACD_LAW);
        Cursor cs = resolver
                .query(uri, null, where, null, AcdSimple.AcdLaws.XH);
        if (cs.moveToFirst()) {
            do {
                AcdWftLawBean wft = new AcdWftLawBean();
                wft.setXh(cs.getString(0));
                wft.setFlmc(cs.getString(1));
                wft.setTkmc(cs.getString(2));
                wft.setTknr(cs.getString(3));
                laws.add(wft);
            } while (cs.moveToNext());
        }
        cs.close();
        return laws;
    }

    /**
     * 根据代码查法规
     *
     * @param resolver
     * @param xh
     * @return
     */
    public static AcdWftLawBean queryWftknrByXh(ContentResolver resolver,
                                                String xh) {
        AcdWftLawBean law = null;
        Uri uri = createUri(AcdSimple.QUERY_ACD_LAW);
        Cursor cs = resolver.query(uri, null, AcdSimple.AcdLaws.XH + "=?",
                new String[]{xh}, null);
        if (cs.moveToFirst()) {
            law = new AcdWftLawBean();
            law.setXh(cs.getString(0));
            law.setFlmc(cs.getString(1));
            law.setTkmc(cs.getString(2));
            law.setTknr(cs.getString(3));
        }
        cs.close();
        return law;
    }

    private static AcdSimpleHumanBean setAcdHuman(Cursor cs) {
        AcdSimpleHumanBean human = new AcdSimpleHumanBean();
        human.setHumanID(cs.getString(0));
        human.setSgbh(cs.getString(1));
        human.setXzqh(cs.getString(2));
        human.setRybh(cs.getInt(3));
        human.setXm(cs.getString(4));
        human.setXb(cs.getString(5));
        human.setSfzmhm(cs.getString(6));
        human.setNl(cs.getString(7));
        human.setZz(cs.getString(8));
        human.setDh(cs.getString(9));
        human.setRylx(cs.getString(10));
        human.setShcd(cs.getString(11));
        human.setWfxw1(cs.getString(12));
        human.setWfxw2(cs.getString(13));
        human.setWfxw3(cs.getString(14));
        human.setTk1(cs.getString(15));
        human.setTk2(cs.getString(16));
        human.setTk3(cs.getString(17));
        human.setZyysdw(cs.getString(18));
        human.setJtfs(cs.getString(19));
        human.setGlxzqh(cs.getString(20));
        human.setDabh(cs.getString(21));
        human.setJl(cs.getString(22));
        human.setJszzl(cs.getString(23));
        human.setZjcx(cs.getString(24));
        human.setCclzrq(cs.getString(25));
        human.setJsrgxd(cs.getString(26));
        human.setFzjg(cs.getString(27));
        human.setSgzr(cs.getString(28));
        human.setHphm(cs.getString(29));
        human.setHpzl(cs.getString(30));
        human.setClfzjg(cs.getString(31));
        human.setFdjh(cs.getString(32));
        human.setClsbdh(cs.getString(33));
        human.setJdcxh(cs.getString(34));
        human.setClpp(cs.getString(35));
        human.setClxh(cs.getString(36));
        human.setCsys(cs.getString(37));
        human.setCllx(cs.getString(38));
        human.setJdczt(cs.getString(39));
        human.setSyq(cs.getString(40));
        human.setJdcsyr(cs.getString(41));
        human.setClsyxz(cs.getString(42));
        human.setBx(cs.getString(43));
        human.setBxgs(cs.getString(44));
        human.setBxpzh(cs.getString(45));
        human.setClzzwp(cs.getString(46));
        human.setClgxd(cs.getString(47));
        human.setCjcxbj(cs.getString(48));
        human.setJyw(cs.getString(49));
        human.setScbj(cs.getString(50));
        return human;
    }

    /**
     * 保存事故基本信息
     *
     * @param resolver
     * @param acd
     * @return 保存事故的顺序号
     */
    public static String saveAcdJbxxIntoDb(ContentResolver resolver,
                                           AcdSimpleBean acd) {
        String id = "";
        ContentValues cv = createCv(acd);
        if (TextUtils.isEmpty(acd.getSgbh())) {
            Uri uri = createUri(AcdSimple.ADD_ACD);
            Uri res = resolver.insert(uri, cv);
            if (!TextUtils.isEmpty(res.getLastPathSegment())) {
                id = res.getLastPathSegment();
            }
        } else {
            Uri uri = createUri(AcdSimple.UPDATE_ACD);
            resolver.update(uri, cv, AcdSimple.AcdDutySimple.SGBH + "=?",
                    new String[]{acd.getSgbh()});
            return acd.getSgbh();
        }

        return id;
    }

    /**
     * 保存人员情况
     *
     * @param resolver
     * @param human
     */
    public static String saveAcdHumanInDb(ContentResolver resolver,
                                          AcdSimpleHumanBean human) {
        String id = "";
        if (TextUtils.isEmpty(human.getHumanID())) {
            Uri uri = createUri(AcdSimple.ADD_HUMAN);
            Uri res = resolver.insert(uri, createHumanCv(human));
            if (!TextUtils.isEmpty(res.getLastPathSegment())) {
                id = res.getLastPathSegment();
            }
        } else {
            Uri uri = createUri(AcdSimple.UPDATE_HUMAN);
            resolver.update(uri, createHumanCv(human),
                    AcdSimple.AcdDutySimpleHuman.HUMAN_ID + "=?",
                    new String[]{human.getHumanID()});
            return human.getHumanID();
        }
        return id;
    }

    public static int queryAcdCount(ContentResolver resolver) {
        int id = 0;
        Uri uri = createUri(AcdSimple.RAW_QUERY);
        String where = "SELECT count(*) FROM ACD_DUTYSIMPLE";
        Cursor cs = resolver.query(uri, null, where, null, null);
        if (cs.moveToFirst()) {
            id = cs.getInt(0);
        }
        cs.close();
        return id;
    }

    public static int queryAcdHumanCount(ContentResolver resolver) {
        int id = 0;
        Uri uri = createUri(AcdSimple.RAW_QUERY);
        String where = "SELECT count(*) FROM ACD_DUTYSIMPLEHUMAN";
        Cursor cs = resolver.query(uri, null, where, null, null);
        if (cs.moveToFirst()) {
            id = cs.getInt(0);
        }
        cs.close();
        return id;
    }

    /**
     * 取已保存事故基本信息的当前最大编号
     *
     * @param resolver
     * @return
     */
    // private static int getMaxId(ContentResolver resolver, String tableName) {
    // int id = 0;
    // Uri uri = createUri(AcdSimple.RAW_QUERY);
    // String where = "SELECT seq FROM sqlite_sequence WHERE name='"
    // + tableName + "'";
    // Cursor cs = resolver.query(uri, null, where, null, null);
    // if (cs.moveToFirst()) {
    // id = cs.getInt(0);
    // }
    // cs.close();
    // return id;
    // }

    /**
     * 根据事故违法代码查询相对应的内容
     *
     * @param wfdm
     * @param resolver
     * @return
     */
    public static AcdWfxwBean getAcdWfxwByWfdm(String wfdm,
                                               ContentResolver resolver) {
        AcdWfxwBean acdWfxw = null;
        Uri uri = Uri.parse("content://" + Fixcode.AUTHORITY + "/item/"
                + GlobalConstant.ACD_WFXW);
        Cursor cs = resolver.query(uri, new String[]{Fixcode.FrmCode.DMZ,
                Fixcode.FrmCode.DMSM1, Fixcode.FrmCode.DMSM2,
                Fixcode.FrmCode.DMSM3}, Fixcode.FrmCode.DMZ + "='" + wfdm
                + "'", null, null);
        if (cs.moveToFirst()) {
            acdWfxw = new AcdWfxwBean();
            acdWfxw.setWfxwdm(cs.getString(0));
            acdWfxw.setWfnr(cs.getString(1));
            acdWfxw.setRdyy(cs.getString(2));
            acdWfxw.setWffl(cs.getString(3));
        }
        cs.close();
        return acdWfxw;
    }

    private static ContentValues createHumanCv(AcdSimpleHumanBean human) {
        ContentValues cv = new ContentValues();
        // cv.put(AcdSimple.AcdDutySimpleHuman.HUMAN_ID,
        // GlobalMethod.ifNull(human
        // .getHumanID()));
        cv.put(AcdSimple.AcdDutySimpleHuman.SGBH,
                GlobalMethod.ifNull(human.getSgbh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.XZQH,
                GlobalMethod.ifNull(human.getXzqh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.RYBH, human.getRybh());
        cv.put(AcdSimple.AcdDutySimpleHuman.XM,
                GlobalMethod.ifNull(human.getXm()));
        cv.put(AcdSimple.AcdDutySimpleHuman.XB,
                GlobalMethod.ifNull(human.getXb()));
        cv.put(AcdSimple.AcdDutySimpleHuman.SFZMHM,
                GlobalMethod.ifNull(human.getSfzmhm()));
        cv.put(AcdSimple.AcdDutySimpleHuman.NL,
                GlobalMethod.ifNull(human.getNl()));
        cv.put(AcdSimple.AcdDutySimpleHuman.ZZ,
                GlobalMethod.ifNull(human.getZz()));
        cv.put(AcdSimple.AcdDutySimpleHuman.DH,
                GlobalMethod.ifNull(human.getDh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.RYLX,
                GlobalMethod.ifNull(human.getRylx()));
        cv.put(AcdSimple.AcdDutySimpleHuman.SHCD,
                GlobalMethod.ifNull(human.getShcd()));
        cv.put(AcdSimple.AcdDutySimpleHuman.WFXW1,
                GlobalMethod.ifNull(human.getWfxw1()));
        cv.put(AcdSimple.AcdDutySimpleHuman.WFXW2,
                GlobalMethod.ifNull(human.getWfxw2()));
        cv.put(AcdSimple.AcdDutySimpleHuman.WFXW3,
                GlobalMethod.ifNull(human.getWfxw3()));
        cv.put(AcdSimple.AcdDutySimpleHuman.TK1,
                GlobalMethod.ifNull(human.getTk1()));
        cv.put(AcdSimple.AcdDutySimpleHuman.TK2,
                GlobalMethod.ifNull(human.getTk2()));
        cv.put(AcdSimple.AcdDutySimpleHuman.TK3,
                GlobalMethod.ifNull(human.getTk3()));
        cv.put(AcdSimple.AcdDutySimpleHuman.ZYYSDW,
                GlobalMethod.ifNull(human.getZyysdw()));
        cv.put(AcdSimple.AcdDutySimpleHuman.JTFS,
                GlobalMethod.ifNull(human.getJtfs()));
        cv.put(AcdSimple.AcdDutySimpleHuman.GLXZQH,
                GlobalMethod.ifNull(human.getGlxzqh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.DABH,
                GlobalMethod.ifNull(human.getDabh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.JL,
                GlobalMethod.ifNull(human.getJl()));
        cv.put(AcdSimple.AcdDutySimpleHuman.JSZZL,
                GlobalMethod.ifNull(human.getJszzl()));
        cv.put(AcdSimple.AcdDutySimpleHuman.ZJCX,
                GlobalMethod.ifNull(human.getZjcx()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CCLZRQ,
                GlobalMethod.ifNull(human.getCclzrq()));
        cv.put(AcdSimple.AcdDutySimpleHuman.JSRGXD,
                GlobalMethod.ifNull(human.getJsrgxd()));
        cv.put(AcdSimple.AcdDutySimpleHuman.FZJG,
                GlobalMethod.ifNull(human.getFzjg()));
        cv.put(AcdSimple.AcdDutySimpleHuman.SGZR,
                GlobalMethod.ifNull(human.getSgzr()));
        cv.put(AcdSimple.AcdDutySimpleHuman.HPHM,
                GlobalMethod.ifNull(human.getHphm()));
        cv.put(AcdSimple.AcdDutySimpleHuman.HPZL,
                GlobalMethod.ifNull(human.getHpzl()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CLFZJG,
                GlobalMethod.ifNull(human.getClfzjg()));
        cv.put(AcdSimple.AcdDutySimpleHuman.FDJH,
                GlobalMethod.ifNull(human.getFdjh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CLSBDH,
                GlobalMethod.ifNull(human.getClsbdh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.JDCXH,
                GlobalMethod.ifNull(human.getJdcxh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CLPP,
                GlobalMethod.ifNull(human.getClpp()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CLXH,
                GlobalMethod.ifNull(human.getClxh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CSYS,
                GlobalMethod.ifNull(human.getCsys()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CLLX,
                GlobalMethod.ifNull(human.getCllx()));
        cv.put(AcdSimple.AcdDutySimpleHuman.JDCZT,
                GlobalMethod.ifNull(human.getJdczt()));
        cv.put(AcdSimple.AcdDutySimpleHuman.SYQ,
                GlobalMethod.ifNull(human.getSyq()));
        cv.put(AcdSimple.AcdDutySimpleHuman.JDCSYR,
                GlobalMethod.ifNull(human.getJdcsyr()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CLSYXZ,
                GlobalMethod.ifNull(human.getClsyxz()));
        cv.put(AcdSimple.AcdDutySimpleHuman.BX,
                GlobalMethod.ifNull(human.getBx()));
        cv.put(AcdSimple.AcdDutySimpleHuman.BXGS,
                GlobalMethod.ifNull(human.getBxgs()));
        cv.put(AcdSimple.AcdDutySimpleHuman.BXPZH,
                GlobalMethod.ifNull(human.getBxpzh()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CLZZWP,
                GlobalMethod.ifNull(human.getClzzwp()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CLGXD,
                GlobalMethod.ifNull(human.getClgxd()));
        cv.put(AcdSimple.AcdDutySimpleHuman.CJCXBJ,
                GlobalMethod.ifNull(human.getCjcxbj()));
        cv.put(AcdSimple.AcdDutySimpleHuman.JYW,
                GlobalMethod.ifNull(human.getJyw()));
        cv.put(AcdSimple.AcdDutySimpleHuman.SCBJ,
                GlobalMethod.ifNull(human.getScbj()));
        return cv;
    }

    private static ContentValues createCv(AcdSimpleBean acd) {
        ContentValues cv = new ContentValues();

        // cv
        // .put(AcdSimple.AcdDutySimple.SGBH, GlobalMethod.ifNull(acd
        // .getSgbh()));
        cv.put(AcdSimple.AcdDutySimple.XZQH, GlobalMethod.ifNull(acd.getXzqh()));
        cv.put(AcdSimple.AcdDutySimple.XQ, GlobalMethod.ifNull(acd.getXq()));
        cv.put(AcdSimple.AcdDutySimple.SGFSSJ,
                GlobalMethod.ifNull(acd.getSgfssj()));
        cv.put(AcdSimple.AcdDutySimple.LH, GlobalMethod.ifNull(acd.getLh()));
        cv.put(AcdSimple.AcdDutySimple.LM, GlobalMethod.ifNull(acd.getLm()));
        cv.put(AcdSimple.AcdDutySimple.GLS, GlobalMethod.ifNull(acd.getGls()));
        cv.put(AcdSimple.AcdDutySimple.MS, GlobalMethod.ifNull(acd.getMs()));
        cv.put(AcdSimple.AcdDutySimple.JDWZ, GlobalMethod.ifNull(acd.getJdwz()));
        cv.put(AcdSimple.AcdDutySimple.SGDD, GlobalMethod.ifNull(acd.getSgdd()));
        cv.put(AcdSimple.AcdDutySimple.SSRS, GlobalMethod.ifNull(acd.getSsrs()));
        cv.put(AcdSimple.AcdDutySimple.ZJCCSS,
                GlobalMethod.ifNull(acd.getZjccss()));
        cv.put(AcdSimple.AcdDutySimple.LWSGLX,
                GlobalMethod.ifNull(acd.getLwsglx()));
        cv.put(AcdSimple.AcdDutySimple.RDYYFL,
                GlobalMethod.ifNull(acd.getRdyyfl()));
        cv.put(AcdSimple.AcdDutySimple.SGRDYY,
                GlobalMethod.ifNull(acd.getSgrdyy()));
        cv.put(AcdSimple.AcdDutySimple.TQ, GlobalMethod.ifNull(acd.getTq()));
        cv.put(AcdSimple.AcdDutySimple.XC, GlobalMethod.ifNull(acd.getXc()));
        cv.put(AcdSimple.AcdDutySimple.SWSG, GlobalMethod.ifNull(acd.getSwsg()));
        cv.put(AcdSimple.AcdDutySimple.SGXT, GlobalMethod.ifNull(acd.getSgxt()));
        cv.put(AcdSimple.AcdDutySimple.CLJSG,
                GlobalMethod.ifNull(acd.getCljsg()));
        cv.put(AcdSimple.AcdDutySimple.DCSG, GlobalMethod.ifNull(acd.getDcsg()));
        cv.put(AcdSimple.AcdDutySimple.PZFS, GlobalMethod.ifNull(acd.getPzfs()));
        cv.put(AcdSimple.AcdDutySimple.LBQK, GlobalMethod.ifNull(acd.getLbqk()));
        cv.put(AcdSimple.AcdDutySimple.TJR1, GlobalMethod.ifNull(acd.getTjr1()));
        cv.put(AcdSimple.AcdDutySimple.CCLRSJ,
                GlobalMethod.ifNull(acd.getCclrsj()));
        cv.put(AcdSimple.AcdDutySimple.JLLX, GlobalMethod.ifNull(acd.getJllx()));
        cv.put(AcdSimple.AcdDutySimple.SCSJD,
                GlobalMethod.ifNull(acd.getScsjd()));
        cv.put(AcdSimple.AcdDutySimple.SSZD, GlobalMethod.ifNull(acd.getSszd()));
        cv.put(AcdSimple.AcdDutySimple.DAH, GlobalMethod.ifNull(acd.getDah()));
        cv.put(AcdSimple.AcdDutySimple.SB, GlobalMethod.ifNull(acd.getSb()));
        cv.put(AcdSimple.AcdDutySimple.TJSGBH,
                GlobalMethod.ifNull(acd.getTjsgbh()));
        cv.put(AcdSimple.AcdDutySimple.GLBM, GlobalMethod.ifNull(acd.getGlbm()));
        cv.put(AcdSimple.AcdDutySimple.DZZB, GlobalMethod.ifNull(acd.getDzzb()));
        cv.put(AcdSimple.AcdDutySimple.BADW, GlobalMethod.ifNull(acd.getBadw()));
        cv.put(AcdSimple.AcdDutySimple.WSBH, GlobalMethod.ifNull(acd.getWsbh()));
        cv.put(AcdSimple.AcdDutySimple.SGSS, GlobalMethod.ifNull(acd.getSgss()));
        cv.put(AcdSimple.AcdDutySimple.ZRTJJG,
                GlobalMethod.ifNull(acd.getZrtjjg()));
        cv.put(AcdSimple.AcdDutySimple.JAR1, GlobalMethod.ifNull(acd.getJar1()));
        cv.put(AcdSimple.AcdDutySimple.JAR2, GlobalMethod.ifNull(acd.getJar2()));
        cv.put(AcdSimple.AcdDutySimple.JBR, GlobalMethod.ifNull(acd.getJbr()));
        cv.put(AcdSimple.AcdDutySimple.GXSJ, GlobalMethod.ifNull(acd.getGxsj()));
        cv.put(AcdSimple.AcdDutySimple.JYW, GlobalMethod.ifNull(acd.getJyw()));
        cv.put(AcdSimple.AcdDutySimple.JAFS, GlobalMethod.ifNull(acd.getJafs()));
        cv.put(AcdSimple.AcdDutySimple.DLLX, GlobalMethod.ifNull(acd.getDllx()));
        cv.put(AcdSimple.AcdDutySimple.GLXZDJ,
                GlobalMethod.ifNull(acd.getGlxzdj()));
        cv.put(AcdSimple.AcdDutySimple.TJFS, GlobalMethod.ifNull(acd.getTjfs()));
        cv.put(AcdSimple.AcdDutySimple.SCBJ, GlobalMethod.ifNull(acd.getScbj()));
        return cv;
    }


    private static AcdSimpleBean setAcd(Cursor c) {
        AcdSimpleBean acd = new AcdSimpleBean();
        acd.setSgbh(c.getString(0));
        acd.setXzqh(c.getString(1));
        acd.setXq(c.getString(2));
        acd.setSgfssj(c.getString(3));
        acd.setLh(c.getString(4));
        acd.setLm(c.getString(5));
        acd.setGls(GlobalMethod.paddingZero(c.getString(6), 4));
        acd.setMs(GlobalMethod.paddingZero(c.getString(7), 3));
        acd.setJdwz(c.getString(8));
        acd.setSgdd(c.getString(9));
        acd.setSsrs(c.getString(10));
        acd.setZjccss(c.getString(11));
        acd.setLwsglx(c.getString(12));
        acd.setRdyyfl(c.getString(13));
        acd.setSgrdyy(c.getString(14));
        acd.setTq(c.getString(15));
        acd.setXc(c.getString(16));
        acd.setSwsg(c.getString(17));
        acd.setSgxt(c.getString(18));
        acd.setCljsg(c.getString(19));
        acd.setDcsg(c.getString(20));
        acd.setPzfs(c.getString(21));
        acd.setLbqk(c.getString(22));
        acd.setTjr1(c.getString(23));
        acd.setCclrsj(c.getString(24));
        acd.setJllx(c.getString(25));
        acd.setScsjd(c.getString(26));
        acd.setSszd(c.getString(27));
        acd.setDah(c.getString(28));
        acd.setSb(c.getString(29));
        acd.setTjsgbh(c.getString(30));
        acd.setGlbm(c.getString(31));
        acd.setDzzb(c.getString(32));
        acd.setBadw(c.getString(33));
        acd.setWsbh(c.getString(34));
        acd.setSgss(c.getString(35));
        acd.setZrtjjg(c.getString(36));
        acd.setJar1(c.getString(37));
        acd.setJar2(c.getString(38));
        acd.setJbr(c.getString(39));
        acd.setGxsj(c.getString(40));
        acd.setJyw(c.getString(41));
        acd.setJafs(c.getString(42));
        acd.setDllx(c.getString(43));
        acd.setGlxzdj(c.getString(44));
        acd.setTjfs(c.getString(45));
        acd.setScbj(c.getString(46));
        return acd;
    }

    public static String createRyxxStr(AcdSimpleHumanBean human) {
        String delimiter1 = "$$";
        String delimiter2 = "~~";
        String ryxx = "";
        ryxx += delimiter1;// 无需事故编号
        ryxx += GlobalMethod.ifNull(human.getXzqh()) + delimiter1;
        ryxx += (human.getRybh() + 1) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getXm()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getXb()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getSfzmhm()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getNl()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getZz()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getDh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getRylx()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getShcd()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getWfxw1()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getWfxw2()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getWfxw3()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getTk1()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getTk2()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getTk3()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getZyysdw()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJtfs()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getGlxzqh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getDabh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJl()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJszzl()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getZjcx()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getCclzrq()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJsrgxd()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getFzjg()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getSgzr()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getHphm()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getHpzl()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClfzjg()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getFdjh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClsbdh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJdcxh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClpp()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClxh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getCsys()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getCllx()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJdczt()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getSyq()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJdcsyr()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClsyxz()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getBx()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getBxgs()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getBxpzh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClzzwp()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClgxd()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getCjcxbj()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJyw()) + delimiter1 + delimiter2;
        return ryxx;
    }

}
