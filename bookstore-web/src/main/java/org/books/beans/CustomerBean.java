package org.books.beans;

import java.io.Serializable;
import java.util.Locale;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.persistence.Customer;
import org.books.services.CustomerService;
import org.books.util.MessageFactory;

/**
 *
 * @author AWy
 */
@Named("customerBean")
@SessionScoped
public class CustomerBean implements Serializable {

    private static final String WARNING_USER_EXISTS = "org.books.userExistsAlready";

    @Inject
    private CustomerService customerService;
    @Inject
    private LoginBean loginBean;
    @Inject
    private NavigationBean navigationBean;

    private Customer customer;

    public CustomerBean() {
	customer = new Customer();
    }

    public Customer getCustomer() {
	return customer;
    }

    public void setCustomer(Customer customer) {
	this.customer = customer;
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
	    customerService.register(customer);
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
	}
    }

    /**
     * Login and redirect to next page
     *
     * @return
     */
    private String login() {
	loginBean.setEmail(customer.getEmail());
	loginBean.setPassword(customer.getPassword());
	return loginBean.login();
    }
}
