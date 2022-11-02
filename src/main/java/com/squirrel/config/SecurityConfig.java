package com.squirrel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.squirrel.handler.FailHander;
import com.squirrel.handler.SuccessHander;

/**
 * @author Administrator
 * security 配置类
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private SuccessHander successHander;

    @Autowired
    private FailHander failHander;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //需要禁用csrf、session
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/user/login")
                .anonymous()
                .antMatchers("/user/register").anonymous()
                .antMatchers("/user/addUser").anonymous()
                .antMatchers("/goods/uploadGoods").authenticated()
                .anyRequest().permitAll();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //允许同源嵌套其他页面
        http.headers().frameOptions().sameOrigin();
        //将认证失败处理器和认证成功处理器加入到过滤器链
        http.formLogin().successHandler(successHander).failureHandler(failHander);

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
    }

    /**
     * 将认证管理器注入容器
     *
     * @return AuthenticationManager对象，该对象的作用是认证账号和密码
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 使用BCryptPasswordEncoder加密器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}

