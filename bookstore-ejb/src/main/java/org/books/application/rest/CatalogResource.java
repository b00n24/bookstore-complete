package org.books.application.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import javax.ws.rs.core.Response.Status;
import org.books.application.exception.BookNotFoundException;
import org.books.application.service.CatalogService;
import org.books.application.service.CatalogServiceLocal;
import org.books.persistence.entity.Book;

/**
 *
 * @author AWy
 */
@Path("books")
public class CatalogResource {

    @EJB(beanInterface = CatalogServiceLocal.class)
    private CatalogService service;

    @GET
    @Path("{id}")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Book findBookById(@PathParam("id") Long id) {
	if (id == null) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	Book book = null;
	try {
	    book = service.findBook(id);
	} catch (BookNotFoundException ex) {
	    Logger.getLogger(CatalogResource.class.getName()).log(Level.SEVERE, "BookNotFound", ex.getMessage());
	    throw new WebApplicationException(Status.NOT_FOUND);
	}

	return book;
    }

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Book findBookByIsbn(@QueryParam("isbn") String isbn) {
	if (!CheckerUtility.isNotNullAndNotEmpty(isbn)) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	Book book = null;
	try {
	    book = service.findBook(isbn);
	} catch (BookNotFoundException ex) {
	    Logger.getLogger(CatalogResource.class.getName()).log(Level.SEVERE, "BookNotFound", ex.getMessage());
	    throw new WebApplicationException(Status.NOT_FOUND);
	}

	return book;
    }

    @GET
    @Path("search")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public List<Book> search(@QueryParam("keywords") String keywords) {
	if (!CheckerUtility.isNotNullAndNotEmpty(keywords)) {
	    throw new WebApplicationException(Status.BAD_REQUEST);
	}
	List<Book> books = null;
	books = service.searchBooks(keywords);

	return books;
    }
}
