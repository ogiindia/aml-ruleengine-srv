package com.aml.srv.core.efrm.parquet.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrm.parquet.entity.AccountDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.entity.BankandBranchDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.entity.CustomerDetailsParquetEntity;

@Component
public class AccountServiceForParquet {

	private Logger LOGGER = LoggerFactory.getLogger(AccountServiceForParquet.class);
	
	@Autowired
	ParquetService parquetService;
	
	@Autowired
	CustomerServiceForParquet customerServiceForParqute;
	
	@Autowired
	BranchServiceForParquet branchServiceForParquet;
	
	public String getAccountOpeningAndClosingDate(String reqId, String accNo, String custId) {
		SearchFieldsDTO srchDto =  null;
		List<AccountDetailsParquetEntity> lstAc = null;
		String openingDate=null;
		String closingDate=null;
		String combinedStr=null;
		try {
			srchDto =  new SearchFieldsDTO(custId, accNo, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
			lstAc = parquetService.executeQueryReturnEntity("ACCOUNTS", AccountDetailsParquetEntity.class, srchDto,null);
			if (lstAc != null && lstAc.size() > 0) {
				AccountDetailsParquetEntity acc = lstAc.get(0);
				if (acc != null && acc.getAccountopeneddate() != null) {
					openingDate = acc.getAccountopeneddate();
					if (acc.getAccountClosedDate() != null) {
						combinedStr = openingDate + "@" + acc.getAccountClosedDate();
					} else {
						combinedStr = openingDate;
					}
				} else {
					combinedStr=null;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in AccountServiceForParqute@getAccountOpeningAndClosingDate : {}",e);
		} finally {srchDto =  null; lstAc = null;}
		return combinedStr;
	}
	
	public AccountDetailsParquetEntity getAccountDetailsFromParqute(String custId, String accNo) {
		AccountDetailsParquetEntity accountDtlParqEtyObj = null;
		SearchFieldsDTO srchDto =  null;
		List<AccountDetailsParquetEntity> lstAc = null;
		try {
			srchDto =  new SearchFieldsDTO(custId, accNo, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
			lstAc = parquetService.executeQueryReturnEntity("ACCOUNTS", AccountDetailsParquetEntity.class, srchDto,null);
			if (lstAc != null && lstAc.size() > 0) {
				accountDtlParqEtyObj = lstAc.get(0);
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in AccountServiceForParqute@getAccountDetailsFromParqute : {}",e);
		} finally {
			srchDto =  null;  lstAc = null;
		}
		return accountDtlParqEtyObj;
	}
	
	public TransactionAccountCustDetailsDAO getCustIdfromAccounts(String accNo, String reqId) {
		TransactionAccountCustDetailsDAO transAccCustObj = null;
		AccountDetailsParquetEntity accDtslParqEntityObj = null;
		CustomerDetailsParquetEntity custDetailsParEntObj = null;
		try {
			transAccCustObj =  new TransactionAccountCustDetailsDAO();
			accDtslParqEntityObj = getAccountDetailsFromParqute(null,accNo);
			if (accDtslParqEntityObj != null) {
				if(StringUtils.isBlank(accDtslParqEntityObj.getBankCode()) && StringUtils.isNotBlank(accDtslParqEntityObj.getBranchcode())) {
					BankandBranchDetailsParquetEntity baranchEntity = branchServiceForParquet.getBankAndBranchDetails(accDtslParqEntityObj.getBranchcode(), null);
					if(baranchEntity!=null) {
						transAccCustObj.setBankCode(baranchEntity.getBankcode());
					}
				} else {
					transAccCustObj.setBankCode(accDtslParqEntityObj.getBankCode());
				}
				
				transAccCustObj.setBranchCode(accDtslParqEntityObj.getBranchcode());
				transAccCustObj.setCustId(accDtslParqEntityObj.getCustomerid());
				
				custDetailsParEntObj = customerServiceForParqute.getCustParqueEntity(accDtslParqEntityObj.getCustomerid(), null);
				if(custDetailsParEntObj!=null) {
					transAccCustObj.setCusomerName(custDetailsParEntObj.getCustomername());
					transAccCustObj.setPanNo(custDetailsParEntObj.getPanno());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in AccountServiceForParqute@getCustIdfromAccounts : {}",e);
		}finally {
			accDtslParqEntityObj = null; custDetailsParEntObj = null;
		}
		return transAccCustObj;
	}
	
}
