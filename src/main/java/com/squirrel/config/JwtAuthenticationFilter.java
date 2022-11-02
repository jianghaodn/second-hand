package com.squirrel.config;

import com.squirrel.pojo.LoginUser;
import com.squirrel.util.JwtUtil;
import com.squirrel.util.ResponseResult;
import com.squirrel.util.SystemConstant;
import com.squirrel.util.WebUtils;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Administrator
 * 认证类
 */
@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求中的token
        // 除登录以外，每次请求必须携带token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            // 直接放行
            filterChain.doFilter(request, response);
            return;
        }
        String userPhone = null;
        // 解析token
        try {
            Claims claims = JwtUtil.parseJWT(token);
            // 得到的是用户的id
            userPhone = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            // 解析token失败,可能是token过期、错误
            //这里抛出异常，全局异常处理无法捕获，会直接返回前端
            //如果想要捕获异常信息响应到前端，那么可以将异常信息保存在request中，到了service方法中判断
            //如果携带有异常信息，那么再抛出异常，这样就可以由全局异常处理进行捕获
//            throw new SystemException(SystemConstant.TOKEN_OUT_OF_DATE,"登录信息已经过期");
            WebUtils.render(response, ResponseResult.result(SystemConstant.TOKEN_OUT_OF_DATE, "token错误或已经过时"));
            return;
        }
        //如果认证失败，会经过FailHandler
        // 根据userPhone 从redis中查询这个用户的信息
        LoginUser loginUser = redisCache.getCacheObject(SystemConstant.USER_PREFIX + userPhone);

        // 如果查询结果为空，说明信息已经过期或者没登录，需要重新登录
        if (Objects.isNull(loginUser)) {
            WebUtils.render(response, ResponseResult.errorResult(SystemConstant.TOKEN_OUT_OF_DATE, "token过期，请先登录!"));
            return;
        }

        // 用户已经登录
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser,
                null, null);

        // 将用户认证信息存入securityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
        //认证通过，那么会经过SuccessHandler

    }
}
