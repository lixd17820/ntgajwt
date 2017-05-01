package com.ntga.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MessageDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "jwt_message.db";
    public static final String TABLE_NAME = "message";
    public static final String USER_TABLE_NAME = "udpuser";
    public static final String JQTB_TABLE_NAME = "jqtb";
    public static final String GCM_BBDD_TABLE_NAME = "bbdd";
    public static final String GCM_BBINFO_TABLE_NAME = "bbinfo";
    public static final String SPRING_KCDJ_TABLE_NAME = "t_jwt_spring_kcdj";

    public static final String SPRING_WHPDJ_TABLE_NAME = "t_jwt_spring_whpdj";

    public static final String TRUCK_QYMC = "truck_qymc";

    public static final String TABLE_WFXW_CLLX = "t_wfxw_cllx";

    public static final String TABLE_FXC_JL_NAME = "fxc_jl";
    public static final String TABLE_FXC_ZP_NAME = "fxc_zp";

    public static final String TABLE_SERIOUS_STREET_NAME = "serious_street";

    /**
     * 加入治安盘查外挂信息
     */
    public static final String ZAPC_WPXX_JDC_ADD = "wpxx_add";
    private String TAG = this.getClass().getName();

    private Context self;

    public MessageDbHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        self = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_FXC_JL_NAME
                + " (id integer primary key,cjjg text,hpzl text,hphm text,jtfs text,"
                + "fzjg text,tzsh text,tzrq text,wfsj text,xzqh text,wfdd text,lddm text,"
                + "ddms text,wfdz text,wfxw text,zqmj text,sbbh text,xtxh text,photos integer default 0,"
                + "scbj integer default 0, cwms text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_FXC_ZP_NAME
                + "(id integer primary key,fxc_id integer,wjdz text,scbj integer default 0)");
        db.execSQL("CREATE TABLE "
                + TABLE_NAME
                + " (id INTEGER PRIMARY KEY, sender text,recive text,message text,rec_riqi text,fsbj int,ydbj integer default 0)");

        db.execSQL("create table "
                + USER_TABLE_NAME
                + "(id integer primary key, jybh text,xm text, dw text, jb text)");
        db.execSQL("create table "
                + JQTB_TABLE_NAME
                + " (id integer primary key, sysId text,title text,sender text,"
                + "content text,sendDate text,recDate text,isFile text,fileSize text,"
                + "fileCata text,fileLocation text,readBj integer default 0,"
                + "delBj integer default 0, force integer default 0)");
        db.execSQL("create table " + GCM_BBDD_TABLE_NAME
                + "(id text primary key,"
                + "mc text,gl4 text,gl5 text,gxdw text)");
        db.execSQL("create table " + GCM_BBINFO_TABLE_NAME
                + "(id integer primary key,"
                + "jybh text,gps_id text,bbmc text,fjrs text,kssj text,"
                + "lxfs text,lxhm text,djsj text,scbj integer default 0)");
        db.execSQL("create table " + ZAPC_WPXX_JDC_ADD
                + "(xlpcwpbh integer primary key,"
                + "clpp text,syr text,sfzmhm text)");

        db.execSQL("create table "
                + SPRING_KCDJ_TABLE_NAME
                + "(id integer primary key,jcdd text,"
                + "hpzl text,hphm text,cllx text,hzrs text,szrs text,dsr text,"
                + "dabh text,sfzh text,jcsj text,lxjssj text,jszsyqk text,"
                + "cljyqk text,wfxw text,wfcljg text,djjg text,zqmj text,gxsj text,scbj integer)");
        db.execSQL("create table " + TABLE_WFXW_CLLX
                + " (id integer primary key,wfxw text,cllx text,"
                + "lx integer,ms text)");
        db.execSQL("create table "
                + SPRING_WHPDJ_TABLE_NAME
                + "(id integer primary key,jcdd text,"
                + "hpzl text,hphm text,cllx text,hzzl text,"
                + "szzl text,zzwpmc text,dsr text,dabh text,sfzh text,jcsj text,"
                + "yyryqk text,claqss text,jszsyqk text,cljyqk text,wfxw text,"
                + "wfcljg text,djjg text,zqmj text,gxsj text,scbj integer)");
        db.execSQL("create table " + TRUCK_QYMC + " (qybh text,qymc text)");
        //创建严管路段记录表
        db.execSQL("create table " + TABLE_SERIOUS_STREET_NAME + "(id integer primary key, wfdd text,n_wfxw text,version integer)");
        AssetManager am = self.getAssets();
        try {
            InputStream in = am.open("qymc.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in,
                    "utf-8"));
            String s = null;
            while ((s = br.readLine()) != null) {
                String[] as = s.split(",");
                db.execSQL("insert into " + TRUCK_QYMC + " values('" + as[0]
                        + "','" + as[1] + "')");
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion <= oldVersion)
            return;
        Log.e(TAG, "Jwt main Database is updateing, old version: " + oldVersion
                + ", new Version: " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + JQTB_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GCM_BBDD_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GCM_BBINFO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ZAPC_WPXX_JDC_ADD);
        db.execSQL("DROP TABLE IF EXISTS " + SPRING_KCDJ_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SPRING_WHPDJ_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRUCK_QYMC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WFXW_CLLX);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERIOUS_STREET_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_FXC_JL_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_FXC_ZP_NAME);
        onCreate(db);
    }

}
