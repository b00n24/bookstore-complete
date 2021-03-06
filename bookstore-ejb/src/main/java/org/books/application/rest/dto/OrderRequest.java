package org.books.application.rest.dto;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.books.persistence.dto.OrderItem;

/**
 *
 * @author AWy
 */
@XmlRootElement(name = "orderRequest")
@XmlType(propOrder = {"customerId", "items"})
public class OrderRequest implements Serializable {

    private Long customerId;
    private List<OrderItem> items;

    public OrderRequest() {

    }

    public OrderRequest(Long customerId, List<OrderItem> items) {
	this.customerId = customerId;
	this.items = items;
    }

    public Long getCustomerId() {
	return customerId;
    }

    public List<OrderItem> getItems() {
	return items;
    }

    public void setCustomerId(Long customerId) {
	this.customerId = customerId;
    }

    public void setItems(List<OrderItem> items) {
	this.items = items;
    }

}
