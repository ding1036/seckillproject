package com.springboot.seckill.controller;

import com.rabbitmq.client.AMQP;
import com.springboot.seckill.access.AccessLimit;
import com.springboot.seckill.domain.OrderInfo;
import com.springboot.seckill.domain.SecKillOrder;
import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.rabbitmq.MQSender;
import com.springboot.seckill.rabbitmq.SeckillMessage;
import com.springboot.seckill.redis.AccessKey;
import com.springboot.seckill.redis.GoodsKey;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.redis.SecKillKey;
import com.springboot.seckill.result.CodeMsg;
import com.springboot.seckill.result.Result;
import com.springboot.seckill.service.GoodsService;
import com.springboot.seckill.service.OrderService;
import com.springboot.seckill.service.SeckillService;
import com.springboot.seckill.util.MD5Util;
import com.springboot.seckill.util.UUIDUtil;
import com.springboot.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean{
    @Autowired
    GoodsService goodsService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    private Map<Long , Boolean> localOverMap = new HashMap<Long, Boolean>();
   /* @RequestMapping("/do_seckill")
    public String doSeckill(Model model, SecKillUser user,
                            @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user ==null){
            return "login";
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if(stock<0){
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER);
            return "seckill_fail";
        }
        SecKillOrder order =  orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            model.addAttribute("errmsg",CodeMsg.REPEATE_SECKILL);
            return "seckill_fail";
        }
        OrderInfo orderInfo = seckillService.seckill(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
    }*/
    @RequestMapping(value = "/{path}/do_seckill" , method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill( SecKillUser user,
                                     @RequestParam("goodsId")long goodsId,
                                     @PathVariable("path")String path) {
        if (user ==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = seckillService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //memory tag, reduce call redis time
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //reduction in advance
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock,""+goodsId);
        if(stock<0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        SecKillOrder order =  orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //push in queue
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setGoodsId(goodsId);
        seckillMessage.setSecKillUser(user);
        mqSender.sendSeckillMessage(seckillMessage);
        return Result.success(0);// 0 means in queue

      /*  GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if(stock<0){
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        SecKillOrder order =  orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        OrderInfo orderInfo = seckillService.seckill(user,goods);
        return Result.success(orderInfo);*/
    }

    /**
     *
     *orderId success
     * -1 failure
     * 0 in queue
     *
     */
    @AccessLimit(seconds = 5,maxCount =10,needLogin =true)
    @RequestMapping(value = "/result" , method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult( SecKillUser user,
                                     @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = seckillService.getSeckillResult(user.getId(),goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds = 5,maxCount =5,needLogin =true)
    @RequestMapping(value = "/path" , method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(HttpServletRequest request, SecKillUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        String url = request.getRequestURI();
        String key = url +"_"+user.getId();

        boolean check = seckillService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.SECKILL_FAILURE);
        }
        String path = seckillService.createSeckillPath(user,goodsId);

        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode" , method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response, SecKillUser user,
                                                      @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage img = seckillService.createSeckillVerifyCode(user,goodsId);
        try{
            OutputStream out = response.getOutputStream();
            ImageIO.write(img,"JPEG",out);
            out.flush();
            out.close();
            return null;
        }catch (IOException e){
            return Result.error(CodeMsg.SECKILL_FAILURE);
        }
    }

    // system initialize
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null){
            return;
        }
        for (GoodsVo goods:goodsList){
            redisService.set(GoodsKey.getSeckillGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }
}
