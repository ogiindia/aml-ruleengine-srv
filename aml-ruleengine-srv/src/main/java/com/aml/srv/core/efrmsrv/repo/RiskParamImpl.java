package com.aml.srv.core.efrmsrv.repo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.entity.RiskParamEntity;
import com.efrm.rt.srv.core.recordDTO.RiskParamFieldDTO;

import jakarta.persistence.EntityManager;

@Component
public class RiskParamImpl {

	public static final Logger LOGGER = LoggerFactory.getLogger(RiskParamImpl.class);

	@Autowired
	EntityManager em;

	@Autowired
	RiskParamRepository<?> riskParamRepository;

	public List<RiskParamFieldDTO> toGetRiskParam() {
		Example<RiskParamEntity> exampleRiskParamNNtty = null;
		RiskParamEntity riskParamEntity = null;
		try {
			riskParamEntity = new RiskParamEntity();
			riskParamEntity.setStatus(1);
			exampleRiskParamNNtty = Example.of(riskParamEntity);

			return riskParamRepository.findBy(exampleRiskParamNNtty,
					q -> q.sortBy(Sort.by("id")).as(RiskParamFieldDTO.class).all());
		} catch (Exception e) {
			return null;
		} finally {
			exampleRiskParamNNtty = null;
		}

	}

}
