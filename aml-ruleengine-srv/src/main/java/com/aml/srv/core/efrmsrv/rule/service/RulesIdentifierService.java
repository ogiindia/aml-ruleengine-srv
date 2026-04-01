package com.aml.srv.core.efrmsrv.rule.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrm.parquet.entity.AccountDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.entity.CustomerDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.service.AccountServiceForParquet;
import com.aml.srv.core.efrm.parquet.service.CustomerServiceForParquet;
import com.aml.srv.core.efrm.rule.fact.util.ClassLoaderUtil;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.intr.SchemaInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.rule.process.response.RuleResposeDetailsVO;
import com.aml.srv.core.efrmsrv.ruleengine.Schema;

/**
 * 
 * @author : E5554365 (Prabakaran.R)
 * @Project : aml-srv
 * @year : 2025
 */
@Component
public class RulesIdentifierService {

	private Logger LOGGER = LoggerFactory.getLogger(RulesIdentifierService.class);

	/*
	 * @Autowired RulesAggregateService rulesExecutorService;
	 */

	/*
	 * @Autowired RulesRiskComplianceService rulesRsikComplianceService;
	 */

	/*
	 * @Autowired CustomerDetailsRepoImpl customerDetailsRepoImpl;
	 */
	/*
	 * @Autowired AccountDetailsService accountDetailsService;
	 * 
	 * @Autowired AccountStatusRepositryImpl accountStatusRepositryImpl;
	 */
	
	@Autowired
	AccountServiceForParquet accountServiceForParqute;
	
	@Autowired
	CustomerServiceForParquet customerServiceForParqute;
	
	@Autowired
	RulesUtils rulesUtils;
	
	@Autowired
	ClassLoaderUtil classLoaderUtil;

	public RuleResposeDetailsVO toComputeAMLData(RuleRequestVo ruleRequestVoObParam) {

		String classname = RulesIdentifierService.class.getSimpleName();
		String methodname = Thread.currentThread().getStackTrace()[1].getMethodName();
		LOGGER.info("RulesIdentifierService toComputeAMLData method called......[{}] [{}]", classname, methodname);

		//RuleResponseVo ruleResponseVoObj = null;
		//List<RuleResposeDetailsVO> ruleRespDtlObj = null;
		RuleResposeDetailsVO ruleResposeDetailsVO = null;
		List<ComputedFactsVO> computedFacts = null;
		ComputedFactsVO computedFactsVO = null;
		try {
			LOGGER.info("RulesIdentifierService toComputeAMLData - ruleRequestVoObParam [{}]......", ruleRequestVoObParam);
			LOGGER.info("RulesIdentifierService toComputeAMLData - ruleRequestVoObParam as String[{}]......", ruleRequestVoObParam.toString());
			if (ruleRequestVoObParam != null) {
				//ruleResponseVoObj = new RuleResponseVo();
				//ruleRespDtlObj = new ArrayList<RuleResposeDetailsVO>();
				computedFacts = new ArrayList<ComputedFactsVO>();
				ruleResposeDetailsVO = new RuleResposeDetailsVO();
				
				
				if (ruleRequestVoObParam.getFactSet() != null && ruleRequestVoObParam.getFactSet().size() > 0) {
					// Fact wise rule
					for (Factset fact : ruleRequestVoObParam.getFactSet()) {
						computedFactsVO = new ComputedFactsVO();
						if (StringUtils.isNotBlank(fact.getFact())) {
							FactInterface factInterface = classLoaderUtil.getBean(fact.getFact() + "Service", FactInterface.class);
							computedFactsVO = factInterface.getFactExecutor(ruleRequestVoObParam, fact, computedFacts);
							computedFactsVO.setFact(fact.getFact());
							computedFactsVO.setFieldTag(fact.getField());
							computedFacts.add(computedFactsVO);
						} else {
							LOGGER.info("RuleRequestVo object is NULL recevie");
						}
					}
				} 
				
				LOGGER.info("Schema Details >>>> : {} - FACT Set Details >>> : {}", ruleRequestVoObParam.getSchema(),ruleRequestVoObParam.getFactSet() );
				
				
				/*** Schema Base ****/
				if (ruleRequestVoObParam.getSchema() != null && ruleRequestVoObParam.getSchema().size() > 0 
						&& (ruleRequestVoObParam.getFactSet() == null 
						|| ruleRequestVoObParam.getFactSet().size()==0)) {
					for (Schema schemaobj : ruleRequestVoObParam.getSchema()) {
						computedFactsVO = new ComputedFactsVO();
						SchemaInterface schemaInterface = classLoaderUtil.getBean("SIMPLERULESService",
								SchemaInterface.class);
						computedFactsVO = schemaInterface.getScheamExecutor(ruleRequestVoObParam, schemaobj,
								computedFacts);
						computedFacts.add(computedFactsVO);
					}
				}
					
			}

			if (StringUtils.isNotBlank(ruleRequestVoObParam.getCustomerId()) && StringUtils.isNotBlank(ruleRequestVoObParam.getAccountNo())) {
				
				AccountDetailsParquetEntity customerEnityObj = accountServiceForParqute.getAccountDetailsFromParqute(null, ruleRequestVoObParam.getAccountNo());
				//CustomerDetailsParquteEntity customerEnityObj = customerServiceForParqute.getCustParqueEntity(ruleRequestVoObParam.getCustomerId(), null);
				if (customerEnityObj != null) {
					ruleResposeDetailsVO.setAccountType(customerEnityObj.getAccounttype());
					ruleResposeDetailsVO.setAccountStatus(customerEnityObj.getStatus());
				}
				
			} else if (StringUtils.isNotBlank(ruleRequestVoObParam.getAccountNo())) {
				AccountDetailsParquetEntity customerEnityObj = accountServiceForParqute.getAccountDetailsFromParqute(null, ruleRequestVoObParam.getAccountNo());
				//CustomerDetailsParquteEntity customerEnityObj = customerServiceForParqute.getCustParqueEntity(ruleRequestVoObParam.getCustomerId(), null);
				if (customerEnityObj != null) {
					ruleResposeDetailsVO.setAccountType(customerEnityObj.getAccounttype());
					ruleResposeDetailsVO.setAccountStatus(customerEnityObj.getStatus());
				}
			}  else if (StringUtils.isNotBlank(ruleRequestVoObParam.getCustomerId())) {
				CustomerDetailsParquetEntity customerEnityObj = customerServiceForParqute.getCustParqueEntity(ruleRequestVoObParam.getCustomerId(), null);
				if (customerEnityObj != null) {
					ruleResposeDetailsVO.setAccountType(customerEnityObj.getCustomertype());
				}
			} 
			ruleResposeDetailsVO.setComputedFacts(computedFacts);
			ruleResposeDetailsVO.setReqId(ruleRequestVoObParam.getReqId());
			
			
			/*if (StringUtils.isNotBlank(ruleRequestVoObParam.getAccountNo())) {
				AccountDetailsEntity customerEnityObj = accountDetailsService.getAccountDetails(ruleRequestVoObParam.getReqId(),null, ruleRequestVoObParam.getAccountNo());
				if (customerEnityObj != null) {
					ruleResposeDetailsVO.setAccountType(customerEnityObj.getAccountType());
				}
			}
			
			if (StringUtils.isNotBlank(ruleRequestVoObParam.getAccountNo())) {
				AccountStatusEntity accountStatusEntityObj = accountStatusRepositryImpl
						.getAccountStatusByAccNO(ruleRequestVoObParam.getAccountNo(), ruleRequestVoObParam.getReqId());
				if (accountStatusEntityObj != null) {
					ruleResposeDetailsVO.setAccountStatus(accountStatusEntityObj.getStatus());
				}
			}*/ 
			

			// ruleRespDtlObj.add(ruleResposeDetailsVO);
			// ruleResponseVoObj.setRuleResponse(ruleRespDtlObj);

		} catch (Exception e) {
			LOGGER.error("Exception found in RulesIdentifierService");
		} finally {

		}
		return ruleResposeDetailsVO;
	}
}
