package com.squirrel.util;

import com.squirrel.pojo.LoginUser;
import com.squirrel.pojo.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * @author Administrator
 */
public class SecurityUtils {

    public static LoginUser getLoginUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Objects.isNull(principal)){
            return null;
        }
        return (LoginUser) principal;
    }

    public static User getUser(){
        if(Objects.isNull(getLoginUser())){
            return null;
        }
        return getLoginUser().getUser();
    }
    public static Integer getUserId() {
        User user = getUser();
        if (Objects.nonNull(user)){
            //注意，Integer.parseInt超过10位数就转化不了了
            return user.getId();
        }
        return -1;
    }

    public static String getPhone(){
        return Objects.requireNonNull(getUser()).getPhone();
    }
}
