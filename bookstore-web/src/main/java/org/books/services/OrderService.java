package org.books.services;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.books.application.Bookstore;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.Customer;
import org.books.persistence.LineItem;
import org.books.persistence.Order;

/**
 *
 * @author Silvan
 */
@ApplicationScoped
public class OrderService {

    @Inject
    private Bookstore bookstore;

    public Order findOrder(Integer id) throws OrderNotFoundException {
	return bookstore.findOrder(id);
    }

    public Order findOrder(String number) throws OrderNotFoundException {
	return bookstore.findOrder(number);
    }

    public List<Order> searchOrders(Customer customer, Integer year) {
	return bookstore.searchOrders(customer, year);
    }

    public Order placeOrder(Customer customer, List<LineItem> items) throws PaymentFailedException {
	return bookstore.placeOrder(customer, items);
    }

    public Order cancelOrder(Integer id) throws OrderNotFoundException, InvalidOrderStatusException {
	return bookstore.cancelOrder(id);
    }
}
