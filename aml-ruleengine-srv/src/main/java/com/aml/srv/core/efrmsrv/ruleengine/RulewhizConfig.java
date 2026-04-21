package com.aml.srv.core.efrmsrv.ruleengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.aml.srv.core.efrm.cust.scoring.RedisService;
import com.aml.srv.core.efrmsrv.entity.NormalizedTblEntity;
import com.aml.srv.core.efrmsrv.repo.NormalizedTblImpl;
import com.google.gson.Gson;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
@EnableScheduling
public class RulewhizConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(RulewhizConfig.class);
	private static final Gson GSON = new Gson();
	public List<NormalizedTblEntity> ruleEntity = new ArrayList<NormalizedTblEntity>();
	public HashMap<String, String> ruleMvel = new HashMap<String, String>();
	public HashMap<String, String> ruleJson = new HashMap<String, String>();
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Autowired
	LettuceConnectionFactory connectionFactory;
	 
	@Autowired
	RedisService redisService;
	
	@Autowired
	private NormalizedTblImpl normalizedTblImpl;
	

	@PostConstruct
	public void loadConfigs() {
		LOGGER.info("--------------STARTING-----------------------");
		loadRulesConfig();
		generateMVEL();
	}

	public void loadRulesConfig() {
		LOGGER.info("Loading Rules.....");
		ruleEntity = normalizedTblImpl.getActiveRules();
		LOGGER.info("[{}] - No. Of Rules are Loaded...", ruleEntity.size());
	}
	

	@PostConstruct
	public void startJob() {
		executor.scheduleAtFixedRate(() -> {
			try {
				// Example: read/write to Redis
				redisService.setValue("heartbeat", System.currentTimeMillis());
				var cfg = connectionFactory.getStandaloneConfiguration();
				if (cfg != null) {
					LOGGER.warn("Redis host = " + cfg.getHostName() + ", port = " + cfg.getPort());
				}
				LOGGER.warn("Redis heartbeat updated....");
			} catch (Exception e) {
				// Handle gracefully if Redis is unavailable
				LOGGER.error("Redis job failed: " + e.getMessage());
			}
		}, 0, 1, TimeUnit.MINUTES);
	}

	@PreDestroy
	public void stopJob() { // Cleanly shut down when Spring context closes
		executor.shutdownNow();
		LOGGER.warn("Redis scheduled job stopped");
	}

	/**
	 * 
	 */
	public void generateMVEL() {
		for (NormalizedTblEntity entity : ruleEntity) {
			LOGGER.info("Rule Details are ID: [{}] - Rule Name : [{}] - Payload : [{}]", entity.getId(),
					entity.getRuleName(), entity.getPayload());
			ruleJson.put(entity.getId(), entity.getPayload());
			String MVELExpression = processMVEL(entity.getPayload());
			ruleMvel.put(entity.getId(), MVELExpression);
			LOGGER.info("From RULE Config - [PUT] MVELExpression - [{}] : [{}]", entity.getId(), MVELExpression);
		}
	}

	/**
	 * 
	 * @param payload
	 * @return
	 */
	public String processMVEL(String payload) {
		StringBuilder sb = new StringBuilder();
		AMLRule rulepayload = GSON.fromJson(payload, AMLRule.class);
		int schemaCounter = 0;
		int funcCounter = 0;
		boolean schemaFlag = false;
		for (Schema schema : rulepayload.getSchema()) {
			schemaCounter += 1;
			String schemaTag = schema.getTag();
			if (schemaCounter == 1) {
				sb.append(" (");
			}
			if (schema.getCondition().equalsIgnoreCase("between")) {
				LOGGER.trace("::::::::::::::::>>>IF - SCHEMA - CONDITION : {}", schema.getCondition());
				schemaFlag = true;
				sb.append(" (");
				sb.append(schemaTag);
				String[] valuelist = schema.getValue().split(",");
				sb.append(getExpression("greater_than_equal"));
				// sb.append(valuelist[0]);
				sb.append(toCheckKeyName(schemaTag, valuelist[0]));
				sb.append(" " + getExpression(schema.getJoinexpression()));
				sb.append(" " + schemaTag);
				sb.append(getExpression("lesser_than_equal"));
				// sb.append(valuelist[1]);
				sb.append(toCheckKeyName(schemaTag, valuelist[1]));
				sb.append(")");
				LOGGER.trace("::::::::::::::::>>>IF - SCHEMA - SB : {}", sb.toString());
			} else {
				LOGGER.trace("::::::::::::::::>>>ELSE- SCHEMA - CONDITION : {}", schema.getCondition());
				schemaFlag = true;
				sb.append(" " + schemaTag);
				sb.append(getExpression(schema.getCondition()));
				// sb.append(schema.getValue());
				sb.append(toCheckKeyName(schemaTag, schema.getValue()));
			}
			if (!(schemaCounter == rulepayload.getSchema().size())) {
				sb.append(" " + getExpression(schema.getJoinexpression()));
			}
			
		}
		if (schemaFlag)
			sb.append(") ");
		
		if (rulepayload.getFunc().size() > 0) {
			if (schemaFlag) {sb.append(" " + getExpression("AND")); }
		}
		for (Func func : rulepayload.getFunc()) {
			String funTag = func.getFact().toLowerCase()+"_"+func.getTag();
			funcCounter += 1;
			if (funcCounter == 1) {
				sb.append(" (");
			}
			LOGGER.trace("::::::::::::::::>>>FACT - Operator : [{}]", func.getOperator());
			if ("between".equals(func.getOperator())) {
				sb.append(" (");
				sb.append(funTag);
				String[] valuelist = func.getValue().split(",");
				sb.append(getExpression("greater_than_equal"));
				// sb.append(valuelist[0]);
				sb.append(toCheckKeyName(funTag, valuelist[0]));
				sb.append(" " + getExpression(func.getJoinexpression()));
				sb.append(" " + funTag);
				sb.append(getExpression("lesser_than_equal"));
				// sb.append(valuelist[1]);
				sb.append(toCheckKeyName(funTag, valuelist[1]));
				sb.append(")");
			} else {
				sb.append(" " + funTag);
				sb.append(getExpression(func.getOperator()));
				// sb.append("\""+func.getValue()+"\"");
				sb.append(toCheckKeyName(funTag, func.getValue()));
			}

			if (!(funcCounter == rulepayload.getFunc().size())) {
				sb.append(" " + getExpression(func.getJoinexpression()));
			}
		}
		if(funcCounter>=1) {
			sb.append(")");
		}
		return sb.toString().trim();
	}

	/**
	 * 
	 * @param keyName
	 * @param valuee
	 * @return
	 */
	private Object toCheckKeyName(String keyName, Object valuee) {
		if (isValidNumber(valuee)) {
			return valuee;
		} else {
			return "\"" + valuee + "\"";
		}

		/*
		 * 
		 * if (StringUtils.isNotBlank(keyName) &&
		 * keyName.equalsIgnoreCase(RuleWhizConstants.ACCTYPE)) { return "\"" + valuee +
		 * "\""; } else if (StringUtils.isNotBlank(keyName) &&
		 * keyName.equalsIgnoreCase(RuleWhizConstants.DEPOSITEorWITHDRAW)) { if(valuee
		 * instanceof java.lang.String) { if(isValidNumber(valuee)) { return valuee; }
		 * else { return "\"" + valuee + "\""; }
		 * 
		 * } else if(valuee instanceof java.lang.Integer) { return valuee; } else {
		 * return valuee; } } else if (StringUtils.isNotBlank(keyName) &&
		 * keyName.equalsIgnoreCase(RuleWhizConstants.AMOUNT)) { return valuee; } else
		 * if (StringUtils.isNotBlank(keyName) &&
		 * keyName.equalsIgnoreCase(RuleWhizConstants.PANNO)) { return "\"" + valuee +
		 * "\""; } else { return valuee; }
		 */
	}

	public boolean isValidNumber(Object str) {
		return str.toString().matches("\\d+(\\.\\d+)?");
	}

	public String getExpression(String expression) {
		String retVal = "";
		switch (expression) {
		case "equals":
			retVal = MyExpression.equals.getAction();
			break;
		case "AND":
			retVal = MyExpression.AND.getAction();
			break;
		case "greater_than_equal":
			retVal = MyExpression.greaterthanequals.getAction();
			break;
		case "greater_than":
			retVal = MyExpression.greatherthan.getAction();
			break;
		case "lesser_than_equal":
			retVal = MyExpression.lesserthanequals.getAction();
			break;
		case "lesser_than":
			retVal = MyExpression.lesserthan.getAction();
			break;
		default:
			retVal = MyExpression.equals.getAction();
		}
		return retVal;
	}
}