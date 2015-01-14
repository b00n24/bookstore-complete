package org.books.application.service;

import java.util.List;
import javax.ejb.Remote;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Order;

/**
 *
 * @author AWy
 */
@Remote
public interface OrderService {

    /**
     * Cancels an order.
     *
     * @param orderId the order identifier
     * @throws OrderNotFoundException if no order with the specified identifier
     * exists
     * @throws InvalidOrderStatusException if the order is not cancelable
     * anymore
     */
    void cancelOrder(Long orderId) throws OrderNotFoundException, InvalidOrderStatusException;

    /**
     * Finds an order with the specified identifier.
     *
     * @param orderId the order identifier
     * @return the found order
     * @throws OrderNotFoundException if no order with the specified identifier
     * exists
     */
    Order findOrder(Long orderId) throws OrderNotFoundException;

    /**
     * Finds an order with the specified number.
     *
     * @param number the order number
     * @return the found order
     * @throws OrderNotFoundException if no order with the specified identifier
     * exists
     */
    Order findOrder(String number) throws OrderNotFoundException;

    /**
     * Places an order on the bookstore.
     *
     * @param customerId the customer identifier
     * @param items the order items
     * @return information on the created order
     * @throws CustomerNotFoundException if no customer with the specified
     * identifier exists
     * @throws BookNotFoundException if an order item reference a book that does
     * not exist
     * @throws PaymentFailedException if an error occurs during the credit card
     * payment
     */
    OrderInfo placeOrder(Long customerId, List<OrderItem> items) throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException;

    /**
     * Searches for orders of a particular customer and year.
     *
     * @param customerId the customer identifier
     * @param year the order year
     * @return information on matching orders (may be empty)
     * @throws CustomerNotFoundException if no customer with the specified
     * identifier exists
     */
    List<OrderInfo> searchOrders(Long customerId, Integer year) throws CustomerNotFoundException;
}
