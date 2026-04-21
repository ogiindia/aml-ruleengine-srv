package com.aml.srv.core.efrmsrv.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aml.srv.core.efrmsrv.entity.FS_FIU_NTREntity;

import jakarta.data.repository.Repository;

@Repository
public interface FS_FIU_NTR_Repository<T> extends JpaRepository<FS_FIU_NTREntity, Integer> {

	Optional<FS_FIU_NTREntity> findByTransactionIdAndReportTypeAndRuleId(String transactionId, String reportType, String ruleId);

}