package net.icdpublishing.exercise2.myapp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import net.icdpublishing.exercise2.myapp.customers.domain.Customer;
import net.icdpublishing.exercise2.searchengine.requests.SimpleSurnameAndPostcodeQuery;

public class SearchRequest {
	private SimpleSurnameAndPostcodeQuery query;
	private Customer customer;
	
	public SearchRequest(SimpleSurnameAndPostcodeQuery query, Customer customer) {
		this.query = query;
		this.customer = customer;
	}

	public SimpleSurnameAndPostcodeQuery getQuery() {
		return query;
	}

	public Customer getCustomer() {
		return customer;
	}
	
	@Override
	public boolean equals(Object obj) {	
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {		
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}