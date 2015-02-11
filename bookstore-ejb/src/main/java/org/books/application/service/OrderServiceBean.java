package org.books.application.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.integration.amazon.AmazonCatalog;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.LineItem;
import org.books.persistence.entity.Order;
import org.books.persistence.enums.Status;
import org.books.persistence.service.BookRepository;
import org.books.persistence.service.CustomerRepository;
import org.books.persistence.service.OrderRepository;

/**
 *
 * @author AWy
 */
@Stateless(name = "OrderService")
public class OrderServiceBean implements OrderServiceLocal, OrderServiceRemote {

    @PersistenceContext(unitName = "bookstore", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private BookRepository bookRepository;
    @Inject
    @JMSConnectionFactory("jms/orderQueueFactory")
    private JMSContext jmsContext;
    @Resource(lookup = "jms/orderQueue")
    private Queue orderQueue;
    @Inject
    private MailBean mailBean;
    @Inject
    private CatalogService catalogService;

    @PostConstruct
    public void initialize() {
	orderRepository = new OrderRepository(em);
	customerRepository = new CustomerRepository(em);
	bookRepository = new BookRepository(em);
    }

    @Override
    public void cancelOrder(Long orderId) throws OrderNotFoundException, InvalidOrderStatusException {
	final Order order = findOrder(orderId);
	if (order.getStatus() != Status.accepted && order.getStatus() != Status.processing) {
	    throw new InvalidOrderStatusException();
	}
	order.setStatus(Status.canceled);

	orderRepository.update(order);
	mailBean.sendMailOrderChanged(order);
    }

    @Override
    public Order findOrder(Long orderId) throws OrderNotFoundException {
	final Order order = orderRepository.findById(orderId);
	if (order == null) {
	    throw new OrderNotFoundException();
	}
	return order;
    }

    @Override
    public Order findOrder(String number) throws OrderNotFoundException {
	final Order order = orderRepository.findByNumber(number);
	if (order == null) {
	    throw new OrderNotFoundException();
	}
	return order;
    }

    @Override
    public OrderInfo placeOrder(Long customerId, List<OrderItem> items) throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException {
	Customer customer = getCustomer(customerId);

	Order order = new Order();
	Address a = customer.getAddress();
	Address addressCopy = new Address(a.getStreet(), a.getCity(), a.getPostalCode(), a.getCountry());
	order.setAddress(addressCopy);

	CreditCard cc = customer.getCreditCard();
	CreditCard ccCopy = new CreditCard(cc.getType(), cc.getNumber(), cc.getExpirationMonth(), cc.getExpirationYear());
	order.setCreditCard(ccCopy);
	order.setCustomer(customer);
	order.setDate(new Date());
	order.setNumber(UUID.randomUUID().toString());
	order.setStatus(Status.accepted);

	BigDecimal amount = new BigDecimal(BigInteger.ZERO);
	for (OrderItem item : items) {
	    Book book = catalogService.findBook(item.getIsbn());

	    BigDecimal curAmount = book.getPrice().multiply(new BigDecimal(item.getQuantity()));
	    amount = amount.add(curAmount);
	    order.getItems().add(new LineItem(book, item.getQuantity()));
	}
	order.setAmount(amount);
	orderRepository.persist(order);
	mailBean.sendMailOrderChanged(order);

	// Put order in orderQueue
	// We put a MapMessage, because we want the flexibility to add more values in future
	MapMessage message = jmsContext.createMapMessage();
	try {
	    message.setLong("orderId", order.getId());
	    jmsContext.createProducer().send(orderQueue, message);
	} catch (JMSException ex) {
	    Logger.getLogger(OrderServiceBean.class.getName()).log(Level.SEVERE, "Could not put order in processing queue", ex);
	}
	// PaymentFailedException will be used when we work with paypal
	OrderInfo orderInfo = new OrderInfo(order.getId(), order.getNumber(), order.getDate(), order.getAmount(), order.getStatus());
	return orderInfo;
    }

    @Override
    public List<OrderInfo> searchOrders(Long customerId, Integer year) throws CustomerNotFoundException {
	Customer customer = getCustomer(customerId);
	return orderRepository.getOrders(customer, year);
    }

    private Customer getCustomer(Long customerId) throws CustomerNotFoundException {
	final Customer customer = customerRepository.findById(customerId);
	if (customer == null) {
	    throw new CustomerNotFoundException();
	}
	return customer;
    }

}
