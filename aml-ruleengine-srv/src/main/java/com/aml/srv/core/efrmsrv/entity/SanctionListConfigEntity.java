package com.aml.srv.core.efrmsrv.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FS_SANCTION_CONF")
public class SanctionListConfigEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "SANCTION_NAME")
	private String sanctionName;

	@Column(name = "SANCTION_CODE")
	private String sanctionCode;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;
	
	@Column(name = "CHECKSUMVAL")
	private String checksumval;
	
	@Column(name = "LSTSIZE")
	private Integer lstsize;
	
	@Column(name = "LIST_TYPE")
	private Integer lsttype;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSanctionName() {
		return sanctionName;
	}

	public void setSanctionName(String sanctionName) {
		this.sanctionName = sanctionName;
	}

	public String getSanctionCode() {
		return sanctionCode;
	}

	public void setSanctionCode(String sanctionCode) {
		this.sanctionCode = sanctionCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getChecksumval() {
		return checksumval;
	}

	public void setChecksumval(String checksumval) {
		this.checksumval = checksumval;
	}

	public Integer getLstsize() {
		return lstsize;
	}

	public void setLstsize(Integer lstsize) {
		this.lstsize = lstsize;
	}

	public Integer getLsttype() {
		return lsttype;
	}

	public void setLsttype(Integer lsttype) {
		this.lsttype = lsttype;
	}
}
