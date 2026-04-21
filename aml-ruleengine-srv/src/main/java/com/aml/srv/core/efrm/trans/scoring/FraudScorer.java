package com.aml.srv.core.efrm.trans.scoring;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aml.file.pro.core.efrmsrv.startup.config.ColumnMapping;
import com.aml.srv.core.efrm.parquet.entity.TransactionParquetMppaing;
import com.aml.srv.core.efrm.parquet.service.ParquetService;
import com.aml.srv.core.efrm.parquet.service.TransactionCustomFieldRDTO;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrmsrv.repo.MapperImpl;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

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
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;
	
	@Autowired
	ParquetService parquetService;

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
	
	/**
	 * Get From Parqute file
	 * @param transId
	 * @return
	 */
	public float[] getMapperDtlForTrans(String transId) {
		LOGGER.info("DataLoader@getMapperDtlForTrans Method Called.......");
		//List<MapperSummarizationFiledDTO> mapperSummdto = null;
		ArrayList<Float> tem = new ArrayList<>();
		float[] arr = null;
		TransactionCustomFieldRDTO transCustomFldDTOObj = null;
		List<ColumnMapping> columMapLst = null;
		List<String> scoringColumnlst = null;
		List<TransactionParquetMppaing> tranPArMapLst =	null;
		try {
			//mapperSummdto = mapperImpl.getMapperByIdentifier("TRANSACTION_SCORE");
			/*for (MapperSummarizationFiledDTO mapperObj : mapperSummdto) {
				tem.add(customerDetailsMLUse.getDataFromDynamicQueryTrans(mapperObj, transId));
			}*/
			transCustomFldDTOObj = parquetService.getConfig(RuleWhizConstants.TRANSACTIONS);
			if(transCustomFldDTOObj!=null) {
				scoringColumnlst = new ArrayList<>();
				columMapLst = transCustomFldDTOObj.columnMappLstObj();
				for(ColumnMapping clmaPaobj : columMapLst) {
					if(StringUtils.isNotBlank( clmaPaobj.getFunc()) && 
							(clmaPaobj.getFunc().equalsIgnoreCase("Y") 
							|| clmaPaobj.getFunc().equalsIgnoreCase("Yes"))) {
						scoringColumnlst.add(clmaPaobj.getTo());
					}
				}
			}
			
			tranPArMapLst =	transactionServiceForParqute.getTransDetailsFromParquteFromTrnsid(1, transId);
			
			for(TransactionParquetMppaing transPArMap : tranPArMapLst) {
				Map<String, Object> mapOb = extractValues(transPArMap,scoringColumnlst);
				for (String col : scoringColumnlst) {
				    Object val = mapOb.get(col);
				    Float score = null;

				    if (val instanceof Number) {
				        score = ((Number) val).floatValue();
				    } else if (val instanceof String && !((String) val).trim().isEmpty()) {
				        score = Float.parseFloat(((String) val).trim());
				    } else {
				    	score = 0.0f;
				    }
				    tem.add(score);
				}	
			}
			LOGGER.info("features size : {}", tem.size());
			arr = new float[tem.size()];
			for (int i = 0; i < tem.size(); i++) {
				arr[i] = tem.get(i);
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in DataLoader@getMapperDtlForTrans : {}", e); arr = null;
		} finally {
			transCustomFldDTOObj = null;  columMapLst = null; scoringColumnlst = null;tranPArMapLst =	null;
			LOGGER.info("DataLoader@getMapperDtlForTrans Method End.......");
		}
		return arr;
	}
	
	public Map<String, Object> extractValues(TransactionParquetMppaing bean, List<String> columnNames) {
		Map<String, Object> values = new LinkedHashMap<>(); // keeps order of columnNames
		Class<?> type = bean.getClass();
		for (String col : columnNames) {
			String fieldName = parquetService.toCamel(col); // e.g. "TRANSACTIONID" -> "transactionid"
			LOGGER.info("fieldName ----------------->>>[{}]", fieldName);
			try {
				Field f = type.getDeclaredField(fieldName);
				f.setAccessible(true);
				Object value = f.get(bean);
				values.put(col, value); // key = column name, value from bean
			} catch (NoSuchFieldException e) {
				// column not present in bean, skip or log
					LOGGER.debug("No field for column: {}", col);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Cannot access field: " + fieldName, e);
			}
		}
		return values;
	}
		/*** This is data get from table***/
	/*public float[] getMapperDtlForTrans(String transId) {
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
	} */
}
