package com.springboot.seckill.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.seckill.dao.SecKillUserDao;
import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.exception.GlobalException;
import com.springboot.seckill.redis.SecKillUserKey;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.result.CodeMsg;
import com.springboot.seckill.util.MD5Util;
import com.springboot.seckill.util.UUIDUtil;
import com.springboot.seckill.vo.LoginVo;

@Service
public class SecKillUserService {
	
	
	public static final String COOKI_NAME_TOKEN = "token";
	
	@Autowired
    SecKillUserDao secKillUserDao;
	
	@Autowired
	RedisService redisService;
	
	public SecKillUser getById(long id) {
		SecKillUser user = redisService.get(SecKillUserKey.getById,""+id,SecKillUser.class);
		if(user!=null){
			return user;
		}
		user = secKillUserDao.getById(id);
		if(user!=null){
			redisService.set(SecKillUserKey.getById,""+id,SecKillUser.class);
		}
		return user;
	}

	public boolean updatePassword(long id,String passwordNew,String token){
		SecKillUser user = getById(id);
		if(user != null){
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		SecKillUser toBeUpdate = new SecKillUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew,user.getSalt()));
		secKillUserDao.update(toBeUpdate);
		redisService.delete(SecKillUserKey.getById,""+id);
		user.setPassword(toBeUpdate.getPassword());
		redisService.set(SecKillUserKey.token,token,user);
		return true;
	}
	

	public SecKillUser getByToken(HttpServletResponse response, String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		SecKillUser user = redisService.get(SecKillUserKey.token, token, SecKillUser.class);
		//extend exist time
		if(user != null) {
			addCookie(response, token, user);
		}
		return user;
	}
	

	public String login(HttpServletResponse response, LoginVo loginVo) {
		if(loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		//check phone
		SecKillUser user = getById(Long.parseLong(mobile));
		if(user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//check password
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
		if(!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		//generate cookie
		String token = UUIDUtil.uuid();
		addCookie(response, token, user);
		return token;
	}
	
	private void addCookie(HttpServletResponse response, String token, SecKillUser user) {
		redisService.set(SecKillUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
		cookie.setMaxAge(SecKillUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}

}
