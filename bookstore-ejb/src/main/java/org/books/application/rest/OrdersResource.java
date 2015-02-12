package org.books.application.rest;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.application.rest.dto.OrderRequest;
import org.books.application.service.OrderService;
import org.books.application.service.OrderServiceLocal;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Order;

/**
 *
 * @author AWy
 */
@Path("orders")
public class OrdersResource {

    @EJB(beanInterface = OrderServiceLocal.class)
    private OrderService service;

    @POST
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public OrderInfo placeOrder(OrderRequest orderRequest) {
	if (orderRequest == null || orderRequest.getCustomerId() == null || orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	for (OrderItem item : orderRequest.getItems()) {
	    if (!CheckerUtility.isNotNullAndNotEmpty(item.getIsbn()) || item.getQuantity() == null) {
		throw new WebApplicationException(Status.BAD_REQUEST);
	    }
	}
	OrderInfo orderInfo = null;
	try {
	    orderInfo = service.placeOrder(orderRequest.getCustomerId(), orderRequest.getItems());
	} catch (CustomerNotFoundException ex) {
	    Logger.getLogger(OrdersResource.class.getName()).log(Level.SEVERE, "CustomerNotFoundException", ex.getMessage());
	    throw new WebApplicationException(Status.NOT_FOUND);
	} catch (BookNotFoundException ex) {
	    Logger.getLogger(OrdersResource.class.getName()).log(Level.SEVERE, "BookNotFoundException", ex.getMessage());
	    throw new WebApplicationException(Status.NOT_FOUND);
	} catch (PaymentFailedException ex) {
	    Logger.getLogger(OrdersResource.class.getName()).log(Level.SEVERE, "PaymentFailedException", ex.getMessage());
	    throw new WebApplicationException(Status.PAYMENT_REQUIRED);
	}

	return orderInfo;
    }

    @GET
    @Path("{id}")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Order findOrderById(@PathParam("id") Long id) {
	if (id == null) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	Order order = null;
	try {
	    order = service.findOrder(id);
	} catch (OrderNotFoundException ex) {
	    Logger.getLogger(OrdersResource.class.getName()).log(Level.SEVERE, "OrderNotFoundException", ex.getMessage());
	    throw new WebApplicationException(Status.NOT_FOUND);
	}

	return order;
    }

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Order findOrderByNumber(@QueryParam("number") String number) {
	if (!CheckerUtility.isNotNullAndNotEmpty(number)) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	Order order = null;
	try {
	    order = service.findOrder(number);
	} catch (OrderNotFoundException ex) {
	    Logger.getLogger(OrdersResource.class.getName()).log(Level.SEVERE, "OrderNotFoundException", ex.getMessage());
	    throw new WebApplicationException(Status.NOT_FOUND);
	}

	return order;
    }

    @DELETE
    @Path("{id}")
    public void cancelOrder(@PathParam("id") Long id) {
	if (id == null) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	try {
	    service.cancelOrder(id);
	} catch (OrderNotFoundException ex) {
	    Logger.getLogger(OrdersResource.class.getName()).log(Level.SEVERE, "OrderNotFoundException", ex);
	    throw new WebApplicationException(Status.NOT_FOUND);
	} catch (InvalidOrderStatusException ex) {
	    Logger.getLogger(OrdersResource.class.getName()).log(Level.SEVERE, "InvalidOrderStatusException", ex);
	    throw new WebApplicationException(Status.CONFLICT);
	}
    }

}
