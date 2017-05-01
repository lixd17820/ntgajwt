package com.ntga.jwt;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.ntga.adaper.MyExpandableListAdapter;
import com.ntga.bean.ClgjBean;
import com.ntga.dao.GlobalConstant;
import com.ntga.thread.QueryDrvVehThread;

import java.util.ArrayList;
import java.util.List;

public class ZhcxClgjResultActivity extends ActionBarActivity {

    private static final int MENU_COPY_VALUE = 0;
    private ExpandableListAdapter mAdapter;
    private ArrayList<ClgjBean> clgjs;
    private Context self;
    private int selecedGroup = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhcx_query_result);
        self = this;
        clgjs = (ArrayList<ClgjBean>) getIntent().getSerializableExtra(QueryDrvVehThread.RESULT_CLGJ);
        if (clgjs != null && !clgjs.isEmpty()) {
            int size = clgjs.size();
            setTitle(" 共查询到" + size + "个结果");
            String[] gr = new String[size];
            String[][] childs = new String[size][];
            for (int i = 0; i < gr.length; i++) {
                ClgjBean clgj = clgjs.get(i);
                String[] temp = new String[5];
                temp[0] = "号牌种类：" + clgj.getHpzl();
                temp[1] = "号牌号码：" + clgj.getHphm();
                temp[2] = "通过时间：" + clgj.getDdsj();
                temp[3] = "通过地点：" + clgj.getCbz();
                temp[4] = "行驶方向：" + GlobalConstant.xsfxMap.get(clgj.getXsfx());
                gr[i] = temp[2] + "\n" + temp[3];
                childs[i] = temp;
            }
            mAdapter = new MyExpandableListAdapter(gr, childs, self);
            getExpandableListView().setAdapter(mAdapter);
        }
        registerForContextMenu(getExpandableListView());
        getExpandableListView().setOnGroupExpandListener(
                new OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int sg) {
                        if (selecedGroup > -1)
                            getExpandableListView().collapseGroup(selecedGroup);
                        selecedGroup = sg;
                    }
                }
        );

        getExpandableListView().setOnGroupCollapseListener(
                new OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int sg) {
                        selecedGroup = -1;
                    }
                }
        );
    }

    private ExpandableListView getExpandableListView() {
        return (ExpandableListView) findViewById(R.id.exp_list);
    }
}
