package org.books.integration;

import java.util.List;
import org.books.persistence.entity.Book;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class AmazonCatalogTest {

    private static final AmazonCatalog amazonCatalog = new AmazonCatalog();
    private static final String isbn = "013390069X";
    private static final String keywords = "Java EE";

//    @Test
    public void findBook() throws AmazonException {
	Book book = amazonCatalog.itemLookup(isbn);
	assertNotNull(book);
    }

//    @Test
    public void searchBooks() throws AmazonException {
	List<Book> books = amazonCatalog.itemSearch(keywords);
	assertFalse(books.isEmpty());
    }
}
