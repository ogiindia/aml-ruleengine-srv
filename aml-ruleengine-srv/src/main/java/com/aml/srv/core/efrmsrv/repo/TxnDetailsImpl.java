package com.aml.srv.core.efrmsrv.repo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aml.srv.core.efrmsrv.entity.TransactionDetailsEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class TxnDetailsImpl {

	private Logger LOGGER = LoggerFactory.getLogger(TxnDetailsImpl.class);

	@Autowired
	EntityManager entityManager;

	public List<TransactionDetailsEntity> toGetTxnDetailsBydate() {
		List<TransactionDetailsEntity> txnDetilsLstObj = null;

		CriteriaBuilder cb = null;
		List<Predicate> predicates = null;
		CriteriaQuery<TransactionDetailsEntity> cq = null;
		Root<TransactionDetailsEntity> rootBk = null;
		try {
			cb = entityManager.getCriteriaBuilder();
			cq = cb.createQuery(TransactionDetailsEntity.class);
			predicates = new ArrayList<Predicate>();
			rootBk = cq.from(TransactionDetailsEntity.class);
			LocalDate currentDateTdy = LocalDate.now();
			LocalDate stDate = currentDateTdy.minusDays(1); // RT or NRT (T-1)
			String todayStr = currentDateTdy.toString(); // yyyy-MM-dd
			String startDateStr = stDate.toString();
			Expression<java.sql.Date> txnDateAsDate = cb.function("to_Date", java.sql.Date.class,
					rootBk.get("transactionDate"), cb.literal("YYYY-MM-DD"));
			Predicate betweenDates = cb.between(txnDateAsDate, java.sql.Date.valueOf(startDateStr),
					java.sql.Date.valueOf(todayStr));
			predicates.add(betweenDates);
			if (predicates != null) {
				cq.where(cb.and(predicates.toArray(new Predicate[0])));
				TypedQuery<TransactionDetailsEntity> query = entityManager.createQuery(cq);
				txnDetilsLstObj = query.getResultList();
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in TxnDetailsImpl@toGetTxnDetailsBydate : {}", e);
		} finally {
			cb = null;
			predicates = null;
			cq = null;
			rootBk = null;
		}
		return txnDetilsLstObj;
	}
}
