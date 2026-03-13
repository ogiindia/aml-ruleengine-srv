package com.aml.srv.core.efrm.cust.scoring;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.repo.RiskParamImpl;
import com.aml.srv.core.efrmsrv.repo.RiskValueImpl;
import com.aml.srv.core.efrmsrv.utils.CommonUtils;
import com.efrm.rt.srv.core.recordDTO.PresenceScoreFeatureEnc;
import com.efrm.rt.srv.core.recordDTO.RiskParamFieldDTO;
import com.efrm.rt.srv.core.recordDTO.RiskValueFieldsDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * Customer Genuineness Service
 */
@Slf4j
@Component
public class GenuinenessService {

	public static final Logger LOGGER = LoggerFactory.getLogger(GenuinenessService.class);
	
	@Autowired
	RiskValueImpl riskValueImpl;

	@Autowired
	RiskParamImpl riskParamImpl;
	
	@Autowired
	BuildDataSetML buildDataSetML;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	CustomerDetailsMLUse customerDetailsMLUse;
	
	@Autowired
	CommonUtils commonUtils;
	 
	/**
	 * 
	 * @return
	 */
	public Map<String, List<String>> getSchema(){
		List<RiskParamFieldDTO> rsikParamEntyLst = null;
		Map<String, List<String>> schema = null;
		try {
			schema = new LinkedHashMap<>();
			rsikParamEntyLst = riskParamImpl.toGetRiskParam();
			for (RiskParamFieldDTO riskparamFildto : rsikParamEntyLst) {
				List<RiskValueFieldsDTO> riskValueFieldDTO = riskValueImpl.toGetRiskParamValue(riskparamFildto.getId());
				List<String> stringList = riskValueFieldDTO.stream().map(RiskValueFieldsDTO::getParamValues) // replace-with-actual-getter
						.collect(Collectors.toList());
				schema.put(riskparamFildto.getFieldName(), stringList);
			}
		} catch (Exception e) {
			LOGGER.info("Exception found in GenuinenessService@getSchema : {}", e);
		} finally { rsikParamEntyLst = null; }
		return schema;
	}
	
	
	/**
	 * Get Individual Customer Genuiness Scores
	 * @param custIdParam
	 */
	public CustmerGenuinessDetailsRecord toGetIndividualGenuinenessScore(String custIdParam) {
		Long startDate = new Date().getTime();
		 LOGGER.info("###########Customer - toGetIndividualGenuinenessScore Method Called..............");
		PresenceScoreFeatureEnc presenceScoreFeatureEnc = null;
		//CustomerDetailsEntity  custenttyobj = null;
		List<Map<String, Collection<String>>> samples = null;
		CustmerGenuinessDetailsRecord custGenuinessDtslrecObj = null;
		Map<String, List<String>> schema = null;
		try {
			presenceScoreFeatureEnc = (PresenceScoreFeatureEnc) redisService.toPullObjectFrmRedis("CUST-GENUINE-SYNTHETIC");
		
			LOGGER.debug("presenceScoreFeatureEnc ------------------ {}",presenceScoreFeatureEnc);
			//	custenttyobj = customerDetailsRepoImpl.getCustomerDetailsByCustId(custIdParam);
			samples = new ArrayList<>();
			schema = getSchema();
			samples = customerDetailsMLUse.getCustomerData(schema, samples, custIdParam);
			LOGGER.debug("samples>>>>>>>>>>>>>>>>>>> : {}", samples);
			
			if (samples != null && samples.size() > 0) {
				custGenuinessDtslrecObj = toGetScoreBySingleInput(presenceScoreFeatureEnc, samples.get(0), schema);
			} else {// Double prob,String risk, Long riskScore, Double logItScore
				custGenuinessDtslrecObj = new CustmerGenuinessDetailsRecord(new Double(0.0), "", new Long(0),
						new Double(0.0));
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in GenuinenessService@toGetIndividualGenuinenessScore : {}",e);
		} finally {
			Long endTime = new Date().getTime();
	        LOGGER.info("###########Customer - toGetIndividualGenuinenessScore - End Time : [{}]\n\n", commonUtils.findIsHourMinSec((endTime - startDate)));
	        presenceScoreFeatureEnc = null; samples = null; schema=null;
		}
		return custGenuinessDtslrecObj;
	}
	
	/**
	 * 
	 * @param presenceScoreFeatureEnc
	 * @param input
	 * @param schema
	 * @return
	 */
	public CustmerGenuinessDetailsRecord toGetScoreBySingleInput(PresenceScoreFeatureEnc presenceScoreFeatureEnc,Map<String, Collection<String>> input, Map<String, List<String>> schema) {
		CustmerGenuinessDetailsRecord custGenuineeDtlsRcodOj = null;
		BigDecimal bdProb = null;
		BigDecimal bdLogi = null;
		try {
			LOGGER.info("-----------------------welcome to toGetScoreBySingleInput Method--------------"); 
			FeatureSpace space = new FeatureSpace(schema, true); // include bias
			FeatureEncoder encoder = new FeatureEncoder(space);
			 long seed = 12345L;
		        Split split = buildDataSetML.trainTestSplit(presenceScoreFeatureEnc.getSamples(), presenceScoreFeatureEnc.getLabels(), 0.7, seed);

		        // 6.4 Encode train data
		        List<double[]> Xtrain = new ArrayList<>();
		        for (Map<String, Collection<String>> s : split.trainSamples) Xtrain.add(encoder.encode(s));

		        // 6.5 Train model
		        LogisticRegression model = new LogisticRegression(space);
		        model.setRandomSeed(42L);
		        model.fitSGD(Xtrain, split.trainLabels, /*epochs*/ 400, /*lr*/ 0.05, /*l2*/ 1e-5);
		        PresenceScorer scorer = new PresenceScorer(space, model);
			LOGGER.info("---------------->>>>input : {}",input);
	       // LOGGER.info("---------------->>>>presenceScoreFeatureEnc Scorer : {}",presenceScoreFeatureEnc.getScorer());
	        Double prob = scorer.score(input);
	        Double logit = scorer.logit(input);
	        String risk = buildDataSetML.riskBand(prob, 0.40, 0.75); // configurable
	        Long riskScore = Math.round((1-prob)*100);
	        bdProb = new BigDecimal(prob).setScale(2, RoundingMode.HALF_UP);
	        bdLogi = new BigDecimal(logit).setScale(2, RoundingMode.HALF_UP);
	       // LOGGER.info("Demo Input Prob=%.4f -> Risk=%s  -> RiskScore=%s%n  -> LogIt=%.4f", prob, risk,riskScore);
	        LOGGER.info("Demo Input Prob={} -> Risk={}  -> RiskScore={} -> LogIt={}",bdProb.doubleValue(),risk,riskScore,bdLogi.doubleValue());
	        custGenuineeDtlsRcodOj = new CustmerGenuinessDetailsRecord(bdProb.doubleValue(),risk,riskScore, bdLogi.doubleValue());
		} catch (Exception e) {
			LOGGER.error("Exception found in GenuinenessService@toGetScoreBySingleInput Method : {}", e);
		} finally {bdProb = null;
		bdLogi = null; }
		return custGenuineeDtlsRcodOj;
	}	
}