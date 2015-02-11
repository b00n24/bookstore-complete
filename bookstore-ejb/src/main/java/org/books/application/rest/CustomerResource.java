package org.books.application.rest;

import org.books.application.rest.dto.Registration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import javax.ws.rs.core.Response.Status;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.EmailAlreadyUsedException;
import org.books.application.service.CustomerService;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;

/**
 *
 * @author AWy
 */
@Path("customers")
public class CustomerResource {

    @EJB
    private CustomerService service;

    @POST
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Long findBookById(Registration registration) {
	if (registration == null || 
		registration.getCustomer() == null || 
		!CheckerUtility.notNullAndNotEmpty(registration.getPassword()) || 
		!CheckerUtility.check(registration.getCustomer())) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	Long customerId = null;
	try {
	    customerId = service.registerCustomer(registration.getCustomer(), registration.getPassword());
	} catch (EmailAlreadyUsedException ex) {
	    Logger.getLogger(CustomerResource.class.getName()).log(Level.SEVERE, "EmailAlreadyUsedException", ex.getMessage());
	    throw new WebApplicationException(Status.CONFLICT);
	}

	return customerId;
    }

    @GET
    @Path("{id}")
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Customer findCustomerById(@PathParam("id") Long id) {
	if (id == null) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	Customer customer = null;
	try {
	    customer = service.findCustomer(id);
	} catch (CustomerNotFoundException ex) {
	    Logger.getLogger(CustomerResource.class.getName()).log(Level.SEVERE, "CustomerNotFoundException", ex.getMessage());
	    throw new WebApplicationException(Status.NOT_FOUND);
	}

	return customer;
    }

    @GET
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Customer findCustomerByEmail(@QueryParam("email") String email) {
	if (!CheckerUtility.notNullAndNotEmpty(email)) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	Customer customer = null;
	try {
	    customer = service.findCustomer(email);
	} catch (CustomerNotFoundException ex) {
	    Logger.getLogger(CustomerResource.class.getName()).log(Level.SEVERE, "CustomerNotFoundException", ex.getMessage());
	    throw new WebApplicationException(Status.NOT_FOUND);
	}

	return customer;
    }

    @GET
    @Path("search")
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public List<CustomerInfo> searchCustomerByName(@QueryParam("name") String name) {
	if (!CheckerUtility.notNullAndNotEmpty(name)) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	List<CustomerInfo> customerInfos = null;
	customerInfos = service.searchCustomers(name);

	return customerInfos;
    }

    @PUT
    @Path("{id}")
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public void updateCustomer(@PathParam("id") Long id, Customer customer) {
	if (id == null || !CheckerUtility.check(customer)) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	try {
	    service.updateCustomer(customer);
	} catch (CustomerNotFoundException ex) {
	    Logger.getLogger(CustomerResource.class.getName()).log(Level.SEVERE, "CustomerNotFoundException", ex.getMessage());
	    throw new WebApplicationException(Status.BAD_REQUEST);
	} catch (EmailAlreadyUsedException ex) {
	    Logger.getLogger(CustomerResource.class.getName()).log(Level.SEVERE, "EmailAlreadyUsedException", ex.getMessage());
	    throw new WebApplicationException(Status.CONFLICT);
	}
    }
}
