package org.books.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.books.application.Bookstore;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.application.exception.InvalidCredentialsException;
import org.books.persistence.Customer;

/**
 *
 * @author AWy
 */
@ApplicationScoped
public class CustomerService {
    
    @Inject
    private Bookstore bookstore;
    
    public Customer register(final Customer customer) throws EmailAlreadyUsedException {
	return bookstore.registerCustomer(customer);
    }
    
    public Customer authenticate(String email, String password) throws InvalidCredentialsException {
	return bookstore.authenticateCustomer(email, password);
    }
    
    public Customer updateCustomer(Customer customer) throws EmailAlreadyUsedException{
	return bookstore.updateCustomer(customer);
    }
}
