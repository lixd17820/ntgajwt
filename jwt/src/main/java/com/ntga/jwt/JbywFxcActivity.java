package com.ntga.jwt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.provider.wfdmcode.Wfdmcode;
import com.ntga.adaper.OnSpinnerItemSelected;
import com.ntga.bean.JdsPrintBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.VioFxcFileBean;
import com.ntga.bean.VioFxczfBean;
import com.ntga.dao.BlueToothPrint;
import com.ntga.dao.ConnCata;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.GlobalSystemParam;
import com.ntga.dao.PrintJdsTools;
import com.ntga.dao.WfddDao;
import com.ntga.database.FxczfDao;
import com.ntga.database.MessageDao;
import com.ntga.thread.FxcUploadPhotoThread;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JbywFxcActivity extends ActionBarActivity {

    private final int CAMER_REQUEST = 1110;
    private final int REQCODE_WFDD = 1111;
    private final int REQCODE_WFXW = 1112;

    private ArrayList<String> bigList;
    private ArrayList<VioFxcFileBean> zpList;
    private Context self;
    private Spinner spHpzl, spHpqz, spImageList;
    private EditText edWfdd, edWfxw, edHphm, edWfsj;
    private ImageView mImageView;
    private int imageIndex = -1;
    private KeyValueBean kvWfdd;

    private boolean isSaveText = false, isSaveFile = false;
    private VioFxczfBean fxczf;
    private String tzsbh;
    private KeyValueBean printerInfo;
    private BlueToothPrint btp = null;

    private static final int READONLY = 100;
    private static final int ADD_NEW = 101;

    private int operMod = ADD_NEW;

    private static final String STATE_IMAGE_INDEX = "imageIndex";
    private static final String STATE_ZP_LIST = "zpList";
    private static final String STATE_BIG_LIST = "bigList";
    private static final String STATE_KV_WFDD = "kvWfdd";
    private static final String STATE_IS_SAVE_FILE_BOL = "isSaveFile";
    private static final String STATE_IS_SAVE_TEXT_BOL = "isSaveText";
    private static final String STATE_TZSBH = "tzsbh";
    private static final String STATE_FXCZF_BEAN = "fxczf";
    private static final String STATE_OPER_MOD = "operMod";

    @SuppressWarnings("unchecked")
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the previously serialized current dropdown position.
        Log.e("AcdTakePhotoActivity", "onRestoreInstanceState");
        if (savedInstanceState.containsKey(STATE_ZP_LIST)) {
            zpList = (ArrayList<VioFxcFileBean>) savedInstanceState
                    .getSerializable(STATE_ZP_LIST);
        }
        if (savedInstanceState.containsKey(STATE_BIG_LIST)) {
            bigList = savedInstanceState.getStringArrayList(STATE_BIG_LIST);
        }

        if (savedInstanceState.containsKey(STATE_IMAGE_INDEX)) {
            imageIndex = savedInstanceState.getInt(STATE_IMAGE_INDEX);
        }
        if (savedInstanceState.containsKey(STATE_TZSBH)) {
            tzsbh = savedInstanceState.getString(STATE_TZSBH);
        }

        if (savedInstanceState.containsKey(STATE_IS_SAVE_TEXT_BOL)) {
            isSaveText = savedInstanceState.getBoolean(STATE_IS_SAVE_TEXT_BOL);
        }

        if (savedInstanceState.containsKey(STATE_IS_SAVE_FILE_BOL)) {
            isSaveFile = savedInstanceState.getBoolean(STATE_IS_SAVE_FILE_BOL);
        }

        if (savedInstanceState.containsKey(STATE_KV_WFDD)) {
            kvWfdd = (KeyValueBean) savedInstanceState
                    .getSerializable(STATE_KV_WFDD);
        }

        if (savedInstanceState.containsKey(STATE_FXCZF_BEAN)) {
            fxczf = (VioFxczfBean) savedInstanceState
                    .getSerializable(STATE_FXCZF_BEAN);
        }

        if (savedInstanceState.containsKey(STATE_OPER_MOD)) {
            operMod = savedInstanceState.getInt(STATE_OPER_MOD);
        }

        findViewById(R.id.unless_linear).requestFocus();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putStringArrayList(STATE_BIG_LIST, bigList);
        outState.putInt(STATE_IMAGE_INDEX, imageIndex);
        outState.putString(STATE_TZSBH, tzsbh);
        outState.putBoolean(STATE_IS_SAVE_FILE_BOL, isSaveFile);
        outState.putBoolean(STATE_IS_SAVE_TEXT_BOL, isSaveText);
        outState.putInt(STATE_OPER_MOD, operMod);
        if (kvWfdd != null)
            outState.putSerializable(STATE_KV_WFDD, kvWfdd);
        if (fxczf != null) {
            outState.putSerializable(STATE_FXCZF_BEAN, fxczf);
        }
        if (zpList != null)
            outState.putSerializable(STATE_ZP_LIST, zpList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jbyw_fxc);
        self = this;
        if (!GlobalData.isInitLoadData) {
            GlobalData.initGlobalData(getContentResolver());
            GlobalData.serialNumber = GlobalMethod.getSerial(self);
            GlobalMethod.readParam(self);
        }
        spHpzl = (Spinner) findViewById(R.id.spin_hpzl);
        spHpqz = (Spinner) findViewById(R.id.spin_hpqz);
        edWfdd = (EditText) findViewById(R.id.edit_wfdd);
        edWfdd.setKeyListener(null);
        edWfxw = (EditText) findViewById(R.id.edit_wfxw);
        edHphm = (EditText) findViewById(R.id.edit_hphm);
        edWfsj = (EditText) findViewById(R.id.edit_wfsj);
        edWfsj.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(new Date()));
        edWfsj.setKeyListener(null);
        spImageList = (Spinner) findViewById(R.id.spin_image_list);
        mImageView = (ImageView) findViewById(R.id.imgview_1);
        GlobalMethod.changeAdapter(spHpzl, GlobalData.hpzlList, this);
        GlobalMethod.changeAdapter(spHpqz, GlobalData.hpqlList, this);
        spHpzl.setSelection(
                GlobalMethod.getPositionByKey(GlobalData.hpzlList, "02"), true);
        spHpqz.setSelection(
                GlobalMethod.getPositionByKey(GlobalData.hpqlList, "320000"),
                true);

        edHphm.setText("F");
        kvWfdd = new KeyValueBean("", "");
        bigList = new ArrayList<String>();
        zpList = new ArrayList<VioFxcFileBean>();
        findViewById(R.id.but_wfsj).setOnClickListener(butClick);
        findViewById(R.id.but_wfdd).setOnClickListener(butClick);
        findViewById(R.id.but_wfxw).setOnClickListener(butClick);
        findViewById(R.id.btn_show_image).setOnClickListener(butClick);
        // 检测
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            GlobalMethod.showDialogWithListener("错误信息", "本模块需要SD存储卡,请加载后继续!",
                    "确定", dc, self);
            return;
        }
        FxczfDao dao = new FxczfDao(self);
        String op = getIntent().getStringExtra("operMod");
        if (TextUtils.equals("readonly", op)) {
            operMod = READONLY;
            fxczf = (VioFxczfBean) getIntent().getSerializableExtra("fxc");
            zpList = (ArrayList<VioFxcFileBean>) dao.queryFxczfFileByFId(fxczf
                    .getId());
            GlobalMethod.changeSpinnerSelect(spHpzl, fxczf.getHpzl(),
                    GlobalConstant.KEY);
            GlobalMethod.changeSpinnerSelect(spHpqz,
                    fxczf.getHphm().substring(0, 1), GlobalConstant.VALUE);
            edHphm.setText(fxczf.getHphm().substring(1));
            edWfsj.setText(fxczf.getWfsj());
            edWfdd.setText(fxczf.getWfdz());
            String wfdd = fxczf.getXzqh() + fxczf.getWfdd() + fxczf.getLddm()
                    + fxczf.getDdms();
            kvWfdd = new KeyValueBean(wfdd, fxczf.getWfdz());
            edWfxw.setText(fxczf.getWfxw());
            referImageList();
        } else {
            tzsbh = dao.getTodayFxczfId();
            String[] ar = dao.getLastWfdd();
            if (ar != null && WfddDao.isWfddOk(ar[0], getContentResolver())) {
                kvWfdd = new KeyValueBean(ar[0], ar[1]);
                edWfdd.setText(ar[1]);
                edWfxw.setText(ar[2]);
            }
        }
        dao.closeDb();
        //

        // 设置打印的名字，打印时在数据库中取
        printerInfo = new KeyValueBean(
                GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME),
                GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS));

        // String printerName = !TextUtils.isEmpty(printerInfo.getValue()) ?
        // printerInfo
        // .getKey() : "无打印机";
        setTitle("非现场执法");
        // setTitle("非现场编号: " + tzsbh + " 打印机：" + printerName);
        Log.e("AcdTakePhotoActivity", "onCreate");
        spImageList.setOnItemSelectedListener(sl);
        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageIndex > -1 && zpList != null && !zpList.isEmpty()
                        && zpList.size() > imageIndex) {
                    showImageActivity(zpList.get(imageIndex).getWjdz());
                }
            }
        });
    }

    private void showImageActivity(String file) {
        Intent intent = new Intent(self, ShowImageActivity.class);
        intent.putExtra("image", file);
        startActivity(intent);
    }

    OnSpinnerItemSelected sl = new OnSpinnerItemSelected() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (imageIndex != position) {
                imageIndex = position;
                threadReferImageView();
            }
        }
    };

    @Override
    protected void onResume() {
        referImageList();
        Log.e("JbywFxc onResume", "" + GlobalSystemParam.isPreviewPhoto + "/"
                + imageIndex);
        if (GlobalSystemParam.isPreviewPhoto && imageIndex > -1)
            threadReferImageView();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.e("JbywImageView", "onStop");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (isSaveFile || operMod == READONLY) {
            Intent i = new Intent();
            i.putExtra("operMod", operMod);
            setResult(RESULT_OK, i);
            finish();
        } else {
            // 本决定书没有保存
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("系统提醒")
                    .setMessage("本次处罚还没有保存，是否确定退出！")
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
        }
    }

    private View.OnClickListener butClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == findViewById(R.id.but_wfsj)) {
                GlobalMethod.changeTime(edWfsj, self);
            } else if (v == findViewById(R.id.btn_show_image)) {
                threadReferImageView();
            } else if (v == findViewById(R.id.but_wfdd)) {
                // 查询违法地点
                Intent intent = new Intent(self, ConfigWfddActivity.class);
                startActivityForResult(intent, REQCODE_WFDD);
            } else if (v == findViewById(R.id.but_wfxw)) {
                Intent intent = new Intent(self, ConfigWfdmActivity.class);
                intent.putExtra("comefrom", 1);
                startActivityForResult(intent, REQCODE_WFXW);
            }

        }
    };

    private void delImage() {
        int pos = spImageList.getSelectedItemPosition();
        if (pos < 0 || bigList == null || bigList.size() <= pos)
            return;
        File bf = new File(bigList.get(pos));
        bf.delete();
        bigList.remove(pos);
        VioFxcFileBean zp = zpList.get(pos);
        File mf = new File(zp.getWjdz());
        mf.delete();
        zpList.remove(pos);
        imageIndex = zpList.size() - 1;
        if (zpList.isEmpty()) {
            mImageView.setImageBitmap(null);
            mImageView.destroyDrawingCache();
        } else
            threadReferImageView();
    }

    private DialogInterface.OnClickListener delImageDialog = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            delImage();
        }
    };
    private ProgressDialog progressDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fxczf_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (GlobalData.connCata == ConnCata.OFFCONN
                || GlobalData.connCata == ConnCata.UNKNOW)
            menu.removeItem(R.id.menu_upload);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (operMod == READONLY) {
            GlobalMethod.showErrorDialog("不能在此模式下操作", self);
            return true;
        }
        switch (item.getItemId()) {
            // 打开照相机
            case R.id.open_camare: {
                if (isSaveFile) {
                    GlobalMethod.showErrorDialog("记录已保存，不能再拍照了！", self);
                    return true;
                }
                if (TextUtils.isEmpty(edWfdd.getText())
                        || TextUtils.isEmpty(edWfsj.getText())) {
                    GlobalMethod.showErrorDialog("违法时间和违法地点是必填项", self);
                    return true;
                }
                if (bigList.size() >= 3) {
                    GlobalMethod.showErrorDialog("最多可拍摄3张照片，请删除一张后再拍摄", self);
                    return true;
                }
                startTakePhoto();
            }
            return true;
            case R.id.print_file: {
                // 打印文本，已保存打印保存
                if (!isSaveText && !isSaveFile) {
                    VioFxczfBean temp = getFxcFromView();
                    String err = vaidatePic(temp, false);
                    if (!TextUtils.isEmpty(err)) {
                        GlobalMethod.showErrorDialog(err, self);
                        return true;
                    }
                    fxczf = temp;
                    isSaveText = true;
                }
                printFxcTzs();
            }
            return true;
            case R.id.save_file: {
                if (isSaveFile) {
                    GlobalMethod.showErrorDialog("已保存,无需重复保存", self);
                    return true;
                }
                VioFxczfBean temp = getFxcFromView();
                String err = vaidatePic(temp, true);
                if (!TextUtils.isEmpty(err)) {
                    GlobalMethod.showErrorDialog(err, self);
                    return true;
                }
                fxczf = temp;
                QueryVehHandler qvHandler = new QueryVehHandler(this);
                QueryVehThread thread = new QueryVehThread(qvHandler,
                        fxczf.getHpzl(), fxczf.getHphm());
                thread.start();
            }
            return true;
            case R.id.menu_del_image:
                if (isSaveFile) {
                    GlobalMethod.showErrorDialog("记录已保存，不能删除图片", self);
                    return true;
                }
                int pos = spImageList.getSelectedItemPosition();
                if (pos < 0 || bigList == null || bigList.size() <= pos)
                    return true;
                GlobalMethod.showDialogTwoListener("系统提示", "是否删除图片，该操作将无法恢复", "删除",
                        "取消", delImageDialog, self);
                return true;
            default:
                break;
        }
        return false;
    }

    private void saveFxcIntoDb() {
        FxczfDao dao = new FxczfDao(self);
        long row = dao.insertFxczfDb(fxczf);
        String result = row + ",";
        if (row > 0) {
            fxczf.setId(String.valueOf(row));
            for (int i = 0; i < zpList.size(); i++) {
                String smallZp = zpList.get(i).getWjdz();
                long l = dao.insertFxcFile(smallZp, fxczf);
                result += l + ",";
            }
            isSaveFile = true;
        }
        Log.e("fxc", result);
        GlobalMethod.showDialog("系统提示", row > 0 ? "非现场保存成功" : "非现场保存失败",
                "确定", self);
        dao.closeDb();
    }

    static class QueryVehHandler extends Handler {

        private final WeakReference<JbywFxcActivity> myActivity;

        public QueryVehHandler(JbywFxcActivity activity) {
            myActivity = new WeakReference<JbywFxcActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            JbywFxcActivity ac = myActivity.get();
            if (ac != null) {
                ac.queryVehHandler(msg);
            }
        }
    }

    /**
     * 查询返回后根据情况决定是否提示民警
     *
     * @param msg
     */
    private void queryVehHandler(Message msg) {
        Bundle b = msg.getData();
        String s = b.getString("veh", "");
        if (TextUtils.isEmpty(s)) {
            saveFxcIntoDb();
            return;
        }
        JSONObject json = null;
        try {
            json = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null) {
            saveFxcIntoDb();
            return;
        }
        String err = json.optString("err", "");
        String bdjg = json.optString("bdjg", "");
        String mes = "";
        if (!TextUtils.isEmpty(err) && TextUtils.equals("未查询到机动车信息", err)) {
            //如果出现未查询到机动车信息，提示民警注意
            mes = "未查询到机动车信息，是否保存？";
        } else if (!TextUtils.isEmpty(bdjg)) {
            mes = bdjg + "，是否保存？";
        }
        if (!TextUtils.isEmpty(mes)) {
            GlobalMethod.showDialogTwoListener("系统提示", mes, "保存", "不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveFxcIntoDb();
                }
            }, self);
            return;
        }
        saveFxcIntoDb();
    }

    private VioFxczfBean getFxcFromView() {
        VioFxczfBean temp = new VioFxczfBean();
        if (kvWfdd != null && !TextUtils.isEmpty(kvWfdd.getKey())
                && kvWfdd.getKey().length() == 18) {
            String key = kvWfdd.getKey();
            temp.setXzqh(key.substring(0, 6));
            temp.setWfdd(key.substring(6, 11));
            temp.setLddm(key.substring(11, 15));
            temp.setDdms(key.substring(15, 18));
            temp.setWfdz(edWfdd.getText().toString());
        }
        Editable hphm = edHphm.getText();
        temp.setHpzl(GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                GlobalData.hpzlList, GlobalConstant.KEY));
        String hpqz = spHpqz.getSelectedItem() == null ? "" : spHpqz
                .getSelectedItem().toString();
        String hp = hpqz + hphm.toString().toUpperCase();
        temp.setHphm(hp);
        temp.setWfxw(edWfxw.getText().toString());
        temp.setWfsj(edWfsj.getText().toString());
        temp.setTzsh(tzsbh);
        temp.setPhotos(zpList == null ? "0" : String.valueOf(zpList.size()));
        temp.setCjjg(GlobalData.grxx.get(GlobalConstant.KSBMBH));
        temp.setFzjg(hp.length() > 2 ? hp.substring(0, 2) : "");
        temp.setTzrq(temp.getWfsj());
        temp.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
        temp.setSbbh(GlobalData.serialNumber);
        return temp;
    }

    private String checkHphm(String hpzl, String hphm) {
        if (TextUtils.isEmpty(hphm) || hphm.length() < 6)
            return "号牌号码长度不够";
        String hm = hphm.substring(1, hphm.length());
        if (TextUtils.equals(hpzl, "51") || TextUtils.equals(hpzl, "52")) {
            if (hphm.length() != 8)
                return "新能源车的号牌长度为八位";
        } else {
            if ((TextUtils.equals(hpzl, "15") || TextUtils.equals(hpzl, "16") || TextUtils.equals(hpzl, "23"))) {
                if (hm.length() != 5)
                    return "教练车、挂车号牌为六位，无需汉字";
            } else if (hphm.length() != 7) {
                return "普通号牌号码长度应该为七位";
            }
        }
        if (!GlobalMethod.isNumberOrAZ(hm)) {
            return "号牌包含非法字符";
        }

        if (hphm.toUpperCase().indexOf("I") >= 0 || hphm.toUpperCase().indexOf("O") >= 0) {
            return "号牌中不能包含字母O或者I，可能为数字零或者一";
        }

        return null;
    }

    private String vaidatePic(VioFxczfBean temp, boolean isCheckImage) {
        String wfdd = temp.getXzqh() + temp.getWfdd() + temp.getLddm()
                + temp.getDdms();
        if (TextUtils.isEmpty(wfdd) || TextUtils.isEmpty(temp.getWfdz())
                || wfdd.length() != 18) {
            return "违法地点不能为空!";
        }
        String err = checkHphm(temp.getHpzl(), temp.getHphm());
        if (!TextUtils.isEmpty(err)) {
            return err;
        }
        if (TextUtils.isEmpty(temp.getWfxw())) {
            return "违法行为不能为空";
        }
        if (!TextUtils.equals("10393", temp.getWfxw()) && !TextUtils.equals("13446", temp.getWfxw())) {
            return "违法行为目前只允许10393和13446";
        }
        //WfdmBean wf = ViolationDAO.queryWfxwByWfdm(temp.getWfxw(),
        //        getContentResolver());
        //if (wf == null)
        //    return "违法代码错误!";
        // 查询无此号码或不在有效期内

        // 先前不要过滤,在这里过滤最好了,可以有提示
        //if (!ViolationDAO.isYxWfdm(wf)) {
        //   return "不是有效代码,不可以处罚!";
        //}

        MessageDao dao = new MessageDao(self);
        boolean isSer = dao.checkIsSeriousStreet(wfdd);
        dao.closeDb();

        if (isSer) {
            if (TextUtils.equals(temp.getWfxw(), "10393"))
                return "严管路段不允许使用10393代码";
        } else {
            if (TextUtils.equals("13446", temp.getWfxw()))
                return "不是严管路段，请不要使用13446代码！";
        }

        if (isCheckImage) {
            if (zpList == null || zpList.size() < 2) {
                return "至少需要2张照片";
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("JbywImageView", "onActivityResult");
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMER_REQUEST) {
                //
                camerResult();
            } else if (requestCode == REQCODE_WFDD) {
                Bundle b = data.getExtras();
                kvWfdd = new KeyValueBean(b.getString("wfddDm"),
                        b.getString("wfddMc"));
                edWfdd.setText(kvWfdd.getValue());
            } else if (requestCode == REQCODE_WFXW) {
                String wfxw = data.getStringExtra(Wfdmcode.VioCodeWfdm.WFXW);
                if (!TextUtils.isEmpty(wfxw))
                    edWfxw.setText(wfxw);
            }
        } else {
            if (requestCode == CAMER_REQUEST) {
                while (bigList.size() > zpList.size()) {
                    bigList.remove(bigList.size() - 1);
                }
            }
        }

    }

    DialogInterface.OnClickListener dc = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    /**
     * 多线程刷新图片
     */
    private void threadReferImageView() {
        Handler handler = new ChangeImageHandler(JbywFxcActivity.this);
        ChangeImageThread thread = new ChangeImageThread(handler);
        thread.start();
    }

    /**
     * 查询机动车信息线程类
     */
    class QueryVehThread extends Thread {
        private Handler mVehHandler;
        private String hpzl;
        private String hphm;

        public QueryVehThread(Handler h, String hpzl, String hphm) {
            this.mVehHandler = h;
            this.hphm = hphm;
            this.hpzl = hpzl;
        }

        @Override
        public void run() {
            if (GlobalData.connCata == ConnCata.OFFCONN)
                GlobalData.connCata = ConnCata.INSIDECONN;
            RestfulDao dao = RestfulDaoFactory.getDao();
            String s = dao.jycxQueryVeh(hpzl, hphm);
            Message msg = mVehHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("veh", s);
            msg.setData(b);
            mVehHandler.sendMessage(msg);
        }
    }


    private Bitmap currentSmallImage;

    class ChangeImageThread extends Thread {
        private Handler mHandler;

        public ChangeImageThread(Handler mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public void run() {
            Log.e("ChangeImageThread", "on run");
            boolean isOk = false;
            if (imageIndex > -1 && zpList != null && !zpList.isEmpty()) {
                VioFxcFileBean zp = zpList.get(imageIndex);
                File smallFn = new File(zp.getWjdz());
                Log.e("ChangeImageThread", "file " + smallFn.getAbsolutePath());
                if (smallFn.exists()) {
                    currentSmallImage = GlobalMethod.getImageFromFile(smallFn
                            .getAbsolutePath());
                }
                isOk = currentSmallImage != null;
            }
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("isOk", isOk);
            msg.setData(b);
            mHandler.sendMessage(msg);
            Log.e("ChangeImageThread", "isOk " + isOk);

        }
    }

    static class ChangeImageHandler extends Handler {

        private final WeakReference<JbywFxcActivity> myActivity;

        public ChangeImageHandler(JbywFxcActivity activity) {
            myActivity = new WeakReference<JbywFxcActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            JbywFxcActivity ac = myActivity.get();
            if (ac != null) {
                Bundle data = msg.getData();
                if (data != null && data.getBoolean("isOk"))
                    ac.showSmallImage();
            }
        }
    }

    private void showSmallImage() {
        if (currentSmallImage != null) {
            mImageView.setImageBitmap(currentSmallImage);
        }
    }

    private void referImageList() {
        if (zpList == null)
            zpList = new ArrayList<VioFxcFileBean>();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < zpList.size(); i++) {
            list.add("第" + (i + 1) + "张图片");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(self,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImageList.setAdapter(adapter);
        spImageList.setSelection(imageIndex);
    }

    private void startTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = GlobalMethod.createImageFile(JbywFxcActivity.this,
                        false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                bigList.add(photoFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMER_REQUEST);
            }
        }
    }

    private void camerResult() {
        if (bigList == null || bigList.isEmpty())
            return;
        String mCurrentPhotoPath = bigList.get(bigList.size() - 1);
        File image = new File(mCurrentPhotoPath);
        Log.e("mCurrentPhotoPath", mCurrentPhotoPath);
        if (!image.exists()) {
            Toast.makeText(self, "照片拍摄失败", Toast.LENGTH_LONG).show();
            bigList.remove(bigList.size() - 1);
            return;
        }
        File dir = image.getParentFile();
        String fn = image.getName();
        dir = new File(dir, "small");
        if (!dir.exists())
            dir.mkdirs();
        File smallF = new File(dir, fn);
        String text = "拍摄时间："
                + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        text += " 拍摄地点：" + edWfdd.getText();
        Bitmap smallImage = GlobalMethod.compressBitmap(mCurrentPhotoPath, 800,
                text);
        if (smallImage == null) {
            Toast.makeText(self, "照片压缩失败", Toast.LENGTH_LONG).show();
            bigList.remove(bigList.size() - 1);
            return;
        }
        boolean isSave = GlobalMethod.savePicIntoFile(smallImage, smallF);
        if (!isSave) {
            Toast.makeText(self, "照片保存失败", Toast.LENGTH_LONG).show();
            bigList.remove(bigList.size() - 1);
            return;
        }
        VioFxcFileBean zp = new VioFxcFileBean();
        zp.setWjdz(smallF.getAbsolutePath());
        zpList.add(zp);
        imageIndex = zpList.size() - 1;
        showImageActivity(smallF.getAbsolutePath());

    }

    private void printFxcTzs() {
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

        if (!TextUtils.isEmpty(printerInfo.getValue()) && btp == null) {
            btp = new BlueToothPrint(printerInfo.getValue());
        }

        if (btp == null)
            return;
        if (btp.getBluetoothStatus() != BlueToothPrint.BLUETOOTH_STREAMED) {
            // 没有建立蓝牙串口流
            int errorStaus = btp.createSocket(btAdapter);
            if (errorStaus != BlueToothPrint.SOCKET_SUCCESS) {
                GlobalMethod.showErrorDialog(
                        btp.getBluetoothCodeMs(errorStaus), self);
                return;
            }
        }
        List<JdsPrintBean> content = PrintJdsTools.getPrintFxczfContent(fxczf,
                self);
        int status = btp.printJdsByBluetooth(content);
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btp != null) {
            btp.closeConn();
        }
    }
}
