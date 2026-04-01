package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parquet.entity.CustomerDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.service.CustomerServiceForParquet;
import com.aml.srv.core.efrm.parquet.service.ParquetService;
import com.aml.srv.core.efrmsrv.entity.FS_FIUIndCriminalListEntity;
import com.aml.srv.core.efrmsrv.entity.FS_UAPAListEntity;
import com.aml.srv.core.efrmsrv.entity.FS_UNSCRListEntity;
import com.aml.srv.core.efrmsrv.repo.CustomerDetailsService;
import com.aml.srv.core.efrmsrv.repo.FS_FIUIndCriminalListRepositry;
import com.aml.srv.core.efrmsrv.repo.FS_UAPAListRepositry;
import com.aml.srv.core.efrmsrv.repo.FS_UNSCRListRepositry;
import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.Range;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;


@Service("CUSTOMER_MATCHService")
public class CustomerMatchFact implements FactInterface {

	@Autowired
	CustomerDetailsService customerDetailsService;

	@Autowired
	FS_UAPAListRepositry<?> fS_UAPAListRepositry;

	@Autowired
	FS_UNSCRListRepositry<?> fS_UNSCRListRepositry;

	@Autowired
	FS_FIUIndCriminalListRepositry<?> fS_FIUIndCriminalListRepositry;

	@Autowired
	JaroWinklerSimilarity similarity;
	
	@Autowired
	ParquetService parquetService;
	
	@Autowired
	CustomerServiceForParquet customerServiceForParqute;

	@Autowired
	Soundex soundex;

	private Logger LOGGER = LoggerFactory.getLogger(CustomerMatchFact.class);

	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj,List<ComputedFactsVO> computedFacts ) {

		ComputedFactsVO computedFactsVOObj = null;
		LOGGER.info("REQID : [{}]::::::::::::CustomerProfileFact@getFactExecutor (ENTRY) Called::::::::::",requVoObjParam.getReqId());
		String factName = null, accNo = null, custId = null, transMode = null, transType = null, txnTime = null,
				txnId = null, reqId = null;
		try {
			computedFactsVOObj = new ComputedFactsVO();
			accNo = requVoObjParam.getAccountNo();
			custId = requVoObjParam.getCustomerId();
			txnId = requVoObjParam.getTxnId();
			reqId = requVoObjParam.getReqId();
			transMode = requVoObjParam.getTransactionMode();
			transType = requVoObjParam.getTxnType();
			factName = factSetObj.getFact();
			Integer days = factSetObj.getDays();
			Integer hours = factSetObj.getHours();
			Integer months = factSetObj.getMonths();
			txnTime = requVoObjParam.getTxn_time();
			Range range = factSetObj.getRange();
			double nameScore = 0.0;
			computedFactsVOObj.setFact(factName);
			computedFactsVOObj.setStrValue("false");
			String condition = factSetObj.getCondition();
			computedFactsVOObj.setValue(new BigDecimal(0));
			String custName = null;
			String country = null;
			if (condition != null) {
				CustomerDetailsParquetEntity custDetails = customerServiceForParqute.getCustParqueEntity(custId, accNo);
				if (condition.equals("UNSCR")) {
					computedFactsVOObj.setStrType("str");
					//CustomerDetailsEntity custDetails = customerDetailsService.getCustomerDetails(reqId, custId);
					if (custDetails != null && StringUtils.isNotBlank(custDetails.getCustomername()) && StringUtils.isNotBlank(custDetails.getCountry())) {
						custName = custDetails.getCustomername();
						country = custDetails.getCountry();
						List<FS_UAPAListEntity> uapaList = fS_UAPAListRepositry.findAll();
						for (FS_UAPAListEntity uapa : uapaList) {
							if (custName != null && uapa.getName() != null) {
								double score = similarity.apply(custName.toLowerCase(), uapa.getName().toLowerCase());
								boolean soundMatch = soundmatch(custName.toLowerCase(), uapa.getName().toLowerCase());
								nameScore = score * 100;
								if ((nameScore > 80 || soundMatch)) {
									computedFactsVOObj.setFact(factName);
									computedFactsVOObj.setStrValue("true");
								}
							}
						}
					}

				} else if (condition.equals("UNSCR")) {
					computedFactsVOObj.setStrType("str");
					//CustomerDetailsEntity custDetails = customerDetailsService.getCustomerDetails(reqId, custId);
					if (custDetails != null && StringUtils.isNotBlank(custDetails.getCustomername()) && StringUtils.isNotBlank(custDetails.getCountry())) {

						custName = custDetails.getCustomername();
						country = custDetails.getCountry();
						List<FS_UNSCRListEntity> unscrList = fS_UNSCRListRepositry.findAll();
						for (FS_UNSCRListEntity unscr : unscrList) {
							if (custName != null && unscr.getName() != null) {
								double score = similarity.apply(custName.toLowerCase(), unscr.getName().toLowerCase());
								boolean soundMatch = soundmatch(custName.toLowerCase(), unscr.getName().toLowerCase());
								nameScore = score * 100;
								if ((nameScore > 80 || soundMatch)) {
									computedFactsVOObj.setFact(factName);
									computedFactsVOObj.setStrValue("true");
								}

							}
						}
					}

				} else if (condition.equals("CRIMINAL_LIST")) {
					computedFactsVOObj.setStrType("str");
					//CustomerDetailsEntity custDetails = customerDetailsService.getCustomerDetails(reqId, custId);
					if (custDetails != null && StringUtils.isNotBlank(custDetails.getCustomername()) && StringUtils.isNotBlank(custDetails.getCountry())) {

						custName = custDetails.getCustomername();
						country = custDetails.getCountry();
						List<FS_FIUIndCriminalListEntity> unscrList = fS_FIUIndCriminalListRepositry.findAll();
						for (FS_FIUIndCriminalListEntity unscr : unscrList) {
							if (custName != null && unscr.getName() != null) {
								double score = similarity.apply(custName.toLowerCase(), unscr.getName().toLowerCase());
								boolean soundMatch = soundmatch(custName.toLowerCase(), unscr.getName().toLowerCase());
								nameScore = score * 100;
								if ((nameScore > 80 || soundMatch)) {
									computedFactsVOObj.setFact(factName);
									computedFactsVOObj.setStrValue("true");
								}
							}
						}
					}

				} else if (condition.equals("TF_SUSPECT")) {
					computedFactsVOObj.setStrType("str");
					//CustomerDetailsEntity custDetails = customerDetailsService.getCustomerDetails(reqId, custId);
					if (custDetails != null && StringUtils.isNotBlank(custDetails.getCustomername()) && StringUtils.isNotBlank(custDetails.getCountry())) {
						custName = custDetails.getCustomername();
						country = custDetails.getCountry();
						List<FS_FIUIndCriminalListEntity> unscrList = fS_FIUIndCriminalListRepositry.findAll();
						for (FS_FIUIndCriminalListEntity unscr : unscrList) {
							if (custName != null && unscr.getName() != null) {
								double score = similarity.apply(custName.toLowerCase(), unscr.getName().toLowerCase());
								boolean soundMatch = soundmatch(custName.toLowerCase(), unscr.getName().toLowerCase());
								nameScore = score * 100;
								if ((nameScore > 80 || soundMatch)) {
									computedFactsVOObj.setFact(factName);
									computedFactsVOObj.setStrValue("true");
								}
							}
						}

					}

				} else {LOGGER.debug("REQID : [{}] - Given Fact Not Match....",requVoObjParam.getReqId()); }

			} else { LOGGER.debug("REQID : [{}] - Condition is Null....", requVoObjParam.getReqId());}

		} catch (Exception e) {
			LOGGER.error("REQID : [{}] - Exception found in CustomerProfileFact@getFactExecutor : {}", requVoObjParam.getReqId(), e);
		} finally {
			LOGGER.info("REQID : [{}]::::::::::::CustomerProfileFact@getFactExecutor (EXIT) End::::::::::\n\n",
					requVoObjParam.getReqId());
		}
		return computedFactsVOObj;

	}
	/**
	 * 
	 * @param name1
	 * @param name2
	 * @return
	 */
	public boolean soundmatch(String name1, String name2) {
		boolean score = soundex.encode(name1).equals(soundex.encode(name2));
		return score;
	}

}