package com.ntga.jwt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.ntga.bean.JdsPrintBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.VioViolation;
import com.ntga.bean.WfdmBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.PrintJdsTools;
import com.ntga.dao.VerifyData;
import com.ntga.dao.ViolationDAO;
import com.ntga.dao.WsglDAO;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class VioQzcsActivity extends ViolationActivity {

    private static final String TAG = "VioQzcsActivity";

    private Context self;

    private ContentResolver resolver;

    private EditText edAllWfxw;

    private Button btnAddWfxwList;

    private TreeMap<Integer, Boolean> qzcsMap = new TreeMap<Integer, Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolver = this.getContentResolver();
        self = this;
        edAllWfxw = (EditText) findViewById(R.id.Edit_wfxw_all);
        btnAddWfxwList = (Button) findViewById(R.id.But_add_wfxw_list);
        jdsbh = WsglDAO.getCurrentJdsbh(GlobalConstant.QZCSPZ, zqmj, resolver);
        if (jdsbh == null) {
            GlobalMethod.showDialogWithListener("提示信息",
                    "没有相应的处罚编号，请到文书管理中获取编号", "确定", exitSystem, self);
            return;
        }

        if (WsglDAO.hmNotEqDw(jdsbh)) {
            GlobalMethod.showDialogWithListener("提示信息",
                    "当前文书编号与处罚机关不符，请上交文书后重新获取", "确定", exitSystem, self);
            return;
        }

        // 设置标题
        setTitle(getActivityTitle());
        textJdsbh.setText("强制措施编号：" + jdsbh.getDqhm());
        initViolation();
        // violation.setCfzl("2");
        wslb = "3";
        violation.setWslb(wslb);
        violation.setFkje("0");
        RelativeLayout r2 = (RelativeLayout) findViewById(R.id.layout_jycx);
        r2.removeAllViewsInLayout();
        registerForContextMenu(edAllWfxw);
        btnAddWfxwList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addWfdmIntoEditWfxw();
            }
        });
    }

    @Override
    protected String getViolationTitle() {
        return "强制措施";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.force_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.qzcsxm): {
                showQzcsxmDialog();
            }
            return true;
            case (R.id.save_quite):
                return menuSaveViolation();
            case (R.id.print_preview):
                // 预览打印
                return menuPreviewViolation();

            case (R.id.pre_print):
                // 单据已保存，打印决定书
                return menuPrintViolation();
            case R.id.con_vio:
                if (violation != null && isViolationSaved)
                    showConVio(violation);
                else
                    GlobalMethod.showToast("请保存当前决定书", self);

                return true;
            case R.id.sys_config:
                Intent intent = new Intent(self, ConfigParamSetting.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    protected String showWfdmDetail(WfdmBean w) {
        String s = w.getWfxw() + ": " + w.getWfms();
        // 强制措施将显示收缴或扣留项目
        s += "| 强制措施  "
                + (TextUtils.isEmpty(w.getQzcslx()) ? "无" : w.getQzcslx());
        s += "| " + (ViolationDAO.isYxWfdm(w) ? "有效代码" : "无效代码");
        return s;
    }

    @Override
    protected String saveAndCheckVio() {
        getViolationFromView(violation);
        String err = VerifyData.verifyCommVio(violation, self);
        if (!TextUtils.isEmpty(err))
            return err;

        ArrayList<String> allWfxws = getWfdmsByEdit();
        if (allWfxws == null || allWfxws.isEmpty())
            return "至少要有一个违法行为";
        violation.setWfxw1(allWfxws.get(0));
        violation.setWfxw2(allWfxws.size() > 1 ? allWfxws.get(1) : "");
        violation.setWfxw3(allWfxws.size() > 2 ? allWfxws.get(2) : "");
        violation.setWfxw4(allWfxws.size() > 3 ? allWfxws.get(3) : "");
        violation.setWfxw5(allWfxws.size() > 4 ? allWfxws.get(4) : "");
        // 强制措施项目
        String qzcsError = "第%S个违法代码不可开具强制措施，请到系统配置->强制措施代码模块中查询！";
        if (!TextUtils.isEmpty(violation.getWfxw1())
                && TextUtils.isEmpty(ViolationDAO.queryQzcsYj(
                violation.getWfxw1(), getContentResolver())))
            return String.format(qzcsError, "1");
        if (!TextUtils.isEmpty(violation.getWfxw2())
                && TextUtils.isEmpty(ViolationDAO.queryQzcsYj(
                violation.getWfxw2(), getContentResolver())))
            return String.format(qzcsError, "2");
        if (!TextUtils.isEmpty(violation.getWfxw3())
                && TextUtils.isEmpty(ViolationDAO.queryQzcsYj(
                violation.getWfxw3(), getContentResolver())))
            return String.format(qzcsError, "3");
        if (!TextUtils.isEmpty(violation.getWfxw4())
                && TextUtils.isEmpty(ViolationDAO.queryQzcsYj(
                violation.getWfxw4(), getContentResolver())))
            return String.format(qzcsError, "4");
        if (!TextUtils.isEmpty(violation.getWfxw5())
                && TextUtils.isEmpty(ViolationDAO.queryQzcsYj(
                violation.getWfxw5(), getContentResolver()))) {
            return String.format(qzcsError, "5");
        }
        String qzxm = "";
        String sjxm = "";
        // 强制项目和收缴项目保存在一个表中
        // 强制项目小于10，收缴项目大于10
        if (qzcsMap != null && qzcsMap.size() > 0) {
            for (Entry<Integer, Boolean> entry : qzcsMap.entrySet()) {
                if (entry.getValue()) {
                    if (entry.getKey() < 10)
                        qzxm += entry.getKey();
                    else
                        // 收缴项目
                        sjxm += (entry.getKey() - 10);
                }
            }
        }
        if (TextUtils.isEmpty(qzxm)) {
            return "至少需要一个强制措施项目";
        }
        // 强制项目中包含了收缴
        if (qzxm.indexOf("5") > -1) {
            if (TextUtils.isEmpty(sjxm)) {
                return "强制措施包含收缴,却没有提供具体收缴项目";
            } else {
                violation.setSjxm(sjxm);
                if (!TextUtils.isEmpty(sjxm)) {
                    String sjxmMs = "";
                    for (int i = 0; i < sjxm.length(); i++) {
                        sjxmMs += GlobalMethod.getStringFromKVListByKey(
                                GlobalData.sjxmList,
                                "1" + sjxm.substring(i, i + 1))
                                + ",";
                    }
                    if (!TextUtils.isEmpty(sjxmMs))
                        violation.setSjxmmc(sjxmMs.substring(0,
                                sjxmMs.length() - 1));
                }
                violation.setSjwpcfd(GlobalData.grxx.get(GlobalConstant.BMMC));
            }
        }
        violation.setQzcslx(qzxm);
        err = VerifyData.verifyQzcsVio(violation, self);
        return err;
    }

    /**
     * 显示强制措施项目对话框，用于用户选择强制措施项目
     */
    private void showQzcsxmDialog() {
        if (qzcsMap.isEmpty()) {
            GlobalMethod.showDialog("系统提示", "所有违法行为均无强制措施项目，请检查违法代码", "确定",
                    self);
            return;
        }

        final String[] ws = new String[qzcsMap.size()];
        final boolean[] se = new boolean[qzcsMap.size()];
        final ArrayList<Integer> al = new ArrayList<Integer>(qzcsMap.keySet());
        // 初始化对话框的显示项
        Set<Entry<Integer, Boolean>> set = qzcsMap.entrySet();
        int i = 0;
        for (Entry<Integer, Boolean> entry : set) {
            // 判断是否为收缴项目
            if (entry.getKey() > 9)
                ws[i] = GlobalMethod.getStringFromKVListByKey(
                        GlobalData.sjxmList, String.valueOf(entry.getKey()));
            else
                ws[i] = GlobalMethod.getStringFromKVListByKey(
                        GlobalData.qzcslxList, String.valueOf(entry.getKey()));
            se[i] = entry.getValue();
            i++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择强制措施类型");
        builder.setMultiChoiceItems(ws, se,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        se[which] = isChecked;
                    }
                }
        )
                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int w) {
                        for (int j = 0; j < se.length; j++) {
                            qzcsMap.put(al.get(j), se[j]);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    private ArrayList<String> getWfdmsByEdit() {
        ArrayList<String> list = new ArrayList<String>();
        String allWfxwTxt = edAllWfxw.getText().toString();
        if (!TextUtils.isEmpty(allWfxwTxt)) {
            String[] allWfxws = allWfxwTxt.split(",");
            if (allWfxws != null && allWfxws.length > 0) {
                for (String w : allWfxws) {
                    list.add(w);
                }
            }
        }
        return list;
    }

    /**
     * 将上一个框内的违法代码加入到下一个列表编辑框内<br>
     * 同时更新违法代码列表以及强制措施列表
     */
    private void addWfdmIntoEditWfxw() {
        ArrayList<String> allWfxws = getWfdmsByEdit();
        if (allWfxws.size() > 4) {
            GlobalMethod.showErrorDialog("最多只能加入五条交通违法行为，请删除一条或多条", self);
            return;
        }
        String newWfxw = edWfxw.getText().toString();
        // 加入违法行为列表
        if (TextUtils.isEmpty(newWfxw)) {
            Toast.makeText(this, "违法行为为空", Toast.LENGTH_LONG).show();
            return;
        }
        WfdmBean wf = ViolationDAO.queryWfxwByWfdm(newWfxw, resolver);

        // 查询无此号码或不在有效期内
        if (wf == null || !ViolationDAO.isYxWfdm(wf)) {
            GlobalMethod.showErrorDialog("不是有效代码,不可以处罚!", self);
            return;
        }

        // 判断是否先前是否已加入相同号码,如果已有，则不执行加入操作
        for (int i = 0; i < allWfxws.size(); i++) {
            if (TextUtils.equals(wf.getWfxw(), allWfxws.get(i)))
                return;
        }
        allWfxws.add(newWfxw);
        // 同步数组与编辑框的内容，同时消除所有强制措施项目和收缴项目，简化程序的逻辑
        synQzcslxFromList(allWfxws);
        edAllWfxw.setText(synEditFromList(allWfxws));
        textWfxwms.setText("");
        edWfxw.setText("");

    }

    /**
     * 根据违法代码查找强制措施项目以及收缴项目
     *
     * @param wf
     * @return
     */
    private List<Integer> findQzcdxmAndSjxmByWfdm(WfdmBean wf) {
        List<Integer> cs = new ArrayList<Integer>();
        String qzlx = wf.getQzcslx();
        if (TextUtils.isEmpty(qzlx))
            return cs;
        // 有强制措施内容
        for (int j = 0; j < qzlx.length(); j++) {
            int key = Integer.valueOf(qzlx.substring(j, j + 1));
            cs.add(key);
            // 判断是否为收缴项目
            if (key == 5) {
                for (KeyValueBean kv : GlobalData.sjxmList)
                    cs.add(Integer.valueOf(kv.getKey()));
            }
        }
        return cs;
    }

    /**
     * 私有方法，同步违法列表与编辑框中的内容
     *
     * @param wfdms
     * @return
     */
    private String synEditFromList(ArrayList<String> wfdms) {
        StringBuilder s = new StringBuilder();
        for (String w : wfdms) {
            s.append(w).append(",");
        }
        if (s.length() > 0)
            s.delete(s.length() - 1, s.length());
        return s.toString();
    }

    /**
     * 同步违法代码以及强制措施类型<br>
     * 此方法会将所有类型选择设为空，所以只能在删除代码后使用
     *
     * @param wfs 违法代码列表
     */
    private void synQzcslxFromList(ArrayList<String> wfs) {
        qzcsMap.clear();
        if (wfs != null && wfs.size() > 0) {
            for (String wfdm : wfs) {
                WfdmBean wfxw = ViolationDAO.queryWfxwByWfdm(wfdm, resolver);
                if (wfxw == null)
                    continue;
                List<Integer> list = findQzcdxmAndSjxmByWfdm(wfxw);
                if (list == null || list.isEmpty())
                    continue;
                for (Integer key : list) {
                    qzcsMap.put(key, false);
                }
            }
        }
    }

    /**
     * 显示一个多选对话框，用于删除违法代码列表框中的项目
     */
    private void showModifyWfxws() {
        final ArrayList<String> allWfxws = getWfdmsByEdit();
        if (allWfxws.size() < 1) {
            return;
        }
        final String[] ws = new String[allWfxws.size()];
        final boolean[] se = new boolean[allWfxws.size()];
        // 初始化对话框的显示项
        for (int i = 0; i < allWfxws.size(); i++) {
            se[i] = true;
            ws[i] = showWfdmDetail(ViolationDAO.queryWfxwByWfdm(
                    allWfxws.get(i), resolver));

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择保留一个或多个违法行为");
        builder.setMultiChoiceItems(ws, se,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        se[which] = isChecked;
                    }
                }
        )
                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int w) {
                        ArrayList<String> newWfxws = new ArrayList<String>();
                        for (int k = 0; k < allWfxws.size(); k++) {
                            if (se[k])
                                newWfxws.add(allWfxws.get(k));
                        }
                        edAllWfxw.setText(synEditFromList(newWfxws));
                        // 如果是强制措施,删除违法代码后需要同步强制措施类型项目
                        synQzcslxFromList(newWfxws);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (MENU_WFXW_ADD): {
            }
            return true;
            case MENU_WFXW_MOD:
                showModifyWfxws();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == edAllWfxw) {
            Log.e("Violation", "edAllWfxws");
            menu.add(Menu.NONE, MENU_WFXW_MOD, Menu.NONE, "修改违法列表");
        }
    }

}
