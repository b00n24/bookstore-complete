package org.books.persistence;

import java.util.Calendar;
import java.util.List;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Order;
import org.books.persistence.service.OrderRepository;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Silvan
 */
public class OrderRepositoryTestIT extends QueriesWithDataTest {

    OrderRepository service;
    
    @BeforeMethod
    public void init() {
	service = new OrderRepository(emf.createEntityManager());
    }

    @Test
    public void queryOrderByCustomerAndYearUtil() {
	Calendar cal = Calendar.getInstance();
	cal.setTime(orderDate);
	int year = cal.get(Calendar.YEAR);
	final List<OrderInfo> result = service.getOrders(customer, year);

	assertFalse(result.isEmpty());
    }

    @Test
    public void queryOrderByCustomerAndYearUtil_wrongYear() {
	final List<OrderInfo> result = service.getOrders(customer, 1999);

	assertTrue(result.isEmpty());
    }

    @Test
    public void queryOrderByNumber() {
	Order result = service.findByNumber(orderNumber);

	assertEquals(orderNumber, result.getNumber());
    }

    @Test
    public void queryOrderByNumberCaseInsensitiv() {
	Order result = service.findByNumber(orderNumber.toUpperCase());
	
	assertEquals(orderNumber, result.getNumber());
    }
}
