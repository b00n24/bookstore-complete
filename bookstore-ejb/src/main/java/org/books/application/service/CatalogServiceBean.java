package org.books.application.service;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import org.books.application.exception.BookNotFoundException;
import org.books.integration.amazon.AmazonCatalog;
import org.books.integration.amazon.AmazonException;
import org.books.persistence.entity.Book;
import org.books.persistence.service.BookRepository;

@Stateless(name = "CatalogService")
public class CatalogServiceBean implements CatalogServiceLocal, CatalogServiceRemote {

    @PersistenceContext(unitName = "bookstore", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;
    private BookRepository bookRepository;

    @Inject
    private AmazonCatalog amazonCatalog;

    @PostConstruct
    public void initialize() {
	bookRepository = new BookRepository(em);
    }

    @Override
    public Book findBook(Long bookId) throws BookNotFoundException {
	Book book = bookRepository.findById(bookId);
	if (book == null) {
	    throw new BookNotFoundException();
	}
	return book;
    }

    @Override
    public Book findBook(String isbn) throws BookNotFoundException {
	Book book = bookRepository.findByISBN(isbn);
	if (book == null) {
	    try {
		book = amazonCatalog.itemLookup(isbn);
	    } catch (AmazonException ex) {
		Logger.getLogger(CatalogServiceBean.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
	    }
	}
	if (book == null) {
	    throw new BookNotFoundException();
	}
	return book;
    }

    @Override
    public List<Book> searchBooks(String keywords) {
	List<Book> books = Collections.EMPTY_LIST;
	try {
	    books = amazonCatalog.itemSearch(keywords);
	} catch (AmazonException ex) {
	    Logger.getLogger(CatalogServiceBean.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
	}
	return books;
    }

}
