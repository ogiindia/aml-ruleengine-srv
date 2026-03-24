package com.aml.srv.core.efrm.parqute.service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.file.pro.core.efrmsrv.startup.config.ColumnMapping;
import com.aml.file.pro.core.efrmsrv.startup.config.TransactionMapping;
import com.google.gson.Gson;

@Service
public class ParquetService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParquetService.class);

	@Autowired
	JsonConfigLoader jsonConfigLoader;

	/**
	 * Get Duck DB Connection
	 * @return
	 */
	public Connection getDuckDbConn() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:duckdb:");
		} catch (Exception e) {
			LOGGER.error("Exception found in ParquetService@getDuckDbConn : {}", e);
		} finally {}
		return conn;
	}
	
	/**
	 * Execute Query to get Entity Class
	 * @param <T>
	 * @param shortName
	 * @param type
	 * @return
	 */
	public <T> List<T> executeQueryReturnEntity(String shortName, Class<T> type, SearchFieldsDTO srcField) {
		LOGGER.info("::::::::::::::executeQueryReturnEntity Method Called::::::::::::::::::");
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData meta = null;
		List<T> result = null;
		try {
			result = new ArrayList<>();
			con = getDuckDbConn();
			String slQry =null;
			if(srcField!=null) {
				slQry = buildSelectQuery(shortName, srcField);
			} else {
				slQry = buildSelectQuery(shortName);
			}
			stmt = con.prepareStatement(slQry);
			rs = stmt.executeQuery();

			if (!rs.next()) {
			    LOGGER.warn("No data found");
			    return Collections.emptyList(); // never return null
			}

			meta = rs.getMetaData();
			int cols = meta.getColumnCount();

			do {
			    T entity = type.getDeclaredConstructor().newInstance();
			    for (int i = 1; i <= cols; i++) {

			        String colName = meta.getColumnLabel(i);
			        Object value = rs.getObject(i);

			        String fieldName = toCamel(colName);
			        try {
			            Field f = type.getDeclaredField(fieldName);
			            f.setAccessible(true);
			            Object converted = convertValue(value, f.getType());
			            f.set(entity, converted);

			        } catch (NoSuchFieldException ignore) {
			            LOGGER.debug("Field not found: {}", fieldName);
			        }
			    }

			    result.add(entity);

			} while (rs.next());
		} catch (Exception e) {
			LOGGER.error("Exception in executeQueryReturnEntity - {}", e);
		} finally {
			try {
				if (con != null) { con.close(); con = null; }
				if (stmt != null) { stmt.close(); stmt = null; }
				if (rs != null) { rs.close(); rs = null; }
				meta = null;
			} catch (Exception ignore) {
			}

			LOGGER.info("::::::::::::::executeQueryReturnEntity Method End::::::::::::::::::");
		}
		return result;
	}

	/**
	 * Puplate Column
	 * @param col
	 * @return
	 */
	private String toCamel(String col) {
	    // SIMPLE example: ACCOUNT_NO -> accountNo
	    String lower = col.toLowerCase();          // account_no
	    String[] parts = lower.split("_");
	    StringBuilder sb = new StringBuilder(parts[0]);
	    for (int i = 1; i < parts.length; i++) {
	        sb.append(Character.toUpperCase(parts[i].charAt(0)))
	          .append(parts[i].substring(1));
	    }
	    return sb.toString();
	}

	/**
	 * 
	 * @param shortName
	 * @return
	 */
	public TransactionCustomFieldRDTO getConfig(String shortName) {
		List<TransactionMapping> transMappLstObj = null;
		TransactionCustomFieldRDTO trancustFldDTOObj = null;
		try {
			transMappLstObj = jsonConfigLoader.getStartUpConfig();
			if (transMappLstObj != null && transMappLstObj.size() > 0) {
				for (TransactionMapping tranMap : transMappLstObj) {
					String json = new Gson().toJson(tranMap);
					LOGGER.debug("----------->> {}", json);
					if (tranMap != null && StringUtils.isNotBlank(tranMap.getShortName())
							&& StringUtils.isNotBlank(shortName)
							&& shortName.equalsIgnoreCase(tranMap.getShortName())) {
						trancustFldDTOObj = new TransactionCustomFieldRDTO(tranMap.getDestFileType(),
								tranMap.getDestLocation(), tranMap.getSourceFileName(), tranMap.getSource(),
								tranMap.getShortName(), tranMap.getColumns());

						break;
					}
				}
			}
		} catch (Exception e) {
			trancustFldDTOObj = null;
			LOGGER.error("Exception found in ParquetService@getConfig : {}", e);
		} finally {transMappLstObj = null; }
		return trancustFldDTOObj;
	}
	
/**
	 * 
	 * @param shortName
	 * @return Select Query
	 */
	public String buildSelectQuery(String shortName, SearchFieldsDTO srcField) {

		try {

			if (shortName == null || shortName.trim().isEmpty()) {
				LOGGER.error("Invalid shortName");
				return null;
			}

			if (srcField == null) {
				LOGGER.error("SearchFieldsDTO is null");
				return null;
			}

			StringBuilder selectQuery = new StringBuilder("SELECT ");

			TransactionCustomFieldRDTO config = getConfig(shortName);

			if (config == null) {
				LOGGER.error("Config is null for shortName={}", shortName);
				return null;
			}

			List<ColumnMapping> columnMappingLst = config.columnMappLstObj();

			if (columnMappingLst == null || columnMappingLst.isEmpty()) {
				LOGGER.error("Column mapping is empty for shortName={}", shortName);
				return null;
			}

			String condition = "";
			List<String> conditions = new ArrayList<>();

			for (int i = 0; i < columnMappingLst.size(); i++) {

				ColumnMapping col = columnMappingLst.get(i);

				if (col.getFrom() == null || col.getTo() == null) {
					LOGGER.warn("Invalid column mapping: {}", col);
					continue;
				}

				selectQuery.append(col.getFrom()).append(" AS ").append(col.getTo());

				if (i < columnMappingLst.size() - 1) {
					selectQuery.append(", ");
				}

				if ("Y".equalsIgnoreCase(col.getSearch())) {

					String criteria = col.getCriteria();
					if (criteria != null && !criteria.isEmpty()) {
						String value = null;
						boolean isValid = false;
						switch (col.getTo().toLowerCase()) {
						case "customerid":
							value = srcField.customerId();
							if (value != null && !value.trim().isEmpty()) {
								criteria = criteria.replace("#customerid#", "'" + value.trim() + "'");
								isValid = true;
							}
							break;

						case "accountno":
							value = srcField.accountNo();
							if (value != null && !value.trim().isEmpty()) {
								criteria = criteria.replace("#accountno#", "'" + value.trim() + "'");
								isValid = true;
							}
							break;

						case "transactionid":
							value = srcField.transId();
							if (value != null && !value.trim().isEmpty()) {
								criteria = criteria.replace("#transactionid#", "'" + value.trim() + "'");
								isValid = true;
							}
							break;

						case "transactiondate":
							if (srcField.startDate() != null && srcField.endDate() != null) {
								criteria = criteria.replaceFirst("#Date#", "'" + srcField.startDate() + "'")
										.replaceFirst("#Date#", "'" + srcField.endDate() + "'");
								isValid = true;
							}
							break;
						}

						if (isValid && !criteria.contains("#")) {
							conditions.add(criteria);
						}
					}
				}
			}

			if (!conditions.isEmpty()) {
				condition = " WHERE " + String.join(" AND ", conditions);
			}

			String parquetPath = tofindParqutePat(config);

			if (parquetPath == null || parquetPath.trim().isEmpty()) {
				LOGGER.error("Parquet path is null/empty for shortName={}", shortName);
				return null;
			}

			parquetPath = parquetPath.replace("\\", "/");

			String query = selectQuery + " FROM read_parquet('" + parquetPath + "*/*/*/*.parquet') " + condition;

			LOGGER.debug("Generated query for shortName={}: {}", shortName, query);

			return query;

		} catch (Exception e) {
			LOGGER.error("Exception in buildSelectQuery | shortName={}", shortName, e);
			return null;
		} finally {}
	}
	
	/**
	 * 
	 * @param shortName
	 * @return Select Query
	 */
	public String buildSelectQuery(String shortName) {
		List<ColumnMapping> columnMappingLst = null;
		StringBuilder selectQuery = null;
		TransactionCustomFieldRDTO transacuFildRTO = null;
		String query = null;
		try {
			selectQuery = new StringBuilder("SELECT ");
			transacuFildRTO = getConfig(shortName);

			columnMappingLst = transacuFildRTO.columnMappLstObj();
			int i = 0;
			for (ColumnMapping columMap : columnMappingLst) {
				selectQuery.append(columMap.getFrom()).append(" AS ").append(columMap.getTo());
				if (i < columnMappingLst.size() - 1) {
					selectQuery.append(", ");
				}
				i++;
			}
			String parqutePath	= tofindParqutePat(transacuFildRTO);
			
			query = selectQuery + " FROM read_parquet('" + parqutePath.replace("\\", "/")
					+ "*/*/*/*.parquet' , union_by_name=true)";

		} catch (Exception e) {
			LOGGER.error("Exception found in ParquetService@buildSelectQuery : {}", e);
		} finally {columnMappingLst = null; transacuFildRTO = null;}
		return query;
	}
	
	/**
	 * 
	 * @param trancustFldDTOObj
	 * @return
	 */
	public String tofindParqutePat(TransactionCustomFieldRDTO trancustFldDTOObj) {
		String rtnPath = null;
		String destLocation = null;
		String source = null;
		String shortName = null;
		try {
			 destLocation = trancustFldDTOObj.destLocation();
			 source = trancustFldDTOObj.source();
			 shortName =  trancustFldDTOObj.shortName();
			 if(StringUtils.isNotBlank(destLocation)) {
				// --c:/#ShortName#/#Source#/#year#/#month#/#date#/
				 int idx = destLocation.indexOf("#year#");
				 String patternUpToYear = destLocation.substring(0, idx);
				 // patternUpToYear = "c:/#ShortName#/#Source#/#year#"
				 rtnPath = patternUpToYear
					        .replace("#ShortName#", shortName)
					        .replace("#Source#", source);
			 }
		} catch (Exception e){
			LOGGER.error("Exception found in  ParquetService@tofindParqutePat : {}", e);
		} finally {}
		return rtnPath;
	}
	
	/**
	 * Utils method
	 * @param value
	 * @param target
	 * @return
	 */
	private Object convertValue(Object value, Class<?> target) {
		if (value == null)
			return null;
		if (target.isInstance(value))
			return value;

		// String
		if (target == String.class) {
			return value.toString();
		}

		// Integer / int
		if (target == Integer.class || target == int.class) {
			return ((Number) value).intValue();
		}

		// Long / long
		if (target == Long.class || target == long.class) {
			return ((Number) value).longValue();
		}

		// Double / double
		if (target == Double.class || target == double.class) {
			return ((Number) value).doubleValue();
		}

		// Boolean / boolean
		if (target == Boolean.class || target == boolean.class) {
			if (value instanceof Boolean b) {
				return b;
			}
			if (value instanceof Number n) {
				return n.intValue() != 0;
			}
			String s = value.toString().trim().toLowerCase();
			return "true".equals(s) || "y".equals(s) || "yes".equals(s) || "1".equals(s);
		}

		// java.sql.Timestamp
		if (target == java.sql.Timestamp.class) {
			if (value instanceof java.sql.Timestamp ts) {
				return ts;
			}
			if (value instanceof java.util.Date d) {
				return new java.sql.Timestamp(d.getTime());
			}
		}

		// java.time.LocalDate
		if (target == java.time.LocalDate.class) {
			if (value instanceof java.sql.Date d) {
				return d.toLocalDate();
			}
			if (value instanceof java.sql.Timestamp ts) {
				return ts.toLocalDateTime().toLocalDate();
			}
			// parse from String (e.g. "2024-03-21")
			return java.time.LocalDate.parse(value.toString());
		}

		// java.math.BigDecimal
		if (target == java.math.BigDecimal.class) {
			if (value instanceof java.math.BigDecimal bd) {
				return bd;
			}
			if (value instanceof Number n) {
				return java.math.BigDecimal.valueOf(n.doubleValue());
			}
			return new java.math.BigDecimal(value.toString());
		}
		
		// Float / float
		if (target == Float.class || target == float.class) {
		    return ((Number) value).floatValue();
		}

		// Short / short
		if (target == Short.class || target == short.class) {
		    return ((Number) value).shortValue();
		}

		// Byte / byte
		if (target == Byte.class || target == byte.class) {
		    return ((Number) value).byteValue();
		}

		// java.time.LocalDateTime
		if (target == java.time.LocalDateTime.class) {
		    if (value instanceof java.sql.Timestamp ts) {
		        return ts.toLocalDateTime();
		    }
		    if (value instanceof java.util.Date d) {
		        return java.time.LocalDateTime.ofInstant(
		                d.toInstant(), java.time.ZoneId.systemDefault());
		    }
		    return java.time.LocalDateTime.parse(value.toString());
		}

		// java.time.LocalTime
		if (target == java.time.LocalTime.class) {
		    if (value instanceof java.sql.Time t) {
		        return t.toLocalTime();
		    }
		    return java.time.LocalTime.parse(value.toString());
		}

		// java.util.Date
		if (target == java.util.Date.class) {
		    if (value instanceof java.util.Date d) {
		        return d;
		    }
		    if (value instanceof java.sql.Timestamp ts) {
		        return new java.util.Date(ts.getTime());
		    }
		}

		// fallback: return as-is
		return value;
	}
}
