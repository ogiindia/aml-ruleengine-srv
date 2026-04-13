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
import com.aml.srv.core.efrmsrv.repo.AccountDetailsService;
import com.aml.srv.core.efrmsrv.repo.TransactionDetailsDTO;
import com.aml.srv.core.efrmsrv.repo.TransactionService;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.utils.DateFormatUtils;

@Service("ACCOUNT_STATUSService")
public class AccountStatusFact implements FactInterface {

	private Logger LOGGER = LoggerFactory.getLogger(SumDebitCreditFact.class);

	@Autowired
	DateFormatUtils dateFormatUtils;
	
	@Autowired
	TransactionService transactionService;

	@Autowired
	AccountDetailsService accountDetailsService;
	
	@Autowired
	ParquetService parquetService;

	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,
			List<ComputedFactsVO> computedFacts) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::AccountStatusFact@getFactExecutor (ENTRY) Called::::::::::",
				requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, txnTime = null,
				txnId = null, reqId = null;
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
			computedFactsVOObj.setStrType("str");
			computedFactsVOObj.setFact(factName);
			// customerId,  accountNo,  startDate, endDate, transId,   amount, withdraDeposit,  srchStr
			SearchFieldsDTO srchDto =  new SearchFieldsDTO(custId, accNo, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
			List<AccountDetailsParquetEntity> lstAc = parquetService.executeQueryReturnEntity("ACCOUNTS", AccountDetailsParquetEntity.class, srchDto,null);
			if (condition != null) {
				if (condition.equals("NEW_ACCOUNT")) {
					//AccountDetailsEntity acctDetails = accountDetailsService.getAccountDetails(requVoObjParam.getReqId(), accNo, custId);
					//String customerId, String accountNo, String startDate, String endDate, String transId, String srchStr
					if (lstAc != null && lstAc.size() > 0) {
						AccountDetailsParquetEntity acc = lstAc.get(0);
						if (acc != null && acc.getAccountopeneddate() != null) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
							//LocalDate openDate = LocalDate.parse(acc.getAccountOpenedDate(), formatter);
							
							LocalDate openDate = dateFormatUtils.parseToLocalDate(acc.getAccountopeneddate());
							LocalDate currentDate = LocalDate.now();
							LOGGER.info("openDate : {}",openDate); // Output: 2025-05-20
							computedFactsVOObj.setStrType("str");
							long daysBetween = ChronoUnit.DAYS.between(openDate, currentDate);
							if (days != null && days >= daysBetween) {
								computedFactsVOObj.setAcc_open_date(acc.getAccountopeneddate());
								computedFactsVOObj.setStrValue("NEW");
							} else if (months != null) {
								int totalDays = months * 30;
								if (totalDays >= daysBetween) {
									computedFactsVOObj.setAcc_open_date(acc.getAccountopeneddate());
									computedFactsVOObj.setStrValue("NEW");
								}
							} else {
								computedFactsVOObj.setAcc_open_date(acc.getAccountopeneddate());
								computedFactsVOObj.setStrValue("OLD");
							}
						} else {
							computedFactsVOObj.setStrValue("OLD");
						}
					} else {
						computedFactsVOObj.setStrValue("OLD");
					} }
			} else {

				if (lstAc != null && lstAc.size() > 0) {
					AccountDetailsParquetEntity acc = lstAc.get(0);
					if (acc != null && acc.getStatus() != null) {
						computedFactsVOObj.setFact(factName);
						computedFactsVOObj.setStrValue(acc.getStatus());
						computedFactsVOObj.setStrType("str");
					}
				}
				/*AccountStatusEntity acctStatus = accountDetailsService.getAccountStatusByAccNO(accNo, reqId);

				if (acctStatus != null && acctStatus.getStatus() != null) {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setStrValue(acctStatus.getStatus());
					computedFactsVOObj.setStrType("str");
				}*/
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in AccountStatusFact@getFactExecutor : {}", e);
		} finally { LOGGER.info("REQID : [{}]::::::::::::AccountStatusFact@getFactExecutor (EXIT) End::::::::::\n\n",
					requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}
}