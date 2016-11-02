package edu.uci.oit.kfs.dwh.webservices.dataaccess.impl;

import javax.sql.DataSource;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.uci.oit.kfs.dwh.webservices.dataaccess.DataAccessObject;

public abstract class DataAccessObjectBase implements DataAccessObject {

	Logger LOG = Logger.getLogger(DataAccessObjectBase.class);

	private JdbcTemplate jdbcTemplate;

	@Override
	@Cacheable("currentFiscalYear")
	public String findCurrentFiscalYear() {

		try {
			StrBuilder sql = new StrBuilder();
			sql.appendln("SELECT UNIV_FISCAL_YR");
			sql.appendln("FROM SH_UNIV_DATE_T");
			sql.appendln("WHERE UNIV_DT = CAST(getdate() As Date)");

			String result = getJdbcTemplate().queryForObject(sql.toString(), String.class);
			return result;
		} catch (Exception e) {
			LOG.error("Exception", e);
		}

		return null;
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
