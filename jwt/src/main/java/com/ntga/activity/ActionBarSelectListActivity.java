package com.ntga.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ntga.adaper.CommTwoColTwoSelectListAdapter;
import com.ntga.bean.TwoColTwoSelectBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixd on 14-4-3.
 */
public class ActionBarSelectListActivity extends ActionBarActivity {

    private static final String TAG = "CommTwoRowSelectAcbarListActivity";
    protected List<TwoColTwoSelectBean> beanList = null;
    protected CommTwoColTwoSelectListAdapter commAdapter;
    protected int selectedIndex;
    //protected ListView mListView;
    private TextView mEmptyTextView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("Select_index", selectedIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("Select_index"))
            selectedIndex = savedInstanceState.getInt("Select_index");
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onRestart");

        if (getListView() != null) {
            getListView()
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {

                            if (position < 0)
                                return;
                            boolean isAddSelect = beanList.get(position)
                                    .isSelectDown();
                            if (selectedIndex > -1)
                                beanList.get(selectedIndex)
                                        .setSelectDown(false);
                            beanList.get(position).setSelectDown(!isAddSelect);
                            selectedIndex = isAddSelect ? -1 : position;
                            commAdapter.notifyDataSetChanged();
                        }
                    });
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEmptyTextView = (TextView) findViewById(android.R.id.empty);
        if (mEmptyTextView == null)
            return;
        if (beanList == null || beanList.isEmpty()) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.INVISIBLE);
        }
    }

    public CommTwoColTwoSelectListAdapter getCommAdapter() {
        return commAdapter;
    }

    public void initView() {
        selectedIndex = -1;
        if (beanList == null)
            beanList = new ArrayList<TwoColTwoSelectBean>();
        commAdapter = new CommTwoColTwoSelectListAdapter(this, beanList);
        getListView().setAdapter(commAdapter);
    }

    protected ListView getListView() {
        return (ListView) findViewById(android.R.id.list);
    }

}
