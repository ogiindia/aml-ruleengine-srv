package com.efrm.rt.srv.core.recordDTO;

public interface AlretIntDTO {

	String getTranRiskType();

	Double getTranRiskScore();

	String getCustRiskType();

	Double getCustRiskScore();

	String getTransactionId();
	
	String getAlertParentId();
}
