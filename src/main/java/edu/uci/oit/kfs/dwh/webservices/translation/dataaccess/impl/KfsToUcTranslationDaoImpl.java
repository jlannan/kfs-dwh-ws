package edu.uci.oit.kfs.dwh.webservices.translation.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import edu.uci.oit.kfs.dwh.webservices.dataaccess.impl.DataAccessObjectBase;
import edu.uci.oit.kfs.dwh.webservices.translation.dataaccess.KfsToUcTranslationDao;
import edu.uci.oit.kfs.dwh.webservices.translation.dto.Account;
import edu.uci.oit.kfs.dwh.webservices.translation.dto.ObjectCode;

public class KfsToUcTranslationDaoImpl extends DataAccessObjectBase implements KfsToUcTranslationDao {

	private Logger LOG = Logger.getLogger(KfsToUcTranslationDaoImpl.class);

	private static final String CHART_CODE_UC = "UC";

	@Override
	@Cacheable("kfsToUcTranslation_TopLevelReportsToObjectCode")
	public ObjectCode getTopLevelReportsToObjectCode(String kfsFiscalYear, ObjectCode kfsObjectCode) {

		if (kfsObjectCode == null) {
			return null;
		}

		int counter = 0;
		while (!StringUtils.equals(kfsObjectCode.getChartCode(), kfsObjectCode.getReportsToChartCode()) || !StringUtils.equals(kfsObjectCode.getObjectCode(), kfsObjectCode.getReportsToObjectCode())) {

			if (counter++ > 20) {
				throw new IllegalStateException(String.format("Cycle detected for reportsTo traversal of objectCode:%s", kfsObjectCode.getChartCode() + " " + kfsObjectCode.getObjectCode()));
			}

			kfsObjectCode = findObjectCode(kfsFiscalYear, kfsObjectCode.getReportsToChartCode(), kfsObjectCode.getReportsToObjectCode());
		}

		if (!CHART_CODE_UC.equals(kfsObjectCode.getChartCode())) {
			throw new IllegalStateException(
					String.format("Expected top level object code chart: %s chart but found object %s", CHART_CODE_UC, kfsObjectCode.getChartCode() + " " + kfsObjectCode.getObjectCode()));
		}

		return kfsObjectCode;
	}

	@Override
	@Cacheable("kfsToUcTranslation_UCSubCode")
	public String findUCSubCode(String kfsChartCode, String objectLevelCode) {

		if (StringUtils.isEmpty(kfsChartCode) || StringUtils.isEmpty(objectLevelCode)) {
			return null;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(kfsChartCode);
		args.add(objectLevelCode);

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("select oce.UC_SUB_CD");
			sql.appendln("from CA_OBJ_LEVEL_T ol");
			sql.appendln("JOIN CA_OBJ_CONSOLDTN_T oc on oc.FIN_COA_CD = ol.FIN_COA_CD and oc.FIN_CONS_OBJ_CD = ol.FIN_CONS_OBJ_CD ");
			sql.appendln("JOIN CA_OBJ_CONSOLDTN_EXT_T oce on oce.FIN_COA_CD = oc.FIN_COA_CD and oce.FIN_CONS_OBJ_CD = oc.FIN_CONS_OBJ_CD ");
			sql.appendln("where ol.FIN_COA_CD = ? AND ol.FIN_OBJ_LEVEL_CD = ?");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			if (resultSet.iterator().hasNext()) {

				Map<String, Object> rowMap = (Map<String, Object>) resultSet.iterator().next();
				String ucSubCode = (String) rowMap.get("UC_SUB_CD");
				return ucSubCode;
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return null;
	}

	@Override
	@Cacheable("kfsToUcTranslation_Account")
	public Account findAccount(String kfsChartCode, String kfsAccountNumber) {

		if (StringUtils.isEmpty(kfsChartCode) || StringUtils.isEmpty(kfsAccountNumber)) {
			return null;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(kfsChartCode);
		args.add(kfsAccountNumber);

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("select a.SUB_FUND_GRP_CD, ae.UC_LOC_CD, ae.UC_ACCT_NBR, ae.UC_FUND_NBR");
			sql.appendln("from CA_ACCOUNT_T a");
			sql.appendln("join CA_ACCOUNT_EXT_T ae on  a.FIN_COA_CD = ae.FIN_COA_CD");
			sql.appendln("where a.ACCOUNT_NBR = ae.ACCOUNT_NBR");
			sql.appendln("and a.FIN_COA_CD = ? and a.ACCOUNT_NBR = ?");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			if (resultSet.iterator().hasNext()) {

				Map<String, Object> rowMap = (Map<String, Object>) resultSet.iterator().next();

				Account account = new Account();

				account.setSubFundGroupCode((String) rowMap.get("SUB_FUND_GRP_CD"));
				account.setUcLocationCode((String) rowMap.get("UC_LOC_CD"));
				account.setUcAccountNumber((String) rowMap.get("UC_ACCT_NBR"));
				account.setUcFundNumber((String) rowMap.get("UC_FUND_NBR"));
				return account;
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return null;
	}

	@Override
	@Cacheable("kfsToUcTranslation_ObjectCode")
	public ObjectCode findObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsObjectCode) {

		if (StringUtils.isEmpty(kfsFiscalYear) || StringUtils.isEmpty(kfsChartCode) || StringUtils.isEmpty(kfsObjectCode)) {
			return null;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(kfsFiscalYear);
		args.add(kfsChartCode);
		args.add(kfsObjectCode);

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("select o.FIN_COA_CD, o.FIN_OBJECT_CD, o.FIN_OBJ_TYP_CD, ol.FIN_OBJ_LEVEL_CD, oe.UC_ACCT_NBR, oe.OFT_IND, o.RPTS_TO_FIN_COA_CD, o.RPTS_TO_FIN_OBJ_CD");
			sql.appendln("from CA_OBJECT_CODE_T o");
			sql.appendln("join CA_OBJECT_CODE_EXT_T oe on o.UNIV_FISCAL_YR = oe.UNIV_FISCAL_YR and o.FIN_COA_CD = oe.FIN_COA_CD and o.FIN_OBJECT_CD = oe.FIN_OBJECT_CD");
			sql.appendln("join CA_OBJ_LEVEL_T ol on o.FIN_COA_CD = ol.FIN_COA_CD and o.FIN_OBJ_LEVEL_CD = ol.FIN_OBJ_LEVEL_CD");
			sql.appendln("where o.UNIV_FISCAL_YR = ? and o.FIN_COA_CD = ? and o.FIN_OBJECT_CD = ?");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			if (resultSet.iterator().hasNext()) {

				Map<String, Object> rowMap = (Map<String, Object>) resultSet.iterator().next();

				ObjectCode objectCode = new ObjectCode();
				objectCode.setChartCode((String) rowMap.get("FIN_COA_CD"));
				objectCode.setObjectCode((String) rowMap.get("FIN_OBJECT_CD"));
				objectCode.setObjectTypeCode((String) rowMap.get("FIN_OBJ_TYP_CD"));
				objectCode.setObjectLevelCode((String) rowMap.get("FIN_OBJ_LEVEL_CD"));
				objectCode.setUcAccountNumber((String) rowMap.get("UC_ACCT_NBR"));
				objectCode.setOftIndicator((String) rowMap.get("OFT_IND"));
				objectCode.setReportsToChartCode((String) rowMap.get("RPTS_TO_FIN_COA_CD"));
				objectCode.setReportsToObjectCode((String) rowMap.get("RPTS_TO_FIN_OBJ_CD"));
				return objectCode;
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return null;
	}

	@Override
	@Cacheable("kfsToUcTranslation_BasicAccountingCategoryCode")
	public String findBasicAccountingCategoryCode(String objectTypeCode) {

		if (StringUtils.isEmpty(objectTypeCode)) {
			return null;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(objectTypeCode);

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT ACCTG_CTGRY_CD");
			sql.appendln("FROM CA_OBJ_TYPE_T");
			sql.appendln("WHERE FIN_OBJ_TYP_CD = ?");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			if (resultSet.iterator().hasNext()) {

				Map<String, Object> rowMap = (Map<String, Object>) resultSet.iterator().next();

				String basicAccountingCategoryCode = (String) rowMap.get("ACCTG_CTGRY_CD");
				return basicAccountingCategoryCode;
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return null;
	}

	@Override
	@Cacheable("kfsToUcTranslation_UCUnexpendedBalanceAccountNumber")
	public String findUCUnexpendedBalanceAccountNumber(String subFundGroupCode) {

		if (StringUtils.isEmpty(subFundGroupCode)) {
			return null;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(subFundGroupCode);

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT UC_ACCT_NBR_FOR_UB");
			sql.appendln("FROM CA_SUB_FUND_GRP_EXT_T");
			sql.appendln("WHERE SUB_FUND_GRP_CD = ?");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			if (resultSet.iterator().hasNext()) {

				Map<String, Object> rowMap = (Map<String, Object>) resultSet.iterator().next();

				String ubAccountNumber = (String) rowMap.get("UC_ACCT_NBR_FOR_UB");
				return ubAccountNumber;
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return null;
	}

}
