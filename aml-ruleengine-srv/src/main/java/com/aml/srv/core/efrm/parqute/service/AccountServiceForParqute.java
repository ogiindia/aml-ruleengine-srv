package com.aml.srv.core.efrm.parqute.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrm.parqute.entity.AccountDetailsParquteEntity;

@Component
public class AccountServiceForParqute {

	private Logger LOGGER = LoggerFactory.getLogger(AccountServiceForParqute.class);
	
	@Autowired
	ParquetService parquetService;
	
	public String getAccountOpeningAndClosingDate(String reqId, String accNo, String custId) {
		SearchFieldsDTO srchDto =  null;
		List<AccountDetailsParquteEntity> lstAc = null;
		String openingDate=null;
		String closingDate=null;
		String combinedStr=null;
		try {
			srchDto =  new SearchFieldsDTO(custId, accNo, null,null,null,null,null,null,null,null,null,null,null);
			lstAc = parquetService.executeQueryReturnEntity("ACCOUNTS", AccountDetailsParquteEntity.class, srchDto,null);
			if (lstAc != null && lstAc.size() > 0) {
				AccountDetailsParquteEntity acc = lstAc.get(0);
				if (acc != null && acc.getAccountOpenedDate() != null) {
					openingDate = acc.getAccountOpenedDate();
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
}
