package com.aml.srv.core.efrm.parquet.entity;

import jakarta.persistence.Column;

//@Entity
//@Table(name = "FS_TRN")
public class TransactionParquetMppaing {

	//@Id
	@Column(name = "TRANSACTIONBATCHID")
	private String transactionbatchid;

	@Column(name = "TRANSACTIONID")
	private String transactionid;

	@Column(name = "CUSTOMERID")
	private String customerid;

	@Column(name = "ACCOUNTNO")
	private String accountno;

	@Column(name = "BRANCHCODE")
	private String branchcode;

	@Column(name = "TRANSACTIONTYPE")
	private String transactiontype;

	@Column(name = "CHANNELID")
	private String channelid;

	@Column(name = "TRANSACTIONDATE")
	private String transactiondate;

	@Column(name = "AMOUNT")
	private String amount;

	@Column(name = "DEPOSITORWITHDRAWAL")
	private String depositorwithdrawal;

	@Column(name = "CURRENCYCODE")
	private String currencycode;
	
	@Column(name = "ACCTCURRENCYCODE")
	private String acctcurrencycode;

	@Column(name = "NARRATION")
	private String narration;

	@Column(name = "COUNTERPARTYID")
	private String counterpartyid;

	@Column(name = "COUNTERPARTYNAME")
	private String counterpartyname;

	@Column(name = "COUNTERPARTYACCOUNTNO")
	private String counterpartyaccountno;

	@Column(name = "COUNTERBRANCHCODE")
	private String counterbranchcode;

	@Column(name = "COUNTERPARTYADDRESS")
	private String counterpartyaddress;

	@Column(name = "COUNTERCOUNTRYCODE")
	private String countercountrycode;

	public String getTransactionbatchid() {
		return transactionbatchid;
	}

	public void setTransactionbatchid(String transactionbatchid) {
		this.transactionbatchid = transactionbatchid;
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public String getBranchcode() {
		return branchcode;
	}

	public void setBranchcode(String branchcode) {
		this.branchcode = branchcode;
	}

	public String getTransactiontype() {
		return transactiontype;
	}

	public void setTransactiontype(String transactiontype) {
		this.transactiontype = transactiontype;
	}

	public String getChannelid() {
		return channelid;
	}

	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}

	public String getTransactiondate() {
		return transactiondate;
	}

	public void setTransactiondate(String transactiondate) {
		this.transactiondate = transactiondate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDepositorwithdrawal() {
		return depositorwithdrawal;
	}

	public void setDepositorwithdrawal(String depositorwithdrawal) {
		this.depositorwithdrawal = depositorwithdrawal;
	}

	public String getCurrencycode() {
		return currencycode;
	}

	public void setCurrencycode(String currencycode) {
		this.currencycode = currencycode;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getCounterpartyid() {
		return counterpartyid;
	}

	public void setCounterpartyid(String counterpartyid) {
		this.counterpartyid = counterpartyid;
	}

	public String getCounterpartyname() {
		return counterpartyname;
	}

	public void setCounterpartyname(String counterpartyname) {
		this.counterpartyname = counterpartyname;
	}

	public String getCounterbranchcode() {
		return counterbranchcode;
	}

	public void setCounterbranchcode(String counterbranchcode) {
		this.counterbranchcode = counterbranchcode;
	}

	public String getCounterpartyaddress() {
		return counterpartyaddress;
	}

	public void setCounterpartyaddress(String counterpartyaddress) {
		this.counterpartyaddress = counterpartyaddress;
	}

	public String getCountercountrycode() {
		return countercountrycode;
	}

	public void setCountercountrycode(String countercountrycode) {
		this.countercountrycode = countercountrycode;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getAccountno() {
		return accountno;
	}

	public void setAccountno(String accountno) {
		this.accountno = accountno;
	}

	public String getCounterpartyaccountno() {
		return counterpartyaccountno;
	}

	public void setCounterpartyaccountno(String counterpartyaccountno) {
		this.counterpartyaccountno = counterpartyaccountno;
	}

	public String getAcctcurrencycode() {
		return acctcurrencycode;
	}

	public void setAcctcurrencycode(String acctcurrencycode) {
		this.acctcurrencycode = acctcurrencycode;
	}
	
}
