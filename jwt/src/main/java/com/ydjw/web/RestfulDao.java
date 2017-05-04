package com.ydjw.web;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ntga.bean.AcdPhotoBean;
import com.ntga.bean.AcdSimpleBean;
import com.ntga.bean.AcdSimpleHumanBean;
import com.ntga.bean.ClgjBean;
import com.ntga.bean.GcmBbInfoBean;
import com.ntga.bean.GcmBbddBean;
import com.ntga.bean.JdsUnjkPrintBean;
import com.ntga.bean.JqtbBean;
import com.ntga.bean.KeyValueBean;
import com.ntga.bean.LoginResultBean;
import com.ntga.bean.MjJobBean;
import com.ntga.bean.RepairBean;
import com.ntga.bean.SchoolZtzBean;
import com.ntga.bean.SeriousStreetBean;
import com.ntga.bean.SpringKcdjBean;
import com.ntga.bean.SpringWhpdjBean;
import com.ntga.bean.SysIconBean;
import com.ntga.bean.THmb;
import com.ntga.bean.TTViolation;
import com.ntga.bean.TruckCheckBean;
import com.ntga.bean.TruckDriverBean;
import com.ntga.bean.TruckVehicleBean;
import com.ntga.bean.VioDrvBean;
import com.ntga.bean.VioFxczfBean;
import com.ntga.bean.VioVehBean;
import com.ntga.bean.VioViolation;
import com.ntga.bean.WebQueryResult;
import com.ntga.dao.AcdSimpleDao;
import com.ntga.dao.GlobalConstant;
import com.ntga.dao.GlobalData;
import com.ntga.dao.GlobalMethod;
import com.ntga.login.DownFileThread;
import com.ntga.login.LoginDao;
import com.ntga.login.UpdateFile;
import com.ntga.tools.TypeCenvert;
import com.ntga.tools.ZipUtils;
import com.ntga.xml.CommParserXml;
import com.ntga.zapc.ZapcGzxxBean;
import com.ntga.zapc.ZapcReturn;
import com.ntga.zapc.ZapcRypcxxBean;
import com.ntga.zapc.ZapcWppcxxBean;
import com.ydjw.pojo.CxMenus;
import com.ydjw.pojo.GlobalQueryResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RestfulDao {

    private boolean isDebug = false;

    protected int GET = 0;
    protected int POST = 1;
    private int timeoutConnection = 30000;
    private int timeoutSocket = 30000;
    private String URL_PATH = "/ydjw/services/ydjw/";
    private String FORBID_PATH = "/forbid/services/forbid/";
    private String CROSS_PATH = "/cross/services/cross";
    private String ZHCXURL = URL_PATH + "newQuery";
    private String menuUrl = URL_PATH + "zhcxMenus";
    protected String PIC_URL = "/phototemp/";
    protected String JQTB_FILE_URL = "/ydjw/DownloadJqtbFile";

    private String GZXXUPLOAD = URL_PATH + "zapcTestUpload";
    private String RYXXUPLOAD = URL_PATH + "ryxxTestUpload";
    private String WPXXUPLOAD = URL_PATH + "wpxxTestUpload";

    private String ACDUPLOAD = URL_PATH + "uploadAcd";
    private String ACDBILLHQ = URL_PATH + "billAcdHq";
    private String ACDBILLSJ = URL_PATH + "billAcdSj";
    private String UPLOADREP = URL_PATH + "uploadRepair";
    protected String UPLOAD_REP_PIC = URL_PATH + "uploadRepairPic";
    private String UPLOADVIO = URL_PATH + "uploadVio";
    private String GAIN_VIO_BILL = URL_PATH + "gainVioBill";
    private String BACK_VIO_BILL = URL_PATH + "backVioBill";

    private String SYN_VIO_BILL = URL_PATH + "synVioBill";

    private String SYS_CONFIG = URL_PATH + "getSysConfig";
    private String QUERY_MJ_JOB = URL_PATH + "queryMjJob";
    private String UPLOAD_ACD_RECODE = URL_PATH + "uploadAcdRecode";
    private String UPLOAD_ACD_PHOTO = URL_PATH + "uploadAcdPhoto";

    private String QUERY_VIO_DRV = URL_PATH + "queryVioDrv";
    private String QUERY_VIO_VEH = URL_PATH + "queryVioVeh";
    private String JYCX_QUERY_DRV = URL_PATH + "jycxQueryDrv";
    private String JYCX_QUERY_VEH = URL_PATH + "jycxQueryVeh";


    private String QUERY_JQTB = URL_PATH + "getJqtb";

    private String GAT_ALL_GCM_DD = URL_PATH + "allBbdd";

    private String UPLOAD_GCM_INFO = URL_PATH + "bbinfoUpload";

    private String GET_UN_JK_JDS = URL_PATH + "jdsPrint";

    private String IS_OPEN_UNJK = URL_PATH + "isUnjk";

    private String GPS_UPLOAD = URL_PATH + "uploadGpsInfo";

    private String CHECK_USER = URL_PATH + "checkJwtUser";

    private String CHECK_SIX_SP = URL_PATH + "isSixSp";

    private String QUERY_SIX_SP = URL_PATH + "getAllSixUnsp";

    private String SUBMIT_SIX_SP = URL_PATH + "sixSp";

    private String LOGOUT_JWT = URL_PATH + "logoutJwtUser";

    private String GAT_ALL_ICON = URL_PATH + "getSysIconList";

    private String QUEYR_VIO_RKQK = URL_PATH + "queryVioRkqk";

    private String IS_DOWNLOAD_ICON = URL_PATH + "isDownloadIcon";

    private String GET_SYSTEM_TIME = URL_PATH + "getSystemTime";

    private String IS_DUP_VIO = URL_PATH + "isDupVio";

    private String UPLOAD_SPRING_KCDJ = URL_PATH + "uploadSpingKcdj";

    private String UPLOAD_SPRING_WHPDJ = URL_PATH + "uploadSpingWhpdj";

    private String QUERY_TRUCK_CHECK = URL_PATH + "queryTruckCheck";
    private String UPLOAD_TRUCK_VEH = URL_PATH + "uploadTruckVeh";
    private String UPLOAD_TRUCK_DRV = URL_PATH + "uploadTruckDrv";

    private String QUERY_ALL_QYMC = URL_PATH + "queryAllQymc";

    private String GET_ALL_WFXW_CLLX = URL_PATH + "allWfxwCllx";

    private final String UPLOAD_FXC_PHOTO_CHECK = URL_PATH + "uploadFxcPhotoCheck";

    private final String UPLOAD_FXC_PHOTO = URL_PATH + "uploadFxcPhoto";

    private final String UPLOAD_FXC_JL = URL_PATH + "uploadFxcJl";

    private final String UPLOAD_FXC_PHOTO_MD5 = URL_PATH + "uploadFxcPhotoMd5";

    private final String UPLOAD_FXC_JL_IMG = URL_PATH + "uploadFxcJlImg";

    private final String CHECK_USER_MD5 = URL_PATH + "checkUserMd5";

    private final String TEST_NETWORK = URL_PATH + "testNetwork";

    private final String QUERY_FXC_RKQK = URL_PATH + "queryFxcRkqk";

    private final String ZHBD = URL_PATH + "zhbd";

    private final String QUERY_CLGJ = URL_PATH + "queryClgj";

    private final String QUERY_SCHOOL = URL_PATH + "querySchoolZtz";

    //程序文件更新
    private final String UPDATE_FILE = URL_PATH + "updateFileVersion";

    private final String SERIOUS_STREET = URL_PATH + "getSeriousStreet";

    private final String CHECK_FXC_ALL_OK = URL_PATH + "checkFxcAllOk";

    private final String CHECK_FXC_ZQMJ_ALL_UPLOAD = URL_PATH + "checkFxcZqmjAllUpload";

    private final String CHECK_FXC_TZSH = URL_PATH + "checkFxcTzsh";

    private String checkUrl = "/ydjw/services/login/checkJwtUser";
    private String checkMd5 = "/ydjw/services/ydjw/checkJwtUserNew";

    //---------------------------禁区通行证常量-------------------------------
    private final String QUERY_FROBID = FORBID_PATH + "queryPassInfo";


    /**
     * 获取严管违停信息
     *
     * @param version
     * @return 实体为空或返回码不为200，不更新
     */
    public WebQueryResult<List<SeriousStreetBean>> getSeriousStreet(String version) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("version", version));
        return GlobalMethod.webXmlStrToListObj(
                restfulQuery(getUrl() + SERIOUS_STREET, postParams, POST),
                SeriousStreetBean.class);
    }


    /**
     * 系统文件更新信息，用于和本地文件进行比对
     *
     * @return
     */
    public WebQueryResult<List<UpdateFile>> updateInfoRestful() {

        WebQueryResult<String> re = restfulQuery(getUrl() + UPDATE_FILE, null, GET);
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        WebQueryResult<List<UpdateFile>> res = null;
        if (TextUtils.isEmpty(err)) {
            res = GlobalMethod.webXmlStrToListObj(re, UpdateFile.class);
        }
        return res;
    }


    /**
     * 测试网络连接
     *
     * @return true 可连接 false 不可以连接
     */
    public boolean testNetwork() {
        WebQueryResult<String> re = restfulQuery(getUrl() + TEST_NETWORK, null, GET);
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (TextUtils.isEmpty(err)) {
            return re != null && re.getResult() != null && "OK".equals(re.getResult());
        }
        return false;
    }

    /**
     * 全局综合查询
     *
     * @param cxid
     * @param conds
     * @return
     */
    public WebQueryResult<GlobalQueryResult> zhcxRestful(String cxid,
                                                         String conds) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("cxid", cxid));
        postParams.add(new BasicNameValuePair("conds", conds));
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        postParams.add(new BasicNameValuePair("jybh", jh));
        postParams.add(new BasicNameValuePair("meid", GlobalData.serialNumber));
        return GlobalMethod.webXmlStrToObj(
                restfulQuery(getUrl() + ZHCXURL, postParams, POST),
                GlobalQueryResult.class);
    }

    /**
     * 获取系统菜单
     *
     * @return
     */
    public WebQueryResult<String> getSystemMenu() {
        return restfulQuery(getUrl() + menuUrl, null, GET);
    }

    public WebQueryResult<List<CxMenus>> restfulGetMenus() {
        // WebQueryResult<List<CxMenus>> res = new
        // WebQueryResult<List<CxMenus>>();
        WebQueryResult<String> ws = getSystemMenu();
        return GlobalMethod.webXmlStrToListObj(ws, CxMenus.class);
        // restfulQuery(menuUrl, null, GET);
        // restfulGetString(menuUrl);
        // res.setStatus(ws.getStatus());
        // if (!TextUtils.isEmpty(ws.getResult())) {
        // List<CxMenus> list;
        // try {
        // list = CommParserXml.ParseXmlToListObj(ws.getResult(),
        // CxMenus.class);
        // res.setResult(list);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // return res;
    }

    /**
     * 获取系统变量参数配置
     *
     * @return
     */
    public WebQueryResult<List<KeyValueBean>> restfulGetSysConfig() {
        WebQueryResult<String> ws = restfulQuery(getUrl() + SYS_CONFIG, null,
                GET);
        return GlobalMethod.webXmlStrToListObj(ws, KeyValueBean.class);
    }

    /**
     * 上传工作信息
     *
     * @param gzxx
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadZapcGzxx(ZapcGzxxBean gzxx) {
        WebQueryResult<ZapcReturn> re = null;
        try {
            String xml = CommParserXml.objToXml(gzxx);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("gzxx", xml));
            re = GlobalMethod
                    .webXmlStrToObj(
                            restfulQuery(getUrl() + GZXXUPLOAD, postParams,
                                    POST, true), ZapcReturn.class
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传人员盘查信息
     *
     * @param ryxx
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadZapcRypcxx(ZapcRypcxxBean ryxx,
                                                       String gzid, String kssj) {
        WebQueryResult<ZapcReturn> re = null;
        try {
            String xml = CommParserXml.objToXml(ryxx);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("ryxx", xml));
            postParams.add(new BasicNameValuePair("dwdm", GlobalData.grxx
                    .get(GlobalConstant.YBMBH)));
            String jh = GlobalData.grxx.get(GlobalConstant.JH);
            postParams.add(new BasicNameValuePair("jybh", jh.length() == 8 ? jh
                    .substring(2) : jh));
            postParams.add(new BasicNameValuePair("gzid", gzid));
            postParams.add(new BasicNameValuePair("kssj", kssj));
            re = GlobalMethod
                    .webXmlStrToObj(
                            restfulQuery(getUrl() + RYXXUPLOAD, postParams,
                                    POST, true), ZapcReturn.class
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传物品信息
     *
     * @param wpxx 物品信息
     * @param gzid 工作号
     * @param kssj 开始时间
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadZapcWpxx(ZapcWppcxxBean wpxx,
                                                     String gzid, String kssj) {
        WebQueryResult<ZapcReturn> re = null;
        try {
            String xml = CommParserXml.objToXml(wpxx);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("wpxx", xml));
            postParams.add(new BasicNameValuePair("dwdm", GlobalData.grxx
                    .get(GlobalConstant.YBMBH)));
            String jh = GlobalData.grxx.get(GlobalConstant.JH);
            postParams.add(new BasicNameValuePair("jybh", jh.length() == 8 ? jh
                    .substring(2) : jh));
            postParams.add(new BasicNameValuePair("gzid", gzid));
            postParams.add(new BasicNameValuePair("kssj", kssj));
            re = GlobalMethod
                    .webXmlStrToObj(
                            restfulQuery(getUrl() + WPXXUPLOAD, postParams,
                                    POST, true), ZapcReturn.class
                    );
            // (uploadAction(postParams, WPXXUPLOAD),
            // ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 获取联系人列表
     *
     * @param jybh
     * @return
     */
    // public WebQueryResult<List<UdpMessageUser>> queryAllLxr(String jybh) {
    // WebQueryResult<List<UdpMessageUser>> res = new
    // WebQueryResult<List<UdpMessageUser>>();
    // int status = HttpStatus.SC_BAD_REQUEST;
    // BasicHttpParams httpParameters = new BasicHttpParams();
    // HttpConnectionParams.setConnectionTimeout(httpParameters,
    // timeoutConnection);
    // HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
    // HttpClient httpclient = new DefaultHttpClient(httpParameters);
    // HttpGet request = new HttpGet();
    // try {
    // if (!TextUtils.isEmpty(jybh))
    // request.setURI(new URI(udpMesUser + "?jybh=" + jybh));
    // else
    // request.setURI(new URI(udpMesUser));
    // HttpParams params = new BasicHttpParams();
    // params.setParameter("jybh", jybh);
    // request.setParams(params);
    // HttpResponse response = httpclient.execute(request);
    // status = response.getStatusLine().getStatusCode();
    // Log.e("queryAllLxr", "return code is " + status);
    // // 返回数据正常
    // if (status == HttpStatus.SC_OK) {
    // HttpEntity entity = response.getEntity();
    // if (entity != null) {
    // BufferedReader reader = new BufferedReader(
    // new InputStreamReader(entity.getContent(), "utf-8"));
    // String s = null;
    // String result = "";
    // while ((s = reader.readLine()) != null) {
    // result += s;
    // }
    // Log.e("queryAllLxr", result);
    // if (!TextUtils.isEmpty(result)) {
    // List<UdpMessageUser> g = CommParserXml
    // .ParseXmlToListObj(result, UdpMessageUser.class);
    // res.setResult(g);
    // }
    // }
    // }
    // } catch (ClientProtocolException e1) {
    // Log.e("exception", "ClientProtocolException");
    // e1.printStackTrace();
    // } catch (IOException e1) {
    // Log.e("exception", "IOException");
    // e1.printStackTrace();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // httpclient.getConnectionManager().shutdown();
    // res.setStatus(status);
    // return res;
    // }

    /**
     * 上传交通事故
     *
     * @param acd
     * @param humans
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadAcdInfo(AcdSimpleBean acd,
                                                    ArrayList<AcdSimpleHumanBean> humans) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        try {
            String xml = CommParserXml.objToXml(acd);
            String ryxx = "";
            for (AcdSimpleHumanBean human : humans) {
                ryxx += AcdSimpleDao.createRyxxStr(human);
            }
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("acd", xml));
            postParams.add(new BasicNameValuePair("ryxx", ryxx));
            postParams.add(new BasicNameValuePair("dwdm", GlobalData.grxx
                    .get(GlobalConstant.BMBH)));
            String jh = GlobalData.grxx.get(GlobalConstant.JH);
            postParams.add(new BasicNameValuePair("jybh", jh.length() == 8 ? jh
                    .substring(2) : jh));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + ACDUPLOAD, postParams, POST, true),
                    ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return re;
    }

    /**
     * 获取简易事故文书编号
     *
     * @param jybh
     * @param glbm
     * @param hdzl
     * @return
     */
    public WebQueryResult<THmb> hqAcdWs(String jybh, String glbm, String hdzl) {
        // WebQueryResult<THmb> res = new WebQueryResult<THmb>();
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("jybh", jybh));
        postParams.add(new BasicNameValuePair("glbm", glbm));
        postParams.add(new BasicNameValuePair("hdzl", hdzl));
        WebQueryResult<String> rs = restfulQuery(getUrl() + ACDBILLHQ,
                postParams, POST);
        return GlobalMethod.webXmlStrToObj(rs, THmb.class);
        // restfulPostQuery(postParams, ACDBILLHQ);
        // try {
        // if (!TextUtils.isEmpty(rs.getResult())) {
        // THmb g = CommParserXml
        // .parseXmlToObj(rs.getResult(), THmb.class);
        // res.setResult(g);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // res.setStatus(rs.getStatus());
        // return res;
    }

    /**
     * 简易事故程序文书上交
     *
     * @param hdids
     * @param wsbhs
     * @return
     */
    public WebQueryResult<String> sjAcdWs(String hdids, String wsbhs) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("hdids", hdids));
        postParams.add(new BasicNameValuePair("wsbhs", wsbhs));
        WebQueryResult<String> rs = restfulQuery(getUrl() + ACDBILLSJ,
                postParams, POST);
        // restfulPostQuery(postParams, ACDBILLSJ);
        return rs;
    }

    /**
     * 上传报修文字信息
     *
     * @param rep
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadRepair(RepairBean rep) {
        try {
            String xml = CommParserXml.objToXml(rep);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("rep", xml));
            postParams.add(new BasicNameValuePair("dwdm", GlobalData.grxx.get(
                    GlobalConstant.YBMBH).substring(0, 6)));
            String jh = GlobalData.grxx.get(GlobalConstant.JH);
            postParams.add(new BasicNameValuePair("jybh", jh.length() == 8 ? jh
                    .substring(2) : jh));
            WebQueryResult<String> r = restfulQuery(getUrl() + UPLOADREP,
                    postParams, POST, true);
            // uploadAction(postParams, UPLOADREP);
            return GlobalMethod.webXmlStrToObj(r, ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传简易处罚
     *
     * @param vio
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadViolation(VioViolation vio) {
        WebQueryResult<ZapcReturn> re = null;// new
        // WebQueryResult<ZapcReturn>();
        try {
            String xml = CommParserXml.objToXml(vio);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("vio", xml));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + UPLOADVIO, postParams, POST, true),
                    ZapcReturn.class);
            // webXmlStrToObj(uploadAction(postParams, UPLOADVIO),
            // ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 新的获取法律文书方法
     *
     * @param yhbh
     * @param wslx
     * @return
     */
    public WebQueryResult<List<THmb>> hqVioWs(String yhbh, String wslx) {
        WebQueryResult<List<THmb>> hmbs = new WebQueryResult<List<THmb>>();
        hmbs.setStatus(HttpStatus.SC_BAD_REQUEST);
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("yhbh", yhbh));
        postParams.add(new BasicNameValuePair("wslx", wslx));
        String uuu = getUrl() + GAIN_VIO_BILL;
        Log.e(getClassName(), uuu);
        WebQueryResult<String> re = restfulQuery(uuu, postParams, POST);
        return GlobalMethod.webXmlStrToListObj(re, THmb.class);
    }

    /**
     * 大于服务器的文件，同步文书
     *
     * @param hmb
     * @param jybh
     * @return
     */
    public WebQueryResult<ZapcReturn> synVioWs(THmb hmb, String jybh) {
        WebQueryResult<ZapcReturn> re = null;// new
        // WebQueryResult<ZapcReturn>();
        try {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("hdid", hmb.getHdid()));
            postParams.add(new BasicNameValuePair("dqz", hmb.getDqhm()));
            postParams.add(new BasicNameValuePair("yhbh", jybh));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + SYN_VIO_BILL, postParams, POST,
                            true),
                    // uploadAction(postParams, BACK_VIO_BILL),
                    ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 新的文书上交方法
     *
     * @param hmb
     * @param jybh
     * @return
     */
    public WebQueryResult<ZapcReturn> backVioWs(THmb hmb, String jybh) {
        WebQueryResult<ZapcReturn> re = null;// new
        // WebQueryResult<ZapcReturn>();
        try {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("hdid", hmb.getHdid()));
            postParams.add(new BasicNameValuePair("dqz", hmb.getDqhm()));
            postParams.add(new BasicNameValuePair("yhbh", jybh));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + BACK_VIO_BILL, postParams, POST,
                            true),
                    // uploadAction(postParams, BACK_VIO_BILL),
                    ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 查询民警工作量的方法
     *
     * @param jybh
     * @param stime
     * @param etime
     * @return
     */
    public WebQueryResult<MjJobBean> queryMjJob(String jybh, String stime,
                                                String etime) {
        // WebQueryResult<MjJobBean> re = new WebQueryResult<MjJobBean>();
        // try {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("jybh", jybh.length() == 8 ? jybh
                .substring(2) : jybh));
        postParams.add(new BasicNameValuePair("stime", stime));
        postParams.add(new BasicNameValuePair("etime", etime));
        return GlobalMethod.webXmlStrToObj(
                restfulQuery(getUrl() + QUERY_MJ_JOB, postParams, POST),
                MjJobBean.class);

        // WebQueryResult<String> sre = restfulPostQuery(postParams,
        // QUERY_MJ_JOB);
        // re.setStatus(sre.getStatus());
        // if (!TextUtils.isEmpty(sre.getResult())) {
        // MjJobBean g = CommParserXml.parseXmlToObj(sre.getResult(),
        // MjJobBean.class);
        // re.setResult(g);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // return re;
    }

    /**
     * 上传事故文本信息，由返回的数据写记录号
     *
     * @param acd
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadAcdRecode(AcdPhotoBean acd) {
        // WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        // re.setStatus(HttpStatus.SC_BAD_REQUEST);
        // try {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        String jh = GlobalData.grxx.get(GlobalConstant.YHBH);
        postParams.add(new BasicNameValuePair("zqmj", jh.length() == 8 ? jh
                .substring(2) : jh));
        postParams.add(new BasicNameValuePair("sgbh", acd.getSgbh()));
        postParams.add(new BasicNameValuePair("sgsj", acd.getSgsj()));
        postParams.add(new BasicNameValuePair("sgdd", acd.getSgdd()));
        return GlobalMethod.webXmlStrToObj(
                restfulQuery(getUrl() + UPLOAD_ACD_RECODE, postParams, POST,
                        true), ZapcReturn.class
        );
        // WebQueryResult<String> r = uploadAction(postParams,
        // UPLOAD_ACD_RECODE);
        // if (r != null)
        // re = webXmlStrToObj(r, ZapcReturn.class);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // return re;
    }

    /**
     * 上传GPS信息
     *
     * @param b
     * @return
     */
    public WebQueryResult<String> uploadGpsInfo(byte[] b) {
        String jwtUpUrl = getUrl() + GPS_UPLOAD;
        WebQueryResult<String> sre = uploadByte(jwtUpUrl, b, null, 0, null);
        return sre;
    }

    /**
     * 上传事故照片
     *
     * @param file
     * @param xtbh
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadAcdPhoto(File file, long xtbh) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        re.setStatus(HttpStatus.SC_BAD_REQUEST);
        long fileLen = file.length();
        String jwtUpUrl = getUrl() + UPLOAD_ACD_PHOTO;
        byte[] data = TypeCenvert.long2Byte(xtbh);
        try {
            FileInputStream in = new FileInputStream(file);
            WebQueryResult<String> sre = uploadByte(jwtUpUrl, data, in,
                    fileLen, null);
            in.close();
            if (sre.getStatus() == HttpStatus.SC_OK
                    && !TextUtils.isEmpty(sre.getResult())) {
                ZapcReturn g = CommParserXml.parseXmlToObj(sre.getResult(),
                        ZapcReturn.class);
                re.setResult(g);
                re.setStatus(HttpStatus.SC_OK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public String uploadFxcPhotoAndMd5(File file, String md5) {
        String result = "";
        if (!file.exists())
            return "";
        String jwtUpUrl = getUrl() + UPLOAD_FXC_PHOTO_MD5 + "?md5=" + md5.toLowerCase();
        try {
            URL nurl = new URL(jwtUpUrl);
            URLConnection conn = nurl.openConnection();
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.flush();
            out.close();
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                result += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("uploadFxcPhotoAndMd5", "图片数据返回：" + result);
        return result;
    }

    public String uploadFxczfJlAndImage(VioFxczfBean fxc, List<String> imageInfo) {
        String ph = "";
        for (String s : imageInfo)
            ph += s + ",";
        ph = ph.substring(0, ph.length() - 1);
        fxc.setCwms(ph);
        try {
            String xml = CommParserXml.objToXml(fxc);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("fxcjl", xml));
            String re = restfulJsonQuery(getUrl() + UPLOAD_FXC_JL_IMG,
                    postParams, POST);
            Log.e("uploadFxczfJlAndImage", re);
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkFxcTzsh(String tzsh) {
        String url = getUrl() + CHECK_FXC_TZSH + "?tzsh=" + tzsh;
        String json = restfulJsonQuery(url, null, GET);
        try {
            JSONObject obj = new JSONObject(json);
            if (TextUtils.equals(obj.optString("re"), "1"))
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public WebQueryResult<ZapcReturn> uploadFxczfJl(VioFxczfBean fxc) {
        try {
            String xml = CommParserXml.objToXml(fxc);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("fxcjl", xml));
            WebQueryResult<String> r = restfulQuery(getUrl() + UPLOAD_FXC_JL,
                    postParams, POST, true);
            // uploadAction(postParams, UPLOADREP);
            return GlobalMethod.webXmlStrToObj(r, ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String uploadFxcPhoto(File file, Long xtbh) {
        String result = "";
        if (!file.exists())
            return "";
        String md5 = ZipUtils.getFileMd5(file).toLowerCase();
        String jwtUpUrl = getUrl() + UPLOAD_FXC_PHOTO + "?fxcId=" + xtbh + "&md5=" + md5;
        try {
            URL nurl = new URL(jwtUpUrl);
            URLConnection conn = nurl.openConnection();
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.flush();
            out.close();
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                result += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public WebQueryResult<ZapcReturn> uploadFxcPhotoOld(File file, Long xtbh) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        re.setStatus(HttpStatus.SC_BAD_REQUEST);
        long fileLen = file.length();
        String jwtUpUrl = getUrl() + UPLOAD_FXC_PHOTO_CHECK;
        byte[] data = TypeCenvert.long2Byte(xtbh);
        data = TypeCenvert.addByte(data, TypeCenvert.long2Byte(fileLen));
        try {
            FileInputStream in = new FileInputStream(file);
            WebQueryResult<String> sre = uploadByte(jwtUpUrl, data, in,
                    fileLen, null);
            in.close();
            if (sre.getStatus() == HttpStatus.SC_OK
                    && !TextUtils.isEmpty(sre.getResult())) {
                ZapcReturn g = CommParserXml.parseXmlToObj(sre.getResult(),
                        ZapcReturn.class);
                re.setResult(g);
                re.setStatus(HttpStatus.SC_OK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }


    /**
     * 简易处罚中专用的查询驾驶员简项信息
     *
     * @param dabh    档案编号
     * @param islocal 是否为本地，无用的参数
     * @return
     */
    public WebQueryResult<VioDrvBean> queryVioDrv(String dabh, String islocal) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("dabh", dabh));
        postParams.add(new BasicNameValuePair("islocal", islocal));
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        postParams.add(new BasicNameValuePair("jybh", jh));
        postParams.add(new BasicNameValuePair("meid", GlobalData.serialNumber));
        return GlobalMethod.webXmlStrToObj(
                restfulQuery(getUrl() + QUERY_VIO_DRV, postParams, POST),
                VioDrvBean.class);
    }

    /**
     * 简易处罚中专用的查询驾驶员信息，包括比对信息
     *
     * @param dabh 档案编号
     * @param sfzh 身份证号
     * @return
     */
    public String jycxQueryDrv(String dabh, String sfzh) {
        String url = getUrl() + JYCX_QUERY_DRV + "?dabh=" + dabh + "&sfzh=" + sfzh;
        String json = restfulJsonQuery(url, null, GET);
        return json;
    }

    /**
     * 简易程序查询机动车信息包括比对信息
     *
     * @param hpzl
     * @param hphm
     * @return
     */
    public String jycxQueryVeh(String hpzl, String hphm) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("hpzl", hpzl));
        postParams.add(new BasicNameValuePair("hphm", hphm));
        return restfulJsonQuery(getUrl() + JYCX_QUERY_VEH, postParams, POST);
    }

    /**
     * 简易处罚中用查询机动车简易项信息
     *
     * @param hpzl
     * @param hphm
     * @return
     */
    public WebQueryResult<VioVehBean> queryVioVeh(String hpzl, String hphm) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("hpzl", hpzl));
        postParams.add(new BasicNameValuePair("hphm", hphm));
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        postParams.add(new BasicNameValuePair("jybh", jh));
        postParams.add(new BasicNameValuePair("meid", GlobalData.serialNumber));
        return GlobalMethod.webXmlStrToObj(
                restfulQuery(getUrl() + QUERY_VIO_VEH, postParams, POST),
                VioVehBean.class);
    }

    /**
     * 查询布控图片信息
     *
     * @param jybh
     * @return
     */
    public WebQueryResult<List<JqtbBean>> queryBkPicMessage(String jybh,
                                                            String force) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("jybh", jybh));
        postParams.add(new BasicNameValuePair("force", force));
        WebQueryResult<String> re = restfulQuery(getUrl() + QUERY_JQTB,
                postParams, POST);
        WebQueryResult<List<JqtbBean>> reza = new WebQueryResult<List<JqtbBean>>();
        reza.setStatus(re.getStatus());
        try {
            if (!TextUtils.isEmpty(re.getResult())) {
                List<JqtbBean> g = CommParserXml.ParseXmlToListObj(
                        re.getResult(), JqtbBean.class);
                reza.setResult(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reza;
    }

    /**
     * 获取所有关城门地点列表
     *
     * @return
     */
    public WebQueryResult<List<GcmBbddBean>> getAllGcmDd() {
        WebQueryResult<String> re = restfulQuery(getUrl() + GAT_ALL_GCM_DD,
                null, GET);
        return GlobalMethod.webXmlStrToListObj(re, GcmBbddBean.class);
    }

    /**
     * 上传关城门信息
     *
     * @param info
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadGcmBb(GcmBbInfoBean info) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        try {
            String xml = CommParserXml.objToXml(info);
            postParams.add(new BasicNameValuePair("bbInfo", xml));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GlobalMethod.webXmlStrToObj(
                restfulQuery(getUrl() + UPLOAD_GCM_INFO, postParams, POST),
                ZapcReturn.class);
    }

    /**
     * 获取所有图标文件列表，有修改，暂时不用这个方法了
     *
     * @return
     */
    public WebQueryResult<List<SysIconBean>> getAllIconList() {
        WebQueryResult<String> re = restfulQuery(getUrl() + GAT_ALL_ICON, null,
                GET);
        return GlobalMethod.webXmlStrToListObj(re, SysIconBean.class);
    }

    /**
     * 是否需要下载图标文件
     *
     * @param md5
     * @return
     */
    public WebQueryResult<ZapcReturn> isDownloadIcon(String md5) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("md5", md5));
        WebQueryResult<String> re = restfulQuery(getUrl() + IS_DOWNLOAD_ICON,
                postParams, POST);
        return GlobalMethod.webXmlStrToObj(re, ZapcReturn.class);

    }

    /**
     * 获取未缴款的决定书信息
     *
     * @param sfzh
     * @return
     */
    public WebQueryResult<List<JdsUnjkPrintBean>> getUnJkJds(String sfzh,
                                                             String hpzl, String hphm) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("sfzh", sfzh));
        postParams.add(new BasicNameValuePair("hpzl", hpzl));
        postParams.add(new BasicNameValuePair("hphm", hphm));
        WebQueryResult<String> re = restfulQuery(getUrl() + GET_UN_JK_JDS,
                postParams, POST);
        return GlobalMethod.webXmlStrToListObj(re, JdsUnjkPrintBean.class);
    }

    /**
     * 是否可以打开补打印决定书模块
     *
     * @param jybh
     * @return
     */
    public WebQueryResult<String> isOpenUnjk(String jybh) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("jybh", jybh));
        WebQueryResult<String> re = restfulQuery(getUrl() + IS_OPEN_UNJK,
                postParams, POST);
        return re;
    }

    public WebQueryResult<LoginResultBean> checkUserAndUpdate(String yhbh,
                                                              String mm, String sbid) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("yhbh", yhbh));
        postParams.add(new BasicNameValuePair("sbid", sbid));
        postParams.add(new BasicNameValuePair("mm", mm));
        String url = getUrl() + CHECK_USER;
        // Log.e("checkUserAndUpdate url", url);
        WebQueryResult<String> re = restfulQuery(url, postParams, POST, false);
        return GlobalMethod.webXmlStrToObj(re, LoginResultBean.class);
    }

    /**
     * 上传报修的图片
     *
     * @param xtbh
     * @param path
     * @param handler
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadRepPic(long xtbh, String path,
                                                   Handler handler) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        re.setStatus(HttpStatus.SC_BAD_REQUEST);
        File f = new File(path);
        long fileLen = f.length();
        String jwtUpUrl = getUrl() + UPLOAD_REP_PIC;
        byte[] data = TypeCenvert.long2Byte(xtbh);
        try {
            FileInputStream in = new FileInputStream(f);
            WebQueryResult<String> sre = uploadByte(jwtUpUrl, data, in,
                    fileLen, handler);
            in.close();
            if (sre.getStatus() == HttpStatus.SC_OK
                    && !TextUtils.isEmpty(sre.getResult())) {
                ZapcReturn g = CommParserXml.parseXmlToObj(sre.getResult(),
                        ZapcReturn.class);
                re.setResult(g);
                re.setStatus(HttpStatus.SC_OK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 验证六分审批权限
     *
     * @param jybh
     * @return
     */
    public WebQueryResult<ZapcReturn> checkSixSp(String jybh) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("jybh", jybh));
        String url = getUrl() + CHECK_SIX_SP;
        // Log.e("checkUserAndUpdate url", url);
        WebQueryResult<String> re = restfulQuery(url, postParams, POST, false);
        return GlobalMethod.webXmlStrToObj(re, ZapcReturn.class);
    }

    /**
     * 六分审批主方法
     *
     * @param jdsbh
     * @param sp
     * @param spnr
     * @param jybh
     * @return
     */
    public WebQueryResult<ZapcReturn> submitSixSp(String jdsbh, String sp,
                                                  String spnr, String jybh) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("jdsbh", jdsbh));
        postParams.add(new BasicNameValuePair("sp", sp));
        postParams.add(new BasicNameValuePair("spnr", spnr));
        postParams.add(new BasicNameValuePair("jybh", jybh));
        String url = getUrl() + SUBMIT_SIX_SP;
        // Log.e("checkUserAndUpdate url", url);
        WebQueryResult<String> re = restfulQuery(url, postParams, POST, false);
        return GlobalMethod.webXmlStrToObj(re, ZapcReturn.class);

    }

    /**
     * 获取该单位的审批列表
     *
     * @param dwdm
     * @param sp   审批状态 9为未审批，8为审批否定，5为审批合格
     * @return
     */
    public WebQueryResult<List<TTViolation>> querySixSpList(String dwdm,
                                                            String sp) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("dwdm", dwdm));
        postParams.add(new BasicNameValuePair("sp", sp));
        WebQueryResult<String> re = restfulQuery(getUrl() + QUERY_SIX_SP,
                postParams, POST);
        return GlobalMethod.webXmlStrToListObj(re, TTViolation.class);
    }

    /**
     * 警务通用户登出
     *
     * @param yhbh
     * @param sbid
     * @return
     */
    public WebQueryResult<String> logoutJwt(String yhbh, String sbid) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("yhbh", yhbh));
        postParams.add(new BasicNameValuePair("sbid", sbid));
        WebQueryResult<String> re = restfulQuery(getUrl() + LOGOUT_JWT,
                postParams, POST);
        return re;
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    public Date getSystemTime() {
        WebQueryResult<String> re = restfulQuery(getUrl() + GET_SYSTEM_TIME,
                null, GET);
        if (re.getStatus() == HttpStatus.SC_OK
                && !TextUtils.isEmpty(re.getResult())) {
            Long l = Long.valueOf(re.getResult());
            Date d = new Date(l);
            return d;
        }
        return null;
    }

    /**
     * 检查决定书在服务器端是否存在重复
     *
     * @param wslb
     * @param jdsbh
     * @return
     */
    public WebQueryResult<String> isDupVio(String wslb, String jdsbh) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("wslb", wslb));
        postParams.add(new BasicNameValuePair("jdsbh", jdsbh));
        WebQueryResult<String> re = restfulQuery(getUrl() + IS_DUP_VIO,
                postParams, POST);
        return re;
    }

    /**
     * 查询决定书入库情况
     *
     * @param jdsbh
     * @return
     */
    public WebQueryResult<ZapcReturn> queryVioRkqk(String jdsbh, String wslb) {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("jdsbh", jdsbh));
        postParams.add(new BasicNameValuePair("wslb", wslb));
        String url = getUrl() + QUEYR_VIO_RKQK;
        // Log.e("checkUserAndUpdate url", url);
        WebQueryResult<String> re = restfulQuery(url, postParams, POST, false);
        return GlobalMethod.webXmlStrToObj(re, ZapcReturn.class);
    }

    /**
     * 上传客车登记
     *
     * @param kcdj
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadSpringKcdj(SpringKcdjBean kcdj) {
        WebQueryResult<ZapcReturn> re = null;// new
        try {
            String xml = CommParserXml.objToXml(kcdj);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("kcdj", xml));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + UPLOAD_SPRING_KCDJ, postParams,
                            POST, true), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传危化品登记
     *
     * @param whpdj
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadSpringWhpdj(SpringWhpdjBean whpdj) {
        WebQueryResult<ZapcReturn> re = null;// new
        try {
            String xml = CommParserXml.objToXml(whpdj);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("whpdj", xml));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + UPLOAD_SPRING_WHPDJ, postParams,
                            POST, true), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 查询重型车登记情况
     *
     * @param hpzl
     * @param hphm
     * @return
     */
    public WebQueryResult<TruckCheckBean> queryTruckCheck(String hpzl,
                                                          String hphm) {
        WebQueryResult<TruckCheckBean> re = null;
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("hpzl", hpzl));
        postParams.add(new BasicNameValuePair("hphm", hphm));
        re = GlobalMethod.webXmlStrToObj(
                restfulQuery(getUrl() + QUERY_TRUCK_CHECK, postParams, POST),
                TruckCheckBean.class);
        return re;
    }

    /**
     * 上传重型车登记
     *
     * @param truck
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadTruckVeh(TruckVehicleBean truck) {
        WebQueryResult<ZapcReturn> re = null;// new
        try {
            String xml = CommParserXml.objToXml(truck);
            Log.e("uploadTruckVeh", xml);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("truck", xml));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + UPLOAD_TRUCK_VEH, postParams, POST,
                            true), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传重型车驾驶员信息
     *
     * @param drv
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadTruckDrv(TruckDriverBean drv) {
        WebQueryResult<ZapcReturn> re = null;// new
        try {
            String xml = CommParserXml.objToXml(drv);
            Log.e("uploadTruckDrv", xml);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("drv", xml));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + UPLOAD_TRUCK_DRV, postParams, POST,
                            true), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 获取所有车辆运输单位名称
     *
     * @return
     */
    public WebQueryResult<List<KeyValueBean>> queryAllQymc() {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        WebQueryResult<String> re = restfulQuery(getUrl() + QUERY_ALL_QYMC,
                postParams, POST);
        return GlobalMethod.webXmlStrToListObj(re, KeyValueBean.class);
    }

    public WebQueryResult<KeyValueBean> getBjbdBySfzh(String sfzh) {
        WebQueryResult<String> re = restfulQuery(getUrl() + ZHBD + "?sfzh=" + sfzh.toUpperCase(),
                null, GET);
        WebQueryResult<KeyValueBean> kv = new WebQueryResult<KeyValueBean>();
        kv.setStatus(re.getStatus());
        try {
            JSONObject obj = new JSONObject(re.getResult());
            KeyValueBean k = new KeyValueBean();
            k.setKey(obj.getString("key"));
            k.setValue(obj.getString("value"));
            kv.setResult(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kv;
    }

    /**
     * 获取所有违法行为和车辆类型的逻辑关系
     *
     * @return
     */
    public WebQueryResult<String> getAllWfxwCllxCheck() {
        return restfulQuery(getUrl() + GET_ALL_WFXW_CLLX, null, GET);
    }

    /**
     * 验证用户的各种MD5值
     *
     * @param jybh    八位警号
     * @param meid    手机串号
     * @param md5
     * @param catalog 分类
     * @return
     */
    public WebQueryResult<String> checkUserMd5(String jybh, String meid,
                                               String md5, String catalog) {
        WebQueryResult<String> re = null;// new
        try {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("jybh", jybh));
            postParams.add(new BasicNameValuePair("meid", meid));
            postParams.add(new BasicNameValuePair("md5", md5));
            postParams.add(new BasicNameValuePair("catalog", catalog));
            re = restfulQuery(getUrl() + CHECK_USER_MD5, postParams, POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 查询非现场入库情况
     *
     * @param xtbh
     * @return
     */
    public WebQueryResult<String> queryFxcRkqk(String xtbh) {
        WebQueryResult<String> re = null;
        try {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("xtbh", xtbh));
            re = restfulQuery(getUrl() + QUERY_FXC_RKQK, postParams, POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public WebQueryResult<ZapcReturn> checkFxcIsUpOK(String xtbh) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        try {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("xtbh", xtbh));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + CHECK_FXC_ALL_OK, postParams, POST,
                            true), ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 验证该民警的非现场是否均已上传完整
     *
     * @param zqmj
     * @return
     */
    public WebQueryResult<ZapcReturn> checkFxcZqmjAllUpload(String zqmj) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        try {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("zqmj", zqmj));
            re = GlobalMethod.webXmlStrToObj(
                    restfulQuery(getUrl() + CHECK_FXC_ZQMJ_ALL_UPLOAD, postParams, POST,
                            true), ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 查询车辆轨迹
     *
     * @param stime
     * @param etime
     * @param hpzl
     * @param hphm
     * @return
     */
    public WebQueryResult<List<ClgjBean>> queryClgj(String stime, String etime, String hpzl, String hphm) {
        WebQueryResult<List<ClgjBean>> re = new WebQueryResult<List<ClgjBean>>();
        try {
            Log.e("Resutl dao query clgj", stime + "/" + etime + "/" + hpzl + "/" + hphm);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("stime", stime));
            postParams.add(new BasicNameValuePair("etime", etime));
            postParams.add(new BasicNameValuePair("hpzl", hpzl));
            postParams.add(new BasicNameValuePair("hphm", hphm));
            postParams.add(new BasicNameValuePair("cbz", ""));
            postParams.add(new BasicNameValuePair("xsfx", ""));
            postParams.add(new BasicNameValuePair("isLike", ""));
            WebQueryResult<String> s = restfulQuery(getUrl() + QUERY_CLGJ, postParams, POST);
            re = GlobalMethod.webXmlStrToListObj(s, ClgjBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return re;
    }

    public WebQueryResult<SchoolZtzBean> querySchool(String pzh) {
        WebQueryResult<SchoolZtzBean> re = new WebQueryResult<SchoolZtzBean>();
        try {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("pzh", pzh));
            WebQueryResult<String> s = restfulQuery(getUrl() + QUERY_SCHOOL, postParams, POST);
            re = GlobalMethod.webXmlStrToObj(s, SchoolZtzBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public void queryForbidById(String id) {

    }

    //------------------------------------------登录文件更新---------------------------------------------

    protected String url = "ydjw/services/login/updateFileVersion";


    public WebQueryResult<String> checkUserAndUpdate(String yhbh, String mm,
                                                     String sbid, boolean isCheckMd5) {
        String url = getUrl()
                + (isCheckMd5 ? checkMd5 : checkUrl);
        Log.e("login url", url);
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("yhbh", yhbh));
        postParams.add(new BasicNameValuePair("sbid", sbid));
        postParams.add(new BasicNameValuePair("mm", mm));
        return restfulQuery(url, postParams, POST, true);
    }

    /**
     * 最近的验证、更新、警员信息一次请求返回的方法
     *
     * @param yhbh
     * @param mm
     * @param sbid
     * @return
     */
    public WebQueryResult<String> checkUserAndUpdate2222(String yhbh, String mm,
                                                         String sbid, boolean isCheckMd5) {
        WebQueryResult<String> result = new WebQueryResult<String>();
        result.setStatus(HttpStatus.SC_BAD_REQUEST);
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        String url = getUrl()
                + (isCheckMd5 ? checkMd5 : checkUrl);
        HttpPost request = new HttpPost(url);
        // writeDisk(request.getURI().toString());
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("yhbh", yhbh));
        postParams.add(new BasicNameValuePair("sbid", sbid));
        postParams.add(new BasicNameValuePair("mm", mm));
        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                    postParams, "utf-8");
            request.addHeader("Content-type", "application/xml");
            request.setEntity(formEntity);
            HttpResponse response = httpclient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            result.setStatus(status);
            // writeDisk("return code", "return code is " + status);
            // 返回数据正常
            String html = "";
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(entity.getContent(), "utf-8"));
                    String s = null;
                    while ((s = reader.readLine()) != null) {
                        html += s;
                    }
                    result.setResult(html);

                }
                Log.e("UpdateFileDao", html);
            }

        } catch (Exception e) {
            // writeDisk(e.toString());
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        return result;
    }

    private int downFile(String url, File file) {
        int writeByte = 0;
        int status = HttpStatus.SC_BAD_REQUEST;
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpClient client = new DefaultHttpClient(httpParameters);

        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = client.execute(request);
            status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                // long fileSize = response.getEntity().getContentLength();
                BufferedInputStream in = new BufferedInputStream(response
                        .getEntity().getContent());

                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(file));
                byte[] b = new byte[1024];
                int l = 0;
                while ((l = in.read(b)) > 0) {
                    out.write(b, 0, l);
                    writeByte += l;
                }
                out.flush();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writeByte;
    }

    public int downloadApkFile(UpdateFile uf, String dir, Handler handler,
                               int fileIndex) {
        String fileId = uf.getId();
        String fileName = uf.getFileName();
        int fileSize = Integer.valueOf(uf.getHashValue());
        String pack = uf.getPackageName();
        // 文件过大，分块下载
        if (fileSize > 100 * 1024) {
            return downloadSmallFile(fileId, fileName, fileSize, pack, dir,
                    handler, fileIndex);
        }
        int writeByte = 0;
        int status = HttpStatus.SC_BAD_REQUEST;
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpClient client = new DefaultHttpClient(httpParameters);

        HttpGet request = new HttpGet(getFileUrl() + pack);
        try {

            HttpResponse response = client.execute(request);
            status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                // long fileSize = response.getEntity().getContentLength();
                BufferedInputStream in = new BufferedInputStream(response
                        .getEntity().getContent());

                File file = new File(dir);
                if (!file.exists())
                    file.mkdirs();
                file = new File(file, fileName);
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(file));
                byte[] b = new byte[1024 * 1024];
                int l = 0;
                while ((l = in.read(b)) > 0) {
                    out.write(b, 0, l);
                    writeByte += l;
                    int step = writeByte * 100 / fileSize;
                    LoginDao.sendData(handler, DownFileThread.DOWNLOADING_APK,
                            fileIndex, step);
                }
                out.flush();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writeByte;
    }

    private int downloadSmallFile(String fileId, String fileName, int fileSize,
                                  String pack, String dir, Handler handler, int fileIndex) {
        File fdir = new File(dir);
        if (!fdir.exists())
            fdir.mkdirs();
        List<File> files = new ArrayList<File>();
        int count = fileSize / (100 * 1024) + 1;
        for (int i = 0; i < count; i++) {
            String url = getFileUrl() + pack + "&index=" + i;
            File sf = new File(dir, "temp_" + i);
            int wb = downFile(url, sf);
            boolean isOk = false;
            if (i < count - 1) {
                isOk = wb == 100 * 1024;
            } else {
                isOk = wb == fileSize - i * 100 * 1024;
            }
            LoginDao.sendData(handler, DownFileThread.DOWNLOADING_APK,
                    fileIndex, (i + 1) * 100 / count);
            if (isOk)
                files.add(sf);
            else
                return 0;
        }
        if (files.size() != count)
            return 0;
        int writeByte = 0;
        File bigFile = new File(fdir, fileName);
        try {
            BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(bigFile));
            byte[] b = new byte[1024];
            for (File file : files) {
                BufferedInputStream bin = new BufferedInputStream(
                        new FileInputStream(file));
                int len = 0;
                while ((len = bin.read(b)) > 0) {
                    bout.write(b, 0, len);
                    writeByte += len;
                }
                bout.flush();
                bin.close();
            }
            bout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writeByte;
    }


    // ------------------------以下是本类中公用的方法-----------------------------------

    /**
     * 查询JSON数据
     *
     * @param url
     * @param params
     * @param method
     * @return
     */
    public String restfulJsonQuery(String url, List<NameValuePair> params, int method) {
        String result = "";
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        Log.e(getClassName(), url + " method: " + method);
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        try {
            HttpResponse response = null;
            if (method == GET) {
                HttpGet request = new HttpGet(url);
                request.addHeader("Content-type", "application/json");
                request.addHeader("jybh", jh);
                request.addHeader("meid", GlobalData.serialNumber);
                response = httpclient.execute(request);
            } else {
                HttpPost requestPost = new HttpPost(url);
                requestPost.addHeader("jybh", jh);
                requestPost.addHeader("meid", GlobalData.serialNumber);
                if (params != null && params.size() > 0) {
                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                            params, "utf-8");
                    requestPost.addHeader("Content-type", "application/json");
                    requestPost.setEntity(formEntity);
                }
                response = httpclient.execute(requestPost);
            }

            int status = response.getStatusLine().getStatusCode();
            Log.e(getClassName(), " status: " + status);
            // 返回数据正常
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(entity.getContent(), "utf-8"));
                    String s = null;
                    while ((s = reader.readLine()) != null) {
                        result += s;
                    }
                    Log.e(getClassName() + " restfulQuery", result);
                }
            } else {
                JSONObject json = new JSONObject();
                json.put("err", status + "");
                result = json.toString();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JSONObject json = new JSONObject();
            try {
                json.put("err", e1.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result = json.toString();
        }
        httpclient.getConnectionManager().shutdown();
        return result;
    }

    public WebQueryResult<String> restfulQuery(String url,
                                               List<NameValuePair> params, int method, boolean isXml) {
        WebQueryResult<String> result = new WebQueryResult<String>(
                HttpStatus.SC_BAD_REQUEST);
        int status = HttpStatus.SC_BAD_REQUEST;
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        Log.e(getClassName(), url + " method: " + method);
        String jh = "";
        String serialNumber = GlobalData.serialNumber == null ? "" : GlobalData.serialNumber;
        if (GlobalData.grxx != null && GlobalData.grxx.get(GlobalConstant.JH) != null)
            jh = GlobalData.grxx.get(GlobalConstant.JH);

        try {
            HttpResponse response;
            if (method == GET) {
                HttpGet request = new HttpGet(url);
                request.addHeader("jybh", jh);
                request.addHeader("meid", serialNumber);
                response = httpclient.execute(request);
            } else {
                HttpPost requestPost = new HttpPost(url);
                requestPost.addHeader("jybh", jh);
                requestPost.addHeader("meid", serialNumber);
                if (params != null && params.size() > 0) {
                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                            params, "utf-8");
                    requestPost.addHeader("Content-type", "application/xml");
                    // request.addHeader("Accept", "text/plain");
                    requestPost.setEntity(formEntity);
                }
                response = httpclient.execute(requestPost);
            }

            status = response.getStatusLine().getStatusCode();
            Log.e("gsm return code", "return code is " + status);
            // 返回数据正常
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(entity.getContent(), "utf-8"));
                    String s = null;
                    String r = "";
                    while ((s = reader.readLine()) != null) {
                        r += s;
                    }
                    Log.e(getClassName() + " restfulQuery", r);
                    if (!TextUtils.isEmpty(r)) {
                        result.setResult(r);
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        result.setStatus(status);
        return result;
    }

    public WebQueryResult<String> uploadByte(String url, byte[] data,
                                             InputStream in, long inLength, Handler handler) {
        WebQueryResult<String> re = new WebQueryResult<String>(
                HttpStatus.SC_BAD_REQUEST);
        try {
            int readLen = 0;
            sendData(0, (int) inLength, 0, handler);
            URL nurl = new URL(url);
            URLConnection conn = nurl.openConnection();
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            if (data != null && data.length > 0)
                out.write(data);
            if (in != null) {
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                    readLen += len;
                    sendData(0, (int) inLength, readLen, handler);
                }
                sendData(0, (int) inLength, (int) inLength, handler);
            }
            out.close();
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            String s = null;
            String result = "";
            while ((s = reader.readLine()) != null) {
                result += s;
            }
            if (!TextUtils.isEmpty(result)) {
                re.setResult(result);
                re.setStatus(HttpStatus.SC_OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendData(1, 0, 0, handler);
        }

        return re;
    }

    /**
     * 下载小文件的公共方法
     *
     * @param urlStr
     * @param dest
     * @return
     */
    public long downloadFile(String urlStr, File dest) {
        long count = 0;
        byte[] b = new byte[1024];
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK)
                return 0;
            InputStream is = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(dest);
            int len = -1;
            while ((len = is.read(b)) > 0) {
                out.write(b, 0, len);
                count += len;
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public long downloadFile(String urlStr, File dest, long fileSize,
                             Handler handler) {
        long count = 0;
        byte[] b = new byte[1024];
        try {
            String u = getUrl() + urlStr;
            Log.e("RestfulDao", "下载文件地址：" + u);
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            int code = conn.getResponseCode();
            Log.e("RestfulDao", "下载文件状态：" + code);
            if (code != HttpURLConnection.HTTP_OK)
                return 0;
            InputStream is = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(dest);
            int len = -1;
            while ((len = is.read(b)) > 0) {
                out.write(b, 0, len);
                count += len;
                int step = (int) (count * 100 / fileSize);
                sendData(0, 0, step, handler);
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendData(0, 1, 100, handler);
        return count;
    }

    public void sendData(int err, int what, int step, Handler mHandler) {
        if (mHandler == null)
            return;
        Message m = mHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("catalog", 0);
        b.putInt("length", what);
        b.putInt("step", step);
        b.putInt("err", err);
        m.setData(b);
        mHandler.sendMessage(m);
    }

    public WebQueryResult<String> restfulQuery(String url,
                                               List<NameValuePair> params, int method) {
        return restfulQuery(url, params, method, false);
    }

    // public abstract WebQueryResult<String> restfulQuery(String url,
    // List<NameValuePair> params, int method);

    public abstract String getUrl();

    public abstract String getClassName();

    public abstract String getPicUrl();

    public abstract String getFileUrl();

    public abstract String getJqtbFileUrl();

    public String getIconFileUrl() {
        return getUrl() + "/ydjw/DownloadIconFile";
    }


}