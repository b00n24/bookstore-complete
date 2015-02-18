package org.books.presentation.admin;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

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

	public Login() {
	}

	public Login(String userName, String password) {
		this.userName = userName;
		this.password = password;
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
		this.password = password;
	}
}
