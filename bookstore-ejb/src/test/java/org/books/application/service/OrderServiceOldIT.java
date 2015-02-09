package org.books.application.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.LineItem;
import org.books.persistence.entity.Login;
import org.books.persistence.entity.Order;
import org.books.persistence.enums.Binding;
import org.books.persistence.enums.Status;
import org.books.persistence.enums.Type;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author Silvan
 */
public class OrderServiceOldIT {

    private static final String ORDER_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/OrderService!org.books.application.service.OrderServiceRemote";

    private OrderService orderService;

    protected final String firstName = "Homer";
    protected final String lastName = "Simpson";
    protected final String email = "silvan.strueby@students.bfh.ch";
    protected final String cityName = "St. Ursen";
    protected final String isbn1 = "isbn1";
    protected final String title1 = "Title1";
    protected final String authors1 = "Authors1";
    protected final String publisher1 = "Publisher1";
    protected final double price1 = 55.65;
    protected final String isbn2 = "isbn2";
    protected final String title2 = "Title2";
    protected final String authors2 = "Authors2";
    protected final String publisher2 = "Publisher2";
    protected final double price2 = 44.23;
    protected final String orderNumber = "23aBb432";
    protected final Date orderDate = Calendar.getInstance().getTime();

    protected Customer customer;
    protected Customer customer2;
    protected Address address;
    protected Address address2;
    protected CreditCard creditCard;
    protected CreditCard creditCard2;
    protected Book book1;
    protected Book book2;
    protected Order order;
    protected LineItem lineItem1;

    protected Login login;
    protected Login login2;
    
    protected String createdOrderNmber;
    private EntityManager em;

    @BeforeTest
    public void setUpClass() {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookstore");
	em = emf.createEntityManager();
	
	// GIVEN
	em.getTransaction().begin();

	// Address
	address = new Address();
	address.setStreet("Street");
	address.setCity(cityName);
	address.setPostalCode("postalCode");
	address.setCountry("country");

	// Address 2
	address2 = new Address();
	address2.setStreet("Street2");
	address2.setCity(cityName);
	address2.setPostalCode("postalCode2");
	address2.setCountry("country2");

	// CreditCard
	creditCard = new CreditCard();
	creditCard.setType(Type.MasterCard);
	creditCard.setNumber("12345");
	creditCard.setExpirationMonth(6);
	creditCard.setExpirationYear(16);

	// CreditCard 2
	creditCard2 = new CreditCard();
	creditCard2.setType(Type.Visa);
	creditCard2.setNumber("123232332345");
	creditCard2.setExpirationMonth(7);
	creditCard2.setExpirationYear(21);

	// Customer
	customer = new Customer();
	customer.setFirstName(firstName);
	customer.setLastName(lastName);
	customer.setEmail(email);
	customer.setAddress(address);
	customer.setCreditCard(creditCard);
	em.persist(customer);

	// Customer 2
	customer2 = new Customer();
	customer2.setFirstName(firstName + "2");
	customer2.setLastName(lastName + "2");
	customer2.setEmail(email + "2");
	customer2.setAddress(address2);
	customer2.setCreditCard(creditCard2);
	em.persist(customer2);

	// Book 1
	book1 = new Book();
	book1.setIsbn(isbn1);
	book1.setTitle(title1);
	book1.setAuthors(authors1);
	book1.setPublisher(publisher1);
	book1.setPublicationYear(1999);
	book1.setBinding(Binding.Hardcover);
	book1.setNumberOfPages(55);
	book1.setPrice(new BigDecimal(price1, MathContext.DECIMAL32));
	em.persist(book1);

	// Book 2
	book2 = new Book();
	book2.setIsbn(isbn2);
	book2.setTitle(title2);
	book2.setAuthors(authors2);
	book2.setPublisher(publisher2);
	book2.setPublicationYear(2006);
	book2.setBinding(Binding.Paperback);
	book2.setNumberOfPages(99);
	book2.setPrice(new BigDecimal(price2, MathContext.DECIMAL32));
	em.persist(book2);

	// LineItem
	lineItem1 = new LineItem();
	lineItem1.setQuantity(1);
	lineItem1.setBook(book1);
	em.persist(lineItem1);

	// Order
	order = new Order();
	order.setNumber(orderNumber);
	order.setDate(orderDate);
	order.setAmount(new BigDecimal(55));
	order.setStatus(Status.accepted);
	order.getItems().add(lineItem1);
	order.setCustomer(customer);
	em.persist(order);

	// Login
	login = new Login();
	login.setUserName(customer.getEmail());
	login.setPassword("pass");
	em.persist(login);

	login2 = new Login();
	login2.setUserName(customer2.getEmail());
	login2.setPassword("pass");
	em.persist(login2);

	em.getTransaction().commit();

    }

//    @AfterClass
//    public void tearDownClass() throws OrderNotFoundException {
//	em.getTransaction().begin();
//
//	if(createdOrderNmber != null){
//	    Order order = orderService.findOrder(createdOrderNmber);
//	    order = em.merge(order);
//	    em.remove(order);
//	} 
//	
//	Order o1 = em.merge(order);
//	em.remove(o1);
//	Customer c1 = em.merge(customer);
//	em.remove(c1);
//	Customer c2 = em.merge(customer2);
//	em.remove(c2);
//	Book b1 = em.merge(book1);
//	em.remove(b1);
//	Book b2 = em.merge(book2);
//	em.remove(b2);
//	Login l1 = em.merge(login);
//	em.remove(l1);
//	Login l2 = em.merge(login2);
//	em.remove(l2);
//
//	em.getTransaction().commit();
//    }

    @Test
    public void placeOrder() throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException, NamingException {
	orderService = (OrderService) new InitialContext().lookup(ORDER_SERVICE_NAME);
	List<OrderItem> orderItems = new LinkedList();
	OrderItem orderItem = new OrderItem();

	orderItem.setIsbn(book1.getIsbn());
	orderItem.setQuantity(1);
	orderItems.add(orderItem);
	OrderInfo order = orderService.placeOrder(customer.getId(), orderItems);
	createdOrderNmber = order.getNumber();
    }
}
