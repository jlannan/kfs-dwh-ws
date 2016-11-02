package edu.uci.oit.kfs.dwh.webservices.validation.dataaccess;

public interface SingleObjectValidationDao {

	public String getOriginationCode(String kfsOriginationCode);

	public String getChartOfAccountsCode(String kfsChartCode);

	public String getAccountNumber(String kfsChartCode, String kfsAcctNumber);

	public String getSubAccountNumber(String kfsChartCode, String kfsAcctNumber, String kfsSubAcctNumber);

	public String getObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsObjectCode);

	public String getSubObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsAcctNumber, String kfsObjectCode, String kfsSubObjectCode);

	public String getProjectCode(String kfsProjectCode);

}