package com.aml.srv.core.efrm.parquet.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aml.file.pro.core.efrmsrv.startup.config.ColumnMapping;
import com.aml.srv.core.efrm.parquet.entity.TransactionParquetMppaing;
import com.aml.srv.core.efrmsrv.repo.TransactionDetailsDTO;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.rule.service.PercentageDetailsVO;
import com.aml.srv.core.efrmsrv.rule.service.RulesUtils;
import com.aml.srv.core.efrmsrv.ruleengine.Schema;
import com.aml.srv.core.efrmsrv.utils.AMLConstants;
import com.aml.srv.core.efrmsrv.utils.DateFormatUtils;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

/**
 * Transaction Service For Fetch Data from Parqute Service
 */
@Component
public class TransactionServiceForParqute {

	private Logger LOGGER = LoggerFactory.getLogger(TransactionServiceForParqute.class);
	
	@Autowired
	ParquetService parquetService;
	
	@Autowired
	DateFormatUtils dateformatUtils;
	
	@Autowired
	RulesUtils rulesUtils;
	
	 @Value("${aml.rule.deployed.country:IN}")
	private String amlDeployedCountryCode;
	
	 
	/**
	  * 
	  * @param days
	  * @return
	  */
	public List<TransactionParquetMppaing> getTransDetailsFromProperty(Integer days) {
		List<TransactionParquetMppaing> trasParMapLst = null;
		String todayStr = null;
		String startDateStr = null;
		String dateformat = null;
		try {
			dateformat = getTransactionDateFormat();
			LocalDate currentDateTdy = LocalDate.now();
			LocalDate stDate = currentDateTdy.minusDays(days);
			todayStr = dateformatUtils.chageDateFormatLocalDate(dateformat, currentDateTdy); // yyyy-MM-dd
			startDateStr = dateformatUtils.chageDateFormatLocalDate(dateformat, stDate);
			SearchFieldsDTO srchDto = new SearchFieldsDTO(null, null, startDateStr, todayStr, null, null, null, null,
					null, null, null, null, null,null,null,null,null,null);
						
			String yearStr  = String.valueOf(stDate.getYear());
			String monthStr = String.format("%02d", stDate.getMonthValue());
			String dateStr  = String.format("%02d", stDate.getDayOfMonth());
			String parqutePath = yearStr + "/" + monthStr + "/" + dateStr; // - */*/*
			trasParMapLst = parquetService.executeQueryReturnEntityWithPath(RuleWhizConstants.TRANSACTIONS,
					TransactionParquetMppaing.class, srchDto, parqutePath);

		} catch (Exception e) {
			LOGGER.error("Exception found in TransactionServiceForParqute@getTransDetailsFromProperty : {}", e);
		} finally {
		}
		return trasParMapLst;
	}
	
	/**
	 * 
	 * @param days
	 * @param transID
	 * @return
	 */
	public List<TransactionParquetMppaing> getTransDetailsFromParquteFromTrnsid(Integer days, String transID) {
		List<TransactionParquetMppaing> trasParMapLst = null;
		String todayStr = null;
		String startDateStr = null;
		String dateformat = null;
		try {
			dateformat = getTransactionDateFormat();
			LocalDate currentDateTdy = LocalDate.now();
			LocalDate stDate = currentDateTdy.minusDays(days);
			todayStr = dateformatUtils.chageDateFormatLocalDate(dateformat, currentDateTdy); // yyyy-MM-dd
			startDateStr = dateformatUtils.chageDateFormatLocalDate(dateformat, stDate);
			SearchFieldsDTO srchDto = new SearchFieldsDTO(null, null, startDateStr, todayStr, transID, null, null, null,
					null, null, null, null, null,null,null,null,null,null);	
			String yearStr  = String.valueOf(stDate.getYear());
			String monthStr = String.format("%02d", stDate.getMonthValue());
			String dateStr  = String.format("%02d", stDate.getDayOfMonth());
			String parqutePath = yearStr + "/" + monthStr + "/" + dateStr; // - */*/*
			trasParMapLst = parquetService.executeQueryReturnEntityWithPath(RuleWhizConstants.TRANSACTIONS,
					TransactionParquetMppaing.class, srchDto, parqutePath);

		} catch (Exception e) {
			LOGGER.error("Exception found in TransactionServiceForParqute@getTransDetailsFromProperty : {}", e);
		} finally {
		}
		return trasParMapLst;
	}
	 
	 /**
	  * 
	  * @return
	  */
	public String getTransactionDateFormat() {
		TransactionCustomFieldRDTO  transCustFldRdtoObj = null;
		String dateformat = null;
		try {
			transCustFldRdtoObj = parquetService.getConfig(RuleWhizConstants.TRANSACTIONS);
			if (transCustFldRdtoObj != null) {
				List<ColumnMapping> lstCl = transCustFldRdtoObj.columnMappLstObj();
				if (lstCl != null && lstCl.size() > 0) {
					for (ColumnMapping cmlp : lstCl) {
						if (StringUtils.isNotBlank(cmlp.getTo())
								&& cmlp.getTo().equalsIgnoreCase("transactiondate")) {
							dateformat = cmlp.getFormat();
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in TransactionServiceForParqute@getTransactionDateFormat : {}", e);
		} finally {
		}
		return dateformat;
	}
	/**
	 * 
	 * @param transSrvVoObj
	 * @param reqId
	 * @return
	 */
	public List<TransactionParquetMppaing> processTrasn(TransactionServiceSrchFieldVo transSrvVoObj, String reqId) {
		String dateformat = null;
		List<TransactionParquetMppaing>	 trasParMapLst = null;
		try {
			if(transSrvVoObj!=null) {
				dateformat = getTransactionDateFormat();
				Integer days = transSrvVoObj.getDays();
				Integer months = transSrvVoObj.getMonths();
				Integer hours = transSrvVoObj.getHours();
				String transDate = transSrvVoObj.getTransactionDate();
				String todayStr = null;
				String startDateStr = null;
				if (days != null && dateformat!=null) {
					LocalDate currentDateTdy = LocalDate.now();
					LocalDate stDate = currentDateTdy.minusDays(days);
					// Convert LocalDate to String in same format as DB
					//todayStr = currentDateTdy.toString(); // yyyy-MM-dd
					//startDateStr = stDate.toString();
					todayStr = dateformatUtils.chageDateFormatLocalDate(dateformat,currentDateTdy); // yyyy-MM-dd
					startDateStr = dateformatUtils.chageDateFormatLocalDate(dateformat,stDate);
				}
				
				if (months != null && dateformat!=null) {
					LocalDate currentDateTdy = LocalDate.now();
					LocalDate stDate = currentDateTdy.minusMonths(months);
					// Convert LocalDate to String in same format as DB
					//todayStr = currentDateTdy.toString(); // yyyy-MM-dd
					//startDateStr = stDate.toString();
					todayStr = dateformatUtils.chageDateFormatLocalDate(dateformat,currentDateTdy); // yyyy-MM-dd
					startDateStr = dateformatUtils.chageDateFormatLocalDate(dateformat,stDate);
					
				}
				if (hours != null && dateformat!=null) {
					LocalDateTime currentDateTime = LocalDateTime.now();
					// Subtract hours
					LocalDateTime stDate = currentDateTime.minusHours(hours);
					// Convert LocalDate to String in same format as DB
					todayStr = dateformatUtils.chageDateFormatLocalDateTime(dateformat+" HH:mm:ss",currentDateTime); // yyyy-MM-dd
					startDateStr = dateformatUtils.chageDateFormatLocalDateTime(dateformat+" HH:mm:ss",stDate);
				}
				Range range = transSrvVoObj.getRange();
				String minAmt = null; String maxAmout = null;
				if (range != null) {
					if (range.getMin() != null && range.getMax() != null) {
						minAmt = range.getMin(); maxAmout = range.getMax();
					} else if (range.getMin() != null) {
						minAmt = range.getMin(); maxAmout = range.getMin();
					} else if (range.getMax() != null) {
						minAmt = range.getMax(); maxAmout = range.getMax();
					}
				}
				
				String inclauuseForeignCntry = null;
				if(transSrvVoObj.isForeignCountryCode()) {
					List<String> countryCode = Arrays.asList(amlDeployedCountryCode);
					inclauuseForeignCntry = " NOT IN(" + countryCode.stream()
				        .map(s -> "'" + s.replace("'", "''") + "'")  // escape single quotes
				        .collect(Collectors.joining(",")) + ")";
				}
				/***REMITTENCE***/
				String othercurrencycode=null;
				if(transSrvVoObj.isOthercurrencycode()) {
					List<String> countryCode = Arrays.asList(amlDeployedCountryCode);
					othercurrencycode = " NOT IN(" + countryCode.stream()
				        .map(s -> "'" + s.replace("'", "''") + "'")  // escape single quotes
				        .collect(Collectors.joining(",")) + ")";
				}
								
				List<String> channeltype = null;
				String inClause = null;
				if (StringUtils.isNotBlank(transSrvVoObj.getTransMode()) && transSrvVoObj.getTransMode().equals("CASH")) {
					channeltype = Arrays.asList("ATM", "CASH");
					inClause = " IN(" +channeltype.stream()
			        .map(s -> "'" + s.replace("'", "''") + "'")  // escape single quotes
			        .collect(Collectors.joining(",")) +")";
				} else if (StringUtils.isNotBlank(transSrvVoObj.getTransMode()) && transSrvVoObj.getTransMode().equals("NON-CASH")) {
					 channeltype = Arrays.asList("ATM", "CASH");
					 inClause = " NOT IN(" + channeltype.stream()
				        .map(s -> "'" + s.replace("'", "''") + "'")  // escape single quotes
				        .collect(Collectors.joining(",")) + ")";
				} else {
					
				}
				
				Map<String, String> conditionLst =  new HashMap<>();
				if (StringUtils.isNotBlank(transSrvVoObj.getConditionName())) {
					switch (transSrvVoObj.getConditionName()) {
					case AMLConstants.IMMEDIATE_DIFFERENT_LOCATIONS:
						LOGGER.debug("REQID : [{}] - Transaction Date : [{}] - Deposite amt : [{}]", reqId, transSrvVoObj.getTransactionDate(), transSrvVoObj.getAmount());
						conditionLst.put("amount","LESSTHANOREQUAL");
						conditionLst.put("transactiondate","GREATERTHANOREQUAL");
						break;
					case AMLConstants.IMMEDIATE_WITHDRAWAL:
						LOGGER.debug("REQID : [{}] - Transaction Date : [{}] - Deposite amt : [{}]", reqId, transSrvVoObj.getTransactionDate(), transSrvVoObj.getAmount());
						conditionLst.put("amount","LESSTHANOREQUAL");
						conditionLst.put("transactiondate","GREATERTHANOREQUAL");
						break;
					case AMLConstants.IMMEDIATE_WITHDRAWAL_ATM_OR_OTHER:
						LOGGER.debug("REQID : [{}] - Transaction Date : [{}] - Deposite amt : [{}]", reqId, transSrvVoObj.getTransactionDate(), transSrvVoObj.getAmount());
						conditionLst.put("amount","LESSTHANOREQUAL");
						conditionLst.put("transactiondate","GREATERTHANOREQUAL");
						break;
					default:
						LOGGER.info("REQ ID : [{}] - toGetValueByImediateWithDraw default block Condition not match");
					}
				} else {
					conditionLst = null;
					//conditionLst.put("amount","LESSTHANOREQUAL");
					//conditionLst.put("transactionDate","GREATERTHANOREQUAL");
				}
				
				
				// customerId,  accountNo,  startDate, endDate, transId,   amount, withdraDeposit, String transtype, transmode,  srchStr 
				SearchFieldsDTO srchDto = new SearchFieldsDTO(transSrvVoObj.getCustId(), transSrvVoObj.getAccNo(),
						startDateStr, todayStr, transSrvVoObj.getTxnNo(), minAmt, maxAmout,
						transSrvVoObj.getWithdarwDeposit(), transSrvVoObj.getTransType(), inClause, conditionLst, transDate,inclauuseForeignCntry,null,null,null,othercurrencycode, transSrvVoObj.getDynamicConditions());
				trasParMapLst = parquetService.executeQueryReturnEntity(RuleWhizConstants.TRANSACTIONS, TransactionParquetMppaing.class, srchDto,null);
				if(trasParMapLst!=null) {
					LOGGER.info("Transaction Parquet - trasParMapLst [IF] SIze : {}", trasParMapLst.size());
				} else {
					LOGGER.info("Transaction Parquet - trasParMapLst [ELSE]: {}", trasParMapLst);
				}
				
			} else {
				
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in TransactionServiceForParqute@processOfReq : {}",e);
		} finally {}
		return trasParMapLst;
	}
	/**
	 * THis method used for get Percentage of amount
	 * @param transSrvVoObj
	 * @param reqId
	 * @return
	 */
	public ComputedFactsVO toGetWithdaralFactsFrmParqute(TransactionServiceSrchFieldVo transSrvVoObj, String reqId) {
		LOGGER.info("REQ ID : [{}] - toGetWithdaralFactsFrmParqute Methd Called...........",reqId);
		ComputedFactsVO computedFactsVO = null;
		PercentageDetailsVO percentageDetailsVOObj = null;
		try {
			computedFactsVO = new ComputedFactsVO();
			LOGGER.info("REQ ID : [{}] - toGetWithdaralFactsFrmParqute conditianName : [{}]........", transSrvVoObj.getConditionName());
			percentageDetailsVOObj = getPerDetailsFromParqute(transSrvVoObj,reqId);
			if (percentageDetailsVOObj != null) {
				if (StringUtils.isNotBlank(transSrvVoObj.getConditionName())) {
					switch (transSrvVoObj.getConditionName()) {
					case AMLConstants.IMMEDIATE_DIFFERENT_LOCATIONS:
						computedFactsVO.setFact(transSrvVoObj.getFactName());
						computedFactsVO.setValue(percentageDetailsVOObj.getTotalValue());
						computedFactsVO.setPerCentValue(String.valueOf(rulesUtils.toGetPercentAge(percentageDetailsVOObj)));
						break;
					case AMLConstants.IMMEDIATE_WITHDRAWAL:
						computedFactsVO.setFact(transSrvVoObj.getFactName());
						computedFactsVO.setValue(percentageDetailsVOObj.getTotalValue());
						computedFactsVO.setPerCentValue(String.valueOf(rulesUtils.toGetPercentAge(percentageDetailsVOObj)));
						break;
					case AMLConstants.IMMEDIATE_WITHDRAWAL_ATM_OR_OTHER:
						computedFactsVO.setFact(transSrvVoObj.getFactName());
						computedFactsVO.setValue(percentageDetailsVOObj.getTotalValue());
						computedFactsVO.setPerCentValue(String.valueOf(rulesUtils.toGetPercentAge(percentageDetailsVOObj)));
						break;
					default:
						LOGGER.info("REQ ID : [{}] - toGetWithdaralFacts default block Condition not match");
						computedFactsVO.setFact(transSrvVoObj.getFactName());
						computedFactsVO.setValue(percentageDetailsVOObj.getTotalValue());
					}
				} else {
					LOGGER.info("REQ ID : [{}] - toGetWithdaralFacts conditation not available.");
					computedFactsVO.setFact(transSrvVoObj.getFactName());
					computedFactsVO.setValue(percentageDetailsVOObj.getTotalValue());
				}
			}
		} catch (Exception e) {
			computedFactsVO = null;
			LOGGER.error("REQ ID : [{}] - Exception found in toGetWithdaralFactsFrmParqute : {}",reqId, e);
		} finally {
			LOGGER.info("REQ ID : [{}] - toGetWithdaralFactsFrmParqute Method End........", reqId);
		}
		return computedFactsVO;
	}
	/**
	 * 
	 * @param transSrvVoObj
	 * @param reqId
	 * @param amountOnly
	 * @return
	 */
	public TransactionDetailsDTO getTransactionDetails(TransactionServiceSrchFieldVo transSrvVoObj, String reqId, boolean amountOnly) {
		List<TransactionParquetMppaing> transParMapLst = null;
		TransactionDetailsDTO transDtlObj = null;
		Set<String> uniqueCounterAccNo = null;
		try {
			transDtlObj = new TransactionDetailsDTO();
			transParMapLst = processTrasn(transSrvVoObj, reqId);
			if (transParMapLst != null && transParMapLst.size() > 0) {
				LOGGER.info("REQ ID : [{}] - getTransactionDetails Method transParMapLst [IF] : [{}]", reqId, transParMapLst);
				
				transDtlObj.setCountAmount((long) transParMapLst.size());
				
				BigDecimal total = BigDecimal.ZERO;
				BigDecimal sumAmt = BigDecimal.ZERO;
				uniqueCounterAccNo = new HashSet<>();
				for (TransactionParquetMppaing transParquMapp : transParMapLst) {
					uniqueCounterAccNo.add(transParquMapp.getCounterpartyaccountno());
					if (StringUtils.isNotBlank(transParquMapp.getAmount()) && amountOnly) {
						String amtStr = transParquMapp.getAmount();
						LOGGER.trace("amtStr IF - : [{}]",amtStr);
						if (isValidDecimal(amtStr)) {
							total = new BigDecimal(amtStr.trim());
							sumAmt.add(total);
							transDtlObj.setTxnAmount(total);
							break;
						}
					} else if (StringUtils.isNotBlank(transParquMapp.getAmount())){
						String amtStr = transParquMapp.getAmount();
						LOGGER.trace("amtStr ELSE - : [{}]",isValidDecimal(amtStr));
						if (isValidDecimal(amtStr)) {
							total = new BigDecimal(amtStr.trim());
							sumAmt = sumAmt.add(total);
						}
					}
				}
				if (uniqueCounterAccNo != null && uniqueCounterAccNo.size() > 0) {
					transDtlObj.setCOuntDistcounterpartyAccountNo((long) uniqueCounterAccNo.size());
				} else {
					transDtlObj.setCOuntDistcounterpartyAccountNo((long) 0);
				}
				transDtlObj.setSumAmount(sumAmt);
			} else {
				LOGGER.info("REQ ID : [{}] - getTransactionDetails Method transParMapLst [ELSE] : [{}]", reqId, transParMapLst);
				
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in TransactionServiceForParqute@processOfReq : {}",e);
		} finally {}
		return transDtlObj;
	}
	
	/**
	 * 
	 * @param transSrvVoObj
	 * @param reqId
	 * @return
	 */
	public List<TransactionDetailsDTO> getTransactionDetailsLst(TransactionServiceSrchFieldVo transSrvVoObj, String reqId) {
		List<TransactionParquetMppaing> transParMapLst = null;
		TransactionDetailsDTO transDtlObj = null;
		List<TransactionDetailsDTO> transDtlDTOObj = null;
		try {
			transParMapLst = processTrasn(transSrvVoObj, reqId);
			transDtlDTOObj =  new ArrayList<>();
			if (transParMapLst != null && transParMapLst.size() > 0) {
				
				for (TransactionParquetMppaing transParquMapp : transParMapLst) {
					transDtlObj = new TransactionDetailsDTO();
					BigDecimal total = BigDecimal.ZERO;
					BigDecimal sumAmt = BigDecimal.ZERO;
					transDtlObj.setCountAmount((long) transParMapLst.size());
					if (StringUtils.isNotBlank(transParquMapp.getAmount())){
						String amtStr = transParquMapp.getAmount();
						if (isValidDecimal(amtStr)) {
							total = new BigDecimal(amtStr.trim());
							sumAmt = sumAmt.add(total);
						}
					}
					transDtlObj.setSumAmount(sumAmt);
					transDtlDTOObj.add(transDtlObj);
				}
				
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in TransactionServiceForParqute@getTransactionDetailsLst : {}",e);
		} finally {}
		return transDtlDTOObj;
		
	}
	
	/**
	 * 
	 * @param transSrvVoObj
	 * @param reqId
	 * @return
	 */
	public PercentageDetailsVO getPerDetailsFromParqute(TransactionServiceSrchFieldVo transSrvVoObj, String reqId) {
		TransactionCustomFieldRDTO  transCustFldRdtoObj = null;
		List<TransactionParquetMppaing>	 trasParMapLst = null;
		PercentageDetailsVO  percDtlObj = null;
		try {
			if(transSrvVoObj!=null) {
				transCustFldRdtoObj = parquetService.getConfig(RuleWhizConstants.TRANSACTIONS);
				String dateformat = null;
				if (transCustFldRdtoObj != null) {
					List<ColumnMapping> lstCl = transCustFldRdtoObj.columnMappLstObj();
					if (lstCl != null && lstCl.size() > 0) {
						for (ColumnMapping cmlp : lstCl) {
							if (StringUtils.isNotBlank(cmlp.getTo())
									&& cmlp.getTo().equalsIgnoreCase("transactiondate")) {
								dateformat = cmlp.getFormat();
								break;
							}
						}
					}
				}
			
				Integer days = transSrvVoObj.getDays();
				Integer months = transSrvVoObj.getMonths();
				Integer hours = transSrvVoObj.getHours();
				String transDate = transSrvVoObj.getTransactionDate();
				String todayStr = null;
				String startDateStr = null;
				if (days != null && dateformat!=null) {
					LocalDate currentDateTdy = LocalDate.now();
					LocalDate stDate = currentDateTdy.minusDays(days);
					// Convert LocalDate to String in same format as DB
					//todayStr = currentDateTdy.toString(); // yyyy-MM-dd
					//startDateStr = stDate.toString();
					todayStr = dateformatUtils.chageDateFormatLocalDate(dateformat,currentDateTdy); // yyyy-MM-dd
					startDateStr = dateformatUtils.chageDateFormatLocalDate(dateformat,stDate);
				}
				
				if (months != null && dateformat!=null) {
					LocalDate currentDateTdy = LocalDate.now();
					LocalDate stDate = currentDateTdy.minusMonths(months);
					// Convert LocalDate to String in same format as DB
					//todayStr = currentDateTdy.toString(); // yyyy-MM-dd
					//startDateStr = stDate.toString();
					todayStr = dateformatUtils.chageDateFormatLocalDate(dateformat,currentDateTdy); // yyyy-MM-dd
					startDateStr = dateformatUtils.chageDateFormatLocalDate(dateformat,stDate);
					
				}
				if (hours != null && dateformat!=null) {
					LocalDateTime currentDateTime = LocalDateTime.now();
					// Subtract hours
					LocalDateTime stDate = currentDateTime.minusHours(hours);
					// Convert LocalDate to String in same format as DB
					todayStr = dateformatUtils.chageDateFormatLocalDateTime(dateformat+" HH:mm:ss",currentDateTime); // yyyy-MM-dd
					startDateStr = dateformatUtils.chageDateFormatLocalDateTime(dateformat+" HH:mm:ss",stDate);
				}
				Range range = transSrvVoObj.getRange();
				String minAmt = null; String maxAmout = null;
				if (range != null) {
					if (range.getMin() != null && range.getMax() != null) {
						minAmt = range.getMin(); maxAmout = range.getMax();
					} else if (range.getMin() != null) {
						minAmt = range.getMin(); maxAmout = range.getMin();
					} else if (range.getMax() != null) {
						minAmt = range.getMax(); maxAmout = range.getMax();
					}
				}
				
				String inclauuseForeignCntry = null;
				if(transSrvVoObj.isForeignCountryCode()) {
					List<String> countryCode = Arrays.asList(amlDeployedCountryCode);
					inclauuseForeignCntry = " NOT IN(" + countryCode.stream()
				        .map(s -> "'" + s.replace("'", "''") + "'")  // escape single quotes
				        .collect(Collectors.joining(",")) + ")";
				}
				/***REMITTENCE***/
				String othercurrencycode=null;
				if(transSrvVoObj.isOthercurrencycode()) {
					List<String> countryCode = Arrays.asList(amlDeployedCountryCode);
					othercurrencycode = " NOT IN(" + countryCode.stream()
				        .map(s -> "'" + s.replace("'", "''") + "'")  // escape single quotes
				        .collect(Collectors.joining(",")) + ")";
				}
				
				List<String> channeltype = null;
				String inClause = null;
				if (StringUtils.isNotBlank(transSrvVoObj.getTransMode()) && transSrvVoObj.getTransMode().equals("CASH")) {
					channeltype = Arrays.asList("ATM", "CASH");
					inClause = " IN(" +channeltype.stream()
			        .map(s -> "'" + s.replace("'", "''") + "'")  // escape single quotes
			        .collect(Collectors.joining(",")) +")";
				} else if (StringUtils.isNotBlank(transSrvVoObj.getTransMode()) && transSrvVoObj.getTransMode().equals("NON-CASH")) {
					 channeltype = Arrays.asList("ATM", "CASH");
					 inClause = " NOT IN(" + channeltype.stream()
				        .map(s -> "'" + s.replace("'", "''") + "'")  // escape single quotes
				        .collect(Collectors.joining(",")) + ")";
				} else {
					
				}
				//amount>= and Date<=
				Map<String, String> conditionLst =  new HashMap<>();
				if (StringUtils.isNotBlank(transSrvVoObj.getConditionName())) {
					switch (transSrvVoObj.getConditionName()) {
					case AMLConstants.IMMEDIATE_DIFFERENT_LOCATIONS:
						LOGGER.debug("REQID : [{}] - Transaction Date : [{}] - Deposite amt : [{}]", reqId, transSrvVoObj.getTransactionDate(), transSrvVoObj.getAmount());
						conditionLst.put("amount","LESSTHANOREQUAL");
						conditionLst.put("transactiondate","GREATERTHANOREQUAL");
						break;
					case AMLConstants.IMMEDIATE_WITHDRAWAL:
						LOGGER.debug("REQID : [{}] - Transaction Date : [{}] - Deposite amt : [{}]", reqId, transSrvVoObj.getTransactionDate(), transSrvVoObj.getAmount());
						conditionLst.put("amount","LESSTHANOREQUAL");
						conditionLst.put("transactiondate","GREATERTHANOREQUAL");
						break;
					case AMLConstants.IMMEDIATE_WITHDRAWAL_ATM_OR_OTHER:
						LOGGER.debug("REQID : [{}] - Transaction Date : [{}] - Deposite amt : [{}]", reqId, transSrvVoObj.getTransactionDate(), transSrvVoObj.getAmount());
						conditionLst.put("amount","LESSTHANOREQUAL");
						conditionLst.put("transactiondate","GREATERTHANOREQUAL");
						break;
					default:
						LOGGER.info("REQ ID : [{}] - toGetValueByImediateWithDraw default block Condition not match");
					}
				} else {
					//conditionLst.put("amount","LESSTHANOREQUAL");
					//conditionLst.put("transactionDate","GREATERTHANOREQUAL");
				}
				// customerId,  accountNo,  startDate, endDate, transId,   amount, withdraDeposit, String transtype, transmode,  srchStr 
				SearchFieldsDTO srchDto = new SearchFieldsDTO(transSrvVoObj.getCustId(), transSrvVoObj.getAccNo(),
						startDateStr, todayStr, transSrvVoObj.getTxnNo(), minAmt, maxAmout,
						transSrvVoObj.getWithdarwDeposit(), transSrvVoObj.getTransType(), inClause, conditionLst, transDate,
						inclauuseForeignCntry,null,null,null,othercurrencycode,null); // amount>Givenammount and Date >=
				trasParMapLst = parquetService.executeQueryReturnEntity(RuleWhizConstants.TRANSACTIONS, TransactionParquetMppaing.class, srchDto,null);
			
				if (trasParMapLst != null && trasParMapLst.size() > 0) {
					percDtlObj = new PercentageDetailsVO();
					percDtlObj.setReqId(reqId);
					percDtlObj.setNoOfTimes((long) trasParMapLst.size());
					BigDecimal total = BigDecimal.ZERO;
					for (TransactionParquetMppaing tranmappPObj : trasParMapLst) {
						if (StringUtils.isNotBlank(tranmappPObj.getAmount())) {

							String amtStr = tranmappPObj.getAmount();
							if (isValidDecimal(amtStr)) {
								total = total.add(new BigDecimal(amtStr.trim()));
							}
						}
					}
					percDtlObj.setTotalValue(total);
					LOGGER.info("REQID : [{}] - retnVal : [{}]", reqId, percDtlObj);
				} else {
					percDtlObj = null;
					LOGGER.info("REQID : [{}] - result object is NUll, so retnVal : [{}]", reqId, percDtlObj);
				}
			} else {
				
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in TransactionServiceForParqute@processOfReq : {}",e);
		} finally {}
		return percDtlObj;
	}
	
	public List<TransactionDetailsDTO> getTransDtoFromParqute(Schema schemaObj, String reqId, String accNo, String txnId, String custId) {
		List<TransactionParquetMppaing> transactionParMappinLst = null;
		TransactionDetailsDTO transDtlObj = null;
		List<TransactionDetailsDTO> transDtlDTOObj = null;
		try {
			transDtlDTOObj = new ArrayList<>();
			if (schemaObj != null) {
					String tagName = schemaObj.getTag();
					String value = schemaObj.getValue();
					String conndition =  schemaObj.getCondition();
					String joinExpression =  schemaObj.getJoinexpression();
					String type =  schemaObj.getType();
					
					List<String> conditions = new ArrayList<>();
					List<Object> params = new ArrayList<>();
					
					if(StringUtils.isNotBlank(conndition) && conndition.equalsIgnoreCase("equals")) {
						String queryStr = tagName + " = ?";
						conditions.add(queryStr);
						params.add(value);
					
					}
					if(StringUtils.isNotBlank(conndition) && (conndition.equalsIgnoreCase("greater_than")
							|| conndition.equalsIgnoreCase("after"))) {
						String queryStr = tagName + " > ?";
						conditions.add(queryStr);
						params.add(value);
					
					}
					if(StringUtils.isNotBlank(conndition) && (conndition.equalsIgnoreCase("greater_than_equal") 
							|| conndition.equalsIgnoreCase("on_of_after"))) {
						String quryStr = tagName + ">= ?";
						conditions.add(quryStr);
						params.add(value);
					
					}
					if(StringUtils.isNotBlank(conndition) && (conndition.equalsIgnoreCase("less_than")
							|| conndition.equalsIgnoreCase("before"))) {
						  String queryStr = tagName + " < ?";
					        conditions.add(queryStr);
					        params.add(value);
					
					}
					if(StringUtils.isNotBlank(conndition) && (conndition.equalsIgnoreCase("less_than_equal") 
							|| conndition.equalsIgnoreCase("on_or_before"))) {
						String queryStr = tagName + " <= ?";
				        conditions.add(queryStr);
				        params.add(value);
					}
					if(StringUtils.isNotBlank(conndition) && conndition.equalsIgnoreCase("in")) {
						String quryStr = tagName + " IN (" + value + ") ";
						conditions.add(quryStr);
					
					}
					if(StringUtils.isNotBlank(conndition) && conndition.equalsIgnoreCase("notin")) {
						String quryStr = tagName + " not in(" + value + ") ";
						conditions.add(quryStr);
					
					}
					if(StringUtils.isNotBlank(conndition) && conndition.equalsIgnoreCase("not_equals")) {
						String queryStr = tagName + " <> ?";
						conditions.add(queryStr);
						params.add(value);
					
					}
					if(StringUtils.isNotBlank(conndition) && conndition.equalsIgnoreCase("between")) {
						String queryStr = tagName + " BETWEEN ? AND ?";
				        conditions.add(queryStr);
				        params.add(value);
				        params.add(value);
					
					}
					SerarchFieldsSimpleRuleDTO srchRlObj = new SerarchFieldsSimpleRuleDTO(custId, accNo, null,null, txnId,conditions, params, joinExpression);
					transactionParMappinLst = parquetService.executeQueryWithSimpleRule(RuleWhizConstants.TRANSACTIONS,TransactionParquetMppaing.class,srchRlObj,null);
					
					if (transactionParMappinLst != null && transactionParMappinLst.size() > 0) {
						
						for (TransactionParquetMppaing transParquMapp : transactionParMappinLst) {
							BigDecimal total = BigDecimal.ZERO;
							BigDecimal sumAmt = BigDecimal.ZERO;
							transDtlObj = new TransactionDetailsDTO();
							transDtlObj.setCountAmount((long) transactionParMappinLst.size());
							transDtlObj.setTxnAmount(null);
							
							
							if (StringUtils.isNotBlank(transParquMapp.getAmount())){
								String amtStr = transParquMapp.getAmount();
								if (isValidDecimal(amtStr)) {
									total = new BigDecimal(amtStr.trim());
									sumAmt = sumAmt.add(total);
								}
							}
							transDtlObj.setSumAmount(sumAmt);
							transDtlDTOObj.add(transDtlObj);
						}
					}
				} else {
					transactionParMappinLst = null;
				}
		} catch (Exception e) {
			transactionParMappinLst = null;
			LOGGER.error("Exception found in TransactionServiceForParqute@getTransDtoFromParqute : {}", e);
		} finally {
		}
		return transDtlDTOObj;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	private boolean isValidDecimal(String s) {
	    return s != null &&
	           s.matches("^(?:\\d+\\.?\\d*|\\.\\d+)$");
	}
}