package org.books.application.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import javax.ws.rs.core.Response.Status;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.service.OrderService;
import org.books.persistence.dto.OrderInfo;

/**
 *
 * @author AWy
 */
@Path("admin/orders")
public class OrdersResourceAdmin {

    @EJB
    private OrderService service;

    @GET
    @Path("search")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public List<OrderInfo> searchOrdersOfCustomer(@QueryParam("customerId") Long customerId, @QueryParam("year") Integer year) {
	if (customerId == null || year == null) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	List<OrderInfo> orderInfos = null;
	try {
	    orderInfos = service.searchOrders(customerId, year);
	} catch (CustomerNotFoundException ex) {
	    Logger.getLogger(OrdersResourceAdmin.class.getName()).log(Level.SEVERE, "OrdersResourceAdmin", ex);
	    throw new WebApplicationException(Status.NOT_FOUND);
	}

	return orderInfos;
    }

}
