package org.books.persistence.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.Validate;
import org.books.persistence.QueryUtil;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Login;

public class CustomerRepository {

    private final EntityManager em;

    public CustomerRepository(EntityManager em) {
	this.em = em;
    }

    public List<CustomerInfo> searchCustomersByName(String name) {
	Validate.notNull(name, "'name' darf nicht null sein");
	Validate.notEmpty(name, "'name' darf nicht leer sein");

	TypedQuery<CustomerInfo> query = em.createNamedQuery(Customer.QUERY_BY_NAME, CustomerInfo.class);
	query.setParameter(Customer.PARAM_NAME, QueryUtil.convertToLikeValueAndLowerCase(name));

	return query.getResultList();
    }

    public Customer findByMail(String email) {
	Validate.notNull(email, "'email' darf nicht null sein");
	Validate.notEmpty(email, "'email' darf nicht leer sein");
	TypedQuery<Customer> query = em.createNamedQuery(Customer.QUERY_BY_EMAIL, Customer.class);
	query.setParameter(Customer.PARAM_EMAIL, email.toLowerCase());
	Customer result = null;
	try {
	    result = query.getSingleResult();
	} catch (NoResultException ex) {
	    // Ignore no Result Exception
	}
	return result;
    }

    public Login findLoginByUserName(String userName) {
	Validate.notNull(userName, "'userName' darf nicht null sein");
	Validate.notEmpty(userName, "'userName' darf nicht leer sein");
	TypedQuery<Login> query = em.createNamedQuery(Login.QUERY_BY_NAME, Login.class);
	query.setParameter(Login.PARAM_NAME, userName.toLowerCase());

	Login result = null;
	try {
	    result = query.getSingleResult();
	} catch (NoResultException ex) {
	    // Ignore no Result Exception
	}
	return result;
    }

    public void update(Login login) {
	em.merge(login);
    }

    public void update(Customer customer) {
	em.merge(customer);
    }

    public Customer findById(Long customerId) {
	return em.find(Customer.class, customerId);
    }

    public void persist(Login login) {
	em.persist(login);
    }

    public void persist(Customer customer) {
	em.persist(customer);
    }

    // TODO SIR flush hier implementieren?! ist bei register wohl nötig um id zurückgeben zu können....
    public void flush() {
	em.flush();
    }
}
