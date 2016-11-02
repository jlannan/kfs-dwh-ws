package edu.uci.oit.kfs.dwh.webservices.objectedit.dataaccess;

import edu.uci.oit.kfs.dwh.webservices.dataaccess.DataAccessObject;
import edu.uci.oit.kfs.dwh.webservices.objectedit.objectedit.dto.ObjectCode;

public interface ObjectEditValidationDao extends DataAccessObject {

	public ObjectCode findObjectCode(String kfsFiscalYear, String kfsChartCode, String kfsObjectCode);

}