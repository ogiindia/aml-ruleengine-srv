package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parquet.entity.AccountDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.service.ParquetService;
import com.aml.srv.core.efrm.parquet.service.SearchFieldsDTO;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrmsrv.repo.AccountDetailsService;
import com.aml.srv.core.efrmsrv.repo.TransactionDetailsDTO;
import com.aml.srv.core.efrmsrv.repo.TransactionService;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;


@Service("INACTIVITY_PERIODService")
public class InActivityPeriodFact implements FactInterface{

private Logger LOGGER = LoggerFactory.getLogger(InActivityPeriodFact.class);
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	AccountDetailsService accountDetailsService;
	
	@Autowired
	ParquetService parquetService;

	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

	
	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,List<ComputedFactsVO> computedFacts ) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::InActivityPeriodFact@getFactExecutor (ENTRY) Called::::::::::",
				requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, 
				txnTime = null, txnId = null, reqId = null;
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
			TransactionDetailsDTO dto =null;
			computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_STR);
			computedFactsVOObj.setFact(factName);
			if (condition != null) {
				if (condition.equals("NEW_ACCOUNT")) {
					AccountDetailsParquetEntity acctDetails = null;
					//AccountDetailsEntity acctDetails = accountDetailsService.getAccountDetails(requVoObjParam.getReqId(), accNo, custId);
					SearchFieldsDTO srchDto = new SearchFieldsDTO(custId, accNo, null, null, null, null, null, null,
							null, null, null,null,null,null,null,null,null,null);
					List<AccountDetailsParquetEntity> lstAc = parquetService.executeQueryReturnEntity(RuleWhizConstants.ACCOUNTS, AccountDetailsParquetEntity.class, srchDto,null);
					if (lstAc != null && lstAc.size() > 0) {
						acctDetails = lstAc.get(0);
					}
					
					if (acctDetails != null && acctDetails.getAccountopeneddate() != null) {
						String format = transactionServiceForParqute.getTransactionDateFormat();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
						LocalDate openDate = LocalDate.parse(acctDetails.getAccountopeneddate(), formatter);
						LocalDate currentDate = LocalDate.now();
						System.out.println(openDate); // Output: 2025-05-20
						computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_STR);
						long daysBetween = ChronoUnit.DAYS.between(openDate, currentDate);
						if (days != null && days >= daysBetween) {
							computedFactsVOObj.setAcc_open_date(acctDetails.getAccountopeneddate());
							computedFactsVOObj.setStrValue("NEW");
						} else if (months != null) {
							int totalDays = months * 30;
							if (totalDays >= daysBetween) {
								computedFactsVOObj.setAcc_open_date(acctDetails.getAccountopeneddate());
								computedFactsVOObj.setStrValue("NEW");
							}
						} else {
							computedFactsVOObj.setAcc_open_date(acctDetails.getAccountopeneddate());
							computedFactsVOObj.setStrValue("OLD");
						}
					} else {
						computedFactsVOObj.setStrValue("OLD");
					}
				}

			} else {
				//AccountStatusEntity acctStatus = accountDetailsService.getAccountStatusByAccNO(accNo, reqId);
				AccountDetailsParquetEntity acctStatus = null;
				SearchFieldsDTO srchDto = new SearchFieldsDTO(null, accNo, null, null, null, null, null, null, null,
						null, null, null,null,null,null,null,null,null);
				List<AccountDetailsParquetEntity> lstAc = parquetService.executeQueryReturnEntity(RuleWhizConstants.ACCOUNTS,
						AccountDetailsParquetEntity.class, srchDto, null);
				if (lstAc != null && lstAc.size() > 0) {
					acctStatus = lstAc.get(0);
				}
				if (acctStatus != null && acctStatus.getStatus() != null) {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setStrValue(acctStatus.getStatus());
					computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_STR);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in InActivityPeriodFact@getFactExecutor : {}", e);
		} finally {

			LOGGER.info("REQID : [{}]::::::::::::InActivityPeriodFact@getFactExecutor (EXIT) End::::::::::\n\n", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;

	}

}