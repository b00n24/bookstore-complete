package org.books.application.service.rest;

import java.util.List;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import junit.framework.Assert;
import org.books.application.service.DBUnitInitializer;
import org.books.persistence.entity.Book;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import org.testng.annotations.BeforeClass;

public class CatalogResourceIT extends DBUnitInitializer {

    private static final String ENDPOINT = "http://localhost:8080/bookstore/rest/books";
    private WebTarget target;

    @BeforeClass
    public void setupClass() throws Exception {
	target = ClientBuilder.newClient().target(ENDPOINT);
    }

    @Test
    public void findBookById() {
	// WHEN
	final Response response = target.path("3").request(MediaType.APPLICATION_XML).get();

	// TEHN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final Book result = response.readEntity(Book.class);
	Assert.assertNotNull(result);
    }

    @Test
    public void findBookById_notFound() {
	final Response response = target.path("555555").request(MediaType.APPLICATION_XML).get();
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void findBookByIsbn() {
	// WHEN
	final Response response = target.queryParam("isbn", "0071809252").request(MediaType.APPLICATION_XML).get();

	// TEHN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final Book result = response.readEntity(Book.class);
	Assert.assertNotNull(result);
	Assert.assertEquals("Java: A Beginner's Guide", result.getTitle());
    }

    @Test
    public void findBookByIbsn_empty_badRequest() {
	final Response response = target.queryParam("isbn", "").request(MediaType.APPLICATION_XML).get();
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void findBookByIbsn_notFound() {
	final Response response = target.queryParam("isbn", "00718093252").request(MediaType.APPLICATION_XML).get();
	assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void searchBooks() {
	// WHEN
	final Response response = target.path("search").queryParam("keywords", "Java").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final List<Book> result = response.readEntity(new GenericType<List<Book>>() {
	});
	assertFalse(result.isEmpty());
    }

    @Test
    public void searchBooks_emptyKeywords_badRequest() {
	// WHEN
	final Response response = target.path("search").queryParam("keywords", "").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test(threadPoolSize = 20, invocationCount = 10)
    public void searchBooks_multipleThreads() {
	// WHEN
	final Response response = target.path("search").queryParam("keywords", "Java").request(MediaType.APPLICATION_XML).get();

	// THEN
	assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	final List<Book> result = response.readEntity(new GenericType<List<Book>>() {
	});
	assertFalse(result.isEmpty());
    }

}
