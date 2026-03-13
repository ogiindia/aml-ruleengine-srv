package com.aml.srv.core.efrmsrv.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.entity.RiskValuesEntity;
import com.efrm.rt.srv.core.recordDTO.RiskValueFieldsDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Component
public class RiskValueImpl {

	public static final Logger LOGGER = LoggerFactory.getLogger(RiskValueImpl.class);

	@Autowired
	EntityManager em;
	
	@Autowired
	RiskValuesRepository<T> riskValuesRepository;

	public List<RiskValuesEntity> getParamValueseByRiskParamId(Integer riskParamId) {
		List<RiskValuesEntity> rtnRiskValuse = null;

		CriteriaBuilder cb = null;
		CriteriaQuery<RiskValuesEntity> cq = null;
		Root<RiskValuesEntity> book = null;
		List<Predicate> predicates = null;
		TypedQuery<RiskValuesEntity> query = null;
		try {
			cb = em.getCriteriaBuilder();
			cq = cb.createQuery(RiskValuesEntity.class);
			book = cq.from(RiskValuesEntity.class);
			predicates = new ArrayList<Predicate>();
			predicates.add(cb.equal(book.get("riskParamId"), riskParamId));
			cq.where(predicates.toArray(new Predicate[] {}));
			query = em.createQuery(cq);
			rtnRiskValuse = query.getResultList();
			return rtnRiskValuse;
		} catch (Exception e) {
			LOGGER.error("Exception found in RiskValueImpl@getParamValueseByRiskParamId : {}",e);
		} finally {
			cb = null; cq = null; book = null; predicates = null; query = null;
		}
		return rtnRiskValuse;
	}
	
	
	
	public List<RiskValueFieldsDTO> toGetRiskParamValue(Integer riskparamId) {
		Example<RiskValuesEntity> exampleRiskParamNNtty = null;
		RiskValuesEntity riskValueEntity = null;
		try {
			riskValueEntity = new RiskValuesEntity();
			riskValueEntity.setStatus(1);
			riskValueEntity.setRiskParamId(riskparamId);
			exampleRiskParamNNtty = Example.of(riskValueEntity);

			return riskValuesRepository.findBy(exampleRiskParamNNtty,
					q -> q.sortBy(Sort.by("id")).as(RiskValueFieldsDTO.class).all());
		} catch (Exception e) {
			LOGGER.error("Exception found in RiskValueImpl@toGetRiskParamValue : {}",e);
			return null;
		} finally {
			exampleRiskParamNNtty = null;
		}

	}
	
}
