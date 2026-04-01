package com.aml.srv.core.efrm.parquet.service;

import java.io.Serializable;

public class TransactionAccountCustDetailsDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String custId;
	private String bankCode;
	private String branchCode;
	private String cusomerName;
	private String panNo;

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCusomerName() {
		return cusomerName;
	}

	public void setCusomerName(String cusomerName) {
		this.cusomerName = cusomerName;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}
}
