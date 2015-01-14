package org.books.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.books.persistence.Book;
import org.books.persistence.LineItem;

/**
 *
 * @author Silvan
 */
@Named("shoppingCartBean")
@SessionScoped
public class ShoppingCartBean implements Serializable {

    private final Map<String, LineItem> itemsMap = new HashMap();

    public List<LineItem> getItems() {
        return new ArrayList<>(itemsMap.values());
    }

    public void add(Book book) {
        LineItem item = itemsMap.get(book.getIsbn());
        if (item != null) {
            Integer newQuantity = item.getQuantity() == null ? 1 : item.getQuantity() + 1;
            item.setQuantity(newQuantity);
        } else {
            itemsMap.put(book.getIsbn(), new LineItem(book, 1));
        }
    }

    public void remove(LineItem item) {
        if (itemsMap.containsKey(item.getBook().getIsbn())) {
            itemsMap.remove(item.getBook().getIsbn());
        }
    }

    public BigDecimal getTotal() {
	BigDecimal total = BigDecimal.ZERO;
	for (LineItem lineItem : itemsMap.values()) {
	    if (lineItem.getQuantity() != null) {
		BigDecimal itemPrice = lineItem.getBook().getPrice().multiply(new BigDecimal(lineItem.getQuantity()));
		total = total.add(itemPrice);
	    }
	}
	return total;
    }

    public boolean isCartEmpty() {
	return itemsMap.isEmpty();
    }

    public void clearCart() {
	itemsMap.clear();
    }
}
