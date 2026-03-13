package com.aml.srv.core.efrm.cust.scoring;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.entity.CustomerDetailsEntity;
import com.aml.srv.core.efrmsrv.repo.CustomerDetailsRepoImpl;
import com.aml.srv.core.efrmsrv.repo.CustomerDetailsRepositry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomerGenuinessScore {

	public static final Logger LOGGER = LoggerFactory.getLogger(CustomerGenuinessScore.class);
	
	@Autowired
	GenuinenessService genuinenessService;

	@Autowired
	CustomerDetailsRepoImpl customerDetailsRepoImpl;

	@Autowired
	CustomerDetailsRepositry<?> customerDetailsRepositry;

	private final ExecutorService executor = Executors.newFixedThreadPool(5); // 5 worker threads

	/**
	 * No Use
	 */
	public void updateCustScore() {
		int page = 0;
		int size = 50; // fetch 50 records per batch
		Page<CustomerDetailsEntity> customerPage = null;
		Pageable pageable = null;
		try {
			do {
				pageable = PageRequest.of(page, size);
				customerPage = customerDetailsRepositry.findAll(pageable);

				final List<CustomerDetailsEntity> batch = customerPage.getContent();
				final int currentPage = page; // final copy for lambda
				executor.submit(() -> {
					batch.forEach(cust -> {
						CustmerGenuinessDetailsRecord custDtlRcodObj = null;
						try {
							 custDtlRcodObj = genuinenessService
									.toGetIndividualGenuinenessScore(cust.getCustomerId());
							if (custDtlRcodObj != null && custDtlRcodObj.riskScore() != null) {
								cust.setScore(String.valueOf(custDtlRcodObj.riskScore()));
							}
						} catch (Exception e) {
							LOGGER.error("Exception found in updateCustScore Method Inner : {}", e);
						} finally {
							custDtlRcodObj = null;
						}
					});
					customerDetailsRepositry.saveAll(batch);
					LOGGER.info("Processed batch: {}", currentPage);
				});
				page++;
			} while (customerPage.hasNext());
			if (executor != null) {
				executor.shutdown();
			}

		} catch (Exception e) {
			LOGGER.error("Exception found in CustomerGenuinessScore@updateCustScore Method : {}",e);
		} finally {

		}
	}

	/**
	 * 
	 * @param customerIdParam
	 * @return
	 */
	public CustmerGenuinessDetailsRecord toUpdateCustScoreAlrt(String customerIdParam) {
		CustmerGenuinessDetailsRecord custDtlRcodObj = null;
		custDtlRcodObj = genuinenessService
				.toGetIndividualGenuinenessScore(customerIdParam);
		LOGGER.info("custDtlRcodObj : {}",custDtlRcodObj);
		return custDtlRcodObj;
		
	}
}