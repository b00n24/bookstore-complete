package org.books.persistence.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author AWy
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Login.QUERY_BY_NAME, query = "SELECT l FROM Login l WHERE LOWER(l.userName) = :" + Login.PARAM_NAME)
})
@XmlRootElement(name = "login")
@XmlType(propOrder = {"userName", "password"})
public class Login implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String QUERY_BY_NAME = "Login.name";
    public static final String PARAM_NAME = "name";

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    private String password;

    @XmlAttribute
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    @Override
    public String toString() {
	return "Login{" + "id=" + id + ", userName=" + userName + ", password=" + password + '}';
    }

}
