package org.books.application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.application.exception.InvalidCredentialsException;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.Book;
import org.books.persistence.CreditCard;
import org.books.persistence.Customer;
import org.books.persistence.LineItem;
import org.books.persistence.Order;

@ApplicationScoped
public class Bookstore {

	private static final String CATALOG_DATA = "/data/catalog.xml";
	private static final String CUSTOMERS_DATA = "/data/customers.xml";
	private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("\\d{16}");
	private static final long PAYMENT_AMOUNT_LIMIT = 1000;
	private static final long ORDER_PROCESS_TIME = 600000;
	private static final Logger logger = Logger.getLogger(Bookstore.class.getName());

	private final Map<Integer, Book> books = new HashMap<>();
	private final Map<Integer, Customer> customers = new HashMap<>();
	private final Map<Integer, Order> orders = new HashMap<>();
	private int lastId = 0;

	@PostConstruct
	public void init() {
		for (Book book : XmlParser.parse(CATALOG_DATA, Book.class)) {
			book.setId(++lastId);
			books.put(book.getId(), book);
		}
		for (Customer customer : XmlParser.parse(CUSTOMERS_DATA, Customer.class)) {
			customer.setId(++lastId);
			customers.put(customer.getId(), customer);
		}
	}

	public Book findBook(Integer id) throws BookNotFoundException {
		logger.log(Level.INFO, "Finding book with id ''{0}''", id);
		Book book = books.get(id);
		if (book == null) {
			throw new BookNotFoundException();
		}
		return clone(book);
	}

	public Book findBook(String isbn) throws BookNotFoundException {
		logger.log(Level.INFO, "Finding book with isbn ''{0}''", isbn);
		for (Book book : books.values()) {
			if (book.getIsbn().equals(isbn)) {
				return clone(book);
			}
		}
		throw new BookNotFoundException();
	}

	public List<Book> searchBooks(String keywords) {
		logger.log(Level.INFO, "Searching books with keywords ''{0}''", keywords);
		keywords = keywords.toLowerCase();
		List<Book> results = new ArrayList<>();
		loop:
		for (Book book : books.values()) {
			for (String keyword : keywords.split("\\s+")) {
				if (!book.getTitle().toLowerCase().contains(keyword)
						&& !book.getAuthors().toLowerCase().contains(keyword)
						&& !book.getPublisher().toLowerCase().contains(keyword)) {
					continue loop;
				}
			}
			results.add(clone(book));
		}
		return results;
	}

	public Customer findCustomer(Integer id) throws CustomerNotFoundException {
		logger.log(Level.INFO, "Finding customer with id ''{0}''", id);
		Customer customer = customers.get(id);
		if (customer == null) {
			throw new CustomerNotFoundException();
		}
		return clone(customer);
	}

	public Customer findCustomer(String email) throws CustomerNotFoundException {
		logger.log(Level.INFO, "Finding customer with email ''{0}''", email);
		for (Customer customer : customers.values()) {
			if (customer.getEmail().equals(email)) {
				return clone(customer);
			}
		}
		throw new CustomerNotFoundException();
	}

	public List<Customer> searchCustomers(String name) {
		logger.log(Level.INFO, "Searching customers with name ''{0}''", name);
		name = name.toLowerCase();
		List<Customer> results = new ArrayList<>();
		for (Customer customer : customers.values()) {
			if (customer.getFirstName().toLowerCase().contains(name)
					|| customer.getLastName().toLowerCase().contains(name)) {
				results.add(clone(customer));
			}
		}
		return results;
	}

	public Customer registerCustomer(Customer customer) throws EmailAlreadyUsedException {
		logger.log(Level.INFO, "Registering customer with email ''{0}''", customer.getEmail());
		try {
			findCustomer(customer.getEmail());
			throw new EmailAlreadyUsedException();
		} catch (CustomerNotFoundException ex) {
			customer.setId(++lastId);
			customers.put(customer.getId(), clone(customer));
			return customer;
		}
	}

	public Customer authenticateCustomer(String email, String password) throws InvalidCredentialsException {
		logger.log(Level.INFO, "Authenticating customer with email ''{0}''", email);
		try {
			Customer customer = findCustomer(email);
			if (!customer.getPassword().equals(password)) {
				throw new InvalidCredentialsException();
			}
			return clone(customer);
		} catch (CustomerNotFoundException ex) {
			throw new InvalidCredentialsException();
		}
	}

	public Customer updateCustomer(Customer customer) throws EmailAlreadyUsedException {
		logger.log(Level.INFO, "Updating customer with id ''{0}''", customer.getId());
		try {
			Customer other = findCustomer(customer.getEmail());
			if (!other.equals(customer)) {
				throw new EmailAlreadyUsedException();
			}
		} catch (CustomerNotFoundException ex) {
		}
		Customer existing = customers.get(customer.getId());
		existing.setFirstName(customer.getFirstName());
		existing.setLastName(customer.getLastName());
		existing.setEmail(customer.getEmail());
		existing.setPassword(customer.getPassword());
		existing.setAddress(clone(customer.getAddress()));
		existing.setCreditCard(clone(customer.getCreditCard()));
		return clone(existing);
	}

	public Order findOrder(Integer id) throws OrderNotFoundException {
		logger.log(Level.INFO, "Finding order with id ''{0}''", id);
		Order order = orders.get(id);
		if (order == null) {
			throw new OrderNotFoundException();
		}
		return clone(order);
	}

	public Order findOrder(String number) throws OrderNotFoundException {
		logger.log(Level.INFO, "Finding order with number ''{0}''", number);
		for (Order order : orders.values()) {
			if (order.getNumber().equals(number)) {
				return clone(order);
			}
		}
		throw new OrderNotFoundException();
	}

	public List<Order> searchOrders(Customer customer, Integer year) {
		logger.log(Level.INFO, "Searching orders of customer with id ''{0}''", customer.getId());
		List<Order> results = new ArrayList<>();
		for (Order order : orders.values()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(order.getDate());
			if (order.getCustomer().equals(customer) && calendar.get(Calendar.YEAR) == year) {
				results.add(clone(order));
			}
		}
		return results;
	}

	public Order placeOrder(Customer customer, List<LineItem> items) throws PaymentFailedException {
		logger.log(Level.INFO, "Placing order for customer with id ''{0}''", customer.getId());

		// make payment
		BigDecimal amount = BigDecimal.ZERO;
		for (LineItem item : items) {
			BigDecimal itemAmount = item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity()));
			amount = amount.add(itemAmount);
		}
		makePayment(customer, amount);

		// create order
		Order order = new Order();
		order.setId(++lastId);
		Calendar calendar = Calendar.getInstance();
		order.setNumber(calendar.get(Calendar.YEAR) + "-" + String.format("%05d", order.getId()));
		order.setDate(calendar.getTime());
		order.setStatus(Order.Status.accepted);
		order.setAmount(amount);
		customer = customers.get(customer.getId());
		order.setCustomer(clone(customer));
		order.setAddress(clone(customer.getAddress()));
		order.setCreditCard(clone(customer.getCreditCard()));
		for (LineItem item : items) {
			item.setBook(books.get(item.getBook().getId()));
		}
		order.setItems(clone(items));
		orders.put(order.getId(), order);

		processOrder(order);
		return clone(order);
	}

	public Order cancelOrder(Integer id) throws OrderNotFoundException, InvalidOrderStatusException {
		logger.log(Level.INFO, "Canceling order with id ''{0}''", id);
		Order order = orders.get(id);
		if (order == null) {
			throw new OrderNotFoundException();
		}
		if (order.getStatus() == Order.Status.delivered) {
			throw new InvalidOrderStatusException();
		}
		order.setStatus(Order.Status.canceled);
		return clone(order);
	}

	private void makePayment(Customer customer, BigDecimal amount) throws PaymentFailedException {
		logger.log(Level.INFO, "Making payment for customer with id ''{0}''", customer.getId());
		CreditCard card = customer.getCreditCard();
		Matcher matcher = CREDIT_CARD_PATTERN.matcher(card.getNumber());
		if (!matcher.matches()) {
			throw new PaymentFailedException();
		}
		checkLuhnDigit(card.getNumber());
		Calendar calendar = Calendar.getInstance();
		if (card.getExpirationYear() < calendar.get(Calendar.YEAR)
				|| (card.getExpirationYear() == calendar.get(Calendar.YEAR)
				&& card.getExpirationMonth() < calendar.get(Calendar.MONTH) + 1)) {
			throw new PaymentFailedException();
		}
		if (amount.compareTo(BigDecimal.valueOf(PAYMENT_AMOUNT_LIMIT)) > 0) {
			throw new PaymentFailedException();
		}
	}

	private void checkLuhnDigit(String number) throws PaymentFailedException {
		int sum = 0;
		for (int i = 0; i < number.length(); i++) {
			int d = Character.digit(number.charAt(i), 10);
			if (i % 2 == number.length() % 2) {
				d += d < 5 ? d : (d - 9);
			}
			sum += d;
		}
		if (sum % 10 != 0) {
			throw new PaymentFailedException();
		}
	}

	private void processOrder(final Order order) {
		logger.log(Level.INFO, "Processing order with id ''{0}''", order.getId());
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (order.getStatus() == Order.Status.processing) {
					logger.log(Level.INFO, "Delivering order with id ''{0}''", order.getId());
					order.setStatus(Order.Status.delivered);
				}
			}
		};
		new Timer().schedule(task, ORDER_PROCESS_TIME);
		order.setStatus(Order.Status.processing);
	}

	@SuppressWarnings("unchecked")
	private <T> T clone(T object) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			new ObjectOutputStream(os).writeObject(object);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			return (T) new ObjectInputStream(is).readObject();
		} catch (IOException | ClassNotFoundException ex) {
			return object;
		}
	}
}
