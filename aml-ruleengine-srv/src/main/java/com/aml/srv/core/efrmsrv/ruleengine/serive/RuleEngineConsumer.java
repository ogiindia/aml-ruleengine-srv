package com.aml.srv.core.efrmsrv.ruleengine.serive;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parquet.entity.TransactionParquetMppaing;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrmsrv.kafka.repo.FinSecIndicatorVO;
import com.aml.srv.core.efrmsrv.repo.TxnDetailsImpl;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;
import com.google.gson.Gson;

/**
 * This class used for receive the file import status from AML Server
 */
@Service
public class RuleEngineConsumer {

	private Logger LOGGER = LoggerFactory.getLogger(RuleEngineConsumer.class);
	
	private static final Gson GSON = new Gson();

	@Autowired
	TxnDetailsImpl txnDetailsImpl;

	@Autowired
	private KafkaTemplate<String, String> template;
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

	/**
	 * 
	 * @param msg
	 * @param acknowledgment Pushing from Service Project PublishData2Kafka.class
	 */
	@KafkaListener(id = RuleWhizConstants.KAFKA_PUB_ID, topics = RuleWhizConstants.KAFKA_PUB_TOPIC, groupId = RuleWhizConstants.KAFKA_PUB_GRP)
	public void onEventResult(String msg, Acknowledgment acknowledgment) {
		processTransaction(msg);
		acknowledgment.acknowledge();
	}

	/**
	 * 
	 * @param msg
	 */
	private void processTransaction(String msg) {
		FinSecIndicatorVO finSecIndicatorVOObj = null;
		Integer minusDays =1;
		try {
			if (StringUtils.isNotBlank(msg)) {
				LOGGER.info("Received Message [IF]: {}", msg);
				finSecIndicatorVOObj = GSON.fromJson(msg, FinSecIndicatorVO.class);
				if (finSecIndicatorVOObj != null) {
					LOGGER.info("FinSecIndicatorVO conversion [IF]: {}", finSecIndicatorVOObj);
					if (finSecIndicatorVOObj.isFileCompletedStatus()) {
						/**Get from Parqute**/
						List<TransactionParquetMppaing> tansParquMappLst = 	transactionServiceForParqute.getTransDetailsFromProperty(minusDays);
						if(tansParquMappLst!=null && !tansParquMappLst.isEmpty()) {
							for(TransactionParquetMppaing trasnDataEntity : tansParquMappLst) {
								LOGGER.debug("AMLRuleEngineConsumer - Transaction ID  : [{}]", trasnDataEntity.getTransactionid());
								//Record Publish kafka for Rule ENgine Dynamic Listener(Dynamic Listener In RuleExecutorService.java same Project)
								ProducerRecord<String, String> record = new ProducerRecord<String, String>(RuleWhizConstants.KAFKA_PUB_TOPIC_DYNAMIC, null, trasnDataEntity.getTransactionid(), new Gson().toJson(trasnDataEntity));

								template.send(record); // Published successfully
								//template.send(RuleWhizConstants.KAFKA_PUB_TOPIC_DYNAMIC,  trasnDataEntity.getTransactionid(), new Gson().toJson(trasnDataEntity));
								// Record publish kafak for Sanction List Check (Listener In RT Engine Project)
								ProducerRecord<String, String> recordForSanctionList = new ProducerRecord<String, String>(
										RuleWhizConstants.KAFKA_PUB_TOPIC_SANCTIONLIST, null,
										trasnDataEntity.getTransactionid(), new Gson().toJson(trasnDataEntity));
								template.send(recordForSanctionList);
							}
						}
					
						/****From Table*****/
						/*
						List<TransactionDetailsEntity> transDetailsEntyLstObj = txnDetailsImpl.toGetTxnDetailsBydate();
						LOGGER.info("AMLRuleEngineConsumer - transListObj  : [{}]", transDetailsEntyLstObj);

						for (TransactionDetailsEntity trasnDataEntity : transDetailsEntyLstObj) {
							LOGGER.debug("AMLRuleEngineConsumer - Transaction Batch ID  : [{}]", trasnDataEntity.getTransactionBatchId());
							//Record Publish kafka for Rule ENgine Dynamic Listener(Dynamic Listener In RuleExecutorService.java same Project)
							ProducerRecord<String, String> record = new ProducerRecord<String, String>(
									RuleWhizConstants.KAFKA_PUB_TOPIC_DYNAMIC, null,
									trasnDataEntity.getTransactionBatchId(), new Gson().toJson(trasnDataEntity));

							template.send(record); // Published successfully

							// Record publish kafak for Sanction List Check (Listener In RT Engine Project)
							ProducerRecord<String, String> recordForSanctionList = new ProducerRecord<String, String>(
									RuleWhizConstants.KAFKA_PUB_TOPIC_SANCTIONLIST, null,
									trasnDataEntity.getTransactionBatchId(), new Gson().toJson(trasnDataEntity));
							template.send(recordForSanctionList);

						}
						*/
					} else {
						LOGGER.info("AMLRuleEngineConsumer isFileCompletedStatus [ELSE]: {}", finSecIndicatorVOObj.isFileCompletedStatus());
					}
				} else {
					LOGGER.info("AMLRuleEngineConsumer conversion [ELSE]: {}", finSecIndicatorVOObj);
				}
			} else {
				LOGGER.info("Received Message [ELSE]: {}", msg);
			}
		} catch (Exception e) {
			LOGGER.error("Exception found in AMLRuleEngineConsumer@processTransaction : {}", e);
		} finally {
			finSecIndicatorVOObj = null;
		}
	}
}
