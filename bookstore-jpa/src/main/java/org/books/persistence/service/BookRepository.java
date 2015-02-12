package org.books.persistence.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.books.persistence.QueryUtil;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Book_;

public class BookRepository {

    private final EntityManager em;

    public BookRepository(EntityManager em) {
	this.em = em;
    }

    public Book findById(Long id) {
	Book book = em.find(Book.class, id);
	if (book != null) {
	    em.refresh(book);
	}
	return book;
    }

    public Book findByISBN(String isbn) {
	Validate.notNull(isbn, "'isbn' darf nicht null sein");
	Validate.notEmpty(isbn, "'isbn' darf nicht leer sein");
	TypedQuery<Book> query = em.createNamedQuery(Book.QUERY_BY_ISBN, Book.class);
	query.setParameter(Book.PARAM_ISBN, isbn.toLowerCase());

	Book result = null;
	try {
	    result = query.getSingleResult();
	} catch (NoResultException ex) {
	    // Ignore no Result Exception
	} catch (Exception ex) {
	    // TODO Log error
	    System.err.println(ex.getMessage());
	}
	return result;
    }

    public List<Book> searchBooks(String keywords) {
	Validate.notNull(keywords, "'keywords' darf nicht null sein");
	Validate.notEmpty(keywords, "'keywords' darf nicht leer sein");

	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<Book> q = cb.createQuery(Book.class);
	Root<Book> book = q.from(Book.class);

	CriteriaQuery<Book> crit = q.select(book);

	Expression<String> titlePath = book.get(Book_.title);
	Expression<String> authorPath = book.get(Book_.authors);
	Expression<String> publisherPath = book.get(Book_.publisher);

	String[] keywordsArray = StringUtils.split(keywords, ' ');
	Predicate[] predicates = new Predicate[keywordsArray.length];
	for (int i = 0; i < keywordsArray.length; i++) {
	    String convertedKeyword = QueryUtil.convertToLikeValueAndLowerCase(keywordsArray[i]);
	    predicates[i] = cb.or(cb.like(cb.lower(titlePath), convertedKeyword), cb.like(cb.lower(authorPath), convertedKeyword), cb.like(cb.lower(publisherPath), convertedKeyword));
	}
	crit.where(cb.and(predicates));
	TypedQuery<Book> query = em.createQuery(q);
	return query.getResultList();

    }

}
