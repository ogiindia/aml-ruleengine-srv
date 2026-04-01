package com.aml.srv.core.efrm.parquet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrm.parquet.entity.CustomerDetailsParquetEntity;

@Component
public class CustomerServiceForParquet {
	
	private Logger LOGGER = LoggerFactory.getLogger(CustomerServiceForParquet.class);
	
	@Autowired
	ParquetService parquetService;

	/**
	 * 
	 * @param custId
	 * @return
	 */
	public CustomerDetailsParquetEntity getCustParqueEntity(String custId, String accno) {
		CustomerDetailsParquetEntity custDtlParEnty = null;
		SearchFieldsDTO srchDto = null;
		try {
			srchDto =  new SearchFieldsDTO(custId, accno, null,null,null,null,null,null,null,null,null,null,null,null,null);
			List<CustomerDetailsParquetEntity> lstcustObj = parquetService.executeQueryReturnEntity("CUSTOMERS", CustomerDetailsParquetEntity.class, srchDto, null);
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
