package edu.uci.oit.kfs.dwh.webservices.translation.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Account {

	String subFundGroupCode;
	String ucLocationCode;
	String ucAccountNumber;
	String ucFundNumber;
	String ucObjectCode;
	String ucSubCode;

	public String getSubFundGroupCode() {
		return subFundGroupCode;
	}

	public void setSubFundGroupCode(String subFundGroupCode) {
		this.subFundGroupCode = subFundGroupCode;
	}

	public String getUcLocationCode() {
		return ucLocationCode;
	}

	public void setUcLocationCode(String ucLocationCode) {
		this.ucLocationCode = ucLocationCode;
	}

	public String getUcAccountNumber() {
		return ucAccountNumber;
	}

	public void setUcAccountNumber(String ucAccountNumber) {
		this.ucAccountNumber = ucAccountNumber;
	}

	public String getUcFundNumber() {
		return ucFundNumber;
	}

	public void setUcFundNumber(String ucFundNumber) {
		this.ucFundNumber = ucFundNumber;
	}

	public String getUcObjectCode() {
		return ucObjectCode;
	}

	public void setUcObjectCode(String ucObjectCode) {
		this.ucObjectCode = ucObjectCode;
	}

	public String getUcSubCode() {
		return ucSubCode;
	}

	public void setUcSubCode(String ucSubCode) {
		this.ucSubCode = ucSubCode;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
