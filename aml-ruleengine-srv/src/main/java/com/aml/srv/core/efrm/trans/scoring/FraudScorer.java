package com.aml.srv.core.efrm.trans.scoring;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.repo.MapperImpl;
import com.efrm.rt.srv.core.recordDTO.MapperSummarizationFiledDTO;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;

@Component
public class FraudScorer {

	private Logger LOGGER = LoggerFactory.getLogger(FraudScorer.class);

	String tranModelfileName = "/fraud_model.json";
	
	@Value("${trans.model.path:./}")
	String transModelPath;
	
	@Autowired
	MapperImpl mapperImpl;
	
	@Autowired
	TransactionDataLoader transactionDataLoader;

	public TransactionGenuinessDetailsRecord toGetTransFraudScorer(float[] features ) {
		LOGGER.info("######FRDSCORE####FraudScorer@toGetTransFraudScorer Method Called------------------------------");

		String score = null;
		String DMatrix = "NEWDMatrix";
		TransactionGenuinessDetailsRecord transGenDtlRcodObj = null;
		try {
			// Load the trained model
			// Booster booster = Booster.loadModel("src/main/resources/fraud_model.xgb");
			// Example transaction features (amount, merchant_id, location_id) (Load data
			// from transaction table)
			//float[] features = new float[] { 100.0f, 4.0f, 7.0f };
			if(features ==null || features.length==0) {
				features = new float[] { 100.0f, 4.0f, 7.0f };
			}
			
			if(StringUtils.equalsIgnoreCase(DMatrix, "NEWDMatrix")) {
				LOGGER.info("transModelPath : {}", transModelPath + tranModelfileName);
				/*
				 * Map<Integer, Float> fmap = new HashMap<>(); for (int i = 0; i <
				 * features.length; i++) { fmap.put(i, (float) features[i]); }
				 * Optional<Predictor> predictor = loadPredictor(transModelPath); Predictor pp=
				 * predictor.orElse(null);double[] pred =
				 * pp.predict(FVec.Transformer.fromMap(fmap));//double modelProb = (pred.length
				 * > 0) ? pred[0] : 0.0;
				 */
				Booster booster = XGBoost.loadModel(transModelPath + tranModelfileName);
				// Wrap features in DMatrix (1 row, 3 columns)
				DMatrix dmat = new DMatrix(features, 1, features.length, Float.NaN);

				float[][] pred = booster.predict(dmat);

				double modelProb = (pred.length > 0 && pred[0].length > 0) ? pred[0][0] : 0.0;
				double blended = UltraFraudUtils.BASE_CLASS_PROBABILITY + (1.0 - UltraFraudUtils.BASE_CLASS_PROBABILITY) * modelProb;
				double score1 = Math.round(blended * 10000.0) / 100.0; // round to 2 decimals
				String[] levelDecision = UltraFraudUtils.applyDecision(score1);
				LOGGER.info("pred : [{}]", pred);
				LOGGER.info("modelProb : [{}]", modelProb);
				LOGGER.info("blended : [{}]", blended);
				LOGGER.info("score1 : [{}]", score1);
				LOGGER.info("levelDecision : [{}]", levelDecision[0]);
				score = String.valueOf(score1);
				transGenDtlRcodObj = new TransactionGenuinessDetailsRecord(pred, modelProb, blended, score, levelDecision);
			} else {
				FileInputStream fis = new FileInputStream(transModelPath + tranModelfileName);
				LOGGER.info("--------->>>>fis : [{}]", fis);
				Booster booster = XGBoost.loadModel(fis);
				// Wrap features in DMatrix (1 row, 3 columns)
				DMatrix dmat = new DMatrix(features, 1, features.length, Float.NaN);
				// labels must be 0 or 1
				// Predict fraud risk score
				float[][] pred = booster.predict(dmat);
				float percentage = pred[0][0] * 100;
				LOGGER.info("Fraud risk score 1 Exac -> : [{}]", pred[0][0]);
				//score = String.format("%.2f", pred[0][0]);
				
				double modelProb = (pred.length > 0 && pred[0].length > 0) ? pred[0][0] : 0.0;
				double blended = UltraFraudUtils.BASE_CLASS_PROBABILITY + (1.0 - UltraFraudUtils.BASE_CLASS_PROBABILITY) * modelProb;
				double score1 = Math.round(blended * 10000.0) / 100.0; // round to 2 decimals
				String[] levelDecision = UltraFraudUtils.applyDecision(score1);
				
				// score = String.valueOf(preds[0][0]);
				LOGGER.info("Fraud risk score 2 Percentage - > : [{}]", percentage);
				score = String.format("%.2f", percentage);
				//score = String.valueOf(score1);
				transGenDtlRcodObj = new TransactionGenuinessDetailsRecord(pred, modelProb, blended, score, levelDecision);
			}

		} catch (Exception e) {
			LOGGER.error("Exception found in FraudScorer@toGetTransFraudScorer : {}", e);
		} finally {
			LOGGER.info("######FRDSCORE####FraudScorer@toGetTransFraudScorer Method END------------------------------");
		}
		return transGenDtlRcodObj;
	}
	
	public float[] getMapperDtlForTrans(String transId) {
		LOGGER.info("DataLoader@getMapperDtlForTrans Method Called.......");
		List<MapperSummarizationFiledDTO> mapperSummdto = null;
		ArrayList<Float> tem = new ArrayList<>();
		float[] arr = null;
		try {
			mapperSummdto = mapperImpl.getMapperByIdentifier("TRANSACTION_SCORE");
			for (MapperSummarizationFiledDTO mapperObj : mapperSummdto) {
				tem.add(transactionDataLoader.getDataFromDynamicQueryTrans(mapperObj, transId));
			}
			LOGGER.info("features size : {}", tem.size());
			arr = new float[tem.size()];
			for (int i = 0; i < tem.size(); i++) {
				arr[i] = tem.get(i);
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in DataLoader@getMapperDtlForTrans : {}", e);
			arr = null;
		} finally {
			mapperSummdto = null;
			LOGGER.info("DataLoader@getMapperDtlForTrans Method End.......");
		}
		return arr;
	}
}
