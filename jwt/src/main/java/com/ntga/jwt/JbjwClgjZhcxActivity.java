package com.ntga.jwt;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ntga.bean.ClgjBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.thread.QueryDrvVehThread;
import com.ntga.zhcx.ZhcxHandler;
import com.ntga.zhcx.ZhcxThread;
import com.ydjw.pojo.GlobalQueryResult;

import org.apache.http.HttpStatus;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class JbjwClgjZhcxActivity extends ActionBarActivity {

    private EditText editHphm;
    private Spinner spinSjkd, spinHpqz, spinHpzl;
    private Button btnQuery, btnCancel;
    private Context self;
    private static List<KeyValueBean> sjkdList = new ArrayList<KeyValueBean>();

    static {
        sjkdList.add(new KeyValueBean("1", "今天"));
        sjkdList.add(new KeyValueBean("2", "最近两天"));
        sjkdList.add(new KeyValueBean("3", "最近三天"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.zhcx_clgj);
        editHphm = (EditText) findViewById(R.id.edit_hphm);
        spinSjkd = (Spinner) findViewById(R.id.spin_sjkd);
        spinHpqz = (Spinner) findViewById(R.id.spin_hpqz);
        spinHpzl = (Spinner) findViewById(R.id.spin_hpzl);
        btnQuery = (Button) findViewById(R.id.btn_query);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        GlobalMethod.changeAdapter(spinSjkd, sjkdList,
                this);
        GlobalMethod.changeAdapter(spinHpqz, GlobalData.hpqlList, this, true);
        GlobalMethod.changeAdapter(spinHpzl, GlobalData.hpzlList, this, true);
        GlobalMethod.changeSpinnerSelect(spinHpqz, "苏", GlobalConstant.VALUE, true);
        GlobalMethod.changeSpinnerSelect(spinHpzl, "02", GlobalConstant.KEY, true);
        btnQuery.setOnClickListener(clQuery);
        btnCancel.setOnClickListener(clQuery);
    }

    private View.OnClickListener clQuery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnQuery) {
                String hphm = editHphm.getText().toString().toUpperCase();
                if (hphm.length() < 5) {
                    GlobalMethod.showErrorDialog("号牌号码不正确", self);
                    return;
                }
                hphm = GlobalMethod.getKeyFromSpinnerSelected(spinHpqz, GlobalConstant.VALUE) + hphm;
                String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spinHpzl, GlobalConstant.KEY);
                String sjkd = GlobalMethod.getKeyFromSpinnerSelected(spinSjkd, GlobalConstant.KEY);
                int kd = Integer.valueOf(sjkd);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String etime = sdf.format(new Date()) + " 23:59:59";
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, (1 - kd));
                String stime = sdf.format(c.getTime()) + " 00:00:00";
                QueryDrvVehThread thread = new QueryDrvVehThread(new ClgjHandler(JbjwClgjZhcxActivity.this), QueryDrvVehThread.QUERY_CLGJ,
                        new String[]{stime, etime, hpzl, hphm}, self
                );
                thread.doStart();
            } else if (v == btnCancel) {
                finish();
            }
        }
    };

    static class ClgjHandler extends Handler {

        private final WeakReference<JbjwClgjZhcxActivity> myActivity;

        public ClgjHandler(JbjwClgjZhcxActivity activity) {
            myActivity = new WeakReference<JbjwClgjZhcxActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            JbjwClgjZhcxActivity ac = myActivity.get();
            if (ac != null) {
                ac.operHandler(msg);
            }
        }
    }

    private void operHandler(Message msg) {
        Bundle b = msg.getData();
        WebQueryResult<List<ClgjBean>> webResult = (WebQueryResult<List<ClgjBean>>) b
                .getSerializable(QueryDrvVehThread.RESULT_CLGJ);
        if (webResult.getStatus() == HttpStatus.SC_OK) {
            if (webResult.getResult() == null
                    || webResult.getResult() == null
                    || webResult.getResult().size() == 0)
                GlobalMethod.showDialog("提示信息", "没有查询到对应的结果", "确定", self);
            else {
                ArrayList<ClgjBean> zhcx = (ArrayList<ClgjBean>) webResult.getResult();
                Intent intent = new Intent(self,
                        ZhcxClgjResultActivity.class);

                intent.putExtra(QueryDrvVehThread.RESULT_CLGJ, zhcx);
                self.startActivity(intent);

            }
        } else if (webResult.getStatus() == 204) {
            GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！", "确定", self);
        } else if (webResult.getStatus() == 500) {
            GlobalMethod.showDialog("提示信息", "该查询在服务器不能实现，请与管理员联系！", "确定",
                    self);
        } else {
            GlobalMethod.showDialog("提示信息", "网络连接失败，请检查配查或与管理员联系！", "确定",
                    self);
        }
    }

}
