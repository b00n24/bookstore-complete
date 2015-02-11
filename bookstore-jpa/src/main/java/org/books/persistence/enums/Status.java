package org.books.persistence.enums;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author AWy
 */
// Enum bewusst nicht in Order implementiert, da wir es schöner finden, wenn die
// Enums asugelagert sind.
@XmlRootElement(name = "OrderStatus")
public enum Status {

    accepted, processing, shipped, canceled;
}
