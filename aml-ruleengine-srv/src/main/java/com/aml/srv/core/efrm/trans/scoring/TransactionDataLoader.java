package com.aml.srv.core.efrm.trans.scoring;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.efrm.rt.srv.core.recordDTO.MapperSummarizationFiledDTO;

@Component
public class TransactionDataLoader {


	public static final Logger LOGGER = LoggerFactory.getLogger(TransactionDataLoader.class);
	
	@Value("${spring.jpa.properties.hibernate.default_schema:amlschema}")
	private String schemaName;
	
	private final JdbcTemplate jdbcTemplate;

	public TransactionDataLoader(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public float getDataFromDynamicQueryTrans(MapperSummarizationFiledDTO mappsum, String whereClauseParam) {
		LOGGER.info("getDataFromDynamicQueryTrans Method Called..............");
		List<Map<String, Object>> rows = null;
		List<String> columns = null;
		float arr = 0.0f;
		try {
			if (mappsum != null) {
				if(StringUtils.isNotBlank(mappsum.getColumnName()) && StringUtils.isNotBlank(mappsum.getTableName())) {
					columns = Arrays.asList(mappsum.getColumnName());
					//String sql = "SELECT " + mappsum.getColumnName() + " FROM amlschema." + mappsum.getTableName();
					StringBuilder sql = new StringBuilder("SELECT " + mappsum.getColumnName() + " FROM " + schemaName + "." + mappsum.getTableName());
			        if(StringUtils.isNotBlank(whereClauseParam)) {
						sql.append(" where " + mappsum.getWhereClauseCloumn() + " = '" + whereClauseParam + "'");
			        }
					LOGGER.info("getDataFromDynamicQueryTrans SQL : [{}]", sql);
					try {
						boolean connetSts = hasConnection();
						LOGGER.info("checkCustomerDataHaveRNt connetSts : [{}]", connetSts);
						rows = jdbcTemplate.queryForList(sql.toString());
					} catch (Exception e) {
						LOGGER.error("Exception e : {}", e);
					}
					if ( rows!= null && !rows.isEmpty() && rows.size() > 0) {
						LOGGER.info("[IF] Query return rows");
						for (Map<String, Object> row : rows) {
							ArrayList<Float> tem = new ArrayList<>();
							for (String col : columns) {
								Object value = row.get(col);
								Optional<Object> maybeName = Optional.ofNullable(value);
								//LOGGER.info("Table Name : {} and Column Name :" + col + " = " + value, mappsum.getTableName());
								if (StringUtils.isNotBlank(col) && !maybeName.isEmpty()) {
									LOGGER.debug("Column Name : [{}] and Column Value : {} ", col, maybeName.get());
									tem = toCheckAndParse(value);
								} else {
									tem.add(0.0f);
								}
							}
							for (int i = 0; i < tem.size(); i++) { arr = tem.get(i); }
						}
						LOGGER.info("FLoat Value from Transaction column {} : {}",mappsum.getColumnName(), arr);
						
					} else {
						LOGGER.info("[ELSE] Query return No Rows");
						arr = 0.0f;
					}
				} else {
					LOGGER.info("[ELSE] Column Not Mapped with customer tables...");
					arr = 0.0f;
				}
			} else {
				LOGGER.info("MapperSummarizationFiledDTO object is null.");
				arr = 0.0f;
			}
		} catch (Exception e) {
			LOGGER.error("Exxception found in CustomerDetailsMLUse@getDataFromDynamicQueryTrans : {}", e);
			arr = 0.0f;
		} finally {
			rows = null; columns = null;
			LOGGER.info("checkCustomerDataHaveRNt Method End..............\n");
		}
		return arr;
	}
	public boolean hasConnection() {
	    try {
	        // Run a trivial query
	        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
	        return result != null && result == 1;
	    } catch (Exception e) {
	        // If we get here, connection failed
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public ArrayList<Float> toCheckAndParse(Object value){
		ArrayList<Float> tem = new ArrayList<>();
		try {
			if (value instanceof Long) {
				float f = ((Long) value).floatValue(); // convert properly
				tem.add(f);
			} else if (value instanceof Float) {
				tem.add((Float) value);
			} else if (value instanceof Integer) {
				tem.add((Float) value);
			} else if (value instanceof BigInteger) {
				tem.add(((BigInteger) value).floatValue());
			}  else if (value instanceof Double) {
				tem.add(((Double) value).floatValue());
			} else if (value instanceof Double) {
				tem.add(((Double) value).floatValue());
			} else {
				tem.add(0.0f);
			}
		} catch (Exception e) {
			
		} finally {
			
		}
		return tem;
	}
}
