package com.ntga.dao;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;

import com.android.provider.fixcode.Fixcode;
import com.ntga.bean.KeyValueBean;

public class GlobalData {

	// 图片压缩比例
	//public static int picCompress = 60;
	// 保存决定书时验证驾驶员和机动车的方式,初始化为本地车和证
	//public static int drvCheckFs = 2;
	//public static int vehCheckFs = 2;

	// 是否已初始化加载数据
	public static boolean isInitLoadData = false;

	// 系统登录标记，用于心跳包的回传
	public static byte loginStatus = 1;
	// 是否上传GPS位置
	//public static boolean isGpsUpload = false;
	// 是否对非机动车身份证明进行严格证认
	//public static boolean isCheckFjdcSfzm = false;
	// 心跳包和GPS包上传频率，单位是分钟
	//public static int uploadFreq = 2;
	
	/**
	 * 拍照后是否预览
	 */
	//public static boolean isPreviewPhoto = false;

	public static ConnCata connCata = ConnCata.OFFCONN;

	public static ArrayList<KeyValueBean> ryflList = null;
	public static ArrayList<KeyValueBean> sfList = null;
	// public static ArrayList<KeyValueBean> chenshiList = null;

	public static ArrayList<KeyValueBean> hpzlList = null;
	public static ArrayList<KeyValueBean> jtfsList = null;
	public static ArrayList<KeyValueBean> jkfsList = null;
	public static ArrayList<KeyValueBean> jkbjList = null;
	public static ArrayList<KeyValueBean> hpqlList = null;
	// public static ArrayList<String> wfxwdmList = null;
	public static ArrayList<KeyValueBean> clflList = null;
	public static ArrayList<KeyValueBean> cfzlList = null;
	public static ArrayList<KeyValueBean> qzcslxList = null;
	public static ArrayList<KeyValueBean> sjxmList = null;
	public static ArrayList<KeyValueBean> zjcxList = null;
	public static ArrayList<KeyValueBean> clbjList = null;
	public static ArrayList<KeyValueBean> syxzList = null;
	public static ArrayList<KeyValueBean> zzmmList = null;
	public static ArrayList<KeyValueBean> zyxxList = null;

	// 公共字典数据
	public static ArrayList<KeyValueBean> xbList = null;

	// 事故处理字典表
	public static ArrayList<KeyValueBean> acdJszzlList = null;
	public static ArrayList<KeyValueBean> acdRylxList = null;
	public static ArrayList<KeyValueBean> acdSgzrList = null;
	public static ArrayList<KeyValueBean> acdJtfsList = null;
	public static ArrayList<KeyValueBean> acdWfxwList = null;
	public static ArrayList<KeyValueBean> arrayAcdTq = null;
	public static ArrayList<KeyValueBean> arrayAcdSgxt = null;
	public static ArrayList<KeyValueBean> arrayAcdCjpz = null;
	public static ArrayList<KeyValueBean> arrayAcdDcpz = null;
	public static ArrayList<KeyValueBean> arrayAcdTjfs = null;
	public static ArrayList<KeyValueBean> arrayAcdJafs = null;

	// 工作日志中警务状态的列表
	public static ArrayList<KeyValueBean> qwztList = null;

	public static HashMap<String, String> grxx = null;

	// public static String visitorXML;
	/**
	 * 手机串号
	 */
	public static String serialNumber;


	public static void initGlobalData(ContentResolver resolver) {
		

		//int f = ViolationDAO.queryGpsUploadFreq(resolver);
		//if (f > 0)
		//	uploadFreq = f;
		// 加载治安盘查数据
		ZaPcdjDao.initZapcData(resolver);
		// if (mjgrxx == null) {
		// getMjgrxx(resolver);
		// }
		// 个人信息应在程序初始化时从服务器处获取,然后存入数据库
		// if (grxx == null || grxx.isEmpty()) {
		grxx = ViolationDAO.getMjgrxx(resolver);
		// }
		// if (!grxx.isEmpty()) {
		// visitorXML = String.format(GlobalConstant.visitorXMLMb,
		// grxx.get(GlobalConstant.YHBH), grxx.get(GlobalConstant.MM),
		// serialNumber);
		// }
		if(clbjList == null || clbjList.size() == 0){
			clbjList = new ArrayList<KeyValueBean>();
			clbjList.add(new KeyValueBean("0","未处理"));
			clbjList.add(new KeyValueBean("1","已处理"));
		}

		if (ryflList == null || ryflList.size() == 0) {
			ryflList = ViolationDAO.getAllFrmCode(GlobalConstant.RYFL,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, Fixcode.FrmCode.DMZ
							+ " not in ('8','9')", Fixcode.FrmCode.DMZ);
		}

		if (sfList == null || sfList.size() == 0) {
			sfList = ViolationDAO.getAllFrmCode(GlobalConstant.SHENFEN,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM2 }, null, Fixcode.FrmCode.DMZ);
		}
		// if (chenshiList != null) {
		// GlobalData.chenshiList = ViolationDAO.getAllFrmCode(CHENSHI,
		// resolver, new String[] { Fixcode.FrmCode.DMZ,
		// Fixcode.FrmCode.DMSM3 }, null, null);
		// }
		if (hpzlList == null || hpzlList.size() == 0)
			hpzlList = ViolationDAO.getAllFrmCode(GlobalConstant.HPZL,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);

		if (jtfsList == null || jtfsList.size() == 0)
			jtfsList = ViolationDAO.getAllFrmCode(GlobalConstant.JTFS,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);

		if (jkbjList == null || jkbjList.size() == 0)
			jkbjList = ViolationDAO.getAllFrmCode(GlobalConstant.JKBJ,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);

		if (jkfsList == null || jkfsList.size() == 0)
			jkfsList = ViolationDAO.getAllFrmCode(GlobalConstant.JKFS,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, Fixcode.FrmCode.DMZ
							+ "<'3'", Fixcode.FrmCode.DMZ);

		if (hpqlList == null || hpqlList.size() == 0) {
			hpqlList = ViolationDAO.getAllFrmCode(GlobalConstant.HPQL,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		if (clflList == null || clflList.size() == 0) {
			clflList = ViolationDAO.getAllFrmCode(GlobalConstant.CLFL,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}

		if (cfzlList == null || cfzlList.size() == 0) {
			cfzlList = ViolationDAO.getAllFrmCode(GlobalConstant.CFZL,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, Fixcode.FrmCode.DMZ + "<'"
							+ 3 + "'", Fixcode.FrmCode.DMZ);
		}
		if (qzcslxList == null || qzcslxList.size() == 0) {
			qzcslxList = ViolationDAO.getAllFrmCode(GlobalConstant.QZCSLX,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}

		if (zjcxList == null || zjcxList.size() == 0) {
			zjcxList = ViolationDAO.getAllFrmCode(GlobalConstant.ZJCX,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}

		if (xbList == null || xbList.size() == 0) {
			xbList = ViolationDAO.getAllFrmCode(GlobalConstant.FRM_XB,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}

		if (syxzList == null || syxzList.size() == 0) {
			syxzList = ViolationDAO.getAllFrmCode(GlobalConstant.SYXZ,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		if (zzmmList == null || zzmmList.size() == 0) {
			zzmmList = ViolationDAO.getAllFrmCode(GlobalConstant.ZZMM,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		if (zyxxList == null || zyxxList.size() == 0) {
			zyxxList = ViolationDAO.getAllFrmCode(GlobalConstant.ZYXX,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}

		if (acdJszzlList == null || acdJszzlList.size() == 0) {
			acdJszzlList = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_JSZZL,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
			//acdJszzlList.add(0, new KeyValueBean("", ""));
		}

		if (acdRylxList == null || acdRylxList.size() == 0) {
			acdRylxList = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_RYLX,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
			//acdRylxList.add(0, new KeyValueBean("", ""));
		}

		if (acdSgzrList == null || acdSgzrList.size() == 0) {
			acdSgzrList = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_SGZR,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		if (acdJtfsList == null || acdJtfsList.size() == 0) {
			acdJtfsList = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_JTFS,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		if (acdWfxwList == null || acdWfxwList.size() == 0) {
			acdWfxwList = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_WFXW,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		if (arrayAcdTq == null || arrayAcdTq.size() == 0) {
			arrayAcdTq = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_TQ,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		if (arrayAcdSgxt == null || arrayAcdSgxt.size() == 0) {
			arrayAcdSgxt = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_SGXT,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		if (arrayAcdCjpz == null || arrayAcdCjpz.size() == 0) {
			arrayAcdCjpz = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_CLJPZ,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);

			//arrayAcdCjpz.add(0, new KeyValueBean("", ""));
		}
		if (arrayAcdDcpz == null || arrayAcdDcpz.size() == 0) {
			arrayAcdDcpz = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_DLPZ,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
			//arrayAcdDcpz.add(0, new KeyValueBean("", ""));
		}
		if (arrayAcdTjfs == null || arrayAcdTjfs.size() == 0) {
			arrayAcdTjfs = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_TJFS,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
			//arrayAcdTjfs.add(0, new KeyValueBean("", ""));
		}
		if (arrayAcdJafs == null || arrayAcdJafs.size() == 0) {
			arrayAcdJafs = ViolationDAO.getAllFrmCode(GlobalConstant.ACD_JAFS,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
		}
		// 收缴项目列表,和系统不同,在前面加1
		if (sjxmList == null || sjxmList.size() == 0) {
			sjxmList = ViolationDAO.getAllFrmCode(GlobalConstant.SJXM,
					resolver, new String[] { Fixcode.FrmCode.DMZ,
							Fixcode.FrmCode.DMSM1 }, null, Fixcode.FrmCode.DMZ);
			for (KeyValueBean kv : sjxmList) {
				kv.setKey("1" + kv.getKey());
			}
		}

		// if(qwztList == null || qwztList.isEmpty()){
		// qwztList.add(new KeyValueBean("01", "正常工作"));
		// qwztList.add(new KeyValueBean("02", "外地出差"));
		// qwztList.add(new KeyValueBean("03", "本市借调"));
		// qwztList.add(new KeyValueBean("04", "培训学习"));
		// qwztList.add(new KeyValueBean("05", "其他工作"));
		// qwztList.add(new KeyValueBean("06", "正常休息"));
		// qwztList.add(new KeyValueBean("07", "休息"));
		// qwztList.add(new KeyValueBean("08", "病假"));
		// qwztList.add(new KeyValueBean("09", "其他休息"));
		// }

		// if (wfxwdmList == null) {
		// wfxwdmList = ViolationDAO.getAllWfdm(ViolationDAO.QZCSPZ, resolver);
		// }
		isInitLoadData = true;
	}

	/**
	 * 返回当前是否为在线模式
	 * 
	 * @return
	 */
	// public static boolean isOnline() {
	// return connCata != ConnCata.OFFCONN;
	// }
	//
	// public static void setOnline(boolean isOnline) {
	// if (!isOnline)
	// connCata = ConnCata.OFFCONN;
	// }
	//
	// public static void setConnCata(int c) {
	// connCata = c;
	// }

	// /**
	// * 设置在,离线模式
	// *
	// * @param isOnline
	// */
	// public static void setLineMode(boolean isOnline) {
	// connCata = isOnline ? GlobalConstant.ONLINE : GlobalConstant.OFFLINE;
	// }

}
