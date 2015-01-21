package org.books.application.service;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.application.exception.InvalidCredentialsException;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Login;
import org.books.persistence.service.CustomerRepository;

@Stateless(name = "CustomerService")
public class CustomerServiceBean implements CustomerServiceLocal, CustomerServiceRemote {

    @PersistenceContext(unitName = "bookstore", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;
    private CustomerRepository customerRepository;

    @PostConstruct
    public void initialize() {
	customerRepository = new CustomerRepository(em);
    }

    @Override
    public void authenticateCustomer(String email, String password) throws InvalidCredentialsException {
	try {
	    Login login = customerRepository.findLoginByUserName(email);
	    if (!login.getPassword().equals(password)) {
		throw new InvalidCredentialsException();
	    }
	} catch (Exception ex) {
	    // Bewusst keine null-Checks, da ich bei einem Fehler sowieso hier lande und dem Benutzer keine genaueren Infos gebe.
	    throw new InvalidCredentialsException();
	}
    }

    @Override
    public void changePassword(String email, String password) throws CustomerNotFoundException {
	Customer customer = customerRepository.findByMail(email);
	checkCustomerFound(customer);
	Login login = customerRepository.findLoginByUserName(email);
	login.setPassword(password);
	customerRepository.update(login);
    }

    @Override
    public Customer findCustomer(Long customerId) throws CustomerNotFoundException {
	Customer customer = customerRepository.findById(customerId);
	return checkCustomerFound(customer);
    }

    @Override
    public Customer findCustomer(String email) throws CustomerNotFoundException {
	Customer customer = customerRepository.findByMail(email);
	return checkCustomerFound(customer);
    }

    @Override
    public Long registerCustomer(Customer customer, String password) throws EmailAlreadyUsedException {
	Customer alreadyRegisteredCustomer = customerRepository.findByMail(customer.getEmail());
	if (alreadyRegisteredCustomer != null) {
	    throw new EmailAlreadyUsedException();
	}
	Login login = new Login();
	login.setUserName(customer.getEmail());
	login.setPassword(password);
	customerRepository.persist(login);
	customerRepository.persist(customer);
	customerRepository.flush();
	return customer.getId();

    }

    @Override
    public List<CustomerInfo> searchCustomers(String name) {
	return customerRepository.searchCustomersByName(name);
    }

    @Override
    public void updateCustomer(Customer customer) throws CustomerNotFoundException, EmailAlreadyUsedException {
	Customer foundCustomer = customerRepository.findByMail(customer.getEmail());
	checkCustomerFound(foundCustomer);
	if (!foundCustomer.getId().equals(customer.getId())) {
	    throw new EmailAlreadyUsedException();
	}
	customerRepository.update(customer);
    }

    private Customer checkCustomerFound(Customer customer) throws CustomerNotFoundException {
	if (customer == null) {
	    throw new CustomerNotFoundException();
	}
	return customer;
    }

}
