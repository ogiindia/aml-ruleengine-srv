package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parqute.service.AccountServiceForParqute;
import com.aml.srv.core.efrm.parqute.service.TransactionServiceForParqute;
import com.aml.srv.core.efrm.parqute.service.TransactionServiceSrchFieldVo;
import com.aml.srv.core.efrmsrv.repo.AccountDetailsService;
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

@Service("SUM_DEBIT_CREDITService")
public class SumDebitCreditFact implements FactInterface {

	private Logger LOGGER = LoggerFactory.getLogger(SumDebitCreditFact.class);

	@Autowired
	TransactionService transactionService;

	@Autowired
	FS_FactConditionRepoImpl fS_FactConditionRepoImpl;

	@Autowired
	FS_FactConditionAttributeRepoImpl fS_FactConditionAttributeRepoImpl;

	@Autowired
	AccountDetailsService accountDetailsService;

	@Autowired
	AccountServiceForParqute accountServiceForParqute;
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,
			List<ComputedFactsVO> computedFacts) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::SumNonCashDepositFact@getFactExecutor (ENTRY) Called::::::::::",
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
			//transSrvSrchFilevoObj.setWithdarwDeposit(AMLConstants.DR); // Debit
			computedFactsVOObj.setStrType("num");
			if (condition != null) {
				if (condition.equals("NEW_ACCOUNT_CLOSED")) {
					boolean newClosedflag = false;
					LocalDate currentDateTdy = null;
					LocalDate openDate = null;
					LocalDate closeDate = null;
					// String openingDate =
					// accountDetailsService.getAccountOpeningAndClosingDateByritiria(requVoObjParam.getReqId(),
					// accNo, custId);
					String openingDate = accountServiceForParqute.getAccountOpeningAndClosingDate(reqId, accNo, custId);

					if (openingDate != null) {
						if (openingDate.contains("@")) {
							String dateStr[] = openingDate.split("@");
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
							openDate = LocalDate.parse(dateStr[0], formatter);
							closeDate = LocalDate.parse(dateStr[1], formatter);
							LOGGER.info("Open Date : [{}]", openDate); // Output: 2025-05-20

							long daysBetween = ChronoUnit.DAYS.between(openDate, closeDate);
							if (days != null) {
								if (daysBetween <= days) {
									newClosedflag = true;
									currentDateTdy = closeDate;
								} else {
									newClosedflag = false; // return new BigDecimal(0);
								}
							} else if (months != null) {
								int totaldays = months * 30;
								if (daysBetween <= totaldays) { // Account Closed Immediately
									newClosedflag = true;
									currentDateTdy = closeDate;
								} else {
									newClosedflag = false; // return new BigDecimal(0);
								}
							}
						} else {
							newClosedflag = false;
						}
					} else {
						newClosedflag = false;
					}
					if (newClosedflag) {

						/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null, transMode,
								days, months, factSetObj, range, hours);*/
						dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,false);
						computedFactsVOObj.setStrType("num");
						if (dto != null && dto.getSumAmount() != null) {
							computedFactsVOObj.setFact(factName);
							computedFactsVOObj.setValue(dto.getSumAmount());
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
			LOGGER.error("Exception found in SumNonCashDepositFact@getFactExecutor : {}", e);
		} finally {
			LOGGER.info("REQID : [{}]::::::::::::SumNonCashDepositFact@getFactExecutor (EXIT) End::::::::::\n\n", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;

	}

}