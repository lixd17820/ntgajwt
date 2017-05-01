package com.android.provider.wfdmcode;

import android.net.Uri;

public class Wfdmcode {

	public static final String DATABASE_NAME = "wfdmcode.db";
	public static final int VERSION = 1;

	public static final String AUTHORITY = "com.android.provider.wfdmcode";

	public static final int ITEM = 1;
	public static final int ITEM_ID = 2;
	public static final int QZYJ = 3;
	public static final int FORCE = 4;

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.android.wfdmcode";

	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.android.wfdmcode";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/item");
	

	public class VioCodeWfdm {
		/**
		 * 违法代码表的名称
		 */
		public static final String TABLE_NAME = "vio_codewfdm";

		public static final String WFXW = "WFXW";
		public static final String DMZL = "DMZL";
		public static final String DMFL = "DMFL";
		public static final String WFMS = "WFMS";
		public static final String WFNR = "WFNR";
		public static final String WFGD = "WFGD";
		public static final String FLTW = "FLTW";
		public static final String WFJFS = "WFJFS";
		public static final String FKJE_MIN = "FKJE_MIN";
		public static final String FKJE_MAX = "FKJE_MAX";
		public static final String FKJE_DUT = "FKJE_DUT";
		public static final String ZKYS_MIN = "ZKYS_MIN";
		public static final String ZKYS_MAX = "ZKYS_MAX";
		public static final String ZKYS_DUT = "ZKYS_DUT";
		public static final String JLSJ_MIN = "JLSJ_MIN";
		public static final String JLSJ_MAX = "JLSJ_MAX";
		public static final String JLSJ_DUT = "JLSJ_DUT";
		public static final String QZCSLX = "QZCSLX";
		public static final String JGBJ = "JGBJ";
		public static final String FKBJ = "FKBJ";
		public static final String ZKBJ = "ZKBJ";
		public static final String DXBJ = "DXBJ";
		public static final String JLBJ = "JLBJ";
		public static final String CXVBJ = "CXVBJ";
		public static final String CXDBJ = "CXDBJ";
		public static final String GB = "GB";
		public static final String YXQS = "YXQS";
		public static final String YXQZ = "YXQZ";
		public static final String GLBM = "GLBM";
		public static final String JYW = "JYW";
		public static final String CSBJ = "CSBJ";

	}

	public class ForceCode {
		public static final String TABLE_NAME = "forcecode";
		public static final String WFDM = "wfdm";
		public static final String WFXW = "wfxw";
		public static final String QZYJ = "qzyj";
		public static final String ZT = "zt";
		public static final String GXSJ = "gxsj";
		public static final String BZ = "bz";
	}
}
