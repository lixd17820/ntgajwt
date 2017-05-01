package com.ntga.jwt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ntga.adaper.IconifiedTextListAdapter;
import com.ntga.bean.IconifiedText;
import com.ntga.dao.GlobalMethod;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FileManager extends ListActivity {
	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	private File currentDirectory = new File("/");
	private File myTmpFile = null;
	private int myTmpOpt = -1;
	private Context self;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		self = this;
		String cs = getIntent().getStringExtra("curSelect");
		if (TextUtils.isEmpty(cs))
			browseToRoot();
		else {
			currentDirectory = new File(cs);
			browseTo(currentDirectory);
		}
		this.setSelection(0);
	}

	// 浏览文件系统的根目录
	private void browseToRoot() {
		browseTo(new File("/"));
	}

	// 返回上一级目录
	private void upOneLevel() {
		if (this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}

	// 浏览指定的目录,如果是文件则进行打开操作
	private void browseTo(final File file) {
		this.setTitle(file.getAbsolutePath());
		if (file.isDirectory()) {
			this.currentDirectory = file;
			fill(file.listFiles());
		} else {
			fileOptMenu(file);
		}
	}

	/**
	 * 查看文件
	 * 
	 * @param aFile
	 */
	protected void openFile(File aFile) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		File file = new File(aFile.getAbsolutePath());
		// 取得文件名
		String fileName = file.getName();
		// 根据不同的文件类型来打开文件
		if (checkEndsWithInStringArray(fileName,
				getResources().getStringArray(R.array.fileEndingImage))) {
			intent.setDataAndType(Uri.fromFile(file), "image/*");
		} else {
			GlobalMethod.showErrorDialog("不是图片文件", this);
			return;
		}
		startActivity(intent);
	}

	// 这里可以理解为设置ListActivity的源
	private void fill(File[] files) {
		// 清空列表
		this.directoryEntries.clear();

		Drawable currentIcon = null;
		for (File currentFile : files) {
			// 判断是否为隐藏文件
			if (currentFile.isHidden())
				continue;
			// 判断是一个文件夹还是一个文件
			if (currentFile.isDirectory()) {
				currentIcon = getResources().getDrawable(R.drawable.folder);
			} else {
				// 取得文件名
				String fileName = currentFile.getName();
				// 根据文件名来判断文件类型，设置不同的图标
				if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingImage))) {
					currentIcon = getResources().getDrawable(R.drawable.files);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingWebText))) {
					currentIcon = getResources().getDrawable(R.drawable.files);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPackage))) {
					currentIcon = getResources().getDrawable(R.drawable.files);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingAudio))) {
					currentIcon = getResources().getDrawable(R.drawable.files);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingVideo))) {
					currentIcon = getResources().getDrawable(R.drawable.files);
				} else {
					currentIcon = getResources().getDrawable(R.drawable.files);
				}
			}
			// 确保只显示文件名、不显示路径如：/sdcard/111.txt就只是显示111.txt
			// int currentPathStringLenght = this.currentDirectory
			// .getAbsolutePath().length();
			this.directoryEntries.add(new IconifiedText(currentFile.getName(),
					currentIcon, !currentFile.isDirectory()));
		}
		Collections.sort(this.directoryEntries);
		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add(0, new IconifiedText(
					getString(R.string.up_one_level), getResources()
							.getDrawable(android.R.drawable.ic_menu_revert),
					false));
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this,
				directoryEntries);
		// 将表设置到ListAdapter中
		// itla.setListItems(this.directoryEntries);
		// 为ListActivity添加一个ListAdapter
		this.setListAdapter(itla);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// 取得选中的一项的文件名
		String selectedFileString = this.directoryEntries.get(position)
				.getText();

		if (selectedFileString.equals(getString(R.string.current_dir))) {
			// 如果选中的是刷新
			this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals(getString(R.string.up_one_level))) {
			// 返回上一级目录
			this.upOneLevel();
		} else {
			File clickedFile = null;
			clickedFile = new File(this.currentDirectory, this.directoryEntries
					.get(position).getText());
			if (clickedFile != null)
				this.browseTo(clickedFile);
		}
	}

	// 通过文件名判断是什么类型的文件
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	// 处理文件，包括打开，重命名等操作
	public void fileOptMenu(final File file) {
		OnClickListener listener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					if (checkEndsWithInStringArray(
							file.getName(),
							getResources().getStringArray(
									R.array.fileEndingImage))) {
						Intent i = new Intent();
						Bundle b = new Bundle();
						b.putString("pic_file", file.getAbsolutePath());
						i.putExtras(b);
						setResult(RESULT_OK, i);
						finish();
					} else {
						GlobalMethod.showErrorDialog("不是图片文件", self);
					}

				} else if (which == 1) {
					openFile(file);
				}
				// else if (which == 1) {
				// // 自定义一个带输入的对话框由TextView和EditText构成
				// final LayoutInflater factory = LayoutInflater
				// .from(FileManager.this);
				// final View dialogview = factory.inflate(R.layout.rename,
				// null);
				// // 设置TextView的提示信息
				// ((TextView) dialogview.findViewById(R.id.TextView01))
				// .setText("重命名");
				// // 设置EditText输入框初始值
				// ((EditText) dialogview.findViewById(R.id.EditText01))
				// .setText(file.getName());
				//
				// Builder builder = new Builder(FileManager.this);
				// builder.setTitle("重命名");
				// builder.setView(dialogview);
				// builder.setPositiveButton(android.R.string.ok,
				// new AlertDialog.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int which) {
				// // 点击确定之后
				// String value = GetCurDirectory()
				// + "/"
				// + ((EditText) dialogview
				// .findViewById(R.id.EditText01))
				// .getText().toString();
				// if (new File(value).exists()) {
				// Builder builder = new Builder(
				// FileManager.this);
				// builder.setTitle("重命名");
				// builder.setMessage("文件名重复，是否需要覆盖？");
				// builder.setPositiveButton(
				// android.R.string.ok,
				// new AlertDialog.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int which) {
				// String str2 = GetCurDirectory()
				// + "/"
				// + ((EditText) dialogview
				// .findViewById(R.id.EditText01))
				// .getText()
				// .toString();
				// file.renameTo(new File(
				// str2));
				// browseTo(new File(
				// GetCurDirectory()));
				// }
				// });
				// builder.setNegativeButton(
				// android.R.string.cancel,
				// new DialogInterface.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int which) {
				// dialog.cancel();
				// }
				// });
				// builder.setCancelable(false);
				// builder.create();
				// builder.show();
				// } else {
				// // 重命名
				// file.renameTo(new File(value));
				// browseTo(new File(GetCurDirectory()));
				// }
				// }
				// });
				// builder.setNegativeButton(android.R.string.cancel,
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int which) {
				// dialog.cancel();
				// }
				// });
				// builder.setOnCancelListener(new
				// DialogInterface.OnCancelListener() {
				// public void onCancel(DialogInterface dialog) {
				// dialog.cancel();
				// }
				// });
				// builder.show();
				// } else if (which == 2) {
				// Builder builder = new Builder(FileManager.this);
				// builder.setTitle("删除文件");
				// builder.setMessage("确定删除" + file.getName() + "？");
				// builder.setPositiveButton(android.R.string.ok,
				// new AlertDialog.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int which) {
				// if (deleteFile(file)) {
				// Builder builder = new Builder(
				// FileManager.this);
				// builder.setTitle("提示对话框");
				// builder.setMessage("删除成功");
				// builder.setPositiveButton(
				// android.R.string.ok,
				// new AlertDialog.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int which) {
				// // 点击确定按钮之后
				// dialog.cancel();
				// browseTo(new File(
				// GetCurDirectory()));
				// }
				// });
				// builder.setCancelable(false);
				// builder.create();
				// builder.show();
				// } else {
				// Builder builder = new Builder(
				// FileManager.this);
				// builder.setTitle("提示对话框");
				// builder.setMessage("删除失败");
				// builder.setPositiveButton(
				// android.R.string.ok,
				// new AlertDialog.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int which) {
				// // 点击确定按钮之后
				// dialog.cancel();
				// }
				// });
				// builder.setCancelable(false);
				// builder.create();
				// builder.show();
				// }
				// }
				// });
				// builder.setNegativeButton(android.R.string.cancel,
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int which) {
				// dialog.cancel();
				// }
				// });
				// builder.setCancelable(false);
				// builder.create();
				// builder.show();
				// } else if (which == 3)// 复制
				// {
				// // 保存我们复制的文件目录
				// myTmpFile = file;
				// // 这里我们用0表示复制操作
				// myTmpOpt = 0;
				// } else if (which == 4)// 剪切
				// {
				// // 保存我们复制的文件目录
				// myTmpFile = file;
				// // 这里我们用0表示剪切操作
				// myTmpOpt = 1;
				// }
			}
		};
		// 显示操作菜单
		String[] menu = { "加入图片", "查看图片" };
		new Builder(FileManager.this).setTitle("请选择你要进行的操作")
				.setItems(menu, listener).show();
	}

	// 得到当前目录的绝对路劲
	public String GetCurDirectory() {
		return this.currentDirectory.getAbsolutePath();
	}

}
