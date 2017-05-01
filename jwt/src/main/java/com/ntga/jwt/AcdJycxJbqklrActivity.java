package com.ntga.jwt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ntga.adaper.OnSpinnerItemSelected;
import com.ntga.bean.AcdPhotoBean;
import com.ntga.bean.AcdSimpleBean;
import com.ntga.bean.AcdSimpleHumanBean;
import com.ntga.bean.AcdWfxwBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.THmb;
import com.ntga.dao.AcdSimpleDao;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.GlobalSystemParam;
import com.ntga.dao.VerifyData;
import com.ntga.dao.WfddDao;
import com.ntga.dao.WsglDAO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;

public class AcdJycxJbqklrActivity extends ActionBarActivity {

    protected static final int REQCODE_SGDD = 0;
    protected static final int REQCODE_ADD_MOD_RYJBQK = 1;

    private static final int REQCODE_ADD_MOD_CONTEXT = 2;
    private static final String TAG = "AcdJycxJbqklrActivity";

    private EditText editSgfssj, editSgfsdd, editRdyy, editSsrs, editZjccss;
    private Spinner spinTq, spinSgxt, spinCjpz, spinDcpz, spinJafs, spinTjfs;
    private Button btnChangeSgsj, btnChangeSgdd, btnChangeSgrq;
    private SimpleDateFormat sdf;
    private Context self;
    //private ExpandableListView expRyJbqk;
    private Spinner spinRyjbqk;
    //private SimpleExpandableListAdapter mAdapter;
    //private List<Map<String, String>> groupData;
    //private List<List<Map<String, String>>> childData;
    //private static String NAME = "name";
    //private static String VALUE = "value";

    // 人员基本情况列表
    private ArrayList<AcdSimpleHumanBean> ryjbqkList = null;
    private ArrayList<KeyValueBean> ryjbqkKVList;
    // 违法地点和违法行为
    private KeyValueBean kvSgdd = new KeyValueBean("", "");
    private AcdWfxwBean acdRdyyWfxw = null;

    private THmb dqbmb;

    private AcdSimpleBean acdJbqk = null;
    // 是否可以修改，分为浏览模式、修改模式、新增模式
    private int operMod;
    private boolean isSave;

    private final String STATE_KV_SGDD = "kvSgdd";
    private final String STATE_OPER_MOD = "operMod";
    private final String STATE_IS_SAVE = "isSave";
    private final String STATE_RYJBQK_BEAN = "ryjbqk_bean";
    private final String STATE_RYJBQK_KV = "ryjbqk_kv";
    private final String STATE_RDYY_WFXW = "rdyy_wfxw";
    private final String STATE_HMB = "hmb";
    private final String STATE_ACD_JBQK = "acd_jbqk";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_SAVE, isSave);
        outState.putInt(STATE_OPER_MOD, operMod);
        if (kvSgdd != null)
            outState.putSerializable(STATE_KV_SGDD, kvSgdd);
        if (ryjbqkList != null) {
            outState.putSerializable(STATE_RYJBQK_BEAN, ryjbqkList);
        }
        if (ryjbqkKVList != null) {
            outState.putSerializable(STATE_RYJBQK_KV, ryjbqkKVList);
        }
        if (acdRdyyWfxw != null) {
            outState.putSerializable(STATE_RDYY_WFXW, acdRdyyWfxw);
        }
        if (dqbmb != null) {
            outState.putSerializable(STATE_HMB, dqbmb);
        }
        if (acdJbqk != null) {
            outState.putSerializable(STATE_ACD_JBQK, acdJbqk);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle ss) {
        super.onRestoreInstanceState(ss);
        if (ss.containsKey(STATE_IS_SAVE))
            isSave = ss.getBoolean(STATE_IS_SAVE);
        if (ss.containsKey(STATE_OPER_MOD))
            operMod = ss.getInt(STATE_OPER_MOD);
        if (ss.containsKey(STATE_KV_SGDD))
            kvSgdd = (KeyValueBean) ss.getSerializable(STATE_KV_SGDD);
        if (ss.containsKey(STATE_RYJBQK_BEAN))
            ryjbqkList = (ArrayList<AcdSimpleHumanBean>) ss.getSerializable(STATE_RYJBQK_BEAN);
        if (ss.containsKey(STATE_RYJBQK_KV))
            ryjbqkKVList = (ArrayList<KeyValueBean>) ss.getSerializable(STATE_RYJBQK_KV);
        if (ss.containsKey(STATE_RDYY_WFXW))
            acdRdyyWfxw = (AcdWfxwBean) ss.getSerializable(STATE_RDYY_WFXW);
        if (ss.containsKey(STATE_HMB))
            dqbmb = (THmb) ss.getSerializable(STATE_HMB);
        if (ss.containsKey(STATE_ACD_JBQK))
            acdJbqk = (AcdSimpleBean) ss.getSerializable(STATE_ACD_JBQK);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        setContentView(R.layout.acd_jycx_jbqk);
        isSave = false;
        operMod = getIntent().getIntExtra(AcdSimpleDao.OPER_MOD,
                AcdSimpleDao.ACD_MOD_NEW);
        acdJbqk = (AcdSimpleBean) getIntent().getSerializableExtra("acd");

        ryjbqkList = (ArrayList<AcdSimpleHumanBean>) getIntent()
                .getSerializableExtra("humans");

        // 初始化案情和人员对象
        if (acdJbqk == null)
            acdJbqk = new AcdSimpleBean();
        if (ryjbqkList == null)
            ryjbqkList = new ArrayList<AcdSimpleHumanBean>();
        ryjbqkKVList = new ArrayList<KeyValueBean>();


        // 初始化控件
        initView();
        if (operMod == AcdSimpleDao.ACD_MOD_MODITY
                || operMod == AcdSimpleDao.ACD_MOD_SHOW) {
            //事故已存在
            changeViewByValue(acdJbqk);
        } else {
            if (operMod == AcdSimpleDao.ACD_MOD_PHOTO_NEW) {
                // 已拍了照片,然后再进行简易程序,时间和地点,文书编号
                AcdPhotoBean acdPhoto = (AcdPhotoBean) getIntent()
                        .getSerializableExtra(AcdTakePhotoActivity.ACD_PHOTO_BEAN);
                if (acdPhoto != null) {
                    acdJbqk.setSgfssj(acdPhoto.getSgsj());
                    acdJbqk.setSgdd(acdPhoto.getSgdd());
                    kvSgdd = new KeyValueBean(acdPhoto.getSgdddm(),
                            acdPhoto.getSgdd());
                    editSgfssj.setText(acdPhoto.getSgsj());
                    editSgfsdd.setText(acdPhoto.getSgdd());
                    acdJbqk.setWsbh(acdPhoto.getSgbh());
                }
            } else if (operMod == AcdSimpleDao.ACD_MOD_NEW) {
                String zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);
                dqbmb = WsglDAO.getCurrentJdsbh(GlobalConstant.ACDSIMPLEWS, zqmj,
                        getContentResolver());
                if (dqbmb != null) {
                    acdJbqk.setWsbh(dqbmb.getDqhm());
                }
                editSgfssj.setText(sdf.format(new Date()));
            }
            GlobalMethod.changeSpinnerSelect(spinSgxt, "11", GlobalConstant.KEY, true);
            changeCjpzAdnDcpzBySgxt();
            GlobalMethod.changeSpinnerSelect(spinJafs, "1", GlobalConstant.KEY, true);
            changeJafsByTjfs();
        }
        setTitle("简易事故");
        ((TextView) findViewById(R.id.tv_jysgbh)).setText("事故编号：" + acdJbqk.getWsbh());
        // 最后设下拉框的监听
        spinSgxt.setOnItemSelectedListener(sgxtSelect);
        spinJafs.setOnItemSelectedListener(jafsSelect);

        referRyjbqkKVList();
    }

    private void referRyjbqkKVList() {
        if (ryjbqkList == null)
            ryjbqkList = new ArrayList<AcdSimpleHumanBean>();
        ryjbqkKVList.clear();
        for (int i = 0; i < ryjbqkList.size(); i++) {
            AcdSimpleHumanBean jbqk = ryjbqkList.get(i);
            String ryqk = "人员编号：" + (jbqk.getRybh() + 1) + ",";
            ryqk += (TextUtils.isEmpty(jbqk.getXm()) ? "无姓名" : jbqk.getXm());
            String jtfs = TextUtils.isEmpty(jbqk.getJtfs()) ? "无交通方式"
                    : GlobalMethod.getStringFromKVListByKey(
                    GlobalData.acdJtfsList, jbqk.getJtfs());
            String clqk = jtfs
                    + ","
                    + (TextUtils.isEmpty(jbqk.getHphm()) ? "无车号" : jbqk
                    .getHphm());
            ryjbqkKVList.add(new KeyValueBean("" + i, ryqk + "\n" + clqk));
        }
        GlobalMethod.changeAdapter(spinRyjbqk, ryjbqkKVList, (Activity) self);
    }

    private void initView() {
        // 初始化控件
        editSgfssj = (EditText) findViewById(R.id.acd_sgfssj);
        editSgfsdd = (EditText) findViewById(R.id.acd_sgfsdd);
        editRdyy = (EditText) findViewById(R.id.edit_acd_rdyy);
        editSsrs = (EditText) findViewById(R.id.edit_acd_ssrs);
        editZjccss = (EditText) findViewById(R.id.edit_acd_ccss);
        spinTq = (Spinner) findViewById(R.id.spin_acd_tq);
        spinSgxt = (Spinner) findViewById(R.id.spin_acd_sjxt);
        spinCjpz = (Spinner) findViewById(R.id.spin_acd_cjpz);
        spinDcpz = (Spinner) findViewById(R.id.spin_acd_dcpz);
        spinJafs = (Spinner) findViewById(R.id.spin_acd_jafs);
        spinTjfs = (Spinner) findViewById(R.id.spin_acd_tjfs);
        spinRyjbqk = (Spinner) findViewById(R.id.spin_ryjbqk);
        btnChangeSgsj = (Button) findViewById(R.id.btn_chg_sgsj);
        btnChangeSgrq = (Button) findViewById(R.id.btn_chg_sgrq);
        btnChangeSgdd = (Button) findViewById(R.id.btn_chg_sgdd);

        // 先设置监听
        btnChangeSgdd.setOnClickListener(btnClick);
        btnChangeSgsj.setOnClickListener(btnClick);
        btnChangeSgrq.setOnClickListener(btnClick);

        // 下拉框数据填充
        GlobalMethod.changeAdapter(spinTq, GlobalData.arrayAcdTq, this);
        GlobalMethod.changeAdapter(spinSgxt, GlobalData.arrayAcdSgxt, this, true);
        GlobalMethod.changeAdapter(spinCjpz, GlobalData.arrayAcdCjpz, this, true);
        GlobalMethod.changeAdapter(spinDcpz, GlobalData.arrayAcdDcpz, this, true);
        GlobalMethod.changeAdapter(spinJafs, GlobalData.arrayAcdJafs, this, true);
        GlobalMethod.changeAdapter(spinTjfs, GlobalData.arrayAcdTjfs, this, true);

        // 初值填充
        editSgfssj.setInputType(InputType.TYPE_NULL);
        editSgfsdd.setInputType(InputType.TYPE_NULL);
    }

    /**
     * 改变车间碰撞与单车碰撞形态
     */
    private void changeCjpzAdnDcpzBySgxt() {
        String sgxt = GlobalMethod.getKeyFromSpinnerSelected(spinSgxt, GlobalConstant.KEY);
        boolean cjpz = TextUtils.equals(sgxt, "11") || TextUtils
                .equals(sgxt, "12");
        boolean dcpz = TextUtils.equals(sgxt, "35") || TextUtils
                .equals(sgxt, "36");
        spinCjpz.setSelection(cjpz ? 1 : 0, true);
        spinDcpz.setSelection(dcpz ? 1 : 0, true);
        spinCjpz.setEnabled(cjpz);
        spinDcpz.setEnabled(dcpz);
    }

    private int sgxtCount = 0, tjfsCount = 0;

    /**
     * 根据事故形态决定车间碰撞与单车碰撞是否可用
     */
    private OnSpinnerItemSelected sgxtSelect = new OnSpinnerItemSelected() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                   long arg3) {
            Log.e("AcdJycxActivity", "sgxt: OnSpinnerItemSelected");
            //2.3以上版本不多次调用
            sgxtCount++;
            if (sgxtCount < 3 && GlobalSystemParam.isSkipSpinner)
                return;
            if (pos > -1) {
                changeCjpzAdnDcpzBySgxt();
            }
        }
    };

    private void changeViewByValue(AcdSimpleBean acd) {
        editSgfssj.setText(TextUtils.isEmpty(acd.getSgfssj()) ? "" : acd
                .getSgfssj());
        Log.e(TAG, "事故地点：" + acd.getSgdd());
        if (!TextUtils.isEmpty(acd.getSgdd())) {
            // 很多关于地点的可以赋值
            String dldm = acdJbqk.getXzqh() + acdJbqk.getLh()
                    + acdJbqk.getGls() + acdJbqk.getMs();
            Log.e(TAG, "事故代码：" + dldm);
            if (!TextUtils.isEmpty(dldm) && dldm.length() == 18) {
                kvSgdd.setKey(dldm);
                kvSgdd.setValue(acdJbqk.getSgdd());
                editSgfsdd.setText(acdJbqk.getSgdd());
            }
        }
        if (!TextUtils.isEmpty(acd.getSgrdyy())) {
            acdRdyyWfxw = AcdSimpleDao.getAcdWfxwByWfdm(acd.getSgrdyy(),
                    getContentResolver());
            if (acdRdyyWfxw != null)
                editRdyy.setText(acdRdyyWfxw.getWfnr());
        }
        editSsrs.setText(TextUtils.isEmpty(acd.getSsrs()) ? "0" : acd.getSsrs());
        editZjccss.setText(TextUtils.isEmpty(acd.getZjccss()) ? "0" : acd
                .getZjccss());
        GlobalMethod.changeSpinnerSelect(spinTq, acd.getTq(), GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spinSgxt, acd.getSgxt(), GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spinCjpz, acd.getCljsg(), GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spinDcpz, acd.getDcsg(), GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spinJafs, acd.getJafs(), GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spinTjfs, acd.getTjfs(), GlobalConstant.KEY, true);
    }

    /**
     * 改变结案方式
     */
    private void changeJafsByTjfs() {
        String jafs = GlobalMethod.getKeyFromSpinnerSelected(spinJafs, GlobalConstant.KEY);
        boolean isTj = TextUtils.equals(jafs, "1");
        spinTjfs.setSelection(isTj ? 1 : 0);
        spinTjfs.setEnabled(isTj);
    }

    /**
     * 结案方式选择时界面的改变监听
     */
    private OnSpinnerItemSelected jafsSelect = new OnSpinnerItemSelected() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                   long arg3) {
            tjfsCount++;
            if (tjfsCount < 3 && GlobalSystemParam.isSkipSpinner)
                return;
            if (pos > -1) {
                // 结案方式为调解结案，调解方式不能为空
                changeJafsByTjfs();
            }
        }
    };

    /**
     * 所有的按扭监听
     */
    private View.OnClickListener btnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_chg_sgsj:
                    GlobalMethod.changeTime(editSgfssj, self);
                    break;
                case R.id.btn_chg_sgrq:
                    GlobalMethod.changeDate(editSgfssj, self);
                    break;
                case R.id.btn_chg_sgdd:
                    Intent intent = new Intent(self, ConfigWfddActivity.class);
                    startActivityForResult(intent, REQCODE_SGDD);
                    break;
                default:
                    break;
            }

        }
    };

    // 返回时的操作
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            if (b == null)
                return;
            if (requestCode == REQCODE_ADD_MOD_RYJBQK) {
                AcdSimpleHumanBean rycl = (AcdSimpleHumanBean) b
                        .getSerializable("jbqk");
                if (rycl != null) {
                    int rybh = rycl.getRybh();
                    if (ryjbqkList.size() > rybh) {
                        ryjbqkList.set(rybh, rycl);
                    } else {
                        ryjbqkList.add(rycl);
                    }
                    if (rybh == 0) {
                        // 取第一个当事人，定入认定原因
                        acdRdyyWfxw = AcdSimpleDao.getAcdWfxwByWfdm(
                                rycl.getWfxw1(), getContentResolver());
                        editRdyy.setText(acdRdyyWfxw.getWfnr());
                    }
                }
                referRyjbqkKVList();
                spinRyjbqk.setSelection(ryjbqkKVList.size() - 1);
            } else if (requestCode == REQCODE_SGDD) {
                kvSgdd.setKey(b.getString("wfddDm"));
                Log.e(TAG, kvSgdd.getKey());
                kvSgdd.setValue(b.getString("wfddMc"));
                editSgfsdd.setText(b.getString("wfddMc"));
            } else if (requestCode == REQCODE_ADD_MOD_CONTEXT) {
                acdJbqk.setSgss(b.getString("sgss"));
                acdJbqk.setZrtjjg(b.getString("tjjg"));
                GlobalMethod.changeAdapter(spinRyjbqk, ryjbqkKVList, (Activity) self);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acd_jycx_jbqk_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.menu_add_ryclqk): {
                // 增加人员基本情况
                int rybh = 0;
                if (ryjbqkList != null)
                    rybh = ryjbqkList.size();
                Intent intent = new Intent(self, AcdRyclJbqkActivity.class);
                intent.putExtra("rybh", rybh);
                startActivityForResult(intent, REQCODE_ADD_MOD_RYJBQK);
            }
            return true;
            case (R.id.menu_save_acd_quite): {
                // 保存记录
                saveAcdSimpleFromViewValue();
                String check = VerifyData.checkAcdSimple(acdJbqk, true);
                if (TextUtils.isEmpty(check)) {
                    // 没有错误，可以保存
                    check = VerifyData.verifyAcdJbqkAndHuman(acdJbqk, ryjbqkList);
                    Log.e(TAG, "保存的米数：" + acdJbqk.getMs());
                    if (TextUtils.isEmpty(check)) {
                        isSave = true;
                        String id = AcdSimpleDao.saveAcdJbxxIntoDb(
                                getContentResolver(), acdJbqk);
                        acdJbqk.setSgbh(id);
                        for (AcdSimpleHumanBean human : ryjbqkList) {
                            human.setSgbh(id);
                            String huID = AcdSimpleDao.saveAcdHumanInDb(
                                    getContentResolver(), human);
                            human.setHumanID(huID);
                        }
                        if (operMod == AcdSimpleDao.ACD_MOD_NEW) {
                            WsglDAO.saveHmbAddOne(dqbmb, getContentResolver());
                        }
                        closeOkView();
                    } else {
                        GlobalMethod.showErrorDialog(check, self);
                    }
                } else {
                    GlobalMethod.showErrorDialog(check, self);
                }
            }
            return true;
            case R.id.menu_mod_context:
            case R.id.menu_show_context: {
                if (operMod != AcdSimpleDao.ACD_MOD_SHOW) {
                    // 进行文档生成，传递事故基本情况和人员基本情况
                    saveAcdSimpleFromViewValue();
                    // 验证基本情况，不验证事故事实及调解情况
                    String err = VerifyData.checkAcdSimple(acdJbqk, false);
                    if (!TextUtils.isEmpty(err)) {
                        GlobalMethod.showErrorDialog(err, self);
                        return true;
                    }
                    err = VerifyData.verifyAcdJbqkAndHuman(acdJbqk, ryjbqkList);
                    if (!TextUtils.isEmpty(err)) {
                        GlobalMethod.showErrorDialog(err, self);
                        return true;
                    }
                }
                Intent intent = new Intent(self, AcdSsZrTjjgActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("sgjbqk", acdJbqk);
                b.putSerializable("ryjbqk", ryjbqkList);
                b.putInt(AcdSimpleDao.OPER_MOD, operMod);
                intent.putExtras(b);
                startActivityForResult(intent, REQCODE_ADD_MOD_CONTEXT);
            }
            return true;
            case (R.id.menu_del_ryjbqk): {
                //删除人员情况
                int position = spinRyjbqk.getSelectedItemPosition();
                if (position < 0) {
                    GlobalMethod.showErrorDialog("未选择人员情况", self);
                    return true;
                }
                ryjbqkList.remove(position);
                referRyjbqkKVList();
            }
            return true;
            case R.id.menu_mod_ryjbqk: {
                //修改
                int pos = spinRyjbqk.getSelectedItemPosition();
                if (pos > -1 && ryjbqkList.size() > 0) {
                    Intent intent = new Intent(self, AcdRyclJbqkActivity.class);
                    intent.putExtra("jbqk", ryjbqkList.get(pos));
                    intent.putExtra(AcdSimpleDao.OPER_MOD, operMod);
                    startActivityForResult(intent, REQCODE_ADD_MOD_RYJBQK);
                }
            }
            return true;
        }
        return false;
    }

    private void closeOkView() {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    /**
     * 从界面元素保存到事故对象
     */
    private void saveAcdSimpleFromViewValue() {
        // 道路相关
        String dldm = kvSgdd.getKey();
        if (!TextUtils.isEmpty(dldm) && dldm.length() == 18) {
            String xzqh = dldm.substring(0, 6);
            acdJbqk.setXzqh(xzqh);
            acdJbqk.setLh(dldm.substring(6, 11));
            acdJbqk.setGls(dldm.substring(11, 15));
            acdJbqk.setMs(dldm.substring(15, 18));
            acdJbqk.setSgdd(kvSgdd.getValue());
            String[] dlmx = WfddDao.getDlmx(acdJbqk.getXzqh(), acdJbqk.getLh(),
                    getContentResolver());
            if (dlmx != null) {
                acdJbqk.setDllx(dlmx[0]);
                acdJbqk.setGlxzdj(dlmx[1]);
                acdJbqk.setLm(dlmx[2]);
            }
        }
        // 星期
        acdJbqk.setXq("1");
        acdJbqk.setSgfssj(editSgfssj.getText().toString());
        acdJbqk.setCclrsj(sdf.format(new Date()));
        acdJbqk.setGxsj(sdf.format(new Date()));

        // 以下部分需完善
        if (acdRdyyWfxw != null
                && editRdyy.getText().toString().equals(acdRdyyWfxw.getWfnr())) {
            acdJbqk.setRdyyfl(acdRdyyWfxw.getRdyy());
            acdJbqk.setSgrdyy(acdRdyyWfxw.getWfxwdm());
        }
        // acdJbqk.setSgss(sgssText);
        // acdJbqk.setZrtjjg(zrtjjgText);
        //
        acdJbqk.setSsrs(editSsrs.getText().toString());
        acdJbqk.setZjccss(editZjccss.getText().toString());

        // 事故事实、调解、认定

        acdJbqk.setTq(GlobalMethod.getKeyFromSpinnerSelected(spinTq, GlobalConstant.KEY));
        acdJbqk.setSgxt(GlobalMethod.getKeyFromSpinnerSelected(spinSgxt, GlobalConstant.KEY));
        acdJbqk.setCljsg(GlobalMethod.getKeyFromSpinnerSelected(spinCjpz, GlobalConstant.KEY));
        acdJbqk.setDcsg(GlobalMethod.getKeyFromSpinnerSelected(spinDcpz, GlobalConstant.KEY));
        acdJbqk.setJafs(GlobalMethod.getKeyFromSpinnerSelected(spinJafs, GlobalConstant.KEY));
        acdJbqk.setTjfs(GlobalMethod.getKeyFromSpinnerSelected(spinTjfs, GlobalConstant.KEY));

        // 管理部门和所属中队，服务器上传时查找
        acdJbqk.setSszd("");
        acdJbqk.setGlbm("");
        acdJbqk.setJar1(GlobalData.grxx.get(GlobalConstant.YHBH));
        acdJbqk.setJar2("");
        acdJbqk.setJbr(GlobalData.grxx.get(GlobalConstant.YHBH));
        acdJbqk.setTjr1(GlobalData.grxx.get(GlobalConstant.YHBH));

        // 可以赋空值或默认值
        acdJbqk.setLwsglx("");
        acdJbqk.setPzfs("");
        acdJbqk.setLbqk("");
        acdJbqk.setDah("");
        acdJbqk.setSb("1");
        acdJbqk.setTjsgbh("");
        acdJbqk.setDzzb("");
        acdJbqk.setBadw("");
        // acdJbqk.setWsbh(dqbmb.getDqhm());
        acdJbqk.setJllx("1");
        acdJbqk.setScsjd("0");
        acdJbqk.setXc("");
        acdJbqk.setSwsg("2");
        acdJbqk.setJdwz("0");

        acdJbqk.setScbj("0");
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (operMod == AcdSimpleDao.ACD_MOD_SHOW) {
            menu.removeItem(R.id.menu_save_acd_quite);
            menu.removeItem(R.id.menu_add_ryclqk);
            menu.findItem(R.id.menu_mod_context).setTitle("调解认定内容");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if ((operMod == AcdSimpleDao.ACD_MOD_NEW || operMod == AcdSimpleDao.ACD_MOD_MODITY)
                && !isSave) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("系统提醒")
                    .setMessage("事故信息还没有保存，是否确定退出！")
                    .setNeutralButton("返回",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                }
                            }
                    )
                    .setNegativeButton("退出",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    finish();
                                }
                            }
                    ).create().show();
        } else {
            super.onBackPressed();
        }
    }

}
