package edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AccountRange {

	String fromAccountNumber;
	String toAccountNumber;
	Long goeDefinitionId;

	public String getFromAccountNumber() {
		return fromAccountNumber;
	}

	public void setFromAccountNumber(String fromAccountNumber) {
		this.fromAccountNumber = fromAccountNumber;
	}

	public String getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(String toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
	}

	public Long getGoeDefinitionId() {
		return goeDefinitionId;
	}

	public void setGoeDefinitionId(Long goeDefinitionId) {
		this.goeDefinitionId = goeDefinitionId;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
