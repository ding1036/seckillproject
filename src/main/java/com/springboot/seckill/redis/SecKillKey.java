package com.springboot.seckill.redis;

public class SecKillKey extends BasePrefix{

	private SecKillKey(int expireSeconds, String prefix) {
		super( expireSeconds,prefix);
	}
	public static SecKillKey isGoodsOver = new SecKillKey( 0,"go");
	public static SecKillKey getSeckillPath = new SecKillKey( 60,"sp");
	public static KeyPrefix getSeckillVerifyCode= new SecKillKey( 300,"vc");
}
