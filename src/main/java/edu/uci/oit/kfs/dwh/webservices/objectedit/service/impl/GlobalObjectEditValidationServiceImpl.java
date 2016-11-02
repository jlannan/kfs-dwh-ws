package edu.uci.oit.kfs.dwh.webservices.objectedit.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;

import edu.uci.oit.kfs.dwh.webservices.objectedit.ObjectEditConstants.RuleType;
import edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess.GlobalObjectEditValidationDao;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.Account;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.AccountRange;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.GOERule;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.ObjectCode;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.ObjectCodeAccountExclusion;
import edu.uci.oit.kfs.dwh.webservices.objectedit.service.GlobalObjectEditValidationService;
import edu.uci.oit.kfs.dwh.webservices.objectedit.service.ObjectEditValidationServiceBase;
import edu.uci.oit.kfs.dwh.webservices.wsresult.ObjectCodeEditValidationResult;

public class GlobalObjectEditValidationServiceImpl extends ObjectEditValidationServiceBase implements GlobalObjectEditValidationService {

	GlobalObjectEditValidationDao globalObjectEditValidationDao;

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
	@Cacheable("goeValidation")
	public ObjectCodeEditValidationResult getGlobalObjectCodeEditValidationResult(
			String kfsFiscalYear, String kfsChartCode, String kfsAccountNumber, String kfsObjectCode) {

		if (StringUtils.isBlank(kfsChartCode) || StringUtils.isBlank(kfsAccountNumber) || StringUtils.isBlank(kfsObjectCode)) {
			throw new BadRequestException("missing required parameter(s)");
		}

		if (StringUtils.isBlank(kfsFiscalYear)) {
			kfsFiscalYear = findCurrentFiscalYear();
		}

		kfsFiscalYear = kfsFiscalYear.trim();
		kfsChartCode = kfsChartCode.trim().toUpperCase();
		kfsAccountNumber = kfsAccountNumber.trim().toUpperCase();
		kfsObjectCode = kfsObjectCode.trim().toUpperCase();

		// check that account is found and not closed
		Account account = globalObjectEditValidationDao.findAccount(kfsChartCode, kfsAccountNumber);
		if (account == null) {
			return getFailureObjectEditValidationResult();
		}

		// check if sub fund group code exists for GOE definition
		if (account.getSubFundGroupCode() == null) {
			return getFailureObjectEditValidationResult();
		}

		// check that object code is active for current fiscal year
		ObjectCode objectCode = globalObjectEditValidationDao.findObjectCode(findCurrentFiscalYear(), kfsChartCode, kfsObjectCode);
		if (objectCode == null) {
			return getFailureObjectEditValidationResult();
		}

		// find matching GOE definitions

		// check if account number is in account number exclusion ranges
		List<AccountRange> accountRangeList = globalObjectEditValidationDao.findAccountRanges(account.getSubFundGroupCode(), kfsChartCode);

		List<AccountRange> matchingAccountRangeList = new ArrayList<AccountRange>();

		for (AccountRange accountRange : accountRangeList) {
			if (isAccountInRange(kfsAccountNumber, accountRange)) {
				matchingAccountRangeList.add(accountRange);
			}
		}

		if (CollectionUtils.isEmpty(matchingAccountRangeList)) {
			return getFailureObjectEditValidationResult();
		}

		for (AccountRange accountRange : matchingAccountRangeList) {

			Long goeDefinitionId = accountRange.getGoeDefinitionId();

			// get all active rules by subFundGroupCode and chartCode
			List<GOERule> allowRuleList = globalObjectEditValidationDao.findGOERules(goeDefinitionId, kfsChartCode, RuleType.ALLOW);
			List<GOERule> denyRuleList = globalObjectEditValidationDao.findGOERules(goeDefinitionId, kfsChartCode, RuleType.DENY);

			boolean emptyAllowRuleList = CollectionUtils.isEmpty(allowRuleList);
			boolean emptyDenyRuleList = CollectionUtils.isEmpty(denyRuleList);

			if (emptyAllowRuleList && emptyDenyRuleList) {
				return getFailureObjectEditValidationResult(goeDefinitionId);
			}

			Long allowRuleMatched = findMatchingRule(allowRuleList, kfsChartCode, kfsAccountNumber, objectCode);

			// allow rules only
			if (emptyDenyRuleList) {
				if (allowRuleMatched == null) {
					return getFailureObjectEditValidationResult(goeDefinitionId);
				}
			}

			Long denyRuleMatched = findMatchingRule(denyRuleList, kfsChartCode, kfsAccountNumber, objectCode);

			// deny rules only
			if (emptyAllowRuleList) {
				if (denyRuleMatched != null) {
					return getFailureObjectEditValidationResult(goeDefinitionId);
				}
			}

			// allow and deny rules exist
			if (!emptyAllowRuleList && !emptyDenyRuleList) {
				if (denyRuleMatched != null) {
					return getFailureObjectEditValidationResult(goeDefinitionId);
				}
				else {
					if (allowRuleMatched == null) {
						return getFailureObjectEditValidationResult(goeDefinitionId);
					}
				}
			}
		}

		return getSuccessObjectEditValidationResult(accountRangeList.get(0).getGoeDefinitionId());
	}

	protected boolean isAccountInRange(String kfsAccountNumber, AccountRange accountRange) {

		if (accountRange == null) {
			return false;
		}

		String start = accountRange.getFromAccountNumber();
		String end = accountRange.getToAccountNumber();

		if (StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {

			String value = kfsAccountNumber;

			if (matchStringRange(start, end, value)) {
				return true;
			}
		}

		return false;
	}

	protected Long findMatchingRule(List<GOERule> ruleList, String kfsChartCode, String kfsAccountNumber, ObjectCode objectCode) {

		if (ruleList == null || ruleList.isEmpty()) {
			return null;
		}

		for (GOERule rule : ruleList) {

			if (hasMatchingObjectCodeAccountNumberExclusion(rule, kfsChartCode, kfsAccountNumber)) {
				continue;
			}

			// match consolidation
			Long ruleMatched = matchConsolidation(rule, objectCode);
			if (ruleMatched != null) {
				return ruleMatched;
			}

			// match level
			ruleMatched = matchLevel(rule, objectCode);
			if (ruleMatched != null) {
				return ruleMatched;
			}

			// match range
			ruleMatched = matchRange(rule, objectCode);
			if (ruleMatched != null) {
				return ruleMatched;
			}
		}

		return null;
	}

	protected boolean hasMatchingObjectCodeAccountNumberExclusion(GOERule rule, String kfsChartCode, String kfsAccountNumber) {

		List<ObjectCodeAccountExclusion> objectCodeAccountExclusionList = globalObjectEditValidationDao.findObjectCodeAccountNumberExclusions(rule.getRuleId(), kfsChartCode);

		for (ObjectCodeAccountExclusion objectCodeAccountExclusion : objectCodeAccountExclusionList) {

			String start = objectCodeAccountExclusion.getFromAccountNumber();
			String end = objectCodeAccountExclusion.getToAccountNumber();

			if (StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {

				String value = kfsAccountNumber;

				if (matchStringRange(start, end, value)) {
					return true;
				}
			}
		}

		return false;

	}

	public void setGlobalObjectEditValidationDao(GlobalObjectEditValidationDao globalObjectEditValidationDao) {
		this.globalObjectEditValidationDao = globalObjectEditValidationDao;
	}
}
