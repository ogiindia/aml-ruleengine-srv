package com.aml.srv.core.efrm.parquet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrm.parquet.entity.BankandBranchDetailsParquetEntity;

@Component
public class BranchServiceForParquet {

private Logger LOGGER = LoggerFactory.getLogger(BranchServiceForParquet.class);
	
	@Autowired
	ParquetService parquetService;
	
	public BankandBranchDetailsParquetEntity getBankAndBranchDetails(String branchCode, String bankCode){
		BankandBranchDetailsParquetEntity bankAndBrachDtlparquetObj = null;
		SearchFieldsDTO srchDto = null;
		try {
			srchDto =  new SearchFieldsDTO(null, null, null,null,null,null,null,null,null,null,null,null,null, branchCode, bankCode,null,null);
			List<BankandBranchDetailsParquetEntity> lstcustObj = parquetService.executeQueryReturnEntity("BRANCH", BankandBranchDetailsParquetEntity.class, srchDto, null);
			if (lstcustObj != null && lstcustObj.size() > 0) {
				bankAndBrachDtlparquetObj = lstcustObj.get(0);
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in BranchServiceForParquet@getBankAndBranchDetails : {}",e);
		} finally {
			
		}
		return bankAndBrachDtlparquetObj;
	}
	
	public List<BankandBranchDetailsParquetEntity> getBankAndBranchDetailsLst(String branchCode, String bankCode){
		List<BankandBranchDetailsParquetEntity> lstcustObj = null;
		SearchFieldsDTO srchDto = null;
		try {
			srchDto =  new SearchFieldsDTO(null, null, null,null,null,null,null,null,null,null,null,null,null, branchCode, bankCode,null,null);
			 lstcustObj = parquetService.executeQueryReturnEntity("BRANCH", BankandBranchDetailsParquetEntity.class, srchDto, null);
			
		} catch (Exception e) {
			LOGGER.error("Exception found in BranchServiceForParquet@getBankAndBranchDetailsLst : {}",e);
		} finally {
			
		}
		return lstcustObj;
	}
}
