package com.springboot.seckill.service;

import com.springboot.seckill.dao.GoodsDao;
import com.springboot.seckill.domain.Goods;
import com.springboot.seckill.domain.SecKillGoods;
import com.springboot.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.getGoodsVoList();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVo(goodsId);
    }

    public void reduceStock(GoodsVo goods) {
        SecKillGoods secKillGoods = new SecKillGoods();
        secKillGoods.setGoodsId(goods.getId());
        secKillGoods.setStockCount(goods.getStockCount());
        goodsDao.reduceStock(secKillGoods);
    }
}
