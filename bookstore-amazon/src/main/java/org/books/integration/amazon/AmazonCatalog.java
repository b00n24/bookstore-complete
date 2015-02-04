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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import org.books.persistence.entity.Book;
import org.books.persistence.enums.Binding;

@Stateless
public class AmazonCatalog {

    private AWSECommerceServicePortType proxy;

    private static final String HARDCOVER = "Hardcover";
    private static final String PAPERBACK = "Paperback";
    private static final String ITEM_ATTRIBUTES = "ItemAttributes";
    private static final String BOOKS = "Books";
    private static final String ISBN = "ISBN";

    @PostConstruct
    public void init() {
	AWSECommerceService service = new AWSECommerceService();
	proxy = service.getAWSECommerceServicePort();
    }

    public Book itemLookup(String isbn) throws AmazonException {
	final ItemLookupResponse response = proxy.itemLookup(createItemLookup(isbn));

	for (Items items : response.getItems()) {
	    checkForErrors(items);
	    if (!isValid(items)) {
		continue;
	    }
	    for (Item item : items.getItem()) {
		return getBook(item);
	    }
	}
	return null;
    }

    public List<Book> itemSearch(String keywords) throws AmazonException {
	final ItemSearchResponse response = proxy.itemSearch(createItemSearch(keywords));

	List<Book> result = new ArrayList<>();
	for (Items items : response.getItems()) {
	    checkForErrors(items);
	    if (!isValid(items)) {
		continue;
	    }
	    for (Item item : items.getItem()) {
		result.add(getBook(item));
	    }
	}
	return result;
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

    private ItemSearch createItemSearch(String keywords) {
	ItemSearch itemSearch = new ItemSearch();
	ItemSearchRequest request = new ItemSearchRequest();
	request.setSearchIndex(BOOKS);
	request.getResponseGroup().add(ITEM_ATTRIBUTES);
	request.setKeywords(keywords);
	itemSearch.getRequest().add(request);
	return itemSearch;
    }

    private Binding getBinding(String bindingString) {
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
	try {
	    year = Integer.parseInt(attributes.getPublicationDate().substring(0, 4));
	} catch (Exception e) {
	    Logger.getLogger(AmazonCatalog.class.getName()).warning("Could not parse the year.");
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
	    return BigDecimal.ZERO;
	}
	final BigInteger amount = listPrice.getAmount();
	if (amount == null) {
	    return BigDecimal.ZERO;
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
}
