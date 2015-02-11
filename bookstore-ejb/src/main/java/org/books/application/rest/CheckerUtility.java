package org.books.application.rest;

import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;

/**
 *
 * @author AWy
 */
public class CheckerUtility {

    public static boolean check(Customer customer) {
	return isNotNullAndNotEmpty(customer.getFirstName())
		&& isNotNullAndNotEmpty(customer.getLastName())
		&& isNotNullAndNotEmpty(customer.getEmail())
		&& CheckerUtility.check(customer.getAddress())
		&& CheckerUtility.check(customer.getCreditCard());
    }

    public static boolean isNotNullAndNotEmpty(String s) {
	return s == null || s.isEmpty();
    }

    public static boolean check(Address obj) {
	return isNotNullAndNotEmpty(obj.getCity())
		&& isNotNullAndNotEmpty(obj.getCountry())
		&& isNotNullAndNotEmpty(obj.getPostalCode())
		&& isNotNullAndNotEmpty(obj.getStreet());
    }

    public static boolean check(CreditCard obj) {
	return isNotNullAndNotEmpty(obj.getNumber())
		&& obj.getExpirationMonth() != null
		&& obj.getExpirationYear() != null
		&& obj.getType() != null;
    }

    public static boolean check(Order order) {
	return CheckerUtility.check(order.getAddress())
		&& order.getAmount() != null
		&& CheckerUtility.check(order.getCreditCard())
		&& CheckerUtility.check(order.getCustomer())
		&& order.getDate() != null
		&& !order.getItems().isEmpty()
		&& CheckerUtility.isNotNullAndNotEmpty(order.getNumber())
		&& order.getStatus() != null;
    }
}
