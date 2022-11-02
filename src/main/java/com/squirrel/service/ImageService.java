package com.squirrel.service;

import com.squirrel.pojo.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Administrator
 */
public interface ImageService {
    int insert(Image record);
    /**
     * 通过商品id获取该商品的图片
     * @param goodsId
     * @return
     */
    public List<Image> getImageByGoodsId(Integer goodsId);

    /**
     * 通过商品Id删除商品
     * @param goodsId
     * @return
     */
    int deleteImagesByGoodsPrimaryKey(Integer goodsId);

    /**
     * 上传图片
     * @param file 图片文件
     * @return 图片url
     */
    String updateImages(MultipartFile file);
}
