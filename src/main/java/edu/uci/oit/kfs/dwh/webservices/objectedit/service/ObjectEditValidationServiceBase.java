package edu.uci.oit.kfs.dwh.webservices.objectedit.service;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;

import edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess.ObjectEditValidationDao;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.BaseObjectEditRule;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.ObjectCode;
import edu.uci.oit.kfs.dwh.webservices.wsresult.ObjectCodeEditValidationResult;

public abstract class ObjectEditValidationServiceBase {

	protected ObjectEditValidationDao objectEditValidationDao;

	public String findCurrentFiscalYear() {

		String fiscalYear = objectEditValidationDao.findCurrentFiscalYear();
		if (StringUtils.isBlank(fiscalYear)) {
			throw new BadRequestException("current fiscal year not found");
		}

		return fiscalYear;
	}

	protected Long matchConsolidation(BaseObjectEditRule rule, ObjectCode originObjectCode) {

		if (StringUtils.isNotEmpty(rule.getConsolidationObjectCode())) {

			if (StringUtils.equals(rule.getConsolidationObjectCode(), originObjectCode.getFinancialConsolidationObjectCode())) {
				return rule.getRuleId();
			}
		}

		return null;
	}

	protected Long matchLevel(BaseObjectEditRule rule, ObjectCode originObjectCode) {

		if (StringUtils.isNotEmpty(rule.getObjectLevelCode())) {

			if (StringUtils.equals(rule.getObjectLevelCode(), originObjectCode.getFinancialObjectLevelCode())) {
				return rule.getRuleId();
			}
		}

		return null;
	}

	protected Long matchRange(BaseObjectEditRule rule, ObjectCode originObjectCode) {

		if (StringUtils.isNotEmpty(rule.getFromObjectCode()) && StringUtils.isNotEmpty(rule.getToObjectCode())) {
			boolean objectCodeInRange = matchStringRange(rule.getFromObjectCode(), rule.getToObjectCode(), originObjectCode.getFinancialObjectCode());
			if (objectCodeInRange) {
				return rule.getRuleId();
			}
		}

		return null;
	}

	protected boolean matchStringRange(String start, String end, String value) {

		if (StringUtils.isBlank(start) || StringUtils.isBlank(end) || StringUtils.isBlank(value)) {
			return false;
		}

		if (value.compareToIgnoreCase(start) >= 0 && value.compareToIgnoreCase(end) <= 0) {
			return true;
		}

		return false;
	}

	protected ObjectCodeEditValidationResult getFailureObjectEditValidationResult() {

		ObjectCodeEditValidationResult failureObjectEditValidationResult = new ObjectCodeEditValidationResult();
		failureObjectEditValidationResult.setValid(false);
		return failureObjectEditValidationResult;
	}

	protected ObjectCodeEditValidationResult getFailureObjectEditValidationResult(Long matchingDefinitionId) {

		ObjectCodeEditValidationResult failureObjectEditValidationResult = getFailureObjectEditValidationResult();
		failureObjectEditValidationResult.setMatchingDefinitionId(matchingDefinitionId);
		return failureObjectEditValidationResult;
	}

	protected ObjectCodeEditValidationResult getSuccessObjectEditValidationResult() {

		ObjectCodeEditValidationResult successObjectEditValidationResult = new ObjectCodeEditValidationResult();
		successObjectEditValidationResult.setValid(true);
		return successObjectEditValidationResult;
	}

	protected ObjectCodeEditValidationResult getSuccessObjectEditValidationResult(Long matchingDefinitionId) {

		ObjectCodeEditValidationResult successObjectEditValidationResult = getSuccessObjectEditValidationResult();
		successObjectEditValidationResult.setMatchingDefinitionId(matchingDefinitionId);
		return successObjectEditValidationResult;
	}

	public void setObjectEditValidationDao(ObjectEditValidationDao objectEditValidationDao) {
		this.objectEditValidationDao = objectEditValidationDao;
	}
}
