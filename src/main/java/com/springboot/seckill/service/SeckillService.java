package com.springboot.seckill.service;

import com.springboot.seckill.domain.OrderInfo;
import com.springboot.seckill.domain.SecKillOrder;
import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.redis.SecKillKey;
import com.springboot.seckill.util.MD5Util;
import com.springboot.seckill.util.UUIDUtil;
import com.springboot.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;
    @Transactional
    public OrderInfo seckill(SecKillUser user, GoodsVo goods) {

        boolean success = goodsService.reduceStock(goods);
        if(success){
            return orderService.createOrder(user,goods);
        }else{
            setGoodsOver(goods.getId());
            return null;
        }

    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SecKillKey.isGoodsOver,""+goodsId,true);
    }

    public long getSeckillResult(Long userId, long goodsId) {
        SecKillOrder secKillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId,goodsId);
        if(secKillOrder != null) {
            return secKillOrder.getGoodsId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else{
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
       return redisService.exists(SecKillKey.isGoodsOver,""+goodsId);
    }

    public boolean checkPath(SecKillUser user, long goodsId, String path) {
        if(user==null||path==null){
            return false;
        }
        String pathOld = redisService.get(SecKillKey.getSeckillPath,""+user.getId()+"_"+goodsId,String.class);
        return path.equals(pathOld);
    }

    public String createSeckillPath(SecKillUser user,long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SecKillKey.getSeckillPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }

    public BufferedImage createSeckillVerifyCode(SecKillUser user, long goodsId) {
        if(user==null||goodsId <=0){
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //store verify code in redis中
        int rnd = calc(verifyCode);
        redisService.set(SecKillKey.getSeckillVerifyCode, user.getId()+","+goodsId, rnd);
        //output img
        return image;
    }

    public boolean checkVerifyCode(SecKillUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(SecKillKey.getSeckillVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(SecKillKey.getSeckillVerifyCode, user.getId()+","+goodsId);
        return true;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
}
