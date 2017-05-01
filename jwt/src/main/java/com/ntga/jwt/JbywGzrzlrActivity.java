package com.ntga.jwt;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class JbywGzrzlrActivity extends Activity {

	private Spinner spQwzt;
	private EditText edGzrzKssj;
	private EditText edGzrzJssj;
	private EditText edGzrzDjsj;
	private Button butKssj;
	private Button butJssj;
	private Button butDjsj;

	private SimpleDateFormat sdfRq = new SimpleDateFormat("yyyy-MM-dd");

	Context self;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.jbyw_gzrzlr);
		spQwzt = (Spinner) findViewById(R.id.spin_qwzt);

		edGzrzKssj = (EditText) findViewById(R.id.edit_sttime);
		edGzrzJssj = (EditText) findViewById(R.id.edit_etime);
		edGzrzDjsj = (EditText) findViewById(R.id.edit_gzrz_djsj);
		butKssj = (Button) findViewById(R.id.but_gzrz_sttime);
		String d = sdfRq.format(new Date());
		edGzrzKssj.setText(d + " " + "08:00");
		edGzrzJssj.setText(d + " " + "17:30");
		edGzrzDjsj.setText(d);
		GlobalMethod.changeAdapter(spQwzt, GlobalData.qwztList, this);
		
	}

}
