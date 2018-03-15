package com.springboot.seckill.config;

import com.springboot.seckill.domain.SecKillUser;

public class UserContext {
    private static ThreadLocal<SecKillUser> userHolder = new ThreadLocal<SecKillUser>();

    public static void setUserHolder(SecKillUser secKillUser){
        userHolder.set(secKillUser);
    }

    public static SecKillUser getSeckillUser(){
        return userHolder.get();
    }
}
