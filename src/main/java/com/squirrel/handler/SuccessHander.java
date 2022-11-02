package com.squirrel.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.squirrel.pojo.LoginUser;
import org.springframework.stereotype.Component;

/**
 * 配置认证成功处理器
 */
@Component
public class SuccessHander implements AuthenticationSuccessHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.web.authentication.AuthenticationSuccessHandler#
     * onAuthenticationSuccess(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse,
     * org.springframework.security.core.Authentication)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        System.out.println(principal.getUser().getUsername() + ":认证成功了");

    }

}
