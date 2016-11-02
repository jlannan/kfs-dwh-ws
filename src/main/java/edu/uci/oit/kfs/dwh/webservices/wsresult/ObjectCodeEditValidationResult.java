package edu.uci.oit.kfs.dwh.webservices.wsresult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ObjectCodeEditValidationResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ObjectCodeEditValidationResult {

	private boolean valid;
	private Long matchingDefinitionId;

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Long getMatchingDefinitionId() {
		return matchingDefinitionId;
	}

	public void setMatchingDefinitionId(Long matchingDefinitionId) {
		this.matchingDefinitionId = matchingDefinitionId;
	}

	public String toString() {
		return "valid: " + isValid() + "\n" +
				"matchingDefinitionId: " + getMatchingDefinitionId() + "\n";
	}
}
