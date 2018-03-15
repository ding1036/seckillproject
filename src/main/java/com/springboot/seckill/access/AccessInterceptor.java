package com.springboot.seckill.access;

import com.alibaba.fastjson.JSON;
import com.springboot.seckill.config.UserContext;
import com.springboot.seckill.domain.SecKillUser;
import com.springboot.seckill.redis.AccessKey;
import com.springboot.seckill.redis.RedisService;
import com.springboot.seckill.result.CodeMsg;
import com.springboot.seckill.result.Result;
import com.springboot.seckill.service.SecKillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter{

    @Autowired
    SecKillUserService secKillUserService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            SecKillUser user = getUser(request,response);
            UserContext.setUserHolder(user);

            HandlerMethod hm = (HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit ==null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin){
                if (user ==null) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key +="_"+user.getId();
            }else{
                //do nothing
            }

            Integer count = redisService.get(AccessKey.withExpire(seconds),""+key,Integer.class);
            if(count == null){
                redisService.set(AccessKey.withExpire(seconds),""+key,1);
            }else if(count<maxCount){
                redisService.incr(AccessKey.withExpire(seconds),""+key);
            }else{
                render(response,CodeMsg.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg sessionError) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(Result.error(sessionError));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private SecKillUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(SecKillUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SecKillUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return secKillUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
