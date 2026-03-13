package com.aml.srv.core.efrmsrv.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aml.srv.core.efrmsrv.entity.RiskValuesEntity;


@Repository
public interface RiskValuesRepository<T> extends JpaRepository<RiskValuesEntity, Integer> {

}