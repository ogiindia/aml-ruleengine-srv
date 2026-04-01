package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrm.parquet.entity.TransactionParquetMppaing;
import com.aml.srv.core.efrm.parquet.service.TransactionServiceForParqute;
import com.aml.srv.core.efrmsrv.repo.TransactionDetailsDTO;
import com.aml.srv.core.efrmsrv.rule.intr.SchemaInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.ruleengine.Schema;

@Service("SIMPLERULESService")
public class SimpleRuelsBySchema implements SchemaInterface {

	private Logger LOGGER = LoggerFactory.getLogger(SimpleRuelsBySchema.class);
	
	@Autowired
	TransactionServiceForParqute transactionServiceForParqute;

	@Override
	public ComputedFactsVO getScheamExecutor(RuleRequestVo requVoObjParam, Schema scheam,
			List<ComputedFactsVO> computedFacts) {
		LOGGER.info("REQID : [{}]::::::::::::SimpleRuelsBySchema@getScheamExecutor (ENTRY) Called::::::::::", requVoObjParam.getReqId());
		//TransactionDetailsDTO dto = null;
		List<TransactionDetailsDTO> dto = null;
		ComputedFactsVO computedFactsVOObj = null;
		try {
			computedFactsVOObj =  new ComputedFactsVO();
			if(scheam!=null) {
				String tagName = scheam.getTag();
				String value = scheam.getValue();
				String conndition =  scheam.getCondition();
				String joinExpression =  scheam.getJoinexpression();
				String type =  scheam.getType();
				
				dto = transactionServiceForParqute.getTransDtoFromParqute(scheam,requVoObjParam.getReqId(), requVoObjParam.getAccountNo(), requVoObjParam.getTxnId(), requVoObjParam.getCustomerId());
				if (dto != null && dto.size() > 0) {
					computedFactsVOObj.setStrType("num");
					computedFactsVOObj.setFact("SIMPLERULES");
					computedFactsVOObj.setValue(new BigDecimal(dto.size()));
				} else {
					computedFactsVOObj.setStrType("num");
					computedFactsVOObj.setFact("SIMPLERULES");
					computedFactsVOObj.setValue(new BigDecimal(0));
				}
				
			}

		} catch (Exception e) {
			LOGGER.error("Exception found in SimpleRuelsBySchema@processOfReq : {}", e);
		} finally {
			LOGGER.info("REQID : [{}]::::::::::::SimpleRuelsBySchema@getScheamExecutor (EXIT) End::::::::::", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}

}
