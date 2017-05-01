package com.ntga.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

import com.ntga.bean.MenuGridBean;
import com.ntga.bean.MenuOptionBean;
import com.ntga.dao.GlobalMethod;

public class MainLoading {

	// public static Map<String, Integer> getMenuImgContent() {
	// Map<String, Integer> map = new HashMap<String, Integer>();
	// Field[] fields = R.drawable.class.getDeclaredFields();
	// for (Field field : fields) {
	// int index;
	// try {
	// index = field.getInt(R.drawable.class);
	// map.put(field.getName().toLowerCase(), index);
	// } catch (IllegalArgumentException e) {
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// e.printStackTrace();
	// }
	// }
	// return map;
	// }

	public static List<MenuGridBean> parseMenuXml(Context context) {
		try {
			InputStream myInput = context.getAssets().open("menu_option.xml");
			InputStreamReader bfr = new InputStreamReader(myInput);
			List<MenuGridBean> grids = parseMenuXml(bfr);
			bfr.close();
			return grids;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<MenuGridBean> parseMenuXml(InputStreamReader xml) {
		// Map<String, Integer> map = getMenuImgContent();
		List<MenuGridBean> menuGrid = new ArrayList<MenuGridBean>();
		MenuGridBean mb = new MenuGridBean();
		XmlPullParser parser = Xml.newPullParser();
		try {
			boolean isContinue = true;
			parser.setInput(xml);
			int event = parser.getEventType();// 产生第一个事件
			while (event != XmlPullParser.END_DOCUMENT) {
				String curTag = parser.getName();
				switch (event) {
				// 判断当前事件是否是文档开始事件
				case XmlPullParser.START_DOCUMENT:
					break;
				// 判断当前事件是否是标签元素开始事件
				case XmlPullParser.START_TAG: {
					if ("grid".equals(curTag)) {
						mb = new MenuGridBean();
						mb.setId(Integer.valueOf(parser.getAttributeValue(null,
								"gid")));
						String img = parser.getAttributeValue(null, "img");
						mb.setImg(img);
						mb.setGridName(parser.getAttributeValue(null, "name"));
					} else if ("menu".equals(curTag)) {
						MenuOptionBean menu = new MenuOptionBean();
						menu.setId(Integer.valueOf(parser.getAttributeValue(
								null, "id")));
						menu.setMenuName(parser.getAttributeValue(null, "name"));
						String img = parser.getAttributeValue(null, "img");
						menu.setImg(img);
						menu.setQx(Integer.valueOf(parser.getAttributeValue(
								null, "qx")));
						menu.setPck(parser.getAttributeValue(null, "pck"));
						menu.setClassName(parser.getAttributeValue(null,
								"cname"));
						menu.setDataName(parser
								.getAttributeValue(null, "dname"));
						menu.setData(parser.getAttributeValue(null, "data"));
						menu.setCatalog(parser.getAttributeValue(null,
								"catalog"));
						mb.getOptions().add(menu);
					}
				}
					break;
				// 判断当前事件是否是标签元素结束事件
				case XmlPullParser.END_TAG:
					if ("grid".equals(curTag)) {
						menuGrid.add(mb);
					}
					break;
				}
				if (!isContinue)
					break;
				event = parser.next();// 进入下一个元素并触发相应事件
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return menuGrid;
	}

	// public static List<List<MenuOption>> initMenuOption(Context context) {
	// List<List<MenuOption>> list = new ArrayList<List<MenuOption>>();
	// List<MenuGridBean> grids = parseMenuXml(context);
	// for (MenuGridBean mb : grids) {
	// List<MenuOption> menus = new ArrayList<MenuOption>();
	// for (MenuOptionBean m : mb.getOptions()) {
	// menus.add(new MenuOption(m.getId(), m.getMenuName(),
	// m.getImg(), m.getQx()));
	// }
	// list.add(menus);
	// }
	// return list;
	// }

	// private static List<List<MenuOption>> initMenuOption() {
	//
	// List<List<MenuOption>> list = new ArrayList<List<MenuOption>>();
	// List<MenuOption> menus = new ArrayList<MenuOption>();
	// menus.add(new MenuOption(0, "简易程序", R.drawable.menu_ywcl_jycf, JXJ));
	// menus.add(new MenuOption(1, "轻微警告", R.drawable.menu_ywcl_qwjg, JXJ));
	// menus.add(new MenuOption(2, "强制措施", R.drawable.menu_ywcl_qzcs, JXJ));
	// menus.add(new MenuOption(3, "违法通知书", R.drawable.menu_ywcl_wftzs, JXJ));
	// menus.add(new MenuOption(4, "非现场处罚", R.drawable.menu_ywcl_fxccf, JXJ));
	// menus.add(new MenuOption(5, "文书打印", R.drawable.menu_ywcl_wsdy, JXJ));
	// menus.add(new MenuOption(6, "治安盘查", R.drawable.menu_ywcl_zapc, JXJ));
	// // 根据警种过滤系统菜单
	// // int i = 0;
	// // while (i < menus.size()) {
	// // if (menus.get(i).getQx() != JXJ)
	// // menus.remove(i);
	// // else
	// // i++;
	// // }
	//
	// List<MenuOption> zhcxMenus = new ArrayList<MenuOption>();
	// zhcxMenus
	// .add(new MenuOption(0, "驾驶员查询", R.drawable.menu_xxcx_jsy, JXJ));
	// zhcxMenus
	// .add(new MenuOption(1, "机动车查询", R.drawable.menu_xxcx_jdc, JXJ));
	// zhcxMenus
	// .add(new MenuOption(2, "人口综查", R.drawable.menu_xxcx_rkzc, JXJ));
	// zhcxMenus
	// .add(new MenuOption(3, "违法查询", R.drawable.menu_xxcx_wfcx, JXJ));
	// zhcxMenus
	// .add(new MenuOption(4, "罚款查询", R.drawable.menu_xxcx_fkcx, JXJ));
	// zhcxMenus.add(new MenuOption(5, "在逃人员", R.drawable.menu_xxcx_ztrycx,
	// JXJ));
	// zhcxMenus.add(new MenuOption(6, "民警电话", R.drawable.menu_xxcx_mjdhcx,
	// JXJ));
	// zhcxMenus.add(new MenuOption(7, "旅馆住宿", R.drawable.menu_xxcx_lgxxcx,
	// JXJ));
	//
	// List<MenuOption> xtpzMenus = new ArrayList<MenuOption>();
	// xtpzMenus
	// .add(new MenuOption(0, "文书管理", R.drawable.menu_xtgl_wsgl, JXJ));
	// xtpzMenus
	// .add(new MenuOption(1, "违法代码", R.drawable.menu_xtgl_wfdm, JXJ));
	// xtpzMenus
	// .add(new MenuOption(2, "执勤地点", R.drawable.menu_xtgl_zqdd, JXJ));
	// xtpzMenus.add(new MenuOption(3, "打印机配置", R.drawable.menu_xtgl_dyjpz,
	// JXJ));
	// xtpzMenus
	// .add(new MenuOption(4, "网络配置", R.drawable.menu_xtgl_wlpz, JXJ));
	// xtpzMenus
	// .add(new MenuOption(5, "民警信息", R.drawable.menu_xtgl_mjxx, JXJ));
	// list.add(menus);
	// list.add(zhcxMenus);
	// list.add(xtpzMenus);
	// return list;
	// }

	public static boolean checkServerRunning(Context context, String packName,
			String serverName) {
		boolean isRunning = false;
		// 检测服务是否在运行
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = activityManager.getRunningServices(100);
		for (RunningServiceInfo ri : list) {
			if (TextUtils.equals(ri.service.getPackageName(), packName)
					&& TextUtils.equals(ri.service.getClassName(), serverName)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 根据权限决定菜单的显示
	 * 
	 * @param list
	 * @param sqx
	 * @return
	 */
	public static List<MenuGridBean> filterMenuByQx(List<MenuGridBean> list,
			String sqx) {
		int qx = Integer.valueOf(sqx.trim());
		List<MenuGridBean> result = new ArrayList<MenuGridBean>();
		for (MenuGridBean menuGridBean : list) {
			if (menuGridBean.getId() == 0) {
				MenuGridBean mb = new MenuGridBean();
				mb.setGridName(menuGridBean.getGridName());
				mb.setId(menuGridBean.getId());
				mb.setImg(menuGridBean.getImg());
				ArrayList<MenuOptionBean> options = new ArrayList<MenuOptionBean>();
				for (MenuOptionBean op : menuGridBean.getOptions()) {
					if (isQx(op, qx))
						options.add(op);
				}
				mb.setOptions(options);
				result.add(mb);
			} else {
				result.add(menuGridBean);
			}
		}
		return result;
	}

	private static boolean isQx(MenuOptionBean op, int qx) {
		int pw = GlobalMethod.power(2, op.getId());
		int i = pw & qx;
		return (i == pw);
	}

	/**
	 * 返回数据正确则将其存入数据库并加入个人信息变量
	 * 
	 * @param lm
	 * @param resolver
	 */
	// public static void updateMjgrxx(WebQueryResult<LoginMessage> lm,
	// ContentResolver resolver) {
	// if (lm.getResult() == null)
	// return;
	// for (int i = 0; i < lm.getResult().getFields().size(); i++) {
	// String value = lm.getResult().getValues().get(i);
	// GlobalData.grxx.put(lm.getResult().getFields().get(i), TextUtils
	// .isEmpty(value) ? "" : value);
	// }
	// ViolationDAO.saveMjgrxxIntoDB(GlobalData.grxx, resolver);
	// }

}
