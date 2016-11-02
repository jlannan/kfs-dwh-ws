package edu.uci.oit.kfs.dwh.webservices.validation.service;

public interface SingleObjectValidationService {

	public boolean validateOriginationCode(String kfsOriginationCode);

	public boolean validateChartOfAccountsCode(String kfsChartCode);

	public boolean validateAccount(String kfsChartCode, String kfsAcctNumber);

	public boolean validateSubAccount(String kfsChartCode, String kfsAcctNumber, String kfsSubAcctNumber);

	public boolean validateObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsObjectCode);

	public boolean validateSubObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsAcctNumber,
			String kfsObjectCode, String kfsSubObjectCode);

	public boolean validateProjectCode(String kfsProjectCode);

}