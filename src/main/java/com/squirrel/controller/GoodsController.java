package com.squirrel.controller;

import com.alibaba.fastjson.JSON;
import com.squirrel.dto.AjaxResult;
import com.squirrel.exception.SystemException;
import com.squirrel.pojo.*;
import com.squirrel.service.*;
import com.squirrel.util.DateUtil;
import com.squirrel.util.ResponseResult;
import com.squirrel.util.SystemConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Controller
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private CatelogService catelogService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentsService commentsService;

    @GetMapping(value = "/search")
    @ResponseBody
    public ResponseResult searchGoods(@RequestParam("info") String info) {
        if (!StringUtils.hasText(info)) {
            return ResponseResult.errorResult("请输入搜索关键字");
        }
        return goodsService.searchGoods(info);
    }

    /**
     * 跳转到搜索页
     * @param info
     * @return
     */
    @GetMapping("/toSearch/{info}")
    @Deprecated
    public ModelAndView toSearch(@PathVariable("info") String info) {
        ModelAndView modelAndView = new ModelAndView("/goods/searchGoods");
        modelAndView.addObject("info", info);
        return modelAndView;
    }

    /**
     * 修改商品信息
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/editGoods/{id}")
    public ModelAndView editGoods(HttpServletRequest request, @PathVariable("id") Integer id)
            throws Exception {

        Goods goods = goodsService.getGoodsByPrimaryKey(id);
        List<Image> imageList = imageService.getImageByGoodsId(id);
        GoodsExtend goodsExtend = new GoodsExtend();
        goodsExtend.setGoods(goods);
        goodsExtend.setImages(imageList);
        ModelAndView modelAndView = new ModelAndView();
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        modelAndView.addObject("cur_user", cur_user);
        // 将商品信息添加到model
        modelAndView.addObject("goodsExtend", goodsExtend);
        modelAndView.setViewName("/goods/editGoods");
        return modelAndView;
    }

    /**
     * 提交商品更改信息
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/editGoodsSubmit")
    public String editGoodsSubmit(HttpServletRequest request, Goods goods) throws Exception {
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        goods.setUserId(cur_user.getId());
        String polish_time = DateUtil.getNowDay();
        goods.setPolishTime(polish_time);
        goodsService.updateGoodsByPrimaryKeyWithBLOBs(goods.getId(), goods);
        return "redirect:/user/allGoods";
    }

    /**
     * 商品下架
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/offGoods")
    public ModelAndView offGoods() throws Exception {

        return null;
    }

    /**
     * 用户删除商品
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/deleteGoods/{id}")
    public String deleteGoods(HttpServletRequest request, @PathVariable("id") Integer id)
            throws Exception {
        Goods goods = goodsService.getGoodsByPrimaryKey(id);
        //删除商品后，catlog的number-1，user表的goods_num-1，image删除,更新session的值
        User cur_user = (User) request.getSession().getAttribute("cur_user");
        goods.setUserId(cur_user.getId());
        int number = cur_user.getGoodsNum();
        Integer calelog_id = goods.getCatelogId();
        Catelog catelog = catelogService.selectByPrimaryKey(calelog_id);
        catelogService.updateCatelogNum(calelog_id, catelog.getNumber() - 1);
        userService.updateGoodsNum(cur_user.getId(), number - 1);
        cur_user.setGoodsNum(number - 1);
        request.getSession().setAttribute("cur_user", cur_user);//修改session值
        imageService.deleteImagesByGoodsPrimaryKey(id);
        goodsService.deleteGoodsByPrimaryKey(id);
        return "redirect:/user/allGoods";
    }

    /**
     * 发布商品
     * @param target 需要跳转的目标功能
     * @return
     */
    @GetMapping(value = "/publishGoods")
    public ModelAndView publishGoods(@RequestParam(value = "target",required = false)
                                         String target) {
        ModelAndView modelAndView = new ModelAndView(SystemConstant.USER_HOME);
        modelAndView.addObject("target",target);
        return modelAndView;
    }

    /**
     * 提交发布的商品信息
     * 上传物品需要登录验证，但是上传物品的前端提交时是formData格式，
     * 会默认写入请求头，导致我手动写入的失效
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadGoods")
    @ResponseBody
    public ResponseResult uploadGoods(@RequestParam(value = "objectString",required = false) String goodsStr,
            MultipartFile[] file)
    {
        //前端传来的goods是json格式的字符串
        Goods goods = JSON.parseObject(goodsStr, Goods.class);
        System.out.println(goods);
        Objects.requireNonNull(goods);
        return goodsService.uploadGoods(goods, file);
    }

    /**
     * 下架商品
     */
    @DeleteMapping("/api/offGoods/{id}")
    @ResponseBody
    public AjaxResult offGoods(@PathVariable int id) {
        AjaxResult ajaxResult = new AjaxResult();
        goodsService.deleteGoodsByPrimaryKey(id);
        return new AjaxResult().setData(true);
    }

    @GetMapping({"/list", ""})
    @ResponseBody
    public ResponseResult getAllGoods() {
        return ResponseResult.okResult(goodsService.getAllGoods());
    }

    @GetMapping("/getGoods/{categoryId}")
    @ResponseBody
    public ResponseResult getGoodsByCategoryId(@PathVariable("categoryId") Integer categoryId) {
        if (categoryId < 0) {
            return ResponseResult.okResult(goodsService.getAllGoods());
        }
        return ResponseResult.okResult(goodsService.getGoodsByCatelog(categoryId));
    }
}
