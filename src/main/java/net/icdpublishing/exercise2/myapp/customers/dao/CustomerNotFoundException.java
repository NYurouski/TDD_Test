package net.icdpublishing.exercise2.myapp.customers.dao;

public class CustomerNotFoundException extends RuntimeException {

	public CustomerNotFoundException(String message) {
		super(message);
	}
}
