package org.books.application.service;

import java.util.List;
import javax.ejb.Remote;
import org.books.application.exception.BookNotFoundException;
import org.books.persistence.entity.Book;

/**
 *
 * @author AWy
 */
@Remote
public interface CatalogService {

    /**
     * Finds a book with a particular identifier.
     *
     * @param bookId the book identifier
     * @return the found book
     * @throws BookNotFoundException if no book with the specified identifier
     * exists
     */
    Book findBook(Long bookId) throws BookNotFoundException;

    /**
     * Finds a book with a particular ISBN number.
     *
     * @param isbn the ISBN number
     * @return the found book
     * @throws BookNotFoundException if no book with the specified ISBN number
     * exists
     */
    Book findBook(String isbn) throws BookNotFoundException;

    /**
     * Searches for books by keywords. A book is included in the results list if
     * all keywords are contained in its title, authors or publisher field.
     *
     * @param keywords the keywords to search for
     * @return information on the matching books (the list may be empty)
     */
    List<Book> searchBooks(String keywords);
}
