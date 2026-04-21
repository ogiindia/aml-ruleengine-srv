package com.aml.srv.core.efrmsrv.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "FS_FIU_NTR")
public class FS_FIU_NTREntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Version
	@Column(name ="VERSION")
	private Integer version;

	@Column(name = "TRANSACTIONID")
	private String transactionId;

	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Column(name = "ENTITY_ID")
	private String entityId;

	@Column(name = "NGO_NAME")
	private String ngoName;

	@Column(name = "FCRA_REGISTER_NUMBER")
	private String fcraRegisterNumber;

	@Column(name = "DONOR_NAME")
	private String donorName;

	@Column(name = "DONOR_COUNTRY")
	private String donorCountry;

	@Column(name = "TRANSACTION_DATE")
	private String transactionDate;

	@Column(name = "TRANSACTION_AMOUNT")
	private String trasnactionAmount;

	@Column(name = "CURRENCY")
	private String currency;

	@Column(name = "PURPOSE_OF_FUNDS")
	private String purposeOfFund;

	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "PARENT_ID")
	private String parnetId;

	@Column(name = "CUSTOMER_ID")
	private String customerId;
	
	@Column(name = "RULEID")
	private String ruleId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getNgoName() {
		return ngoName;
	}

	public void setNgoName(String ngoName) {
		this.ngoName = ngoName;
	}

	public String getFcraRegisterNumber() {
		return fcraRegisterNumber;
	}

	public void setFcraRegisterNumber(String fcraRegisterNumber) {
		this.fcraRegisterNumber = fcraRegisterNumber;
	}

	public String getDonorName() {
		return donorName;
	}

	public void setDonorName(String donorName) {
		this.donorName = donorName;
	}

	public String getDonorCountry() {
		return donorCountry;
	}

	public void setDonorCountry(String donorCountry) {
		this.donorCountry = donorCountry;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTrasnactionAmount() {
		return trasnactionAmount;
	}

	public void setTrasnactionAmount(String trasnactionAmount) {
		this.trasnactionAmount = trasnactionAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPurposeOfFund() {
		return purposeOfFund;
	}

	public void setPurposeOfFund(String purposeOfFund) {
		this.purposeOfFund = purposeOfFund;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getParnetId() {
		return parnetId;
	}

	public void setParnetId(String parnetId) {
		this.parnetId = parnetId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
}
