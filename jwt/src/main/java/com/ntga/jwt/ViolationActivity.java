package com.ntga.jwt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.provider.fixcode.Fixcode;
import com.android.provider.wfdmcode.Wfdmcode;
import com.ntga.adaper.OnSpinnerItemSelected;
import com.ntga.bean.JdsPrintBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.THmb;
import com.ntga.bean.VioDrvBean;
import com.ntga.bean.VioVehBean;
import com.ntga.bean.VioViolation;
import com.ntga.bean.WebQueryResult;
import com.ntga.bean.WfdmBean;
import com.ntga.dao.BlueToothPrint;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.GlobalSystemParam;
import com.ntga.dao.PrintJdsTools;
import com.ntga.dao.UploadViolationHandler;
import com.ntga.dao.ViolationDAO;
import com.ntga.dao.WfddDao;
import com.ntga.dao.WsglDAO;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class ViolationActivity extends ActionBarActivity {

    private static final String TAG = "ViolationActivity";
    private ContentResolver resolver;
    private Context self;
    // private int cfCatalog;

    // protected SimpleDateFormat sdf = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm");

    // 打开子页面的请求码
    public static final int REQCODE_WFDD = 10;
    public static final int REQCODE_WFXW = 11;
    public static final int REQCODE_JTFS = 12;
    public static final int REQCODE_PREVIEW_JDS = 13;
    public static final int REQCODE_FZJG = 14;
    // 快捷菜单码
    public static final int MENU_WFXW_ADD = 20;
    public static final int MENU_WFXW_MOD = 21;

    // 信息查询种类
    public static final int QUERY_DRV_INFO = 30;
    public static final int QUERY_VEH_INFO = 31;

    private static final int SAVE_VIO_HANDLER = 40;
    private static final int QUERY_DRV_HANDLER = 41;
    private static final int QUERY_VEH_HANDLER = 42;

    // 车辆分类常量
    // private static final int CLFL_FJDC = 1;
    // private static final int CLFL_WPWZJDC = 2;
    // private static final int CLFL_GAPZJDC = 3;
    // private static final int CF = 1000;
    // private static final int QZ = 1001;
    private static final int ERROR = -1;

    //
    // public static final int QWWFJG = Integer.valueOf(GlobalConstant.QWWFJG);
    // public static final int JYCFJDS =
    // Integer.valueOf(GlobalConstant.JYCFJDS);
    // public static final int QZCSPZ = Integer.valueOf(GlobalConstant.QZCSPZ);
    // public static final int WFTZD = Integer.valueOf(GlobalConstant.WFTZD);

    private Spinner spRyfl, spHpzl, spJtfs, spHpql, spClfl, spWfdd, spZzmm, spZyxx, spSyxz;
    // , spJkfs,edAllWfxws,
    protected TextView textWfxwms, textJdsbh;
    private EditText edFzjgms, edWfsj, edDabh, edXm, edJszh, edLxdz, edZjcx,
            edLxdh, edHphm, edLjjf;
    protected EditText edWfxw, edBzz, edScz;
    private Button btnQueryDriver, btnQueryVeh, btnFzjg;

    // 用于储存变量
    protected KeyValueBean printerInfo;


    // private ArrayList<KeyValueBean> cityList;
    // private ArrayList<WfdmBean> allWfxws = new ArrayList<WfdmBean>();
    // private TreeMap<Integer, Boolean> qzcsMap = new TreeMap<Integer,
    // Boolean>();
    protected BlueToothPrint btp = null;

    // 界面元素是否已成功保存到数据库中

    // 用于保存处罚决定书

    protected THmb jdsbh;

    protected boolean isViolationSaved = false;

    protected VioViolation violation;

    protected String zqmj;

    private final String STATE_IS_SAVE = "isViolationSaved";
    private final String STATE_VIOLATION = "violation";
    private final String JDSBH = "jdsbh";
    private final String CFZL = "cfzl";
    private final String WSLB = "wslb";

    protected String wslb, cfzl;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_SAVE, isViolationSaved);
        if (violation != null)
            outState.putSerializable(STATE_VIOLATION, violation);
        if (jdsbh != null)
            outState.putSerializable(JDSBH, jdsbh);
        if (cfzl != null) {
            outState.putString(CFZL, cfzl);
        }
        if (wslb != null) {
            outState.putString(WSLB, wslb);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle ss) {
        super.onRestoreInstanceState(ss);
        if (ss.containsKey(STATE_IS_SAVE))
            isViolationSaved = ss.getBoolean(STATE_IS_SAVE);
        if (ss.containsKey(STATE_VIOLATION))
            violation = (VioViolation) ss.getSerializable(STATE_VIOLATION);
        if (ss.containsKey(JDSBH))
            jdsbh = (THmb) ss.getSerializable(JDSBH);
        if (ss.containsKey(CFZL))
            cfzl = ss.getString(CFZL);
        if (ss.containsKey(WSLB))
            wslb = ss.getString(WSLB);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "VIOLATION ON CREATE");
        // 常量赋值
        resolver = this.getContentResolver();
        self = this;
        zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);

        setContentView(R.layout.jbyw_violation_r);
        findViewById(R.id.unless_linear).requestFocus();

        // 取出处罚文书类别用于检测逻辑
        // String s = getIntent().getStringExtra("cfCatalog");
        // if (s != null && TextUtils.isDigitsOnly(s)) {
        // cfCatalog = Integer.valueOf(s);
        // } else {
        // finish();
        // }
        // 初始化控件
        findView();
        // 由上一处罚取得
        VioViolation preViolation = (VioViolation) getIntent()
                .getSerializableExtra("violation");

        // 新的处罚则设默认值
        if (preViolation == null)
            preViolation = setDefaultVio();

        setViewDefaultValue(preViolation);

        // 取得当前编号,简易程序和警告是一种,强制和通知单是一种
        // 改变控件的状态
        // if (getCfOrQz() == CF) {
        // jdsbh = WsglDAO.getCurrentJdsbh(GlobalConstant.JYCFJDS, zqmj,
        // resolver);
        // // 警告则缴款方式不要变
        // if (cfCatalog == GlobalConstant.QWWFJG) {
        // GlobalMethod.changeSpinnerSelect(spJkfs, "0",
        // GlobalConstant.KEY);
        // spJkfs.setEnabled(false);
        // } else {
        // GlobalMethod.changeSpinnerSelect(spJkfs, "2",
        // GlobalConstant.KEY);
        // }
        // } else if (getCfOrQz() == QZ) {
        // jdsbh = WsglDAO.getCurrentJdsbh(GlobalConstant.QZCSPZ, zqmj,
        // resolver);
        // // 移除处罚时间和缴款方式控件
        // RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_jycx);
        // rl.removeAllViewsInLayout();
        // } else {
        // finish();
        // }
        // 初始化标题栏

        // String title = getIntent().getStringExtra("title");

        // 设置打印的名字，打印时在数据库中取
        printerInfo = new KeyValueBean(
                GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME),
                GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS));
        //if (!TextUtils.isEmpty(printerInfo.getValue())) {
        //   btp = new BlueToothPrint(printerInfo.getValue());
        //}

        // 注册右键快捷菜单
        // registerForContextMenu(edWfxw);
        // registerForContextMenu(edAllWfxws);
        // 设置监听
        setListeners();
        //
    }

    /**
     * 退出界面的事件
     */
    OnClickListener exitSystem = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    /**
     * 初始化界面元素
     */
    protected void findView() {
        // mTitle = (TextView) findViewById(R.id.title_left_text);
        // mTitle_r = (TextView) findViewById(R.id.title_right_text);
        // 初始化全局数据字典表
        if (!GlobalData.isInitLoadData)
            GlobalData.initGlobalData(resolver);
        textJdsbh = (TextView) findViewById(R.id.tv_jdsbh);
        spRyfl = (Spinner) findViewById(R.id.Sp_ryfl);
        spHpzl = (Spinner) findViewById(R.id.Sp_hpzl);
        spJtfs = (Spinner) findViewById(R.id.Sp_cllx);

        spClfl = (Spinner) findViewById(R.id.Sp_clfl);
        spHpql = (Spinner) findViewById(R.id.Sp_hpql);
        edFzjgms = (EditText) findViewById(R.id.Edit_fzjgms);
        spWfdd = (Spinner) findViewById(R.id.sp_wfdd);
        edWfxw = (EditText) findViewById(R.id.Edit_wfxw);
        textWfxwms = (TextView) findViewById(R.id.Text_wfxwms);
        edWfsj = (EditText) findViewById(R.id.Edit_wfsj);
        // 人员基本信息控件
        edDabh = (EditText) findViewById(R.id.Edit_dabh);
        // ----------------------------------------------------------------
        // edDabh.setText("320600075015");

        edXm = (EditText) findViewById(R.id.Edit_xm);
        edJszh = (EditText) findViewById(R.id.Edit_jszh);
        edLxdz = (EditText) findViewById(R.id.Edit_lxdz);
        edZjcx = (EditText) findViewById(R.id.Edit_zjcx);
        edLxdh = (EditText) findViewById(R.id.Edit_lddh);
        edLjjf = (EditText) findViewById(R.id.Edit_ljjf);
        // 累计记分只能是查询的结果，不能修改
        edLjjf.setKeyListener(null);
        // 车辆信息控件
        edHphm = (EditText) findViewById(R.id.Edit_hphm);
        //标准值和实测值
        edBzz = (EditText) findViewById(R.id.edit_bzz);
        edScz = (EditText) findViewById(R.id.edit_scz);
        spZzmm = (Spinner) findViewById(R.id.sp_zzmm);
        spZyxx = (Spinner) findViewById(R.id.sp_zyxx);
        spSyxz = (Spinner) findViewById(R.id.sp_syxz);
        // 以下为固定的字典表，城市列表不在其中
        // 设置人员分类适配器
        GlobalMethod.changeAdapter(spRyfl, GlobalData.ryflList, this);

        // 设置号牌种类
        GlobalMethod.changeAdapter(spHpzl, GlobalData.hpzlList, this, true);

        // 设置号牌前辍
        GlobalMethod.changeAdapter(spHpql, GlobalData.hpqlList, this, true);
        // 设置车辆分类
        GlobalMethod.changeAdapter(spClfl, GlobalData.clflList, this);
        //政治面貌
        GlobalMethod.changeAdapter(spZzmm, GlobalData.zzmmList, this, true);
        //职业信息
        GlobalMethod.changeAdapter(spZyxx, GlobalData.zyxxList, this, true);
        //使用性质
        GlobalMethod.changeAdapter(spSyxz, GlobalData.syxzList, this, true);

        btnQueryDriver = (Button) findViewById(R.id.But_query_drv);
        btnQueryVeh = (Button) findViewById(R.id.But_clbd);
        btnFzjg = (Button) findViewById(R.id.btn_fzjg);
        // spRyfl.setFocusable(true);
        // spRyfl.setFocusableInTouchMode(true);
        // spRyfl.requestFocus();
    }

    /**
     * 设置控件的默认值
     */
    private void setViewDefaultValue(VioViolation v) {
        // 这时还没有监听，所以对应的变化均需手工录入
        // int ryfl = Integer.valueOf(v.getRyfl());
        GlobalMethod.changeSpinnerSelect(spRyfl, v.getRyfl(),
                GlobalConstant.KEY, true);
        changeRyflActive(v.getRyfl());
        edFzjgms.setText(v.getFzjg());
        // if (ryfl == 4) {
        // GlobalMethod.changeSpinnerSelect(spShenFen,
        // v.getFzjg().substring(0, 1), GlobalConstant.VALUE, true);
        // GlobalMethod.changeAdapter(spChenShi, createCityList(v.getFzjg()
        // .substring(0, 1)), this, true);
        // GlobalMethod.changeSpinnerSelect(spChenShi, v.getFzjg(),
        // GlobalConstant.KEY, true);
        // }
        Log.e(TAG, "CHANGE CITY VALUE");
        GlobalMethod.changeSpinnerSelect(spClfl, v.getClfl(),
                GlobalConstant.KEY, true);
        changeClflActive(v.getClfl());
        GlobalMethod.changeSpinnerSelect(spHpzl, v.getHpzl(),
                GlobalConstant.KEY, true);
        if (!TextUtils.isEmpty(v.getHphm())) {
            boolean isHpql = GlobalMethod.changeSpinnerSelect(spHpql, v
                    .getHphm().substring(0, 1), GlobalConstant.VALUE, true);
            edHphm.setText(v.getHphm().substring(isHpql ? 1 : 0));
        }
        // 设置交通方式
        GlobalMethod.changeSpinnerSelect(spJtfs, v.getJtfs(),
                GlobalConstant.KEY, true);
        if (!TextUtils.isEmpty(v.getWfdd()) && WfddDao.isWfddOk(v.getWfdd(), getContentResolver())) {
            List<KeyValueBean> wfdds = new ArrayList<KeyValueBean>();
            wfdds.add(new KeyValueBean(v.getWfdd(), v.getWfdz()));
            GlobalMethod.changeAdapter(spWfdd, wfdds, this);
        }
        //三个数据均保存在GZXM字段中，解析并赋值
        String gzxm = v.getGzxm();
        if (!TextUtils.isEmpty(gzxm)) {
            String[] temp = ViolationDAO.getJsonStrs(gzxm, "zzmm", "zyxx", "syxz");
            GlobalMethod.changeSpinnerSelect(spZzmm, temp[0],
                    GlobalConstant.KEY, true);
            GlobalMethod.changeSpinnerSelect(spZyxx, temp[1],
                    GlobalConstant.KEY, true);
            GlobalMethod.changeSpinnerSelect(spSyxz, temp[2],
                    GlobalConstant.KEY, true);
        }
        edWfsj.setText(v.getWfsj());
        // 不弹出输入法
        edWfsj.setKeyListener(null);
        edDabh.setText(v.getDabh() == null ? "" : v.getDabh());
        edXm.setText(v.getDsr() == null ? "" : v.getDsr());
        edJszh.setText(v.getJszh() == null ? "" : v.getJszh());
        edLxdz.setText(v.getLxfs() == null ? "" : v.getLxfs());
        edLxdh.setText(v.getDh() == null ? "" : v.getDh());
        edZjcx.setText(v.getZjcx() == null ? "" : v.getZjcx());
        edLjjf.setText(TextUtils.isEmpty(v.getWfjfs()) ? "" : v.getWfjfs());
    }

    /**
     * 设置默认值
     *
     * @return
     */
    private VioViolation setDefaultVio() {
        KeyValueBean kvWfdd = ViolationDAO.getLastVioWfdd(resolver);
        VioViolation v = new VioViolation();
        v.setRyfl("4");
        v.setFzjg("苏F");
        v.setClfl("3");
        v.setHpzl("02");
        v.setHphm("苏F");
        // v.setDsr("李小冬");
        // v.setJszh("32060219731130201X");
        // v.setDabh("320600075015");
        v.setWfsj(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        if (kvWfdd != null) {
            v.setWfdz(kvWfdd.getValue());
            v.setWfdd(kvWfdd.getKey());
        }
        return v;
    }

    private void setListeners() {
        findViewById(R.id.But_wfdd).setOnClickListener(butClickListener);
        findViewById(R.id.But_query_wfxw).setOnClickListener(butClickListener);
        findViewById(R.id.but_query_drv_sfzh).setOnClickListener(butClickListener);
        btnQueryDriver.setOnClickListener(butClickListener);
        btnQueryVeh.setOnClickListener(butClickListener);
        btnFzjg.setOnClickListener(butClickListener);
        findViewById(R.id.But_cllx).setOnClickListener(butClickListener);
        findViewById(R.id.But_wzsj).setOnClickListener(butClickListener);
        findViewById(R.id.But_wzrq).setOnClickListener(butClickListener);


        // 人员分类变化时,控制档案编号和查询驾驶人按扭不可用,发证机关,准驾车型
        spRyfl.setOnItemSelectedListener(ryflChangeListener);

        // 车辆分类变化时的监听 1 非机动车 号牌种类，查车按扭，号牌前，不可用，号牌号码可用，交通方式过滤。
        // 2 无牌无证机动车 号牌种类，查车按扭，号牌前，号牌号码均不可用
        // 3 公安牌证机动车 不限制
        // 9 其它 同非机动车
        spClfl.setOnItemSelectedListener(clflChangeListener);

        // 隐藏违法时间控件的软键盘
        // edWfsj.setOnFocusChangeListener(fl);
        // edWfsj.setOnTouchListener(tl);

        // 违法行为列表设为不能编辑
        // edAllWfxws.setOnFocusChangeListener(fl);
        // edAllWfxws.setOnTouchListener(tl);

        //hideIM(edDabh);

        // 当违法代码为四位时自动生成违法行为和罚款记分的内容
        edWfxw.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 4 || s.length() == 5) {
                    char[] dest = new char[s.length()];
                    s.getChars(0, s.length(), dest, 0);
                    queryAndShowWfxw(new String(dest));
                } else {
                    textWfxwms.setText("");
                }
            }
        });

    }

    private int ryflCount = 0;

    private OnSpinnerItemSelected ryflChangeListener = new OnSpinnerItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1,
                                   int position, long arg3) {
            Log.e(TAG, "ryflChangeListener: " + position);
            ryflCount++;
            if (ryflCount < 3 && GlobalSystemParam.isSkipSpinner)
                return;
            if (position > -1)
                changeRyflActive(((KeyValueBean) spRyfl.getSelectedItem())
                        .getKey());
        }
    };

    private int clflCount = 0;

    private OnSpinnerItemSelected clflChangeListener = new OnSpinnerItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1,
                                   int position, long arg3) {

            Log.e(TAG, "clflChangeListener: " + position);
            clflCount++;
            if (clflCount < 3 && GlobalSystemParam.isSkipSpinner)
                return;
            if (position > -1)
                changeClflActive(((KeyValueBean) spClfl.getSelectedItem())
                        .getKey());
        }
    };

    /**
     * 改变车辆分类下拦框时,控件状态相应进行的改变
     */
    private void changeClflActive(String clfl) {
        int xfJtfsfl = 4;
        int iClfl = Integer.valueOf(clfl);
        GlobalMethod.cleanText(edHphm);
        switch (iClfl) {
            case 1:
            case 9:
                // 非机动车或其他
                GlobalMethod.setEnable(false, spHpzl, spHpql, btnQueryVeh);
                GlobalMethod.setEnable(true, edHphm);
                // if (cfCatalog == GlobalConstant.JYCFJDS
                // && cfCatalog != GlobalConstant.QWWFJG && iClfl == 1) {
                // GlobalMethod.changeSpinnerSelect(spJkfs, "1",
                // GlobalConstant.KEY, true);
                // }
                xfJtfsfl = iClfl == 1 ? 2 : 1;
                break;
            case 2:
                // 无牌无证机动车
                GlobalMethod.setEnable(false, spHpzl, spHpql, edHphm, btnQueryVeh);
                break;
            case 3:
                // 公安牌证机动车
                GlobalMethod.setEnable(true, spHpzl, spHpql, edHphm, btnQueryVeh);
                GlobalMethod.changeSpinnerSelect(spHpzl, "02", GlobalConstant.KEY,
                        true);
                GlobalMethod.changeSpinnerSelect(spHpql, "苏", GlobalConstant.VALUE,
                        true);
                edHphm.setText("F");
                break;
            case 4:// 武警牌证机动车
            case 5:// 部队牌证机动车
            case 6:// 农机牌证机动车
                GlobalMethod.setEnable(true, spHpzl, spHpql, edHphm);
                GlobalMethod.setEnable(false, btnQueryVeh);
                if (iClfl == 6)
                    GlobalMethod.changeSpinnerSelect(spHpzl, "14", GlobalConstant.KEY,
                            true);
            default:
                break;
        }
        GlobalMethod.changeAdapter(spJtfs, getXfJtfs(xfJtfsfl), this, true);
        GlobalMethod.changeSpinnerSelect(spJtfs, xfJtfsfl == 1 ? "A01" : (xfJtfsfl == 2 ? "F07" : (iClfl == 6 ? "T21" : "")),
                GlobalConstant.KEY);
    }

    /**
     * 查询交通方式
     *
     * @param fl 1 行人 2 非机动车 其余 机动车
     * @return
     */
    private List<KeyValueBean> getXfJtfs(int fl) {
        String wh = "substr(dmz,1,1) not IN ('A','C','F','X')";
        if (fl == 1)
            wh = "substr(dmz,1,1) IN ('A','C')";
        else if (fl == 2)
            wh = "substr(dmz,1,1) IN ('F','X')";
        ArrayList<KeyValueBean> jtfsList = ViolationDAO.getAllFrmCode(
                GlobalConstant.JTFS, resolver, new String[]{
                        Fixcode.FrmCode.DMZ, Fixcode.FrmCode.DMSM1}, wh, null
        );
        return jtfsList;
    }

    /**
     * 改变人员分类下拉框时控件改变行为
     */
    private void changeRyflActive(String ryfl) {
        spClfl.setOnItemSelectedListener(null);
        GlobalMethod.cleanText(edFzjgms, edXm, edDabh, edJszh, edLxdz, edLxdh,
                edZjcx);
        GlobalMethod.setEnable(false, btnQueryDriver);
        String clfl = "3";
        int iRyfl = Integer.valueOf(ryfl);
        switch (iRyfl) {
            case 4:
                // 公安驾驶证
                GlobalMethod.setEnable(true, edDabh, edZjcx, btnQueryDriver,
                        spClfl, spHpzl, spHpql, btnQueryVeh);
                GlobalMethod.setEnable(false, edFzjgms);

                // edFzjgms.setText(GlobalMethod.getKeyFromSpinnerSelected(spChenShi,
                // GlobalConstant.KEY));
                break;
            case 5:
            case 6:
                // 武警、部队驾驶证
                GlobalMethod.setEnable(false, edDabh);
                GlobalMethod.setEnable(true, edZjcx, edFzjgms, spClfl, spHpzl,
                        spHpql, btnQueryVeh);
                clfl = "" + (iRyfl - 1);
                // edDabh.setText("无");
                break;
            case 7:
                // 农机驾驶证
                GlobalMethod.setEnable(true, edDabh, edFzjgms, edZjcx, spClfl,
                        spHpzl, spHpql, btnQueryVeh);
                clfl = "" + (iRyfl - 1);
                break;
            case 1:
            case 2:
                // 非机动车或乘车人
                GlobalMethod.setEnable(false, edDabh, edFzjgms, edZjcx, spClfl,
                        spHpzl, spHpql, btnQueryVeh);
                clfl = iRyfl == 2 ? "1" : "9";
                break;
            case 3:
                GlobalMethod.setEnable(false, edDabh, edFzjgms, edZjcx);
                GlobalMethod.setEnable(true, spClfl, spHpzl, spHpql, btnQueryVeh);
                break;
            default:
                break;
        }
        // 修改车辆分类，调用车辆分类变化方法
        GlobalMethod.changeSpinnerSelect(spClfl, clfl, GlobalConstant.KEY);
        changeClflActive(clfl);
        //GlobalMethod.changeAdapter(spJtfs, getXfJtfs(iRyfl > 2), this, true);
        // GlobalMethod.changeSpinnerSelect(spJtfs, iRyfl > 2 ? "" : (iRyfl == 1 ? "A01" : "F07"),
        //         GlobalConstant.KEY);
        spClfl.setOnItemSelectedListener(clflChangeListener);
        // boolean isGA = TextUtils.equals("4", kv.getKey());
        // edDabh.setEnabled(isGA);
        // findViewById(R.id.But_query_drv).setEnabled(isGA);
        // spShenFen.setEnabled(isGA);
        // spChenShi.setEnabled(isGA);
        // edZjcx.setEnabled(isGA);
        // if (!isGA) {
        // edDabh.setText("");
        // edZjcx.setText("");
        // }
    }

    /**
     * 隐藏键盘
     *
     * @param edt 控件名称
     */
    private void hideIM(View edt) {
        try {
            InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = edt.getWindowToken();
            if (windowToken != null) {
                // always de-activate IM
                im.hideSoftInputFromWindow(windowToken, 0);
            }
        } catch (Exception e) {
            Log.e("HideInputMethod", "failed:" + e.getMessage());
        }
    }

    /**
     * 隐藏键盘时的监听
     */
    View.OnFocusChangeListener fl = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                hideIM(v);
        }
    };
    View.OnTouchListener tl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideIM(v);
            }
            return false;
        }
    };

    /**
     * 选择了违法行为后,将其详细情况显示到文本框中
     *
     * @param wfdm
     */
    private void queryAndShowWfxw(String wfdm) {
        WfdmBean wf = ViolationDAO.queryWfxwByWfdm(wfdm, resolver);
        if (wf != null) {
            textWfxwms.setText(showWfdmDetail(wf));
        } else {
            textWfxwms.setText("");
            wf = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQCODE_WFDD) {
                Bundle b = data.getExtras();
                if (b != null) {
                    String wfdd = b.getString("wfddDm");
                    String wfdz = b.getString("wfddMc");
                    boolean isSelect = GlobalMethod.changeSpinnerSelect(spWfdd,
                            wfdz, GlobalConstant.VALUE);
                    if (!isSelect) {
                        List<KeyValueBean> wfdds = new ArrayList<KeyValueBean>();
                        wfdds.add(new KeyValueBean(wfdd, wfdz));
                        GlobalMethod.changeAdapter(spWfdd, wfdds, this);
                        GlobalMethod.changeSpinnerSelect(spWfdd, wfdd,
                                GlobalConstant.KEY);
                    }
                }
            } else if (requestCode == REQCODE_WFXW) {
                String wfxw = data.getStringExtra(Wfdmcode.VioCodeWfdm.WFXW);
                if (!TextUtils.isEmpty(wfxw))
                    edWfxw.setText(wfxw);
            } else if (requestCode == REQCODE_JTFS) {
                Bundle b = data.getExtras();
                String j = b.getString("jtfsDm");
                GlobalMethod.changeSpinnerSelect(spJtfs, j, GlobalConstant.KEY);
            } else if (requestCode == REQCODE_FZJG) {
                Bundle b = data.getExtras();
                String fzjg = b.getString("fzjg");
                edFzjgms.setText(fzjg);
            }
        } else {
            // edWfdd.setText("");
        }
    }

    /**
     * 获取界面元素的内容的公共内容
     *
     * @return 处罚对象
     */
    protected void getViolationFromView(VioViolation vio) {
        // 人员分类
        vio.setRyfl(GlobalMethod.getKeyFromSpinnerSelected(spRyfl,
                GlobalConstant.KEY));
        // 驾驶证号
        vio.setJszh(edJszh.getText().toString().toUpperCase());
        // 档案编号
        vio.setDabh(edDabh.getText().toString().trim());
        // 发证机关
        vio.setFzjg(edFzjgms.getText().toString().trim());
        // 准驾车型
        vio.setZjcx(edZjcx.getText().toString().trim().toUpperCase());
        // 当事人姓名
        vio.setDsr(edXm.getText().toString().trim());
        // 电话、地址，可空
        vio.setDh(edLxdh.getText().toString().trim());
        vio.setLxfs(edLxdz.getText().toString().trim());
        // 车辆分类
        vio.setClfl(GlobalMethod.getKeyFromSpinnerSelected(spClfl,
                GlobalConstant.KEY));
        // 号牌种类
        vio.setHpzl(GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                GlobalConstant.KEY));
        // 号牌号码
        Editable hp = edHphm.getText();
        String hpql = GlobalMethod.getKeyFromSpinnerSelected(spHpql,
                GlobalConstant.VALUE);
        vio.setHphm(hpql + hp.toString().toUpperCase().trim());
        // 交通方式
        vio.setJtfs(GlobalMethod.getKeyFromSpinnerSelected(spJtfs,
                GlobalConstant.KEY));
        // 违法时间
        vio.setWfsj(edWfsj.getText().toString());
        // 违法地点
        vio.setWfdd(GlobalMethod.getKeyFromSpinnerSelected(spWfdd,
                GlobalConstant.KEY));
        vio.setWfdz(GlobalMethod.getKeyFromSpinnerSelected(spWfdd,
                GlobalConstant.VALUE));
        // 更新时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        vio.setGxsj(sdf.format(new Date()));
        vio.setClsj(sdf.format(new Date()));
        // 违法记分数字段用于存放驾驶员当前累计记分
        vio.setWfjfs(TextUtils.isEmpty(edLjjf.getText()) ? "0" : edLjjf
                .getText().toString());
        vio.setBzz(edBzz.getText().toString());
        vio.setScz(edScz.getText().toString());
        //保存三个字段
        String zzmm = GlobalMethod.getKeyFromSpinnerSelected(spZzmm,
                GlobalConstant.KEY);
        String zyxx = GlobalMethod.getKeyFromSpinnerSelected(spZyxx,
                GlobalConstant.KEY);
        String syxz = GlobalMethod.getKeyFromSpinnerSelected(spSyxz,
                GlobalConstant.KEY);
        String gzxm = ViolationDAO.saveIntoJsonStr("zzmm", zzmm, "zyxx", zyxx, "syxz", syxz);
        vio.setGzxm(gzxm);
    }

    /**
     * 界面中按扭的监听
     *
     * @author lenovo
     */
    View.OnClickListener butClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == findViewById(R.id.But_query_drv) || v == findViewById(R.id.but_query_drv_sfzh)) {
                // 判断在离线模式
                if (!GlobalMethod.isOnline()) {
                    GlobalMethod.showErrorDialog("离线模式下不能上网查询比对！", self);
                    return;
                }


                // 根据档案号查驾驶员信息
                Editable dabh = edDabh.getText();
                Editable sfzh = edJszh.getText();
                Log.e("证件查询", dabh.toString() + "," + sfzh.toString());
                if (TextUtils.isEmpty(dabh) && v.getId() == R.id.But_query_drv) {
                    GlobalMethod.showErrorDialog("档案编号不能为空", self);
                    return;
                }
                if (TextUtils.isEmpty(sfzh) && v.getId() == R.id.but_query_drv_sfzh) {
                    GlobalMethod.showErrorDialog("驾驶证号不能为空", self);
                    return;
                }
                if (!GlobalMethod.getKeyFromSpinnerSelected(spRyfl,
                        GlobalConstant.KEY).equals("4")) {
                    GlobalMethod.showErrorDialog("只有公安驾驶员才能查询", self);
                    return;
                }
                // String bd = edFzjgms.getText().toString().trim();
                // GlobalMethod.getKeyFromSpinnerSelected(spChenShi,
                // cityList, GlobalConstant.KEY);
                QueryInfoThread thread = new QueryInfoThread(
                        new ViolationHandler(ViolationActivity.this,
                                QUERY_DRV_HANDLER), QUERY_DRV_INFO,
                        new String[]{dabh.toString().trim(), sfzh.toString().trim()}
                );
                thread.doStart();
            } else if (v == findViewById(R.id.But_clbd)) {
                // 根据车辆号码查询车辆信息
                // 判断在离线模式
                if (!GlobalMethod.showOfflineNotQuery(self))
                    return;
                Editable hp = edHphm.getText();
                if (TextUtils.isEmpty(hp)) {
                    GlobalMethod.showErrorDialog("号牌号码不能为空", self);
                    return;
                }
                String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                        GlobalConstant.KEY);
                String hphm = GlobalMethod.getKeyFromSpinnerSelected(spHpql,
                        GlobalConstant.VALUE)
                        + hp.toString().trim().toUpperCase();
                QueryInfoThread thread = new QueryInfoThread(
                        new ViolationHandler(ViolationActivity.this,
                                QUERY_VEH_HANDLER), QUERY_VEH_INFO,
                        new String[]{hpzl, hphm}
                );
                thread.doStart();
            } else if (v == findViewById(R.id.But_cllx)) {
                // 查询交通方式,如果是非机动车,送一个F,给交通方式进行检索
                Intent intent = new Intent(self, ConfigJtfsActivity.class);
                if ("1".equals(((KeyValueBean) spClfl.getSelectedItem())
                        .getKey()))
                    intent.putExtra("clfl", "F");
                startActivityForResult(intent, REQCODE_JTFS);
            } else if (v == findViewById(R.id.But_wfdd)) {
                // 查询违法地点
                Intent intent = new Intent(self, ConfigWfddActivity.class);
                startActivityForResult(intent, REQCODE_WFDD);
            } else if (v == findViewById(R.id.But_wzsj)) {
                // 违法时间修改监听
                GlobalMethod.changeTime(edWfsj, self);
            } else if (v == findViewById(R.id.But_wzrq)) {
                GlobalMethod.changeDate(edWfsj, self);
            } else if (v.getId() == R.id.But_query_wfxw) {
                Intent intent = new Intent(self, ConfigWfdmActivity.class);
                intent.putExtra("comefrom", 1);
                startActivityForResult(intent, REQCODE_WFXW);
            } else if (v == btnFzjg) {
                // 选择发证机关
                // 如果编辑框内有文字，根据文字显示
                Intent intent = new Intent(self, JbywVioFzjgActivity.class);
                String fzjgmc = edFzjgms.getText().toString();
                intent.putExtra("fzjg", fzjgmc);
                startActivityForResult(intent, REQCODE_FZJG);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btp != null) {
            btp.closeConn();
        }
    }

    int ch;

    protected void showConVio(final VioViolation violation) {
        final String[] choices = new String[]{"连续处罚", "转警告", "转强制措施",
                "转违法通知单", "退出"};
        final int[] actives = new int[]{GlobalConstant.JYCFJDS,
                GlobalConstant.QWWFJG, GlobalConstant.QZCSPZ,
                GlobalConstant.WFTZD, ERROR};

        ch = choices.length - 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统提醒");
        builder.setSingleChoiceItems(choices, ch,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ch = which;
                    }
                }
        );
        builder.setNeutralButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int which) {
                if (ch < actives.length && ch > -1 && actives[ch] != ERROR) {
                    Intent intent = null;
                    switch (ch) {
                        case 0:
                            intent = new Intent(self, VioJycxActivity.class);
                            break;
                        case 1:
                            intent = new Intent(self, VioQwjgActivity.class);
                            break;
                        case 2:
                            intent = new Intent(self, VioQzcsActivity.class);
                            break;
                        case 3:
                            intent = new Intent(self, VioWftzActivity.class);
                            break;
                        default:
                            break;
                    }
                    // intent.putExtra("cfCatalog",
                    // String.valueOf(actives[ch]));
                    intent.putExtra("violation", violation);
                    startActivity(intent);
                }
                finish();
            }
        }).create().show();
    }

    /**
     * 异步上传违法决定书
     *
     * @author lenovo
     */
    protected class UploadViolationThread extends Thread {

        private Handler mHandler;
        private VioViolation vio;

        public UploadViolationThread(Handler handler, VioViolation vio) {
            this.mHandler = handler;
            this.vio = vio;
        }

        /**
         * 线程运行
         */
        @Override
        public void run() {
            // WebQueryResult<LoginMessage> rs =
            // ViolationDAO.uploadViolation(vio);
            WebQueryResult<ZapcReturn> rs = ViolationDAO.uploadViolation(vio);
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putSerializable("queryResult", rs);
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }

    protected OnClickListener exitVio = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    /**
     * 保存并上传决定书,将决定书加一
     *
     * @return
     */
    protected String saveViolation() {
        violation.setCfzl(cfzl);
        violation.setWslb(wslb);
        if (ViolationDAO.getViolationByJdsbh(violation.getJdsbh(), resolver) != null)
            return "决定书出现重号";
        if (ViolationDAO.isViolationDuplicate(violation, resolver)) {
            return "重复的决定书内容，可能存在误操作";
        }
        if (TextUtils.equals(violation.getWslb(), "6")) {
            return "违法通知书功能已关闭";
        }
        Uri u = ViolationDAO.saveViolationIntoDB(violation, resolver);
        String s = u.getLastPathSegment();
        int row = 0;
        if (TextUtils.isDigitsOnly(s))
            row = Integer.valueOf(s);
        if (row > 0) {
            isViolationSaved = true;
            // 更新当前编号
            WsglDAO.saveHmbAddOne(jdsbh, resolver);
            // String nextDqhm = GlobalMethod
            // .stringAddLong(jdsbh.getDqhm(), 1);
            // WsglDAO.saveHmb(jdsbh.getHdid(), jdsbh.getJshm(), nextDqhm,
            // jdsbh.getHdzl(), resolver);
            // 上传
            UploadViolationHandler uHandler = new UploadViolationHandler(self,
                    violation);
            UploadViolationThread thread = new UploadViolationThread(uHandler,
                    violation);
            thread.start();

        }
        return "";
    }

    /**
     * 保存确定时的监听
     */
    protected OnClickListener saveVioListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 增加一个验证驾驶员和机动车正确性的异步方法
            CheckVehDrvThread thread = new CheckVehDrvThread(violation,
                    new ViolationHandler(ViolationActivity.this,
                            SAVE_VIO_HANDLER)
            );
            thread.doStart();

        }
    };

    static class ViolationHandler extends Handler {

        private final WeakReference<ViolationActivity> myActivity;
        private int cata;

        public ViolationHandler(ViolationActivity activity, int cata) {
            myActivity = new WeakReference<ViolationActivity>(activity);
            this.cata = cata;
        }

        @Override
        public void handleMessage(Message msg) {
            ViolationActivity ac = myActivity.get();
            if (ac != null) {
                if (cata == SAVE_VIO_HANDLER)
                    ac.saveHandler(msg);
                else if (cata == QUERY_DRV_HANDLER)
                    ac.queryDrvHandler(msg);
                else if (cata == QUERY_VEH_HANDLER)
                    ac.queryVehHandler(msg);
            }
        }
    }

    public void saveHandler(Message msg) {
        Bundle b = msg.getData();
        int checkResult = b.getInt("check");
        if (checkResult == 111) {
            String err = saveViolation();
            if (TextUtils.isEmpty(err))
                GlobalMethod.showDialog("系统信息", violation.getJdsbh()
                        + getViolationTitle() + "文书保存成功!", "确定", self);
            else
                GlobalMethod.showErrorDialog(err, self);
            return;
        }
        String checkErr = "服务器出现错误";
        if (checkResult == 0) {
            checkErr = "服务器端有相同编号的决定书";
        } else if (checkResult == 10)
            checkErr = "驾驶员信息未通过验证";
        else if (checkResult == 110)
            checkErr = "机动车信息未通过验证";
        GlobalMethod.showErrorDialog(checkErr, self);
    }

    class CheckVehDrvThread extends Thread {
        private VioViolation v;
        private Handler mHandler;
        private ProgressDialog progressDialog;

        public CheckVehDrvThread(VioViolation v, Handler mHandler) {
            this.v = v;
            this.mHandler = mHandler;
        }

        public void doStart() {
            progressDialog = ProgressDialog.show(self, "提示",
                    "正在验证驾驶员和机动车信息,请稍等...", true);
            progressDialog.setCancelable(true);
            start();
        }

        @Override
        public void run() {
            // 如果三步均通过验证，返回111，否则特定位置为0
            // 分步返回
            int ryCheck = 0;
            int clCheck = 0;
            String wslb = v.getWslb();
            String jdsbh = v.getJdsbh();
            String ryfl = v.getRyfl();
            String clfl = v.getClfl();
            String hpzl = v.getHpzl();
            RestfulDao dao = RestfulDaoFactory.getDao();
            WebQueryResult<String> sre = dao.isDupVio(wslb, jdsbh);
            int checkResult = 0;
            // 验证决定书是否为重复时，不管网络状态，只有结果返回为1时才认为有重复
            if (sre != null && sre.getStatus() == HttpStatus.SC_OK
                    && TextUtils.equals("1", sre.getResult())) {
                setMsgData(0);
                return;
            }
            checkResult = 1;

            if (("4".equals(ryfl) && GlobalSystemParam.drvCheckFs > 0)
                    && ((GlobalSystemParam.drvCheckFs == 1 && v.getDabh().startsWith(
                    "3206")) || GlobalSystemParam.drvCheckFs == 2)) {
                // 公安驾驶证本地驾驶证，需验证
                WebQueryResult<VioDrvBean> re = dao.queryVioDrv(v.getDabh(),
                        "0");
                String err = GlobalMethod.getErrorMessageFromWeb(re);
                if (TextUtils.isEmpty(err)) {
                    VioDrvBean drv = re.getResult();
                    if (drv != null)
                        ryCheck = 1;
                }
            } else {
                ryCheck = 1;
            }
            if (ryCheck != 1) {
                setMsgData(10);
                return;
            }
            checkResult = 11;
            // 车辆分类为公安号牌且需要验证且号牌种类六
            if (("3".equals(clfl) && GlobalSystemParam.vehCheckFs > 0 && "01,02,07,08,15,16"
                    .indexOf(hpzl) > -1)
                    && ((GlobalSystemParam.vehCheckFs == 1 && v.getHphm().startsWith(
                    "苏F")) || GlobalSystemParam.vehCheckFs == 2)) {
                // 公安机动车
                WebQueryResult<VioVehBean> re = dao.queryVioVeh(v.getHpzl(),
                        v.getHphm());
                String err = GlobalMethod.getErrorMessageFromWeb(re);
                if (TextUtils.isEmpty(err)) {
                    VioVehBean veh = re.getResult();
                    if (veh != null)
                        clCheck = 1;
                }
            } else {
                clCheck = 1;
            }

            checkResult = checkResult * 10 + clCheck;
            setMsgData(checkResult);

        }

        private void setMsgData(int checkResult) {
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("check", checkResult);
            msg.setData(b);
            mHandler.sendMessage(msg);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void writeDrvInfoFromyBean(JSONObject drv) {
        edDabh.setText(drv.optString("dabh", ""));
        edXm.setText(drv.optString("xm", ""));
        edJszh.setText(drv.optString("sfzmhm", ""));
        edLxdz.setText(drv.optString("lxzsxxdz", ""));
        edZjcx.setText(drv.optString("zjcx", ""));
        edLxdh.setText(drv.optString("sjhm", ""));
        edLjjf.setText(drv.optString("ljjf", ""));
        edFzjgms.setText(drv.optString("fzjg", ""));
    }

    private void writeVehFromBean(JSONObject veh) {
        String cllx = veh.optString("cllx", "");
        if (!TextUtils.isEmpty(cllx)) {
            GlobalMethod.changeSpinnerSelect(spJtfs, cllx,
                    GlobalConstant.KEY);
        }
        String syxz = veh.optString("syxz", "");
        GlobalMethod.changeSpinnerSelect(spSyxz, syxz,
                GlobalConstant.KEY);
        String hpzl = veh.optString("hpzl", "");
        if (TextUtils.equals("01", hpzl)) {
            String hdzzl = veh.optString("hdzzl", "");
            if (!TextUtils.isEmpty(hdzzl)) {
                edBzz.setText(hdzzl);
            }
        } else if (TextUtils.equals("02", hpzl)) {
            String hdzk = veh.optString("hdzk", "");
            if (!TextUtils.isEmpty(hdzk)) {
                edBzz.setText(hdzk);
            }
        }
    }

    public void queryDrvHandler(Message msg) {
        Bundle b = msg.getData();
        String s = b.getString("drv", "");
        JSONObject json = null;
        try {
            json = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null) {
            GlobalMethod.showErrorDialog("服务器出错", self);
            return;
        }
        if (!TextUtils.isEmpty(json.optString("err", ""))) {
            GlobalMethod.showErrorDialog(json.optString("err"), self);
            return;
        }
        writeDrvInfoFromyBean(json);
        String bdjg = json.optString("bdjg", "");
        if (!TextUtils.isEmpty(bdjg)) {
            GlobalMethod.showDialog("比对信息", bdjg, "知道了", self);
        }
    }

    @SuppressWarnings("unchecked")
    public void queryVehHandler(Message msg) {
        Bundle b = msg.getData();
        //WebQueryResult<VioVehBean> re = (WebQueryResult<VioVehBean>) b
        //        .getSerializable("veh");
        //String err = GlobalMethod.getErrorMessageFromWeb(re);
        String s = b.getString("veh", "");
        JSONObject json = null;
        try {
            json = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null) {
            GlobalMethod.showErrorDialog("服务器出错", self);
            return;
        }
        if (!TextUtils.isEmpty(json.optString("err", ""))) {
            GlobalMethod.showErrorDialog(json.optString("err"), self);
            return;
        }
        writeVehFromBean(json);
        String bdjg = json.optString("bdjg", "");
        if (!TextUtils.isEmpty(bdjg)) {
            GlobalMethod.showDialog("比对信息", bdjg, "知道了", self);
        }
    }

    protected void printJdsBySelect(VioViolation vio) {
        if (TextUtils.isEmpty(printerInfo.getValue())) {
            GlobalMethod.showDialog("错误信息", "没有配置默认打印机!", "返回", self);
            return;
        }
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            return;
        }
        if (btp == null)
            btp = new BlueToothPrint(printerInfo.getValue());
        if (btp.getBluetoothStatus() != BlueToothPrint.BLUETOOTH_STREAMED) {
            // 没有建立蓝牙串口流
            int errorStaus = btp.createSocket(btAdapter);
            if (errorStaus != BlueToothPrint.SOCKET_SUCCESS) {
                GlobalMethod.showErrorDialog(
                        btp.getBluetoothCodeMs(errorStaus), self);
                return;
            }
        }
        int status = btp.printJdsByBluetooth(vio, getContentResolver());
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    class QueryInfoThread extends Thread {
        private Handler mHandler;
        private int queryCata;
        private String[] params;
        private ProgressDialog progressDialog;

        public QueryInfoThread(Handler mHandler, int queryCata, String[] params) {
            this.mHandler = mHandler;
            this.queryCata = queryCata;
            this.params = params;
        }

        public void doStart() {
            progressDialog = ProgressDialog.show(self, "提示", "正在请求数据,请稍等...",
                    true);
            progressDialog.setCancelable(true);
            start();
        }

        @Override
        public void run() {
            RestfulDao dao = RestfulDaoFactory.getDao();
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            if (queryCata == QUERY_DRV_INFO) {
                String json = dao.jycxQueryDrv(params[0], params[1]);
                //WebQueryResult<VioDrvBean> re = dao.queryVioDrv(params[0],
                //       TextUtils.equals(params[1], "苏F") ? "1" : "0");
                // b.putSerializable("drv", re);
                b.putString("drv", json);
            } else if (queryCata == QUERY_VEH_INFO) {
                //WebQueryResult<VioVehBean> re = dao.queryVioVeh(params[0],
                //        params[1]);
                //b.putSerializable("veh", re);
                String json = dao.jycxQueryVeh(params[0], params[1]);
                b.putSerializable("veh", json);
            }
            msg.setData(b);
            mHandler.sendMessage(msg);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }

    }

    @Override
    public void onBackPressed() {
        // 还没有保存
        if (!isViolationSaved) {
            GlobalMethod.showDialogTwoListener("系统提醒", "本次处罚还没有保存，是否确定退出！",
                    "退出", "返回", exitVio, self);
        } else {
            finish();
        }
    }

    protected String getActivityTitle() {
        // String activityTitle = !TextUtils.isEmpty(printerInfo.getValue()) ?
        // printerInfo
        // .getKey() : "无打印机";
        // activityTitle = "编号: " + jdsbh.getDqhm() + "--" + activityTitle;

        return getViolationTitle();
        // + "--" + activityTitle;
    }

    protected abstract String getViolationTitle();

    protected abstract String saveAndCheckVio();

    protected abstract String showWfdmDetail(WfdmBean w);

    /**
     * 初始化决定书对象，类中就使用一个对象,不同的处罚加入不同的特定字段
     */
    protected void initViolation() {
        violation = new VioViolation();
        // 一些公共项目
        String fxjg = GlobalData.grxx.get(GlobalConstant.KSBMBH);
        // 发现机关
        violation.setFxjg(fxjg);
        // 多个默认项目
        violation.setZsxzqh("");
        violation.setZsxxdz("");
        violation.setJsjqbj("00");
        violation.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
        // 上传标记和时间
        violation.setScbj("0");
        violation.setCwxx("");
        //violation.setGzxm("");
        //violation.setGzxmmc("");
        violation.setBzz("");
        violation.setScz("");
        violation.setJdsbh(jdsbh.getDqhm().trim());
        violation.setHdid(jdsbh.getHdid().trim());
    }

    protected boolean menuSaveViolation() {
        if (isViolationSaved) {
            GlobalMethod.showErrorDialog("单据已保存,无需重复保存!", self);
            return true;
        }

        String err = saveAndCheckVio();
        if (!TextUtils.isEmpty(err)) {
            GlobalMethod.showErrorDialog(err, self);
        } else {
            GlobalMethod.showDialogTwoListener("系统提示",
                    "是否保存 " + jdsbh.getDqhm() + getViolationTitle() + " ? 请确认",
                    "保存", "取消", saveVioListener, self);
        }
        return true;
    }

    protected boolean menuPreviewViolation() {
        if (!isViolationSaved) {
            String errInfo = saveAndCheckVio();
            if (!TextUtils.isEmpty(errInfo)) {
                // 保存不成功
                GlobalMethod.showErrorDialog(errInfo, this);
                return true;
            }
        }
        ArrayList<JdsPrintBean> jds = PrintJdsTools.getPrintJdsContent(
                violation, getContentResolver());
        Intent intent = new Intent(self, JdsPreviewActivity.class);
        intent.putExtra("jds", jds);
        startActivityForResult(intent, REQCODE_PREVIEW_JDS);
        return true;
    }

    protected boolean menuPrintViolation() {
        if (isViolationSaved && violation != null) {
            printJdsBySelect(violation);
        } else {
            GlobalMethod.showErrorDialog("决定书还没有保存,请保存后再打印", self);
        }
        return true;
    }

}
