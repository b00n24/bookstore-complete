package org.books.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-01-14T19:37:16")
@StaticMetamodel(Customer.class)
public class Customer_ { 

    public static volatile SingularAttribute<Customer, String> firstName;
    public static volatile SingularAttribute<Customer, String> lastName;
    public static volatile SingularAttribute<Customer, Address> address;
    public static volatile SingularAttribute<Customer, Long> id;
    public static volatile SingularAttribute<Customer, CreditCard> creditCard;
    public static volatile SingularAttribute<Customer, String> email;

}