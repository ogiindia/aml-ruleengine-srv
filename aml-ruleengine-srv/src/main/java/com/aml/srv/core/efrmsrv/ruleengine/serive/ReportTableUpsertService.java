package com.aml.srv.core.efrmsrv.ruleengine.serive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.aml.srv.core.efrmsrv.entity.TransactionDetailsEntity;

@EnableAsync(proxyTargetClass = true)
@EnableCaching
@Service
public class ReportTableUpsertService {

	public static final Logger Logger = LoggerFactory.getLogger(ReportTableUpsertService.class);

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void toUpdateInsertReportTbl(TransactionDetailsEntity transactionEntity, String alertCategory) {
		// TODO Auto-generated method stub
		
	}	
}