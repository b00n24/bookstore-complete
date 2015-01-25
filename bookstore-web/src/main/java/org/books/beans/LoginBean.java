package org.books.beans;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.InvalidCredentialsException;
import org.books.application.service.CustomerService;
import org.books.persistence.entity.Customer;
import org.books.util.MessageFactory;

/**
 *
 * @author Silvan
 */
@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    
    private static final String ERROR_INVALID_CREDENTIALS = "org.books.errorInvalidCredentials";
    private static final String WARNING_USER_NOT_FOUND = "org.books.customerNotFound";

    @Inject
    private NavigationBean navigation;
    
    @Inject
    private CustomerService customerService;
    
    private String email;
    private String password;
    
    private Customer customer;

    public boolean isLoggedIn() {
        return customer != null;
    }
    
    public Customer getCustomer(){
	return customer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String logout(){
	this.customer = null;
	// We don't destroy the session in order to be able to keep the language.
//	FacesContext.getCurrentInstance().getExternalContext()
//	    .invalidateSession();
	return navigation.goToCatalogSearch();
    }

    public String login() {
	try {
	    customerService.authenticateCustomer(email, password);
	    customer = customerService.findCustomer(email);
	} catch (InvalidCredentialsException ex) {
	    MessageFactory.error(ERROR_INVALID_CREDENTIALS);
	    return null;
	} catch (CustomerNotFoundException ex) {
	    MessageFactory.error(WARNING_USER_NOT_FOUND);
	    return null;
	}
        return navigation.goToNextPage();
    }
}
