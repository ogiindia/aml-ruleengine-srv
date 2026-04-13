package com.aml.srv.core.efrm.cust.scoring;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.efrm.rt.srv.core.common.repo.Dynamic_VO;

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
	
	public List<Dynamic_VO> toPullListIntoRedis(String keyName) {
		try {
			List<Dynamic_VO> list = (List<Dynamic_VO>) redisTemplate.opsForValue().get(keyName);
			return list;
		} catch (Exception e) {
			Logger.error("Exception found in RedisService@toPullListIntoRedis : {}", e);
			return null;
		} finally {

		}
	}
	
	public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public String toPushListIntoRedis(String keyName, Object objParam) {
		try {

			redisTemplate.opsForValue().set(keyName, objParam);

		} catch (Exception e) {
			Logger.error("Exception found in RedisService@toPushListIntoRedis : {}", e);
		} finally {

		}
		return "SUCCESS";
	}
}
