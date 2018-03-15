package com.springboot.seckill.rabbitmq;

import com.springboot.seckill.domain.SecKillUser;

public class SeckillMessage {
    private SecKillUser secKillUser;
    private long goodsId;

    public SecKillUser getSecKillUser() {
        return secKillUser;
    }

    public void setSecKillUser(SecKillUser secKillUser) {
        this.secKillUser = secKillUser;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
