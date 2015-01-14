package org.books.persistence.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LineItem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne(optional = false)
    private Book book;
    
    private Integer quantity;

    public LineItem() {
    }

    public LineItem(Book book, Integer quantity) {
	this.book = book;
	this.quantity = quantity;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }
    
    public Book getBook() {
	return book;
    }

    public void setBook(Book book) {
	this.book = book;
    }

    public Integer getQuantity() {
	return quantity;
    }

    public void setQuantity(Integer quantity) {
	this.quantity = quantity;
    }

    @Override
    public String toString() {
	return "LineItem{" + "book=" + book + ", quantity=" + quantity + '}';
    }
}
