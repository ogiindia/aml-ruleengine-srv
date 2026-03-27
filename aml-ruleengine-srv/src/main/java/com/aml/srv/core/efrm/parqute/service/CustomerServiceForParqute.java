package com.aml.srv.core.efrm.parqute.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrm.parqute.entity.CustomerDetailsParquteEntity;

@Component
public class CustomerServiceForParqute {
	
	private Logger LOGGER = LoggerFactory.getLogger(CustomerServiceForParqute.class);
	
	@Autowired
	ParquetService parquetService;

	/**
	 * 
	 * @param custId
	 * @return
	 */
	public CustomerDetailsParquteEntity getCustParqueEntity(String custId, String accno) {
		CustomerDetailsParquteEntity custDtlParEnty = null;
		SearchFieldsDTO srchDto = null;
		try {
			srchDto =  new SearchFieldsDTO(custId, accno, null,null,null,null,null,null,null,null,null,null,null);
			List<CustomerDetailsParquteEntity> lstcustObj = parquetService.executeQueryReturnEntity("CUSTOMERS", CustomerDetailsParquteEntity.class, srchDto, null);
			if (lstcustObj != null && lstcustObj.size() > 0) {
				custDtlParEnty = lstcustObj.get(0);
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in CustomerServiceForParqute@getCustParqueEntity : {}", e);
		} finally {
			srchDto = null;
		}
		return custDtlParEnty;
	}
}
