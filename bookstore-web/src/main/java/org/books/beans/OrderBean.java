package org.books.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.application.service.OrderService;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.LineItem;
import org.books.util.MessageFactory;

/**
 *
 * @author Silvan
 */
@Named("orderBean")
@SessionScoped
public class OrderBean implements Serializable {

    private static final String PAYMENT_FAILED_ERROR = "org.books.paymentFailedError";
    private static final String WARNING_USER_NOT_FOUND = "org.books.customerNotFound";
    private static final String BOOK_NOT_FOUND_ERROR = "org.books.bookNotFound";

    @Inject
    private LoginBean loginBean;

    @Inject
    private NavigationBean navigationBean;

    @Inject
    private OrderService orderService;

    @Inject
    private ShoppingCartBean shoppingCartBean;

    private OrderInfo order;

    private static final Logger logger = Logger.getLogger(OrderBean.class.getName());

    public List<LineItem> getItems() {
	return shoppingCartBean.getItems();
    }

    public String creditCardExpireDate() {
	CreditCard card = loginBean.getCustomer().getCreditCard();
	return String.format("%02d/%02d%n", card.getExpirationMonth(), card.getExpirationYear() % 1000);
    }

    public OrderInfo getOrder() {
	return order;
    }

    // FIXME F5 in orderConfimration erstellt immer eine neue order?!
    public String send() {
	try {
	    // TODO SIR noch nicht schön!
	    List<LineItem> lineItems = getItems();
	    List<OrderItem> orderItems = new LinkedList<>();
	    for (LineItem item : lineItems) {
		OrderItem orderItem = new OrderItem();
		orderItem.setIsbn(item.getBook().getIsbn());
		orderItem.setQuantity(item.getQuantity());
		orderItems.add(orderItem);
	    }
	    this.order = orderService.placeOrder(loginBean.getCustomer().getId(), orderItems);
	    logger.log(Level.SEVERE, loginBean.getCustomer().toString());
	    shoppingCartBean.clearCart();
	    return navigationBean.goToOrderConfirmation();
	} catch (PaymentFailedException ex) {
	    Logger.getLogger(OrderBean.class.getName()).log(Level.SEVERE, null, ex);
	    MessageFactory.error(PAYMENT_FAILED_ERROR);
	    return null;
	} catch (CustomerNotFoundException ex) {
	    Logger.getLogger(OrderBean.class.getName()).log(Level.SEVERE, null, ex);
	    MessageFactory.error(WARNING_USER_NOT_FOUND);
	    return null;
	} catch (BookNotFoundException ex) {
	    Logger.getLogger(OrderBean.class.getName()).log(Level.SEVERE, null, ex);
	    MessageFactory.error(BOOK_NOT_FOUND_ERROR);
	    return null;
	}
    }
}
