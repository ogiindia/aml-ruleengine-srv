package com.aml.srv.core.efrmsrv.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aml.srv.core.efrmsrv.entity.SanctionListConfigEntity;

@Repository
public interface SancationListConfigRepository<T> extends JpaRepository<SanctionListConfigEntity, Integer> {

}
