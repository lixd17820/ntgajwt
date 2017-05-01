package com.ntga.jwt;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class JbywForbidPassActivity extends ActionBarActivity {

    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jbyw_forbid_pass);
        tvInfo = (TextView) findViewById(R.id.tv_pass_info);
        if (MainReferService.location != null) {
            tvInfo.setText(MainReferService.location.getLatitude() + ";" + MainReferService.location.getLongitude());
        }
    }
}
