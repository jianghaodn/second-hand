package com.squirrel.controller;

import com.squirrel.common.GgeeConst;
import com.squirrel.dto.AjaxResult;
import com.squirrel.exception.GgeeWebError;
import com.squirrel.pojo.Goods;
import com.squirrel.pojo.GoodsExtend;
import com.squirrel.pojo.Image;
import com.squirrel.pojo.User;
import com.squirrel.service.GoodsService;
import com.squirrel.service.ImageService;
import com.squirrel.service.UserService;
import com.squirrel.util.DateUtil;
import com.squirrel.util.JwtUtil;
import com.squirrel.util.MD5;
import com.squirrel.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 */
@Controller
@RequestMapping(value = "/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    /**
     * 登录
     * @param user
     * @return
     */
    @ResponseBody
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping("/getUserInfo")
    @ResponseBody
    public ResponseResult getUserInfo(HttpServletRequest request){
        return userService.getUserInfo(request);
    }
    /**
     * API:验证登录
     */
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult login(HttpServletRequest request, HttpServletResponse response) {
        AjaxResult ajaxResult = new AjaxResult();
        try {
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            User cur_user = userService.getUserByPhone(phone);
            if (cur_user != null) {
                String pwd = MD5.md5(password);
                if (pwd.equals(cur_user.getPassword())) {
                    //设置单位为秒，设置为-1永不过期
                    request.getSession().setMaxInactiveInterval(24 * 60 * 60);    //24小时
                    request.getSession().setAttribute(GgeeConst.CUR_USER, cur_user);
                    ajaxResult.setData(cur_user);
                } else {
                    return AjaxResult.fixedError(GgeeWebError.WRONG_PASSWORD);
                }
            } else {
                return AjaxResult.fixedError(GgeeWebError.WRONG_USERNAME);
            }
        } catch (Exception e) {
            return AjaxResult.fixedError(GgeeWebError.COMMON);
        }
        return ajaxResult;
    }

    /**
     * API:添加用户
     */
    @RequestMapping(value = "/api/addUser", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addUser(@RequestBody User user) {
        AjaxResult ajaxResult = new AjaxResult();
        User existUser = userService.getUserByPhone(user.getPhone());
        if (existUser == null) {//检测该用户是否已经注册
            String t = DateUtil.getNowTime();
            //对密码进行MD5加密
            String str = MD5.md5(user.getPassword());
            user.setCreateAt(t);//创建开始时间
            user.setPassword(str);
            user.setGoodsNum(0);
            user.setStatus((byte) 0);
            userService.addUser(user);
            return new AjaxResult().setData(1);
        }
        return AjaxResult.fixedError(GgeeWebError.AREADY_EXIST_PHONE);
    }

    /**
     * API:更新用户
     */
    @RequestMapping(value = "/api/updateUser", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult updateUser(@RequestBody User user) {
        AjaxResult ajaxResult = new AjaxResult();
        //对密码进行MD5加密
        String str = MD5.md5(user.getPassword());
        user.setPassword(str);
        boolean result = userService.updateUserById(user);
        return new AjaxResult().setData(result);
    }

    /**
     * API:删除用户
     */
    @DeleteMapping("/api/deleteUser/{id}")
    @ResponseBody
    public AjaxResult deleteUser(@PathVariable int id) {
        AjaxResult ajaxResult = new AjaxResult();
        boolean result = userService.deleteUserById(id);
        return new AjaxResult().setData(result);
    }

    /**
     * API:冻结用户
     */
    @RequestMapping(value = "/api/freezeUser/{id}", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult freezeUser(@PathVariable int id) {
        AjaxResult ajaxResult = new AjaxResult();
        boolean result = userService.freezeUser(id);
        return new AjaxResult().setData(result);
    }

    /**
     * API:解冻用户
     */
    @RequestMapping(value = "/api/unfreezeUser/{id}", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult unfreezeUser(@PathVariable int id) {
        AjaxResult ajaxResult = new AjaxResult();
        boolean result = userService.unfreezeUser(id);
        return new AjaxResult().setData(result);
    }

    /**
     * 更改用户名
     *
     * @param request
     * @param user
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/changeName")
    public ModelAndView changeName(HttpServletRequest request, User user, ModelMap modelMap) {
        String url = request.getHeader("Referer");
        //从session中获取出当前用户
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        cur_user.setUsername(user.getUsername());//更改当前用户的用户名
        userService.updateUserName(cur_user);//执行修改操作
        request.getSession().setAttribute("cur_user", cur_user);//修改session值
        return new ModelAndView("redirect:" + url);
    }

    /**
     * 完善或修改信息
     *
     * @param request
     * @param user
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/updateInfo")
    public ModelAndView updateInfo(HttpServletRequest request, User user, ModelMap modelMap) {
        //从session中获取出当前用户
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        cur_user.setUsername(user.getUsername());
        cur_user.setQq(user.getQq());
        userService.updateUserName(cur_user);//执行修改操作
        request.getSession().setAttribute("cur_user", cur_user);//修改session值
        return new ModelAndView("redirect:/user/basic");
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @GetMapping("/logout")
    @ResponseBody
    public ResponseResult logout(HttpServletRequest request) {
        return userService.logout(null);
    }

    /**
     * 注册用户接口
     * @param user
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseResult register(@RequestBody User user){
        if (Objects.nonNull(user)) {
            return userService.register(user);
        }
        return ResponseResult.errorResult();
    }
    /**
     * 个人中心
     *
     * @return
     */
    @RequestMapping(value = "/home")
    public String home(HttpServletRequest request, Model model) {
        return "/user/home";
    }

    /**
     * 个人信息设置
     *
     * @return
     */
    @RequestMapping(value = "/basic")
    public String basic(HttpServletRequest request, Model model) {
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        model.addAttribute("cur_user", cur_user);
        return "/user/basic";
    }

    /**
     * 我的闲置
     * 查询出所有的用户商品以及商品对应的图片
     *
     * @return 返回的model为 goodsAndImage对象,该对象中包含goods 和 images，参考相应的类
     */
    @GetMapping("/allGoods")
    public String goods(HttpServletRequest request) {
//        User user = (User) request.getSession().getAttribute("user");
//        if (Objects.isNull(user)) {
//            return new ModelAndView("redirect:/goods/homeGoods");
//        }
//        Integer userId = user.getId();
//        List<Goods> goodsList = goodsService.getGoodsByUserId(userId);
//        List<GoodsExtend> goodsAndImage = new ArrayList<>();
//
//        getGoodsInfo(goodsList, goodsAndImage);

//        ModelAndView mv = new ModelAndView();
//        mv.addObject("user", user);
//        mv.addObject("goodsAndImage", goodsAndImage);
//        mv.setViewName();
        return "/user/goods";
    }

    /**
     * 封装商品的信息
     * @param goodsList     商品清单
     * @param goodsAndImage 商品以及图片信息
     */
    void getGoodsInfo(List<Goods> goodsList, List<GoodsExtend> goodsAndImage) {
        for (int i = 0; i < goodsList.size(); i++) {
            //将用户信息和image信息封装到GoodsExtend类中，传给前台
            GoodsExtend goodsExtend = new GoodsExtend();
            Goods goods = goodsList.get(i);
            List<Image> images = imageService.getImageByGoodsId(goods.getId());
            goodsExtend.setGoods(goods);
            goodsExtend.setImages(images);
            goodsAndImage.add(i, goodsExtend);
        }
    }
}
