package com.aml.srv.core.efrmsrv.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aml.srv.core.efrmsrv.entity.Alerts;

@Repository
public interface AlertsRepo extends JpaRepository<Alerts, Long> {

	Optional<Alerts> findByAlertParentIdAndRuleId(String alertParentId,String ruleId);

}