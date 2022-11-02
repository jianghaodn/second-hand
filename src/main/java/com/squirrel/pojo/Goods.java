package com.squirrel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {
    private Integer id;

    private Integer catelogId;

    private Integer userId;

    private String name;

    private Float price;

    private Float realPrice;

    private String startTime;

    private String endTime;

    private String polishTime;

    private Integer commetNum;

    private String describle;

    private int state;

    private User user;

    private List<Image> images;

    private String imgUrl;
    private String imgId;
}
