package org.books.validator;

import java.util.Calendar;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import javax.faces.convert.ConverterException;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.books.util.MessageFactory;

/**
 *
 * @author AWy
 */
@FacesValidator("org.books.validator.CreditCardExpirationDateValidator")
public class CreditCardExpirationDateValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "org.books.validator.CreditCardExpirationDateValidator";
    public static final String VALIDATOR_INVALID_EXPIRATION_DATE = "org.books.validator.CreditCardExpirationDateValidator.VALIDATOR_INVALID_EXPIRATION_DATE";

    private String cardExpirationMonthId;
    private boolean transientValue;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
	if (!(value instanceof String)) {
	    throwError(VALIDATOR_INVALID_EXPIRATION_DATE);
	}
	String val = String.valueOf(value);
	int year = parseIntOrThrowValidationError(val);
	checkDateValidity(component, year);
    }

    private void checkDateValidity(UIComponent component, Integer year) {
	Calendar now = Calendar.getInstance();
	if (now.get(Calendar.YEAR) > year) {
	    throwError(VALIDATOR_INVALID_EXPIRATION_DATE);
	}

	if (now.get(Calendar.YEAR) == year) {
	    UIComponent uIComponent = component.findComponent(cardExpirationMonthId);
	    if (uIComponent == null) {
		throw new IllegalArgumentException("No component found for id: " + cardExpirationMonthId);
	    }
	    int month;
	    UIInput input = ((UIInput) uIComponent);
	    if (input.getValue() instanceof Integer) {
		month = (Integer) input.getValue();
	    } else {
		month = parseIntOrThrowValidationError(String.valueOf(input.getValue()));
	    }
	    if (now.get(Calendar.MONTH) + 1 >= month) {
		throwError(VALIDATOR_INVALID_EXPIRATION_DATE);
	    }
	}
    }

    private Integer parseIntOrThrowValidationError(String value) {
	Integer intValue = null;
	try {
	    intValue = Integer.parseInt(value);
	} catch (NumberFormatException e) {
	    throwError(VALIDATOR_INVALID_EXPIRATION_DATE);
	}
	return intValue;
    }

    @Override
    public Object saveState(FacesContext context) {
	return cardExpirationMonthId;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	this.cardExpirationMonthId = (String) state;
    }

    @Override
    public boolean isTransient() {
	return transientValue;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
	this.transientValue = newTransientValue;
    }

    public void setCardExpirationMonthId(String cardExpirationMonthId) {
	this.cardExpirationMonthId = cardExpirationMonthId;
    }

    private void throwError(String messageId) throws ConverterException {
	FacesMessage facesMessage = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, messageId);
	throw new ValidatorException(facesMessage);
    }
}
