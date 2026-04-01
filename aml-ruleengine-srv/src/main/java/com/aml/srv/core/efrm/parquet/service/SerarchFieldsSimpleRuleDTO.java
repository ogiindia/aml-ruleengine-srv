package com.aml.srv.core.efrm.parquet.service;

import java.util.List;

public record SerarchFieldsSimpleRuleDTO (String customerId, String accountNo, String startDate,
		String endDate, 
		String transId, List<String> conditionLst, List<Object> params,String joinexprssion) {

}
