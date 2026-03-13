package com.aml.srv.core.efrm.cust.scoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
	
	private static final Logger Logger = LoggerFactory.getLogger(RedisService.class);
	
	private RedisTemplate<String, Object> redisTemplate;

	public RedisService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	public Object toPullObjectFrmRedis(String keyName) {
		try {
			return (Object) redisTemplate.opsForValue().get(keyName);
		} catch (Exception e) {
			Logger.error("Exception found in RedisService@toPullObjectFrmRedis : {}", e);
			return null;
		} finally {

		}
	}
}
