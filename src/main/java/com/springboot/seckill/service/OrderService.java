package com.springboot.seckill.service;

import com.springboot.seckill.dao.OrderDao;
import com.springboot.seckill.domain.OrderInfo;
import com.springboot.seckill.domain.SecKillOrder;
import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public SecKillOrder getSeckillOrderByUserIdGoodsId(Long userId, long goodsId) {
        return orderDao.getSeckillOrderByUserIdGoodsId(userId,goodsId);
    }

    @Transactional
    public OrderInfo createOrder(SecKillUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        long orderId = orderDao.insert(orderInfo);
        SecKillOrder secKillOrder = new SecKillOrder();
        secKillOrder.setGoodsId(goods.getId());
        secKillOrder.setOrderId(orderId);
        secKillOrder.setUserId(user.getId());

        orderDao.insertSeckillOrder(secKillOrder);
        return orderInfo;
    }
}
