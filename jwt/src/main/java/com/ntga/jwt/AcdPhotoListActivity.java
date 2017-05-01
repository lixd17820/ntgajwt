package com.ntga.jwt;

import java.util.ArrayList;
import java.util.List;

import com.acd.simple.provider.AcdSimple;
import com.ntga.activity.ActionBarListActivity;
import com.ntga.activity.ActionBarSelectListActivity;
import com.ntga.adaper.CommTwoRowSelectListActivity;
import com.ntga.bean.AcdPhotoBean;
import com.ntga.bean.AcdSimpleBean;
import com.ntga.bean.TwoColTwoSelectBean;
import com.ntga.dao.AcdSimpleDao;
import com.ntga.dao.AcdUploadPhotoThread;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalMethod;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;

public class AcdPhotoListActivity extends ActionBarSelectListActivity {
    public static final int SEQ_NEW_ACD_PHOTO = 0;

    protected static final int MENU_MODIFY_ACD = 1;

    protected static final int MENU_DETAIL_ACD = 2;

    public static final int SEQ_MODIFY_ACD_PHOTO = 3;

    public static final int SEQ_SHOW_ACD_PHOTO = 4;

    private Button btnNewAcd, btnUpload, btnJycx, btnDel;

    private List<AcdPhotoBean> photos;

    private Context self;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.comm_four_btn_show_list);
        btnUpload = (Button) findViewById(R.id.btn_two);
        btnUpload.setText("上传");
        btnNewAcd = (Button) findViewById(R.id.btn_one);
        btnNewAcd.setText("新增");
        btnJycx = (Button) findViewById(R.id.btn_three);
        btnJycx.setText("简易程序");
        btnDel = (Button) findViewById(R.id.btn_four);
        btnDel.setText("删除");
        btnNewAcd.setOnClickListener(clickListener);
        btnUpload.setOnClickListener(clickListener);
        btnJycx.setOnClickListener(clickListener);
        btnDel.setOnClickListener(clickListener);
        changeDataFromDb();
        initView();

        getListView().setOnCreateContextMenuListener(contextMenuListener);
        //setTitle(getIntent().getStringExtra("title"));
    }

    private OnCreateContextMenuListener contextMenuListener = new OnCreateContextMenuListener() {

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenuInfo menuInfo) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int pos = mi.position;
            if (pos > -1 && photos != null && photos.size() > 0) {
                //AcdPhotoBean acd = photos.get(pos);
                //if (acd.getScbj() != 1)
                //	menu.add(Menu.NONE, MENU_MODIFY_ACD, Menu.NONE, "修改该事故");
                menu.add(Menu.NONE, MENU_DETAIL_ACD, Menu.NONE, "显示详细信息");
            }
        }
    };


    /**
     * 按扭的监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnNewAcd) {
                Intent intent = new Intent(self, AcdTakePhotoActivity.class);
                intent.putExtra(AcdSimpleDao.OPER_MOD, AcdSimpleDao.ACD_MOD_NEW);
                startActivityForResult(intent, SEQ_NEW_ACD_PHOTO);
            } else {
                if (selectedIndex < 0 || photos == null
                        || photos.get(selectedIndex) == null) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return;
                }
                AcdPhotoBean acd = photos.get(selectedIndex);
                if (v == btnUpload) {
                    if (acd.getScbj() == 1) {
                        GlobalMethod.showErrorDialog("记录已上传，无需重复上传", self);
                        return;
                    }
                    uploadAcd(acd);
                } else if (v == btnJycx) {
                    String wsbh = acd.getSgbh();
                    List<AcdSimpleBean> l = AcdSimpleDao.getAllAcd(
                            getContentResolver(), AcdSimple.AcdDutySimple.WSBH
                                    + "='" + wsbh + "'"
                    );
                    if (l == null || l.size() == 0) {
                        Intent intent = new Intent(self,
                                AcdJycxJbqklrActivity.class);
                        intent.putExtra(AcdSimpleDao.OPER_MOD,
                                AcdSimpleDao.ACD_MOD_PHOTO_NEW);
                        intent.putExtra(AcdTakePhotoActivity.ACD_PHOTO_BEAN,
                                acd);
                        startActivity(intent);
                    } else {
                        GlobalMethod.showErrorDialog("已存在简易程序,无需重复录入,也可删除重录",
                                self);
                    }

                } else if (v == btnDel) {
                    GlobalMethod.showDialogTwoListener("系统提示",
                            "是否确定删除，此操作无法恢复", "删除", "取消", delRecodeListener,
                            self);
                }
            }
        }
    };

    /**
     * 删除对话框中删除的监听
     */
    private DialogInterface.OnClickListener delRecodeListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            AcdPhotoBean acd = photos.get(selectedIndex);
            AcdSimpleDao.delAcdPhotoRecode(getContentResolver(), acd);
            changeDataFromDb();
            getCommAdapter().notifyDataSetChanged();
            selectedIndex = -1;
        }
    };

    /**
     * 重新加载数据列表，并更新显示列表，不触发重显示列表
     */
    private void changeDataFromDb() {
        photos = AcdSimpleDao.getAllAcdPhoto(getContentResolver(), null);
        if (beanList == null)
            beanList = new ArrayList<TwoColTwoSelectBean>();
        createBeanFromAcdPhoto(photos);
        setTitle("事故图片－共" + photos.size() + "条");
    }

    /**
     * 上传记录的方法实现
     *
     * @param acd
     */
    protected void uploadAcd(AcdPhotoBean acd) {
        progressDialog = new ProgressDialog(self);
        int maxStep = acd.getPhoto().size() + 1;
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在上传事故信息...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxStep * 25);
        progressDialog.show();
        AcdUploadPhotoThread thread = new AcdUploadPhotoThread(upHandler, acd);
        thread.doStart();
    }

    /**
     * 根据数据列表，重加载显示列表数据
     *
     * @param photos
     */
    private void createBeanFromAcdPhoto(List<AcdPhotoBean> photos) {
        beanList.clear();
        for (AcdPhotoBean photo : photos) {
            String text1 = photo.getSgbh() + "|" + photo.getSgsj();
            String text2 = getString(R.string.acd_position) + ":"
                    + photo.getSgdd();
            boolean isSc = photo.getScbj() == 1;
            beanList.add(new TwoColTwoSelectBean(text1, text2, isSc, false));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEQ_NEW_ACD_PHOTO
                || requestCode == SEQ_MODIFY_ACD_PHOTO) {
            if (resultCode == RESULT_OK) {
                changeDataFromDb();
                getCommAdapter().notifyDataSetChanged();
                selectedIndex = -1;
            }
        }
    }

    /**
     * 上传回调
     */
    private Handler upHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            int step = 0;
            switch (what) {
                case GlobalConstant.WHAT_ERR:
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Bundle data = msg.getData();
                    String err = data.getString("err");
                    GlobalMethod.showErrorDialog(err, self);
                    break;
                case GlobalConstant.WHAT_RECODE_OK:
                    step = msg.arg1;
                    progressDialog.setProgress(step);
                    break;
                case GlobalConstant.WHAT_PHOTO_OK:
                    step = msg.arg1;
                    progressDialog.setProgress(step);
                    progressDialog.setMessage("正在上传第" + (step / 25) + "张图片");
                    break;
                case GlobalConstant.WHAT_ALL_OK:
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (msg.getData() != null) {
                        long recID = msg.getData().getLong("xtbh");
                        int acdID = msg.getData().getInt("acdID");
                        AcdSimpleDao.updateAcdPhotoRecode(getContentResolver(),
                                recID, acdID);
                        AcdSimpleDao.updateAcdPhotoRecodeScbj(getContentResolver(),
                                acdID);
                        changeDataFromDb();
                        getCommAdapter().notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acd_photo_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check_detail:
                if (selectedIndex < 0 || photos == null
                        || photos.get(selectedIndex) == null) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return true;
                }
                AcdPhotoBean acd = photos.get(selectedIndex);
                Intent intent = new Intent(self, AcdTakePhotoActivity.class);
                intent.putExtra(AcdSimpleDao.PHOTO_BEAN, acd);
                intent.putExtra(AcdSimpleDao.OPER_MOD,
                        AcdSimpleDao.ACD_MOD_SHOW);
                startActivityForResult(intent, SEQ_SHOW_ACD_PHOTO);
                return true;
        }
        return false;
    }

}
