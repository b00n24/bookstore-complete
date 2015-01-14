package org.books.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.entity.Book;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-01-14T19:37:16")
@StaticMetamodel(LineItem.class)
public class LineItem_ { 

    public static volatile SingularAttribute<LineItem, Integer> quantity;
    public static volatile SingularAttribute<LineItem, Book> book;
    public static volatile SingularAttribute<LineItem, Long> id;

}