package edu.uci.oit.kfs.dwh.webservices.translation.dataaccess;

import edu.uci.oit.kfs.dwh.webservices.dataaccess.DataAccessObject;
import edu.uci.oit.kfs.dwh.webservices.translation.dto.Account;
import edu.uci.oit.kfs.dwh.webservices.translation.dto.ObjectCode;

public interface KfsToUcTranslationDao extends DataAccessObject {

	public ObjectCode getTopLevelReportsToObjectCode(String kfsFiscalYear, ObjectCode kfsObjectCode);

	public String findUCSubCode(String kfsChartCode, String objectLevelCode);

	public Account findAccount(String kfsChartCode, String kfsAccountNumber);

	public ObjectCode findObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsObjectCode);

	public String findBasicAccountingCategoryCode(String objectTypeCode);

	public String findUCUnexpendedBalanceAccountNumber(String subFundGroupCode);

}