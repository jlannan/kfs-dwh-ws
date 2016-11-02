package edu.uci.oit.kfs.dwh.webservices;

import edu.uci.oit.kfs.dwh.webservices.wsresult.KfsToUcAccountingLineTranslationResult;

public interface DWHRestServer {

	public KfsToUcAccountingLineTranslationResult translateKFSAccountingLine(String kfsFiscalYear, String kfsChartCode, String kfsAccountNumber,
			String kfsObjectCode);
}
