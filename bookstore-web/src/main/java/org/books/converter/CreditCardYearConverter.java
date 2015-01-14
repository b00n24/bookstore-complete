package org.books.converter;

import java.util.Calendar;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import javax.faces.convert.FacesConverter;

/**
 *
 * @author AWy
 */
@FacesConverter("org.books.converter.CreditCardYearConverter")
public class CreditCardYearConverter implements Converter {

    public static final String CONVERTER_ID = "org.books.converter.CreditCardYearConverter";

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
	if (value != null && value.length() == 2) {
	    Calendar cal = Calendar.getInstance();
	    String yearPrefix = String.valueOf(cal.get(Calendar.YEAR)).substring(0, 2);
	    value = yearPrefix + value;
	}
	return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
	if (value == null) {
	    return null;
	}
	String val = String.valueOf(value);
	return val.substring(2);
    }

}
