package com.aml.srv.core.efrmsrv.ruleengine.serive;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aml.srv.core.efrmsrv.entity.FS_FinsecTxnEntity;
import com.aml.srv.core.efrmsrv.repo.FS_FinsecTxnRepositry;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

@Component
public class FinsecTxnTabeUpsertService {

	public static final Logger LOGGER = LoggerFactory.getLogger(FinsecTxnTabeUpsertService.class);
	
	@Autowired
	FS_FinsecTxnRepositry<T> fsFinsecTxnRepositry;
	String clazzName = FinsecTxnTabeUpsertService.class.getSimpleName();
	
	@Retryable(
	        retryFor = { ObjectOptimisticLockingFailureException.class },
	        maxAttempts = 3,
	        backoff = @Backoff(delay = 100)
	    )
	
	@Transactional
	public void toUpdateFinsecData(String categoryParam, String txnId, String ruleId) {
		FS_FinsecTxnEntity fsFinsecTxnEntity = null;
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			
			fsFinsecTxnEntity = fsFinsecTxnRepositry.findByTransactionId(txnId).orElse(new FS_FinsecTxnEntity());
			fsFinsecTxnEntity.setCreatedDate(new Timestamp(new Date().getTime()));
			fsFinsecTxnEntity.setModifyDate(new Timestamp(new Date().getTime()));
			fsFinsecTxnEntity.setTransactionId(txnId);
			fsFinsecTxnEntity = getReportValues(fsFinsecTxnEntity, categoryParam, txnId, ruleId);
			fsFinsecTxnRepositry.save(fsFinsecTxnEntity);

		} catch (ObjectOptimisticLockingFailureException ex) {
			LOGGER.warn("Trans-ID : [{}] - Rule-ID : [{}] - Optimistic lock conflict in ctrUpsert for category={}", txnId, ruleId, categoryParam);
		    throw ex; // let @Retryable see it
		}  catch (Exception e) {
			LOGGER.error("Trans-ID : [{}] - Rule-ID : [{}] - Exception found in {}@{} : {}", txnId, ruleId, clazzName, methodName,e);
		} finally {fsFinsecTxnEntity = null; }
	}
	
	/**
	 * 
	 * @param fsFinsecTxnEntityObj
	 * @param categoryPram
	 * @param threadId
	 * @return
	 */
	private FS_FinsecTxnEntity getReportValues(FS_FinsecTxnEntity fsFinsecTxnEntityObj, String categoryPram,
			String txnId, String ruleId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try {
			if (StringUtils.isNotBlank(categoryPram)) {
				switch (categoryPram.toUpperCase()) {
				case RuleWhizConstants.STR:
					fsFinsecTxnEntityObj.setStr(1);
					break;
				case RuleWhizConstants.CTR:
					fsFinsecTxnEntityObj.setCtr(1);
					break;
				case RuleWhizConstants.NTR:
					fsFinsecTxnEntityObj.setNtr(1);
					break;
				case RuleWhizConstants.CBWTR:
					fsFinsecTxnEntityObj.setCbwtr(1);
					break;
				case RuleWhizConstants.CFTR:
					fsFinsecTxnEntityObj.setCftr(1);
					break;
				case RuleWhizConstants.OTHER:
					fsFinsecTxnEntityObj.setOther(1);
					break;
				default:
					fsFinsecTxnEntityObj.setOther(1);
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Trans-ID : [{}] - Rule-ID : [{}] - Exception found in {}@{} : {}", txnId, ruleId, clazzName, methodName);
		} finally { }
		return fsFinsecTxnEntityObj;
	}
	
	/**
	 * 
	 * @param transIdParam
	 * @param categoryParam
	 * @param threadId
	 */
	/*
	 * void exceptionInsert(String transIdParam, String categoryParam, Long
	 * threadId){ try { FS_FinsecTxnEntity fsFinsecTxnEntity =
	 * fsFinsecTxnImpl.getFinsecTxn(transIdParam); Optional<FS_FinsecTxnEntity>
	 * optionalValueExp = Optional.ofNullable(fsFinsecTxnEntity); if
	 * (optionalValueExp!=null && !optionalValueExp.isEmpty() &&
	 * optionalValueExp.isPresent()) { fsFinsecTxnEntity = optionalValueExp.get();
	 * LOGGER.info("$$$$$$$$$$$$$$ [IF] fsFinsecTxnEntity : {}",fsFinsecTxnEntity);
	 * fsFinsecTxnEntity = getReportValues(fsFinsecTxnEntity,categoryParam,
	 * threadId); fsFinsecTxnEntity.setModifyDate(new Timestamp(new
	 * Date().getTime())); fs_FinsecTxnRepositry.save(fsFinsecTxnEntity); } } catch
	 * (Exception e) {
	 * LOGGER.error("Exception found  in processEvent@exceptionInsert : {}",e); } }
	 */
	
	/*
	 * @Transactional(isolation = Isolation.SERIALIZABLE) private void
	 * toUpdateFinsecData(String transIdParam, String categoryParam, Long threadId)
	 * { FS_FinsecTxnEntity fsFinsecTxnEntity = null; String methodName =
	 * Thread.currentThread().getStackTrace()[1].getMethodName(); try {
	 * 
	 * fsFinsecTxnEntity = fsFinsecTxnImpl.getFinsecTxn(transIdParam);
	 * 
	 * Optional<FS_FinsecTxnEntity> optionalValue =
	 * Optional.ofNullable(fsFinsecTxnEntity); if (optionalValue!=null &&
	 * !optionalValue.isEmpty() && optionalValue.isPresent()) { try {
	 * fsFinsecTxnEntity = optionalValue.get();
	 * LOGGER.info("$$$$$$$$$$$$$$ [IF] fsFinsecTxnEntity : {}", fsFinsecTxnEntity);
	 * fsFinsecTxnEntity = getReportValues(fsFinsecTxnEntity, categoryParam,
	 * threadId); fsFinsecTxnEntity.setModifyDate(new Timestamp(new
	 * Date().getTime())); fs_FinsecTxnRepositry.save(fsFinsecTxnEntity); } catch
	 * (Exception e) { retryUpdate(fsFinsecTxnEntity); } } else {
	 * LOGGER.info("$$$$$$$$$$$$$$ [ELSE] fsFinsecTxnEntity : {}",fsFinsecTxnEntity)
	 * ; try { FS_FinsecTxnEntity fsFinsecTxnEntityObj = new FS_FinsecTxnEntity();
	 * fsFinsecTxnEntityObj.setCreatedDate(new Timestamp(new Date().getTime()));
	 * fsFinsecTxnEntityObj.setModifyDate(new Timestamp(new Date().getTime()));
	 * fsFinsecTxnEntityObj.setTransactionId(transIdParam); fsFinsecTxnEntityObj =
	 * getReportValues(fsFinsecTxnEntityObj, categoryParam, threadId);
	 * fs_FinsecTxnRepositry.save(fsFinsecTxnEntityObj); } catch
	 * (DataIntegrityViolationException e) { Thread.sleep(5000);
	 * exceptionInsert(transIdParam, categoryParam, threadId); } catch (Exception e)
	 * { Thread.sleep(5000);. exceptionInsert(transIdParam, categoryParam,
	 * threadId); } } } catch (Exception e) {
	 * LOGGER.error("Thread Id : [{}] - Exception found in {}@{} : {}", threadId,
	 * clazzName, methodName,e); } finally { } }
	 */
}
