package com.squirrel.service.impl;

import com.github.pagehelper.PageHelper;
import com.squirrel.dao.GoodsMapper;
import com.squirrel.exception.SystemException;
import com.squirrel.pojo.Goods;
import com.squirrel.pojo.Image;
import com.squirrel.service.GoodsService;
import com.squirrel.service.ImageService;
import com.squirrel.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对商品的操作类（增删改查）
 *
 * @author Administrator
 */
@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private ImageService imageService;

    @Override
    public int addGood(Goods goods, Integer duration) {
        String startTime = DateUtil.getNowDay();
        String endTime = DateUtil.getLastTime(startTime, duration);
        String polishTime = startTime;
        //添加上架时间，下架时间，擦亮时间
        goods.setPolishTime(polishTime);
        goods.setEndTime(endTime);
        goods.setStartTime(startTime);
        return goodsMapper.insert(goods);
    }

    @Override
    public Goods getGoodsByPrimaryKey(Integer goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        return goods;
    }

    @Override
    public void deleteGoodsByPrimaryKey(Integer id) {
        goodsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Goods> getAllGoods() {
        List<Goods> goods = goodsMapper.selectAllGoods();
        for (Goods good : goods) {
            good.setImgUrl(imageService.getImageByGoodsId(good.getId()).get(0).getImgUrl());
        }
        return goods;
    }

    @Override
    public List<Goods> searchGoods(String name, String describle) {
        List<Goods> goods = goodsMapper.searchGoods(name, describle);
        return goods;
    }

    @Override
    public List<Goods> getGoodsByCatelog(Integer id, String name, String describle) {
        List<Goods> goods = goodsMapper.selectByCatelog(id, name, describle);
        for (Goods good : goods) {
            List<Image> imageByGoodsId = imageService.getImageByGoodsId(good.getId());
            if (imageByGoodsId.size() != 0) {
                good.setImgUrl(imageByGoodsId.get(0).getImgUrl());
            } else {
                //此商品不包含图片，使用指定的图片
                good.setImgUrl(SystemConstant.DEFAULT_IMG);
            }
        }
        return goods;
    }

    @Override
    public List<Goods> getGoodsByCatelog(Integer id) {
        return getGoodsByCatelog(id, null, null);
    }

    @Override
    public void updateGoodsByPrimaryKeyWithBLOBs(int goodsId, Goods goods) {
        goods.setId(goodsId);
        this.goodsMapper.updateByPrimaryKeyWithBLOBs(goods);
    }

    @Override
    public List<Goods> getGoodsByCatelogOrderByDate(Integer catelogId, Integer limit) {
        List<Goods> goodsList = goodsMapper.selectByCatelogOrderByDate(catelogId, limit);
        return goodsList;
    }

    @Override
    public List<Goods> getGoodsByUserId(Integer userId) {
        List<Goods> goodsList = goodsMapper.getGoodsByUserId(userId);
        return goodsList;
    }


    @Override
    public Map<String, Object> getGoodsByCatelogIdAndNameAndDescrible(
            int pageNum, int pageSize, int catelogId,
            String name, String describle) {
        Map<String, Object> data = new HashMap<>();
        int count = goodsMapper.selectCountByCatelog(catelogId, name, describle);
        if (count == 0) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", 1);
            data.put("totalPageSize", 0);
            data.put("goodsList", new ArrayList<>());
            return data;
        }
        int totalPageNum = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        if (pageNum > totalPageNum) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", totalPageNum);
            data.put("totalPageSize", 0);
            data.put("goodsList", new ArrayList<>());
            return data;
        }
        //TODO::分页插件bug
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> goodsList = goodsMapper.selectByCatelog(catelogId, name, describle);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("totalPageNum", totalPageNum);
        data.put("totalPageSize", count);
        data.put("goodsList", goodsList);
        return data;
    }

    @Override
    public ResponseResult searchGoods(String info) {

        //搜索相似分类
        List<Goods> goodsByName = goodsMapper.searchByName(info);
        //搜索相似名称
        List<Goods> goodsByCategory = goodsMapper.searchByCategory(info);

        HashMap<Object, Object> resultMap = new HashMap<>();
        goodsByCategory.stream().peek(goodsByName::add).collect(Collectors.toList());

//        resultMap.put("category","搜索结果");
//        resultMap.put("data",goodsByName.size() == 0? Collections.singletonList("没有找到任何商品，换个名字试试吧") :goodsByName);
//        resultMap.put("data",goodsByName);
        //将结果封装到集合中返回
        return ResponseResult.okResult(goodsByName);

    }

    @Override
    @Transactional
    public ResponseResult uploadGoods(Goods goods, MultipartFile[] file) {
        //插入之前设置用户信息
        Integer userId = null;
        //如果这里出了异常，说明没登录
        try{
            userId = SecurityUtils.getUserId();
        }catch (Exception e){
            System.out.println("请先登录！");
            throw new SystemException(SystemConstant.NEED_LOGIN,"请先登录！");
        }

        goods.setUserId(userId);
        goods.setPolishTime(String.valueOf(LocalDate.now()));
        goods.setStartTime(String.valueOf(LocalDate.now()));

        goodsMapper.insert(goods);

        System.out.println(goods);
        List<String> imgList = new ArrayList<>();
        //上传图片，得到图片的url，先将图片插入进去
        for (MultipartFile multipartFile : file) {
            try {
                String imgUrl = fileUtils.updateFile(multipartFile.getInputStream(),
                        multipartFile.getOriginalFilename());
                imgList.add(imgUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (String s : imgList) {
            imageService.insert(new Image(goods.getId(), s));
        }
        return ResponseResult.okResult();
    }

}
