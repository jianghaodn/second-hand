package com.squirrel.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private Integer id;

    private Integer goodsId;

    private String imgUrl;

    public Image(Integer goodsId, String imgUrl) {
        this.goodsId = goodsId;
        this.imgUrl = imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        //去除图片链接前后的空格
        if(StringUtils.hasText(imgUrl)){
            this.imgUrl =  imgUrl.trim();
        }else {
            this.imgUrl = null;
        }
    }
}
