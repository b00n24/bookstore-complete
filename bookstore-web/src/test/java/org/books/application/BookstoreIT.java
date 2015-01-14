package org.books.application;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.application.exception.InvalidCredentialsException;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.Address;
import org.books.persistence.Book;
import org.books.persistence.CreditCard;
import org.books.persistence.Customer;
import org.books.persistence.LineItem;
import org.books.persistence.Order;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class BookstoreIT {

	private static Bookstore bookstore;
	private List<Book> books;
	private Customer customer;
	private Order order;

	@BeforeClass
	public void setup() {
		bookstore = new Bookstore();
		bookstore.init();
	}

	@Test
	public void findBook() throws BookNotFoundException {
		Book book = bookstore.findBook("0596006209");
		assertNotNull(book);
	}

	@Test
	public void searchBooks() {
		books = bookstore.searchBooks("java language");
		assertFalse(books.isEmpty());
	}

	@Test
	public void registerCustomer() throws EmailAlreadyUsedException {
		Address address = new Address("123 Maple Street", "Mill Valley", "CA 90952", "US");
		CreditCard creditCard = new CreditCard(CreditCard.Type.MasterCard, "5400000000000005", 1, 2015);
		customer = new Customer("Robert", "Smith", "robert@example.org", "robert", address, creditCard);
		customer = bookstore.registerCustomer(customer);
		assertNotNull(customer.getId());
	}

	@Test(dependsOnMethods = "registerCustomer")
	public void authenticeCustomer() throws InvalidCredentialsException {
		customer = bookstore.authenticateCustomer(customer.getEmail(), customer.getPassword());
		assertNotNull(customer);
	}

	@Test(dependsOnMethods = "registerCustomer")
	public void findCustomer() throws CustomerNotFoundException {
		customer = bookstore.findCustomer(customer.getEmail());
		assertNotNull(customer);
	}

	@Test(dependsOnMethods = "registerCustomer")
	public void searchCustomers() {
		List<Customer> customers = bookstore.searchCustomers(customer.getLastName());
		assertFalse(customers.isEmpty());
	}

	@Test(dependsOnMethods = "registerCustomer")
	public void updateCustomer() throws EmailAlreadyUsedException {
		Address address = new Address("8 Oak Avenue", "Old Town", "PA 95819", "US");
		customer.setAddress(address);
		customer = bookstore.updateCustomer(customer);
		assertEquals(customer.getAddress(), address);
	}

	@Test(dependsOnMethods = {"searchBooks", "registerCustomer"})
	public void placeOrder() throws PaymentFailedException {
		List<LineItem> items = new ArrayList<>();
		for (Book book : books) {
			items.add(new LineItem(book, 1));
		}
		order = bookstore.placeOrder(customer, items);
		assertNotNull(order.getId());
	}

	@Test(dependsOnMethods = "placeOrder")
	public void findOrder() throws OrderNotFoundException {
		order = bookstore.findOrder(order.getNumber());
		assertNotNull(order);
	}

	@Test(dependsOnMethods = "placeOrder")
	public void searchOrders() {
		List<Order> orders = bookstore.searchOrders(customer, Calendar.getInstance().get(Calendar.YEAR));
		assertFalse(orders.isEmpty());
	}

	@Test(dependsOnMethods = "placeOrder")
	public void cancelOrder() throws OrderNotFoundException, InvalidOrderStatusException {
		bookstore.cancelOrder(order.getId());
		order = bookstore.findOrder(order.getId());
		assertEquals(order.getStatus(), Order.Status.canceled);
	}
}
