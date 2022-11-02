package com.squirrel.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.squirrel.util.WebUtils;
import com.squirrel.util.ResponseResult;
import com.squirrel.util.SystemConstant;;


/**
 * 认证失败处理器
 */
public class FailHander implements AuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        // TODO Auto-generated method stub
        //给前端相应信息
        WebUtils.render(SystemConstant.LOGIN_FAIL,response,ResponseResult.errorResult());
    }
    
    
}
