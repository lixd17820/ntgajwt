package com.android.provider.roadcode;

import android.net.Uri;

public class Roadcode {
	public static final String DATABASE_NAME = "roadcode.db";

	public static final String AUTHORITY = "com.android.provider.roadcode";

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.android.roadcode";

	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.android.roadcode";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/item/");

	public static final int ROAD_ITEM = 0;

	public static final int ROADSEG_ITEM = 1;

	public static final int XZQH = 2;

	public static final int QUERYLXXX = 3;

	public static final int QUERY_CROSS = 4;

	public static final int UPDATE_CROSS = 5;

	public static final int DELETE_CROSS = 6;

	public static final int INSERT_CROSS = 7;

	public class RoadItem {
		public static final String TABLE_NAME = "FRM_ROADITEM";
		public static final String GLBM = "glbm";
		public static final String DLDM = "dldm";
		public static final String XZQH = "xzqh";
		public static final String DLMC = "dlmc";
		public static final String DLLX = "dllx";
		public static final String GLXZDJ = "glxzdj";
		public static final String DX = "dx";
		public static final String DLXX = "dlxx";
		public static final String LKLDLX = "lkldlx";
		public static final String DLWLGL = "dlwlgl";
		public static final String LMJG = "lmjg";
		public static final String FHSSLX = "fhsslx";
		public static final String QS = "qs";
		public static final String JS = "js";
		public static final String XQLC = "xqlc";
		public static final String CJR = "cjr";
		public static final String CJSJ = "cjsj";
		public static final String GXSJ = "gxsj";
		public static final String JLZT = "jlzt";
		public static final String QSMC = "qsmc";
		public static final String JSMC = "jsmc";
		public static final String XZQHXXLC = "xzqhxxlc";
		public static final String ZYGLSS = "zyglss";
	}

	public class RoadsegItem {
		public static final String TABLE_NAME = "FRM_ROADSEGITEM";
		public static final String GLBM = "glbm";
		public static final String DLDM = "dldm";
		public static final String LDDM = "lddm";
		public static final String LDMC = "ldmc";
		public static final String DLXX = "dlxx";
		public static final String LKLDLX = "lkldlx";
		public static final String DLWLGL = "dlwlgl";
		public static final String LMJG = "lmjg";
		public static final String FHSSLX = "fhsslx";
		public static final String CJR = "cjr";
		public static final String CJSJ = "cjsj";
		public static final String GXSJ = "gxsj";
		public static final String QSMS = "qsms";
		public static final String JSMS = "jsms";
		public static final String XZQH = "xzqh";
		public static final String SSGLBM = "ssglbm";
	}

	public class Xzqh {
		public static final String TABLE_NAME = "FRM_SYSPARA_VALUE";
		public static final String GLBM = "glbm";
		public static final String XTLB = "xtlb";
		public static final String GJZ = "gjz";
		public static final String CSZ = "csz";
		public static final String CSBJ = "csbj";
	}

	public class ZapcLxxx {
		public static final String TABLE_NAME = "V_JWT_ZAPC_LXXX";
		public static final String XLDM = "XLDM";
		public static final String XLXL = "XLXL";
		public static final String TRFFBH = "TRFFBH";
	}

	public class JtbzCross {
		public static final String TABLE_NAME = "T_JTBZ_CROSS";
		public static final String ID = "ID";
		public static final String WE_ROAD = "WE_ROAD";
		public static final String NS_ROAD = "NS_ROAD";
		public static final String CROSS_NAME = "CROSS_NAME";
		public static final String CROSS_OTHER = "CROSS_OTHER";
		public static final String JD = "JD";
		public static final String WD = "WD";
		public static final String GLBM = "GLBM";
		public static final String ROAD_COUNT = "ROAD_COUNT";
		public static final String EAST_SIDE = "EAST_SIDE";
		public static final String WEST_SIDE = "WEST_SIDE";
		public static final String SOUTH_SIDE = "SOUTH_SIDE";
		public static final String NORTH_SIDE = "NORTH_SIDE";
		public static final String GXSJ = "GXSJ";
		public static final String CROSS_LX = "CROSS_LX";
		public static final String R_WIDTH = "R_WIDTH";
		public static final String R_LENGTH = "R_LENGTH";

	}
}
