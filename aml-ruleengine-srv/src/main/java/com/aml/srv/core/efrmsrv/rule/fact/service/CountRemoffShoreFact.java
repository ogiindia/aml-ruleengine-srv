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


@Service("COUNT_REM_OFFSHOREService")
public class CountRemoffShoreFact implements FactInterface{


	private Logger LOGGER = LoggerFactory.getLogger(CountRemoffShoreFact.class);
	
	/*
	 * @Autowired TransactionService transactionService;
	 * 
	 * @Autowired FS_FactConditionRepoImpl fS_FactConditionRepoImpl;
	 * 
	 * @Autowired FS_FactConditionAttributeRepoImpl
	 * fS_FactConditionAttributeRepoImpl;
	 * 
	 * @Autowired CustomerDetailsService customerDetailsService;
	 * 
	 * @Autowired FS_FIUIndHighRiskCountryRepoImpl fS_FIUIndHighRiskCountryRepoImpl;
	 * 
	 * @Autowired FS_FIUIndTerrorLocationRepoImpl fS_FIUIndTerrorLocationRepoImpl;
	 */

	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

	

	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,
			List<ComputedFactsVO> computedFacts) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::CountRemoffShoreFact@getFactExecutor (ENTRY) Called::::::::::",
				requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, txnTime = null,
				txnId = null, reqId = null;
		List<TransactionDetailsDTO> dtoLst = null;
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
			transSrvSrchFilevoObj.setForeignCountryCode(true);

			computedFactsVOObj.setStrType("num");
			if (condition != null) {
				// dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, transType);
				//dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj, reqId, false);
				dtoLst = transactionServiceForParqute.getTransactionDetailsLst(transSrvSrchFilevoObj, reqId);
				if (dtoLst != null) {
					for (TransactionDetailsDTO dto : dtoLst) {
						if (dto != null) {
							computedFactsVOObj.setFact(factName);
							computedFactsVOObj.setValue(new BigDecimal(dto.getCountAmount()));
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in CountRemoffShoreFact@getFactExecutor : {}", e);
		} finally {
			dtoLst = null; transSrvSrchFilevoObj = null;
			LOGGER.info("REQID : [{}]::::::::::::CountRemoffShoreFact@getFactExecutor (EXIT) End::::::::::\n\n", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;

	}

}