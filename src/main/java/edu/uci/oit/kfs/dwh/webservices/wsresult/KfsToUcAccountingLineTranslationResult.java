package edu.uci.oit.kfs.dwh.webservices.wsresult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "KfsToUcAccountingLineTranslationResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class KfsToUcAccountingLineTranslationResult {

	private boolean valid;
	private String message;

	private String ucLocationCode;
	private String ucAccountNumber;
	private String ucFundNumber;
	private String ucObjectCode;
	private String ucSubCode = "";

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
