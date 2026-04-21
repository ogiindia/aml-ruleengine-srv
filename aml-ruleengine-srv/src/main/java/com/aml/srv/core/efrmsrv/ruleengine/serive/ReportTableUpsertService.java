package com.aml.srv.core.efrmsrv.ruleengine.serive;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aml.srv.core.efrm.parquet.entity.TransactionParquetMppaing;
import com.aml.srv.core.efrm.parquet.service.TransactionAccountCustDetailsDAO;
import com.aml.srv.core.efrmsrv.entity.FS_FIU_CBWTREntity;
import com.aml.srv.core.efrmsrv.entity.FS_FIU_CFTREntity;
import com.aml.srv.core.efrmsrv.entity.FS_FIU_CTREntity;
import com.aml.srv.core.efrmsrv.entity.FS_FIU_NTREntity;
import com.aml.srv.core.efrmsrv.repo.FS_FIU_CBWTR_Repository;
import com.aml.srv.core.efrmsrv.repo.FS_FIU_CFTR_Repository;
import com.aml.srv.core.efrmsrv.repo.FS_FIU_CTR_Repository;
import com.aml.srv.core.efrmsrv.repo.FS_FIU_NTR_Repository;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

import io.micrometer.common.util.StringUtils;

@EnableAsync(proxyTargetClass = true)
@EnableCaching
@Service
public class ReportTableUpsertService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ReportTableUpsertService.class);

	@Autowired
	FS_FIU_NTR_Repository<?> fs_fiu_NTR_Repository;
	
	@Autowired
	FS_FIU_CTR_Repository<?> fs_fiu_CTR_Repository;
	
	@Autowired
	FS_FIU_CBWTR_Repository<?> fs_fiu_CBWTR_Repository;
	
	@Autowired
	FS_FIU_CFTR_Repository<?> fs_fiu_CFTR_Repository;
	
	@Retryable(
	        retryFor = { ObjectOptimisticLockingFailureException.class },
	        maxAttempts = 3,
	        backoff = @Backoff(delay = 100)
	    )
	
	@Transactional
	public void toUpdateInsertReportTbl(TransactionParquetMppaing transactionEntity, String alertCategory,
			String alertName, TransactionAccountCustDetailsDAO transAccCustDtlObj, String alertDesc, String ruleId) {
		// TODO Auto-generated method stub
		if (StringUtils.isNotBlank(alertCategory)) {
			switch (alertCategory) {
			case RuleWhizConstants.NGO:
			case RuleWhizConstants.NTR:
				ntrUpsert(transactionEntity, alertCategory, alertName,transAccCustDtlObj,ruleId);
				break;
			case RuleWhizConstants.CTR:
				ctrUpsert(transactionEntity, alertCategory, alertName,transAccCustDtlObj, alertDesc,ruleId);
				break;
			case RuleWhizConstants.CFTR:
				cftrUpsert(transactionEntity, alertCategory, alertName,transAccCustDtlObj, alertDesc,ruleId);
				break;
			case RuleWhizConstants.CBWTR:
				cbwtrUpsert(transactionEntity, alertCategory, alertName,transAccCustDtlObj, alertDesc,ruleId);
				break;
			default:
				break;
			}
		}
	}

	public void cbwtrUpsert(TransactionParquetMppaing transactionEntity, String alertCategory,
			String alertName, TransactionAccountCustDetailsDAO transAccCustDtlObj, String alertDesc, String ruleId) {
		FS_FIU_CBWTREntity cbwtrEntityObj = null;
		try {
			cbwtrEntityObj = fs_fiu_CBWTR_Repository.findByTransactionIdAndReportTypeAndRuleId(transactionEntity.getTransactionid(), alertCategory, ruleId)
		            .orElse(new FS_FIU_CBWTREntity());
			cbwtrEntityObj.setCreatedDate(new Timestamp(new Date().getTime()));
			cbwtrEntityObj.setCurrency(transactionEntity.getCurrencycode());
			cbwtrEntityObj.setCustomerId(transAccCustDtlObj.getCustId());
			cbwtrEntityObj.setEntityId(transAccCustDtlObj.getBankCode());
			cbwtrEntityObj.setParnetId(transAccCustDtlObj.getCustId() + transactionEntity.getTransactionid());
			cbwtrEntityObj.setReceiverCountry(transactionEntity.getCountercountrycode());
			cbwtrEntityObj.setReceiverName(transactionEntity.getCounterpartyname());
			cbwtrEntityObj.setRemarks(alertDesc);
			cbwtrEntityObj.setSwiftPurposeCode(alertName);
			cbwtrEntityObj.setTransactionDate(transactionEntity.getTransactiondate());
			cbwtrEntityObj.setTrasnactionAmount(transactionEntity.getAmount());
			cbwtrEntityObj.setTransactionId(transactionEntity.getTransactionid());
			cbwtrEntityObj.setRuleId(ruleId);
			fs_fiu_CBWTR_Repository.save(cbwtrEntityObj);
		} catch (ObjectOptimisticLockingFailureException ex) {
			LOGGER.warn("Optimistic lock conflict in ctrUpsert for txId={}, category={}", transactionEntity.getTransactionid(), alertCategory);
		    throw ex; // let @Retryable see it
		} catch (Exception e) {
			LOGGER.error("Exception found in ReportTableUpsertService@cbwtrUpsert : {}", e);
		} finally { cbwtrEntityObj = null; }
	}

	public void cftrUpsert(TransactionParquetMppaing transactionEntity, String alertCategory,
			String alertName, TransactionAccountCustDetailsDAO transAccCustDtlObj, String alertDesc,String ruleId) {
		FS_FIU_CFTREntity fsFiuCftrEntityObj =  null;
		try {
			fsFiuCftrEntityObj = fs_fiu_CFTR_Repository.findByTransactionIdAndReportTypeAndRuleId(transactionEntity.getTransactionid(), alertCategory, ruleId)
					.orElse(new FS_FIU_CFTREntity());
			fsFiuCftrEntityObj.setBranchCode(transAccCustDtlObj.getBranchCode());
			fsFiuCftrEntityObj.setCreatedDate(new Timestamp(new Date().getTime()));
			fsFiuCftrEntityObj.setCustomerId(transAccCustDtlObj.getCustId());
			fsFiuCftrEntityObj.setCustomerName(transAccCustDtlObj.getCusomerName());
			fsFiuCftrEntityObj.setDenomination(transactionEntity.getAmount());
			fsFiuCftrEntityObj.setDetectionDate(transactionEntity.getTransactiondate());
			fsFiuCftrEntityObj.setPan(transAccCustDtlObj.getPanNo());
			fsFiuCftrEntityObj.setParnetId(transAccCustDtlObj.getCustId() + transactionEntity.getTransactionid());
			fsFiuCftrEntityObj.setRemarks(alertDesc);
			fsFiuCftrEntityObj.setRuleId(ruleId);
			fs_fiu_CFTR_Repository.save(fsFiuCftrEntityObj);
		} catch (ObjectOptimisticLockingFailureException ex) {
			LOGGER.warn("Optimistic lock conflict in ctrUpsert for txId={}, category={}", transactionEntity.getTransactionid(), alertCategory);
		    throw ex; // let @Retryable see it
		} catch (Exception e) {
			LOGGER.error("Exception found in ReportTableUpsertService@cftrUpsert : {}", e);
		} finally {fsFiuCftrEntityObj =  null;}

	}

	public void ctrUpsert(TransactionParquetMppaing transactionEntity, String alertCategory,
			String alertName, TransactionAccountCustDetailsDAO transAccCustDtlObj, String alertDesc,String ruleId) {
		FS_FIU_CTREntity ctrEntityObj = null;
		try {
			ctrEntityObj = fs_fiu_CTR_Repository.findByTransactionIdAndReportTypeAndRuleId(transactionEntity.getTransactionid(), alertCategory, ruleId)
		            .orElse(new FS_FIU_CTREntity());
			ctrEntityObj.setTransactionId(transactionEntity.getTransactionid());
			ctrEntityObj.setReportType(alertCategory);
			ctrEntityObj.setCreatedDate(new Timestamp(new Date().getTime()));
			ctrEntityObj.setEntityId(transAccCustDtlObj.getBankCode());
			ctrEntityObj.setAccountNo(transactionEntity.getAccountno());
			ctrEntityObj.setBranchCode(transAccCustDtlObj.getBranchCode());
			ctrEntityObj.setCurrency(transactionEntity.getCurrencycode());
			ctrEntityObj.setCustomerId(transAccCustDtlObj.getCustId());
			ctrEntityObj.setCustomerName(transAccCustDtlObj.getCusomerName());
			ctrEntityObj.setPan(transAccCustDtlObj.getPanNo());
			ctrEntityObj.setTransactionDate(transactionEntity.getTransactiondate());
			ctrEntityObj.setTrasnactionAmount(transactionEntity.getAmount());
			ctrEntityObj.setTransType(transactionEntity.getTransactiontype());
			ctrEntityObj.setRemarks(alertDesc);
			ctrEntityObj.setParnetId(transAccCustDtlObj.getCustId() + transactionEntity.getTransactionid());
			ctrEntityObj.setRuleId(ruleId);
			fs_fiu_CTR_Repository.save(ctrEntityObj);
			
		} catch (ObjectOptimisticLockingFailureException ex) {
			LOGGER.warn("Optimistic lock conflict in ctrUpsert for txId={}, category={}", transactionEntity.getTransactionid(), alertCategory);
		    throw ex; // let @Retryable see it
		} catch (Exception e) {
			LOGGER.error("Exception found in ReportTableUpsertService@ctrUpsert : {}", e);
		} finally { ctrEntityObj = null; }
	}

	
	public void ntrUpsert(TransactionParquetMppaing transactionEntity, String alertCategory,
			String alertName, TransactionAccountCustDetailsDAO transAccCustDtlObj,String ruleId) {
		FS_FIU_NTREntity ntrEntityObj =  null;
		try {
			ntrEntityObj = fs_fiu_NTR_Repository.findByTransactionIdAndReportTypeAndRuleId(transactionEntity.getTransactionid(), alertCategory,ruleId)
            .orElse(new FS_FIU_NTREntity());
			ntrEntityObj.setTransactionId(transactionEntity.getTransactionid());
			ntrEntityObj.setReportType(alertCategory);
			ntrEntityObj.setCreatedDate(new Timestamp(new Date().getTime()));
			ntrEntityObj.setEntityId(transAccCustDtlObj.getBankCode());
			ntrEntityObj.setCustomerId(transAccCustDtlObj.getCustId());
			ntrEntityObj.setDonorName(transactionEntity.getCounterpartyname());
			ntrEntityObj.setDonorCountry(transactionEntity.getCountercountrycode());
			ntrEntityObj.setTransactionDate(transactionEntity.getTransactiondate());
			ntrEntityObj.setTrasnactionAmount(transactionEntity.getAmount());
			ntrEntityObj.setCurrency(transactionEntity.getCurrencycode());
			ntrEntityObj.setPurposeOfFund(alertName);
			ntrEntityObj.setParnetId(transAccCustDtlObj.getCustId() + transactionEntity.getTransactionid());
			ntrEntityObj.setRuleId(ruleId);
			fs_fiu_NTR_Repository.save(ntrEntityObj);
		} catch (ObjectOptimisticLockingFailureException ex) {
			LOGGER.warn("Optimistic lock conflict in ctrUpsert for txId={}, category={}", transactionEntity.getTransactionid(), alertCategory);
		    throw ex; // let @Retryable see it
		} catch (Exception e) {
			LOGGER.error("Exception found in ReportTableUpsertService@processOfReq : {}", e);
		} finally {ntrEntityObj = null;}
	}
}