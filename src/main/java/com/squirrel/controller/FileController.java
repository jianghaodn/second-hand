package com.squirrel.controller;

import com.squirrel.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Administrator
 */
@RestController
public class FileController {

    @Autowired
    private FileUtils fileUtils;

    /**
     * 发布物品
     *
     * @return 发布商品的url
     */
//    @GetMapping("/upload")
    public ModelAndView upload(MultipartFile file) {
        ModelAndView modelAndView = new ModelAndView();
        String url = fileUtils.saveFile(file);

        modelAndView.addObject("imgUrl",url);
        return modelAndView;
    }
}
