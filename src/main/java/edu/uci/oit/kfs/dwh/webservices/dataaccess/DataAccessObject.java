package edu.uci.oit.kfs.dwh.webservices.dataaccess;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public interface DataAccessObject {

	public abstract String findCurrentFiscalYear();

	public abstract JdbcTemplate getJdbcTemplate();

	public abstract void setDataSource(DataSource dataSource);

}