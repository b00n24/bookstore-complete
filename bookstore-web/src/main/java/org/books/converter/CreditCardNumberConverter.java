package org.books.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import javax.faces.convert.FacesConverter;


/**
 *
 * @author AWy
 */
@FacesConverter("org.books.converter.CreditCardNumberConverter")
public class CreditCardNumberConverter implements Converter {

    public static final String CONVERTER_ID = "org.books.converter.CreditCardNumberConverter";
    private static final String EMPTY_SPACE = " ";

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
	return value != null ? value.replace(EMPTY_SPACE, "") : null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
	if (value == null) {
	    return null;
	}
	String val = String.valueOf(value);
	val = val.trim();

	final StringBuffer sb = new StringBuffer(val);
	sb.insert(12, EMPTY_SPACE);
	sb.insert(8, EMPTY_SPACE);
	sb.insert(4, EMPTY_SPACE);

	return sb.toString();
    }

}
