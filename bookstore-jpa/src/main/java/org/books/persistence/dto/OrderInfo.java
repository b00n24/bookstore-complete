package org.books.persistence.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import org.books.persistence.enums.Status;

/**
 *
 * @author AWy
 */
@XmlRootElement(name = "orderInfo")
public class OrderInfo implements Serializable {

    private Long id;
    private String number;
    private Date date;
    private BigDecimal amount;
    private Status status;

    public OrderInfo(Long id, String number, Date date, BigDecimal amount, Status status) {
	this.id = id;
	this.number = number;
	this.date = date;
	this.amount = amount;
	this.status = status;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getNumber() {
	return number;
    }

    public void setNumber(String number) {
	this.number = number;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public BigDecimal getAmount() {
	return amount;
    }

    public void setAmount(BigDecimal amount) {
	this.amount = amount;
    }

    public Status getStatus() {
	return status;
    }

    public void setStatus(Status status) {
	this.status = status;
    }

}
