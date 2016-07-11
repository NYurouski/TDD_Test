package net.icdpublishing.exercise2.myapp.customers.dao;

import static org.junit.Assert.*;

import net.icdpublishing.exercise2.myapp.customers.domain.Customer;
import net.icdpublishing.exercise2.myapp.customers.domain.CustomerType;

import org.junit.Before;
import org.junit.Test;

public class HardcodedListOfCustomersImplTest {

	private HardcodedListOfCustomersImpl customerImpl;
	
	@Before
	public void setUp() throws Exception {
		customerImpl = new HardcodedListOfCustomersImpl();
	}

	@Test
	public void shouldReturnExpectedCustomer() {
		Customer customer = customerImpl.findCustomerByEmailAddress("john.doe@123.com");
		assertEquals(customer, getExpectedCustomer());
	}

	@Test(expected=CustomerNotFoundException.class)
	public void shouldThrowCustomerNotFoundException() {
		customerImpl.findCustomerByEmailAddress("invalid_customer@123.com");
	}
	
	private Customer getExpectedCustomer() {
		Customer expectedCustomer = new Customer();
		expectedCustomer.setEmailAddress("john.doe@123.com");
		expectedCustomer.setForename("John");
		expectedCustomer.setSurname("Doe");
		expectedCustomer.setCustomType(CustomerType.PREMIUM);
		return expectedCustomer;
	}
}