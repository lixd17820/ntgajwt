package com.ntga.jwt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.adaper.OnSpinnerItemSelected;
import com.ntga.adaper.ZapcRyWpxxListAdapter;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ZaPcdjDao;
import com.ntga.zapc.ZapcGzxxBean;
import com.ntga.zapc.ZapcReturn;
import com.ntga.zapc.ZapcRypcxxBean;
import com.ntga.zapc.ZapcWppcxxBean;
import com.ntga.zapc.Zapcxx;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZapcGzxxActivity extends ActionBarListActivity {
    private static final int MENU_PCJDC = 1;
    private static final int REQ_PCJDC = 4;
    private static final int REQ_PCRY = 5;
    // protected static final int MENU_DETAIL_PCXX = 0;
    protected static final int MENU_DELETE_PCXX = 3;
    protected static final int MENU_UPLOAD_PCXX = 2;
    private Spinner spXqxd, spXffs;
    private EditText edGzdd, edKssj, edFjrs;
    private ArrayList<KeyValueBean> xqxds;
    private Context self;
    private ZapcGzxxBean gzxx;
    private List<Zapcxx> ryWpxxList;

    private boolean isNew;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.zapc_gzxx_pcxxlb);

        // 初如化控件
        spXqxd = (Spinner) findViewById(R.id.Spinn_xqxd);
        edGzdd = (EditText) findViewById(R.id.Edit_gzdd);
        spXffs = (Spinner) findViewById(R.id.Spin_xffs);
        edKssj = (EditText) findViewById(R.id.Edit_kspcsj);
        edFjrs = (EditText) findViewById(R.id.Edit_fjrs);
        edKssj.setEnabled(false);
        // 初始化数据

        xqxds = ZaPcdjDao
                .getZapcLxxx(GlobalData.grxx.get(GlobalConstant.YBMBH),
                        getContentResolver());
        GlobalMethod.changeAdapter(spXqxd, xqxds, (Activity) self);
        GlobalMethod.changeAdapter(spXffs,
                ZaPcdjDao.zapcDic.get(ZaPcdjDao.XFFS), (Activity) self);
        gzxx = (ZapcGzxxBean) getIntent().getSerializableExtra("gzxx");

        // 从列表选择中获取盘查信息，并对界面赋值
        isNew = (gzxx == null);
        if (!isNew) {
            // isOver = !TextUtils.isEmpty(gzxx.getJssj());
            spXqxd.setSelection(GlobalMethod.getPositionByKey(xqxds,
                    gzxx.getXlmc()));
            spXffs.setSelection(GlobalMethod.getPositionByKey(
                    ZaPcdjDao.zapcDic.get(ZaPcdjDao.XFFS), gzxx.getXffs()));
            edKssj.setText(ZaPcdjDao.changeDptModNor(gzxx.getKssj()));
            edFjrs.setText(gzxx.getFjrs());
            if (!TextUtils.isEmpty(gzxx.getJssj())) {
                LinearLayout line = (LinearLayout) findViewById(R.id.bottom_but);
                RelativeLayout main = (RelativeLayout) findViewById(R.id.main_relative_layout);
                main.removeView(line);
            }
        } else {
            gzxx = new ZapcGzxxBean();
            edKssj.setText(ZaPcdjDao.sdfNor.format(new Date()));
            edFjrs.setText("1");
        }
        if (TextUtils.isEmpty(gzxx.getJssj())) {
            findViewById(R.id.but_gz_pcry).setOnClickListener(startPcBut);
            findViewById(R.id.but_pause_gz).setOnClickListener(pausePcBut);
            findViewById(R.id.but_over_gz).setOnClickListener(overPcBut);
        }
        spXqxd.setEnabled(isNew);
        spXffs.setEnabled(isNew);
        edGzdd.setEnabled(isNew);
        edFjrs.setEnabled(isNew);
        refershView();

        // 巡区巡段变化，对工作地点进行相应的赋值
        spXqxd.setOnItemSelectedListener(new OnSpinnerItemSelected() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {
                KeyValueBean kv = (KeyValueBean) adapter.getAdapter().getItem(
                        position);
                edGzdd.setText(kv.getValue());
            }

        });
        registerForContextMenu(getListView());
        getListView().setOnCreateContextMenuListener(
                new View.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu,
                                                    View arg1, ContextMenuInfo menuInfo) {
                        AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfo;
                        int pos = mi.position;
                        if (pos > -1) {
                            Zapcxx pcxx = ryWpxxList.get(pos);
                            if ("1".equals(gzxx.getCsbj())) {
                                if ("0".equals(pcxx.getScbj()))
                                    menu.add(Menu.NONE, MENU_UPLOAD_PCXX,
                                            Menu.NONE, "上传盘查信息");
                            } else {
                                if (pcxx.getPcZl() == Zapcxx.PCRYXXZL
                                        && TextUtils.isEmpty(gzxx.getJssj())) {
                                    menu.add(Menu.NONE, MENU_PCJDC, Menu.NONE,
                                            "盘查机动车");
                                }
                                menu.add(Menu.NONE, MENU_DELETE_PCXX,
                                        Menu.NONE, "删除盘查信息");
                            }
                        }
                    }
                }
        );
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ryWpxxList != null && ryWpxxList.size() > 0 && position > -1) {
                    Zapcxx zapcxx = ryWpxxList.get(position);
                    if (zapcxx.getPcZl() == Zapcxx.PCRYXXZL) {
                        ZapcRypcxxBean ryxx = ZaPcdjDao.queryRyxxById(zapcxx.getId(),
                                getContentResolver());
                        Intent intent = new Intent(self, ZapcRyxxActivity.class);
                        intent.putExtra("pcryxx", ryxx);
                        startActivity(intent);
                    } else if (zapcxx.getPcZl() == Zapcxx.PCWPXXZL) {
                        ZapcWppcxxBean wpxx = ZaPcdjDao.queryWpxxById(zapcxx.getId(),
                                getContentResolver(), self);
                        Intent intent = new Intent(self, ZapcJdcActivity.class);
                        intent.putExtra("pcwpxx", wpxx);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    // 结束盘查
    private View.OnClickListener overPcBut = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            String err = saveGzxx();
            if (!TextUtils.isEmpty(err)) {
                GlobalMethod.showErrorDialog(err, self);
            } else {
                closeViewAndReturn(true);
            }
        }
    };
    // 暂停盘查
    private View.OnClickListener pausePcBut = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            String err = saveGzxx();
            if (!TextUtils.isEmpty(err)) {
                GlobalMethod.showErrorDialog(err, self);
            } else {
                closeViewAndReturn(false);
            }
        }
    };
    // 盘查人员
    private View.OnClickListener startPcBut = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            String err = saveGzxx();
            if (!TextUtils.isEmpty(err)) {
                GlobalMethod.showErrorDialog(err, self);
            } else {
                if (isNew) {
                    saveNewGzxxIntoDb();
                    isNew = false;
                }
                Intent intent = new Intent(ZapcGzxxActivity.this,
                        ZapcRyxxActivity.class);
                intent.putExtra("gzxx", gzxx);
                startActivityForResult(intent, REQ_PCRY);
            }
        }
    };

    private void setTitleByPcxx() {
        String title = "治安盘查工作";
        if (ryWpxxList != null && ryWpxxList.size() > 0)
            title += "--共有" + ryWpxxList.size() + "人员或物品信息";
        setTitle(title);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item
                .getMenuInfo();
        final int pos = mi.position;
        if (pos > -1) {
            final Zapcxx pcxx = ryWpxxList.get(pos);
            switch (item.getItemId()) {
                case MENU_UPLOAD_PCXX:
                    UploadRyWpThread thread = new UploadRyWpThread(uploadHandler,
                            pcxx);
                    thread.doStart();
                    break;
                case MENU_PCJDC:
                    Intent intent = new Intent(ZapcGzxxActivity.this,
                            ZapcJdcActivity.class);
                    intent.putExtra("pcrybh", pcxx.getId());
                    intent.putExtra("gzbh", gzxx.getId());
                    intent.putExtra("pcdd", pcxx.getPcdd());
                    startActivityForResult(intent, REQ_PCJDC);
                    break;
                case MENU_DELETE_PCXX:
                    if ("0".equals(pcxx.getScbj())) {
                        GlobalMethod.showDialogTwoListener("系统提示", "是否确定删除?", "确定",
                                "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        if (pcxx.getPcZl() == Zapcxx.PCRYXXZL) {
                                            ZaPcdjDao.delPcryxxById(pcxx.getId(),
                                                    getContentResolver());
                                        } else if (pcxx.getPcZl() == Zapcxx.PCWPXXZL) {
                                            ZaPcdjDao.delPcwyxxById(pcxx.getId(),
                                                    getContentResolver());
                                        }
                                        refershView();
                                    }
                                }, self
                        );
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * 验证接口数据返回状态
     *
     * @param re
     * @return
     */
    private boolean checkWebResult(WebQueryResult<ZapcReturn> re) {
        return re.getStatus() == HttpStatus.SC_OK && re.getResult() != null
                && TextUtils.equals(re.getResult().getCgbj(), "1")
                && re.getResult().getPcbh() != null
                && re.getResult().getPcbh().length > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_PCRY || requestCode == REQ_PCJDC) {
                refershView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refershView() {
        ryWpxxList = ZaPcdjDao.getPcxxByGzbh(gzxx, getContentResolver(), self);
        getListView().setAdapter(
                new ZapcRyWpxxListAdapter((Activity) self, ryWpxxList));
        setTitleByPcxx();
    }

    /**
     * 将界面的内容保存到工作信息中，返回的字串表示有错误
     *
     * @return
     */
    private String saveGzxx() {
        String fjrs = edFjrs.getText().toString();
        if (TextUtils.isEmpty(fjrs) || !TextUtils.isDigitsOnly(fjrs))
            return "请正确填写辅警人数";
        if (spXffs.getSelectedItemPosition() == 0)
            return "请选择巡防方式";
        gzxx.setFjrs(fjrs);
        gzxx.setGzdd(edGzdd.getText().toString());
        gzxx.setXlmc(GlobalMethod.getKeyFromSpinnerSelected(spXqxd,
                GlobalConstant.KEY));
        gzxx.setXffs(GlobalMethod.getKeyFromSpinnerSelected(spXffs,
                GlobalConstant.KEY));
        gzxx.setCsbj("0");
        gzxx.setDjdw(GlobalData.grxx.get(GlobalConstant.YBMBH));
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        gzxx.setJybh(jh.length() == 8 ? jh.substring(2) : jh);
        gzxx.setKssj(ZaPcdjDao.changeNorModDpt(edKssj.getText().toString()));
        gzxx.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
        return null;
    }

    /**
     * 保存新的工作信息，并将ID值赋于对象
     */
    private void saveNewGzxxIntoDb() {
        // 是新增加
        ZaPcdjDao.insertGzxx(gzxx, getContentResolver());
        int id = ZaPcdjDao.getMaxGzxxId(getContentResolver());
        gzxx.setId(String.valueOf(id));
    }

    /**
     * 暂停或结束盘查
     *
     * @param isOver 结束为TRUE,暂停为FALSE
     */
    private void closeViewAndReturn(boolean isOver) {
        Intent i = new Intent();
        // Bundle b = new Bundle();
        if (isOver)
            // 已结束，设置结束时间
            gzxx.setJssj(ZaPcdjDao.sdfDpt.format(new Date()));
        if (isNew)
            saveNewGzxxIntoDb();
        else
            ZaPcdjDao.updateGzxx(gzxx, getContentResolver());
        // b.putBoolean("isNew", isNew);
        // b.putSerializable("gzxx", gzxx);
        // i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();

    }

    class UploadRyWpThread extends Thread {
        private Handler mHandler;
        private Zapcxx pcxx;

        public UploadRyWpThread(Handler handler, Zapcxx pcxx) {
            this.mHandler = handler;
            this.pcxx = pcxx;
        }

        /**
         * 启动线程
         */
        public void doStart() {
            // 显示进度对话框
            progressDialog = new ProgressDialog(self);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("系统正在上传请稍等...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            this.start();
        }

        /**
         * 线程运行，上传盘查，成功后发送信号给进度条
         */
        @Override
        public void run() {
            RestfulDao dao = RestfulDaoFactory.getDao();
            WebQueryResult<ZapcReturn> re = null;
            if (pcxx.getPcZl() == Zapcxx.PCRYXXZL) {
                ZapcRypcxxBean ryxx = ZaPcdjDao.queryRyxxById(pcxx.getId(),
                        getContentResolver());
                // 更新上传标记
                re = dao.uploadZapcRypcxx(ryxx, gzxx.getId(), gzxx.getKssj());
                if (checkWebResult(re)) {
                    ZaPcdjDao.setPcryxxIsUpload(ryxx.getId(),
                            getContentResolver());
                }
            } else if (pcxx.getPcZl() == Zapcxx.PCWPXXZL) {
                ZapcWppcxxBean wpxx = ZaPcdjDao.queryWpxxById(pcxx.getId(),
                        getContentResolver(), self);
                re = dao.uploadZapcWpxx(wpxx, gzxx.getId(), gzxx.getKssj());
                if (checkWebResult(re)) {
                    ZaPcdjDao.setWpxxIsUpload(wpxx.getId(),
                            getContentResolver());
                }
            }
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putSerializable("re", re);
            msg.setData(b);
            mHandler.sendMessage(msg);
            progressDialog.dismiss();

        }
    }

    Handler uploadHandler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            WebQueryResult<ZapcReturn> re = (WebQueryResult<ZapcReturn>) b
                    .getSerializable("re");
            if (re == null) {
                GlobalMethod.showErrorDialog("未知错误", self);
            }
            if (checkWebResult(re)) {
                // 重新加载界面
                refershView();
                GlobalMethod.showDialog("系统信息", re.getResult().getScms(), "确定",
                        self);
            } else {
                if (re.getStatus() != HttpStatus.SC_OK)
                    GlobalMethod.showErrorDialog("未知错误", self);
                else {
                    GlobalMethod
                            .showErrorDialog(re.getResult().getScms(), self);
                }
            }
        }

    };
}
