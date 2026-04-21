package com.aml.srv.core.efrmsrv.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aml.srv.core.efrmsrv.entity.FS_FIU_CTREntity;

import jakarta.data.repository.Repository;

@Repository
public interface FS_FIU_CTR_Repository<T> extends JpaRepository<FS_FIU_CTREntity, Integer>  {

	Optional<FS_FIU_CTREntity> findByTransactionIdAndReportTypeAndRuleId(String transactionId, String reportType, String ruleId);

}