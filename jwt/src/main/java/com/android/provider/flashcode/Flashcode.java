package com.android.provider.flashcode;

import android.net.Uri;

public class Flashcode {

	public static final String DATABASE_NAME = "flashcode.db";
	public static final int VERSION = 1;

	public static final String AUTHORITY = "com.android.provider.flashcode";

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.android.flashcode";

	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.android.flashcode";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/item/");

	// public static final Uri ROAD_URI = Uri.parse("content://" + AUTHORITY
	// + "/road");

	//public static final String USER_SEG = "userseg";
	//public static final String ADD_XZQH = "addxzqh";
	//public static final String ADD_ROAD = "addroad";
	//public static final String ADD_SEG = "addseg";
	public static final String ADD_WFDD = "addwfdd";
	public static final String DEL_WFDD = "delwfdd";
	//public static final String DEL_ALL = "delall";
	public static final String QUERY_FAVOR_WFDD = "queryfavorwfdd";
	public static final String DEL_DEPT = "deldept";
	public static final String DEL_VEH = "delveh";
	public static final String ADD_VEH = "addveh";
	public static final String ADD_DEPT = "adddept";
	public static final String ADD_VEHADD_VEH = "addveh";
	public static final String DEPARTMENT = "querydept";
	public static final String VEHICLE = "queryveh";
	public static final String QUERYVIOLATION = "queryviolation";
	public static final String UPDATEVIOLATION = "updateviolation";
	public static final String INSERTVIOLATION = "insertviolation";
	public static final String DELVIOLATION = "delviolation";
	public static final String RAWDELSQL = "rawDelSql";
	public static final String INSERT_GZXX = "insertGzxx";
	public static final String QUERY_GZXX_MAXID = "queryGzxxMaxid";
	public static final String QUERY_GZXX_INFO = "queryGzxxInfo";
	public static final String UPDATE_GZXX = "updateGzxx";
	public static final String QUERY_WPXX_MAXID = "queryWpxxMaxid";
	public static final String QUERY_WPXX_INFO = "queryWpxxInfo";
	public static final String UPDATE_WPXX = "updateWpxx";
	public static final String INSERT_WPXX = "insertWpxx";
	public static final String INSERT_PCRYXX = "insertPcryxx";
	public static final String QUERY_PCRYXX_MAXID = "queryPcryxxMaxid";
	public static final String QUERY_PCRYXX_INFO = "queryPcryxxInfo";
	public static final String UPDATE_PCRYXX = "updatePcryxx";
	public static final String INSERT_JBRYXX = "insertJbryxx";
	public static final String QUERY_JBRYXX_MAXID = "queryJbryxxMaxid";
	public static final String QUERY_JBRYXX_INFO = "queryJbryxxInfo";
	public static final String UPDATE_JBRYXX = "updateJbryxx";
	public static final String DELETE_GZXX = "deleteGzxx";
	public static final String RAWQUERY = "rawQuery";

//	public class UserRoads {
//		public static final String TABLE_NAME = "user_roads";
//		public static final String XZQH = "xzqh";
//		public static final String DLDM = "dldm";
//		public static final String DLMC = "dlmc";
//		public static final String PY = "py";
//	}
//
//	public class UserXzqh {
//		public static final String TABLE_NAME = "user_xzqh";
//
//		public static final String XZQH = "xzqh";
//		public static final String XZQHMC = "xzqhmc";
//		public static final String PY = "py";
//	}
//
//	public class UserRoadSeg {
//		public static final String TABLE_NAME = "user_roadseg";
//		public static final String XZQH = "xzqh";
//		public static final String DLDM = "dldm";
//		public static final String LDDM = "lddm";
//		public static final String LDMC = "ldmc";
//		public static final String PY = "py";
//	}

	public class FavorWfdd {
		public static final String TABLE_NAME = "favor_wfdd";
		public static final String ID = "id";
		public static final String XZQH = "xzqh";
		public static final String DLDM = "dldm";
		public static final String LDDM = "lddm";
		public static final String MS = "ms";
		public static final String SYSLDMC = "sys_ldmc";
		public static final String FAVORLDMC = "favor_ldmc";
		public static final String YXBJ = "yxbj";
		public static final String ZQMJ = "zqmj";
	}

	public class Department {
		public static final String TABLE_NAME = "t_department";
		public static final String BH = "bh";
		public static final String BMBH = "bmbh";
		public static final String BMMC = "bmmc";
	}

	public class Vehicle {
		public static final String TABLE_NAME = "t_pol_vehicle";
		public static final String BH = "bh";
		public static final String VEHID = "veh_id";
		public static final String LICENSE = "license";
		public static final String DWDM = "dwdm";
	}

	public class VioViolation {
		public static final String TABLE_NAME = "VIO_VIOLATION";
		public static final String ID = "id";
		public static final String JDSBH = "jdsbh";
		public static final String WSLB = "wslb";
		public static final String RYFL = "ryfl";
		public static final String JSZH = "jszh";
		public static final String DABH = "dabh";
		public static final String FZJG = "fzjg";
		public static final String ZJCX = "zjcx";
		public static final String DSR = "dsr";
		public static final String ZSXZQH = "zsxzqh";
		public static final String ZSXXDZ = "zsxxdz";
		public static final String DH = "dh";
		public static final String LXFS = "lxfs";
		public static final String CLFL = "clfl";
		public static final String HPZL = "hpzl";
		public static final String HPHM = "hphm";
		public static final String JTFS = "jtfs";
		public static final String WFSJ = "wfsj";
		public static final String WFDD = "wfdd";
		public static final String WFDZ = "wfdz";
		public static final String WFXW1 = "wfxw1";
		public static final String WFXW2 = "wfxw2";
		public static final String WFXW3 = "wfxw3";
		public static final String WFXW4 = "wfxw4";
		public static final String WFXW5 = "wfxw5";
		public static final String WFJFS = "wfjfs";
		public static final String FKJE = "fkje";
		public static final String ZQMJ = "zqmj";
		public static final String JKFS = "jkfs";
		public static final String FXJG = "fxjg";
		public static final String CFZL = "cfzl";
		public static final String JKBJ = "jkbj";
		public static final String JKRQ = "jkrq";
		public static final String JSJQBJ = "jsjqbj";
		public static final String QZCSLX = "qzcslx";
		public static final String GXSJ = "gxsj";
		public static final String CLSJ = "clsj";
		public static final String SJXM = "sjxm";
		public static final String SJXMMC = "sjxmmc";
		public static final String KLWPCFD = "klwpcfd";
		public static final String SJWPCFD = "sjwpcfd";
		public static final String SCBJ = "scbj";
		public static final String CWXX = "cwxx";
		public static final String GZXM = "gzxm";
		public static final String GZXMMC = "gzxmmc";
		public static final String HDID = "hdid";
		public static final String BZZ = "bzz";
		public static final String SCZ = "scz";
	}

	public class ZapcGzxx {
		public static final String TABLE_NAME = "zapc_gzxx";
		public static final String GZXXBH = "gzxxbh";
		public static final String ID = "id";
		public static final String DJDW = "djdw";
		public static final String JYBH = "jybh";
		public static final String XFFS = "xffs";
		public static final String XLMC = "xlmc";
		public static final String GZDD = "gzdd";
		public static final String FJRS = "fjrs";
		public static final String KSSJ = "kssj";
		public static final String JSSJ = "jssj";
		public static final String CSBJ = "csbj";
		public static final String ZQMJ = "zqmj";
	}

	public class ZapcWpxx {
		public static final String TABLE_NAME = "zapc_wpxx";
		public static final String XLPCWPBH = "xlpcwpbh"; // '巡逻盘查物品编号';
		public static final String BPCWPGZQKBH = "bpcwpgzqkbh"; // '盘查物品工作情况编号';
		public static final String BPCWPRYBH = "bpcwprybh"; // '盘查物品人员编号';
		public static final String BPCWPLX = "bpcwplx"; // '被盘查物品类型';
		public static final String BPCWPMC = "bpcwpmc"; // '被盘查物品名称';
		public static final String BPCWPCP = "bpcwpcp"; // '被盘查物品厂牌';
		public static final String BPCWPXH = "bpcwpxh"; // '被盘查物品型号';
		public static final String CLXH = "clxh"; // '车辆型号';
		public static final String CLHPZL = "clhpzl"; // '车辆号牌种类';
		public static final String BHY = "bhy"; // 车辆号牌，原名编号一
		public static final String BHE = "bhe"; // 发动机号
		public static final String BHS = "bhs"; // 车架号
		public static final String BPCWPPCSJ = "bpcwppcsj"; // '被盘查物品盘查时间';
		public static final String BPCWPPCDD = "bpcwppcdd"; // '被盘查物品盘查地点';
		public static final String PCYY = "pcyy"; // '盘查物品原因';
		public static final String BPCWPCLJG = "bpcwpcljg"; // '被盘查物品处理结果';
		public static final String BPCWPLB = "bpcwplb"; // 被盘查物品类别
		public static final String JCCFX = "jccfx";// 进出城方向
		public static final String SCBJ = "scbj";

	}

	public class ZapcPcryxx {
		public static final String TABLE_NAME = "zapc_pcryxx";
		public static final String PCRYBH = "pcrybh"; // 盘查人员编号
		public static final String GZBH = "gzbh"; // 工作编号
		public static final String RYCLJG = "rycljg";// 人员处理结果
		public static final String RYPCYY = "rypcyy";// 人员盘查原因
		public static final String RYPCDD = "rypcdd";// 人员盘查地点
		public static final String RYBDFS = "rybdfs";// 人员比对方式
		public static final String RYBDJG = "rybdjg";// 人员比对结果
		public static final String RYPCSJ = "rypcsj";// 人员盘查时间
		public static final String JCCFX = "jccfx";// 进出城方向
		public static final String SCBJ = "scbj";
	}

	public class ZapcJbryxx {
		public static final String TABLE_NAME = "zapc_jbryxx";
		public static final String RYBH = "rybh";
		public static final String GMSFHM = "gmsfhm";
		public static final String XM = "xm";
		public static final String ZJZL = "zjzl";
		public static final String ZJHM = "zjhm";
		public static final String XB = "xb";
		public static final String MZ = "mz";
		public static final String CSRQ = "csrq";
		public static final String JGXZ = "jgxz";
		public static final String ZJXY = "zjxy";
		public static final String ZZMM = "zzmm";
		public static final String WHCD = "whcd";
		public static final String HYZK = "hyzk";
		public static final String BYZK = "byzk";
		public static final String SG = "sg";
		public static final String SF = "sf";
		public static final String ZYLB = "zylb";
		public static final String FWCS = "fwcs";
		public static final String LXDH = "lxdh";
		public static final String HJQH = "hjqh";
		public static final String HJXZ = "hjxz";
		public static final String XXJB = "xxjb";
		public static final String RYLB = "rylb";
		public static final String RYSX = "rysx";
		public static final String XZZQH = "xzzqh";
		public static final String XZZXZ = "xzzxz";
		public static final String SCBJ = "scbj";
	}

	// public class SysCode {
	// public static final String TABLE_NAME = "SYS_CODE";
	// public static final String CODE_NAME = "CODE_NAME";
	// public static final String CODE_VALUE = "CODE_VALUE";
	// public static final String MS = "MS";
	// }
	//
	// public class Hmb {
	// public static final String TABLE_NAME = "T_HMB";
	// public static final String HDID = "hdid";
	// public static final String JSHM = "jshm";
	// public static final String DQHM = "dqhm";
	// public static final String HDZL = "hdzl";
	// }

}
