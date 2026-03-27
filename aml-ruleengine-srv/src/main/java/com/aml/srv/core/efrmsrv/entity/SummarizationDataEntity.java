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
@Table(name = "FS_SUMMARIZED_DATA")//, fs_summarized_dataschema = "amlschema"
public class SummarizationDataEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Version
	@Column(name = "VERSION")
	private Integer version;
	
	@Column(name = "CUSTOMERID")
	private String customerid;
	
	@Column(name = "ACCOUNTNO")
	private String accountno;
	
	@Column(name = "PERIODTYPE")
	private String periodtype;
	
	@Column(name = "PERIODVALUE")
	private String periodvalue;
	
	@Column(name = "METRIC_NAME")
	private String metric_name;
	
	@Column(name = "METRIC_VALUE")
	private Double metricvalue;
	
	@Column(name = "CREATED_DATE")
	private Timestamp createddate;

	@Column(name = "UPDATED_DATE")
	private Timestamp updateddate;
	
	//depositorwithdrawal
	@Column(name = "DEPOSITORWITHDRAWAL")
	private String depositorwithdrawal;

	
	@Column(name = "TXNDATE")
	private Timestamp txndate;
	
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

	public String getPeriodtype() {
		return periodtype;
	}

	public void setPeriodtype(String periodtype) {
		this.periodtype = periodtype;
	}

	public String getPeriodvalue() {
		return periodvalue;
	}

	public void setPeriodvalue(String periodvalue) {
		this.periodvalue = periodvalue;
	}



	public String getMetric_name() {
		return metric_name;
	}

	public void setMetric_name(String metric_name) {
		this.metric_name = metric_name;
	}

	public Double getMetricvalue() {
		return metricvalue;
	}

	public void setMetricvalue(Double metricvalue) {
		this.metricvalue = metricvalue;
	}

	public Timestamp getCreateddate() {
		return createddate;
	}

	public void setCreateddate(Timestamp createddate) {
		this.createddate = createddate;
	}

	public Timestamp getUpdateddate() {
		return updateddate;
	}

	public void setUpdateddate(Timestamp updateddate) {
		this.updateddate = updateddate;
	}

	public String getDepositorwithdrawal() {
		return depositorwithdrawal;
	}

	public void setDepositorwithdrawal(String depositorwithdrawal) {
		this.depositorwithdrawal = depositorwithdrawal;
	}

	public Timestamp getTxndate() {
		return txndate;
	}

	public void setTxndate(Timestamp txndate) {
		this.txndate = txndate;
	}
	
}
