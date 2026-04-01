package com.aml.srv.core.efrmsrv.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aml.srv.core.efrmsrv.entity.FS_FIU_CFTREntity;

import jakarta.data.repository.Repository;

@Repository
public interface FS_FIU_CFTR_Repository<T> extends JpaRepository<FS_FIU_CFTREntity, Integer>  {

	Optional<FS_FIU_CFTREntity> findByTransactionIdAndReportType(String transactionId, String reportType);

}