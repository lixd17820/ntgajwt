package com.ntga.bean;

import android.graphics.drawable.Drawable;

public class IconifiedText implements Comparable<IconifiedText> {
	/* 文件名 */
	private String mText = "";
	/* 文件的图标ICNO */
	private Drawable mIcon = null;
	/* 能否选中 */
	private boolean mSelectable = true;

	private boolean isDirectory;

	public String getmText() {
		return mText;
	}

	public void setmText(String mText) {
		this.mText = mText;
	}

	public Drawable getmIcon() {
		return mIcon;
	}

	public void setmIcon(Drawable mIcon) {
		this.mIcon = mIcon;
	}

	public boolean ismSelectable() {
		return mSelectable;
	}

	public void setmSelectable(boolean mSelectable) {
		this.mSelectable = mSelectable;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public IconifiedText(String text, Drawable bullet, boolean isDirectory) {
		mIcon = bullet;
		mText = text;
		this.isDirectory = isDirectory;
	}

	// 是否可以选中
	public boolean isSelectable() {
		return mSelectable;
	}

	// 设置是否可用选中
	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}

	// 得到文件名
	public String getText() {
		return mText;
	}

	// 设置文件名
	public void setText(String text) {
		mText = text;
	}

	// 设置图标
	public void setIcon(Drawable icon) {
		mIcon = icon;
	}

	// 得到图标
	public Drawable getIcon() {
		return mIcon;
	}

	// 比较文件名是否相同，目录大于非目录，均为目录或文件比较名字
	public int compareTo(IconifiedText other) {
		if(isDirectory){
			//当前对象是目录
			if(!other.isDirectory)
				return 1;
			else
				return this.mText.compareTo(other.getText());
		}else{
			//当前对象是文件
			if(!other.isDirectory)
				return this.mText.compareTo(other.getText());
			else
				return -1;
		}
	}
}
