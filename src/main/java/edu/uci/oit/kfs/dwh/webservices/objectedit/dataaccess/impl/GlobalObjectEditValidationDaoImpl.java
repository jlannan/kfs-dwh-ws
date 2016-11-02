package edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import edu.uci.oit.kfs.dwh.webservices.objectedit.ObjectEditConstants.RuleType;
import edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess.GlobalObjectEditValidationDao;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.Account;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.AccountRange;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.GOERule;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.ObjectCodeAccountExclusion;

public class GlobalObjectEditValidationDaoImpl extends ObjectEditValidationDaoImpl implements GlobalObjectEditValidationDao {

	private Logger LOG = Logger.getLogger(GlobalObjectEditValidationDaoImpl.class);

	@Override
	@Cacheable("objectCodeEditAccount")
	public Account findAccount(String kfsChartCode, String kfsAccountNumber) {

		if (StringUtils.isEmpty(kfsChartCode) || StringUtils.isEmpty(kfsAccountNumber)) {
			return null;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(kfsChartCode);
		args.add(kfsAccountNumber);

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT FIN_COA_CD, ACCOUNT_NBR, SUB_FUND_GRP_CD");
			sql.appendln("FROM CA_ACCOUNT_T");
			sql.appendln("WHERE FIN_COA_CD = ? AND ACCOUNT_NBR = ?");
			sql.appendln("AND ACCT_CLOSED_IND <> 'Y'");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			if (resultSet.iterator().hasNext()) {

				Map<String, Object> rowMap = (Map<String, Object>) resultSet.iterator().next();

				Account account = new Account();
				account.setChartOfAccountsCode((String) rowMap.get("FIN_COA_CD"));
				account.setAccountNumber((String) rowMap.get("ACCOUNT_NBR"));
				account.setSubFundGroupCode((String) rowMap.get("SUB_FUND_GRP_CD"));
				return account;
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return null;
	}

	@Override
	@Cacheable("objectCodeEditAccountRanges")
	public List<AccountRange> findAccountRanges(String kfsSubFundGroupCode, String kfsChartCode) {

		List<AccountRange> accountRangeList = new ArrayList<AccountRange>();

		if (StringUtils.isEmpty(kfsSubFundGroupCode) || StringUtils.isEmpty(kfsSubFundGroupCode) || StringUtils.isEmpty(kfsChartCode)) {
			return accountRangeList;
		}

		ArrayList<Object> args = new ArrayList<Object>();
		args.add(kfsSubFundGroupCode);
		args.add(kfsChartCode);

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT GOE_DEFINITION_ID, FM_ACCOUNT_NBR, TO_ACCOUNT_NBR");
			sql.appendln("FROM GOE_ACCT_RNG_T");
			sql.appendln("WHERE GOE_DEFINITION_ID IN (");
			sql.appendln("	SELECT GOE_DEFINITION_ID");
			sql.appendln("	FROM GOE_DEFINITION_T");
			sql.appendln("	WHERE SUBFUND_GRP_CD = ?");
			sql.appendln("	AND ACTV_CD = 'Y'");
			sql.appendln(")");
			sql.appendln("AND RANGE_COA_CD = ? AND ACTV_CD = 'Y'");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			for (Iterator<Map<String, Object>> iterator = resultSet.iterator(); iterator.hasNext();) {
				Map<String, Object> rowMap = (Map<String, Object>) iterator.next();
				AccountRange accountRange = new AccountRange();
				accountRange.setGoeDefinitionId((Long) rowMap.get("GOE_DEFINITION_ID"));
				accountRange.setFromAccountNumber((String) rowMap.get("FM_ACCOUNT_NBR"));
				accountRange.setToAccountNumber((String) rowMap.get("TO_ACCOUNT_NBR"));
				accountRangeList.add(accountRange);
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return accountRangeList;
	}

	@Override
	@Cacheable("objectCodeEditObjectCodeAccountNumberExclusions")
	public List<ObjectCodeAccountExclusion> findObjectCodeAccountNumberExclusions(Long ruleId, String kfsChartOfAccountsCode) {

		List<ObjectCodeAccountExclusion> objectCodeAccountExclusionList = new ArrayList<ObjectCodeAccountExclusion>();

		if (ruleId == null || StringUtils.isBlank(kfsChartOfAccountsCode)) {
			return objectCodeAccountExclusionList;
		}

		ArrayList<Object> args = new ArrayList<Object>();
		args.add(ruleId);
		args.add(kfsChartOfAccountsCode);

		try {

			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT FM_ACCOUNT_NBR, TO_ACCOUNT_NBR");
			sql.appendln("FROM GOE_ACCT_EXCL_T");
			sql.appendln("WHERE GOE_OBJ_CD_RULE_ID = ?");
			sql.appendln("AND EXC_COA_CD = ?");
			sql.appendln("AND ACTV_CD = 'Y'");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			for (Iterator<Map<String, Object>> iterator = resultSet.iterator(); iterator.hasNext();) {
				Map<String, Object> rowMap = (Map<String, Object>) iterator.next();
				ObjectCodeAccountExclusion objectCodeAccountExclusion = new ObjectCodeAccountExclusion();
				objectCodeAccountExclusion.setFromAccountNumber((String) rowMap.get("FM_ACCOUNT_NBR"));
				objectCodeAccountExclusion.setToAccountNumber((String) rowMap.get("TO_ACCOUNT_NBR"));
				objectCodeAccountExclusionList.add(objectCodeAccountExclusion);
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return objectCodeAccountExclusionList;
	}

	@Override
	@Cacheable("objectCodeEditGOERules")
	public List<GOERule> findGOERules(Long goeDefinitionId, String kfsChartOfAccountsCode, RuleType ruleType) {

		List<GOERule> ruleList = new ArrayList<GOERule>();

		if (goeDefinitionId == null || StringUtils.isEmpty(kfsChartOfAccountsCode) || ruleType == null) {
			return ruleList;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(String.valueOf(goeDefinitionId));
		args.add(kfsChartOfAccountsCode);
		args.add(RuleType.ALLOW.equals(ruleType) ? "A" : "D");

		try {

			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT GOER.GOE_OBJ_CD_RULE_ID, GOER.FIN_CONS_OBJ_CD, GOER.FIN_OBJ_LEVEL_CD, GOER.FM_OBJ_CD, GOER.TO_OBJ_CD");
			sql.appendln("FROM GOE_DEFINITION_T GOED");
			sql.appendln("JOIN GOE_OBJ_CD_RULES_T GOER ON GOED.GOE_DEFINITION_ID = GOER.GOE_DEFINITION_ID");
			sql.appendln("WHERE GOED.GOE_DEFINITION_ID = ?");
			sql.appendln("AND GOER.RANGE_COA_CD = ? AND GOER.ACTV_CD = 'Y' AND GOER.ALLOW_DENY_FLAG = ?");

			List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
				Map<String, Object> rowMap = (Map<String, Object>) iterator.next();
				GOERule rule = new GOERule();
				rule.setRuleId((Long) rowMap.get("GOE_OBJ_CD_RULE_ID"));
				rule.setConsolidationObjectCode((String) rowMap.get("FIN_CONS_OBJ_CD"));
				rule.setObjectLevelCode((String) rowMap.get("FIN_OBJ_LEVEL_CD"));
				rule.setFromObjectCode((String) rowMap.get("FM_OBJ_CD"));
				rule.setToObjectCode((String) rowMap.get("TO_OBJ_CD"));
				ruleList.add(rule);
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return ruleList;
	}

}
