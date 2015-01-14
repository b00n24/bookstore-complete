package org.books.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import javax.faces.convert.ConverterException;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.books.persistence.CreditCard;

import org.books.util.MessageFactory;

/**
 *
 * @author AWy
 */
@FacesValidator("org.books.validator.CreditCardNumberValidator")
public class CreditCardNumberValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "org.books.validator.CreditCardNumberValidator";
    public static final String VALIDATOR_INVALID_NUMBER = "org.books.validator.CreditCardNumberValidator.INVALID_NUMBER";
    public static final String VALIDATOR_NON_EXISTING_NUMBER = "org.books.validator.CreditCardNumberValidator.NOT_EXISTING_NUMBER";
    public static final String VALIDATOR_NON_MATCHING_TYPE_AND_NUMBER = "org.books.validator.CreditCardNumberValidator.VALIDATOR_NON_MATCHING_TYPE_AND_NUMBER";

    private String cardTypeId;
    private boolean transientValue;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
	if (!(value instanceof String)) {
	    throwError(VALIDATOR_INVALID_NUMBER);
	}
	String val = String.valueOf(value);

	checkNumberFormat(val);
	checkLuhnDigit(val);
	checkIssuerId(component, val);
    }

    private void checkIssuerId(UIComponent component, String number) {
	UIComponent uIComponent = component.findComponent(cardTypeId);
	if (uIComponent == null) {
	    throw new IllegalArgumentException("No component found for id: " + cardTypeId);
	}
	CreditCard.Type cardType = (CreditCard.Type) ((UIInput) uIComponent).getValue();

	if ((CreditCard.Type.MasterCard.equals(cardType) && !number.substring(0, 2).matches("5[1-5]"))
		|| (CreditCard.Type.Visa.equals(cardType) && !number.startsWith("4"))) {
	    throwError(VALIDATOR_NON_MATCHING_TYPE_AND_NUMBER);
	}
    }

    @Override
    public Object saveState(FacesContext context) {
	return cardTypeId;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	this.cardTypeId = (String) state;
    }

    @Override
    public boolean isTransient() {
	return transientValue;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
	this.transientValue = newTransientValue;
    }

    public void setCardTypeId(String cardTypeId) {
	this.cardTypeId = cardTypeId;
    }

    private void checkNumberFormat(String number) {
	Pattern pattern = Pattern.compile("^\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}");
	Matcher matcher = pattern.matcher(number);
	if (!matcher.matches()) {
	    throwError(VALIDATOR_INVALID_NUMBER);
	}
    }

    private void checkLuhnDigit(String number) {
	int sum = 0;
	for (int i = 0; i < number.length(); i++) {
	    int d = Character.digit(number.charAt(i), 10);
	    if (i % 2 == number.length() % 2) {
		d += d < 5 ? d : (d - 9);
	    }
	    sum += d;
	}
	if (sum % 10 != 0) {
	    throwError(VALIDATOR_NON_EXISTING_NUMBER);
	}
    }

    private void throwError(String messageId) throws ConverterException {
	FacesMessage facesMessage = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, messageId);
	throw new ValidatorException(facesMessage);
    }
}
