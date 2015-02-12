package org.books.application.service.rest;

import java.util.List;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import junit.framework.Assert;
import org.books.application.rest.dto.Registration;
import org.books.application.service.DBUnitInitializer;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import org.testng.annotations.BeforeClass;

public class CustomerResourceIT extends DBUnitInitializer {

    private static final String ENDPOINT = "http://localhost:8080/bookstore/rest/customers";
    private WebTarget target;

    @BeforeClass
    public void setupClass() {
	target = ClientBuilder.newClient().target(ENDPOINT);
    }

    @Test
    public void registerCustomer() {
	// GIVEN
	Customer customer = createCustomer("newCustomer@email.com");
	Registration registration = new Registration(customer, "pass");

	// WHEN
	final Response response = target.request().post(Entity.xml(registration));

	// THEN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final Long id = response.readEntity(Long.class);
	Assert.assertNotNull(id);
    }

    @Test
    public void registerCustomer_addressFieldMissing() {
	// GIVEN
	Customer customer = createCustomer("newCustomer_missing@email.com");
	customer.getAddress().setCity(null);
	Registration registration = new Registration(customer, "pass");

	// WHEN
	final Response response = target.request().post(Entity.xml(registration));

	// THEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void findCustomerById() {
	// WHEN
	final Response response = target.path("1").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final Customer result = response.readEntity(Customer.class);
	Assert.assertNotNull(result);
    }

    @Test
    public void findCustomerById_notFound() {
	final Response response = target.path("555555").request(MediaType.APPLICATION_XML).get();
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void findCustomerByEmail() {
	final Response response = target.queryParam("email", "homer@simpson.com").request(MediaType.APPLICATION_XML).get();
	if (response.getStatus() != Response.Status.OK.getStatusCode()) {
	    assertTrue(false);
	}
	final Customer result = response.readEntity(Customer.class);
	Assert.assertNotNull(result);
    }

    @Test
    public void findCustomerByEmail_empty_badRequest() {
	final Response response = target.queryParam("email", "").request(MediaType.APPLICATION_XML).get();
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void findCustomerByEmail_notFound() {
	final Response response = target.queryParam("email", "wrongEmail").request(MediaType.APPLICATION_XML).get();
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void searchCustomersByName() {
	// GIVEN
	final Response response = target.path("search").queryParam("name", "Homer").request(MediaType.APPLICATION_XML).get();

	// THEN
	//assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final List<CustomerInfo> result = response.readEntity(new GenericType<List<CustomerInfo>>() {
	});
	assertFalse(result.isEmpty());
    }

    @Test
    public void searchCustomersByName_emptyKeywords_badRequest() {
	// GIVEN
	final Response response = target.path("search").queryParam("name", "").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void searchCustomersByName_emptyList() {
	// GIVEN
	final Response response = target.path("search").queryParam("name", "notExising").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final List<CustomerInfo> result = response.readEntity(new GenericType<List<CustomerInfo>>() {
	});
	assertTrue(result.isEmpty());
    }

    @Test
    public void updateCustomer() {
	// GIVEN
	final Response response1 = target.path("3").request(MediaType.APPLICATION_XML).get();
	final Customer customer = response1.readEntity(Customer.class);
	customer.setEmail("updateCustomer@email.com");

	// WHEN
	final Response response = target.path(customer.getId().toString()).request().put(Entity.xml(customer));

	// THEN
	assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void updateCustomer_existingEmail_conflict() {
	// GIVEN
	final Response response1 = target.queryParam("email", "homer@simpson.com").request(MediaType.APPLICATION_XML).get();
	final Customer customer = response1.readEntity(Customer.class);
	customer.setEmail("marge@simpson.com");

	// WHEN
	final Response response = target.path(customer.getId().toString()).request().put(Entity.xml(customer));

	// THEN
	assertEquals(response.getStatus(), Response.Status.CONFLICT.getStatusCode());
    }

    @Test
    public void updateCustomer_notValid() {
	Customer customer = createCustomer("updateCustomerNotValid@email.com");
	customer.setEmail("");
	String id = "1";
	final Response response = target.path(id).request().put(Entity.xml(customer));
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void updateCustomer_notValid2() {
	Customer customer = createCustomer("updateCustomerNotValid2@email.com");
	customer.setEmail(null);
	String id = "1";
	final Response response = target.path(id).request().put(Entity.xml(customer));
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }
}
