package org.books.persistence;

import java.util.List;
import static org.books.persistence.QueriesWithDataTest.emf;
import org.books.persistence.entity.Book;
import org.books.persistence.service.BookRepository;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Silvan
 */
public class BookRepositoryTestIT extends QueriesWithDataTest {

    BookRepository service;
    
    @BeforeMethod
    public void init() {
	service = new BookRepository(emf.createEntityManager());
    }

    @Test
    public void queryBookByIsbn() {
	Book result = service.findByISBN(isbn1);

	assertEquals(isbn1, result.getIsbn());
    }

    @Test
    public void queryBooksByKeywordTitle() {
	List<Book> bookByKeyword = service.searchBooks(title1);
	assertEquals(1, bookByKeyword.size());
	assertEquals(book1.getId(), bookByKeyword.get(0).getId());
    }

    @Test
    public void queryBooksByKeywordTitleCaseInsensitiv() {
	List<Book> bookByKeyword = service.searchBooks(title1.toUpperCase());
	assertEquals(1, bookByKeyword.size());
	assertEquals(book1.getId(), bookByKeyword.get(0).getId());
    }

    @Test
    public void queryBooksByKeywordAuthor() {
	List<Book> bookByKeyword = service.searchBooks(authors1);
	assertEquals(1, bookByKeyword.size());
	assertEquals(book1.getId(), bookByKeyword.get(0).getId());
    }

    @Test
    public void queryBooksByKeywordPublisher() {
	List<Book> bookByKeyword = service.searchBooks(publisher1);
	assertEquals(1, bookByKeyword.size());
	assertEquals(book1.getId(), bookByKeyword.get(0).getId());
    }

    @Test
    public void queryBooksByKeywordTitleNoResult() {
	List<Book> bookByKeyword = service.searchBooks(title1 + " " + title2);
	assertEquals(0, bookByKeyword.size());
    }

    @Test
    public void queryBooksByKeywordTitleMultiresult() {
	List<Book> bookByKeyword = service.searchBooks(title1.substring(0, title1.length() - 1));
	assertEquals(2, bookByKeyword.size());
    }

    @Test
    public void queryBooksByKeywordCrossMultiresult() {
	List<Book> bookByKeyword = service.searchBooks(title1.substring(0, title1.length() - 1) + " " + authors1.substring(0, authors1.length() - 1));
	assertEquals(2, bookByKeyword.size());
    }
}
