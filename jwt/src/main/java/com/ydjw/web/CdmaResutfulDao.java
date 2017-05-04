package com.ydjw.web;

import android.util.Log;

public class CdmaResutfulDao extends RestfulDao {

    public CdmaResutfulDao() {
        Log.e("CdmaResutfulDao", "CdmaResutfulDao create");
    }

    @Override
    public String getUrl() {
        return "http://www.ntjxj.com";
    }

    @Override
    public String getPicUrl() {
        return getUrl() + PIC_URL;
    }

    @Override
    public String getFileUrl() {
        return "http://www.ntjxj.com/ydjw/DownloadFile?pack=";
    }

    @Override
    public String getJqtbFileUrl() {
        return getUrl() + JQTB_FILE_URL;
    }

    @Override
    public String getClassName() {
        return "CdmaResutfulDao";
    }

}
