package edu.uci.oit.kfs.dwh.webservices.validation.service;

import edu.uci.oit.kfs.dwh.webservices.wsresult.KFSAccountingLineValidationResult;

public interface AccountingLineValidationService {

	public KFSAccountingLineValidationResult isValidKFSAccountingLine(
			String kfsFiscalYear, String kfsOriginationCode, String kfsChartCode, String kfsAccountNumber,
			String kfsSubAcctNumber, String kfsObjectCode, String kfsSubObjectCode, String kfsProjectCode);

}