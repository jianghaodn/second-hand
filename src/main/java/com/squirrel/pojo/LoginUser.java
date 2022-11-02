package com.squirrel.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author Administrator
 * security所需的userDetails
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {
    private User user;

    public LoginUser(User user) {
        this.user = user;
    }

    /**
     * 权限信息
     * @return 权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 因为是通过电话进行认证的，因此返回phone属性
     * @return
     */
    @Override
    public String getUsername() {
        return user.getPhone();
    }

    /**
     * 下面几个方法不只打作用是什么，反正全部返回true
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
