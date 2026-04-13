package com.aml.srv.core.efrmsrv.repo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aml.srv.core.efrmsrv.entity.SanctionListConfigEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class SanctionListConfigImpl {

	private Logger LOGGER = LoggerFactory.getLogger(SanctionListConfigImpl.class);

	@Autowired
	EntityManager em;

	public List<SanctionListConfigEntity> getSanctionListConfigBySanctionName(String sanctionLstType) {
		List<SanctionListConfigEntity> sanctionListConfigEntityObj = null;
		CriteriaBuilder cb = null;
		CriteriaQuery<SanctionListConfigEntity> cq = null;
		Root<SanctionListConfigEntity> book = null;
		List<Predicate> predicates = null;
		TypedQuery<SanctionListConfigEntity> query = null;
		try {
			cb = em.getCriteriaBuilder();
			cq = cb.createQuery(SanctionListConfigEntity.class);
			book = cq.from(SanctionListConfigEntity.class);
			predicates = new ArrayList<Predicate>();
			predicates.add(cb.equal(book.get("lsttype"), sanctionLstType));
			cq.where(predicates.toArray(new Predicate[] {}));

			query = em.createQuery(cq);
			sanctionListConfigEntityObj = query.getResultList();
			if (sanctionListConfigEntityObj != null) {
				LOGGER.info("sanctionLstType : [{}] - retnVal : [{}]", sanctionLstType, sanctionListConfigEntityObj);
			} else {
				LOGGER.info("REQID : [{}] - result object is NUll, so retnVal : [{}]", sanctionLstType, sanctionListConfigEntityObj);
			}
		} catch (Exception e) {
			sanctionListConfigEntityObj = null;
		} finally {
			 cb = null; cq = null; book = null; predicates = null; query = null;
		}
		return sanctionListConfigEntityObj;
	}
}
