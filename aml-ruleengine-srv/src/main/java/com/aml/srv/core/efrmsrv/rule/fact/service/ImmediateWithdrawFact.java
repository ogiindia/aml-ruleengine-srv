package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parqute.service.ParquetService;
import com.aml.srv.core.efrm.parqute.service.TransactionServiceForParqute;
import com.aml.srv.core.efrm.parqute.service.TransactionServiceSrchFieldVo;
import com.aml.srv.core.efrmsrv.entity.SummarizationDataEntity;
import com.aml.srv.core.efrmsrv.repo.SummarizationDataImpl;
import com.aml.srv.core.efrmsrv.repo.TransactionDetailsDTO;
import com.aml.srv.core.efrmsrv.repo.TransactionService;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.rule.service.RulesUtils;
import com.aml.srv.core.efrmsrv.utils.AMLConstants;

@Service("IMMEDIATE_WITHDRAWALService")
public class ImmediateWithdrawFact implements FactInterface {

	private final AMLConstants AMLConstants;

	@Autowired
	TransactionService transactionService;

	private Logger LOGGER = LoggerFactory.getLogger(ImmediateWithdrawFact.class);

	@Autowired
	RulesUtils rulesUtils;
	
	@Autowired
	ParquetService parquetService;
	
	@Autowired
	SummarizationDataImpl summarizationDataImpl;
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

	ImmediateWithdrawFact(AMLConstants AMLConstants) {
		this.AMLConstants = AMLConstants;
	}

	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,
			List<ComputedFactsVO> computedFacts) {
		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info( "REQID : [{}]::::::::::::RulesExecutorService@ruleOfImmediateWithdraw (IMMEDIATE_WITHDRAWAL) Called::::::::::",requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, txnTime = null,
				txnId = null, reqId = null;
		List<SummarizationDataEntity> sumLstObj =  null;
		TransactionServiceSrchFieldVo transSrvSrchFilevoObj = null;
		try {
			computedFactsVOObj = new ComputedFactsVO();
			accNo = requVoObjParam.getAccountNo();
			custId = requVoObjParam.getCustomerId();
			txnId = requVoObjParam.getTxnId();
			reqId = requVoObjParam.getReqId();
			transMode = requVoObjParam.getTransactionMode();
			transType = requVoObjParam.getTxnType();
			factName = factSetObj.getFact();
			Integer days = factSetObj.getDays();
			Integer hours = factSetObj.getHours();
			Integer months = factSetObj.getMonths();
			txnTime = requVoObjParam.getTxn_time();
			Range range = factSetObj.getRange();
			String condition = factSetObj.getCondition();
			
			transSrvSrchFilevoObj = new TransactionServiceSrchFieldVo();
			transSrvSrchFilevoObj.setAccNo(accNo);
			transSrvSrchFilevoObj.setConditionName(condition);
			transSrvSrchFilevoObj.setCustId(custId);
			transSrvSrchFilevoObj.setDays(days);
			transSrvSrchFilevoObj.setFactName(factName);
			transSrvSrchFilevoObj.setHours(hours);
			transSrvSrchFilevoObj.setMonths(months);
			transSrvSrchFilevoObj.setRange(range);
			transSrvSrchFilevoObj.setTransMode(transMode);
			transSrvSrchFilevoObj.setTransType(transType);
			transSrvSrchFilevoObj.setTxnNo(txnId);
			
			
			if (condition != null) {
				if (condition.equals(AMLConstants.LARGE_DEPOSIT)) {
					//TransactionDetailsDTO dto = transactionService.getTransactionDetails(reqId, custId, accNo, null, null, AMLConstants.DEPOSIT, transMode, days, months, factSetObj, range);
					sumLstObj = summarizationDataImpl.getSummarizationData(reqId, accNo, custId, AMLConstants.CR,null,null,null);
					TransactionDetailsDTO dto = summarizationDataImpl.getTransSummarization(sumLstObj);
					if (dto != null && dto.getMaxAmount() != null) {
						computedFactsVOObj.setFact(factName);
						computedFactsVOObj.setValue((dto.getMaxAmount()));
						BigDecimal depositeLargerAmount = dto.getMaxAmount();
						String transDate = dto.getTransDate();
						transSrvSrchFilevoObj.setWithdarwDeposit(AMLConstants.CR);
						transSrvSrchFilevoObj.setTransactionDate(transDate);
						transSrvSrchFilevoObj.setAmount(depositeLargerAmount.toString());
						
						computedFactsVOObj = transactionServiceForParqute.toGetWithdaralFactsFrmParqute(transSrvSrchFilevoObj, reqId);
						
						/*computedFactsVOObj = rulesUtils.toGetWithdaralFacts(requVoObjParam.getReqId(),
								requVoObjParam.getAccountNo(), requVoObjParam.getCustomerId(),
								requVoObjParam.getTransactionMode(), AMLConstants.DEPOSIT, depositeLargerAmount,
								transDate, factName, factSetObj.getCondition(), factSetObj, null);*/
					}
				} else {

					String fieldName = null;
					computedFactsVOObj = new ComputedFactsVO();
					fieldName = factSetObj.getField();
					factName = factSetObj.getFact();
					LOGGER.info("REQID : [{}] - fieldName : [{}] - factName : [{}]", requVoObjParam.getReqId(), fieldName, factName);

					if (computedFacts != null && computedFacts.size() >= 1) {
						/**
						 * Large Deposit
						 */
						Optional<ComputedFactsVO> matchedComputedFactsVOObj = computedFacts.stream()
								.filter(p -> p.getFact().equals(AMLConstants.LARGE_DEPOSIT)).findFirst();
						if (matchedComputedFactsVOObj.isPresent()) {
							LOGGER.info("Found: " + matchedComputedFactsVOObj.get());
							BigDecimal depositeLargerAmount = matchedComputedFactsVOObj.get().getValue();
							String transDate = matchedComputedFactsVOObj.get().getTransDate();
							
							transSrvSrchFilevoObj.setWithdarwDeposit(requVoObjParam.getTxnType());
							transSrvSrchFilevoObj.setTransactionDate(transDate);
							transSrvSrchFilevoObj.setAmount(depositeLargerAmount.toString());
							
							computedFactsVOObj = transactionServiceForParqute.toGetWithdaralFactsFrmParqute(transSrvSrchFilevoObj, reqId);
							
							/*computedFactsVOObj = rulesUtils.toGetWithdaralFacts(requVoObjParam.getReqId(),
									requVoObjParam.getAccountNo(), requVoObjParam.getCustomerId(),
									requVoObjParam.getTransactionMode(), requVoObjParam.getTxnType(),
									depositeLargerAmount, transDate, factName, factSetObj.getCondition(), factSetObj,
									matchedComputedFactsVOObj);*/
						} else {
							LOGGER.info(AMLConstants.LARGE_DEPOSIT + " not found.");
						}

						/**
						 * Sum of Cash Deposit
						 */
						Optional<ComputedFactsVO> matchedComputedFactsVOObjTwo = computedFacts.stream()
								.filter(p -> p.getFact().equals(AMLConstants.SUM_CASH_DEPOSITS)).findFirst();
						if (matchedComputedFactsVOObjTwo.isPresent()) {
							LOGGER.info("Found: ", matchedComputedFactsVOObjTwo.get());
							BigDecimal depositeLargerAmount = matchedComputedFactsVOObjTwo.get().getValue();
							String transDate = matchedComputedFactsVOObjTwo.get().getTransDate();
							transSrvSrchFilevoObj.setWithdarwDeposit(requVoObjParam.getTxnType());
							transSrvSrchFilevoObj.setTransactionDate(transDate);
							transSrvSrchFilevoObj.setAmount(depositeLargerAmount.toString());
							computedFactsVOObj = transactionServiceForParqute.toGetWithdaralFactsFrmParqute(transSrvSrchFilevoObj, reqId);
							/*computedFactsVOObj = rulesUtils.toGetWithdaralFacts(requVoObjParam.getReqId(),
									requVoObjParam.getAccountNo(), requVoObjParam.getCustomerId(),
									requVoObjParam.getTransactionMode(), requVoObjParam.getTxnType(),
									depositeLargerAmount, transDate, factName, factSetObj.getCondition(), factSetObj,
									matchedComputedFactsVOObjTwo);*/
						} else {
							LOGGER.info(AMLConstants.SUM_CASH_DEPOSITS + " not found.");
						}
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("Exception found in ImmediateWithdrawFact@ruleOfImmediateWithdraw : {}", e);
		} finally {
			LOGGER.info("REQID : [{}]::::::::::::ImmediateWithdrawFact@ruleOfImmediateWithdraw (IMMEDIATE_WITHDRAWAL) End::::::::::\n\n",requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}

}