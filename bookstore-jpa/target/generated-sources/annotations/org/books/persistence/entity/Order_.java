package org.books.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.LineItem;
import org.books.persistence.enums.Status;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-01-14T19:37:16")
@StaticMetamodel(Order.class)
public class Order_ { 

    public static volatile SingularAttribute<Order, Date> date;
    public static volatile SingularAttribute<Order, String> number;
    public static volatile SingularAttribute<Order, BigDecimal> amount;
    public static volatile SingularAttribute<Order, Address> address;
    public static volatile SingularAttribute<Order, Long> id;
    public static volatile SingularAttribute<Order, CreditCard> creditCard;
    public static volatile ListAttribute<Order, LineItem> items;
    public static volatile SingularAttribute<Order, Status> status;
    public static volatile SingularAttribute<Order, Customer> customer;

}