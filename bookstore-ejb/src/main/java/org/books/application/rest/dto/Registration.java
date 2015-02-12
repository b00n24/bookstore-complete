package org.books.application.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.books.persistence.entity.Customer;

/**
 *
 * @author AWy
 */
@XmlRootElement(name = "registration")
@XmlType(propOrder = {"customer", "password"})
public class Registration {

    private Customer customer;
    private String password;

    public Registration() {

    }

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

    public void setCustomer(Customer customer) {
	this.customer = customer;
    }

    public void setPassword(String password) {
	this.password = password;
    }

}
