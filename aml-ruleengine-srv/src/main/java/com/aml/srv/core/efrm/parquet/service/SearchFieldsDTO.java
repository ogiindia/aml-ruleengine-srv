package com.aml.srv.core.efrm.parquet.service;

import java.util.Map;

public record SearchFieldsDTO(String customerId, String accountNo, String startDate,
		String endDate, 
		String transId, 
		String minamount,
		String maxamount,
		String withdraDeposit,
		String transtype,
		String transmode,
		Map<String, String> conditionLst, String transDate, String foreignExchInclaue, String branchCode, String bankCode) {

}