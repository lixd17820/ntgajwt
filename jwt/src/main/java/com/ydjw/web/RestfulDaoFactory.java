package com.ydjw.web;

import com.ntga.dao.ConnCata;
import com.ntga.dao.GlobalData;

public class RestfulDaoFactory {

    public static RestfulDao getDao() {
        return getDao(GlobalData.connCata);
    }

    public static RestfulDao getDao(ConnCata conn) {
        if (conn == ConnCata.JWTCONN)
            return new GsmRestfulDao();
        else if (conn == ConnCata.OUTSIDECONN)
            return new CdmaResutfulDao();
        else if (conn == ConnCata.OFFCONN)
            return new OfflineDao();
        //else if (conn == ConnCata.INSIDECONN)
        return new ThreeTeamDao();
    }

}
