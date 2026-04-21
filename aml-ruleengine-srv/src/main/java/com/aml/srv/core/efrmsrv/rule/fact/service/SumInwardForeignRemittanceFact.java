package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceSrchFieldVo;
import com.aml.srv.core.efrmsrv.repo.NonCustomerDetailsRepoImpl;
import com.aml.srv.core.efrmsrv.repo.TransactionDetailsDTO;
import com.aml.srv.core.efrmsrv.repo.TransactionService;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;


@Service("SUM_INWARD_FOREIGN_REMITTANCESService")
public class SumInwardForeignRemittanceFact implements FactInterface{

	private Logger LOGGER = LoggerFactory.getLogger(SumInwardForeignRemittanceFact.class);
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	NonCustomerDetailsRepoImpl nonCustomerRepositry;
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;
	
	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,List<ComputedFactsVO> computedFacts ) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::SumInwardForeignRemittanceFact@getFactExecutor (ENTRY) Called::::::::::",
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
			//TransactionDetailsDTO dto = null;
			//dto = transactionService.getTransactionDetails(reqId, custId);
			dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,false);
			computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_NUM);
			if (condition != null) {
				if (condition.equals("NON_ACCOUNT_HOLDER")) {
					nonCustomerRepositry.getNonCustomerDetails(custId);
				}
			} else {
				if (dto != null && dto.getSumAmount() != null) {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setValue((dto.getSumAmount()));
				} else {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setValue(new BigDecimal(0));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in SumInwardForeignRemittanceFact@getFactExecutor : {}", e);
		} finally {
			LOGGER.info("REQID : [{}]::::::::::::SumInwardForeignRemittanceFact@getFactExecutor (EXIT) End::::::::::\n\n", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}
}