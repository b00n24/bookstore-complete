package org.books.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.services.CustomerService;
import org.books.util.MessageFactory;

/**
 *
 * @author AWy
 */
@Named("passwordBean")
@SessionScoped
public class PasswordBean implements Serializable {

    private static final String OLD_WRONG = "org.books.passwordOldWrong";
    private static final String OLD_NEW_NOT_EQUAL = "org.books.passwordNewNotEqual";
    private static final String COULD_NOT_SAVE = "org.books.passwordCouldNotSave";

    private String oldPassword;
    private String newPassword;
    private String newPassword2;

    @Inject
    private LoginBean loginBean;
    @Inject
    private CustomerService customerService;
    @Inject
    private NavigationBean navigationBean;

    public String getOldPassword() {
	return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
	this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
	return newPassword;
    }

    public void setNewPassword(String newPassword) {
	this.newPassword = newPassword;
    }

    public String getNewPassword2() {
	return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
	this.newPassword2 = newPassword2;
    }

    public String change() {
	if (loginBean.getCustomer() == null || !oldPassword.equals(loginBean.getCustomer().getPassword())) {
	    MessageFactory.error(OLD_WRONG);
	    return null;
	}

	if (!newPassword.equals(newPassword2)) {
	    MessageFactory.error(OLD_NEW_NOT_EQUAL);
	    return null;
	}

	try {
	    // Set new password and save
	    loginBean.getCustomer().setPassword(newPassword);
	    customerService.updateCustomer(loginBean.getCustomer());
	} catch (EmailAlreadyUsedException ex) {
	    MessageFactory.error(COULD_NOT_SAVE);
	    return null;
	}
	return navigationBean.goBack();
    }
}
