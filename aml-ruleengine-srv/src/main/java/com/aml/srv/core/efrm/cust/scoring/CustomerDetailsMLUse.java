package com.aml.srv.core.efrm.cust.scoring;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.repo.MapperImpl;
import com.aml.srv.core.efrmsrv.repo.RiskParamRepository;
import com.aml.srv.core.efrmsrv.repo.RiskValueImpl;
import com.aml.srv.core.efrmsrv.repo.RiskValuesRepository;
import com.efrm.rt.srv.core.recordDTO.MapperSummarizationFiledDTO;

@Component
public class CustomerDetailsMLUse {
	public static final Logger LOGGER = LoggerFactory.getLogger(CustomerDetailsMLUse.class);

	@Autowired
	MapperImpl mapperImpl;

	@Autowired
	RiskParamRepository<T> riskParamRepository;

	@Autowired
	RiskValuesRepository<T> riskValuesRepository;

	@Autowired
	RiskValueImpl riskValueImpl;

	private final JdbcTemplate jdbcTemplate;

	@Value("${spring.jpa.properties.hibernate.default_schema:amlschema}")
	private String schemaName;

	public CustomerDetailsMLUse(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * To check and mapp customer data
	 * 
	 * @param result
	 */
	public List<Map<String, Collection<String>>> getCustomerData(Map<String, List<String>> result,
			List<Map<String, Collection<String>>> samples, String custId) {
		LOGGER.info("Step 2 : From Risk param to fetch relevant Customer data.......START");
		try {
			AtomicReference<String> lastKey = new AtomicReference<>();
			Map<String, Collection<String>> mapObj = new HashMap<>();
			result.forEach((key, value) -> {
				List<String> flsdLst = null;
				flsdLst = new ArrayList<>();
				if (!key.equalsIgnoreCase(lastKey.get())) {

					for (String fieldName : value) {
						// LOGGER.info("fieldName : [{}] - Start.....", fieldName);
						MapperSummarizationFiledDTO mappsum = getTableColumnName(key, fieldName);
						Object isAvali = checkCustomerDataHaveRNt(mappsum, custId);
						Optional<Object> maybeName = Optional.ofNullable(isAvali);
						if (!maybeName.isEmpty() && maybeName.isPresent()) {
							LOGGER.debug("From Customer data [IF]: key : [{}] and fieldName : [{}] is available : [{}]",
									key, fieldName, isAvali);
							// flsdLst.add(fieldName);
							if (maybeName.get() instanceof String) {
								flsdLst.add((String) maybeName.get());
							} else if (maybeName.get() instanceof Integer) {
								flsdLst.add(String.valueOf(maybeName.get()));
							} else if (maybeName.get() instanceof BigDecimal) {
								flsdLst.add(String.valueOf(maybeName.get()));
							} else if (maybeName.get() instanceof Boolean) {
								flsdLst.add(String.valueOf(maybeName.get()));
							} else {
								LOGGER.info("Unknown type: " + maybeName.get().getClass().getName());
								flsdLst.add(String.valueOf(maybeName.get()));
							}
							lastKey.set(key);
							break;
						} else {
							LOGGER.debug( "From Customer data [ELSE]: key : [{}] and fieldName : [{}] is available Not added.: [{}]",
									key, fieldName, isAvali);
						}
						LOGGER.info("fieldName : [{}] END.....\n", fieldName);
					}
					if (flsdLst != null && flsdLst.size() > 0) {
						List<String> uniqueList = flsdLst.stream().distinct().collect(Collectors.toList());
						mapObj.put(key, uniqueList);
					}
				} else {

				}
			});
			if (mapObj != null && mapObj.size() > 0) {
				samples.add(mapObj);
			}
			LOGGER.info("Step 3 : sample Customer Synthatic Data : [{}]", samples);

		} catch (Exception e) {
			LOGGER.error("Step 3: Exxception found in CustomerDetailsMLUse@getCustomerData : {}", e);
		} finally {
		}
		return samples;
	}

	/**
	 * 
	 * @param key
	 * @param fieldName
	 * @return
	 */
	private MapperSummarizationFiledDTO getTableColumnName(String key, String fieldName) {
		LOGGER.info("CustomerDetailsMLUse@getTableColumnName Method Called............");
		Optional<MapperSummarizationFiledDTO> mapperSummdtdo = null;
		MapperSummarizationFiledDTO mappSUdto = null;
		try {
			//LOGGER.info("Get TableColumn Details getTableColumnName : KY-[{}] and VL-[{}]", key, fieldName);
			mapperSummdtdo = mapperImpl.getMapperBySourceName(key, fieldName);
			if (!mapperSummdtdo.isEmpty() && mapperSummdtdo.isPresent()) {
				mappSUdto = mapperSummdtdo.get();
				//LOGGER.debug("Table name : [{}] - Column Name : [{}]", mappSUdto.getTableName(), mappSUdto.getColumnName());
			} else {
				mappSUdto = null;
			}

		} catch (Exception e) {
			LOGGER.error("Exxception found in CustomerDetailsMLUse@getTableColumnName : {}", e);
		} finally {
			LOGGER.info("CustomerDetailsMLUse@getTableColumnName Method End............");
		}
		return mappSUdto;
	}

	/**
	 * to be check data is there or not in table
	 * 
	 * @param mappsum
	 */
	private Object checkCustomerDataHaveRNt(MapperSummarizationFiledDTO mappsum, String whereClauseParam) {
		LOGGER.info("checkCustomerDataHaveRNt Method Called..............");
		List<Map<String, Object>> rows = null;
		List<String> columns = null;
		// Boolean isVailableData = false;
		Object rtnVaalue = null;
		try {
			if (mappsum != null) {
				if (StringUtils.isNotBlank(mappsum.getColumnName()) && StringUtils.isNotBlank(mappsum.getTableName())) {
					if(mappsum.getColumnName().contains(",")) {
						columns = Arrays.asList(mappsum.getColumnName().split(","));
					} else {
						columns = Arrays.asList(mappsum.getColumnName());
					}
					StringBuilder sql = new StringBuilder("SELECT " + mappsum.getColumnName() + " FROM " + schemaName + "." + mappsum.getTableName());
					LOGGER.info("checkCustomerDataHaveRNt SQL : [{}]", sql);
					try {
						boolean connetSts = hasConnection();
						LOGGER.info("checkCustomerDataHaveRNt connetSts : [{}]", connetSts);
						rows = jdbcTemplate.queryForList(sql.toString());
					} catch (Exception e) {
						LOGGER.error("Exception e : {}", e);
					}
					if (StringUtils.isNotBlank(whereClauseParam)) {
						sql.append(" where " + mappsum.getWhereClauseCloumn() + " = '" + whereClauseParam + "'");
					}
					if(StringUtils.isNotBlank(mappsum.getTableName()) && mappsum.getTableName().equalsIgnoreCase("FS_SUMMARIZED_DATA")) {
						if (rows != null && !rows.isEmpty() && rows.size() > 0) {
							for (Map<String, Object> row : rows) {
								Object value = null;
								if (columns != null && columns.size() > 1) {
									value = row.get(columns.get(1));
								} else {
									if (columns != null && columns.size() > 0) {
										value = row.get(columns.get(0));
									} else {value = null;}
								}
								rtnVaalue = value;
							}
						} else { rtnVaalue = null; }
					} else {
						if (rows != null && !rows.isEmpty() && rows.size() > 0) {
							LOGGER.info("[IF] Query return rows");
							for (Map<String, Object> row : rows) {
								for (String col : columns) {
									Object value = row.get(col);
									Optional<Object> maybeName = Optional.ofNullable(value);
									// LOGGER.info("Table Name : {} and Column Name :" + col + " = " + value,
									// mappsum.getTableName());
									if (StringUtils.isNotBlank(col) && !maybeName.isEmpty()) {
										//LOGGER.debug("Column Name : [{}] and Column Value : {} ", col, maybeName.get());
										rtnVaalue = value;
									} else { rtnVaalue = null; }
								}
							}
						} else { LOGGER.info("[ELSE] Query return No Rows");
							rtnVaalue = null;
						}
					}
				} else {
					LOGGER.info("[ELSE] Column Not Mapped with customer tables...");
					rtnVaalue = null;
				}

			} else {
				LOGGER.info("MapperSummarizationFiledDTO object is null.");
				rtnVaalue = null;
			}
		} catch (Exception e) {
			rtnVaalue = null;
			LOGGER.error("Exxception found in CustomerDetailsMLUse@getCustomerData : {}", e);
		} finally {
			LOGGER.info("checkCustomerDataHaveRNt Method End..............\n");
		}
		return rtnVaalue;
	}
	
	/**
	 * 
	 * @return
	 */
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
}
