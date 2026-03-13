package com.aml.srv.core.efrmsrv.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "FS_RISK_VALUES")
public class RiskValuesEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	@Column(name = "RISK_PARAM_ID")
	private Integer riskParamId;

	@Column(name = "PARAM_VALUES")
	private String paramValues;

	@Column(name = "PARAM_WEIGHTAGE", columnDefinition = "DOUBLE PRECISION")
	private BigDecimal paramWeightage;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "STATUS")
	private Integer status;

	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;

	@Column(name = "UPDATED_DATE")
	private Timestamp updatedDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getParamValues() {
		return paramValues;
	}

	public void setParamValues(String paramValues) {
		this.paramValues = paramValues;
	}

	public BigDecimal getParamWeightage() {
		return paramWeightage;
	}

	public void setParamWeightage(BigDecimal paramWeightage) {
		this.paramWeightage = paramWeightage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getRiskParamId() {
		return riskParamId;
	}

	public void setRiskParamId(Integer riskParamId) {
		this.riskParamId = riskParamId;
	}

}
