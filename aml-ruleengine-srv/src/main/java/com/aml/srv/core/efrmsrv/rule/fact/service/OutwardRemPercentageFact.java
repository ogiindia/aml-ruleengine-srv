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
import com.aml.srv.core.efrmsrv.repo.TransactionService;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.utils.AMLConstants;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;


@Service("OUTWARD_REM_PERCENTAGEService")
public class OutwardRemPercentageFact implements FactInterface{

    private final AMLConstants AMLConstants;
	
private Logger LOGGER = LoggerFactory.getLogger(SumDebitCreditFact.class);
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

    OutwardRemPercentageFact(AMLConstants AMLConstants) {
        this.AMLConstants = AMLConstants;
    }
	
	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,List<ComputedFactsVO> computedFacts ) {

		ComputedFactsVO computedFactsVOObj = null;
		TransactionServiceSrchFieldVo transSrvSrchFilevoObj = null;
		LOGGER.info("REQID : [{}]::::::::::::OutwardRemPercentageFact@getFactExecutor (ENTRY) Called::::::::::",
				requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, 
				txnTime = null, txnId = null, reqId = null;
		TransactionDetailsDTO dto = null;
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
			transSrvSrchFilevoObj.setOthercurrencycode(true);
			transSrvSrchFilevoObj.setWithdarwDeposit(null); // Inward and OUtward
			/*TransactionDetailsDTO dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null,AMLConstants.DEPOSIT, transMode, days, months, factSetObj, range);*/
			
			dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,true);
			
			transSrvSrchFilevoObj.setWithdarwDeposit(AMLConstants.DR); // only outward
			TransactionDetailsDTO outwardRemitancetrsn  = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,true);
			
			computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_NUM);
			if (dto != null && dto.getSumAmount() != null 
					&& outwardRemitancetrsn!=null && outwardRemitancetrsn.getSumAmount()!=null) {

				Double outwardAmount = outwardRemitancetrsn.getSumAmount().doubleValue();
				Double totalAmount = dto.getSumAmount().doubleValue();
				double percentage = (outwardAmount / totalAmount) * 100;
				computedFactsVOObj.setFact(factName);
				computedFactsVOObj.setValue(new BigDecimal(percentage));
			} else {
				computedFactsVOObj.setFact(factName);
				computedFactsVOObj.setValue(new BigDecimal(0));
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in OutwardRemPercentageFact@getFactExecutor : {}", e);
		} finally {
			LOGGER.info("REQID : [{}]::::::::::::OutwardRemPercentageFact@getFactExecutor (EXIT) End::::::::::\n\n", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}
}