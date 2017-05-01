package com.ntga.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.android.provider.userdata.Userdata;
import com.ntga.bean.THmb;

public class WsglDAO {

	/**
	 * 删除号段号码,此方法应配合上交文书使用
	 * 
	 * @param hdzl
	 *            决定书种类
	 * @param resolver
	 * @return
	 */
	public static int delHmb(String hdzl, ContentResolver resolver) {
		Uri url = Uri.parse("content://" + Userdata.AUTHORITY + "/delhmb");
		int row = resolver.delete(url, Userdata.Hmb.HDZL + "='" + hdzl + "'",
				null);
		return row;
	}

	public static int delHmbByHdid(String hdid, ContentResolver resolver) {
		Uri url = Uri.parse("content://" + Userdata.AUTHORITY + "/delhmb");
		int row = resolver.delete(url, Userdata.Hmb.HDID + "=?",
				new String[] { hdid });
		return row;
	}

	/**
	 * 取得特定文书种类
	 * 
	 * @param hdzl
	 * @param resolver
	 * @return
	 */
	public static THmb getCurrentJdsbh(String hdzl, String zqmj,
			ContentResolver resolver) {
		THmb jdsbh = null;
		List<THmb> list = getJdsListByHdzl(hdzl, zqmj, resolver);
		for (int i = 0; i < list.size(); i++) {
			long temp = Long.valueOf(list.get(i).getJshm());
			// 有一个以上的号段列表,比较一下大小,取小的赋值
			if (jdsbh == null || Long.valueOf(jdsbh.getJshm()) > temp)
				jdsbh = list.get(i);
		}
		if (jdsbh != null) {
			// 有决定书可以获取，判断当前值是否与处罚表中有重号，极少数现象
			while (ViolationDAO.getViolationByJdsbh(jdsbh.getDqhm(), resolver) != null) {
				saveHmbAddOne(jdsbh, resolver);
				jdsbh = getHmbByHdId(jdsbh.getHdid(), jdsbh.getHdzl(), resolver);
				if (Long.valueOf(jdsbh.getDqhm()) > Long.valueOf(jdsbh
						.getJshm()))
					return null;
			}
		}
		return jdsbh;
	}

	/**
	 * 取得特定文书种类当前的决定书对象，参数类型为整型
	 * 
	 * @param hdzl
	 * @param resolver
	 * @return
	 */
	public static THmb getCurrentJdsbh(int hdzl, String zqmj,
			ContentResolver resolver) {
		return getCurrentJdsbh(String.valueOf(hdzl), zqmj, resolver);
	}

	/**
	 * 以号码种类为条件查找号码
	 * 
	 * @param hdzl
	 *            简易程序决定书或强制措施凭证
	 * @return
	 */
	public static List<THmb> getJdsListByHdzl(String hdzl, String zqmj,
			ContentResolver resolver) {
		List<THmb> hms = new ArrayList<THmb>();
		Uri CONTENT_URI = Uri.parse("content://" + Userdata.AUTHORITY
				+ "/queryhmb");
		String where = Userdata.Hmb.HDZL + "='" + hdzl + "' and "
				+ Userdata.Hmb.ZQMJ + "='" + zqmj + "' and dqhm <= jshm ";
		Cursor cs = resolver.query(CONTENT_URI, null, where, null, "id desc");
		if (cs.moveToFirst()) {
			do {
				hms.add(getHmbByCs(cs));
			} while (cs.moveToNext());
		}
		cs.close();
		return hms;
	}

	private static THmb getHmbByCs(Cursor cs) {
		THmb hm = new THmb();
		hm.setId(cs.getString(0));
		hm.setHdid(cs.getString(1));
		hm.setJshm(cs.getString(2));
		hm.setDqhm(cs.getString(3));
		hm.setHdzl(cs.getString(4));
		return hm;
	}

	// private static THmb queryHmbById(String id, ContentResolver resolver) {
	// String where = "id=" + id;
	// THmb hm = null;
	// Uri CONTENT_URI = Uri.parse("content://" + Userdata.AUTHORITY
	// + "/queryhmb");
	// Cursor cs = resolver.query(CONTENT_URI, null, where, null, "id desc");
	// if (cs.moveToFirst()) {
	// hm = getHmbByCs(cs);
	// }
	// cs.close();
	// return hm;
	// }

	/**
	 * 得到某种决定书的剩余数量
	 * 
	 * @param hdzl
	 * @param resolver
	 * @return
	 */
	public static int getJdsCount(String hdzl, String zqmj,
			ContentResolver resolver) {
		List<THmb> list = getJdsListByHdzl(hdzl, zqmj, resolver);
		return getJdsCount(list);
	}

	/**
	 * 得到某种决定书的剩余数量,参数为已查询到的列表
	 * 
	 * @param list
	 * @return
	 */
	public static int getJdsCount(List<THmb> list) {
		int dqWssl = 0;
		if (list == null || list.size() == 0)
			return 0;
		for (THmb hm : list) {
			dqWssl += Long.valueOf(hm.getJshm()) - Long.valueOf(hm.getDqhm())
					+ 1;
		}
		return dqWssl;
	}

	/**
	 * 保存号码到数据库中，同一个号段ID将覆盖，
	 * 
	 * @param hdid
	 * @param jshm
	 * @param dqhm
	 * @param hdzl
	 * @param resolver
	 * @return
	 */
	// public static int saveHmb(String hdid, String jshm, String dqhm,
	// String hdzl, ContentResolver resolver) {
	// Uri url = Uri.parse("content://" + Userdata.AUTHORITY + "/updatehmb");
	// ContentValues cv = new ContentValues();
	// // cv.put("id", hm.getId());
	// cv.put(Userdata.Hmb.HDID, hdid);
	// cv.put(Userdata.Hmb.JSHM, jshm);
	// cv.put(Userdata.Hmb.DQHM, dqhm);
	// cv.put(Userdata.Hmb.HDZL, hdzl);
	// int row = resolver.update(url, cv, null, null);
	// return row;
	// }

	/**
	 * 保存号码到数据库中，同一个号段ID将覆盖，
	 * 
	 * @param hmb
	 * @param resolver
	 * @return
	 */
	public static int saveHmb(THmb hmb, ContentResolver resolver) {
		THmb oldHmb = getHmbByHdId(hmb.getHdid(), hmb.getHdzl(), resolver);
		String dqhm = hmb.getDqhm();
		// 如果原有号码大于传入号码
		if (oldHmb != null
				&& Long.valueOf(oldHmb.getDqhm()) > Long.valueOf(dqhm)) {
			dqhm = oldHmb.getDqhm();
		}
		Uri url = Uri.parse("content://" + Userdata.AUTHORITY + "/updatehmb");
		ContentValues cv = new ContentValues();
		// cv.put("id", hm.getId());
		cv.put(Userdata.Hmb.HDID, hmb.getHdid());
		cv.put(Userdata.Hmb.JSHM, hmb.getJshm());
		cv.put(Userdata.Hmb.DQHM, dqhm);
		cv.put(Userdata.Hmb.HDZL, hmb.getHdzl());
		cv.put(Userdata.Hmb.ZQMJ, GlobalData.grxx.get(GlobalConstant.YHBH));
		int row = resolver.update(url, cv, null, null);
		return row;
	}

	public static THmb getHmbByHdId(String hdid, String hdzl,
			ContentResolver resolver) {
		THmb hm = null;
		String where = Userdata.Hmb.HDID + "='" + hdid + "' and "
				+ Userdata.Hmb.HDZL + "='" + hdzl + "'";
		Uri CONTENT_URI = Uri.parse("content://" + Userdata.AUTHORITY
				+ "/queryhmb");
		Cursor cs = resolver.query(CONTENT_URI, null, where, null, "id desc");
		if (cs.moveToFirst()) {
			hm = getHmbByCs(cs);
		}
		cs.close();
		return hm;
	}

	public static int saveHmbAddOne(THmb hmb, ContentResolver resolver) {
		Uri url = Uri.parse("content://" + Userdata.AUTHORITY + "/updatehmb");
		String glbm = hmb.getDqhm().substring(0, 6);
		long dqhm = Long.valueOf(hmb.getDqhm().substring(6)) + 1;
		ContentValues cv = new ContentValues();
		// cv.put("id", hm.getId());
		cv.put(Userdata.Hmb.HDID, hmb.getHdid());
		cv.put(Userdata.Hmb.JSHM, hmb.getJshm());
		cv.put(Userdata.Hmb.DQHM, glbm + String.valueOf(dqhm));
		cv.put(Userdata.Hmb.HDZL, hmb.getHdzl());
		cv.put(Userdata.Hmb.ZQMJ, GlobalData.grxx.get(GlobalConstant.YHBH));
		int row = resolver.update(url, cv, null, null);
		return row;
	}

	// private static THmb queryHmbByDqhm(String hdzl, String dqhm,
	// ContentResolver resolver) {
	// String where = "hdzl='" + hdzl + "' and jshm>='" + dqhm
	// + "' AND jshm<'" + dqhm + "'+100";
	// THmb hm = null;
	// Uri CONTENT_URI = Uri.parse("content://" + Userdata.AUTHORITY
	// + "/queryhmb");
	// Cursor cs = resolver.query(CONTENT_URI, null, where, null, "id desc");
	// if (cs.moveToFirst()) {
	// hm = getHmbByCs(cs);
	// }
	// cs.close();
	// return hm;
	// }

	/**
	 * 检查单位代码与文书代码是否不区配
	 * 
	 * @param jdsList
	 * @return
	 */
	public static boolean hmNotEqDw(List<THmb> jdsList) {
		String sjdw = GlobalData.grxx.get(GlobalConstant.YBMBH).substring(0, 6);
		for (THmb hmb : jdsList) {
			if (!TextUtils.equals(sjdw, hmb.getDqhm().substring(0, 6))) {
				return true;
			}
		}
		return false;
	}

	public static boolean hmNotEqDw(THmb jdshm) {
		String sjdw = GlobalData.grxx.get(GlobalConstant.YBMBH).substring(0, 6);
		if (!TextUtils.equals(sjdw, jdshm.getDqhm().substring(0, 6))) {
			return true;
		}
		return false;
	}
}
