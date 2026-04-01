package com.aml.srv.core.efrmsrv.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aml.srv.core.efrmsrv.entity.FS_FIU_CBWTREntity;

import jakarta.data.repository.Repository;

@Repository
public interface FS_FIU_CBWTR_Repository<T> extends JpaRepository<FS_FIU_CBWTREntity, Integer>  {
	
	Optional<FS_FIU_CBWTREntity> findByTransactionIdAndReportType(String transactionId, String reportType);

}