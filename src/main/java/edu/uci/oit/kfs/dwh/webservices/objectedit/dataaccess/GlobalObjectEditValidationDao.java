package edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess;

import java.util.List;

import edu.uci.oit.kfs.dwh.webservices.objectedit.ObjectEditConstants.RuleType;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.Account;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.AccountRange;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.GOERule;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.ObjectCodeAccountExclusion;

public interface GlobalObjectEditValidationDao extends ObjectEditValidationDao {

	public Account findAccount(String kfsChartCode, String kfsAccountNumber);

	public List<AccountRange> findAccountRanges(String kfsSubFundGroupCode, String kfsChartCode);

	public List<ObjectCodeAccountExclusion> findObjectCodeAccountNumberExclusions(Long ruleId, String kfsChartOfAccountsCode);

	public List<GOERule> findGOERules(Long goeDefinitionId, String kfsChartOfAccountsCode, RuleType ruleType);

}