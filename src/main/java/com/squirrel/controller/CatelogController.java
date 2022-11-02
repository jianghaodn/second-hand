package com.squirrel.controller;

import com.squirrel.service.CatelogService;
import com.squirrel.util.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author Administrator
 */
@Controller
public class CatelogController {
    @Resource
    private CatelogService catelogService;

    @GetMapping("/category/list")
    @ResponseBody
    public ResponseResult getCategories(){
        return ResponseResult.okResult(catelogService.getAllCatelog());
    }
}
