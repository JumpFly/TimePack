package com.server.Tools;

import com.common.Tools.configUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {
	private RedisUtils(){}
	private static  String RedisHost=null;
	private static  int RedisPort;
	private static volatile JedisPool jedisPool=null;
	static {
		RedisHost=configUtils.getRedisAddr();
		RedisPort=Integer.parseInt(configUtils.getRedisPort());
		JedisPoolConfig poolConfig = new JedisPoolConfig();
	    poolConfig.setMaxTotal(100);
	    poolConfig.setBlockWhenExhausted(false);
	    poolConfig.setMaxIdle(20);
	    poolConfig.setMinEvictableIdleTimeMillis(1800000);
	    poolConfig.setTestOnBorrow(true);
		jedisPool=new JedisPool(poolConfig, RedisHost, RedisPort);
	}
	
	public static Jedis getPoolConnection(){
		return jedisPool.getResource();
	}
	public static void ReleaseConn(Jedis jedisconn){
		if(jedisconn!=null)
			jedisconn.close();
	}
	

}