package org.books.persistence.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.books.persistence.enums.Type;

@Entity
public class CreditCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String number;

    private Integer expirationMonth;

    private Integer expirationYear;

    public CreditCard() {
    }

    public CreditCard(Type type, String number, Integer expirationMonth, Integer expirationYear) {
	this.type = type;
	this.number = number;
	this.expirationMonth = expirationMonth;
	this.expirationYear = expirationYear;
    }
    
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }
    
    public Type getType() {
	return type;
    }

    public void setType(Type type) {
	this.type = type;
    }

    public String getNumber() {
	return number;
    }

    public void setNumber(String number) {
	this.number = number;
    }

    public Integer getExpirationMonth() {
	return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
	this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
	return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
	this.expirationYear = expirationYear;
    }

    @Override
    public String toString() {
	return "CreditCard{" + "id=" + id + ", type=" + type + ", number=" + number + ", expirationMonth=" + expirationMonth + ", expirationYear=" + expirationYear + '}';
    }
    
}
