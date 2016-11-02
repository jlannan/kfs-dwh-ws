package edu.uci.oit.kfs.dwh.webservices.translation.util;

import edu.uci.oit.kfs.dwh.webservices.translation.KfsToUcTranslationConstants;
import edu.uci.oit.kfs.dwh.webservices.translation.dto.ObjectCode;

public class KfsToUcTranslationUtil {

	public static String convertUcSubFormat(String sub) {

		if (sub != null) {
			sub = sub.replaceAll("[^0-9]", "");
			if (sub.length() > 1) { // report 00 as just 0
				sub = sub.substring(1);
			}
		}
		return sub;
	}

	public static boolean isFundBalanceObjectCodeType(ObjectCode objectCode) {
		return KfsToUcTranslationConstants.OBJECT_TYPE_CODE_FUND_BALANCE.equalsIgnoreCase(objectCode.getObjectTypeCode());
	}

	public static boolean isOFTIndicatorTransCode(ObjectCode objectCode) {
		return KfsToUcTranslationConstants.TRANSACTION_CODE_TRANSACTIONAL.equalsIgnoreCase(objectCode.getOftIndicator());
	}

}
