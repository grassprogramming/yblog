package com.blog.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by paul on 2018/6/29.
 */
public class CommonUtil {
    public static String GetProjectUrl(HttpServletRequest request){
        String strBackUrl = request.getScheme() +"://" + request.getServerName() //服务器地址
                + ":"
                + request.getServerPort()           //端口号
                + request.getContextPath()+"/";      //项目名称
        return strBackUrl;
    }
}
