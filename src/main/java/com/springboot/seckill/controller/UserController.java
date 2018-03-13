package com.springboot.seckill.controller;

import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.result.Result;
import com.springboot.seckill.service.GoodsService;
import com.springboot.seckill.service.SecKillUserService;
import com.springboot.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	SecKillUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;
	
    @RequestMapping("/info")
	@ResponseBody
    public Result<SecKillUser> info(Model model, SecKillUser user) {
        return Result.success(user);
    }


    
}
