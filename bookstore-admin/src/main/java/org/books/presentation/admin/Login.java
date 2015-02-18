package org.books.presentation.admin;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.DatatypeConverter;

@Entity
@Table(name = "LOGIN")
@NamedQuery(name = Login.findByUserNameQuery, query = "SELECT x FROM Login x WHERE LOWER(x.userName) = LOWER(:userName)")
public class Login implements Serializable {

    public static final String findByUserNameQuery = "Login.findByUserNameQuery";

    @Id
    @GeneratedValue
    @Column(name = "ID")
    protected Long id;
    @Column(nullable = false, unique = true)
    private String userName;
    private String password;
    private String userGroup;

    public Login() {
    }

    public Login(String userName, String password) {
	this.userName = userName;
	setPassword(password);
	this.userGroup = "employee";
    }

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
	this.password = convertToSha256Hash(password);
    }

    public String getUserGroup() {
	return userGroup;
    }

    public void setUserGroup(String userGroup) {
	this.userGroup = userGroup;
    }
    
    private String convertToSha256Hash(String plain) {
	try {
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    byte[] hash = digest.digest(plain.getBytes("UTF-8"));
	    return DatatypeConverter.printHexBinary(hash);
	} catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
	    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Could not encrypt password.", ex);
	    throw new IllegalArgumentException();
	}
    }
}
