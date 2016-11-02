package edu.uci.oit.kfs.dwh.webservices.translation.service;

import edu.uci.oit.kfs.dwh.webservices.wsresult.KfsToUcAccountingLineTranslationResult;

public interface KfsToUcTranslationService {

	public KfsToUcAccountingLineTranslationResult translateKFSAccountingLine(String kfsFiscalYear, String kfsChartCode, String kfsAccountNumber,
			String kfsObjectCode);

}