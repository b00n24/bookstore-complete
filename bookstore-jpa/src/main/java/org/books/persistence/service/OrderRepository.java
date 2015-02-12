package org.books.persistence.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;

public class OrderRepository {

    private final EntityManager em;

    public OrderRepository(EntityManager em) {
	this.em = em;
    }

    public Order findById(Long id) {
	Order order = em.find(Order.class, id);
	if (order != null) {
	    em.refresh(order);
	}
	return order;
    }

    public Order findByNumber(String number) {
	TypedQuery<Order> query = em.createNamedQuery(Order.QUERY_BY_NUMBER, Order.class);
	query.setParameter(Order.PARAM_NUMBER, number.toLowerCase());

	Order result = null;
	try {
	    result = query.getSingleResult();
	} catch (NoResultException ex) {
	    // Ignore no Result Exception
	}
	return result;
    }

    public List<OrderInfo> getOrders(Customer customer, int year) {
	Calendar cal = Calendar.getInstance();

	// First of year
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	cal.set(Calendar.DAY_OF_YEAR, 1);
	cal.set(Calendar.YEAR, year);
	Date firstOfYear = cal.getTime();

	// Last of year
	cal.set(Calendar.HOUR_OF_DAY, 23);
	cal.set(Calendar.MINUTE, 59);
	cal.set(Calendar.SECOND, 59);
	cal.set(Calendar.MILLISECOND, 999);
	cal.set(Calendar.MONTH, 11); // 11 = december
	cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
	Date lastOfYear = cal.getTime();

	TypedQuery<OrderInfo> query = em.createNamedQuery(Order.QUERY_BY_CUSTOMER_AND_YEAR, OrderInfo.class);
	query.setParameter(Order.PARAM_CUSTOMER_ID, customer.getId());
	query.setParameter(Order.PARAM_FIRST_OF_YEAR, firstOfYear);
	query.setParameter(Order.PARAM_LAST_OF_YEAR, lastOfYear);

	return query.getResultList();
    }

    public void update(Order order) {
	em.merge(order);
    }

    public void persist(Order order) {
	em.persist(order);
    }
}
