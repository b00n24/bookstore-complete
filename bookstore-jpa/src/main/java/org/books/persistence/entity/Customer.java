package org.books.persistence.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@NamedQueries({
    @NamedQuery(name = Customer.QUERY_BY_EMAIL, query = "SELECT c FROM Customer c WHERE LOWER(c.email) = :" + Customer.PARAM_EMAIL),
    @NamedQuery(name = Customer.QUERY_BY_NAME, query = "SELECT NEW org.books.persistence.dto.CustomerInfo(c.id, c.firstName, c.lastName, c.email)"
	    + " FROM Customer c WHERE LOWER(c.firstName) LIKE :" + Customer.PARAM_NAME + " OR LOWER(c.lastName) LIKE :" + Customer.PARAM_NAME + " ORDER BY c.firstName")
})
@XmlRootElement(name = "customer")
@XmlType(propOrder = {"firstName", "lastName", "email", "address", "creditCard"})
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String QUERY_BY_EMAIL = "Customer.email";
    public static final String PARAM_EMAIL = "email";
    public static final String QUERY_BY_NAME = "Customer.name";
    public static final String PARAM_NAME = "name";

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private CreditCard creditCard;

    public Customer() {
	this.address = new Address();
	this.creditCard = new CreditCard();
    }

    public Customer(String firstName, String lastName, String email, Address address, CreditCard creditCard) {
	this.firstName = firstName;
	this.lastName = lastName;
	this.email = email;
	this.address = address;
	this.creditCard = creditCard;
    }

    @XmlAttribute
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public Address getAddress() {
	return address;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

    public CreditCard getCreditCard() {
	if (creditCard == null) {
	    creditCard = new CreditCard();
	}
	return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
	this.creditCard = creditCard;
    }

    @Override
    public String toString() {
	return "Customer{" + "id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", address=" + address + ", creditCard=" + creditCard + '}';
    }
}
