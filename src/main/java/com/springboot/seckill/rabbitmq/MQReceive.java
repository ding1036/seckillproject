package com.springboot.seckill.rabbitmq;

import com.springboot.seckill.domain.SecKillOrder;
import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.service.GoodsService;
import com.springboot.seckill.service.OrderService;
import com.springboot.seckill.service.SeckillService;
import com.springboot.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceive {

    @Autowired
    GoodsService goodsService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    private static Logger log = LoggerFactory.getLogger(MQReceive.class);

   /* @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){
        log.info("receive topic q1 message:"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message){
        log.info("receive topic q2 message:"+message);
    }

    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void receiveHeaders(byte[] message){
        log.info("receive header queue message:"+new String(message));
    }*/
    @RabbitListener(queues = MQConfig.SECKILLQUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
        SeckillMessage seckillMessage= RedisService.stringToBean(message,SeckillMessage.class);
        SecKillUser secKillUser = seckillMessage.getSecKillUser();
        long goodsId = seckillMessage.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if(stock<0){
            return ;
        }
        SecKillOrder order =  orderService.getSeckillOrderByUserIdGoodsId(secKillUser.getId(),goodsId);
        if(order != null){
            return;
        }
        seckillService.seckill(secKillUser,goods);

    }
}
