package com.ntga.dao;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.acd.simple.provider.AcdSimple;
import com.ntga.bean.RepairBean;
import com.ntga.tools.RefectResult;

public class RepairDao {

	private static Uri createUri(String u) {
		return Uri.parse("content://" + AcdSimple.AUTHORITY + "/" + u);
	}

	/**
	 * 查询报修列表
	 * 
	 * @param resolver
	 * @param where
	 * @return
	 */
	public static List<RepairBean> queryRepairs(ContentResolver resolver,
			String where) {
		Uri uri = createUri(AcdSimple.QUERY_REPAIR);
		Cursor cs = resolver.query(uri, null, where, null, AcdSimple.Repair.ID
				+ " desc");
		List<RepairBean> list = RefectResult.rsToObject(RepairBean.class, cs);
		cs.close();
		return list;
	}

	/**
	 * 加入报修
	 * 
	 * @param resolver
	 * @param rep
	 * @return
	 */
	public static String insertRepair(ContentResolver resolver, RepairBean rep) {
		String id = "";
		Uri uri = createUri(AcdSimple.ADD_REPAIR);
		ContentValues cv = RefectResult.createContentByObject(rep);
		cv.remove(AcdSimple.Repair.ID);
		Uri res = resolver.insert(uri, cv);

		if (!TextUtils.isEmpty(res.getLastPathSegment())) {
			id = res.getLastPathSegment();
		}
		return id;
	}

	/**
	 * 删除一条报修
	 * 
	 * @param resolver
	 * @param id
	 * @return
	 */
	public static int delRepair(ContentResolver resolver, long id) {
		Uri uri = createUri(AcdSimple.DEL_REPAIR);
		int res = resolver.delete(uri, AcdSimple.Repair.ID + "=" + id, null);
		return res;
	}

	public static int updateRepair(ContentResolver resolver, RepairBean repair) {
		Uri uri = createUri(AcdSimple.UPDATE_REPAIR);
		ContentValues cv = RefectResult.createContentByObject(repair);
		int id = resolver.update(uri, cv, AcdSimple.Repair.ID + "="
				+ repair.getId(), null);
		return id;
	}
}
