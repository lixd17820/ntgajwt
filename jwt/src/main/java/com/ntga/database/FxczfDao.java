package com.ntga.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.ntga.bean.VioFxcFileBean;
import com.ntga.bean.VioFxczfBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FxczfDao {

    private SQLiteDatabase database;

    public FxczfDao(Context context) {

        int version = 0;
        try {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        MessageDbHelper db = new MessageDbHelper(context, version);
        database = db.getWritableDatabase();
    }

    public void closeDb() {
        database.close();
    }

    /**
     * 将收到的信息加入到数据表中
     *
     * @param fxc
     */
    public long insertFxczfDb(VioFxczfBean fxc) {
        ContentValues cv = new ContentValues();
        cv.put("cjjg", fxc.getCjjg());
        cv.put("hpzl", fxc.getHpzl());
        cv.put("hphm", fxc.getHphm());
        cv.put("jtfs", fxc.getJtfs());
        cv.put("fzjg", fxc.getFzjg());
        cv.put("tzsh", fxc.getTzsh());
        cv.put("tzrq", fxc.getTzrq());
        cv.put("wfsj", fxc.getWfsj());
        cv.put("xzqh", fxc.getXzqh());
        cv.put("wfdd", fxc.getWfdd());
        cv.put("lddm", fxc.getLddm());
        cv.put("ddms", fxc.getDdms());
        cv.put("wfdz", fxc.getWfdz());
        cv.put("wfxw", fxc.getWfxw());
        cv.put("zqmj", fxc.getZqmj());
        cv.put("sbbh", fxc.getSbbh());
        cv.put("xtxh", fxc.getXtxh());
        cv.put("photos", fxc.getPhotos());
        //cv.put("scbj", fxc.getScbj());
        //cv.put("cwms", fxc.getCwms());
        long l = database.insert(MessageDbHelper.TABLE_FXC_JL_NAME, null, cv);
        return l;
    }

    public long insertFxcFile(String file, VioFxczfBean fxc) {
        ContentValues cv = new ContentValues();
        cv.put("fxc_id", fxc.getId());
        cv.put("wjdz", file);
        long l = database.insert(MessageDbHelper.TABLE_FXC_ZP_NAME, null, cv);
        return l;
    }

    /**
     * 获取指定行数的非现场列表
     *
     * @param maxRow
     * @return
     */
    public List<VioFxczfBean> getAllFxczf(int maxRow) {
        List<VioFxczfBean> list = new ArrayList<VioFxczfBean>();
        Cursor c = database.query(MessageDbHelper.TABLE_FXC_JL_NAME, null,
                null, null, null, null, "id desc");
        int row = 0;
        if (c.moveToFirst()) {
            do {
                VioFxczfBean fxc = getFxcFromCursor(c);
                list.add(fxc);
                row++;
                if (row >= maxRow)
                    break;
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<VioFxczfBean> getFxczfByScbj(String xszl, int maxRow) {
        String where = null;
        if (TextUtils.equals(xszl, "1"))
            where = "scbj=0";
        else if (TextUtils.equals(xszl, "2"))
            where = "scbj=1";
        List<VioFxczfBean> list = new ArrayList<VioFxczfBean>();
        Cursor c = database.query(MessageDbHelper.TABLE_FXC_JL_NAME, null,
                where, null, null, null, "id desc");
        int row = 0;
        if (c.moveToFirst()) {
            do {
                VioFxczfBean fxc = getFxcFromCursor(c);
                list.add(fxc);
                row++;
                if (row >= maxRow)
                    break;
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    /**
     * 根据ID号查询非现场执法数据
     *
     * @param id
     * @return
     */
    public VioFxczfBean queryFxczfById(String id) {
        String where = "id=" + id;
        Cursor c = database.query(MessageDbHelper.TABLE_FXC_JL_NAME, null,
                where, null, null, null, null);
        VioFxczfBean fxc = null;
        if (c.moveToFirst()) {
            fxc = getFxcFromCursor(c);
        }
        c.close();
        return fxc;
    }

//    public List<VioFxczfBean> getUnuploadFxczf(int maxRow) {
//        List<VioFxczfBean> list = new ArrayList<VioFxczfBean>();
//        Cursor c = database.query(MessageDbHelper.TABLE_FXC_JL_NAME, null,
//                "scbj=0", null, null, null, "id");
//        int row = 0;
//        if (c.moveToFirst()) {
//            do {
//                VioFxczfBean fxc = getFxcFromCursor(c);
//                list.add(fxc);
//                row++;
//                if (row >= maxRow)
//                    break;
//            } while (c.moveToNext());
//        }
//        c.close();
//        return list;
//    }

    /**
     * 获取非现场ID下的所有照片
     *
     * @param fid
     * @return
     */
    public List<VioFxcFileBean> queryFxczfFileByFId(String fid) {
        List<VioFxcFileBean> list = new ArrayList<VioFxcFileBean>();
        String where = "fxc_id=" + fid;
        Cursor c = database.query(MessageDbHelper.TABLE_FXC_ZP_NAME, null,
                where, null, null, null, "id");
        if (c.moveToFirst()) {
            do {
                VioFxcFileBean fxc = getFxcFileCursor(c);
                list.add(fxc);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    /**
     * 取出所有未上传照片的非现场ID
     *
     * @return
     */
    private List<String> queryAllUnUploadFxcIds() {
        String where = "scbj=0";
        String sql = "select fxc_id from " + MessageDbHelper.TABLE_FXC_ZP_NAME + " where scbj=0 group by fxc_id";
        Cursor c = database.rawQuery(sql, null);
        List<String> ids = new ArrayList<String>();
        if (c.moveToFirst()) {
            do {
                ids.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        return ids;
    }

    /**
     * 查询取所有未能完整上传非现场记录
     * 步骤为，首先取出所有未上传照片的非现场ID，然后到主表中比较，如果已上传则为不完整
     *
     * @return
     */
    public List<VioFxczfBean> queryAllUncompleteUploadFxczf() {
        List<VioFxczfBean> list = new ArrayList<VioFxczfBean>();
        List<String> ids = queryAllUnUploadFxcIds();
        if (ids != null && !ids.isEmpty()) {
            for (String id : ids) {
                VioFxczfBean fxc = queryFxczfById(id);
                if (TextUtils.equals("1", fxc.getScbj()))
                    list.add(fxc);
            }
        }
        return list;
    }


    /**
     * 查询未上传的图片
     *
     * @param fid
     * @return
     */
    public List<VioFxcFileBean> queryUnuploadFxczfFileByFId(String fid) {
        List<VioFxcFileBean> list = new ArrayList<VioFxcFileBean>();
        String where = "fxc_id=" + fid + " and scbj=0";
        Cursor c = database.query(MessageDbHelper.TABLE_FXC_ZP_NAME, null,
                where, null, null, null, "id");
        if (c.moveToFirst()) {
            do {
                VioFxcFileBean fxc = getFxcFileCursor(c);
                list.add(fxc);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public int queryUnUploadPhotoCount(String fid) {
        int row = 0;
        String sql = "select count(*) from "
                + MessageDbHelper.TABLE_FXC_ZP_NAME + " where scbj=0 and fxc_id=" + fid;
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            row = c.getInt(0);
        }
        return row;
    }

    private VioFxcFileBean getFxcFileCursor(Cursor c) {
        VioFxcFileBean ff = new VioFxcFileBean();
        ff.setId(c.getString(0));
        ff.setFxcId(c.getString(1));
        ff.setWjdz(c.getString(2));
        ff.setScbj(c.getString(3));
        return ff;
    }

    private VioFxczfBean getFxcFromCursor(Cursor rs) {
        VioFxczfBean fxc = new VioFxczfBean();
        fxc.setId(rs.getString(0));
        fxc.setCjjg(rs.getString(1));
        fxc.setHpzl(rs.getString(2));
        fxc.setHphm(rs.getString(3));
        fxc.setJtfs(rs.getString(4));
        fxc.setFzjg(rs.getString(5));
        fxc.setTzsh(rs.getString(6));
        fxc.setTzrq(rs.getString(7));
        fxc.setWfsj(rs.getString(8));
        fxc.setXzqh(rs.getString(9));
        fxc.setWfdd(rs.getString(10));
        fxc.setLddm(rs.getString(11));
        fxc.setDdms(rs.getString(12));
        fxc.setWfdz(rs.getString(13));
        fxc.setWfxw(rs.getString(14));
        fxc.setZqmj(rs.getString(15));
        fxc.setSbbh(rs.getString(16));
        fxc.setXtxh(rs.getString(17));
        fxc.setPhotos(rs.getString(18));
        fxc.setScbj(rs.getString(19));
        fxc.setCwms(rs.getString(20));
        return fxc;
    }

    /**
     * 删除一条记录
     *
     * @param id
     * @return
     */
    public int delFxczf(String id) {
        String where = "id=" + id;
        int l = database.delete(MessageDbHelper.TABLE_FXC_JL_NAME, where, null);
        l += database.delete(MessageDbHelper.TABLE_FXC_ZP_NAME, "fxc_id=" + id,
                null);
        return l;
    }

    /**
     * 获取通知书编号
     *
     * @return 单位编号+日期+警号+自编号
     */
    public String getTodayFxczfId() {
        int row = 1;
        //String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //String sql = "SELECT ifnull(max(id),0)+1 FROM  "
        //        + MessageDbHelper.TABLE_FXC_JL_NAME + " WHERE wfsj like '"
        //        + date + "%'";
        String sql = "SELECT ifnull(max(id),0)+1 FROM  "
                + MessageDbHelper.TABLE_FXC_JL_NAME;
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            row = c.getInt(0);
        }
        c.close();
        row += 1000;
        String sr = String.valueOf(row);
        String dwdm = GlobalData.grxx.get(GlobalConstant.YBMBH);
        String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        String tzsbh = dwdm.substring(0, 6) + date + jybh.substring(4)
                + sr.substring(sr.length() - 3);
        return tzsbh;
    }

    public String[] getLastWfdd() {
        String[] ar = null;
        String sql = "select xzqh ,wfdd ,lddm ,ddms,wfdz,wfxw from "
                + MessageDbHelper.TABLE_FXC_JL_NAME
                + " where id=(select ifnull(max(id),0) from "
                + MessageDbHelper.TABLE_FXC_JL_NAME + ")";
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            ar = new String[3];
            ar[0] = c.getString(0) + c.getString(1) + c.getString(2)
                    + c.getString(3);
            ar[1] = c.getString(4);
            ar[2] = c.getString(5);
        }
        c.close();
        return ar;
    }

    /**
     * 系统回传的编号
     *
     * @param id
     * @param xtbh
     */
    public void updateXtbhScbj(String id, String xtbh) {
        ContentValues cv = new ContentValues();
        cv.put("scbj", "1");
        cv.put("xtxh", xtbh);
        database.update(MessageDbHelper.TABLE_FXC_JL_NAME, cv, "id=" + id, null);
    }

    public void updateFxcUploaded(String id) {
        ContentValues cv = new ContentValues();
        cv.put("scbj", "1");
        database.update(MessageDbHelper.TABLE_FXC_JL_NAME, cv, "id=" + id, null);
    }

    public void setXtbhScbj(String xtbh, String scbj) {
        ContentValues cv = new ContentValues();
        cv.put("scbj", scbj);
        database.update(MessageDbHelper.TABLE_FXC_JL_NAME, cv, "xtxh='" + xtbh + "'", null);
    }

    /**
     * 设置照片上传标记
     *
     * @param id
     * @param bj
     */
    public void setPhotoBj(String id, String bj) {
        ContentValues cv = new ContentValues();
        cv.put("scbj", bj);
        database.update(MessageDbHelper.TABLE_FXC_ZP_NAME, cv, "id=" + id, null);
    }

}
