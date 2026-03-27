package com.aml.srv.core.efrmsrv.ruleengine.serive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aml.srv.core.efrm.parqute.entity.TransactionParquteMppaing;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

import io.micrometer.common.util.StringUtils;

@EnableAsync(proxyTargetClass = true)
@EnableCaching
@Service
public class ReportTableUpsertService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ReportTableUpsertService.class);

	@Transactional
	public void toUpdateInsertReportTbl(TransactionParquteMppaing transactionEntity, String alertCategory,
			String alertName) {
		// TODO Auto-generated method stub
		if (StringUtils.isNotBlank(alertCategory)) {
			switch (alertCategory) {
			case RuleWhizConstants.NGO:
			case RuleWhizConstants.NTR:
				ntrUpsert();
				break;
			case RuleWhizConstants.CTR:
				ctrUpsert();
				break;
			case RuleWhizConstants.CFTR:
				cftrUpsert();
				break;
			case RuleWhizConstants.CBWTR:
				cbwtrUpsert();
				break;
			default:
				break;
			}
		}
	}

	public void cbwtrUpsert() {
		try {

		} catch (Exception e) {
			LOGGER.error("Exception found in ReportTableUpsertService@cbwtrUpsert : {}", e);
		} finally {
		}

	}

	public void cftrUpsert() {
		try {

		} catch (Exception e) {
			LOGGER.error("Exception found in ReportTableUpsertService@cftrUpsert : {}", e);
		} finally {
		}

	}

	public void ctrUpsert() {
		try {

		} catch (Exception e) {
			LOGGER.error("Exception found in ReportTableUpsertService@ctrUpsert : {}", e);
		} finally {
		}

	}

	public void ntrUpsert() {
		try {

		} catch (Exception e) {
			LOGGER.error("Exception found in ReportTableUpsertService@processOfReq : {}", e);
		} finally {
		}

	}

}