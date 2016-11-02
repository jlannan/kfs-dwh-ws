package edu.uci.oit.kfs.dwh.webservices.translation.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ObjectCode {

	private String chartCode;
	private String objectCode;
	private String objectTypeCode;
	private String objectLevelCode;
	private String ucAccountNumber;
	private String oftIndicator;
	private String reportsToChartCode;
	private String reportsToObjectCode;

	public String getChartCode() {
		return chartCode;
	}

	public void setChartCode(String chartCode) {
		this.chartCode = chartCode;
	}

	public String getObjectCode() {
		return objectCode;
	}

	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}

	public String getObjectTypeCode() {
		return objectTypeCode;
	}

	public void setObjectTypeCode(String objectTypeCode) {
		this.objectTypeCode = objectTypeCode;
	}

	public String getObjectLevelCode() {
		return objectLevelCode;
	}

	public void setObjectLevelCode(String objectLevelCode) {
		this.objectLevelCode = objectLevelCode;
	}

	public String getUcAccountNumber() {
		return ucAccountNumber;
	}

	public void setUcAccountNumber(String ucAccountNumber) {
		this.ucAccountNumber = ucAccountNumber;
	}

	public String getOftIndicator() {
		return oftIndicator;
	}

	public void setOftIndicator(String oftIndicator) {
		this.oftIndicator = oftIndicator;
	}

	public String getReportsToChartCode() {
		return reportsToChartCode;
	}

	public void setReportsToChartCode(String reportsToChartCode) {
		this.reportsToChartCode = reportsToChartCode;
	}

	public String getReportsToObjectCode() {
		return reportsToObjectCode;
	}

	public void setReportsToObjectCode(String reportsToObjectCode) {
		this.reportsToObjectCode = reportsToObjectCode;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
