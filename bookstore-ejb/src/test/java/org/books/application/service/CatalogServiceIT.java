package org.books.application.service;

import java.util.List;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import junit.framework.Assert;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.OrderNotFoundException;
import org.books.persistence.entity.Book;
import org.dbunit.IDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author awy
 */
public class CatalogServiceIT {

    private static final String SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CatalogService!org.books.application.service.CatalogServiceRemote";
    private static final String DB_UNIT_PROPERTIES = "/dbunit.properties";
    private static final String DB_UNIT_DATASET = "/dataset.xml";

    private CatalogService service;

    private static final String ISBN = "0596009208";
    private static final Long ID = 1l;
    private static final String KEYWORDS = "java";

    @BeforeTest
    public void setUpClass() throws Exception {
	initDatabase();
	service = (CatalogService) new InitialContext().lookup(SERVICE_NAME);
    }

    private void initDatabase() throws Exception {
	System.getProperties().load(getClass().getResourceAsStream(DB_UNIT_PROPERTIES));
	IDatabaseTester databaseTester = new PropertiesBasedJdbcDatabaseTester();
	IDataSet dataset = new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream(DB_UNIT_DATASET));
	databaseTester.setDataSet(dataset);
	databaseTester.onSetup();
    }

    @AfterClass
    public void tearDownClass() throws OrderNotFoundException {
    }

    @Test
    public void findBookById() throws BookNotFoundException {
	// WHEN
	Book result = service.findBook(ID);

	// THEN
	Assert.assertNotNull(result);
    }

    @Test(expectedExceptions = BookNotFoundException.class)
    public void findBookById_wrongId() throws BookNotFoundException {
	// WHEN
	service.findBook(345345l);
    }

    @Test
    public void findBookByIsbn() throws BookNotFoundException {
	// WHEN
	Book result = service.findBook(ISBN);

	// THEN
	Assert.assertNotNull(result);
    }

    @Test(expectedExceptions = BookNotFoundException.class)
    public void findBookByIsbn_wrongIsbn() throws BookNotFoundException {
	// WHEN
	service.findBook("99dasd898384834");
    }

    @Test
    public void searchBooks() throws BookNotFoundException {
	// WHEN
	List<Book> result = service.searchBooks(KEYWORDS);

	// THEN
	Assert.assertNotNull(result);
	Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void searchBooks_wrongKeyword() throws BookNotFoundException {
	// WHEN
	List<Book> result = service.searchBooks("hhhhhhhhhhhhhhhh");

	// THEN
	Assert.assertNotNull(result);
	Assert.assertTrue(result.isEmpty());
    }

    @Test(expectedExceptions = EJBException.class)
    public void searchBooks_emptyKeyword() throws BookNotFoundException {
	// WHEN
	service.searchBooks("");
    }

    @Test(expectedExceptions = EJBException.class)
    public void searchBooks_nullKeyword() throws BookNotFoundException {
	// WHEN
	service.searchBooks(null);
    }
}
