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
import edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess.OriginObjectEditValidationDao;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.OOERule;

public class OriginObjectEditValidationDaoImpl extends ObjectEditValidationDaoImpl implements OriginObjectEditValidationDao {

	private Logger LOG = Logger.getLogger(OriginObjectEditValidationDaoImpl.class);

	@Override
	@Cacheable("objectCodeEditOOERules")
	public List<OOERule> findOOERules(String originationCode, String chartOfAccountsCode, RuleType ruleType) {

		List<OOERule> ruleList = new ArrayList<OOERule>();

		if (StringUtils.isEmpty(originationCode) || StringUtils.isEmpty(chartOfAccountsCode) || ruleType == null) {
			return ruleList;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(originationCode);
		args.add(chartOfAccountsCode);
		args.add(RuleType.ALLOW.equals(ruleType) ? "A" : "D");

		try {

			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT OOED.OOE_DEFINITION_ID, OOER.OOE_OBJ_CD_RULE_ID, OOER.FIN_CONS_OBJ_CD, OOER.FIN_OBJ_LEVEL_CD, OOER.FM_OBJ_CD, OOER.TO_OBJ_CD");
			sql.appendln("FROM OOE_DEFINITION_T OOED");
			sql.appendln("JOIN OOE_OBJ_CD_RULE_T OOER ON OOED.OOE_DEFINITION_ID = OOER.OOE_DEFINITION_ID");
			sql.appendln("WHERE OOED.ACTV_CD = 'Y' AND OOED.FS_ORIGIN_CD = ?");
			sql.appendln("AND OOER.RANGE_COA_CD = ? AND OOER.ACTV_CD = 'Y' AND OOER.ALLOW_DENY_FLAG = ?");

			List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
				Map<String, Object> rowMap = (Map<String, Object>) iterator.next();
				OOERule rule = new OOERule();
				rule.setDefinitionId((Long) rowMap.get("OOE_DEFINITION_ID"));
				rule.setRuleId((Long) rowMap.get("OOE_OBJ_CD_RULE_ID"));
				rule.setRuleId((Long) rowMap.get("OOE_OBJ_CD_RULE_ID"));
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
