package com.springboot.seckill.service;

import com.springboot.seckill.domain.OrderInfo;
import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;
    @Transactional
    public OrderInfo seckill(SecKillUser user, GoodsVo goods) {
        goodsService.reduceStock(goods);

        OrderInfo orderInfo = orderService.createOrder(user,goods);

        return orderInfo;
    }
}
