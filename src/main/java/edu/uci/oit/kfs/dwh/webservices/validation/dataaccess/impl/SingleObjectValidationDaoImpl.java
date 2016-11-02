package edu.uci.oit.kfs.dwh.webservices.validation.dataaccess.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;

import edu.uci.oit.kfs.dwh.webservices.dataaccess.impl.DataAccessObjectBase;
import edu.uci.oit.kfs.dwh.webservices.validation.dataaccess.SingleObjectValidationDao;

public class SingleObjectValidationDaoImpl extends DataAccessObjectBase implements SingleObjectValidationDao {

	private Logger LOG = Logger.getLogger(SingleObjectValidationDaoImpl.class);

	@Override
	@Cacheable("singleOriginationCodeValidation")
	public String getOriginationCode(String kfsOriginationCode) {

		List<Object> args = new ArrayList<Object>(1);
		args.add(kfsOriginationCode.trim().toUpperCase());

		StrBuilder sqlBuilder = new StrBuilder();
		sqlBuilder.appendln("SELECT FS_ORIGIN_CD");
		sqlBuilder.appendln("FROM fs_origin_code_t");
		sqlBuilder.appendln("WHERE ROW_ACTV_IND <> 'N'");
		sqlBuilder.appendln("AND FS_ORIGIN_CD = ?");

		String originationCode = queryForString(sqlBuilder.toString(), args);
		return originationCode;
	}

	@Override
	@Cacheable("singleChartOfAccountsCodeValidation")
	public String getChartOfAccountsCode(String kfsChartCode) {

		List<Object> args = new ArrayList<Object>(1);
		args.add(kfsChartCode.toUpperCase());

		StrBuilder sqlBuilder = new StrBuilder();
		sqlBuilder.appendln("SELECT FIN_COA_CD");
		sqlBuilder.appendln("FROM ca_chart_t");
		sqlBuilder.appendln("WHERE FIN_COA_ACTIVE_CD <> 'N'");
		sqlBuilder.appendln("AND FIN_COA_CD = ?");

		String chartOfAccountsCode = queryForString(sqlBuilder.toString(), args);
		return chartOfAccountsCode;
	}

	@Override
	@Cacheable("singleAccountValidation")
	public String getAccountNumber(String kfsChartCode, String kfsAcctNumber) {

		List<Object> args = new ArrayList<Object>(2);
		args.add(kfsChartCode.toUpperCase());
		args.add(kfsAcctNumber.toString());

		StrBuilder sqlBuilder = new StrBuilder();
		sqlBuilder.appendln("SELECT ACCOUNT_NBR");
		sqlBuilder.appendln("FROM ca_account_t");
		sqlBuilder.appendln("WHERE FIN_COA_CD = ? AND ACCOUNT_NBR= ?");
		sqlBuilder.appendln("AND ACCT_CLOSED_IND <> 'Y'");
		sqlBuilder.appendln("AND cast(ACCT_EFFECT_DT as Date) <= cast(getDate() as Date)");
		sqlBuilder.appendln("AND (");
		sqlBuilder.appendln("		cast(ACCT_EXPIRATION_DT as Date) >= cast(getDate() as Date)");
		sqlBuilder.appendln("		OR ACCT_EXPIRATION_DT IS NULL");
		sqlBuilder.appendln("	)");

		String accountNumber = queryForString(sqlBuilder.toString(), args);
		return accountNumber;
	}

	@Override
	@Cacheable("singleSubAccountValidation")
	public String getSubAccountNumber(String kfsChartCode, String kfsAcctNumber, String kfsSubAcctNumber) {

		List<Object> args = new ArrayList<Object>(3);
		args.add(kfsChartCode.toUpperCase().trim());
		args.add(kfsAcctNumber.toUpperCase().trim());
		args.add(kfsSubAcctNumber.toUpperCase().trim());

		StrBuilder sqlBuilder = new StrBuilder();
		sqlBuilder.appendln("SELECT SUB_ACCT_NBR");
		sqlBuilder.appendln("FROM ca_sub_acct_t");
		sqlBuilder.appendln("WHERE SUB_ACCT_ACTV_CD <> 'N'");
		sqlBuilder.appendln("AND fin_coa_cd=? AND account_nbr=? AND sub_acct_nbr=?");

		String subAccountNumber = queryForString(sqlBuilder.toString(), args);
		return subAccountNumber;
	}

	@Override
	@Cacheable("singleObjectCodeValidation")
	public String getObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsObjectCode) {

		List<Object> args = new ArrayList<Object>(3);
		args.add(kfsFiscalYear.trim());
		args.add(kfsChartCode.toUpperCase().trim());
		args.add(kfsObjectCode.toUpperCase().trim());

		StrBuilder sqlBuilder = new StrBuilder();
		sqlBuilder.appendln("SELECT FIN_OBJECT_CD");
		sqlBuilder.appendln("FROM ca_object_code_t");
		sqlBuilder.appendln("WHERE UNIV_FISCAL_YR=? AND FIN_COA_CD=? AND FIN_OBJECT_CD=?");

		String objectCode = queryForString(sqlBuilder.toString(), args);
		return objectCode;
	}

	@Override
	@Cacheable("singleSubObjectCodeValidation")
	public String getSubObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsAcctNumber, String kfsObjectCode, String kfsSubObjectCode) {

		List<Object> args = new ArrayList<Object>(5);
		args.add(kfsFiscalYear.trim());
		args.add(kfsChartCode.toUpperCase().trim());
		args.add(kfsAcctNumber.toUpperCase().trim());
		args.add(kfsObjectCode.toUpperCase().trim());
		args.add(kfsSubObjectCode.toUpperCase().trim());

		StrBuilder sqlBuilder = new StrBuilder();
		sqlBuilder.appendln("SELECT FIN_SUB_OBJ_CD");
		sqlBuilder.appendln("FROM CA_SUB_OBJECT_CD_T");
		sqlBuilder.appendln("WHERE FIN_SUBOBJ_ACTV_CD <> 'N'");
		sqlBuilder.appendln("AND UNIV_FISCAL_YR=? AND FIN_COA_CD=? AND ACCOUNT_NBR=? AND FIN_OBJECT_CD=? AND FIN_SUB_OBJ_CD=?");

		String subObjectCode = queryForString(sqlBuilder.toString(), args);
		return subObjectCode;
	}

	@Override
	@Cacheable("singleProjectCodeValidation")
	public String getProjectCode(String kfsProjectCode) {

		List<Object> args = new ArrayList<Object>(1);
		args.add(kfsProjectCode.toUpperCase().trim());

		StrBuilder sqlBuilder = new StrBuilder();
		sqlBuilder.appendln("SELECT PROJECT_CD");
		sqlBuilder.appendln("FROM ca_project_t");
		sqlBuilder.appendln("WHERE PROJ_ACTIVE_CD <> 'N'");
		sqlBuilder.appendln("AND PROJECT_CD = ?");

		String projectCode = queryForString(sqlBuilder.toString(), args);
		return projectCode;
	}

	protected String queryForString(String sql, List<Object> args) {

		List<String> resultList = null;

		try {
			resultList = getJdbcTemplate().query(sql, args.toArray(), new RowMapper<String>() {

				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString(1);
				}
			});
		} catch (Exception e) {
			LOG.error("Exception", e);
			throw new InternalServerErrorException();
		}

		if (resultList == null || resultList.isEmpty()) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

}
