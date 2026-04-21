package com.aml.srv.core.efrmsrv.rule.fact.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aml.srv.core.efrmsrv.rule.intr.FactInterface;
import com.aml.srv.core.efrmsrv.rule.process.request.Factset;
import com.aml.srv.core.efrmsrv.rule.process.request.RuleRequestVo;
import com.aml.srv.core.efrmsrv.rule.process.response.ComputedFactsVO;
import com.aml.srv.core.efrmsrv.utils.RuleWhizConstants;

/**
 * Find Variance for Given values
 */
@Service("VARIANCEService")
public class VarianceFindFact implements FactInterface {

	private Logger LOGGER = LoggerFactory.getLogger(VarianceFindFact.class);

	@Override
	public ComputedFactsVO getFactExecutor(RuleRequestVo requVoObjParam, Factset factSetObj, List<ComputedFactsVO> computedFacts) {
		LOGGER.info("REQID : [{}] - VarianceFindFact@getFactExecutor Method Called. ", requVoObjParam.getReqId());
		ComputedFactsVO computedFactsVOObj = null;
		try {
			computedFactsVOObj = new ComputedFactsVO();
			if (computedFacts != null && computedFacts.size() > 0) {
				BigDecimal sumOfCashDeposit = BigDecimal.ZERO;
				BigDecimal sumOfCashWithdraw = BigDecimal.ZERO;
				for (ComputedFactsVO comFactVoObj : computedFacts) {
					if (comFactVoObj != null && StringUtils.isNotBlank(comFactVoObj.getFact())
							&& comFactVoObj.getFact().toUpperCase().contains(RuleWhizConstants.DEPOSITS)) {
						sumOfCashDeposit = comFactVoObj.getValue();
					}
					if (comFactVoObj != null && StringUtils.isNotBlank(comFactVoObj.getFact())
							&& comFactVoObj.getFact().toUpperCase().contains(RuleWhizConstants.WITHDRAWALS)) {
						sumOfCashWithdraw = comFactVoObj.getValue();
					}
					if (sumOfCashWithdraw.compareTo(BigDecimal.ZERO) > 0
							&& sumOfCashDeposit.compareTo(BigDecimal.ZERO) > 0) {
						break;
					}
				}
				LOGGER.info("REQID : [{}] - SUM OF Debits / Withdraws : [{}] - SUM OF Credits / Deposits : [{}]",
						requVoObjParam.getReqId(), sumOfCashWithdraw, sumOfCashDeposit);
				BigDecimal actualNetCash = sumOfCashDeposit.subtract(sumOfCashWithdraw);
				BigDecimal fraction = actualNetCash.divide(  sumOfCashDeposit, 10, RoundingMode.HALF_UP);
				BigDecimal variance = fraction.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP); 
				BigDecimal rounded = variance.setScale(2, RoundingMode.HALF_UP);

				LOGGER.info("REQID : [{}] - Variance : [{}] - Rounded : [{}]", requVoObjParam.getReqId(), variance, rounded);
				computedFactsVOObj.setStrType(RuleWhizConstants.VALUE_NUM);
				computedFactsVOObj.setFact(factSetObj.getFact());
				computedFactsVOObj.setValue(variance);

			} else {
				LOGGER.info("REQID : [{}] - VarianceFindFact@getFactExecutor Method List of computedFacts is {} ", requVoObjParam.getReqId(), computedFacts);
			}

		} catch (Exception e) {
			computedFactsVOObj = null;
			LOGGER.error("Exception found in VarianceFindFact@getFactExecutor Method : {}", e);
		} finally {
			LOGGER.info("REQID : [{}] - VarianceFindFact@getFactExecutor Method End. ", requVoObjParam.getReqId());
		}
		return computedFactsVOObj;
	}
}
