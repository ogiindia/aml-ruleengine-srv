package com.aml.srv.core.efrmsrv.ruleengine.serive;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.cust.scoring.CustmerGenuinessDetailsRecord;
import com.aml.srv.core.efrm.cust.scoring.GenuinenessService;
import com.aml.srv.core.efrm.parquet.entity.TransactionParquetMppaing;
import com.aml.srv.core.efrm.parquet.service.AccountServiceForParquet;
import com.aml.srv.core.efrm.parquet.service.ParquetService;
import com.aml.srv.core.efrm.parquet.service.TransactionAccountCustDetailsDAO;
import com.aml.srv.core.efrm.trans.scoring.FraudScorer;
import com.aml.srv.core.efrm.trans.scoring.TransactionGenuinessDetailsRecord;
import com.aml.srv.core.efrm.trans.scoring.UltraFraudUtils;
import com.aml.srv.core.efrmsrv.entity.Alerts;
import com.aml.srv.core.efrmsrv.entity.FS_FinsecTxnEntity;
import com.aml.srv.core.efrmsrv.entity.NormalizedTblEntity;
import com.aml.srv.core.efrmsrv.repo.AlertImpl;
import com.aml.srv.core.efrmsrv.repo.AlertsRepo;
import com.aml.srv.core.efrmsrv.repo.FS_FinsecTxnImpl;
import com.aml.srv.core.efrmsrv.repo.FS_FinsecTxnRepositry;
import com.aml.srv.core.efrmsrv.repo.FinsentinelRiskImpl;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.RuleResposeDetailsVO;
import com.aml.srv.core.efrmsrv.rule.service.RulesIdentifierService;
import com.aml.srv.core.efrmsrv.ruleengine.AMLRule;
import com.aml.srv.core.efrmsrv.ruleengine.Func;
import com.aml.srv.core.efrmsrv.ruleengine.RulewhizConfig;
import com.aml.srv.core.efrmsrv.ruleengine.Schema;
import com.aml.srv.core.efrmsrv.utils.CommonUtils;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;
import com.efrm.rt.srv.core.recordDTO.AlretIntDTO;
import com.google.gson.Gson;

@EnableAsync(proxyTargetClass = true)
@EnableCaching
@Service
public class ProcessEventsService {

	private Logger LOGGER = LoggerFactory.getLogger(ProcessEventsService.class);
	
	private static final Gson GSON = new Gson();

	@Autowired
	private CommonUtils commonUtils;

	@Autowired
	AlertsRepo alertsRepo;

	@Autowired
	private RulewhizConfig appConfig;

	@Autowired
	RulesIdentifierService rulesIdentifierService;

	@Autowired
	FS_FinsecTxnRepositry<?> fs_FinsecTxnRepositry;

	@Autowired
	FinsentinelRiskImpl finsentinelRiskImpl;
	
	@Autowired
	GenuinenessService genuinenessService;

	@Autowired
	FS_FinsecTxnImpl fsFinsecTxnImpl;
	
	@Autowired
	FraudScorer fraudScorer;
	
	@Autowired
	AlertImpl alertImpl;
	
	@Autowired
	WhiteListAccountCheck whiteListAccountCheck;
	
	@Autowired
	ReportTableUpsertService reportTableUpsertService;

	@Value("${aml.without.api.testing:false}")
	private boolean withOutAPITesting;

	@Value("${aml.cust.prof.avg.rsik.score:80}")
	private Integer custRiskScoreAvg;

	@Autowired
	ParquetService parquetService;
	
	@Autowired
	AccountServiceForParquet accountServiceForParqute;
	
	@Autowired
	FinsecTxnTabeUpsertService finsecTxnTabeUpsertService;
	
	String clazzName = ProcessEventsService.class.getSimpleName();

	/**
	 * 
	 * @param transactionEntity
	 * @param groupId
	 * @param ruleEntity
	 */
	//@Async("RuleEngineExecutor")
	//public void processEvent(TransactionDetailsEntity transactionEntity, String groupId, NormalizedTblEntity ruleEntity) {
	public void processEvent(TransactionParquetMppaing transactionEntity, String groupId, NormalizedTblEntity ruleEntity) {
	
		Long threadId = null;
		Long startTime = new Date().getTime();
		threadId = Thread.currentThread().getId();
		String txnId = null;String ruleId = null;
		if (StringUtils.isNotBlank(transactionEntity.getTransactionid()))
			txnId = transactionEntity.getTransactionid();
		
		if (StringUtils.isNotBlank(ruleEntity.getId()))
			ruleId = ruleEntity.getId();
		
		
		LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - {}@processEvent Async method Called...........", txnId,ruleId, clazzName);
		RuleResposeDetailsVO ruleRespDtlVOObj = null;
		RuleRequestVo bean = null;
		String methodName = null;
		// AMLAPIBean bean = null;
		String payload = null;
		AMLRule ruleDetails = null;
		List<Func> funcList = null;
		List<Factset> factSetList = null;
		List<Schema> schemaList = null;
		ConcurrentHashMap<String, Object> mvelConcurntMap = null;
		TransactionAccountCustDetailsDAO transAccCurObj = null;
		try {
			String threadName = Thread.currentThread().getName();
			LOGGER.info("Running in thread : {}  (ID: {})", threadName, threadId);
			methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - transactionEntity Time : [{}] -  Account No.: [{}]", txnId, ruleId,
					commonUtils.getCurrentDateTimeMSec(startTime), transactionEntity.getAccountno());
			// LOGGER.info("Request Details : [{}] ", new Gson().toJson(ruleEntity, NormalizedRuleEntity.class));
			// bean = new AMLAPIBean();
			bean = new RuleRequestVo();
			payload = ruleEntity.getPayload();
			try {
				LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Payload JSON : [{}]", txnId, ruleId, ruleEntity.getPayload());
				ruleDetails = GSON.fromJson(payload, AMLRule.class);
			} catch (Exception e) {
				LOGGER.error("Trans-ID : [{}] - Rule-ID : [{}] - [PAYLOAD] Exception found PAYLOAD Error : {}", txnId, ruleId, e);
			}
			funcList = ruleDetails.getFunc();
			factSetList = new ArrayList<Factset>();
			schemaList = ruleDetails.getSchema();

			mvelConcurntMap = commonUtils.toConvertJson2Map(ruleDetails, threadId, txnId, ruleId);
			LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - mvelConcurntMap Details : [{}]", txnId, ruleId, mvelConcurntMap);
			// LOGGER.info("Row ID : [{}]", ruleEntity.getId());
			
			for (Func func : funcList) {
				Factset factSetObj = getFactSet(func, ruleEntity, txnId, ruleId);	
				factSetList.add(factSetObj);
			}
			bean.setAccountNo(String.valueOf(transactionEntity.getAccountno()));
			bean.setReqId(UUID.randomUUID().toString());
			bean.setCustomerId(String.valueOf(transactionEntity.getCustomerid()));
			bean.setRuleId(String.valueOf(ruleEntity.getId()));
			bean.setTxn_time(transactionEntity.getTransactiondate());
			bean.setTxnType(transactionEntity.getTransactiontype());
			bean.setTransactionMode(ruleEntity.getTransactionMode());
			bean.setTxnId(transactionEntity.getTransactionid());
			bean.setFactSet(factSetList);
			bean.setSchema(schemaList);
			// String jsonReq = new Gson().toJson(bean);
			// LOGGER.info("Thread Id : [{}] - API Request [{}] - URL : [{}] - Row ID : [{}]  - RULE NAME : [{}]",threadId, jsonReq, apiUrl, ruleEntity.getId(), ruleEntity.getRuleName());
			if (withOutAPITesting) {
				return;
			}
			/** Check This account No. is available in Whitelist List**/
			boolean isWhiteListedAccount = whiteListAccountCheck.checkIsWhiteLitedAccount(String.valueOf(transactionEntity.getAccountno()));
			if(isWhiteListedAccount) {
				LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - This Account Number [{}] is available in White listed List - [{}]",txnId, ruleId, transactionEntity.getAccountno(), isWhiteListedAccount);
				return;
			} 
			
			ruleRespDtlVOObj = rulesIdentifierService.toComputeAMLData(bean, txnId, ruleId);
			String custId = null;
			if (ruleRespDtlVOObj != null) {
				LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Response from AML Service [IF]: [{}]", txnId, ruleId, ruleRespDtlVOObj);
				if (appConfig.ruleMvel.containsKey(ruleEntity.getId())) {
					String MVELExpression = appConfig.ruleMvel.get(ruleEntity.getId());
					LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - From Process Event - [GET] MVEL Expression : [{}]", txnId, ruleId, MVELExpression);
					if (mvelConcurntMap != null) {
						mvelConcurntMap = commonUtils.toUpdateConcurtMap(mvelConcurntMap, ruleRespDtlVOObj, threadId, txnId, ruleId);
						if(transactionEntity!=null) {
							if(StringUtils.isBlank(transactionEntity.getCustomerid())) {
								if(StringUtils.isNotBlank(transactionEntity.getAccountno())) {
									transAccCurObj = accountServiceForParqute.getCustIdfromAccounts(transactionEntity.getAccountno(), ruleRespDtlVOObj.getReqId());
									if(transAccCurObj!=null && StringUtils.isBlank(transactionEntity.getCustomerid())) {
										bean.setCustomerId(transAccCurObj.getCustId());
										custId = transAccCurObj.getCustId();
									} else {
										custId = transactionEntity.getCustomerid();
									}
								} else {
									custId = transactionEntity.getCustomerid();
								}
							} else {
								custId = transactionEntity.getCustomerid();
							}
						} 
						LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - CUSTOMER-ID : [{}]", txnId, ruleId, custId);
						// response beancomputedFacts need to pass in entity
						boolean match = MVEL.evalToBoolean(MVELExpression, mvelConcurntMap);
						LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - MVEL Return Status : [{}]", txnId, ruleId, match);
						if (match) {
							LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - MVEL Expression Match Status [IF] Block : [{}]", txnId, ruleId, match);
							//Get Customer Score
							Double riskScore = Double.valueOf(0);
							String riskType = null;
							Double riskScoreTrans = Double.valueOf(0);
							String riskTypeTrans = null;
							AlretIntDTO alreINtDtoObj = alertImpl.getAlretTrans(custId + transactionEntity.getTransactionid());
							LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Alret Available : [{}]", txnId, ruleId, alreINtDtoObj);
							boolean trnsScrflg = false;
							if (alreINtDtoObj != null) {
								riskScore = alreINtDtoObj.getCustRiskScore();
								riskType = alreINtDtoObj.getCustRiskType();
								riskScoreTrans = alreINtDtoObj.getTranRiskScore();
								riskTypeTrans = alreINtDtoObj.getTranRiskType();
								if (riskScore == null || riskScoreTrans == null || StringUtils.isBlank(riskType) || riskScore==0) {
									trnsScrflg = true;
								} else {
									trnsScrflg = false;
								}
							} else { trnsScrflg = true; }
							if(trnsScrflg) {
								LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Alret Not Available [{}], Newly Calculate Cust and Trans Score.", txnId, ruleId, alreINtDtoObj);
								CustmerGenuinessDetailsRecord custRiskRcd = getCusomerGenuinessScore(custId, txnId, ruleId);
								if(custRiskRcd!=null) {
									riskScore = UltraFraudUtils.toDoubleTwoDecimals(custRiskRcd.riskScore());
									riskType = custRiskRcd.risk();
								}
								//Get Transaction SCore
								Long startDate = new Date().getTime();
								LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Transaction Score Fetch Called.",txnId, ruleId);
								float[] feture = fraudScorer.getMapperDtlForTrans(transactionEntity.getTransactionid());
								TransactionGenuinessDetailsRecord trandScoreRcdObj = fraudScorer.toGetTransFraudScorer(feture);
								if (trandScoreRcdObj != null) {
									riskScoreTrans = UltraFraudUtils.toDoubleTwoDecimals(trandScoreRcdObj.score());
									if (trandScoreRcdObj.levelDecision() != null
											&& trandScoreRcdObj.levelDecision().length > 0) {
										riskTypeTrans = trandScoreRcdObj.levelDecision()[0];
									} else {
										riskTypeTrans = null;
									}
								}
								Long endTime = new Date().getTime();
								LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Transaction Score Fetch End - at [{}]", txnId, ruleId, commonUtils.findIsHourMinSec((endTime - startDate)));
							}
							Alerts alert = null;
							try {
								String alretParentId = custId + transactionEntity.getTransactionid();
								alert = alertsRepo.findByAlertParentIdAndRuleId(alretParentId, ruleId).orElseGet(Alerts::new);
								alert.setCustRiskScore(riskScore);
								alert.setCustRiskType(riskType);
								alert.setTranRiskScore(riskScoreTrans);
								alert.setTranRiskType(riskTypeTrans);
								alert.setAccNo(transactionEntity.getAccountno().toString());
								alert.setAlertDesc(ruleEntity.getRuleDescription());
								alert.setAlertId(commonUtils.getUniqueId());
								alert.setAlertName(ruleEntity.getRuleName());
								alert.setAlertParentId(alretParentId);
								alert.setAlertStatus(RuleWhizConstants.ALERT_STATUS_PENDING);
								alert.setCustId(custId);
								alert.setRiskCategory(ruleEntity.getAlertCategory());
								alert.setRuleId(ruleId);
								alert.setTransactionId(transactionEntity.getTransactionid());
								alert.setAlertDT(new Timestamp(new Date().getTime()));
								alert.setModifiedDt(new Timestamp(new Date().getTime()));
								alertsRepo.save(alert);
								
								//Thread.sleep(6000);
								LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Insert will start into Report Tables",txnId, ruleId);
								reportTableUpsertService.toUpdateInsertReportTbl(transactionEntity, ruleEntity.getAlertCategory(), ruleEntity.getRuleName(), transAccCurObj, ruleEntity.getRuleDescription(), ruleEntity.getId());
								LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Insert are completed into Report Tables",txnId, ruleId);
								//alert = null;
								LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Alert Inserted SUccessfully...........", txnId, ruleId);
								Thread.sleep(3000);
								finsecTxnTabeUpsertService.toUpdateFinsecData(ruleEntity.getAlertCategory(), txnId, ruleId);
							
							} catch (Exception ee) {
								LOGGER.error("Trans-ID : [{}] - Rule-ID : [{}] - Exception found in alret Insert : {}", txnId, ruleId,ee);
								ee.printStackTrace();
							} finally {alert = null;}
						} else {
							LOGGER.warn("Trans-ID : [{}] - Rule-ID : [{}] - MVEL Expression Match Status [ELSE] BLock : [{}]", txnId, ruleId, match);
						}
					} else {
						LOGGER.warn("Trans-ID : [{}] - Rule-ID : [{}] - MVEL Expression Not found and Null.", txnId, ruleId);
					}
				}
			} else {
				LOGGER.warn("Trans-ID : [{}] - Rule-ID : [{}] - Response from AML Service [ELSE]: [{}]", txnId, ruleId, ruleRespDtlVOObj);
			}
		} catch (Exception e) {
			LOGGER.error("Trans-ID : [{}] - Rule-ID : [{}] - Exception found in {}@{} : {}",  txnId, ruleId, clazzName, methodName, e);
		} finally {
			Long endTime = new Date().getTime();
			LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - {}@processEvent Async End Time : [{}]\n\n", txnId, ruleId, clazzName, commonUtils.findIsHourMinSec((endTime - startTime)));
			try { Thread.sleep(6000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
			bean = null; payload = null; ruleDetails = null; funcList = null; factSetList = null;
		}
	}

	/**
	 * 
	 * @param fsFinsecTxnEntity
	 */
	private void retryUpdate(FS_FinsecTxnEntity fsFinsecTxnEntity) { try { fs_FinsecTxnRepositry.save(fsFinsecTxnEntity); } catch (Exception e) { LOGGER.error("Retry ----> : {}",e); } finally { } }

	/**
	 * 
	 * @param custId
	 * @param threadId
	 * @return
	 */
	public CustmerGenuinessDetailsRecord getCusomerGenuinessScore(String custId, String txnId, String ruleId){
		CustmerGenuinessDetailsRecord custScrRd =	null;
		try {
			// To call Customer GeuninessService to get Customer Score
			Long startDate = new Date().getTime();
			LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Customer GenuunessService called - at [{}]", txnId,ruleId, startDate);
			custScrRd =	genuinenessService.toGetIndividualGenuinenessScore(custId);
			Long endTime = new Date().getTime();
			LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - Customer GenuunessService End - at [{}]", txnId,ruleId, commonUtils.findIsHourMinSec((endTime - startDate)));
			
		} catch (Exception e) {
			LOGGER.error("Trans-ID : [{}] - Rule-ID : [{}] - Exception found in getCusomerScore Method : {}",txnId,ruleId,e);
		} finally {
		}
		return custScrRd;
	}
	
	
	/*public float[] getTransData(TransactionDetailsEntity transactionEntity) {
		float[] tranData = null;
		try {
			tranData =  new float [] { 
					transactionEntity.getAmount() != null ? transactionEntity.getAmount().floatValue() : 0.0f,
					transactionEntity.getAccountCurrencyTrnAmt() != null ? transactionEntity.getAccountCurrencyTrnAmt().floatValue()  : 0.0f,
					transactionEntity.getOriginalCurrencyTrnAmt() != null ? transactionEntity.getOriginalCurrencyTrnAmt().floatValue()  : 0.0f
					};
		} catch (Exception e) {
			LOGGER.error("Exception found in processEventService@getTransData : {}",e);
		} finally {}
		return tranData;
	} */
	
	
	
	
	/**
	 * 
	 * @param func
	 * @param ruleEntity
	 * @param threadId
	 * @return
	 */
	public Factset getFactSet(Func func, NormalizedTblEntity ruleEntity, String txnId, String ruleId) {
		 LOGGER.info("Step 1.1 getFactSet Method");
		Factset factset = new Factset();
		String methodName = null;
		try {
			LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - RuleName : [{}] - Row ID : [{}] - RULE VALIDATION - getFactSet Method Called......",
					txnId,ruleId, ruleEntity.getRuleName(), ruleEntity.getId());
			methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			if (RuleWhizConstants.day.equalsIgnoreCase(ruleEntity.getOffsetUnit())) {
				factset.setDays(ruleEntity.getOffsetValue());
			} else if (RuleWhizConstants.month.equalsIgnoreCase(ruleEntity.getOffsetUnit())) {
				factset.setMonths(ruleEntity.getOffsetValue());
			} else if (RuleWhizConstants.hour.equalsIgnoreCase(ruleEntity.getOffsetUnit())) {
				factset.setHours(ruleEntity.getOffsetValue());
			} else {
				factset.setDays(1);
			}
			 LOGGER.info("Step 1.2 [{}]", func.getCondition());
			if (!StringUtils.isEmpty(func.getCondition())) {
				factset.setCondition(func.getCondition());
			}
			Optional.ofNullable(func.getRange()).ifPresent(m -> {
				if (!StringUtils.isEmpty(func.getRange())) {
					LOGGER.info("Step 1.3.1 [{}]", func.getRange());
					String list[] = func.getRange().split(",");
					Range range = new Range();
					range.setMin(list[0]);
					range.setMax(list[1]);
					factset.setRange(range);
				}
			});
			if(factset.getRange()!=null) {
				LOGGER.info("Step 1.4 : Range [IF] MAX : {}  - MIN : {}", factset.getRange().getMax(), factset.getRange().getMin());
			} else {
				LOGGER.info("Step 1.4 : Range [ELSE]: {}", factset.getRange());
			}
			
			factset.setFact(func.getFact());
			factset.setField(func.getTag());
			LOGGER.info( "Trans-ID : [{}] - Rule-ID : [{}] - RuleName : [{}] - Row ID : [{}] - RULE VALIDATION - getFactSet Method End......\n",
					txnId,ruleId, ruleEntity.getRuleName(), ruleEntity.getId());

		} catch (Exception e) {
			LOGGER.error("Trans-ID : [{}] - Rule-ID : [{}] - Exception found in {}@{} : {}", txnId,ruleId, clazzName, methodName, e);
		} finally {
		}
		return factset;
	}
}