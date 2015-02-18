package org.books.beans;

import java.io.Serializable;
import java.util.Locale;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.application.service.CustomerService;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Login;
import org.books.util.MessageFactory;

/**
 *
 * @author AWy
 */
@Named("customerBean")
@SessionScoped
public class CustomerBean implements Serializable {

    private static final String WARNING_USER_EXISTS = "org.books.userExistsAlready";
    private static final String WARNING_USER_NOT_FOUND = "org.books.customerNotFound";

    @Inject
    private CustomerService customerService;
    @Inject
    private LoginBean loginBean;
    @Inject
    private NavigationBean navigationBean;

    private Customer customer;
    private Login login;

    public CustomerBean() {
	this.customer = new Customer();
	this.login = new Login();
    }

    public Customer getCustomer() {
	return customer;
    }

    public void setCustomer(Customer customer) {
	this.customer = customer;
    }

    public Login getLogin() {
	return login;
    }

    public void setLogin(Login login) {
	this.login = login;
    }

    public String getCountry() {
	Locale currentLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
	return customer == null || customer.getAddress() == null || customer.getAddress().getCountry() == null ? currentLocale.getCountry() : customer.getAddress().getCountry();
    }

    public void setCountry(String country) {
	customer.getAddress().setCountry(country);
    }

    public String register() {
	try {
	    // TODO SIR kein plan ob das OK!
	    customerService.registerCustomer(customer, login.getPassword());
	    return login();
	} catch (EmailAlreadyUsedException ex) {
	    //klappt noch nicht
	    MessageFactory.error(WARNING_USER_EXISTS, customer.getEmail());
	    return null;
	}
    }

    public String updateCustomer() {
	try {
	    customerService.updateCustomer(loginBean.getCustomer());
	    return navigationBean.goBack();
	} catch (EmailAlreadyUsedException ex) {
	    MessageFactory.error(WARNING_USER_EXISTS, loginBean.getEmail());
	    return null;
	} catch (CustomerNotFoundException ex) {
	    MessageFactory.error(WARNING_USER_NOT_FOUND, loginBean.getEmail());
	    return null;
	}
    }

    /**
     * Login and redirect to next page
     *
     * @return
     */
    private String login() {
	loginBean.setEmail(customer.getEmail());
	loginBean.setPassword(login.getPassword());
	return loginBean.login();
    }
}
