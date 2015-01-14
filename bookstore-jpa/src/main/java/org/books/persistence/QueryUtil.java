package org.books.persistence;

import org.apache.commons.lang3.Validate;

/**
 *
 * @author AWy
 */
public class QueryUtil {

    public static String convertToLikeValueAndLowerCase(String param) {
	Validate.notNull(param, "'param' darf nicht null sein");
	return "%" + param.toLowerCase() + "%";
    }
}
