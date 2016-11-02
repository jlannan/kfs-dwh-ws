package edu.uci.oit.kfs.dwh.webservices.objectedit.service;

import org.springframework.cache.annotation.Cacheable;

import edu.uci.oit.kfs.dwh.webservices.wsresult.ObjectCodeEditValidationResult;

public interface GlobalObjectEditValidationService {

	/**
	 * The "Deny" and "Allow" Rules are as follows
	 * <ul>
	 * <li>If the collection only has the deny rule(s), the object codes not
	 * listed in the collection are considered to be allowed.</li>
	 * <li>If the collection only has the allow rule(s), the object codes not
	 * listed in the collection are considered to be denied.</li>
	 * <li>If the collection has both allow and deny rules, the deny rules will
	 * be applied first then the allow rules are applied. (Note:deny always
	 * wins)</li>
	 * </ul>
	 * 
	 */
	@Cacheable("goeValidation")
	public ObjectCodeEditValidationResult getGlobalObjectCodeEditValidationResult(String kfsFiscalYear, String kfsChartCode, String kfsAccountNumber,
			String kfsObjectCode);

}