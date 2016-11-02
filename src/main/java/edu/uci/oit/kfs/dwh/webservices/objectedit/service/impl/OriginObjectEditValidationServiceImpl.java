package edu.uci.oit.kfs.dwh.webservices.objectedit.service.impl;

import java.util.List;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;

import edu.uci.oit.kfs.dwh.webservices.objectedit.ObjectEditConstants.RuleType;
import edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess.OriginObjectEditValidationDao;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.OOERule;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.ObjectCode;
import edu.uci.oit.kfs.dwh.webservices.objectedit.service.ObjectEditValidationServiceBase;
import edu.uci.oit.kfs.dwh.webservices.objectedit.service.OriginObjectEditValidationService;
import edu.uci.oit.kfs.dwh.webservices.wsresult.ObjectCodeEditValidationResult;

public class OriginObjectEditValidationServiceImpl extends ObjectEditValidationServiceBase implements OriginObjectEditValidationService {

	OriginObjectEditValidationDao originObjectEditValidationDao;

	/**
	 * The "Deny" and "Allow" Rules are as follows
	 * <ul>
	 * <li>If the collection only has the deny rule(s), the object codes not listed in the collection are considered to be allowed.</li>
	 * <li>If the collection only has the allow rule(s), the object codes not listed in the collection are considered to be denied.</li>
	 * <li>If the collection has both allow and deny rules, the deny rules will be applied first then the allow rules are applied.
	 * (Note:deny always wins)</li>
	 * </ul>
	 * 
	 */
	@Override
	@Cacheable("ooeValidation")
	public ObjectCodeEditValidationResult getObjectCodeEditValidationResult(String kfsFiscalYear, String kfsOriginationCode, String kfsChartCode, String kfsObjectCode) {

		if (StringUtils.isBlank(kfsOriginationCode) || StringUtils.isBlank(kfsChartCode) || StringUtils.isBlank(kfsObjectCode)) {
			throw new BadRequestException("missing required parameter(s)");
		}

		if (StringUtils.isBlank(kfsFiscalYear)) {
			kfsFiscalYear = findCurrentFiscalYear();
		}

		kfsFiscalYear = kfsFiscalYear.trim();
		kfsOriginationCode = kfsOriginationCode.trim().toUpperCase();
		kfsChartCode = kfsChartCode.trim().toUpperCase();
		kfsObjectCode = kfsObjectCode.trim().toUpperCase();

		// check that object code is active for current fiscal year
		ObjectCode originObjectCode = originObjectEditValidationDao.findObjectCode(kfsFiscalYear, kfsChartCode, kfsObjectCode);
		if (originObjectCode == null) {
			return getFailureObjectEditValidationResult();
		}

		// get all active allow rules by origin code and chart
		List<OOERule> allowRuleList = originObjectEditValidationDao.findOOERules(kfsOriginationCode, kfsChartCode, RuleType.ALLOW);
		List<OOERule> denyRuleList = originObjectEditValidationDao.findOOERules(kfsOriginationCode, kfsChartCode, RuleType.DENY);

		boolean emptyAllowRuleList = CollectionUtils.isEmpty(allowRuleList);
		boolean emptyDenyRuleList = CollectionUtils.isEmpty(denyRuleList);

		if (emptyAllowRuleList && emptyDenyRuleList) {
			return getFailureObjectEditValidationResult();
		}

		OOERule allowRuleMatched = findMatchingRule(allowRuleList, originObjectCode);

		// allow rules only
		if (emptyDenyRuleList) {
			if (allowRuleMatched == null) {
				return getFailureObjectEditValidationResult();
			}
			else {
				return getSuccessObjectEditValidationResult(allowRuleMatched.getDefinitionId());
			}
		}

		OOERule denyRuleMatched = findMatchingRule(denyRuleList, originObjectCode);

		// deny rules only
		if (emptyAllowRuleList) {
			if (denyRuleMatched == null) {
				return getSuccessObjectEditValidationResult();
			}
			else {
				return getFailureObjectEditValidationResult(denyRuleMatched.getDefinitionId());
			}
		}

		// allow and deny rules exist
		if (!emptyAllowRuleList && !emptyDenyRuleList) {
			if (denyRuleMatched != null) {
				return getFailureObjectEditValidationResult(denyRuleMatched.getDefinitionId());
			}
			else {
				if (allowRuleMatched != null) {
					return getSuccessObjectEditValidationResult(allowRuleMatched.getDefinitionId());
				}
				else {
					return getFailureObjectEditValidationResult();
				}
			}

		}

		return getSuccessObjectEditValidationResult();
	}

	protected OOERule findMatchingRule(List<OOERule> ruleList, ObjectCode objectCode) {

		if (ruleList == null || ruleList.isEmpty()) {
			return null;
		}

		for (OOERule rule : ruleList) {

			// match consolidation
			Long ruleMatched = matchConsolidation(rule, objectCode);
			if (ruleMatched != null) {
				return rule;
			}

			// match level
			ruleMatched = matchLevel(rule, objectCode);
			if (ruleMatched != null) {
				return rule;
			}

			// match range
			ruleMatched = matchRange(rule, objectCode);
			if (ruleMatched != null) {
				return rule;
			}
		}

		return null;
	}

	public void setOriginObjectEditValidationDao(OriginObjectEditValidationDao originObjectEditValidationDao) {
		this.originObjectEditValidationDao = originObjectEditValidationDao;
	}
}
