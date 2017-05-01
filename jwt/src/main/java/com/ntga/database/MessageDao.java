package com.ntga.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.ntga.bean.GcmBbInfoBean;
import com.ntga.bean.GcmBbddBean;
import com.ntga.bean.JqtbBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.SeriousStreetBean;
import com.ntga.bean.SpringDjItf;
import com.ntga.bean.SpringKcdjBean;
import com.ntga.bean.SpringWhpdjBean;
import com.ntga.bean.WfxwCllxCheckBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ydjw.pojo.UdpMessage;
import com.ydjw.pojo.UdpMessageUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageDao {
    private SQLiteDatabase wdb;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final int MESSAGE_VERION = 4;

    public MessageDao(Context context, int version) {
        MessageDbHelper db = new MessageDbHelper(context, version);
        wdb = db.getWritableDatabase();
    }

    public MessageDao(Context context) {
        int version = MESSAGE_VERION;
        try {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        MessageDbHelper db = new MessageDbHelper(context, version);
        wdb = db.getWritableDatabase();
    }

    public void closeDb() {
        wdb.close();
    }

    /**
     * 将收到的信息加入到数据表中
     *
     * @param sender
     * @param mess
     */
    public void insertMessageDb(String sender, String recive, String mess,
                                int fsbj) {
        ContentValues cv = new ContentValues();
        cv.put("sender", sender);
        cv.put("message", mess);
        cv.put("recive", recive);
        cv.put("rec_riqi", sdf.format(new Date()));
        cv.put("fsbj", fsbj);
        cv.put("recive", GlobalData.grxx.get(GlobalConstant.YHBH));
        wdb.insert(MessageDbHelper.TABLE_NAME, null, cv);
    }

    /**
     * 将联系人加入到数据库中
     *
     * @param users
     */
    public void insertUdpUserIntoDb(List<UdpMessageUser> users) {
        wdb.delete(MessageDbHelper.USER_TABLE_NAME, null, null);
        for (UdpMessageUser us : users) {
            ContentValues cv = new ContentValues();
            cv.put("jybh", us.getJybh());
            cv.put("xm", us.getXm());
            cv.put("dw", us.getDw());
            cv.put("jb", us.getJb());
            wdb.insert(MessageDbHelper.USER_TABLE_NAME, null, cv);
        }
    }

    /**
     * 根据条件查询联系人
     *
     * @param cond 空时查询所有联系人
     * @return
     */
    public List<UdpMessageUser> queryUdpUser(String cond) {
        List<UdpMessageUser> list = new ArrayList<UdpMessageUser>();
        String where = null;
        if (!TextUtils.isEmpty(cond))
            where = "xm like '%" + cond.trim() + "%' or jybh like '%"
                    + cond.trim() + "%' or dw like '%" + cond.trim() + "%'";
        Cursor c = wdb.query(MessageDbHelper.USER_TABLE_NAME, null, where,
                null, null, null, "jb desc");
        if (c.moveToFirst()) {
            do {
                UdpMessageUser user = new UdpMessageUser();
                user.setJybh(c.getString(1));
                user.setXm(c.getString(2));
                user.setDw(c.getString(3));
                user.setJb(c.getString(4));
                list.add(user);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    /**
     * 根据发件人查询信息明细
     *
     * @return
     */
    public List<UdpMessage> getAllMessages(String sender) {
        List<UdpMessage> list = new ArrayList<UdpMessage>();
        Cursor c = wdb.query(MessageDbHelper.TABLE_NAME, null, "sender='"
                + sender + "'", null, null, null, "id desc");
        if (c.moveToFirst()) {
            do {
                UdpMessage m = new UdpMessage();
                m.setId(c.getInt(0));
                m.setSender(c.getString(1));
                m.setRecive(c.getString(2));
                m.setMessage(c.getString(3));
                m.setRecRiqi(c.getString(4));
                m.setFsbj(c.getInt(5));
                m.setYdbj(c.getInt(6));
                list.add(m);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    /**
     * 查询收件箱分组
     *
     * @return
     */
    public List<String[]> queryGroupMess() {
        List<String[]> list = new ArrayList<String[]>();
        String sql = "select * from message m,(select max(id) mid,count(*) sl,sum(ydbj) yd from message group by sender) t where t.mid=m.id order by m.id desc";
        Cursor c = wdb.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                String[] temp = new String[10];
                for (int i = 0; i < 10; i++) {
                    temp[i] = c.getString(i);
                }
                list.add(temp);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public String getXmAndJybh(String jybh) {
        String xm = getUdpUserXmByJybh(jybh);
        if (!TextUtils.isEmpty(xm)) {
            return xm.trim() + "<" + jybh + ">";
        }
        return jybh;
    }

    /**
     * 区分显示发送者和接收者
     *
     * @param mes
     * @return
     */
    public String getXmAndJybhByMes(UdpMessage mes) {
        String jybh = mes.getSender();
        if (mes.getFsbj() == 1) {
            // 这是一条发送短信
            jybh = mes.getRecive();
        }
        return getXmAndJybh(jybh);
    }

    /**
     * 根据警员编号查姓名
     *
     * @param jybh
     * @return
     */
    private String getUdpUserXmByJybh(String jybh) {
        String xm = "";
        Cursor c = wdb.query(MessageDbHelper.USER_TABLE_NAME,
                new String[]{"xm"}, "jybh=?", new String[]{jybh}, null,
                null, null);
        if (c.moveToFirst())
            xm = c.getString(0);
        c.close();
        return xm;

    }

    /**
     * 标记信息已读
     *
     * @param sender
     */
    public void setSenderMessageRead(String sender) {
        ContentValues cv = new ContentValues();
        cv.put("ydbj", 1);
        wdb.update(MessageDbHelper.TABLE_NAME, cv, "sender='" + sender + "'",
                null);
    }

    /**
     * 删除整个会话，包括发送者和接收者
     *
     * @param sender
     */
    public void delMessageBySender(String sender) {
        wdb.delete(MessageDbHelper.TABLE_NAME, "sender=?",
                new String[]{sender});
    }

    public UdpMessageUser queryUserByJybh(String jybh) {
        UdpMessageUser user = null;
        Cursor c = wdb.query(MessageDbHelper.USER_TABLE_NAME, null, "jybh=?",
                new String[]{jybh}, null, null, null);
        if (c.moveToFirst()) {
            user = new UdpMessageUser();
            user.setJybh(c.getString(1));
            user.setXm(c.getString(2));
            user.setDw(c.getString(3));
            user.setJb(c.getString(4));
        }
        c.close();
        return user;
    }

    /**
     * 删除单条信息
     *
     * @param id
     */
    public void delMessageById(int id) {
        wdb.delete(MessageDbHelper.TABLE_NAME, "id=" + id, null);
    }

    private boolean isJqtbSaved(JqtbBean jqtb) {
        boolean isSaved = false;
        Cursor c = wdb.query(MessageDbHelper.JQTB_TABLE_NAME, null, "sysId='"
                        + jqtb.getSysId() + "' and force=" + jqtb.getForce(), null,
                null, null, "id desc");
        isSaved = c.moveToFirst();
        c.close();
        return isSaved;
    }

    /**
     * 保存警情况通报到数据库中
     *
     * @param jqtb
     * @return
     */
    public long saveJqtb(JqtbBean jqtb) {
        if (isJqtbSaved(jqtb))
            return 0;
        ContentValues cv = new ContentValues();
        cv.put("sysId", jqtb.getSysId());
        cv.put("title", jqtb.getTitle());
        cv.put("sender", jqtb.getSender());
        cv.put("content", jqtb.getContent());
        cv.put("sendDate", jqtb.getSendDate());
        cv.put("recDate", sdfShort.format(new Date()));
        cv.put("isFile", "0".equals(jqtb.getFileSize()) ? "0" : "1");
        cv.put("fileSize", jqtb.getFileSize());
        cv.put("fileCata", jqtb.getFileCata());
        cv.put("fileLocation", "");
        cv.put("force", jqtb.getForce());
        long i = wdb.insert(MessageDbHelper.JQTB_TABLE_NAME, null, cv);
        return i;
    }

    /**
     * 查询所有未删除通报
     *
     * @return
     */
    public List<JqtbBean> queryAllJqtb() {
        List<JqtbBean> list = new ArrayList<JqtbBean>();
        Cursor c = wdb.query(MessageDbHelper.JQTB_TABLE_NAME, null, "delBj=0",
                null, null, null, "id desc");
        if (c.moveToFirst()) {
            do {
                list.add(createJqtbByCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    private JqtbBean createJqtbByCursor(Cursor c) {
        JqtbBean jqtb = new JqtbBean();
        jqtb.setId(c.getString(0));
        jqtb.setSysId(c.getString(1));
        jqtb.setTitle(c.getString(2));
        jqtb.setSender(c.getString(3));
        jqtb.setContent(c.getString(4));
        jqtb.setSendDate(c.getString(5));
        jqtb.setRecDate(c.getString(6));
        jqtb.setIsFile(c.getString(7));
        jqtb.setFileSize(c.getString(8));
        jqtb.setFileCata(c.getString(9));
        jqtb.setFileLocation(c.getString(10));
        jqtb.setReadBj(c.getString(11));
        jqtb.setDelBj(c.getString(12));
        jqtb.setForce(c.getString(13));
        return jqtb;
    }

    /**
     * 将指定记录打上删除标记
     *
     * @param id
     */
    public void delJqtb(String id) {
        ContentValues cv = new ContentValues();
        cv.put("delBj", 1);
        wdb.update(MessageDbHelper.JQTB_TABLE_NAME, cv, "id=" + id, null);
    }

    /**
     * 将指定记录打上已读标记
     *
     * @param id
     */
    public void readJqtb(String id) {
        ContentValues cv = new ContentValues();
        cv.put("readBj", 1);
        wdb.update(MessageDbHelper.JQTB_TABLE_NAME, cv, "id=" + id, null);
    }

    public void saveFileLocation(String id, String fileLocation) {
        ContentValues cv = new ContentValues();
        cv.put("fileLocation", fileLocation);
        wdb.update(MessageDbHelper.JQTB_TABLE_NAME, cv, "id=" + id, null);
    }

    public JqtbBean getJqtbById(String id) {
        Cursor c = wdb.query(MessageDbHelper.JQTB_TABLE_NAME, null, "id=" + id,
                null, null, null, "id desc");
        JqtbBean jqtb = null;
        if (c.moveToFirst()) {
            jqtb = createJqtbByCursor(c);
        }
        c.close();
        return jqtb;
    }

    /**
     * 查询所有报备地点
     *
     * @return
     */
    public List<GcmBbddBean> getAllGcmBbdd() {
        List<GcmBbddBean> list = new ArrayList<GcmBbddBean>();
        Cursor c = wdb.query(MessageDbHelper.GCM_BBDD_TABLE_NAME, null, null,
                null, null, null, "id");
        if (c.moveToFirst()) {
            do {
                GcmBbddBean bbdd = new GcmBbddBean();
                bbdd.setId(c.getString(0));
                bbdd.setMc(c.getString(1));
                bbdd.setGl4(c.getString(2));
                bbdd.setGl5(c.getString(3));
                bbdd.setGxdw(c.getString(4));
                list.add(bbdd);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public String getGcmBbmc(String id) {
        String result = "";
        String where = "id='" + id + "'";
        Cursor c = wdb.query(MessageDbHelper.GCM_BBDD_TABLE_NAME,
                new String[]{"mc"}, where, null, null, null, "id");
        if (c.moveToFirst()) {
            result = c.getString(0);
        }
        c.close();
        return result;
    }

    /**
     * 删除所有报备地点
     *
     * @return
     */
    public int delAllGcmBbdd() {
        int row = wdb.delete(MessageDbHelper.GCM_BBDD_TABLE_NAME, null, null);
        return row;
    }

    /**
     * 加入一条报备地点
     *
     * @param bbdd
     * @return
     */
    public long insertGcmBbdd(GcmBbddBean bbdd) {
        ContentValues cv = new ContentValues();
        cv.put("id", bbdd.getId());
        cv.put("mc", bbdd.getMc());
        cv.put("gl4", bbdd.getGl4());
        cv.put("gl5", bbdd.getGl5());
        cv.put("gxdw", bbdd.getGxdw());
        long row = wdb.insert(MessageDbHelper.GCM_BBDD_TABLE_NAME, null, cv);
        return row;
    }

    /**
     * 获取所有报备信息
     *
     * @return
     */
    public List<GcmBbInfoBean> getAllGcmBbInfo() {
        List<GcmBbInfoBean> list = new ArrayList<GcmBbInfoBean>();
        Cursor c = wdb.query(MessageDbHelper.GCM_BBINFO_TABLE_NAME, null, null,
                null, null, null, "id desc");
        if (c.moveToFirst()) {
            do {
                GcmBbInfoBean bbInfo = new GcmBbInfoBean();
                bbInfo.setId(c.getString(0));
                bbInfo.setJybh(c.getString(1));
                bbInfo.setGpsId(c.getString(2));
                bbInfo.setBbmc(c.getString(3));
                bbInfo.setFjrs(c.getString(4));
                bbInfo.setKssj(c.getString(5));
                bbInfo.setLxfs(c.getString(6));
                bbInfo.setLxhm(c.getString(7));
                bbInfo.setDjsj(c.getString(8));
                bbInfo.setScbj(c.getString(9));
                list.add(bbInfo);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public long insertGcmBbInfo(GcmBbInfoBean bbInfo) {
        ContentValues cv = new ContentValues();
        // cv.put("id", bbInfo.getId());
        cv.put("jybh", bbInfo.getJybh());
        cv.put("bbmc", bbInfo.getBbmc());
        cv.put("fjrs", bbInfo.getFjrs());
        cv.put("kssj", bbInfo.getKssj());
        cv.put("gps_id", bbInfo.getGpsId());
        cv.put("lxfs", bbInfo.getLxfs());
        cv.put("lxhm", bbInfo.getLxhm());
        // cv.put("scbj", bbInfo.getScbj());
        long row = wdb.insert(MessageDbHelper.GCM_BBINFO_TABLE_NAME, null, cv);
        return row;
    }

    public int delGcmBbInfoById(String id) {
        String where = "id =" + id;
        int row = wdb
                .delete(MessageDbHelper.GCM_BBINFO_TABLE_NAME, where, null);
        return row;
    }

    public int updateGcmBdInfoScbj(String id) {
        ContentValues cv = new ContentValues();
        cv.put("scbj", 1);
        String where = "id =" + id;
        int row = wdb.update(MessageDbHelper.GCM_BBINFO_TABLE_NAME, cv, where,
                null);
        return row;
    }

    public long insertZapcWpxxAdd(int xlpcwpbh, String clpp, String syr,
                                  String sfzmhm) {
        ContentValues cv = new ContentValues();
        cv.put("xlpcwpbh", xlpcwpbh);
        cv.put("clpp", clpp);
        cv.put("syr", syr);
        cv.put("sfzmhm", sfzmhm);
        return wdb.insert(MessageDbHelper.ZAPC_WPXX_JDC_ADD, null, cv);
    }

    public String[] getZapcWpxxAdd(String xlpcwpbh) {
        String where = "xlpcwpbh=" + xlpcwpbh;
        Cursor c = wdb.query(MessageDbHelper.ZAPC_WPXX_JDC_ADD, null, where,
                null, null, null, null);
        String[] result = null;
        if (c.moveToFirst()) {
            result = new String[3];
            result[0] = c.getString(1);
            result[1] = c.getString(2);
            result[2] = c.getString(3);
        }
        c.close();
        return result;
    }

    /**
     * 客车登记加入数据库
     *
     * @param kcdj
     * @return
     */
    public long insertSpringKcdj(SpringKcdjBean kcdj) {
        ContentValues cv = new ContentValues();
        cv.put("jcdd", kcdj.getJcdd());
        cv.put("hpzl", kcdj.getHpzl());
        cv.put("hphm", kcdj.getHphm());
        cv.put("cllx", kcdj.getCllx());
        cv.put("hzrs", kcdj.getHzrs());
        cv.put("szrs", kcdj.getSzrs());
        cv.put("dsr", kcdj.getDsr());
        cv.put("dabh", kcdj.getDabh());
        cv.put("sfzh", kcdj.getSfzh());
        cv.put("jcsj", kcdj.getJcsj());
        cv.put("lxjssj", kcdj.getLxjssj());
        cv.put("jszsyqk", kcdj.getJszsyqk());
        cv.put("cljyqk", kcdj.getCljyqk());
        cv.put("wfxw", kcdj.getWfxw());
        cv.put("wfcljg", kcdj.getWfcljg());
        cv.put("djjg", kcdj.getDjjg());
        cv.put("zqmj", kcdj.getZqmj());
        cv.put("gxsj", kcdj.getGxsj());
        cv.put("scbj", 0);
        long r = wdb.insert(MessageDbHelper.SPRING_KCDJ_TABLE_NAME, null, cv);
        Log.e("MessageDao", "insertSpringKcdj:" + r);
        return r;
    }

    /**
     * 危化品登记进库
     *
     * @param whpdj
     * @return
     */
    public long insertSpringWhpdj(SpringWhpdjBean whpdj) {
        ContentValues cv = new ContentValues();
        cv.put("jcdd", whpdj.getJcdd());
        cv.put("hpzl", whpdj.getHpzl());
        cv.put("hphm", whpdj.getHphm());
        cv.put("cllx", whpdj.getCllx());
        cv.put("hzzl", whpdj.getHzzl());
        cv.put("szzl", whpdj.getSzzl());
        cv.put("zzwpmc", whpdj.getZzwpmc());
        cv.put("dsr", whpdj.getDsr());
        cv.put("dabh", whpdj.getDabh());
        cv.put("sfzh", whpdj.getSfzh());
        cv.put("jcsj", whpdj.getJcsj());
        cv.put("yyryqk", whpdj.getYyryqk());
        cv.put("claqss", whpdj.getClaqss());
        cv.put("jszsyqk", whpdj.getJszsyqk());
        cv.put("cljyqk", whpdj.getCljyqk());
        cv.put("wfxw", whpdj.getWfxw());
        cv.put("wfcljg", whpdj.getWfcljg());
        cv.put("djjg", whpdj.getDjjg());
        cv.put("zqmj", whpdj.getZqmj());
        cv.put("gxsj", whpdj.getGxsj());
        cv.put("scbj", 0);
        return wdb.insert(MessageDbHelper.SPRING_WHPDJ_TABLE_NAME, null, cv);
    }

    public SpringKcdjBean queryKcdjById(String id) {
        SpringKcdjBean re = null;
        String where = "id=" + id + "";
        Cursor c = wdb.query(MessageDbHelper.SPRING_KCDJ_TABLE_NAME, null,
                where, null, null, null, "id");
        if (c.moveToFirst()) {
            re = createKcdjByCursor(c);
        }
        c.close();
        return re;
    }

    public SpringWhpdjBean queryWhpdjById(String id) {
        SpringWhpdjBean re = null;
        String where = "id=" + id + "";
        Cursor c = wdb.query(MessageDbHelper.SPRING_WHPDJ_TABLE_NAME, null,
                where, null, null, null, "id");
        if (c.moveToFirst()) {
            re = createWhpdjByCursor(c);
        }
        c.close();
        return re;
    }

    private SpringKcdjBean createKcdjByCursor(Cursor c) {
        SpringKcdjBean kcdj = new SpringKcdjBean();
        kcdj.setId(c.getString(0));
        kcdj.setJcdd(c.getString(1));
        kcdj.setHpzl(c.getString(2));
        kcdj.setHphm(c.getString(3));
        kcdj.setCllx(c.getString(4));
        kcdj.setHzrs(c.getString(5));
        kcdj.setSzrs(c.getString(6));
        kcdj.setDsr(c.getString(7));
        kcdj.setDabh(c.getString(8));
        kcdj.setSfzh(c.getString(9));
        kcdj.setJcsj(c.getString(10));
        kcdj.setLxjssj(c.getString(11));
        kcdj.setJszsyqk(c.getString(12));
        kcdj.setCljyqk(c.getString(13));
        kcdj.setWfxw(c.getString(14));
        kcdj.setWfcljg(c.getString(15));
        kcdj.setDjjg(c.getString(16));
        kcdj.setZqmj(c.getString(17));
        kcdj.setGxsj(c.getString(18));
        kcdj.setScbj(c.getInt(19));
        return kcdj;
    }

    private SpringWhpdjBean createWhpdjByCursor(Cursor c) {
        SpringWhpdjBean whpdj = new SpringWhpdjBean();
        whpdj.setId(c.getString(0));
        whpdj.setJcdd(c.getString(1));
        whpdj.setHpzl(c.getString(2));
        whpdj.setHphm(c.getString(3));
        whpdj.setCllx(c.getString(4));
        whpdj.setHzzl(c.getString(5));
        whpdj.setSzzl(c.getString(6));
        whpdj.setZzwpmc(c.getString(7));
        whpdj.setDsr(c.getString(8));
        whpdj.setDabh(c.getString(9));
        whpdj.setSfzh(c.getString(10));
        whpdj.setJcsj(c.getString(11));
        whpdj.setYyryqk(c.getString(12));
        whpdj.setClaqss(c.getString(13));
        whpdj.setJszsyqk(c.getString(14));
        whpdj.setCljyqk(c.getString(15));
        whpdj.setWfxw(c.getString(16));
        whpdj.setWfcljg(c.getString(17));
        whpdj.setDjjg(c.getString(18));
        whpdj.setZqmj(c.getString(19));
        whpdj.setGxsj(c.getString(20));
        whpdj.setScbj(c.getInt(21));
        return whpdj;
    }

    public void delKcdjById(String id) {
        wdb.delete(MessageDbHelper.SPRING_KCDJ_TABLE_NAME, "id=" + id, null);
    }

    /**
     * 删除指定的违法行为和车辆类型
     *
     * @param wfxw
     */
    public void delWfxwCllx(String wfxw) {
        wdb.delete(MessageDbHelper.TABLE_WFXW_CLLX, "wfxw='" + wfxw + "'", null);
    }

    /**
     * 加入
     *
     * @param wc
     * @return
     */
    public int addWfxwCllx(WfxwCllxCheckBean wc) {
        int row = 0;
        String aw = wc.getAlCllx();
        String dw = wc.getDeCllx();
        if (!TextUtils.isEmpty(aw)) {
            String[] aws = aw.split(",");
            if (aws != null && aws.length > 0) {
                for (String s : aws) {
                    ContentValues cv = new ContentValues();
                    cv.put("wfxw", wc.getWfxw());
                    cv.put("cllx", s);
                    cv.put("lx", 1);
                    cv.put("ms", wc.getMs());
                    long id = wdb.insert(MessageDbHelper.TABLE_WFXW_CLLX, null,
                            cv);
                    row += id > -1 ? 1 : 0;
                }
            }
            Log.e("addWfxwCllx aw", "add " + row);
        }
        if (!TextUtils.isEmpty(dw)) {
            String[] dws = dw.split(",");
            if (dws != null && dws.length > 0) {
                for (String s : dws) {
                    ContentValues cv = new ContentValues();
                    cv.put("wfxw", wc.getWfxw());
                    cv.put("cllx", s);
                    cv.put("lx", 0);
                    cv.put("ms", wc.getMs());
                    long id = wdb.insert(MessageDbHelper.TABLE_WFXW_CLLX, null,
                            cv);
                    row += id > -1 ? 1 : 0;
                }
            }
            Log.e("addWfxwCllx dw", "add " + row);
        }
        return row;
    }

    public String checkWfxwCllx(String wfxw, String cllx) {
        Log.e("message dao", wfxw + "/" + cllx);
        String where1 = "wfxw=? and cllx=? and lx=1";
        String where0 = "wfxw=? and cllx=? and lx=0";
        Cursor c = wdb.query(MessageDbHelper.TABLE_WFXW_CLLX,
                new String[]{"ms"}, where1, new String[]{wfxw, cllx},
                null, null, null);
        // 规则：允许中有则通过，先允许后否定。
        // 否定中有则不通过
        String ms = "";
        if (c.moveToFirst()) {
            c.close();
            return "";
        } else {
            Log.e("addWfxwCllx aw", "ms " + ms);
        }
        c.close();
        Cursor c0 = wdb.query(MessageDbHelper.TABLE_WFXW_CLLX,
                new String[]{"ms"}, where0, new String[]{wfxw, cllx},
                null, null, null);
        if (c0.moveToFirst()) {
            ms = c0.getString(0);
            c0.close();
            return ms;
        }
        c0.close();
        return null;

    }

    public void delWhpdjById(String id) {
        wdb.delete(MessageDbHelper.SPRING_WHPDJ_TABLE_NAME, "id=" + id, null);
    }

    public List<SpringDjItf> getAllKcdj() {
        List<SpringDjItf> list = new ArrayList<SpringDjItf>();
        Cursor c = wdb.query(MessageDbHelper.SPRING_KCDJ_TABLE_NAME, null,
                null, null, null, null, "id desc");
        if (c.moveToFirst()) {
            do {
                list.add(createKcdjByCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<SpringDjItf> getAllWhpdj() {
        List<SpringDjItf> list = new ArrayList<SpringDjItf>();
        Cursor c = wdb.query(MessageDbHelper.SPRING_WHPDJ_TABLE_NAME, null,
                null, null, null, null, "id desc");
        if (c.moveToFirst()) {
            do {
                list.add(createWhpdjByCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public String getLastVioWfdd(int djlx) {
        String jcdd = "";
        String table = djlx == 0 ? MessageDbHelper.SPRING_KCDJ_TABLE_NAME
                : MessageDbHelper.SPRING_WHPDJ_TABLE_NAME;
        String sql = "SELECT jcdd FROM " + table
                + " WHERE id=(SELECT max(id) FROM " + table + ")";
        Cursor cs = wdb.rawQuery(sql, null);
        if (cs.moveToFirst()) {
            jcdd = cs.getString(0);
        }
        cs.close();
        return jcdd;
    }

    public int updateSpringScbj(String id, int djlx) {
        ContentValues cv = new ContentValues();
        cv.put("scbj", 1);
        String where = "id =" + id;
        int row = wdb.update(djlx == 0 ? MessageDbHelper.SPRING_KCDJ_TABLE_NAME
                : MessageDbHelper.SPRING_WHPDJ_TABLE_NAME, cv, where, null);
        return row;
    }

    public int delAllQymc() {
        return wdb.delete(MessageDbHelper.TRUCK_QYMC, null, null);
    }

    public int addAllQymc(List<KeyValueBean> kvs) {
        int row = 0;
        ContentValues cv = new ContentValues();
        for (KeyValueBean kv : kvs) {
            cv.put("qybh", kv.getKey());
            cv.put("qymc", kv.getValue());
            row += wdb.insert(MessageDbHelper.TRUCK_QYMC, null, cv);
        }
        return row;
    }

    public int getQymcCount() {
        int row = 0;
        Cursor cs = wdb.rawQuery("select count(*) from "
                + MessageDbHelper.TRUCK_QYMC, null);
        if (cs.moveToFirst()) {
            row = cs.getInt(0);
        }
        cs.close();
        return row;
    }

    public KeyValueBean queryQymcByBy(String bh) {
        String where = "qybh='" + bh + "'";
        Cursor c = wdb.query(MessageDbHelper.TRUCK_QYMC, null, where, null,
                null, null, null);
        if (c != null && c.moveToFirst()) {
            KeyValueBean kv = new KeyValueBean(c.getString(0), c.getString(1));
            c.close();
            return kv;
        }
        return null;
    }

    public List<KeyValueBean> queryQymc(String mc) {
        List<KeyValueBean> list = new ArrayList<KeyValueBean>();
        String where = "qymc like '%" + mc + "%'";
        Cursor c = wdb.query(MessageDbHelper.TRUCK_QYMC, null, where, null,
                null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                KeyValueBean kv = new KeyValueBean(c.getString(0),
                        c.getString(1));
                list.add(kv);
                // Log.e("MessageDao", kv.getKey() + "/" + kv.getValue());
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    /**
     * 查询严管路段版本号，判断是否需要升级
     *
     * @return
     */
    public int getSeriousVersion() {
        int version = 0;
        Cursor cs = wdb.rawQuery("select max(version) from "
                + MessageDbHelper.TABLE_SERIOUS_STREET_NAME, null);
        if (cs.moveToFirst()) {
            version = cs.getInt(0);
        }
        cs.close();
        return version;
    }

    /**
     * 将严管路段写入到表中
     *
     * @param sbs
     * @return
     */
    public int addSeriousList(List<SeriousStreetBean> sbs) {
        int row = 0;
        for (SeriousStreetBean sb : sbs) {
            ContentValues cv = new ContentValues();
            cv.put("wfdd", sb.getWfdd());
            cv.put("n_wfxw", sb.getNwfxw());
            cv.put("version", sb.getVersion());
            long id = wdb.insert(MessageDbHelper.TABLE_SERIOUS_STREET_NAME, null,
                    cv);
            row += id > -1 ? 1 : 0;
        }
        return row;
    }

    /**
     * 删除所有严管路段
     *
     * @return
     */
    public int delAllSerious() {
        int l = wdb.delete(MessageDbHelper.TABLE_SERIOUS_STREET_NAME, null, null);
        return l;
    }

    /**
     * 是否为严管路段违法行为
     *
     * @param wfdd
     * @return 真 不可以拍照，否 可以拍照
     */
    public boolean checkIsSeriousStreet(String wfdd) {
        String sql = "select * from " + MessageDbHelper.TABLE_SERIOUS_STREET_NAME + " where wfdd='"
                + wfdd + "'";
        Cursor cs = wdb.rawQuery(sql, null);
        boolean isSer = cs.moveToFirst();
        cs.close();
        return isSer;

    }

}
