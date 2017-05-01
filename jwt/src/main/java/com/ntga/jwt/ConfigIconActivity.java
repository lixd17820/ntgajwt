package com.ntga.jwt;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.ntga.adaper.MainMenuAdapter;
import com.ntga.bean.MenuOptionBean;
import com.ntga.dao.GlobalMethod;
import com.ntga.tools.ZipUtils;
import com.ntga.thread.IconDownloadThread;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

public class ConfigIconActivity extends Activity {

	private static final int MENU_LOAD_DISK = 0;
	private static final int MENU_LOAD_NETWORK = 1;

	private Context self;
	private File iconFile;
	private File innDir;

	private List<MenuOptionBean> iconList;
	private MainMenuAdapter adapter;
	private GridView gv;
	private TextView tv;

	private IconHandler icH = new IconHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_icon);
		self = this;
		gv = (GridView) findViewById(R.id.gv_icon);
		tv = (TextView) findViewById(R.id.tv_icon_count);
		innDir = self.getFilesDir();
		if(!innDir.exists())
			innDir.mkdirs();
		iconFile = new File(innDir, "icon");
		if (!iconFile.exists())
			iconFile.mkdirs();
		referView();
		gv.setAdapter(adapter);
	}

	private void referView() {
		File[] files = iconFile.listFiles();
		tv.setText("系统共有图标" + files.length + "个");
		if (iconList == null)
			iconList = new ArrayList<MenuOptionBean>();
		iconList.clear();
		int i = 0;
		for (File file : files) {
			MenuOptionBean op = new MenuOptionBean();
			op.setId(i);
			op.setMenuName(file.getName());
			op.setImg(file.getName());
			iconList.add(op);
		}
		if (adapter == null)
			adapter = new MainMenuAdapter((Activity) self, iconList);
		else
			adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_LOAD_DISK, Menu.NONE, "从SD卡加载图标");
		menu.add(Menu.NONE, MENU_LOAD_NETWORK, Menu.NONE, "从服务器加载图标");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_LOAD_DISK:
			// 从SD卡上取文件
			File outSideDir = new File(
					Environment.getExternalStorageDirectory(), "jwtdb");
			if (!outSideDir.exists())
				outSideDir.mkdirs();
			File zipFile = new File(outSideDir, "icon_md5.zip");
			if (!zipFile.exists()) {
				GlobalMethod.showErrorDialog("图标文件不存在", self);
				return true;
			}
			ZipUtils.unzipFile(zipFile.getAbsolutePath(),
					innDir.getAbsolutePath());
			// File mdFile = new File(innDir,"md5.txt");
			// String md5 = GlobalMethod.readFileContent(mdFile);
			File megFile = new File(innDir, "icon.meg");
			if (!megFile.exists()) {
				GlobalMethod.showErrorDialog("解压合并文件不存在", self);
				return true;
			}
			ZipUtils.unMegFile(megFile.getAbsolutePath(),
					iconFile.getAbsolutePath());
			referView();
			// 文件获取
			break;
		case MENU_LOAD_NETWORK:
			new IconDownloadThread().doStart(icH, true, self);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	static class IconHandler extends Handler {
		private final WeakReference<ConfigIconActivity> myActivity;

		public IconHandler(ConfigIconActivity activity) {
			myActivity = new WeakReference<ConfigIconActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ConfigIconActivity ac = myActivity.get();
			if (ac != null)
				ac.operMessage(msg);
		}
	}

	private void operMessage(Message msg) {
		int err = msg.arg1;
		int what = msg.what;
		int step = msg.arg2;
		if (err != IconDownloadThread.ERR_NONE) {
			String errMs = "未知错误";
			if (msg.getData() != null)
				errMs = msg.getData().getString(IconDownloadThread.CWMS);
			GlobalMethod.showErrorDialog(errMs, self);
			return;
		}
		if (what == IconDownloadThread.WHAT_CREATE_ICON) {
			GlobalMethod.showDialog("系统提示", "共下载" + step + "个图标", "知道了", self);
			if (step > 0)
				referView();
		}
	}

}
