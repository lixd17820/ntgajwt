package com.ntga.tools;

import java.io.File;

public class MimeType {

	public static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio/*";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video/*";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image/*";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else if (end.equals("pdf")) {
			type = "application/pdf";
		} else if (end.equals("txt")) {
			type = "text/plain";
		} else if (end.equals("doc")) {
			type = "application/msword";
		} else if (end.equals("xls")) {
			type = "application/vnd.ms-excel";
		} else if (end.equals("ppt")) {
			type = "application/vnd.ms-powerpoint";
		} else if (end.equals("chm")) {
			type = "application/x-chm";
		} else {
			type = "text/plain";
		}
		return type;
	}

}
