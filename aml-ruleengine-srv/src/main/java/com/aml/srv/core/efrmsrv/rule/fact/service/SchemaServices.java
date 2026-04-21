package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parquet.entity.CustomerDetailsParquetEntity;
import com.aml.srv.core.efrm.parquet.entity.TransactionParquetMppaing;
import com.aml.srv.core.efrm.parquet.service.CustomerServiceForParquet;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrmsrv.rule.intr.SchemaInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.ruleengine.Schema;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

@Service("SCHEMAService")
public class SchemaServices implements SchemaInterface {
	
	private Logger LOGGER = LoggerFactory.getLogger(SchemaServices.class);
	
	@Autowired
	CustomerServiceForParquet customerServiceForParqute;
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

	@Override
	public ComputedFactsVO getScheamExecutor(RuleRequestVo requVoObjParam, Schema scheam,
			List<ComputedFactsVO> computedFacts) {
		
		ComputedFactsVO computedFactsVO =null;
		String accNo = null, custId = null,
				txnId = null, ruleId = null;
		try {
			computedFactsVO = new ComputedFactsVO();
			accNo = requVoObjParam.getAccountNo();
			custId = requVoObjParam.getCustomerId();
			txnId = requVoObjParam.getTxnId();
			ruleId = requVoObjParam.getRuleId();
			LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - SchemaServices@getScheamExecutor Method Called.....", txnId,ruleId);
			String schemaTag = scheam.getTag();
			if (StringUtils.isNotBlank(schemaTag) && schemaTag.contains("_")) {
				String parquetName = schemaTag.substring(0, schemaTag.indexOf("_"));
				String srchColumn = schemaTag.substring(schemaTag.indexOf("_") + 1, schemaTag.length());
				LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - SchemaServices@getScheamExecutor - parquetName : [{}] & srchColumn : [{}].....", txnId,ruleId, parquetName,srchColumn);
				
				if (StringUtils.isNotBlank(parquetName) && parquetName.equalsIgnoreCase(RuleWhizConstants.CUSTOMERS)) {
					CustomerDetailsParquetEntity custDetails = customerServiceForParqute.getCustParqueEntity(custId,accNo);

					if (StringUtils.isNotBlank(srchColumn) && srchColumn.contentEquals("customertype")) {
						computedFactsVO.setFieldTag(srchColumn);
						computedFactsVO.setStrType(RuleWhizConstants.VALUE_STR);
						computedFactsVO.setStrValue(custDetails.getCustomertype());
					}
				}
				if (StringUtils.isNotBlank(parquetName) && parquetName.equalsIgnoreCase(RuleWhizConstants.TRANSACTIONS)) {
					List<TransactionParquetMppaing> ltsDto = transactionServiceForParqute.getTransDetailsFromParquteFromTrnsid(1,txnId);
					if (ltsDto != null && ltsDto.size() > 0) {
						if (StringUtils.isNotBlank(srchColumn) && srchColumn.contentEquals("amount")) {
							computedFactsVO.setFieldTag(srchColumn);
							computedFactsVO.setStrType(RuleWhizConstants.VALUE_NUM);
							computedFactsVO.setValue(new BigDecimal(ltsDto.get(0).getAmount()));
						}
						if (StringUtils.isNotBlank(srchColumn) && srchColumn.contentEquals("depositorwithdrawal")) {
							computedFactsVO.setFieldTag(srchColumn);
							computedFactsVO.setStrType(RuleWhizConstants.VALUE_STR);
							computedFactsVO.setStrValue(ltsDto.get(0).getDepositorwithdrawal());
						}
						if (StringUtils.isNotBlank(srchColumn) && srchColumn.contentEquals("channelid")) {
							computedFactsVO.setFieldTag(srchColumn);
							computedFactsVO.setStrType(RuleWhizConstants.VALUE_STR);
							computedFactsVO.setStrValue(ltsDto.get(0).getChannelid());
						}
						if (StringUtils.isNotBlank(srchColumn) && srchColumn.contentEquals("transactiontype")) {
							computedFactsVO.setFieldTag(srchColumn);
							computedFactsVO.setStrType(RuleWhizConstants.VALUE_STR);
							computedFactsVO.setStrValue(ltsDto.get(0).getTransactiontype());
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Trans-ID : [{}] - Rule-ID : [{}] - Exception found in SchemaServices : {}",txnId,ruleId, e);
		} finally {LOGGER.info("Trans-ID : [{}] - Rule-ID : [{}] - SchemaServices@getScheamExecutor Method End.....\n", txnId,ruleId);}
		return computedFactsVO;
	}
}