package com.aml.srv.core.efrm.parquet.entity;

import java.io.Serializable;

import jakarta.persistence.Column;

public class BankandBranchDetailsParquetEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "BRANCHCODE")
	private String branchcode;

	@Column(name = "BRANCHNAME")
	private String branchname;

	@Column(name = "BANKCODE")
	private String bankcode;

	public String getBranchcode() {
		return branchcode;
	}

	public void setBranchcode(String branchcode) {
		this.branchcode = branchcode;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}
}