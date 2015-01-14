package org.books.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.persistence.LineItem;
import org.books.persistence.Order;
import org.books.services.OrderService;
import org.books.util.MessageFactory;

/**
 *
 * @author Silvan
 */
@Named("accountBean")
@SessionScoped
public class AccountBean implements Serializable {

    private static final String ORDER_CANCEL_ERROR = "org.books.orderCancelError";
    private static final String INFO_NO_ORDERS_FOUND = "org.books.infoNoOrderFound";

    @Inject
    private OrderService orderService;

    @Inject
    private LoginBean loginBean;

    @Inject
    private NavigationBean navigationBean;

    private Integer searchYear;
    private List<Order> searchResult = new LinkedList<>();
    private Order selectedOrder;

    public Order getSelectedOrder() {
	return selectedOrder;
    }

    public List<Order> getSearchResult() {
	return searchResult;
    }

    public Integer getSearchYear() {
	return searchYear;
    }

    public void setSearchYear(Integer searchYear) {
	this.searchYear = searchYear;
    }

    public void searchOrders() {
	reset();
	this.searchResult = orderService.searchOrders(loginBean.getCustomer(), searchYear);
	if (searchResult.isEmpty()) {
	    MessageFactory.info(INFO_NO_ORDERS_FOUND);
	}
    }

    public String selectOrder(Order order) {
	this.selectedOrder = order;
	return navigationBean.goToOrderDetail();
    }

    public void cancelOrder(Order order) {
	try {
	    Order cancelOrder = orderService.cancelOrder(order.getId());
	    int indexOf = searchResult.indexOf(order);
	    searchResult.remove(order);
	    searchResult.add(indexOf, cancelOrder);
	} catch (OrderNotFoundException | InvalidOrderStatusException ex) {
	    MessageFactory.error(ORDER_CANCEL_ERROR);
	}
    }

    public BigDecimal getSelectedTotal() {
	BigDecimal total = BigDecimal.ZERO;
	for (LineItem lineItem : selectedOrder.getItems()) {
	    if (lineItem.getQuantity() != null) {
		BigDecimal itemPrice = lineItem.getBook().getPrice().multiply(new BigDecimal(lineItem.getQuantity()));
		total = total.add(itemPrice);
	    }
	}
	return total;
    }

    private void reset() {
	this.selectedOrder = null;
	this.searchResult.clear();
    }
}
