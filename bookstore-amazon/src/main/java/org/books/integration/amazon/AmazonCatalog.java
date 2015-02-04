package org.books.integration.amazon;

import com.amazon.webservices.AWSECommerceService;
import com.amazon.webservices.AWSECommerceServicePortType;
import com.amazon.webservices.Errors;
import com.amazon.webservices.Item;
import com.amazon.webservices.ItemAttributes;
import com.amazon.webservices.ItemLookup;
import com.amazon.webservices.ItemLookupRequest;
import com.amazon.webservices.ItemLookupResponse;
import com.amazon.webservices.ItemSearch;
import com.amazon.webservices.ItemSearchRequest;
import com.amazon.webservices.ItemSearchResponse;
import com.amazon.webservices.Items;
import com.amazon.webservices.Price;
import com.amazon.webservices.Request;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.books.persistence.entity.Book;
import org.books.persistence.enums.Binding;

@Stateless
public class AmazonCatalog {

    private static final int MAX_NBR_OF_PAGES = 10;

    private static final String HARDCOVER = "Hardcover";
    private static final String PAPERBACK = "Paperback";
    private static final String ITEM_ATTRIBUTES = "ItemAttributes";
    private static final String BOOKS = "Books";
    private static final String ISBN = "ISBN";

    private AWSECommerceServicePortType proxy;

    @EJB
    private AmazonTimeGuard timeGuard;

    @PostConstruct
    public void init() {
	AWSECommerceService service = new AWSECommerceService();
	proxy = service.getAWSECommerceServicePort();
	if(timeGuard == null) {
	    timeGuard = new AmazonTimeGuard();
	}
    }

    public Book itemLookup(String isbn) throws AmazonException {
	await();
	final ItemLookupResponse response = proxy.itemLookup(createItemLookup(isbn));

	for (Items items : response.getItems()) {
	    checkForErrors(items);
	    if (!isValid(items)) {
		continue;
	    }
	    for (Item item : items.getItem()) {
		Book book = getBook(item);
		if (isComplete(book)) {
		    return book;
		}
	    }
	}
	return null;
    }

    public List<Book> itemSearch(String keywords) throws AmazonException {
	List<Book> result = new ArrayList<>();
	for (int currentPage = 1; currentPage <= MAX_NBR_OF_PAGES; currentPage++) {
	    await();
	    final ItemSearchResponse response = proxy.itemSearch(createItemSearch(keywords, currentPage));

	    for (Items items : response.getItems()) {
		checkForErrors(items);
		if (!isValid(items)) {
		    continue;
		}
		for (Item item : items.getItem()) {
		    Book book = getBook(item);
		    if (isComplete(book)) {
			result.add(book);
		    }
		}
	    }
	}
	return result;
    }

    /**
     * Sleeps if necessary and sets the lastStartTime
     */
    private void await() {
	if (timeGuard.isSafe(System.currentTimeMillis())) {
	    try {
		Thread.sleep(timeGuard.getSleepingTime(System.currentTimeMillis()));
	    } catch (InterruptedException ex) {
		Logger.getLogger(AmazonCatalog.class.getName()).log(Level.SEVERE, "Could not sleep", ex);
	    }
	}
	timeGuard.setLastStartTime(System.currentTimeMillis());
    }

    private ItemLookup createItemLookup(String isbn) {
	ItemLookup itemLookup = new ItemLookup();
	ItemLookupRequest request = new ItemLookupRequest();
	request.setIdType(ISBN);
	request.setSearchIndex(BOOKS);
	request.getResponseGroup().add(ITEM_ATTRIBUTES);
	request.getItemId().add(isbn);
	itemLookup.getRequest().add(request);
	return itemLookup;
    }

    private ItemSearch createItemSearch(String keywords, int pageIndex) {
	ItemSearch itemSearch = new ItemSearch();
	ItemSearchRequest request = new ItemSearchRequest();
	request.setSearchIndex(BOOKS);
	request.getResponseGroup().add(ITEM_ATTRIBUTES);
	request.setItemPage(BigInteger.valueOf(pageIndex));
	request.setKeywords(keywords);
	itemSearch.getRequest().add(request);
	return itemSearch;
    }

    private Binding getBinding(String bindingString) {
	if(bindingString == null){
	    return Binding.Unknown;
	}
	switch (bindingString) {
	    case HARDCOVER:
		return Binding.Hardcover;
	    case PAPERBACK:
		return Binding.Paperback;
	    default:
		return Binding.Unknown;
	}
    }

    private Book getBook(Item item) {
	Book book = new Book();
	ItemAttributes attributes = item.getItemAttributes();
	book.setIsbn(attributes.getISBN());
	book.setTitle(attributes.getTitle());
	StringBuilder sb = new StringBuilder();
	boolean prefix = false;
	for (String author : attributes.getAuthor()) {
	    if (prefix) {
		sb.append(", ");
	    }
	    sb.append(author);
	    prefix = true;
	}
	book.setAuthors(sb.toString());
	book.setPublisher(attributes.getPublisher());
	Integer year = null;
	if (attributes.getPublicationDate() != null) {
	    try {
		year = Integer.parseInt(attributes.getPublicationDate().substring(0, 4));
	    } catch (Exception e) {
		Logger.getLogger(AmazonCatalog.class.getName()).warning("Could not parse the year.");
	    }
	}
	book.setPublicationYear(year);
	book.setBinding(getBinding(attributes.getBinding()));
	book.setNumberOfPages(attributes.getNumberOfPages() == null ? 0 : attributes.getNumberOfPages().intValue());
	book.setPrice(getPrice(attributes));

	return book;
    }

    private BigDecimal getPrice(ItemAttributes attributes) {
	final Price listPrice = attributes.getListPrice();
	if (listPrice == null) {
	    return null;
	}
	final BigInteger amount = listPrice.getAmount();
	if (amount == null) {
	    return null;
	}
	return new BigDecimal(amount, 2);
    }

    private boolean isValid(Items items) {
	return Boolean.parseBoolean(items.getRequest().getIsValid());
    }

    private void checkForErrors(Items items) throws AmazonException {
	final Request request = items.getRequest();
	if (request == null || request.getErrors() == null || request.getErrors().getError() == null) {
	    return;
	}
	for (Errors.Error error : request.getErrors().getError()) {
	    throw new AmazonException(error.getMessage(), error.getCode());
	}
    }

    private boolean isComplete(Book book) {
	return isNotNullOrEmpty(book.getAuthors())
		&& book.getBinding() != null
		&& isNotNullOrEmpty(book.getIsbn())
		&& book.getNumberOfPages() != null
		&& book.getPrice() != null
		&& book.getPublicationYear() != null
		&& isNotNullOrEmpty(book.getPublisher())
		&& isNotNullOrEmpty(book.getTitle());
    }

    private boolean isNotNullOrEmpty(String s) {
	return s != null && !(s.isEmpty());
    }
}
