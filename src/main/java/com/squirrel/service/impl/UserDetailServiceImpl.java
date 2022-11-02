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
        if(Objects.isNull(user)){
            throw new SystemException("用户没有找到！");
        }
        return new LoginUser(user);
    }
}
