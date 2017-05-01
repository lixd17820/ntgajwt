package com.ntga.jwt;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ntga.activity.ActionBarListActivity;
import com.ntga.bean.QueryResultBean;
import com.ntga.bean.VioViolation;
import com.ntga.bean.WebQueryResult;
import com.ntga.bean.WfdmBean;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.dao.ViolationDAO;
import com.ntga.zapc.ZapcReturn;
import com.ntga.zhcx.ZhcxOneRecordListAdapter;
import com.ydjw.web.RestfulDao;
import com.ydjw.web.RestfulDaoFactory;

import java.util.ArrayList;
import java.util.List;

public class PrintJdsDetailActivity extends ActionBarListActivity {

    private Context self;
    private VioViolation punish;
    private List<QueryResultBean> jdsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_no_button_list);
        self = this;
        String jdsbh = getIntent().getStringExtra("jdsbh");
        punish = ViolationDAO.getViolationByJdsbh(jdsbh, getContentResolver());
        if (punish == null) {
            finish();
            return;
        }
        createList();
        ZhcxOneRecordListAdapter adapter = new ZhcxOneRecordListAdapter(this,
                jdsDetail);
        getListView().setAdapter(adapter);

    }

    private void createList() {
        if (jdsDetail == null)
            jdsDetail = new ArrayList<QueryResultBean>();
        jdsDetail.clear();
        // 决定书种类
        if ("1".equals(punish.getWslb())) {
            if ("2".equals(punish.getCfzl()))
                jdsDetail.add(new QueryResultBean("", "决定书类别", "简易处罚决定书"));
            else if ("1".equals(punish.getCfzl()))
                jdsDetail.add(new QueryResultBean("", "决定书类别", "轻微警告决定书"));
        } else if ("3".equals(punish.getWslb()))
            jdsDetail.add(new QueryResultBean("", "决定书类别", "强制措施凭证"));
        else if ("3".equals(punish.getWslb()))
            jdsDetail.add(new QueryResultBean("", "决定书类别", "违法通知书"));
        jdsDetail.add(new QueryResultBean("", "处罚种类", GlobalMethod
                .ifNull(punish.getCfzl())));
        // 决定书编号
        jdsDetail.add(new QueryResultBean("", "决定书编号", punish.getJdsbh()));
        jdsDetail.add(new QueryResultBean("", "上传标记", (TextUtils.equals("1",
                punish.getScbj()) ? "已上传" : "未上传")));
        jdsDetail.add(new QueryResultBean("", "入库情况", GlobalMethod
                .ifNull(punish.getCwxx())));
        // 文书类别
        jdsDetail.add(new QueryResultBean("", "文书类别", punish.getWslb()));
        jdsDetail.add(new QueryResultBean("", "人员分类", (TextUtils.isEmpty(punish
                .getRyfl()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.ryflList, punish.getRyfl())
                + " 代码："
                + punish.getRyfl())));

        jdsDetail.add(new QueryResultBean("", "驾驶证号", GlobalMethod
                .ifNull(punish.getJszh())));
        jdsDetail.add(new QueryResultBean("", "档案编号", GlobalMethod
                .ifNull(punish.getDabh())));
        jdsDetail.add(new QueryResultBean("", "发证机关", GlobalMethod
                .ifNull(punish.getFzjg())));
        jdsDetail.add(new QueryResultBean("", "准驾车型", GlobalMethod
                .ifNull(punish.getZjcx())));
        jdsDetail.add(new QueryResultBean("", "驾驶证号", GlobalMethod
                .ifNull(punish.getJszh())));
        jdsDetail.add(new QueryResultBean("", "当事人", GlobalMethod.ifNull(punish
                .getDsr())));
        jdsDetail.add(new QueryResultBean("", "电话", GlobalMethod.ifNull(punish
                .getDh())));
        jdsDetail.add(new QueryResultBean("", "联系方式", GlobalMethod
                .ifNull(punish.getLxfs())));
        //
        String gzxm = punish.getGzxm();
        String[] temp = ViolationDAO.getJsonStrs(gzxm, "zzmm", "zyxx", "syxz");
        String zzmm = GlobalMethod.getStringFromKVListByKey(GlobalData.zzmmList, temp[0]);
        String zyxx = GlobalMethod.getStringFromKVListByKey(GlobalData.zyxxList, temp[1]);
        String syxz = GlobalMethod.getStringFromKVListByKey(GlobalData.syxzList, temp[2]);
        jdsDetail.add(new QueryResultBean("", "政治面貌", (TextUtils.isEmpty(zzmm) ? "" : zzmm
                + " 代码："
                + temp[0])));
        jdsDetail.add(new QueryResultBean("", "职业", (TextUtils.isEmpty(zyxx) ? "" : zyxx
                + " 代码："
                + temp[1])));
        jdsDetail.add(new QueryResultBean("", "车辆分类", (TextUtils.isEmpty(punish
                .getClfl()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.clflList, punish.getClfl())
                + " 代码："
                + punish.getClfl())));
        jdsDetail.add(new QueryResultBean("", "号牌种类", (TextUtils.isEmpty(punish
                .getHpzl()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.hpzlList, punish.getHpzl())
                + " 代码："
                + punish.getHpzl())));
        jdsDetail.add(new QueryResultBean("", "号牌号码", GlobalMethod
                .ifNull(punish.getHphm())));
        jdsDetail.add(new QueryResultBean("", "车辆类型", (TextUtils.isEmpty(punish
                .getJtfs()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.jtfsList, punish.getJtfs())
                + " 代码："
                + punish.getJtfs())));
        jdsDetail.add(new QueryResultBean("", "使用性质", (TextUtils.isEmpty(syxz) ? "" : syxz
                + " 代码："
                + temp[2])));
        jdsDetail.add(new QueryResultBean("", "违章时间", GlobalMethod
                .ifNull(punish.getWfsj())));
        jdsDetail.add(new QueryResultBean("", "违章地点", GlobalMethod
                .ifNull(punish.getWfdd())));
        jdsDetail.add(new QueryResultBean("", "违章地址", GlobalMethod
                .ifNull(punish.getWfdz())));
        jdsDetail.add(new QueryResultBean("", "违法行为", GlobalMethod
                .ifNull(punish.getWfxw1())));
        WfdmBean wfxw = ViolationDAO.queryWfxwByWfdm(punish.getWfxw1(),
                getContentResolver());
        jdsDetail.add(new QueryResultBean("", "违法行为内容", (wfxw == null ? "错误"
                : wfxw.getWfnr())));
        if (!TextUtils.isEmpty(punish.getWfxw2())) {
            jdsDetail.add(new QueryResultBean("", "违法行为2", GlobalMethod
                    .ifNull(punish.getWfxw2())));
            WfdmBean wfxw2 = ViolationDAO.queryWfxwByWfdm(punish.getWfxw2(),
                    getContentResolver());
            jdsDetail.add(new QueryResultBean("", "违法行为内容",
                    (wfxw2 == null ? "错误" : wfxw2.getWfnr())));
        }
        if (!TextUtils.isEmpty(punish.getWfxw3())) {
            jdsDetail.add(new QueryResultBean("", "违法行为3", GlobalMethod
                    .ifNull(punish.getWfxw3())));
            WfdmBean wfxw3 = ViolationDAO.queryWfxwByWfdm(punish.getWfxw3(),
                    getContentResolver());
            jdsDetail.add(new QueryResultBean("", "违法行为内容",
                    (wfxw3 == null ? "错误" : wfxw3.getWfnr())));
        }
        if (!TextUtils.isEmpty(punish.getWfxw4())) {
            jdsDetail.add(new QueryResultBean("", "违法行为4", GlobalMethod
                    .ifNull(punish.getWfxw4())));
            WfdmBean wfxw4 = ViolationDAO.queryWfxwByWfdm(punish.getWfxw4(),
                    getContentResolver());
            jdsDetail.add(new QueryResultBean("", "违法行为内容",
                    (wfxw4 == null ? "错误" : wfxw4.getWfnr())));
        }
        if (!TextUtils.isEmpty(punish.getWfxw5())) {
            jdsDetail.add(new QueryResultBean("", "违法行为5", GlobalMethod
                    .ifNull(punish.getWfxw5())));
            WfdmBean wfxw5 = ViolationDAO.queryWfxwByWfdm(punish.getWfxw5(),
                    getContentResolver());
            jdsDetail.add(new QueryResultBean("", "违法行为内容",
                    (wfxw5 == null ? "错误" : wfxw5.getWfnr())));
        }
        jdsDetail.add(new QueryResultBean("", "标准值", GlobalMethod
                .ifNull(punish.getBzz())));
        jdsDetail.add(new QueryResultBean("", "实测值", GlobalMethod
                .ifNull(punish.getScz())));
        jdsDetail.add(new QueryResultBean("", "累计记分", GlobalMethod
                .ifNull(punish.getWfjfs())));
        jdsDetail.add(new QueryResultBean("", "罚款金额", GlobalMethod
                .ifNull(punish.getFkje())));
        jdsDetail.add(new QueryResultBean("", "值勤民警", GlobalMethod
                .ifNull(punish.getZqmj())));
        jdsDetail.add(new QueryResultBean("", "缴款方式", (TextUtils.isEmpty(punish
                .getJkfs()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.jkfsList, punish.getJkfs())
                + " 代码："
                + punish.getJkfs())));
        jdsDetail.add(new QueryResultBean("", "发现机关", GlobalMethod
                .ifNull(punish.getFxjg())));
        jdsDetail.add(new QueryResultBean("", "缴款标记", (TextUtils.isEmpty(punish
                .getJkbj()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.jkbjList, punish.getJkbj())
                + " 代码："
                + punish.getJkbj())));
        jdsDetail.add(new QueryResultBean("", "缴款日期", GlobalMethod
                .ifNull(punish.getJkrq())));
        // jdsDetail.add("罚款金额：" + GlobalMethod.ifNull(punish.getJsjqbj()));
        jdsDetail.add(new QueryResultBean("", "强制措施类型", GlobalMethod
                .ifNull(punish.getQzcslx())));
        jdsDetail.add(new QueryResultBean("", "更新时间", GlobalMethod
                .ifNull(punish.getGxsj())));
        jdsDetail.add(new QueryResultBean("", "处理时间", GlobalMethod
                .ifNull(punish.getClsj())));
        jdsDetail.add(new QueryResultBean("", "收缴项目", GlobalMethod
                .ifNull(punish.getSjxm())));
        jdsDetail.add(new QueryResultBean("", "收缴项目名称", GlobalMethod
                .ifNull(punish.getSjxmmc())));
        jdsDetail.add(new QueryResultBean("", "扣留物品存放点", GlobalMethod
                .ifNull(punish.getKlwpcfd())));
        jdsDetail.add(new QueryResultBean("", "收缴物品存放点", GlobalMethod
                .ifNull(punish.getSjwpcfd())));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_vio_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_jds_check:
                if (!TextUtils.equals("1", punish.getScbj())) {
                    GlobalMethod.showErrorDialog("记录未上传，不能查询入库情况，请先上传记录", self);
                    return true;
                }
                QueryRyThread thread = new QueryRyThread(queryRyHandler,
                        punish.getJdsbh(), punish.getWslb());
                thread.doStart();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Handler queryRyHandler = new Handler() {
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            @SuppressWarnings("unchecked")
            WebQueryResult<ZapcReturn> rs = (WebQueryResult<ZapcReturn>) b
                    .getSerializable("queryResult");
            String err = GlobalMethod.getErrorMessageFromWeb(rs);
            if (TextUtils.isEmpty(err)) {
                ZapcReturn upRe = rs.getResult();
                if (upRe != null && !TextUtils.isEmpty(upRe.getScms())) {
                    String cwms = upRe.getScms();
                    punish.setCwxx(cwms);
                    ViolationDAO.uploadViolationRkxx(punish.getJdsbh(), cwms,
                            getContentResolver());
                    createList();
                    ((ZhcxOneRecordListAdapter) getListView().getAdapter())
                            .notifyDataSetChanged();
                } else {
                    GlobalMethod.showToast("查询失败", self);
                }
            } else {
                GlobalMethod.showErrorDialog(err, self);
            }
        }
    };

    class QueryRyThread extends Thread {
        private Handler handler;
        private String jdsbh;
        private String wslb;
        private ProgressDialog progressDialog;

        public QueryRyThread(Handler handler, String jdsbh, String wslb) {
            this.handler = handler;
            this.jdsbh = jdsbh;
            this.wslb = wslb;
        }

        public void doStart() {
            // 显示进度对话框
            progressDialog = ProgressDialog.show(self, "提示", "正在查询,请稍等...",
                    true);
            progressDialog.setCancelable(true);
            this.start();
        }

        @Override
        public void run() {
            RestfulDao dao = RestfulDaoFactory.getDao();
            WebQueryResult<ZapcReturn> re = dao.queryVioRkqk(jdsbh, wslb);
            Message msg = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putSerializable("queryResult", re);
            msg.setData(b);
            handler.sendMessage(msg);
            progressDialog.dismiss();
        }

    }
}
