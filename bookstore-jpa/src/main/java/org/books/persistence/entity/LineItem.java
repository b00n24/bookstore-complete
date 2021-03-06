package org.books.persistence.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@XmlRootElement(name = "lineItem")
@XmlType(propOrder = {"book", "quantity"})
public class LineItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(optional = false, cascade = {CascadeType.REFRESH, CascadeType.PERSIST})
    private Book book;

    private Integer quantity;

    public LineItem() {
    }

    public LineItem(Book book, Integer quantity) {
	this.book = book;
	this.quantity = quantity;
    }

    @XmlAttribute
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
