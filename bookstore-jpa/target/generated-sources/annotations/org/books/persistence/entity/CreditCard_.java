package org.books.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.enums.Type;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-01-14T19:37:16")
@StaticMetamodel(CreditCard.class)
public class CreditCard_ { 

    public static volatile SingularAttribute<CreditCard, Integer> expirationYear;
    public static volatile SingularAttribute<CreditCard, String> number;
    public static volatile SingularAttribute<CreditCard, Long> id;
    public static volatile SingularAttribute<CreditCard, Integer> expirationMonth;
    public static volatile SingularAttribute<CreditCard, Type> type;

}