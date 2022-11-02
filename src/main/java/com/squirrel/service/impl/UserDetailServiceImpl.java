package com.squirrel.service.impl;

import com.squirrel.exception.SystemException;
import com.squirrel.pojo.LoginUser;
import com.squirrel.pojo.User;
import com.squirrel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Administrator
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByPhone(username);
        if(Objects.isNull(user)) {
            // 如果这里出了问题，不会被security的AuthenticationEntryPoint捕获，
            // 也不会调用AuthenticationFailureHandler进行认证失败处理？？？

            //没有进入AuthenticationFailureHandler的原因是我没有把自定义的认证失败处理器加入到过滤器链中
//
            //如果我抛出UsernameNotFoundException这个异常，那么自定义的异常信息不起作用
            //但是我抛出RuntimeException的话，自定义的异常信息就会被传递
            throw new RuntimeException("用户名或密码错误！");
//            throw new UsernameNotFoundException();
        }
        return new LoginUser(user);
    }
}
