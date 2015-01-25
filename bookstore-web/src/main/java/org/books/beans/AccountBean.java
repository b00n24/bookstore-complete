package org.books.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.service.OrderService;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.LineItem;
import org.books.persistence.entity.Order;
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
    // TODO SIR ist in mehrernen BEANS enthalten
    private static final String WARNING_USER_NOT_FOUND = "org.books.customerNotFound";

    @Inject
    private OrderService orderService;

    @Inject
    private LoginBean loginBean;

    @Inject
    private NavigationBean navigationBean;

    private Integer searchYear;
    private List<OrderInfo> searchResult = new LinkedList<>();
    private OrderInfo selectedOrder;

    public Order getSelectedOrder() throws OrderNotFoundException {
	Order order = orderService.findOrder(selectedOrder.getId());
	return order;
    }

    public List<OrderInfo> getSearchResult() {
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
	try {
	    this.searchResult = orderService.searchOrders(loginBean.getCustomer().getId(), searchYear);
	} catch (CustomerNotFoundException ex) {
	    MessageFactory.error(WARNING_USER_NOT_FOUND, loginBean.getEmail());
	    return;
	}
	if (searchResult.isEmpty()) {
	    MessageFactory.info(INFO_NO_ORDERS_FOUND);
	}
    }

    public String selectOrder(OrderInfo order) {
	this.selectedOrder = order;
	return navigationBean.goToOrderDetail();
    }

    public void cancelOrder(OrderInfo order) {
	try {
	    orderService.cancelOrder(order.getId());
	    int indexOf = searchResult.indexOf(order);
	    searchResult.remove(order);
	    searchResult.add(indexOf, order);
	} catch (OrderNotFoundException | InvalidOrderStatusException ex) {
	    MessageFactory.error(ORDER_CANCEL_ERROR);
	}
    }

    public BigDecimal getSelectedTotal() {
	BigDecimal total = BigDecimal.ZERO;
	Order order = null;
	try {
	    // TODO SIR nicht wirklich schöne / optimal
	    order = orderService.findOrder(selectedOrder.getId());
	} catch (OrderNotFoundException ex) {
	    MessageFactory.error(ORDER_CANCEL_ERROR);
	}
	for (LineItem lineItem : order.getItems()) {
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
