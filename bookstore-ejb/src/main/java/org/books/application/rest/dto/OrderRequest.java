package org.books.application.rest.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.books.persistence.dto.OrderItem;

/**
 *
 * @author AWy
 */
@XmlRootElement(name = "orderRequest")
public class OrderRequest {

    private final Long customerId;
    private final List<OrderItem> items;

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

}
