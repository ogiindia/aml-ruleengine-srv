package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceSrchFieldVo;
import com.aml.srv.core.efrmsrv.repo.TransactionDetailsDTO;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.utils.AMLConstants;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

@Service("TRANSACTION_AMOUNTService")
public class TransactionAmountFact implements FactInterface {

	private Logger LOGGER = LoggerFactory.getLogger(TransactionAmountFact.class);
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;
	
	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,List<ComputedFactsVO> computedFacts ) {
		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::TransactionAmountFact@ruleOfFDConversion (FD_CONVERSION) Called::::::::::",
				requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, 
				txnTime = null, txnId = null, reqId = null;
		TransactionDetailsDTO dto = null;
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
			
			computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_NUM);
			computedFactsVOObj.setFact(factName);
			
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
			transSrvSrchFilevoObj.setForeignCountryCode(false);
			transSrvSrchFilevoObj.setWithdarwDeposit(null);
			
			
			dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId, false);
			if(dto!=null && dto.getTxnAmount()!=null) {
				computedFactsVOObj.setFact(factName);
				computedFactsVOObj.setValue(dto.getTxnAmount());
			} else {
				computedFactsVOObj.setFact(factName);
				computedFactsVOObj.setValue(new BigDecimal(0));
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in TransactionAmountFact@ruleOfFDConversion : {}", e);
		} finally {

			LOGGER.info("REQID : [{}]::::::::::::TransactionAmountFact@ruleOfFDConversion (FD_CONVERSION) End::::::::::\n\n",requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}
}
