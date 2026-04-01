package com.aml.srv.core.efrm.parquet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.file.pro.core.efrmsrv.startup.config.TransactionMapping;
import com.aml.srv.core.efrm.cust.scoring.RedisService;

@Service
public class JsonConfigLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonConfigLoader.class);
	
	@Autowired
	RedisService redisService;
		
	@SuppressWarnings("unchecked")
	public List<TransactionMapping> getStartUpConfig() {
		List<TransactionMapping> trnMapLst = null;
		try {
			trnMapLst = (List<TransactionMapping>) redisService.toPullObjectFrmRedis("LDCONFIG");
			LOGGER.info("getStartUpConfig method trnMapLst : {}", trnMapLst);
		} catch (Exception e) {
			LOGGER.info("Exception foud in getStartUpConfig Method : {}",e);
		}
		return trnMapLst;
	}
}
