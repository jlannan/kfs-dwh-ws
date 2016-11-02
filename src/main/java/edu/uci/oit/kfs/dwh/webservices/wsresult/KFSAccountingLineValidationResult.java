package edu.uci.oit.kfs.dwh.webservices.wsresult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AccountingLineValidationResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class KFSAccountingLineValidationResult {

	private boolean valid;
	private String message;
	private Long matchingOOEDefinitionId;
	private Long matchingGOEDefinitionId;

	public KFSAccountingLineValidationResult() {
		super();
	}

	public KFSAccountingLineValidationResult(boolean valid, String message, long matchingOOEDefinitionId, long matchingGOEDefinitionId) {
		super();
		this.valid = valid;
		this.message = message;
		this.matchingOOEDefinitionId = matchingOOEDefinitionId;
		this.matchingGOEDefinitionId = matchingGOEDefinitionId;
	}

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

	public Long getMatchingOOEDefinitionId() {
		return matchingOOEDefinitionId;
	}

	public void setMatchingOOEDefinitionId(Long matchingOOEDefinitionId) {
		this.matchingOOEDefinitionId = matchingOOEDefinitionId;
	}

	public Long getMatchingGOEDefinitionId() {
		return matchingGOEDefinitionId;
	}

	public void setMatchingGOEDefinitionId(Long matchingGOEDefinitionId) {
		this.matchingGOEDefinitionId = matchingGOEDefinitionId;
	}

	public String toString() {
		return "valid: " + isValid() + "\n" + "message: " + getMessage() + "\n" + "matchingOOEDefinitionId: " + getMatchingOOEDefinitionId() + "\n"
				+ "matchingGOEDefinitionId: " + getMatchingGOEDefinitionId() + "\n";
	}

}
