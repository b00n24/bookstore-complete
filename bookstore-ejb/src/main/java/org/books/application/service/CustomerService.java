package org.books.application.service;

import java.util.List;
import javax.ejb.Remote;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.application.exception.InvalidCredentialsException;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;

/**
 *
 * @author Silvan
 */
@Remote
public interface CustomerService {

    /**
     * Authenticates a customer.
     *
     * @param email the email address of the customer
     * @param password the password of the customer
     * @throws InvalidCredentialsException if the email address or the password
     * is invalid
     */
    void authenticateCustomer(String email, String password) throws InvalidCredentialsException;

    /**
     * Changes the password of a customer.
     *
     * @param email the email address of the customer
     * @param password the new password of the customer
     * @throws CustomerNotFoundException if no customer with the specified email
     * address exists
     */
    void changePassword(String email, String password) throws CustomerNotFoundException;

    /**
     * Finds a customer with a particular identifier.
     *
     * @param customerId the customer identifier
     * @return the data of the found customer
     * @throws CustomerNotFoundException if no customer with the specified email
     * address exists
     */
    Customer findCustomer(Long customerId) throws CustomerNotFoundException;

    /**
     * Finds a customer with a particular email address.
     *
     * @param email the email address
     * @return the data of the found customer
     * @throws CustomerNotFoundException if no customer with the specified email
     * address exists
     */
    Customer findCustomer(String email) throws CustomerNotFoundException;

    /**
     * Registers a new customer with the bookstore. The email address and
     * password are used to authenticate the customer.
     *
     * @param customer the data of the customer to be registered (the id must be
     * null)
     * @param password the password of the customer
     * @return the customer identifier
     * @throws EmailAlreadyUsedException if the email address is already used by
     * another customer
     */
    Long registerCustomer(Customer customer, String password) throws EmailAlreadyUsedException;

    /**
     * Searches for customers with a particular name. A customer is included in
     * the results list if the specified name is part of its first or last name.
     *
     * @param name the name to search for
     * @return information on the matching customers (the list may be empty)
     */
    List<CustomerInfo> searchCustomers(String name);

    /**
     * Updates the data of a customer. If the email address is to be changed,
     * the new email address is also used for authentication.
     *
     * @param customer the data of the customer to be updated (the id must not
     * be null)
     * @throws CustomerNotFoundException if no customer with the corresponding
     * identifier exists
     * @throws EmailAlreadyUsedException if the new email address is already
     * used by another customer
     */
    void updateCustomer(Customer customer) throws CustomerNotFoundException, EmailAlreadyUsedException;

}
