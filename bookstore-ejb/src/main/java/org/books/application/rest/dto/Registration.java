package org.books.application.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;
import org.books.persistence.entity.Customer;

/**
 *
 * @author AWy
 */
@XmlRootElement(name = "registration")
public class Registration {

    private final Customer customer;
    private final String password;

    public Registration(Customer customer, String password) {
	this.customer = customer;
	this.password = password;
    }

    public Customer getCustomer() {
	return customer;
    }

    public String getPassword() {
	return password;
    }

}
