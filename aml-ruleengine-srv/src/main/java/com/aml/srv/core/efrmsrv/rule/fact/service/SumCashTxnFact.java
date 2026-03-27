package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parqute.entity.CustomerDetailsParquteEntity;
import com.aml.srv.core.efrm.parqute.service.CustomerServiceForParqute;
import com.aml.srv.core.efrm.parqute.service.TransactionServiceForParqute;
import com.aml.srv.core.efrm.parqute.service.TransactionServiceSrchFieldVo;
import com.aml.srv.core.efrmsrv.entity.FS_FactConditionAttributeEntity;
import com.aml.srv.core.efrmsrv.entity.FS_FactConditionEntity;
import com.aml.srv.core.efrmsrv.repo.CustomerDetailsService;
import com.aml.srv.core.efrmsrv.repo.FS_FactConditionAttributeRepoImpl;
import com.aml.srv.core.efrmsrv.repo.FS_FactConditionRepoImpl;
import com.aml.srv.core.efrmsrv.repo.TransactionDetailsDTO;
import com.aml.srv.core.efrmsrv.repo.TransactionService;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;

@Service("SUM_CASH_TXNSService")
public class SumCashTxnFact implements FactInterface {

	@Autowired
	TransactionService transactionService;

	@Autowired
	FS_FactConditionRepoImpl fS_FactConditionRepoImpl;

	@Autowired
	FS_FactConditionAttributeRepoImpl fS_FactConditionAttributeRepoImpl;

	@Autowired
	CustomerDetailsService customerDetailsService;
	
	@Autowired
	CustomerServiceForParqute customerServiceForParqute;

	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;
	
	private Logger LOGGER = LoggerFactory.getLogger(SumCashTxnFact.class);

	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,
			List<ComputedFactsVO> computedFacts) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::SumCashTxnFact@getFactExecutor (ENTRY) Called::::::::::",
				requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, txnTime = null,
				txnId = null, reqId = null;
		TransactionServiceSrchFieldVo transSrvSrchFilevoObj = null;
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
			
			computedFactsVOObj.setStrType("num");
			if (condition != null) {
				if (condition.equals("LOW-CASH-PROFILE")) {
					String profile = null;
					FS_FactConditionEntity conditionEntity = fS_FactConditionRepoImpl.getFactCondititon(condition,
							requVoObjParam.getReqId());
					if (conditionEntity != null && conditionEntity.getId() != null) {
						List<FS_FactConditionAttributeEntity> conditionAttribute = fS_FactConditionAttributeRepoImpl.getCondititonAttributes(String.valueOf(conditionEntity.getId()), requVoObjParam.getReqId());
						if (conditionAttribute != null && conditionAttribute.size() > 0) {
							//CustomerDetailsEntity custDetails = customerDetailsService.getCustomerDetails(requVoObjParam.getReqId(), custId);
							CustomerDetailsParquteEntity custDetails = customerServiceForParqute.getCustParqueEntity(custId, accNo);
							if (custDetails != null) {
								for (FS_FactConditionAttributeEntity gs : conditionAttribute) {
									if (gs.getAttributes().equals(custDetails.getCustomercategory())) {
										profile = gs.getAttributes();
									}
								}
							}
						}
					}

					if (profile != null) {
						/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, transType,
								transMode, days, months, factSetObj, range, hours);*/
						dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,false);
						if (dto != null && dto.getCountAmount() != null) {
							computedFactsVOObj.setFact(factName);
							computedFactsVOObj.setValue(new BigDecimal(dto.getCountAmount()));
						} else {
							computedFactsVOObj.setFact(factName);
							computedFactsVOObj.setValue(new BigDecimal(0));
						}
					} else {
						computedFactsVOObj.setFact(factName);
						computedFactsVOObj.setValue(new BigDecimal(0));
					}
				}
			} else {
				/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null, transMode, days,
						months, factSetObj, range, hours);*/
				dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,false);
				
				if (dto != null && dto.getSumAmount() != null) {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setValue(dto.getSumAmount());
				} else {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setValue(new BigDecimal(0));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in SumCashTxnFact@getFactExecutor : {}", e);
		} finally {
			transSrvSrchFilevoObj = null; dto = null;
			LOGGER.info("REQID : [{}]::::::::::::SumCashTxnFact@getFactExecutor (EXIT) End::::::::::\n\n", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}
}