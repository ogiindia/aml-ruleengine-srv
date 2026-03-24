package com.aml.file.pro.core.efrmsrv.startup.config;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TrasactionMppList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SerializedName("transactionMapping")
	List<TransactionMapping> transactionMapping;

	@SerializedName("transactionMapping")
	public List<TransactionMapping> getTransactionMapping() {
		return transactionMapping;
	}

	@SerializedName("transactionMapping")
	public void setTransactionMapping(List<TransactionMapping> transactionMapping) {
		this.transactionMapping = transactionMapping;
	}
}
