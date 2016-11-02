package edu.uci.oit.kfs.dwh.webservices.translation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KfsToUcTranslationConstants {

	public static final String OBJECT_TYPE_CODE_INCOME_NOT_CASH = "IC";
	public static final String OBJECT_TYPE_CODE_FUND_BALANCE = "FB";
	public static final String BASIC_ACCOUNTING_CATEGORY_CODE_EX = "EX";
	public static final String TRANSACTION_CODE_TRANSACTIONAL = "T";

	public static final Set<String> BAL_SHEET_AND_REVENUE_BASIC_ACCTG_CATEGORY_CODES = new HashSet<String>(
			Arrays.asList(new String[] { "AS", "LI", "IN" }));

}
