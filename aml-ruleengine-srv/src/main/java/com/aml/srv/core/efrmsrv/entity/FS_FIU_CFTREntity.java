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
@Table(name = "FS_FIU_CFTR")
public class FS_FIU_CFTREntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Version
	@Column(name ="VERSION")
	private Long version;

	@Column(name = "TRANSACTIONID")
	private String transactionId;
	
	@Column(name = "REPORT_TYPE")
	private String reportType;
	
	@Column(name = "ENTITY_ID")
	private String entityId;
	
	@Column(name = "BRANCH_CODE")
	private String branchCode;

	@Column(name = "DETECTION_DATE")
	private String detectionDate;
	
	@Column(name = "DENOMINATION")
	private String denomination;
	
	@Column(name = "QUANTITY")
	private String quantity;
	
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	
	@Column(name = "PAN")
	private String pan;
	
	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "PARENT_ID")
	private String parnetId;

	@Column(name = "CUSTOMER_ID")
	private String customerId;

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

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getDetectionDate() {
		return detectionDate;
	}

	public void setDetectionDate(String detectionDate) {
		this.detectionDate = detectionDate;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
