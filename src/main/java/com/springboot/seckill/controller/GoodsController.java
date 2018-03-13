package com.springboot.seckill.controller;

import com.springboot.seckill.service.GoodsService;
import com.springboot.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.service.SecKillUserService;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	SecKillUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;
	
    @RequestMapping("/to_list")
    public String list(Model model, SecKillUser user) {
    	model.addAttribute("user", user);
    	List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList",goodsList);
        return "goods_list";
    }

	@RequestMapping("/to_detail/{goodsid}")
	public String detail(Model model, SecKillUser user,
						 @PathVariable("goodsid")long goodsId) {
    	//snowflake
		model.addAttribute("user", user);
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		long startAt =goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		int seckillStatus = 0;
		int remainSeconds = 0;
		if(now < startAt){
			seckillStatus = 0;
			remainSeconds = (int)((startAt -now)/1000);
		}else if(now>endAt){
			seckillStatus = 2;
			remainSeconds = -1;
		}else{
			seckillStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("seckillStatus",seckillStatus);
		model.addAttribute("remainSeconds",remainSeconds);
		return "goods_detail";
	}
    
}
