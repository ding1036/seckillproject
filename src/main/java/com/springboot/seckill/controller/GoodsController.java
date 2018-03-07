package com.springboot.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.service.RecKillUserService;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	RecKillUserService userService;
	
	@Autowired
	RedisService redisService;
	
    @RequestMapping("/to_list")
    public String list(Model model,SecKillUser user) {
    	model.addAttribute("user", user);
        return "goods_list";
    }
    
}
