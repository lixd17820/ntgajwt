package com.acd.simple.provider;

import android.net.Uri;

public class AcdSimple {

	public static final String DATABASE_NAME = "acdSimple.db";

	public static final String AUTHORITY = "com.acd.simple.provider";

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.acd.simple";

	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.acd.simple";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/item/");

	public static final String ADD_ACD = "addAcd";

	public static final String DEL_ACD = "delAcd";

	public static final String ADD_HUMAN = "addHuman";

	public static final String DEL_HUMAN = "delHuman";

	public static final String QUERY_ACD = "queryAcd";

	public static final String QUERY_HUMAN = "queryHuman";

	public static final String UPDATE_ACD = "updateAcd";

	public static final String UPDATE_HUMAN = "updateHuman";

	public static final String RAW_QUERY = "raw_query";

	public static final String RAW_DEL = "raw_del";

	public static final String CREATE_DB = "create_db";

	public static final String QUERY_ACD_LAW = "query_acd_law";

	public static final String QUERY_REPAIR = "query_repair";

	public static final String DEL_REPAIR = "del_repair";

	public static final String UPDATE_REPAIR = "update_repair";

	public static final String ADD_REPAIR = "add_repair";

	public static final String ADD_ACD_PHOTO = "add_acd_photo";

	public static final String QUERY_ACD_PHOTO = "query_acd_photo";

	public static final String DEL_ACD_PHOTO = "del_acd_photo";

	public static final String ADD_ACD_FILE = "add_acd_file";

	public static final String DEL_ACD_FILE = "del_acd_file";

	public static final String QUERY_ACD_FILE = "query_acd_file";

	public static final String UPDATE_ACD_PHOTO = "update_acd_photo";

	public class AcdDutySimple {
		public static final String TABLE_NAME = "ACD_DUTYSIMPLE";
		public static final String SGBH = "sgbh";
		public static final String XZQH = "xzqh";
		public static final String XQ = "xq";
		public static final String SGFSSJ = "sgfssj";
		public static final String LH = "lh";
		public static final String LM = "lm";
		public static final String GLS = "gls";
		public static final String MS = "ms";
		public static final String JDWZ = "jdwz";
		public static final String SGDD = "sgdd";
		public static final String SSRS = "ssrs";
		public static final String ZJCCSS = "zjccss";
		public static final String LWSGLX = "lwsglx";
		public static final String RDYYFL = "rdyyfl";
		public static final String SGRDYY = "sgrdyy";
		public static final String TQ = "tq";
		public static final String XC = "xc";
		public static final String SWSG = "swsg";
		public static final String SGXT = "sgxt";
		public static final String CLJSG = "cljsg";
		public static final String DCSG = "dcsg";
		public static final String PZFS = "pzfs";
		public static final String LBQK = "lbqk";
		public static final String TJR1 = "tjr1";
		public static final String CCLRSJ = "cclrsj";
		public static final String JLLX = "jllx";
		public static final String SCSJD = "scsjd";
		public static final String SSZD = "sszd";
		public static final String DAH = "dah";
		public static final String SB = "sb";
		public static final String TJSGBH = "tjsgbh";
		public static final String GLBM = "glbm";
		public static final String DZZB = "dzzb";
		public static final String BADW = "badw";
		public static final String WSBH = "wsbh";
		public static final String SGSS = "sgss";
		public static final String ZRTJJG = "zrtjjg";
		public static final String JAR1 = "jar1";
		public static final String JAR2 = "jar2";
		public static final String JBR = "jbr";
		public static final String GXSJ = "gxsj";
		public static final String JYW = "jyw";
		public static final String JAFS = "jafs";
		public static final String DLLX = "dllx";
		public static final String GLXZDJ = "glxzdj";
		public static final String TJFS = "tjfs";
		public static final String SCBJ = "scbj";

	}

	public class AcdDutySimpleHuman {
		public static final String TABLE_NAME = "ACD_DUTYSIMPLEHUMAN";
		public static final String HUMAN_ID = "human_id";
		public static final String SGBH = "sgbh";
		public static final String XZQH = "xzqh";
		public static final String RYBH = "rybh";
		public static final String XM = "xm";
		public static final String XB = "xb";
		public static final String SFZMHM = "sfzmhm";
		public static final String NL = "nl";
		public static final String ZZ = "zz";
		public static final String DH = "dh";
		public static final String RYLX = "rylx";
		public static final String SHCD = "shcd";
		public static final String WFXW1 = "wfxw1";
		public static final String WFXW2 = "wfxw2";
		public static final String WFXW3 = "wfxw3";
		public static final String TK1 = "tk1";
		public static final String TK2 = "tk2";
		public static final String TK3 = "tk3";
		public static final String ZYYSDW = "zyysdw";
		public static final String JTFS = "jtfs";
		public static final String GLXZQH = "glxzqh";
		public static final String DABH = "dabh";
		public static final String JL = "jl";
		public static final String JSZZL = "jszzl";
		public static final String ZJCX = "zjcx";
		public static final String CCLZRQ = "cclzrq";
		public static final String JSRGXD = "jsrgxd";
		public static final String FZJG = "fzjg";
		public static final String SGZR = "sgzr";
		public static final String HPHM = "hphm";
		public static final String HPZL = "hpzl";
		public static final String CLFZJG = "clfzjg";
		public static final String FDJH = "fdjh";
		public static final String CLSBDH = "clsbdh";
		public static final String JDCXH = "jdcxh";
		public static final String CLPP = "clpp";
		public static final String CLXH = "clxh";
		public static final String CSYS = "csys";
		public static final String CLLX = "cllx";
		public static final String JDCZT = "jdczt";
		public static final String SYQ = "syq";
		public static final String JDCSYR = "jdcsyr";
		public static final String CLSYXZ = "clsyxz";
		public static final String BX = "bx";
		public static final String BXGS = "bxgs";
		public static final String BXPZH = "bxpzh";
		public static final String CLZZWP = "clzzwp";
		public static final String CLGXD = "clgxd";
		public static final String CJCXBJ = "cjcxbj";
		public static final String JYW = "jyw";
		public static final String SCBJ = "scbj";

	}

	public class AcdLaws {
		public static final String TABLE_NAME = "ACD_LAWS";
		public static final String XH = "xh";
		public static final String FLMC = "flmc";
		public static final String TKMC = "tkmc";
		public static final String TKNR = "tknr";
	}

	public class Repair {
		public static final String TABLE_NAME = "repair";
		public static final String ID = "id";
		public static final String XTBH = "xtbh";
		public static final String ITEM = "item";
		public static final String XZQH = "xzqh";
		public static final String BXDD = "bxdd";
		public static final String SIDE = "side";
		public static final String BXSJ = "bxsj";
		public static final String BXNR = "bxnr";
		public static final String PIC = "pic";
		public static final String SCBJ = "scbj";

	}

	public class AcdPhotoRecode {
		public static final String TABLE_NAME = "ACD_PHOTO_RECODE";
		public static final String ID = "id";
		public static final String SGSJ = "sgsj";
		public static final String SGDDDM = "sgdddm";
		public static final String SGDD = "sgdd";
		public static final String SGBH = "sgbh";
		public static final String XTBH = "xtbh";
		public static final String SCBJ = "scbj";
	}

	public class AcdPhotoFile {
		public static final String TABLE_NAME = "acd_photo_file";
		public static final String ID = "id";
		public static final String REC_ID = "rec_id";
		public static final String PHOTO = "photo";
	}

}
