package org.books.application.service;

import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.books.application.exception.BookNotFoundException;
import org.books.persistence.entity.Book;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class CatalogIT {

//	private static final String CATALOG_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CatalogService";
//	private static final String DB_UNIT_PROPERTIES = "/dbunit.properties";
//	private static final String DB_UNIT_DATASET = "/dataset.xml";
//
//	private static CatalogService catalogService;
//
//	private final String isbn = "0321349806";
//	private final String keywords = "Java Nutshell Reilly";
//
//	@BeforeClass
//	public void setUpClass() throws Exception {
//		Context jndiContext = new InitialContext();
//		catalogService = (CatalogService) jndiContext.lookup(CATALOG_SERVICE_NAME);
//		initDatabase();
//	}
//
//	private void initDatabase() throws Exception {
//		System.getProperties().load(getClass().getResourceAsStream(DB_UNIT_PROPERTIES));
//		IDatabaseTester databaseTester = new PropertiesBasedJdbcDatabaseTester();
//		IDataSet dataset = new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream(DB_UNIT_DATASET));
//		databaseTester.setDataSet(dataset);
//		databaseTester.onSetup();
//	}
//
//	@Test
//	public void findBook() throws BookNotFoundException {
//		Book book = catalogService.findBook(isbn);
//		assertNotNull(book);
//	}
//
//	@Test
//	public void searchBooks() {
//		List<Book> books = catalogService.searchBooks(keywords);
//		assertFalse(books.isEmpty());
//	}
}
