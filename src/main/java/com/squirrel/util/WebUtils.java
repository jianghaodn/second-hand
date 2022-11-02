package com.squirrel.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Administrator
 */
public class WebUtils {

    /**
     * 直接使用response.getWriter()会乱码，需要进行设置
     * @param response
     * @param o
     */
    public static void render(HttpServletResponse response,Object o)  {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(JSON.toJSONString(o));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void render(Integer code,HttpServletResponse response,Object o)  {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        try (PrintWriter writer = response.getWriter()) {
            writer.println(JSON.toJSONString(ResponseResult.result(code,o)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
