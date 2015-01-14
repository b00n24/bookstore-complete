package org.books.services;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.books.application.Bookstore;
import org.books.application.exception.BookNotFoundException;
import org.books.persistence.Book;

/**
 *
 * @author Silvan
 */
@ApplicationScoped
public class CatalogService {

    @Inject
    private Bookstore bookstore;

    public Book findBook(Integer id) throws BookNotFoundException {
	return bookstore.findBook(id);
    }

    public Book findBook(String isbn) throws BookNotFoundException {
	return bookstore.findBook(isbn);
    }

    public List<Book> searchBooks(String keywords) {
	return bookstore.searchBooks(keywords);
    }
}
