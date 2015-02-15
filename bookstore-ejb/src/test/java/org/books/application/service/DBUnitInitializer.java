package org.books.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.books.application.rest.dto.OrderRequest;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.enums.Type;
import org.dbunit.IDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.testng.annotations.BeforeTest;

/**
 *
 * @author awy
 */
public class DBUnitInitializer {

    private static final String DB_UNIT_PROPERTIES = "/dbunit.properties";
    private static final String DB_UNIT_DATASET = "/dataset.xml";
    private EntityManager em;

    @BeforeTest
    public void setUpClass() throws Exception {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookstore");
	em = emf.createEntityManager();
	initDatabase();
    }

    protected void initDatabase() throws Exception {
	System.getProperties().load(getClass().getResourceAsStream(DB_UNIT_PROPERTIES));
	IDatabaseTester databaseTester = new PropertiesBasedJdbcDatabaseTester();
	IDataSet dataset = new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream(DB_UNIT_DATASET));
	databaseTester.setDataSet(dataset);
	databaseTester.onSetup();
    }

    protected Address createAddress() {
	Address address = new Address();
	address.setStreet("Street");
	address.setCity("cityName");
	address.setPostalCode("postalCode");
	address.setCountry("country");
	return address;
    }

    protected CreditCard createCreditCard() {
	CreditCard creditCard = new CreditCard();
	creditCard.setType(Type.MasterCard);
	creditCard.setNumber("1234567890123456");
	creditCard.setExpirationMonth(6);
	creditCard.setExpirationYear(2016);
	return creditCard;
    }

    protected Customer createCustomer(String email) {
	Customer customer = new Customer();
	customer.setAddress(createAddress());
	customer.setCreditCard(createCreditCard());
	customer.setEmail(email);
	customer.setFirstName("firstname");
	customer.setLastName("lastname");
	return customer;
    }

    protected OrderRequest createOrderRequest(Long customerId) {
	List<OrderItem> list = new ArrayList<>();
	list.add(createOrderItem("0596009208"));
	list.add(createOrderItem("0596007736"));
	OrderRequest orderRequest = new OrderRequest(customerId, list);
	return orderRequest;
    }

    protected OrderItem createOrderItem(String isbn) {
	OrderItem orderItem = new OrderItem();
	orderItem.setIsbn(isbn);
	orderItem.setQuantity(3);
	return orderItem;
    }
}
