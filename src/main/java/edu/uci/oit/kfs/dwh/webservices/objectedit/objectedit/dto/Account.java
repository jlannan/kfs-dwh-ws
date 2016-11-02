package edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Account {

	String chartOfAccountsCode;
	String accountNumber;
	String subFundGroupCode;

	public String getChartOfAccountsCode() {
		return chartOfAccountsCode;
	}

	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getSubFundGroupCode() {
		return subFundGroupCode;
	}

	public void setSubFundGroupCode(String subFundGroupCode) {
		this.subFundGroupCode = subFundGroupCode;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
