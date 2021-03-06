package org.books.application.service;

import java.util.List;
import javax.naming.InitialContext;
import junit.framework.Assert;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.application.exception.InvalidCredentialsException;
import org.books.application.exception.OrderNotFoundException;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.enums.Type;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author awy
 */
public class CustomerServiceIT extends DBUnitInitializer {

    private static final String SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CustomerService!org.books.application.service.CustomerServiceRemote";
    private CustomerService service;

    private static final String EMAIL = "homer@simpson.com";
    private static final String PASS = "simpson";
    private static final Long ID = 1l;

    @BeforeTest
    @Override
    public void setUpClass() throws Exception {
	super.setUpClass();
	service = (CustomerService) new InitialContext().lookup(SERVICE_NAME);
    }

    @AfterClass
    public void tearDownClass() throws OrderNotFoundException {
    }

    @Test
    public void authenticateCustomer() throws InvalidCredentialsException {
	// WHEN
	service.authenticateCustomer(EMAIL, PASS);
    }

    @Test(expectedExceptions = InvalidCredentialsException.class)
    public void authenticateCustomer_wrongPass() throws InvalidCredentialsException {
	// WHEN
	service.authenticateCustomer(EMAIL, "wrongPass");
    }

    @Test(expectedExceptions = InvalidCredentialsException.class)
    public void changePassword() throws CustomerNotFoundException, InvalidCredentialsException {
	// WHEN
	service.changePassword(EMAIL, "newPass");

	// THEN
	try {
	    // Should fail
	    service.authenticateCustomer(EMAIL, PASS);
	} finally {
	    // Set back old password
	    service.changePassword(EMAIL, PASS);
	}
    }

    @Test(expectedExceptions = CustomerNotFoundException.class)
    public void changePassword_wrongPass() throws CustomerNotFoundException {
	// WHEN
	service.changePassword("wrongMail", "newPass");
    }

    @Test
    public void findCustomerById() throws CustomerNotFoundException {
	// WHEN
	Customer result = service.findCustomer(ID);

	// THEN
	Assert.assertNotNull(result);
    }

    @Test(expectedExceptions = CustomerNotFoundException.class)
    public void findCustomerById_wrongCustomerId() throws CustomerNotFoundException {
	// WHEN
	service.findCustomer(3333l);
    }

    @Test
    public void findCustomerByEmail() throws CustomerNotFoundException {
	// WHEN
	Customer result = service.findCustomer(EMAIL);

	// THEN
	Assert.assertNotNull(result);
    }

    @Test(expectedExceptions = CustomerNotFoundException.class)
    public void findCustomerByEmail_wrongEmail() throws CustomerNotFoundException {
	// WHEN
	service.findCustomer("wrongEmail");
    }

    @Test
    public void registerCustomer() throws EmailAlreadyUsedException, CustomerNotFoundException {
	// GIVEN
	Address address = new Address("street", "city", "postalCode", "country");
	CreditCard cc = new CreditCard(Type.Visa, "77757484848", 4, 2018);
	Customer newCustomer = new Customer("firstname", "lastname", "email@bfh.ch", address, cc);

	// WHEN
	Long result = service.registerCustomer(newCustomer, "newPass");

	// THEN
	Customer findCustomer = service.findCustomer(result);
	Assert.assertNotNull(findCustomer);
    }

    @Test(expectedExceptions = EmailAlreadyUsedException.class)
    public void registerCustomer_emailAlreadyUsed() throws EmailAlreadyUsedException {
	// GIVEN
	Address address = new Address("street", "city", "postalCode", "country");
	CreditCard cc = new CreditCard(Type.Visa, "77757484848", 4, 2018);
	Customer newCustomer = new Customer("firstname", "lastname", EMAIL, address, cc);

	// WHEN
	service.registerCustomer(newCustomer, "newPass");
    }

    @Test
    public void searchCustomers() {
	// WHEN
	List<CustomerInfo> result = service.searchCustomers("simpson");

	// THEN
	Assert.assertNotNull(result);
	Assert.assertEquals(2, result.size());
    }

    @Test
    public void searchCustomers_notExisting() {
	// WHEN
	List<CustomerInfo> result = service.searchCustomers("notexisting");

	// THEN
	Assert.assertNotNull(result);
	Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void updateCustomer() throws CustomerNotFoundException, EmailAlreadyUsedException {
	// GIVEN
	Customer customer = service.findCustomer(ID);
	final String oldFirstname = customer.getFirstName();

	// WHEN
//	customer.setId(0L);
	customer.setFirstName("newFirstname");
	service.updateCustomer(customer);

	// THEN
	Customer updated = service.findCustomer(ID);
	Assert.assertEquals("newFirstname", updated.getFirstName());

	// Revert
	customer.setFirstName(oldFirstname);
	service.updateCustomer(customer);
    }

    @Test(expectedExceptions = EmailAlreadyUsedException.class)
    public void updateCustomer_putSameEmailThanExistingCustomer() throws CustomerNotFoundException, EmailAlreadyUsedException {
	// GIVEN
	Customer customer = service.findCustomer(ID);

	// WHEN
	customer.setEmail("john@miller.com");
	service.updateCustomer(customer);
    }

    @Test
    public void updateCustomer_updatesLogin() throws CustomerNotFoundException, EmailAlreadyUsedException, InvalidCredentialsException {
	// GIVEN
	Customer customer = service.findCustomer(ID);
	final String oldEmail = customer.getEmail();

	// WHEN
	customer.setEmail("email@new.com");
	service.updateCustomer(customer);

	// THEN
	service.authenticateCustomer("email@new.com", PASS);

	// Revert
	customer.setEmail(oldEmail);
	service.updateCustomer(customer);
    }
}
