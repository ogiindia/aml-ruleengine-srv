package com.aml.srv.core.efrmsrv.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aml.srv.core.efrmsrv.entity.SummarizationDataEntity;

import jakarta.data.repository.Repository;

@Repository
public interface SummarizationDataRepository<T> extends JpaRepository<SummarizationDataEntity, Integer>  {

}