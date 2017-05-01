package com.ntga.jwt;

import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.ntga.bean.KeyValueBean;
import com.ntga.bean.RepairBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.RepairDao;
import com.ntga.zapc.ZapcReturn;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RepairJtssActivity extends ActionBarActivity {

    protected static final int FIND_BXDD = 0;
    protected static final int CAMER_REQUEST = 1;
    private Context self;
    private Button btnSaveRep, btnTakePic, btnQuite, btnFindBxdd, btnRepUpload;
    private EditText editBxdd, EditBxnr;
    private Spinner spinBxxm, spinSide;
    private String[] bxItem, bxSide;
    private RepairBean repair;
    private KeyValueBean kvBxdd;
    // -------------------------------------------------------------
    private ImageView imgView;

    private String picFilename = "";
    private Uri uri;
    private boolean isSave;
    private ProgressDialog progressDialog;
    private Bitmap litMap;
    private static String TAG = "RepairJtssActivity";
    private File picDir = new File("/sdcard/jwtpic");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair_jtss);
        bxItem = getResources().getStringArray(R.array.bx_item);
        bxSide = getResources().getStringArray(R.array.bx_side);
        self = this;
        isSave = false;
        btnSaveRep = (Button) findViewById(R.id.btn_rep_save);
        btnTakePic = (Button) findViewById(R.id.btn_rep_pic);
        btnQuite = (Button) findViewById(R.id.btn_rep_quite);
        btnFindBxdd = (Button) findViewById(R.id.btn_rep_bxdd);
        btnRepUpload = (Button) findViewById(R.id.btn_rep_upload);
        editBxdd = (EditText) findViewById(R.id.edit_rep_bxdd);
        EditBxnr = (EditText) findViewById(R.id.edit_rep_bxnr);
        spinBxxm = (Spinner) findViewById(R.id.spin_rep_item);
        spinSide = (Spinner) findViewById(R.id.spin_rep_side);
        imgView = (ImageView) findViewById(R.id.imv_rep_pic);
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(self,
                R.layout.spinner_item, bxItem);
        itemAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBxxm.setAdapter(itemAdapter);
        ArrayAdapter<String> sideAdapter = new ArrayAdapter<String>(self,
                R.layout.spinner_item, bxSide);
        sideAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSide.setAdapter(sideAdapter);

        btnSaveRep.setOnClickListener(btnListener);
        btnTakePic.setOnClickListener(btnListener);
        btnFindBxdd.setOnClickListener(btnListener);
        btnRepUpload.setOnClickListener(btnListener);
        btnQuite.setOnClickListener(btnListener);
        repair = new RepairBean();
        kvBxdd = new KeyValueBean("", "");
        setTitle("交通设施报修");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("RepairActivity", requestCode + "/" + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == FIND_BXDD) {
                if (data == null)
                    return;
                Bundle b = data.getExtras();
                if (b != null) {
                    kvBxdd.setKey(b.getString("wfddDm"));
                    kvBxdd.setValue(b.getString("wfddMc"));
                    editBxdd.setText(b.getString("wfddMc"));
                }
            } else if (requestCode == CAMER_REQUEST) {
                String bigFile = GlobalMethod.getBitmapFilePath(
                        getContentResolver(), uri);
                Log.e("repair", bigFile);
                if (!TextUtils.isEmpty(bigFile)) {
                    Bitmap smallImg = GlobalMethod.compressBitmap(bigFile, 600,
                            true);
                    picFilename = GlobalMethod.savePicIntoSmall(smallImg,
                            picDir);
                    smallImg.recycle();

                    repair.setPic(picFilename);
                    litMap = GlobalMethod.compressBitmap(picFilename, 100,
                            false);
                    imgView.setImageBitmap(litMap);
                }
            }
        }
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnFindBxdd) {
                Intent intent = new Intent(self, ConfigWfddActivity.class);
                startActivityForResult(intent, FIND_BXDD);
            } else if (v == btnSaveRep) {
                if (repair.getScbj() > 0 || isSave) {
                    GlobalMethod.showErrorDialog("该报修已保存或已上传，无需重复保存！", self);
                    return;
                }
                String err = checkSaveRepair();
                if (!TextUtils.isEmpty(err)) {
                    GlobalMethod.showErrorDialog(err, self);
                    return;
                }
                long id = 0L;
                if (repair.getId() != 0) {
                    id = RepairDao.updateRepair(getContentResolver(), repair);
                } else {
                    if (!TextUtils.isEmpty(picFilename)) {
                        repair.setPic(picFilename);
                        String res = RepairDao.insertRepair(
                                getContentResolver(), repair);
                        id = Long.valueOf(res);
                        if (id > 0) {
                            repair.setId(id);
                            isSave = true;
                        }
                    }
                }
                GlobalMethod.showDialog(getString(R.string.sys_prompt_text),
                        id > 0 ? "交通设施报修保存成功!" : "未能保存交通设施报修信息!", "确定", self);
            } else if (v == btnQuite) {
                if (!isSave) {
                    // 没有保存
                    GlobalMethod.showDialogTwoListener("系统提示", "记录没有保存，是否退出？",
                            "退出", "返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    setResult(RESULT_CANCELED);
                                    finish();
                                }
                            }, self
                    );
                    return;
                }
                if (repair.getScbj() < 1) {
                    // 没有上传
                    GlobalMethod.showDialogTwoListener("系统提示", "记录没有上传，是否退出？",
                            "退出", "返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }, self
                    );
                    return;
                }
                setResult(RESULT_OK);
                finish();
            } else if (v == btnRepUpload) {
                if (!isSave) {
                    GlobalMethod.showErrorDialog("请先保存记录才可以上传", self);
                    return;
                }
                if (repair.getScbj() > 0) {
                    GlobalMethod.showErrorDialog("该报修已经上传，无需重复上传！", self);
                    return;
                }
                UploadPicThread thread = new UploadPicThread();
                thread.doStart(uploadPicHander);
            } else if (v == btnTakePic) {
                if (isSave || repair.getScbj() > 0) {
                    GlobalMethod.showErrorDialog("记录已保存或上传，不要拍照了", self);
                }
                // uri = Uri.fromFile(new File(Environment
                // .getExternalStorageDirectory(), "tmp_contact_"
                // + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                String fileName = Environment.getExternalStorageDirectory()
                        + "tmp_contact_"
                        + String.valueOf(System.currentTimeMillis()) + ".jpg";
                Log.e("repair temp image", fileName);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                uri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Log.e("repair temp image", uri.toString());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // Intent intent = new Intent(self, CameraActivity.class);
                startActivityForResult(intent, CAMER_REQUEST);
            }
        }
    };

    // private String savePicIntoSmall() {
    // String smallPath = "";
    // if (smallImg != null) {
    // try {
    // File f = new File(picDir, System.currentTimeMillis() + ".jpg");
    // FileOutputStream fo = new FileOutputStream(f);
    // smallImg.compress(CompressFormat.JPEG, 50, fo);
    // fo.close();
    // smallPath = f.getPath();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // return smallPath;
    // }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //litMap.recycle();
    }

    private String checkSaveRepair() {
        if (TextUtils.isEmpty(picFilename))
            return "报修图片不能为空";
        if (TextUtils.isEmpty(kvBxdd.getKey())
                || TextUtils.isEmpty(kvBxdd.getValue())) {
            return "报修地点不能为空";
        }
        repair.setBxdd(editBxdd.getText().toString());
        repair.setXzqh(kvBxdd.getKey().substring(0, 4));
        if (TextUtils.isEmpty(EditBxnr.getText()))
            return "报修内容不能不空";
        repair.setBxnr(EditBxnr.getText().toString());
        repair.setItem((String) spinBxxm.getSelectedItem());
        repair.setBxsj(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date()));
        repair.setScbj(0);
        return null;
    }

    @Override
    public void onBackPressed() {
        if (isSave) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    class UploadPicThread extends Thread {

        private Handler mHandler;

        /**
         * 启动线程
         */
        public void doStart(Handler mHandler) {
            // 显示进度对话框
            progressDialog = new ProgressDialog(self);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在上传报修记录...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            this.mHandler = mHandler;
            this.start();
        }

        @Override
        public void run() {
            Message m = mHandler.obtainMessage();
            Bundle data = new Bundle();
            data.putInt("catalog", 1);
            RestfulDao dao = RestfulDaoFactory.getDao();
            WebQueryResult<ZapcReturn> re = dao.uploadRepair(repair);
            String err = GlobalMethod.getErrorMessageFromWeb(re);
            if (TextUtils.isEmpty(err)) {
                ZapcReturn z = re.getResult();
                if (z != null) {
                    if (TextUtils.equals(z.getCgbj(), "1")) {
                        // 系统返回记录上传成功
                        repair.setXtbh(re.getResult().getPcbh()[0]);
                        repair.setScbj(1);
                        re = dao.uploadRepPic(Long.valueOf(repair.getXtbh()),
                                repair.getPic(), mHandler);
                        err = GlobalMethod.getErrorMessageFromWeb(re);
                        if (TextUtils.isEmpty(err)) {
                            z = re.getResult();
                            if (z != null) {
                                RepairDao.updateRepair(getContentResolver(),
                                        repair);
                            }
                            data.putBoolean("isOk", true);
                            data.putString("msg", z.getScms());
                            data.putSerializable("rep", repair);
                            m.setData(data);
                            mHandler.sendMessage(m);
                            return;
                        } else {
                            data.putSerializable("rep", repair);
                            data.putString("msg", err);
                        }
                    } else
                        data.putString("msg", z.getScms());
                } else {
                    data.putString("msg", "上传失败");
                }
            } else {
                data.putString("msg", err);
            }
            m.setData(data);
            mHandler.sendMessage(m);
        }
    }

    /**
     * 上传图片控制回调
     */
    private Handler uploadPicHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (data != null) {
                int catalog = data.getInt("catalog");
                if (catalog == 0) {
                    int length = data.getInt("length");
                    int step = data.getInt("step");
                    progressDialog.setMax(length);
                    progressDialog.setProgress(step);
                    if (step < length) {
                        progressDialog.setMessage("正在上传报修图片...");
                    } else {
                        progressDialog.setMessage("报修图片已上传...");
                    }
                } else if (catalog == 1) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    boolean isOk = data.getBoolean("isOk");
                    String message = data.getString("msg");
                    if (isOk)
                        GlobalMethod.showDialog("系统提示", message, "知道了", self);
                    else
                        GlobalMethod.showErrorDialog(message, self);
                }
            }
        }
    };

}
