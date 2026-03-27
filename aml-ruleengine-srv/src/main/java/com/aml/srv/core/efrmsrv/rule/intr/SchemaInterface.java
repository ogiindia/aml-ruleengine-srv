package com.aml.srv.core.efrmsrv.rule.intr;

import java.util.List;

import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.ruleengine.Schema;

public interface SchemaInterface {

	public ComputedFactsVO getScheamExecutor(RuleRequestVo requVoObjParam, Schema scheam,List<ComputedFactsVO> computedFacts );
	
}
