package com.squirrel.util;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Administrator
 */
public class SystemConstant {
    public static final String EMPTY_STRING = "";

    public static final String NO_GOODS_EXCEPTION ="没有这件商品！" ;
    public static final String USER_PREFIX = "login:";
    public static final Integer NEED_LOGIN = 401;
    public static final Integer REGISTER_FAIL = 600;
    public static final String DEFAULT_IMG ="https://th.bing.com/th/id/OIP.m61Y2X4HSsLHVISKGEVHXwHaF7?pid=ImgDet&rs=1";
    public static final String USER_HOME = "/user/home";
    public static final Integer TOKEN_OUT_OF_DATE = 501;

    public static final Integer LOGIN_FAIL = 406;
}
