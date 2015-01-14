package org.books.beans;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.CreditCard;
import org.books.persistence.LineItem;
import org.books.persistence.Order;
import org.books.services.OrderService;
import org.books.util.MessageFactory;

/**
 *
 * @author Silvan
 */
@Named("orderBean")
@SessionScoped
public class OrderBean implements Serializable {

    private static final String PAYMENT_FAILED_ERROR = "org.books.paymentFailedError";

    @Inject
    private LoginBean loginBean;

    @Inject
    private NavigationBean navigationBean;

    @Inject
    private OrderService orderService;

    @Inject
    private ShoppingCartBean shoppingCartBean;

    private Order order;

    private static final Logger logger = Logger.getLogger(OrderBean.class.getName());

    public List<LineItem> getItems() {
	return shoppingCartBean.getItems();
    }
    
    public String creditCardExpireDate() {
	CreditCard card = loginBean.getCustomer().getCreditCard();
	return String.format("%02d/%02d%n", card.getExpirationMonth(), card.getExpirationYear() % 1000);
    }

    public Order getOrder() {
	return order;
    }
    
    // FIXME F5 in orderConfimration erstellt immer eine neue order?!
    public String send() {
	try {
	    this.order = orderService.placeOrder(loginBean.getCustomer(), getItems());
	    logger.log(Level.SEVERE, loginBean.getCustomer().toString());
	    shoppingCartBean.clearCart();
	    return navigationBean.goToOrderConfirmation();
	} catch (PaymentFailedException ex) {
	    Logger.getLogger(OrderBean.class.getName()).log(Level.SEVERE, null, ex);
	    MessageFactory.error(PAYMENT_FAILED_ERROR);
	    return null;
	}
    }
}
