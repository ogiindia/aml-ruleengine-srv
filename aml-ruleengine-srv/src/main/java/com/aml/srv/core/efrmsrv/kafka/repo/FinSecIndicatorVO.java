package com.aml.srv.core.efrmsrv.kafka.repo;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class FinSecIndicatorVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String uuid;
	private boolean fileCompletedStatus;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isFileCompletedStatus() {
		return fileCompletedStatus;
	}

	public void setFileCompletedStatus(boolean fileCompletedStatus) {
		this.fileCompletedStatus = fileCompletedStatus;
	}

}