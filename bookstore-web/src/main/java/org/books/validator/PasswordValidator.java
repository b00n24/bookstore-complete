package org.books.validator;

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
@FacesValidator("org.books.validator.PasswordValidator")
public class PasswordValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "org.books.validator.PasswordValidator";
    public static final String VALIDATOR_PASSWORDS_NOT_EQUAL = "org.books.validator.PasswordValidator.VALIDATOR_PASSWORDS_NOT_EQUAL";

    private String passwordFieldId;
    private boolean transientValue;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
	if (!(value instanceof String)) {
	    throwError(VALIDATOR_PASSWORDS_NOT_EQUAL);
	}
	String password2 = String.valueOf(value);

	UIComponent uIComponent = component.findComponent(passwordFieldId);
	if (uIComponent == null) {
	    throw new IllegalArgumentException("No component found for id: " + passwordFieldId);
	}
	String password1 = "";
	UIInput input = ((UIInput) uIComponent);
	if (input.getSubmittedValue() instanceof String) {
	    password1 = String.valueOf(input.getSubmittedValue());
	} else if (input.getValue()instanceof String) {
	    password1 = String.valueOf(input.getValue());
	}
	if (!password1.equals(password2)) {
	    throwError(VALIDATOR_PASSWORDS_NOT_EQUAL);
	}
    }

    @Override
    public Object saveState(FacesContext context) {
	return passwordFieldId;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	this.passwordFieldId = (String) state;
    }

    @Override
    public boolean isTransient() {
	return transientValue;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
	this.transientValue = newTransientValue;
    }

    public void setPasswordFieldId(String passwordFieldId) {
	this.passwordFieldId = passwordFieldId;
    }

    private void throwError(String messageId) throws ConverterException {
	FacesMessage facesMessage = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, messageId);
	throw new ValidatorException(facesMessage);
    }
}
