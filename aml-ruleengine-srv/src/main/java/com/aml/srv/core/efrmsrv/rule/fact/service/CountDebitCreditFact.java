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

import com.aml.srv.core.efrm.parquet.entity.AccountDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.entity.CustomerDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.service.CustomerServiceForParquet;
import com.aml.srv.core.efrm.parquet.service.ParquetService;
import com.aml.srv.core.efrm.parquet.service.SearchFieldsDTO;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceSrchFieldVo;
import com.aml.srv.core.efrmsrv.entity.FS_FactConditionAttributeEntity;
import com.aml.srv.core.efrmsrv.entity.FS_FactConditionEntity;
import com.aml.srv.core.efrmsrv.repo.AccountDetailsService;
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
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

@Service("COUNT_DEBIT_CREDITService")
public class CountDebitCreditFact implements FactInterface {

	private Logger LOGGER = LoggerFactory.getLogger(CountDebitCreditFact.class);

	@Autowired
	TransactionService transactionService;

	@Autowired
	FS_FactConditionRepoImpl fS_FactConditionRepoImpl;

	@Autowired
	FS_FactConditionAttributeRepoImpl fS_FactConditionAttributeRepoImpl;

	@Autowired
	CustomerDetailsService customerDetailsService;

	@Autowired
	AccountDetailsService accountDetailsService;
	
	@Autowired
	CustomerServiceForParquet customerServiceForParqute;
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;
	
	@Autowired
	ParquetService parquetService;

	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,
			List<ComputedFactsVO> computedFacts) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::CountFact@getFactExecutor (ENTRY) Called::::::::::",
				requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, txnTime = null,
				txnId = null, reqId = null;
		CustomerDetailsParquetEntity custDetails = null;
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
			//TransactionDetailsDTO dto = null;
			computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_NUM);

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
			transSrvSrchFilevoObj.setWithdarwDeposit(AMLConstants.CR);
			if (condition != null) {
				if (condition.equals("LOW-CASH-PROFILE")) {

					String profile = null;

					FS_FactConditionEntity conditionEntity = fS_FactConditionRepoImpl.getFactCondititon(condition,
							requVoObjParam.getReqId());
					if (conditionEntity != null && conditionEntity.getId() != null) {
						List<FS_FactConditionAttributeEntity> conditionAttribute = fS_FactConditionAttributeRepoImpl
								.getCondititonAttributes(String.valueOf(conditionEntity.getId()), requVoObjParam.getReqId());
						if (conditionAttribute != null && conditionAttribute.size() > 0) {
							//CustomerDetailsEntity custDetails = customerDetailsService.getCustomerDetails(requVoObjParam.getReqId(), custId);
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
				} else if (condition.equals("DORMANT_REACTIVATION")) {
					AccountDetailsParquetEntity acctStatus = null;
					accNo = requVoObjParam.getAccountNo();
					custId = null;
					SearchFieldsDTO srchDto =  new SearchFieldsDTO(custId, accNo, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
					List<AccountDetailsParquetEntity> lstAc = parquetService.executeQueryReturnEntity(RuleWhizConstants.ACCOUNTS, AccountDetailsParquetEntity.class, srchDto,null);
					if (lstAc != null && lstAc.size() > 0) {
						acctStatus = lstAc.get(0);
					}
					/*AccountStatusEntity acctStatus = accountDetailsService.getAccountStatusByAccNO(accNo,
							requVoObjParam.getReqId());*/
					if (acctStatus != null && acctStatus.getStatus() != null && acctStatus.getStatus().equals("06")) {
						String status = acctStatus.getStatus();
						computedFactsVOObj.setAccountStatus(status);
						computedFactsVOObj.setAcc_Re_date(acctStatus.getAccountLastUpdatedDate());
						computedFactsVOObj.setStrValue("DORMANT_REACTIVATION");
						/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null, transMode,
								days, months, factSetObj, range, hours);*/
						dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,false);
						computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_NUM);
						if (dto != null && dto.getCountAmount() != null) {

							computedFactsVOObj.setFact(factName);
							computedFactsVOObj.setValue(new BigDecimal(dto.getCountAmount()));
						} else {
							computedFactsVOObj.setFact(factName);
							computedFactsVOObj.setValue(new BigDecimal(0));
						}

					} else {
						computedFactsVOObj.setFact(factName);
						// computedFactsVOObj.setStrValue("NO_DORMANT_REACTIVATION");
						computedFactsVOObj.setValue(new BigDecimal(0));
					}

				} else if (condition.equals("NEW_ACCOUNT")) {
					/*AccountDetailsEntity acctDetails = accountDetailsService
							.getAccountDetails(requVoObjParam.getReqId(), accNo, custId);*/
					AccountDetailsParquetEntity acctDetails = null;
					
					SearchFieldsDTO srchDto =  new SearchFieldsDTO(custId, accNo, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
					List<AccountDetailsParquetEntity> lstAc = parquetService.executeQueryReturnEntity(RuleWhizConstants.ACCOUNTS, AccountDetailsParquetEntity.class, srchDto,null);
					if (lstAc != null && lstAc.size() > 0) {
						acctDetails = lstAc.get(0);
					}
					
					if (acctDetails != null && acctDetails.getAccountopeneddate() != null) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						LocalDate openDate = LocalDate.parse(acctDetails.getAccountopeneddate(), formatter);
						LocalDate currentDate = LocalDate.now();
						//System.out.println(openDate); // Output: 2025-05-20

						long daysBetween = ChronoUnit.DAYS.between(openDate, currentDate);
						if (days != null && days >= daysBetween) {
							computedFactsVOObj.setAcc_open_date(acctDetails.getAccountopeneddate());
							computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_NUM);
							dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,false);
							
							/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null, transMode,
									days, months, factSetObj, range, hours);*/
							if (dto != null && dto.getCountAmount() != null) {
								computedFactsVOObj.setFact(factName);
								computedFactsVOObj.setValue(new BigDecimal(dto.getCountAmount()));
							} else {
								computedFactsVOObj.setFact(factName);
								computedFactsVOObj.setValue(new BigDecimal(0));
							}
						} else if (months != null) {
							int totalDays = months * 30;
							if (totalDays >= daysBetween) {
								computedFactsVOObj.setAcc_open_date(acctDetails.getAccountopeneddate());
								computedFactsVOObj.setAccountStatus("NEW");
								/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null,
										AMLConstants.DEPOSIT, transMode, true, days, months, factSetObj, range, false);*/
								dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,false);
								
								if (dto != null && dto.getTxnAmount() != null) {
									computedFactsVOObj.setValue((dto.getTxnAmount()));
								}
							}
						} else {
							computedFactsVOObj.setAcc_open_date(acctDetails.getAccountopeneddate());
							computedFactsVOObj.setAccountStatus("OLD");
						}
					} else {
						computedFactsVOObj.setAccountStatus("OLD");
					}
				}
			} else {
				/*dto = transactionService.getTransactionDetails(reqId, custId, accNo, txnId, null, transMode, days,
						months, factSetObj, range, hours);*/
				dto = transactionServiceForParqute.getTransactionDetails(transSrvSrchFilevoObj,reqId,false);
				if (dto != null && dto.getCountAmount() != null) {
					computedFactsVOObj.setFact(factName);
					computedFactsVOObj.setValue(new BigDecimal(dto.getCountAmount()));
				}
			}

		} catch (Exception e) {
			LOGGER.error("Exception found in CountFact@getFactExecutor : {}", e);
		} finally {
			LOGGER.info("REQID : [{}]::::::::::::CountFact@getFactExecutor (EXIT) End::::::::::\n\n", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}
}
