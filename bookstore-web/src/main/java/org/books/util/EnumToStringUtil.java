package org.books.util;

import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 *
 * @author Silvan
 */
public class EnumToStringUtil {

    public static String getText(Enum e) {
	FacesContext context = FacesContext.getCurrentInstance();
	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "texts");
	return bundle.getString(e.getClass().getSimpleName().toLowerCase() + e.name().toUpperCase());
    }
}
