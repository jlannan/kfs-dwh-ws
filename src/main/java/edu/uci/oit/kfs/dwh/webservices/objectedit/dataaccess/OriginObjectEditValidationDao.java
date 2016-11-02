package edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess;

import java.util.List;

import edu.uci.oit.kfs.dwh.webservices.objectedit.ObjectEditConstants.RuleType;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.OOERule;

public interface OriginObjectEditValidationDao extends ObjectEditValidationDao {

	public List<OOERule> findOOERules(String originationCode, String chartOfAccountsCode, RuleType ruleType);

}