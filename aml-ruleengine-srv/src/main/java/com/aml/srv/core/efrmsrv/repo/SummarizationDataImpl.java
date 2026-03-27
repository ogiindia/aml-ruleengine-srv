package com.aml.srv.core.efrmsrv.repo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.entity.SummarizationDataEntity;
import com.aml.srv.core.efrmsrv.utils.AMLConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Component
public class SummarizationDataImpl {

    private final AMLConstants AMLConstants;

	private Logger LOGGER = LoggerFactory.getLogger(SummarizationDataImpl.class);

	@Autowired
	EntityManager em;

    SummarizationDataImpl(AMLConstants AMLConstants) {
        this.AMLConstants = AMLConstants;
    }

	public List<SummarizationDataEntity> getSummarizationData(String reqId, String accNo, String custId, String depositeWithdraw, Integer days,
			Integer month,Integer hours) {
		CriteriaBuilder cb = null;
		CriteriaQuery<SummarizationDataEntity> cq = null;
		Root<SummarizationDataEntity> book = null;
		List<Predicate> predicates = null;
		TypedQuery<SummarizationDataEntity> query = null;
		try {
			cb = em.getCriteriaBuilder();
			cq = cb.createQuery(SummarizationDataEntity.class);
			book = cq.from(SummarizationDataEntity.class);
			predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotBlank(accNo)) {
				predicates.add(cb.equal(book.get("accountNo"), accNo));
			}
			if (StringUtils.isNotBlank(custId)) {
				predicates.add(cb.equal(book.get("customerId"), custId));
			}
			if(StringUtils.isNotBlank(depositeWithdraw)) {
				if(depositeWithdraw.equalsIgnoreCase(AMLConstants.CR)) {
					predicates.add(cb.equal(book.get("depositorwithdrawal"), depositeWithdraw));
				} else if(depositeWithdraw.equalsIgnoreCase(AMLConstants.DEPOSIT)) {
					predicates.add(cb.equal(book.get("depositorwithdrawal"), AMLConstants.CR));
				} else if(depositeWithdraw.equalsIgnoreCase(AMLConstants.DR)) {
					predicates.add(cb.equal(book.get("depositorwithdrawal"), depositeWithdraw));
				} else if(depositeWithdraw.equalsIgnoreCase(AMLConstants.WITHDRAW)) {
					predicates.add(cb.equal(book.get("depositorwithdrawal"), AMLConstants.DR));
				}
				
			}
			
			
			if (days != null) {

				LocalDate currentDateTdy = LocalDate.now();
				LocalDate stDate = currentDateTdy.minusDays(days);
				
				Timestamp startTs = Timestamp.valueOf(stDate.atStartOfDay());
				Timestamp endTs   = Timestamp.valueOf(currentDateTdy.plusDays(1).atStartOfDay());
				
				// Convert LocalDate to String in same format as DB
			/*	String todayStr = currentDateTdy.toString(); // yyyy-MM-dd
				String startDateStr = stDate.toString();
				LOGGER.info("REQID : [{}] - Current / today Date : [{}]  startDateStr : [{}]", reqId, todayStr,
						startDateStr);
				Expression<java.sql.Date> txnDateAsDate = cb.function("to_Date", java.sql.Date.class,
						book.get("txndate"), cb.literal("YYYY-MM-DD"));
				Predicate betweenDates = cb.between(txnDateAsDate, java.sql.Date.valueOf(startDateStr),
						java.sql.Date.valueOf(todayStr));
				
			*/	
				Predicate betweenDates = cb.between(book.<Timestamp>get("txndate"), startTs, endTs);
				
				predicates.add(betweenDates);
			}
			if (month != null) {
				LocalDate currentDateTdy = LocalDate.now();
				LocalDate stDate = currentDateTdy.minusMonths(month);
				
				Timestamp startTs = Timestamp.valueOf(stDate.atStartOfDay());
				Timestamp endTs   = Timestamp.valueOf(currentDateTdy.plusDays(1).atStartOfDay());
				
			/*	// Convert LocalDate to String in same format as DB
				String todayStr = currentDateTdy.toString(); // yyyy-MM-dd
				String startDateStr = stDate.toString();

				LOGGER.info("REQID : [{}] - Current / today Date : [{}]  startDateStr : [{}]", reqId, todayStr,
						startDateStr);
				Expression<java.sql.Date> txnDateAsDate = cb.function("to_Date", java.sql.Date.class,
						book.get("txndate"), cb.literal("YYYY-MM-DD"));
				Predicate betweenDates = cb.between(txnDateAsDate, java.sql.Date.valueOf(startDateStr),
						java.sql.Date.valueOf(todayStr));
			*/
				Predicate betweenDates = cb.between(book.<Timestamp>get("txndate"), startTs, endTs);
				predicates.add(betweenDates);
			}
			cq.where(predicates.toArray(new Predicate[] {}));

			query = em.createQuery(cq);
			return query.getResultList();

		} catch (Exception e) {
			return null;
		} finally {
		}
	}

	public TransactionDetailsDTO getTransSummarization(List<SummarizationDataEntity> sumDataLst) {
		Map<String, Double> sumMap = null;
		TransactionDetailsDTO tranObj = null;
		try {
			if (sumDataLst != null && sumDataLst.size() > 0) {
				sumMap = new HashMap<>();
				for (SummarizationDataEntity summData : sumDataLst) {
					sumMap.put(summData.getMetric_name(), summData.getMetricvalue());
				}
				tranObj = new TransactionDetailsDTO();
				for (Map.Entry<String, Double> entry : sumMap.entrySet()) {
					String key = entry.getKey();
					Double value = entry.getValue();

					if (StringUtils.isNotBlank(key) && key.equalsIgnoreCase("TXN_MAX")) {
						tranObj.setMaxAmount(new BigDecimal(value));
					}
					if (StringUtils.isNotBlank(key) && key.equalsIgnoreCase("TXN_MIN")) {
						tranObj.setMinAmount(new BigDecimal(value));
					}
					if (StringUtils.isNotBlank(key) && key.equalsIgnoreCase("TXN_AVG")) {
						tranObj.setAvgAmount(value);
					}
					if (StringUtils.isNotBlank(key) && key.equalsIgnoreCase("TXN_SUM")) {
						tranObj.setSumAmount(new BigDecimal(value));
					}
					if (StringUtils.isNotBlank(key) && key.equalsIgnoreCase("TXN_COUNT")) {
						tranObj.setCountAmount(value.longValue());
					}
					// use key, value
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in getTransSummarization :{}", e);
		} finally {
		}
		return tranObj;
	}
}
