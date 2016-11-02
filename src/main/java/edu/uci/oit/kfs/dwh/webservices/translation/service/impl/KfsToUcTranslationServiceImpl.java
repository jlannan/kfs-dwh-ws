package edu.uci.oit.kfs.dwh.webservices.translation.service.impl;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;

import edu.uci.oit.kfs.dwh.webservices.translation.KfsToUcTranslationConstants;
import edu.uci.oit.kfs.dwh.webservices.translation.dataaccess.KfsToUcTranslationDao;
import edu.uci.oit.kfs.dwh.webservices.translation.dto.Account;
import edu.uci.oit.kfs.dwh.webservices.translation.dto.ObjectCode;
import edu.uci.oit.kfs.dwh.webservices.translation.service.KfsToUcTranslationService;
import edu.uci.oit.kfs.dwh.webservices.translation.util.KfsToUcTranslationUtil;
import edu.uci.oit.kfs.dwh.webservices.wsresult.KfsToUcAccountingLineTranslationResult;

public class KfsToUcTranslationServiceImpl implements KfsToUcTranslationService {

	private KfsToUcTranslationDao kfsToUcTranslationDao;

	@Override
	@Cacheable("kfsToUcTranslation_TranslateKFSAccountingLine")
	public KfsToUcAccountingLineTranslationResult translateKFSAccountingLine(String kfsFiscalYear, String kfsChartCode, String kfsAccountNumber,
			String kfsObjectCode) {

		if (StringUtils.isBlank(kfsChartCode) || StringUtils.isBlank(kfsAccountNumber) || StringUtils.isBlank(kfsObjectCode)) {
			throw new BadRequestException("missing required parameter(s)");
		}

		// default to current fiscal year if not passed in
		if (StringUtils.isBlank(kfsFiscalYear)) {
			kfsFiscalYear = kfsToUcTranslationDao.findCurrentFiscalYear();
		}

		// translate KFS account
		Account account = kfsToUcTranslationDao.findAccount(kfsChartCode, kfsAccountNumber);

		if (account == null) {
			return getFailureKFSToUCAccountingLineTranslationResult("Account not found");
		}

		ObjectCode objectCode = kfsToUcTranslationDao.findObjectCode(kfsFiscalYear, kfsChartCode, kfsObjectCode);

		if (objectCode == null) {
			return getFailureKFSToUCAccountingLineTranslationResult("Object Code not found");
		}

		KfsToUcAccountingLineTranslationResult successKFSToUCAccountingLineTranslationResult = null;

		if (KfsToUcTranslationUtil.isFundBalanceObjectCodeType(objectCode) || KfsToUcTranslationUtil.isOFTIndicatorTransCode(objectCode)) {
			String ubAccountNumber = kfsToUcTranslationDao.findUCUnexpendedBalanceAccountNumber(account.getSubFundGroupCode());
			if (ubAccountNumber == null) {
				return getFailureKFSToUCAccountingLineTranslationResult("SubFundGroupCode not found");
			} else {
				successKFSToUCAccountingLineTranslationResult = getSuccessKFSToUCAccountingLineTranslationResult(account.getUcLocationCode(),
						ubAccountNumber, account.getUcFundNumber());
			}
		} else {
			if (isBalanceSheetOrRevenueObjectTypeCode(objectCode.getObjectTypeCode())) {
				successKFSToUCAccountingLineTranslationResult = getSuccessKFSToUCAccountingLineTranslationResult(account.getUcLocationCode(),
						objectCode.getUcAccountNumber(), account.getUcFundNumber());
			} else {
				successKFSToUCAccountingLineTranslationResult = getSuccessKFSToUCAccountingLineTranslationResult(account.getUcLocationCode(),
						account.getUcAccountNumber(), account.getUcFundNumber());
			}
		}

		// translate KFS object code
		ObjectCode topLevelReportsToObjectCode = kfsToUcTranslationDao.getTopLevelReportsToObjectCode(kfsFiscalYear, objectCode);

		// check if original object code is Expense
		if (isExpensObjectTypeCode(objectCode)) {

			String ucSubCode = kfsToUcTranslationDao.findUCSubCode(objectCode.getChartCode(), objectCode.getObjectLevelCode());
			if (StringUtils.isEmpty(ucSubCode)) {
				throw new IllegalStateException(
						String.format("UC Sub required for expense object code %s %s", objectCode.getChartCode(), objectCode.getObjectLevelCode()));
			}

			ucSubCode = KfsToUcTranslationUtil.convertUcSubFormat(ucSubCode);
			successKFSToUCAccountingLineTranslationResult.setUcSubCode(ucSubCode);
		}

		successKFSToUCAccountingLineTranslationResult.setUcObjectCode(topLevelReportsToObjectCode.getObjectCode());
		return successKFSToUCAccountingLineTranslationResult;
	}

	private boolean isBalanceSheetOrRevenueObjectTypeCode(String objectTypeCode) {

		if (KfsToUcTranslationConstants.OBJECT_TYPE_CODE_INCOME_NOT_CASH.equalsIgnoreCase(objectTypeCode)) {
			return false;
		}

		String basicAccountingCategoryCode = kfsToUcTranslationDao.findBasicAccountingCategoryCode(objectTypeCode);

		boolean isBalanceSheetOrRevenueBasicAccountingCategory = KfsToUcTranslationConstants.BAL_SHEET_AND_REVENUE_BASIC_ACCTG_CATEGORY_CODES
				.contains(basicAccountingCategoryCode);

		return isBalanceSheetOrRevenueBasicAccountingCategory;
	}

	private boolean isExpensObjectTypeCode(ObjectCode kfsObjectCode) {

		String basicAccountingCategoryCode = kfsToUcTranslationDao.findBasicAccountingCategoryCode(kfsObjectCode.getObjectTypeCode());

		boolean isIncomeNotCashObjectTypeCode = KfsToUcTranslationConstants.OBJECT_TYPE_CODE_INCOME_NOT_CASH
				.equalsIgnoreCase(kfsObjectCode.getObjectTypeCode());
		boolean isExpenseBasicAccountingCategoryCode = KfsToUcTranslationConstants.BASIC_ACCOUNTING_CATEGORY_CODE_EX
				.equals(basicAccountingCategoryCode);

		if (isExpenseBasicAccountingCategoryCode || isIncomeNotCashObjectTypeCode) {
			return true;
		}

		return false;
	}

	private KfsToUcAccountingLineTranslationResult getFailureKFSToUCAccountingLineTranslationResult(String message) {

		KfsToUcAccountingLineTranslationResult failureKFSToUCAccountingLineTranslationResult = new KfsToUcAccountingLineTranslationResult();
		failureKFSToUCAccountingLineTranslationResult.setValid(false);
		failureKFSToUCAccountingLineTranslationResult.setMessage(message);
		return failureKFSToUCAccountingLineTranslationResult;
	}

	private KfsToUcAccountingLineTranslationResult getSuccessKFSToUCAccountingLineTranslationResult(String ucLocationCode, String ucAccountNumber,
			String ucFundNumber) {

		KfsToUcAccountingLineTranslationResult successKFSToUCAccountingLineTranslationResult = new KfsToUcAccountingLineTranslationResult();
		successKFSToUCAccountingLineTranslationResult.setValid(true);
		successKFSToUCAccountingLineTranslationResult.setMessage("Success");
		successKFSToUCAccountingLineTranslationResult.setUcLocationCode(ucLocationCode);
		successKFSToUCAccountingLineTranslationResult.setUcAccountNumber(ucAccountNumber);
		successKFSToUCAccountingLineTranslationResult.setUcFundNumber(ucFundNumber);
		return successKFSToUCAccountingLineTranslationResult;
	}

	public void setKfsToUcTranslationDao(KfsToUcTranslationDao kfsToUCTranslationDao) {
		this.kfsToUcTranslationDao = kfsToUCTranslationDao;
	}

}
