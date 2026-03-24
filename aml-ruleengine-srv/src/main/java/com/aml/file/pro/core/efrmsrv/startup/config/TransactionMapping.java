package com.aml.file.pro.core.efrmsrv.startup.config;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMapping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("Source")
    private String source;

    @SerializedName("SourceFileName")
    private String sourceFileName;

    @SerializedName("ShortName")
    private String shortName;

    @SerializedName("SourcefileType")
    private String sourceFileType;

    @SerializedName("SLocation")
    private String sourceLocation;

    @SerializedName("DestFileName")
    private String destFileName;

    @SerializedName("DestFileType")
    private String destFileType;

    @SerializedName("DLocation")
    private String destLocation;

    @SerializedName("targetTable")
    private String targetTable;

    @SerializedName("keyColumns")
    private List<String> keyColumns;

    @SerializedName("columns")
    private List<ColumnMapping> columns;

    @SerializedName("mode")
    private String mode;

    @SerializedName("onError")
    private String onError;

    @SerializedName("Source")
	public String getSource() {
		return source;
	}

    @SerializedName("Source")
	public void setSource(String source) {
		this.source = source;
	}

    @SerializedName("SourceFileName")
	public String getSourceFileName() {
		return sourceFileName;
	}

    @SerializedName("SourceFileName")
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

    @SerializedName("ShortName")
	public String getShortName() {
		return shortName;
	}

    @SerializedName("ShortName")
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

    @SerializedName("SourcefileType")
	public String getSourceFileType() {
		return sourceFileType;
	}

    @SerializedName("SourcefileType")
	public void setSourceFileType(String sourceFileType) {
		this.sourceFileType = sourceFileType;
	}

    @SerializedName("SLocation")
	public String getSourceLocation() {
		return sourceLocation;
	}

    @SerializedName("SLocation")
	public void setSourceLocation(String sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

    @SerializedName("DestFileName")
	public String getDestFileName() {
		return destFileName;
	}

    @SerializedName("DestFileName")
	public void setDestFileName(String destFileName) {
		this.destFileName = destFileName;
	}

    @SerializedName("DestFileType")
	public String getDestFileType() {
		return destFileType;
	}

    @SerializedName("DestFileType")
	public void setDestFileType(String destFileType) {
		this.destFileType = destFileType;
	}

    @SerializedName("DLocation")
	public String getDestLocation() {
		return destLocation;
	}

    @SerializedName("DLocation")
	public void setDestLocation(String destLocation) {
		this.destLocation = destLocation;
	}

    @SerializedName("targetTable")
	public String getTargetTable() {
		return targetTable;
	}

    @SerializedName("targetTable")
	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}

    @SerializedName("keyColumns")
	public List<String> getKeyColumns() {
		return keyColumns;
	}

    @SerializedName("keyColumns")
	public void setKeyColumns(List<String> keyColumns) {
		this.keyColumns = keyColumns;
	}

    @SerializedName("columns")
	public List<ColumnMapping> getColumns() {
		return columns;
	}

    @SerializedName("columns")
	public void setColumns(List<ColumnMapping> columns) {
		this.columns = columns;
	}

    @SerializedName("mode")
	public String getMode() {
		return mode;
	}

    @SerializedName("mode")
	public void setMode(String mode) {
		this.mode = mode;
	}

    @SerializedName("onError")
	public String getOnError() {
		return onError;
	}

    @SerializedName("onError")
	public void setOnError(String onError) {
		this.onError = onError;
	}
    
}
