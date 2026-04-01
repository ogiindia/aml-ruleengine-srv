package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parquet.entity.CustomerDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.service.CustomerServiceForParquet;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceSrchFieldVo;
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
import com.aml.srv.core.efrmsrv.utils.AMLConstants;


@Service("CASH_DEPOSIT_LOCATIONService")
public class CashDepositLocationFact implements FactInterface{

    private final AMLConstants AMLConstants;

	private Logger LOGGER = LoggerFactory.getLogger(CountFact.class);
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	FS_FactConditionRepoImpl fS_FactConditionRepoImpl;

	@Autowired
	FS_FactConditionAttributeRepoImpl fS_FactConditionAttributeRepoImpl;
	
	@Autowired
	CustomerDetailsService customerDetailsService;
	
	@Autowired
	CustomerServiceForParquet customerServiceForParqute;
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

    CashDepositLocationFact(AMLConstants AMLConstants) {
        this.AMLConstants = AMLConstants;
    }
	
	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,List<ComputedFactsVO> computedFacts ) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::CashDepositLocationFact@getFactExecutor (ENTRY) Called::::::::::",
				requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, 
				txnTime = null, txnId = null, reqId = null;
		CustomerDetailsParquetEntity custDetails = null;
		List<TransactionDetailsDTO> dto = null;
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
			transSrvSrchFilevoObj.setTransMode("ATM");
			transSrvSrchFilevoObj.setTransType(transType);
			transSrvSrchFilevoObj.setTxnNo(txnId);
			transSrvSrchFilevoObj.setForeignCountryCode(false);
			transSrvSrchFilevoObj.setWithdarwDeposit(AMLConstants.DR);
			//List<TransactionDetailsEntity>  dto =null;
			computedFactsVOObj.setStrType("num");
			if (StringUtils.isNotBlank(condition) && condition.equalsIgnoreCase("OUTSIDE_MAOIST")) {

				String profile = null;

				FS_FactConditionEntity conditionEntity = fS_FactConditionRepoImpl.getFactCondititon(condition,requVoObjParam.getReqId());
				if (conditionEntity != null && conditionEntity.getId() != null) {

					List<FS_FactConditionAttributeEntity> conditionAttribute = fS_FactConditionAttributeRepoImpl
							.getCondititonAttributes(String.valueOf(conditionEntity.getId()), requVoObjParam.getReqId());
					if (conditionAttribute != null && conditionAttribute.size() > 0) {
						//CustomerDetailsEntity custDetails = customerDetailsService.getCustomerDetails(requVoObjParam.getReqId(),custId);
						custDetails = customerServiceForParqute.getCustParqueEntity(custId, null);
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
					/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null, "W", "ATM", false,
							days, months, factSetObj, range, hours);*/
					dto = transactionServiceForParqute.getTransactionDetailsLst(transSrvSrchFilevoObj, reqId);
					if (dto != null && dto.size() > 0) {
						computedFactsVOObj.setFact(factName);
						computedFactsVOObj.setValue(new BigDecimal(dto.size()));
					} else {
						computedFactsVOObj.setFact(factName);
						computedFactsVOObj.setValue(new BigDecimal(0));
					}
				} else {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setValue(new BigDecimal(0));
				}

			} else {
				/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null, "W", "ATM", false,
						days, months, factSetObj, range, hours);*/
				dto = transactionServiceForParqute.getTransactionDetailsLst(transSrvSrchFilevoObj, reqId);
				if (dto != null && dto.size() > 0) {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setValue(new BigDecimal(dto.size()));
				} else {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setValue(new BigDecimal(0));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in CashDepositLocationFact@getFactExecutor : {}", e);
		} finally {
			custDetails = null;  dto = null;  transSrvSrchFilevoObj = null;
			LOGGER.info("REQID : [{}]::::::::::::CashDepositLocationFact@getFactExecutor (EXIT) End::::::::::\n\n",requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}
}