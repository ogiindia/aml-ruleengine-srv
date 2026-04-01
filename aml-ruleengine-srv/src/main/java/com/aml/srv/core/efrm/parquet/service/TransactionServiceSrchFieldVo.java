package com.aml.srv.core.efrm.parquet.service;

import java.io.Serializable;

import com.aml.srv.core.efrmsrv.rule.process.request.Range;

public class TransactionServiceSrchFieldVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String accNo;
	private String custId;
	private String txnNo;
	private String transMode;
	private String transType;
	private String factName;
	private Integer days;
	private Integer hours;
	private Integer months;
	private Range range;
	private String amount;
	private String withdarwDeposit;
	private String conditionName;
	private String transactionDate;
	private boolean foreignCountryCode;

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getTxnNo() {
		return txnNo;
	}

	public void setTxnNo(String txnNo) {
		this.txnNo = txnNo;
	}

	public String getTransMode() {
		return transMode;
	}

	public void setTransMode(String transMode) {
		this.transMode = transMode;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getFactName() {
		return factName;
	}

	public void setFactName(String factName) {
		this.factName = factName;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	public Integer getMonths() {
		return months;
	}

	public void setMonths(Integer months) {
		this.months = months;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getWithdarwDeposit() {
		return withdarwDeposit;
	}

	public void setWithdarwDeposit(String withdarwDeposit) {
		this.withdarwDeposit = withdarwDeposit;
	}

	public String getConditionName() {
		return conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public boolean isForeignCountryCode() {
		return foreignCountryCode;
	}

	public void setForeignCountryCode(boolean foreignCountryCode) {
		this.foreignCountryCode = foreignCountryCode;
	}
	

}
