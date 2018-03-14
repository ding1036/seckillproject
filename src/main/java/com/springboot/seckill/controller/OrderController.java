package com.springboot.seckill.controller;

import com.springboot.seckill.domain.OrderInfo;
import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.result.CodeMsg;
import com.springboot.seckill.result.Result;
import com.springboot.seckill.service.GoodsService;
import com.springboot.seckill.service.OrderService;
import com.springboot.seckill.service.SecKillUserService;
import com.springboot.seckill.vo.GoodsVo;
import com.springboot.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	SecKillUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;
	
    @RequestMapping("/detail")
	@ResponseBody
    public Result<OrderDetailVo> info(Model model, SecKillUser user, @RequestParam("orderId")long orderId) {
    	if(user == null){
    		return Result.error(CodeMsg.SESSION_ERROR);
		}
		OrderInfo orderInfo = orderService.getOrderById(orderId);
    	if(orderInfo == null){
    		return Result.error(CodeMsg.ORDDER_NOT_EXIST);
		}
		long goodsId = orderInfo.getGoodsId();
    	GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
    	OrderDetailVo orderDetailVo = new OrderDetailVo();
    	orderDetailVo.setGoodsVo(goodsVo);
    	orderDetailVo.setOrderInfo(orderInfo);
        return Result.success(orderDetailVo);
    }


    
}
