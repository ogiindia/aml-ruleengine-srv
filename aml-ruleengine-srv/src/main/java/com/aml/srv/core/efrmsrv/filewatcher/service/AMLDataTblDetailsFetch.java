
package com.aml.srv.core.efrmsrv.filewatcher.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.kafka.repo.FinSecIndicatorVO;

/**
 * 
 * @author : E5554365 (Prabakaran.R)
 * @Project : aml-srv
 * @year : 2025
 */
@Component
public class AMLDataTblDetailsFetch {

	private static final Logger Logger = LoggerFactory.getLogger(AMLDataTblDetailsFetch.class);

	/***
	 * 
	 * @return toSetFinSecIndicatorObjectForDuckDBSts FinSecIndicatorVO
	 */
	public FinSecIndicatorVO toSetFinSecIndicatorObjectForDuckDBSts(FinSecIndicatorVO finSecIndicatorVOObj) {
		//FinSecIndicatorVO finSecIndicatorVOObj = null;
		try {
			String uuid = UUID.randomUUID().toString();
			finSecIndicatorVOObj.setUuid(uuid);
			finSecIndicatorVOObj.setFileCompletedStatus(true);

		} catch (Exception e) {
			Logger.error("Exception found in FileWatcher@toSetFinSecIndicatorObject : {}", e);
		} finally {

		}
		return finSecIndicatorVOObj;
	}

	public FinSecIndicatorVO toGetRowCountEachAMLTblSetINFincSecIndicator(FinSecIndicatorVO finSecIndicatorVOoBj) {
		// TODO Auto-generated method stub
		return finSecIndicatorVOoBj;
	}
}
