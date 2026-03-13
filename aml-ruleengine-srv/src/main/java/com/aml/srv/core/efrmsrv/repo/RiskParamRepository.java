package com.aml.srv.core.efrmsrv.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aml.srv.core.efrmsrv.entity.RiskParamEntity;


@Repository
public interface RiskParamRepository<T> extends JpaRepository<RiskParamEntity, Integer> {

}