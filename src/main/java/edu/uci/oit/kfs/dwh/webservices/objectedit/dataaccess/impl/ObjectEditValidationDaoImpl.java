package edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import edu.uci.oit.kfs.dwh.webservices.dataaccess.impl.DataAccessObjectBase;
import edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess.ObjectEditValidationDao;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.ObjectCode;

public class ObjectEditValidationDaoImpl extends DataAccessObjectBase implements ObjectEditValidationDao {

	private Logger LOG = Logger.getLogger(ObjectEditValidationDaoImpl.class);

	@Override
	@Cacheable("objectCodeEditObjectCode")
	public ObjectCode findObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsObjectCode) {

		if (StringUtils.isEmpty(kfsChartCode) || StringUtils.isEmpty(kfsObjectCode)) {
			return null;
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add(kfsChartCode);
		args.add(kfsObjectCode);

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT CAO.FIN_OBJECT_CD, CAO.FIN_OBJ_LEVEL_CD, CAOL.FIN_CONS_OBJ_CD");
			sql.appendln("FROM CA_OBJECT_CODE_T CAO");
			sql.appendln("JOIN CA_OBJ_LEVEL_T CAOL ON CAO.FIN_COA_CD = CAOL.FIN_COA_CD AND CAO.FIN_OBJ_LEVEL_CD = CAOL.FIN_OBJ_LEVEL_CD");
			sql.appendln("JOIN SH_UNIV_DATE_T UD ON CAO.UNIV_FISCAL_YR = UD.UNIV_FISCAL_YR");
			sql.appendln("WHERE CAO.UNIV_FISCAL_YR = UD.UNIV_FISCAL_YR");
			sql.appendln("AND CAO.FIN_COA_CD = ? AND CAO.FIN_OBJECT_CD = ?");

			sql.appendln("AND UD.UNIV_DT = CAST(getdate() As Date)");
			sql.appendln("AND FIN_OBJ_ACTIVE_CD = 'Y'");

			List<Map<String, Object>> resultSet = getJdbcTemplate().queryForList(sql.toString(), args.toArray());

			if (resultSet.iterator().hasNext()) {
				Map<String, Object> rowMap = (Map<String, Object>) resultSet.iterator().next();

				ObjectCode objectCode = new ObjectCode();
				objectCode.setFinancialObjectCode((String) rowMap.get("FIN_OBJECT_CD"));
				objectCode.setFinancialObjectLevelCode((String) rowMap.get("FIN_OBJ_LEVEL_CD"));
				objectCode.setFinancialConsolidationObjectCode((String) rowMap.get("FIN_CONS_OBJ_CD"));
				return objectCode;
			}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return null;
	}

}
