package com.squirrel.service.impl;

import com.squirrel.dao.ImageMapper;
import com.squirrel.pojo.Image;
import com.squirrel.service.ImageService;
import com.squirrel.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Service("imageService")
public class ImageServiceImpl implements ImageService {
    @Resource
    private ImageMapper imageMapper;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public int insert(Image record) {
        return imageMapper.insert(record);
    }

    @Override
    public List<Image> getImageByGoodsId(Integer goodsId) {
        return imageMapper.selectByGoodsId(goodsId);
    }


    @Override
    public int deleteImagesByGoodsPrimaryKey(Integer goodsId) {
        return imageMapper.deleteImagesByGoodsPrimaryKey(goodsId);
    }

    @Override
    public String updateImages(MultipartFile file) {
        return fileUtils.updateFile(file);
    }
}
