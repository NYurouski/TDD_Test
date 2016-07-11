package net.icdpublishing.exercise2.myapp.customers.dao;

import net.icdpublishing.exercise2.myapp.customers.domain.Customer;
import net.icdpublishing.exercise2.myapp.customers.domain.CustomerType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HardcodedListOfCustomersImpl implements CustomerDao {

	private static Map<String,Customer> customers = new HashMap<String, Customer>();
	
	public HardcodedListOfCustomersImpl() {
		customers.put("john.doe@123.com", createDummyCustomer("john.doe@123.com", "John", "Doe", CustomerType.PREMIUM));
		customers.put("sally.smith@123.com", createDummyCustomer("sally.smith@123.com", "Sally", "Smith", CustomerType.PREMIUM));
		customers.put("harry.lang@123.com", createDummyCustomer("harry.smith@123.com", "Harry", "Smith", CustomerType.NON_PAYING));
	}
	
	public Customer findCustomerByEmailAddress(String email) throws CustomerNotFoundException {
		Customer customer = customers.get(email);
		if(customer == null) {
			throw new CustomerNotFoundException("Invalid customer");
		}	
		return customer;
	}

	public Optional<Customer> findOneByType(CustomerType type) {
		return customers.values().stream().filter(customer -> customer.getCustomType().equals(type)).findFirst();
	}
	
	private Customer createDummyCustomer(String email, String forename, String surname, CustomerType type) {
		Customer c = new Customer();
		c.setEmailAddress(email);
		c.setForename(forename);
		c.setSurname(surname.toLowerCase());
		c.setPostcode("sw6 2bq".toLowerCase());
		c.setCustomType(type);
		return c;
	}
}