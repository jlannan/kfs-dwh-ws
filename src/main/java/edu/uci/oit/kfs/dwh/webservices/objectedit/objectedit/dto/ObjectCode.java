package edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ObjectCode {

	String financialObjectCode;
	String financialObjectLevelCode;
	String financialConsolidationObjectCode;

	public String getFinancialObjectCode() {
		return financialObjectCode;
	}

	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}

	public String getFinancialObjectLevelCode() {
		return financialObjectLevelCode;
	}

	public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
		this.financialObjectLevelCode = financialObjectLevelCode;
	}

	public String getFinancialConsolidationObjectCode() {
		return financialConsolidationObjectCode;
	}

	public void setFinancialConsolidationObjectCode(String financialConsolidationObjectCode) {
		this.financialConsolidationObjectCode = financialConsolidationObjectCode;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
