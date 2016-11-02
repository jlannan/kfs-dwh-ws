package edu.uci.oit.kfs.dwh.webservices;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import edu.uci.oit.kfs.dwh.webservices.translation.service.KfsToUcTranslationService;
import edu.uci.oit.kfs.dwh.webservices.validation.service.AccountingLineValidationService;
import edu.uci.oit.kfs.dwh.webservices.wsresult.KFSAccountingLineValidationResult;
import edu.uci.oit.kfs.dwh.webservices.wsresult.KfsToUcAccountingLineTranslationResult;

@Path("/rest")
public class DWHRestServerImpl implements DWHRestServer {

	Logger LOG = Logger.getLogger(DWHRestServerImpl.class);

	private KfsToUcTranslationService kfsToUcTranslationService;
	private AccountingLineValidationService accountingLineValidationService;

	@GET
	@Path("/coa/accountingLine/translate")
	@Produces("application/json")
	public KfsToUcAccountingLineTranslationResult translateKFSAccountingLine(@QueryParam("kfsFiscalYear") String kfsFiscalYear,
			@QueryParam("kfsChartCode") String kfsChartCode, @QueryParam("kfsAccountNumber") String kfsAccountNumber,
			@QueryParam("kfsObjectCode") String kfsObjectCode) {

		kfsFiscalYear = StringUtils.trimToEmpty(kfsFiscalYear);
		kfsChartCode = StringUtils.trimToEmpty(kfsChartCode).toUpperCase();
		kfsAccountNumber = StringUtils.trimToEmpty(kfsAccountNumber).toUpperCase();
		kfsObjectCode = StringUtils.trimToEmpty(kfsObjectCode).toUpperCase();

		KfsToUcAccountingLineTranslationResult kfsToUCAccountingLineTranslationResult = kfsToUcTranslationService
				.translateKFSAccountingLine(kfsFiscalYear, kfsChartCode, kfsAccountNumber, kfsObjectCode);
		return kfsToUCAccountingLineTranslationResult;
	}

	@GET
	@Path("/coa/accountingLine/validate")
	@Produces("application/json")
	public KFSAccountingLineValidationResult isValidKFSAccountingLine(@QueryParam("kfsFiscalYear") String kfsFiscalYear,
			@QueryParam("kfsOriginationCode") String kfsOriginationCode, @QueryParam("kfsChartCode") String kfsChartCode,
			@QueryParam("kfsAccountNumber") String kfsAccountNumber, @QueryParam("kfsSubAccountNumber") String kfsSubAcctNumber,
			@QueryParam("kfsObjectCode") String kfsObjectCode, @QueryParam("kfsSubObjectCode") String kfsSubObjectCode,
			@QueryParam("kfsProjectCode") String kfsProjectCode) {

		return accountingLineValidationService.isValidKFSAccountingLine(kfsFiscalYear, kfsOriginationCode, kfsChartCode, kfsAccountNumber,
				kfsSubAcctNumber, kfsObjectCode, kfsSubObjectCode, kfsProjectCode);
	}

	public void setKfsToUcTranslationService(KfsToUcTranslationService kfsToUcTranslationService) {
		this.kfsToUcTranslationService = kfsToUcTranslationService;
	}

	public void setAccountingLineValidationService(AccountingLineValidationService accountingLineValidationService) {
		this.accountingLineValidationService = accountingLineValidationService;
	}

}