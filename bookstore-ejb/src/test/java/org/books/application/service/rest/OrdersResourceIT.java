package org.books.application.service.rest;

import java.util.ArrayList;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import junit.framework.Assert;
import org.books.application.rest.dto.OrderRequest;
import org.books.application.service.DBUnitInitializer;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Order;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import org.testng.annotations.BeforeClass;

public class OrdersResourceIT extends DBUnitInitializer {

    private static final String ENDPOINT = "http://localhost:8080/bookstore/rest/orders";
    private WebTarget target;

    private static final Long EXISTING_CUSTOMER_ID = 1l;
    private static final String EXISTING_ORDER_ID = "1";
    private static final String EXISTING_SHIPPED_ORDER_ID = "3";
    private static final String EXISTING_ORDER_NUMBER = "111";

    @BeforeClass
    public void setupClass() {
	target = ClientBuilder.newClient().target(ENDPOINT);
    }

    @Test
    public void placeOrder() {
	// GIVEN
	OrderRequest orderRequest = createOrderRequest(EXISTING_CUSTOMER_ID);

	// WHEN
	final Response response = target.request().post(Entity.xml(orderRequest));

	// WHEN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final OrderInfo result = response.readEntity(OrderInfo.class);
	Assert.assertNotNull(result);
    }

    @Test
    public void placeOrder_badRequest() {
	// WHEN
	final Response response = target.request().post(Entity.xml(null));

	// WHEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void placeOrder_badRequest2() {
	// GIVEN
	OrderRequest orderRequest = new OrderRequest(null, new ArrayList<OrderItem>());

	// WHEN
	final Response response = target.request().post(Entity.xml(orderRequest));

	// WHEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void placeOrder_badRequest3() {
	// GIVEN
	OrderRequest orderRequest = new OrderRequest(3l, null);

	// WHEN
	final Response response = target.request().post(Entity.xml(orderRequest));

	// WHEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void placeOrder_badRequest4() {
	// GIVEN
	OrderRequest orderRequest = new OrderRequest(3l, new ArrayList<OrderItem>());

	// WHEN
	final Response response = target.request().post(Entity.xml(orderRequest));

	// WHEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void placeOrder_badRequest5() {
	// GIVEN
	OrderRequest orderRequest = createOrderRequest(EXISTING_CUSTOMER_ID);
	orderRequest.getItems().get(0).setIsbn("");

	// WHEN
	final Response response = target.request().post(Entity.xml(orderRequest));

	// WHEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void placeOrder_customerNotFound_notFound() {
	// GIVEN
	Long customerId = 555l;
	OrderRequest orderRequest = createOrderRequest(customerId);

	// WHEN
	final Response response = target.request().post(Entity.xml(orderRequest));

	// WHEN
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void placeOrder_bookNotFound_notFound() {
	// GIVEN
	OrderRequest orderRequest = createOrderRequest(EXISTING_CUSTOMER_ID);
	orderRequest.getItems().get(0).setIsbn("notExisting");

	// WHEN
	final Response response = target.request().post(Entity.xml(orderRequest));

	// WHEN
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void findOrderById() {
	// WHEN
	final Response response = target.path(EXISTING_ORDER_ID).request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final Order result = response.readEntity(Order.class);
	Assert.assertNotNull(result);
    }

    @Test
    public void findOrderById_idEmpty_badRequest() {
	// WHEN
	final Response response = target.path("").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void findOrderById_notFound() {
	// WHEN
	final Response response = target.path("3444443").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void findOrderByNumber() {
	// WHEN
	final Response response = target.queryParam("number", EXISTING_ORDER_NUMBER).request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final Order result = response.readEntity(Order.class);
	Assert.assertNotNull(result);
    }

    @Test
    public void findOrderByNumber_null_badRequest() {
	// WHEN
	final Response response = target.queryParam("number", null).request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void findOrderByNumber_empty_badRequest() {
	// WHEN
	final Response response = target.queryParam("number", "").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void findOrderByNumber_nonExisting_notFound() {
	// WHEN
	final Response response = target.queryParam("number", "nonExisting").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void cancelOrder() {
	// WHEN
	final Response response = target.path(EXISTING_ORDER_ID).request(MediaType.APPLICATION_XML).delete();

	// THEN
	assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode());
    }

//    @Test
    public void cancleOrder_orderIdMissing_badRequest() {
	// Dies kann gar nicht gehen, oder? "" kann er nicht nach Long casten... daher kommt er schon gar nicht in die methode
	// WHEN
	final Response response = target.path("").request(MediaType.APPLICATION_XML).delete();

	// THEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void cancleOrder_orderNotFound() {
	// WHEN
	final Response response = target.path("333").request(MediaType.APPLICATION_XML).delete();

	// THEN
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void cancleOrder_invalidOrderStatus() throws InterruptedException {
	// WHEN trying to delete a shipped order
	final Response response = target.path(EXISTING_SHIPPED_ORDER_ID).request(MediaType.APPLICATION_XML).delete();

	// THEN
	assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
    }
}
