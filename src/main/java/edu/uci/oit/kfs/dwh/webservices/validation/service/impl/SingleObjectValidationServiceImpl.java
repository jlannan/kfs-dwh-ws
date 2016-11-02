package edu.uci.oit.kfs.dwh.webservices.validation.service.impl;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;

import edu.uci.oit.kfs.dwh.webservices.validation.dataaccess.SingleObjectValidationDao;
import edu.uci.oit.kfs.dwh.webservices.validation.service.SingleObjectValidationService;

public class SingleObjectValidationServiceImpl implements SingleObjectValidationService {

	private SingleObjectValidationDao singleObjectValidationDao;

	@Override
	public boolean validateOriginationCode(String kfsOriginationCode) {

		if (StringUtils.isBlank(kfsOriginationCode)) {
			throw new BadRequestException("Invalid Parameter");
		}

		String result = singleObjectValidationDao.getOriginationCode(kfsOriginationCode);
		return convertResultToBoolean(result);
	}

	@Override
	public boolean validateChartOfAccountsCode(String kfsChartCode) {

		if (StringUtils.isBlank(kfsChartCode)) {
			throw new BadRequestException("Invalid Parameter");
		}

		String result = singleObjectValidationDao.getChartOfAccountsCode(kfsChartCode);
		return convertResultToBoolean(result);
	}

	@Override
	public boolean validateAccount(String kfsChartCode, String kfsAcctNumber) {

		if (StringUtils.isBlank(kfsChartCode) || StringUtils.isBlank(kfsAcctNumber)) {
			throw new BadRequestException("Invalid Parameter");
		}

		String result = singleObjectValidationDao.getAccountNumber(kfsChartCode, kfsAcctNumber);
		return convertResultToBoolean(result);
	}

	@Override
	public boolean validateSubAccount(String kfsChartCode, String kfsAcctNumber, String kfsSubAcctNumber) {

		if (StringUtils.isBlank(kfsChartCode) || StringUtils.isBlank(kfsAcctNumber) || StringUtils.isBlank(kfsSubAcctNumber)) {
			throw new BadRequestException("Invalid Parameter");
		}

		String result = singleObjectValidationDao.getSubAccountNumber(kfsChartCode, kfsAcctNumber, kfsSubAcctNumber);
		return convertResultToBoolean(result);
	}

	@Override
	public boolean validateObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsObjectCode) {

		if (StringUtils.isBlank(kfsFiscalYear) || StringUtils.isBlank(kfsChartCode) || StringUtils.isBlank(kfsObjectCode)) {
			throw new BadRequestException("Invalid Parameter");
		}

		String result = singleObjectValidationDao.getObjectCode(kfsFiscalYear, kfsChartCode, kfsObjectCode);
		return convertResultToBoolean(result);
	}

	@Override
	public boolean validateSubObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsAcctNumber, String kfsObjectCode,
			String kfsSubObjectCode) {

		if (StringUtils.isBlank(kfsFiscalYear) || StringUtils.isBlank(kfsChartCode) || StringUtils.isBlank(kfsAcctNumber)
				|| StringUtils.isBlank(kfsObjectCode) || StringUtils.isBlank(kfsSubObjectCode)) {
			return false;
		}

		String result = singleObjectValidationDao.getSubObjectCode(kfsFiscalYear, kfsChartCode, kfsAcctNumber, kfsObjectCode, kfsSubObjectCode);
		return convertResultToBoolean(result);
	}

	@Override
	public boolean validateProjectCode(String kfsProjectCode) {

		if (StringUtils.isBlank(kfsProjectCode)) {
			return false;
		}

		String result = singleObjectValidationDao.getProjectCode(kfsProjectCode);
		return convertResultToBoolean(result);
	}

	private boolean convertResultToBoolean(String result) {
		return !StringUtils.isBlank(result);
	}

	public void setSingleObjectValidationDao(SingleObjectValidationDao singleObjectValidationDao) {
		this.singleObjectValidationDao = singleObjectValidationDao;
	}
}
