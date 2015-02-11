package org.books.persistence.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author AWy
 */
@XmlRootElement(name = "OrderItem")
public class OrderItem implements Serializable {

    private String isbn;
    private Integer quantity;

    public String getIsbn() {
	return isbn;
    }

    public void setIsbn(String isbn) {
	this.isbn = isbn;
    }

    public Integer getQuantity() {
	return quantity;
    }

    public void setQuantity(Integer quantity) {
	this.quantity = quantity;
    }

}
