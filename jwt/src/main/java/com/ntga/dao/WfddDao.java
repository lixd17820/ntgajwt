package com.ntga.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.provider.fixcode.Fixcode;
import com.android.provider.flashcode.Flashcode;
import com.android.provider.roadcode.Roadcode;
import com.ntga.bean.FavorWfddBean;
import com.ntga.bean.KeyValueBean;

import java.util.ArrayList;
import java.util.List;

public class WfddDao {

    // public static final String[] USER_XZQH_PROJECTION = new String[] {
    // Flashcode.UserXzqh.XZQH, Flashcode.UserXzqh.XZQHMC };
    //
    // public static final String[] DLDM_DLMC_PROJECTION = new String[] {
    // Roadcode.RoadItem.DLDM, Roadcode.RoadItem.DLMC };
    //
    private static final String[] FAVOR_WFDD_PROJECTION = new String[]{
            Flashcode.FavorWfdd.ID, Flashcode.FavorWfdd.XZQH,
            Flashcode.FavorWfdd.DLDM, Flashcode.FavorWfdd.LDDM,
            Flashcode.FavorWfdd.MS, Flashcode.FavorWfdd.SYSLDMC,
            Flashcode.FavorWfdd.FAVORLDMC, Flashcode.FavorWfdd.YXBJ,
            Flashcode.FavorWfdd.ZQMJ};

    /**
     * 从ROADCODE的系统参数表中查询所属大队管理的行政区划代码
     *
     * @param resolver
     * @param dwdm
     * @return 字符串数组
     */
    private static String getOwnerXzqhCode(ContentResolver resolver, String dwdm) {
        // String[] xzqh = null;
        Uri url = Uri.parse("content://com.android.provider.roadcode/xzqh");
        String where = Roadcode.Xzqh.GLBM + "='" + dwdm + "'";
        Log.e("WfddDao", where);
        Cursor cs = resolver.query(url, new String[]{Roadcode.Xzqh.CSZ},
                where, null, null);
        String xzqhs = "";
        if (cs.moveToFirst()) {
            xzqhs = cs.getString(0);
            // xzqh = xzqhs.split(",");
        }
        cs.close();
        return xzqhs;
    }

    /**
     * 根据单位代码取得本单位的行政区划名称和代码列表
     *
     * @param resolver
     * @param dwdm
     * @return
     */
    public static ArrayList<KeyValueBean> getOwnerXzqhList(
            ContentResolver resolver, String dwdm) {
        String xzqhs = getOwnerXzqhCode(resolver, dwdm);
        ArrayList<KeyValueBean> list = ViolationDAO.getAllFrmCode(
                GlobalConstant.XZQH, resolver, new String[]{
                        Fixcode.FrmCode.DMZ, Fixcode.FrmCode.DMSM1},
                Fixcode.FrmCode.DMZ + " IN (" + xzqhs + ")",
                Fixcode.FrmCode.DMZ);
        return list;
    }

    /**
     * 从道路数据库中根据行政区划查询路段列表
     *
     * @param xzqh
     * @return
     */
    public static List<KeyValueBean> getRoadItemsByXzqh(String xzqh,
                                                        ContentResolver resolver) {
        if (xzqh == null)
            return null;
        List<KeyValueBean> list = new ArrayList<KeyValueBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.roadcode/roaditem");
        String where = Roadcode.RoadItem.XZQH + " like '%" + xzqh
                + "%' AND JLZT='1'";
        Cursor cs = resolver.query(CONTENT_URI, new String[]{
                        Roadcode.RoadItem.DLDM, Roadcode.RoadItem.DLMC}, where, null,
                null);
        int row = 0;
        if (cs.moveToFirst()) {
            do {
                list.add(new KeyValueBean(cs.getString(0), cs.getString(1)));
                row++;
            } while (cs.moveToNext());
        }
        Log.e("abc", "have data " + row);
        cs.close();
        return list;
    }

    /**
     * 根据道路取得路段列表
     *
     * @param road
     * @param resolver
     * @return
     */
    public static List<KeyValueBean> getRoadSegByRoad(String road, String xzqh,
                                                      ContentResolver resolver) {
        if (road == null)
            return null;
        List<KeyValueBean> list = new ArrayList<KeyValueBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.roadcode/roadsegitem");
        String where = Roadcode.RoadsegItem.DLDM + "='" + road + "' AND "
                + Roadcode.RoadsegItem.XZQH + "='" + xzqh + "'";
        Cursor cs = resolver.query(CONTENT_URI, new String[]{
                        Roadcode.RoadsegItem.LDDM, Roadcode.RoadsegItem.LDMC}, where,
                null, null);
        int row = 0;
        if (cs.moveToFirst()) {
            do {
                list.add(new KeyValueBean(cs.getString(0), cs.getString(1)));
                row++;
            } while (cs.moveToNext());
        }
        Log.e("abc", "have data " + row);
        cs.close();
        return list;
    }

    /**
     * 删除自选地点
     *
     * @param id
     * @param resolver
     * @return
     */
    public static int delFavorWfddById(String id, ContentResolver resolver) {
        int row = 0;
        Uri url = Uri.parse("content://com.android.provider.flashcode/delwfdd");
        row = resolver.delete(url, Flashcode.FavorWfdd.ID + "=" + id, null);
        return row;
    }

    public static List<String> getListFromFavorWfddBeans(List<FavorWfddBean> l) {
        List<String> list = new ArrayList<String>();
        if (l != null && l.size() > 0) {
            for (FavorWfddBean kv : l) {
                list.add(kv.getFavorLdmc());
            }
        }
        return list;
    }

    /**
     * 加入自选路段列表,并加入数据库
     */
    public static void addFavorWfdd(FavorWfddBean dd, ContentResolver resolver) {
        // 写数据库
        ContentValues values = new ContentValues();
        values.put(Flashcode.FavorWfdd.XZQH, dd.getXzqh());
        values.put(Flashcode.FavorWfdd.DLDM, dd.getDldm());
        values.put(Flashcode.FavorWfdd.LDDM, dd.getLddm());
        values.put(Flashcode.FavorWfdd.MS, dd.getMs());
        values.put(Flashcode.FavorWfdd.FAVORLDMC, dd.getFavorLdmc());
        values.put(Flashcode.FavorWfdd.SYSLDMC, dd.getSysLdmc());
        values.put(Flashcode.FavorWfdd.ZQMJ,
                GlobalData.grxx.get(GlobalConstant.YHBH));
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.flashcode/addwfdd");
        resolver.insert(CONTENT_URI, values);
    }

    public static List<FavorWfddBean> getAllFavorWfdd(ContentResolver resolver) {
        List<FavorWfddBean> list = new ArrayList<FavorWfddBean>();
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.flashcode/queryfavorwfdd");
        String where = Flashcode.FavorWfdd.ZQMJ + "='"
                + GlobalData.grxx.get(GlobalConstant.YHBH) + "'";
        Cursor cs = resolver.query(CONTENT_URI, FAVOR_WFDD_PROJECTION, where,
                null, null);
        int row = 0;
        if (cs.moveToFirst()) {
            do {
                FavorWfddBean dd = new FavorWfddBean();
                dd.setId(cs.getString(0));
                dd.setXzqh(cs.getString(1));
                dd.setDldm(cs.getString(2));
                dd.setLddm(cs.getString(3));
                dd.setMs(cs.getString(4));
                dd.setSysLdmc(cs.getString(5));
                dd.setFavorLdmc(cs.getString(6));
                dd.setYxbj(cs.getString(7));
                list.add(dd);
                row++;
            } while (cs.moveToNext());
        }
        Log.e("WfddDao", "getAllFavorWfdd " + row);
        cs.close();

        return list;
    }

    public static boolean isWfddOk(String wfdd, ContentResolver resolver) {
        if (TextUtils.isEmpty(wfdd) || wfdd.length() != 18)
            return false;
        FavorWfddBean f = new FavorWfddBean();
        f.setXzqh(wfdd.substring(0, 6));
        f.setDldm(wfdd.substring(6, 11));
        f.setLddm(wfdd.substring(11, 15));
        f.setMs(wfdd.substring(15, 18));
        return isFavorDldmOK(f, resolver);
    }

    /**
     * 验证自选路段是否正确
     *
     * @param f
     * @param resolver
     * @return
     */
    public static boolean isFavorDldmOK(FavorWfddBean f, ContentResolver resolver) {
        String xzqhs = getOwnerXzqhCode(resolver,
                GlobalData.grxx.get(GlobalConstant.YBMBH));
        if (TextUtils.isEmpty(xzqhs))
            return false;
        if (xzqhs.indexOf(f.getXzqh()) < 0)
            return false;
        if (!TextUtils.isDigitsOnly(f.getDldm()))
            return false;
        int row = 0;
        if (Integer.valueOf(f.getDldm().substring(0, 1)) < 5) {
            // 国省道
            Uri CONTENT_URI = Uri
                    .parse("content://com.android.provider.roadcode/roaditem");
            String where = Roadcode.RoadItem.XZQH + " like '%"
                    + f.getXzqh() + "%' AND JLZT='1' AND "
                    + Roadcode.RoadItem.DLDM + "='" + f.getDldm() + "'";
            Cursor cs = resolver.query(CONTENT_URI, new String[]{
                            Roadcode.RoadItem.DLDM, Roadcode.RoadItem.DLMC},
                    where, null, null);

            if (cs.moveToFirst()) {
                do {
                    row++;
                } while (cs.moveToNext());
            }
            cs.close();
        } else {
            // 本地道路
            Uri CONTENT_URI = Uri
                    .parse("content://com.android.provider.roadcode/roadsegitem");
            String where = Roadcode.RoadsegItem.DLDM + "='" + f.getDldm()
                    + "' AND " + Roadcode.RoadsegItem.XZQH + "='"
                    + f.getXzqh() + "' AND " + Roadcode.RoadsegItem.LDDM
                    + "='" + f.getLddm() + "'";
            Cursor cs = resolver.query(CONTENT_URI, new String[]{
                            Roadcode.RoadsegItem.LDDM, Roadcode.RoadsegItem.LDMC},
                    where, null, null);
            if (cs.moveToFirst()) {
                do {
                    row++;
                } while (cs.moveToNext());
            }
            cs.close();
        }
        return row > 0;
    }

    /**
     * 验证自选路段是否合法,不合法则删除
     *
     * @param resolver
     */
    public static void checkFavorWfld(ContentResolver resolver) {
        List<FavorWfddBean> favList = getAllFavorWfdd(resolver);
        if (favList == null || favList.isEmpty())
            return;
        // 部门编号字典
        boolean grxxIsNull = GlobalData.grxx == null || GlobalData.grxx.isEmpty();
        for (FavorWfddBean f : favList) {
            if (grxxIsNull || !isFavorDldmOK(f, resolver))
                delFavorWfddById(f.getId(), resolver);
        }// end for
    }

    /**
     * 查找公路情况
     *
     * @param xzqh
     * @param dldm
     * @param resolver
     * @return 1 道路类型，第2为公路行政等级,3为道路名称
     */
    public static String[] getDlmx(String xzqh, String dldm,
                                   ContentResolver resolver) {
        String[] result = null;
        Uri CONTENT_URI = Uri
                .parse("content://com.android.provider.roadcode/roaditem");
        String where = Roadcode.RoadItem.XZQH + " like '%" + xzqh
                + "%' AND JLZT='1' AND " + Roadcode.RoadItem.DLDM + "='" + dldm
                + "'";
        Cursor cs = resolver.query(CONTENT_URI, new String[]{
                Roadcode.RoadItem.DLLX, Roadcode.RoadItem.GLXZDJ,
                Roadcode.RoadItem.DLMC}, where, null, null);
        if (cs.moveToFirst()) {
            do {
                result = new String[3];
                result[0] = cs.getString(0);
                result[1] = cs.getString(1);
                result[2] = cs.getString(2);
            } while (cs.moveToNext());
        }
        cs.close();
        return result;
    }

    /**
     * 测试一下路口表是否能正确工作，过会儿删除掉
     *
     * @param resolver
     * @return
     */
    public static int testCrossCount(ContentResolver resolver) {
        int row = 0;
        try {
            Uri uri = Uri
                    .parse("content://com.android.provider.roadcode/query_cross");
            Cursor cs = resolver.query(uri, new String[]{Roadcode.JtbzCross.ID}, null, null, null);
            if (cs.moveToFirst()) {
                do {
                    row++;
                } while (cs.moveToNext());
            }
            cs.close();
        }catch (Exception e){

        }
        return row;
    }

    public static boolean isGsd(String road) {
        if (TextUtils.isEmpty(road))
            return false;
        char c = road.charAt(0);
        return Integer.valueOf(c) < 53;
    }

    /**
     * 从标准表中查询路段
     *
     * @param xzqh
     * @return
     */
    // public static List<KeyValueBean> getSysRoadsByXzqh(String xzqh,
    // ContentResolver resolver) {
    // List<KeyValueBean> list = new ArrayList<KeyValueBean>();
    // Uri CONTENT_URI = Uri
    // .parse("content://com.android.provider.roadcode/roaditem");
    // String where = Roadcode.RoadItem.XZQH + " like '%" + xzqh + "%' AND "
    // + Roadcode.RoadItem.JLZT + "='1'";
    // Cursor cs = resolver.query(CONTENT_URI, DLDM_DLMC_PROJECTION, where,
    // null, null);
    // int row = 0;
    // if (cs.moveToFirst()) {
    // do {
    // list.add(new KeyValueBean(cs.getString(0), cs.getString(1)));
    // row++;
    // } while (cs.moveToNext());
    // }
    // Log.e("abc", "have data " + row);
    // cs.close();
    // return list;
    // }

    /**
     * 从用户路段表中查找路段
     *
     * @param xzqh
     * @param road
     */
    // public static List<KeyValueBean> getRoadsegByXzqhRoad(String xzqh,
    // String road, ContentResolver resolver) {
    // if (xzqh == null || road == null)
    // return null;
    // List<KeyValueBean> list = new ArrayList<KeyValueBean>();
    // Uri CONTENT_URI = Uri
    // .parse("content://com.android.provider.flashcode/userseg");
    // String selection = Flashcode.UserRoadSeg.XZQH + "='" + xzqh + "' AND "
    // + Flashcode.UserRoadSeg.DLDM + "='" + road + "'";
    //
    // Cursor cs = resolver.query(CONTENT_URI, null, selection, null, null);
    // int row = 0;
    // if (cs.moveToFirst()) {
    // do {
    // list.add(new KeyValueBean(cs.getString(3), cs.getString(4)));
    // row++;
    // } while (cs.moveToNext());
    // }
    // cs.close();
    // return list;
    // }

    /**
     * 从用户本地表中根据行政区划查询路段列表
     *
     * @param xzqh
     * @return
     */
    // public static List<KeyValueBean> getRoadsByXzqh(String xzqh,
    // ContentResolver resolver) {
    // if (xzqh == null)
    // return null;
    // List<KeyValueBean> list = new ArrayList<KeyValueBean>();
    // Uri CONTENT_URI = Uri
    // .parse("content://com.android.provider.flashcode/userroad/"
    // + xzqh);
    // Cursor cs = resolver.query(CONTENT_URI, null, null, null, null);
    // int row = 0;
    // if (cs.moveToFirst()) {
    // do {
    // list.add(new KeyValueBean(cs.getString(2), cs.getString(3)));
    // row++;
    // } while (cs.moveToNext());
    // }
    // Log.e("abc", "have data " + row);
    // cs.close();
    //
    // return list;
    // }

    /**
     * 解析XML文件,并加入到数据库中
     *
     * @return
     * @throws Exception
     */
    // public static void addDatabaseFromXML(Context context, String xml,
    // ContentResolver resolver) {
    // long rowid = 0;
    // Uri url = Uri.parse("content://com.android.provider.flashcode/delall");
    // resolver.delete(url, null, null);
    //
    // url = Uri.parse("content://com.android.provider.flashcode/addxzqh");
    //
    // // 更新道路列表的地址
    // Uri roadUrl = Uri
    // .parse("content://com.android.provider.flashcode/addroad");
    //
    // Uri segUrl = Uri
    // .parse("content://com.android.provider.flashcode/addseg");
    //
    // List<String> xzqhList = new ArrayList<String>();
    // try {
    // InputStream in = context.getAssets().open(xml);
    // DocumentBuilderFactory dbf;
    // dbf = DocumentBuilderFactory.newInstance();
    // DocumentBuilder db = dbf.newDocumentBuilder();
    //
    // Document dom = db.parse(in);
    // NodeList xzqhs = dom.getElementsByTagName("XZQH");
    // for (int i = 0; i < xzqhs.getLength(); i++) {
    // Element e = (Element) xzqhs.item(i);
    // String xzqh = e.getAttribute("BH").trim();
    // xzqhList.add(xzqh);
    // // 加入可以管理的行政区划表
    // ContentValues cv = new ContentValues();
    // cv.put(Flashcode.UserXzqh.XZQH, xzqh);
    // cv.put(Flashcode.UserXzqh.XZQHMC, e.getAttribute("MC").trim());
    // cv.put(Flashcode.UserXzqh.PY, e.getAttribute("PY").trim());
    // resolver.insert(url, cv);
    //
    // // 根据行行政区划得到所属道路列表
    // List<KeyValueBean> roadList = getSysRoadsByXzqh(xzqh, resolver);
    // if (roadList != null && roadList.size() > 0) {
    // for (KeyValueBean road : roadList) {
    // ContentValues roadCv = new ContentValues();
    // roadCv.put(Flashcode.UserRoads.XZQH, xzqh);
    // roadCv.put(Flashcode.UserRoads.DLDM, road.getKey());
    // roadCv.put(Flashcode.UserRoads.DLMC, road.getValue());
    // resolver.insert(roadUrl, roadCv);
    // }
    // }
    //
    // // 解析XML得到所有路段的名称的代码
    // NodeList rs = e.getElementsByTagName("R");
    // for (int j = 0; j < rs.getLength(); j++) {
    // Element eleRs = (Element) rs.item(j);
    // ContentValues segCv = new ContentValues();
    // segCv.put(Flashcode.UserRoadSeg.DLDM, eleRs
    // .getAttribute("DL"));
    // segCv.put(Flashcode.UserRoadSeg.XZQH, xzqh);
    // segCv.put(Flashcode.UserRoadSeg.LDDM, eleRs
    // .getAttribute("LD"));
    // segCv.put(Flashcode.UserRoadSeg.LDMC, eleRs
    // .getAttribute("MC"));
    // segCv.put(Flashcode.UserRoadSeg.PY, eleRs
    // .getAttribute("PY"));
    // resolver.insert(segUrl, segCv);
    // }
    //
    // }
    // in.close();
    // } catch (Exception e1) {
    // e1.printStackTrace();
    // }
    //
    // }

    /**
     * 解析XML文件,并加入到数据库中
     *
     * @return
     * @throws Exception
     */
    // public static void pullParseXMLAddDatabase(Context context, String xml,
    // ContentResolver resolver) {
    // long rowid = 0;
    // Uri url = Uri.parse("content://com.android.provider.flashcode/delall");
    // resolver.delete(url, null, null);
    // String xzqh = null;
    // try {
    // InputStream in = context.getAssets().open(xml);
    // XmlPullParser parser = Xml.newPullParser();
    // parser.setInput(in, "utf-8");
    // int event = parser.getEventType();// 产生第一个事件
    // while (event != XmlPullParser.END_DOCUMENT) {
    // switch (event) {
    // case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
    // break;
    // case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
    // // 判断是否为OBJECT元素
    // if (TextUtils.equals("XZQH", parser.getName())) {
    // xzqh = parser.getAttributeValue(null, "BH").trim();
    // addXzqhByParser(parser, resolver);
    // addDldmByXzqh(xzqh, resolver);
    //
    // } else if (TextUtils.equals("R", parser.getName())) {
    // addLddmByParser(parser, resolver, xzqh);
    // }
    // break;
    // case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
    // break;
    // }
    // event = parser.next();// 进入下一个元素并触发相应事件
    // }// end while
    // in.close();
    // } catch (Exception e1) {
    // e1.printStackTrace();
    // }
    //
    // }

    // private static void addXzqhByParser(XmlPullParser parser,
    // ContentResolver resolver) {
    // Uri url = Uri.parse("content://com.android.provider.flashcode/addxzqh");
    // ContentValues cv = new ContentValues();
    // cv.put(Flashcode.UserXzqh.XZQH, parser.getAttributeValue(null, "BH")
    // .trim());
    // cv.put(Flashcode.UserXzqh.XZQHMC, parser.getAttributeValue(null, "MC")
    // .trim());
    // cv.put(Flashcode.UserXzqh.PY, parser.getAttributeValue(null, "PY")
    // .trim());
    // resolver.insert(url, cv);
    // }

    // private static void addLddmByParser(XmlPullParser parser,
    // ContentResolver resolver, String xzqh) {
    // Uri segUrl = Uri
    // .parse("content://com.android.provider.flashcode/addseg");
    // ContentValues segCv = new ContentValues();
    // segCv.put(Flashcode.UserRoadSeg.DLDM, parser.getAttributeValue(null,
    // "DL").trim());
    // segCv.put(Flashcode.UserRoadSeg.XZQH, xzqh);
    // segCv.put(Flashcode.UserRoadSeg.LDDM, parser.getAttributeValue(null,
    // "LD").trim());
    // segCv.put(Flashcode.UserRoadSeg.LDMC, parser.getAttributeValue(null,
    // "MC").trim());
    // segCv.put(Flashcode.UserRoadSeg.PY, parser
    // .getAttributeValue(null, "PY").trim());
    // resolver.insert(segUrl, segCv);
    // }

    // private static void addDldmByXzqh(String xzqh, ContentResolver resolver)
    // {
    // Uri roadUrl = Uri
    // .parse("content://com.android.provider.flashcode/addroad");
    // List<KeyValueBean> roadList = getSysRoadsByXzqh(xzqh, resolver);
    // if (roadList != null && roadList.size() > 0) {
    // for (KeyValueBean road : roadList) {
    // ContentValues roadCv = new ContentValues();
    // roadCv.put(Flashcode.UserRoads.XZQH, xzqh);
    // roadCv.put(Flashcode.UserRoads.DLDM, road.getKey());
    // roadCv.put(Flashcode.UserRoads.DLMC, road.getValue());
    // resolver.insert(roadUrl, roadCv);
    // }
    // }
    // }

    /**
     * 从数据库中加载行政区划
     *
     * @return
     * @throws Exception
     */
    // public static List<KeyValueBean> getOwnerXzqh(ContentResolver resolver) {
    // List<KeyValueBean> kvs = new ArrayList<KeyValueBean>();
    // Uri url = Uri
    // .parse("content://com.android.provider.flashcode/userxzqh");
    // Cursor cs = resolver.query(url, USER_XZQH_PROJECTION, null, null, null);
    // if (cs.moveToFirst()) {
    // do {
    // kvs.add(new KeyValueBean(cs.getString(0), cs.getString(1)));
    // } while (cs.moveToNext());
    // }
    // cs.close();
    // return kvs;
    // }

}
