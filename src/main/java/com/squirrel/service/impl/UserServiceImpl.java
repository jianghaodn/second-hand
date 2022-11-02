package com.squirrel.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.squirrel.config.RedisCache;
import com.squirrel.dao.UserMapper;
import com.squirrel.exception.SystemException;
import com.squirrel.pojo.Goods;
import com.squirrel.pojo.LoginUser;
import com.squirrel.pojo.User;
import com.squirrel.service.GoodsService;
import com.squirrel.service.UserService;
import com.squirrel.util.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author Administrator
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public void addUser(User user) {
        userMapper.insert(user);
        // 空属性为MySQL设定默认值
        // userMapper.insertSelective(user);
    }

    @Override
    public User getUserByPhone(String phone) {
        User user = userMapper.getUserByPhone(phone);
        return user;
    }

    @Override
    public void updateUserName(User user) {
        userMapper.updateByPrimaryKey(user);
    }

    @Override
    public int updateGoodsNum(Integer id, Integer goodsNum) {
        return userMapper.updateGoodsNum(id, goodsNum);
    }

    @Override
    public User selectByPrimaryKey(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    /**
     * 获取出当前页用户
     * 
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<User> getPageUser(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 分页核心代码
        List<User> data = userMapper.getUserList();
        return data;
    }

    /**
     * 获取出用户的数量
     * 
     * @return
     */
    @Override
    public int getUserNum() {
        List<User> users = userMapper.getUserList();
        return users.size();
    }

    public static HttpSession getSession() {
        HttpSession session = null;
        try {
            session = getRequest().getSession();
        } catch (Exception e) {
        }
        return session;
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    @Override
    public Map<String, Object> getUsers(int pageNum, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        int count = userMapper.getCount();
        if (count == 0) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", 1);
            data.put("totalPageSize", 0);
            data.put("users", new ArrayList<>());
            return data;
        }
        int totalPageNum = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        if (pageNum > totalPageNum) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", totalPageNum);
            data.put("totalPageSize", 0);
            data.put("users", new ArrayList<>());
            return data;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.getUserList();
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("totalPageNum", totalPageNum);
        data.put("totalPageSize", count);
        data.put("users", users);
        return data;
    }

    @Override
    public boolean updateUserById(User user) {
        return userMapper.updateByPrimaryKeySelective(user) > 0;
    }

    @Override
    public boolean deleteUserById(int id) {
        return userMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean freezeUser(int id) {
        return userMapper.updateStatus(id, 1) > 0;
    }

    @Override
    public boolean unfreezeUser(int id) {
        return userMapper.updateStatus(id, 0) > 0;
    }

    @Override
    public List<User> getUsersByIds(List<Integer> ids) {
        return userMapper.getUsersByIds(ids);
    }

    @Override
    public List<User> getUsersByIds(Set<Integer> ids) {
        return userMapper.getUsersByIdsSet(ids);
    }

    /**
     * 登录
     * 
     * @param user
     * @return
     */
    @Override
    public ResponseResult login(User user) {
        // 认证用户的phone和password
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getPhone(), user.getPassword()));
        // 实际上如果登录失败的话不会执行下面的代码，会被security的认证失败拦截器拦截
        if (Objects.isNull(authenticate)) {
            // 认证失败
            throw new SystemException("登录失败");
        }

        // 获取用户的信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        // 获取phone
        String phone = String.valueOf(loginUser.getUser().getPhone());
        System.out.println("获取的ID：" + phone);
        // 将用户信息存入redis
        redisCache.setCacheObject(SystemConstant.USER_PREFIX + phone, loginUser);

        // 将用户token和用户信息传回前端
        String jwt = JwtUtil.createJWT(phone);
        HashMap<Object, Object> token = new HashMap<>();
        token.put("token", jwt);
        token.put("userInfo", JSON.toJSONString(loginUser.getUser()));
        return ResponseResult.okResult(token);
    }

    /**
     * 登出
     * 
     * @param user
     * @return
     */
    @Override
    public ResponseResult logout(User user) {
        redisCache.deleteObject(SystemConstant.USER_PREFIX + SecurityUtils.getUserId());
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserInfo(HttpServletRequest request) {
        System.out.println("获取用户信息");
        String phone = SecurityUtils.getPhone();
        User userByPhone = getUserByPhone(phone);
        // 获取用户所持的闲置物品数量
        List<Goods> goods = goodsService.getGoodsByUserId(userByPhone.getId());
        HashMap<Object, Object> resultMap = new HashMap<>();
        resultMap.put("user", userByPhone);
        resultMap.put("goodsNum", goods.size());
        return ResponseResult.okResult(resultMap);
    }

    @Override
    public ResponseResult register(User user) {
        // 1.验证手机号是否存在
        if (!StringUtils.hasText(user.getPhone())) {
            return ResponseResult.errorResult(SystemConstant.REGISTER_FAIL, "请输入手机号！");
        }
        User userByPhone = getUserByPhone(user.getPhone());
        if (Objects.nonNull(userByPhone)) {
            return ResponseResult.errorResult(SystemConstant.REGISTER_FAIL, "手机号已经被使用！");
        }
        String password = user.getPassword();
        if (!StringUtils.hasText(password)) {
            return ResponseResult.errorResult();
        }
        String passwordEncode = bCryptPasswordEncoder.encode(password);
        // 将用户存入数据库
        user.setPassword(passwordEncode);
        int insert = userMapper.insert(user);
        return ResponseResult.okResult(insert == 1 ? "注册成功" : "注册失败");
    }
}
