package com.aml.srv.core.efrmsrv.repo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.entity.Alerts;
import com.efrm.rt.srv.core.recordDTO.AlretIntDTO;

/**
 * 
 */
@Component
public class AlertImpl {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(AlertImpl.class);

	@Autowired
	AlertsRepo alretRepoIntObj;
	
	/**
	 * 
	 * @param alertParentIdParam
	 * @return
	 */
	public AlretIntDTO getAlretTrans(String alertParentIdParam){
		Example<Alerts> exAlertObj = null;
		Alerts alrtsObj = null;
		AlretIntDTO alrtDtoObj = null;
		AtomicReference<AlretIntDTO> ref =null;
		try {
			ref = new AtomicReference<>();
			alrtsObj = new Alerts();
			alrtsObj.setAlertParentId(alertParentIdParam);
			exAlertObj = Example.of(alrtsObj);
			Optional<AlretIntDTO> alertOpt = alretRepoIntObj.findBy(exAlertObj,
					q -> q.sortBy(Sort.by("id")).as(AlretIntDTO.class).first());
			alertOpt.ifPresent(ref::set);
			alrtDtoObj = ref.get();
			alrtDtoObj = alertOpt.orElse(null);
		} catch (Exception e) {
			LOGGER.error("Exception found in AlertImpl@getAlretTrans : {}",e);
		} finally {
			exAlertObj = null;alrtsObj = null;ref =null;
		}
		return alrtDtoObj;
		
	}
}
