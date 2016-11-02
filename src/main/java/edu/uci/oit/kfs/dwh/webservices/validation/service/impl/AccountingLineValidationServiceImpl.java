package edu.uci.oit.kfs.dwh.webservices.validation.service.impl;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;

import edu.uci.oit.kfs.dwh.webservices.dataaccess.impl.DataAccessObjectBase;
import edu.uci.oit.kfs.dwh.webservices.objectedit.service.GlobalObjectEditValidationService;
import edu.uci.oit.kfs.dwh.webservices.objectedit.service.OriginObjectEditValidationService;
import edu.uci.oit.kfs.dwh.webservices.validation.service.AccountingLineValidationService;
import edu.uci.oit.kfs.dwh.webservices.validation.service.SingleObjectValidationService;
import edu.uci.oit.kfs.dwh.webservices.wsresult.KFSAccountingLineValidationResult;
import edu.uci.oit.kfs.dwh.webservices.wsresult.ObjectCodeEditValidationResult;

public class AccountingLineValidationServiceImpl implements AccountingLineValidationService {

	private OriginObjectEditValidationService originObjectEditValidationService;
	private GlobalObjectEditValidationService globalObjectEditValidationService;
	private SingleObjectValidationService singleObjectValidationService;

	@Override
	public KFSAccountingLineValidationResult isValidKFSAccountingLine(String kfsFiscalYear, String kfsOriginationCode, String kfsChartCode,
			String kfsAccountNumber, String kfsSubAcctNumber, String kfsObjectCode, String kfsSubObjectCode, String kfsProjectCode) {

		if (StringUtils.isEmpty(kfsOriginationCode) || StringUtils.isEmpty(kfsChartCode) || StringUtils.isEmpty(kfsAccountNumber)
				|| StringUtils.isEmpty(kfsAccountNumber) || StringUtils.isEmpty(kfsObjectCode)) {
			throw new BadRequestException("missing required parameter(s)");
		}

		if (StringUtils.isEmpty(kfsFiscalYear)) {
			kfsFiscalYear = ((DataAccessObjectBase) originObjectEditValidationService).findCurrentFiscalYear();
		}

		kfsFiscalYear = StringUtils.trimToEmpty(kfsFiscalYear);
		kfsOriginationCode = StringUtils.trimToEmpty(kfsOriginationCode).toUpperCase();
		kfsChartCode = StringUtils.trimToEmpty(kfsChartCode).toUpperCase();
		kfsAccountNumber = StringUtils.trimToEmpty(kfsAccountNumber).toUpperCase();
		kfsSubAcctNumber = StringUtils.trimToEmpty(kfsSubAcctNumber).toUpperCase();
		kfsObjectCode = StringUtils.trimToEmpty(kfsObjectCode).toUpperCase();
		kfsSubObjectCode = StringUtils.trimToEmpty(kfsSubObjectCode).toUpperCase();
		kfsProjectCode = StringUtils.trimToEmpty(kfsProjectCode).toUpperCase();

		boolean validOriginationCode = singleObjectValidationService.validateOriginationCode(kfsOriginationCode);
		if (!validOriginationCode) {
			return createNotFoundResult("kfsOriginationCode not found");
		}

		boolean validChartCode = singleObjectValidationService.validateChartOfAccountsCode(kfsChartCode);
		if (!validChartCode) {
			return createNotFoundResult("kfsChartCode not found");
		}

		boolean validAccountNumber = singleObjectValidationService.validateAccount(kfsChartCode, kfsAccountNumber);
		if (!validAccountNumber) {
			return createNotFoundResult("kfsAcctNumber not found");
		}

		if (StringUtils.isNotEmpty(kfsSubAcctNumber)) {
			boolean validSubAccountNumber = singleObjectValidationService.validateSubAccount(kfsChartCode, kfsAccountNumber, kfsSubAcctNumber);
			if (!validSubAccountNumber) {
				return createNotFoundResult("kfsSubAcctNumber not found");
			}
		}

		boolean validObjectCode = singleObjectValidationService.validateObjectCode(kfsFiscalYear, kfsChartCode, kfsObjectCode);
		if (!validObjectCode) {
			return createNotFoundResult("kfsObjectCode not found");
		}

		if (StringUtils.isNotEmpty(kfsSubObjectCode)) {
			boolean validSubObjectCode = singleObjectValidationService.validateSubObjectCode(kfsFiscalYear, kfsChartCode, kfsAccountNumber,
					kfsObjectCode, kfsSubObjectCode);
			if (!validSubObjectCode) {
				return createNotFoundResult("kfsSubObjectCode not found");
			}
		}

		if (StringUtils.isNotEmpty(kfsProjectCode)) {
			boolean validProjectCode = singleObjectValidationService.validateProjectCode(kfsProjectCode);
			if (!validProjectCode) {
				return createNotFoundResult("kfsProjectCode not found");
			}
		}

		KFSAccountingLineValidationResult accountingLineValidationResult = new KFSAccountingLineValidationResult();

		ObjectCodeEditValidationResult ooeValidationResult = originObjectEditValidationService.getObjectCodeEditValidationResult(kfsFiscalYear,
				kfsOriginationCode, kfsChartCode, kfsObjectCode);

		accountingLineValidationResult.setValid(ooeValidationResult.isValid());
		accountingLineValidationResult.setMatchingOOEDefinitionId(ooeValidationResult.getMatchingDefinitionId());
		if (!ooeValidationResult.isValid()) {
			accountingLineValidationResult.setMessage("Failed OOE Validation");
			return accountingLineValidationResult;
		}

		ObjectCodeEditValidationResult goeValidationResult = globalObjectEditValidationService.getGlobalObjectCodeEditValidationResult(kfsFiscalYear,
				kfsChartCode, kfsAccountNumber, kfsObjectCode);

		accountingLineValidationResult.setValid(goeValidationResult.isValid());
		accountingLineValidationResult.setMatchingGOEDefinitionId(goeValidationResult.getMatchingDefinitionId());
		if (!goeValidationResult.isValid()) {
			accountingLineValidationResult.setMessage("Failed GOE Validation");
			return accountingLineValidationResult;
		}

		accountingLineValidationResult.setValid(true);
		accountingLineValidationResult.setMessage("Success");

		return accountingLineValidationResult;
	}

	private KFSAccountingLineValidationResult createNotFoundResult(String message) {
		return new KFSAccountingLineValidationResult(false, message, 0L, 0L);
	}

	public void setOriginObjectEditValidationService(OriginObjectEditValidationService originObjectEditValidationService) {
		this.originObjectEditValidationService = originObjectEditValidationService;
	}

	public void setGlobalObjectEditValidationService(GlobalObjectEditValidationService globalObjectEditValidationService) {
		this.globalObjectEditValidationService = globalObjectEditValidationService;
	}

	public void setSingleObjectValidationService(SingleObjectValidationService singleObjectValidationService) {
		this.singleObjectValidationService = singleObjectValidationService;
	}

}
