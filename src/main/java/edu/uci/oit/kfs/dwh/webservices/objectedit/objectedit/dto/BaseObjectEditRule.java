package edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BaseObjectEditRule {

	Long ruleId;
	String consolidationObjectCode;
	String objectLevelCode;
	String fromObjectCode;
	String toObjectCode;

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getConsolidationObjectCode() {
		return consolidationObjectCode;
	}

	public void setConsolidationObjectCode(String consolidationObjectCode) {
		this.consolidationObjectCode = consolidationObjectCode;
	}

	public String getObjectLevelCode() {
		return objectLevelCode;
	}

	public void setObjectLevelCode(String objectLevelCode) {
		this.objectLevelCode = objectLevelCode;
	}

	public String getFromObjectCode() {
		return fromObjectCode;
	}

	public void setFromObjectCode(String fromObjectCode) {
		this.fromObjectCode = fromObjectCode;
	}

	public String getToObjectCode() {
		return toObjectCode;
	}

	public void setToObjectCode(String toObjectCode) {
		this.toObjectCode = toObjectCode;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
