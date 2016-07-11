package net.icdpublishing.exercise2.myapp;

import java.util.*;
import net.icdpublishing.exercise2.myapp.charging.services.ImaginaryChargingService;
import net.icdpublishing.exercise2.myapp.customers.dao.CustomerNotFoundException;
import net.icdpublishing.exercise2.myapp.customers.dao.HardcodedListOfCustomersImpl;
import net.icdpublishing.exercise2.myapp.customers.domain.Customer;
import net.icdpublishing.exercise2.myapp.customers.domain.CustomerType;
import net.icdpublishing.exercise2.searchengine.domain.Record;
import net.icdpublishing.exercise2.searchengine.domain.SourceType;
import net.icdpublishing.exercise2.searchengine.requests.SimpleSurnameAndPostcodeQuery;
import net.icdpublishing.exercise2.searchengine.services.SearchEngineRetrievalService;

public class MySearchController {
	private SearchEngineRetrievalService retrievalService;
	private ImaginaryChargingService chargingService;
    private HardcodedListOfCustomersImpl customerImp;

    public ImaginaryChargingService getChargingService() {
        return chargingService;
    }

    public void setChargingService(ImaginaryChargingService chargingService) {
        this.chargingService = chargingService;
    }

    public HardcodedListOfCustomersImpl getCustomerImp() {
        return customerImp;
    }

    public void setCustomerImp(HardcodedListOfCustomersImpl customerImp) {
        this.customerImp = customerImp;
    }

    public MySearchController(SearchEngineRetrievalService retrievalService) {
		this.retrievalService = retrievalService;

	}
	
	public Collection<Record> handleRequest(SearchRequest request) throws CustomerNotFoundException{
		Collection<Record> resultSet = new LinkedList<Record>();
        Collection<Record> dirtySet   = getResults(request.getQuery());
        Customer customer = request.getCustomer();
        if(customerImp.findCustomerByEmailAddress(customer.getEmailAddress())==null) {
            throw new CustomerNotFoundException("Customer was not found");
        }

            if(customer.getCustomType()== CustomerType.NON_PAYING)
            {
                for(Record r : dirtySet){

                    if(r.getSourceTypes().size() == 1 && r.getSourceTypes().iterator().next() == SourceType.BT)
                    {
                        resultSet.add(r);
                    }
                }
            }
            else {

                resultSet = dirtySet;
                int countPoints = 0;
                for(Record r : resultSet){

                    if(!(r.getSourceTypes().size() == 1 && r.getSourceTypes().contains(SourceType.BT)))
                    {
                        countPoints++;
                    }
                }

                chargingService.charge(customer.getEmailAddress(), countPoints);
            }
        if(resultSet.isEmpty())
        {
            resultSet = null;
        }

        return resultSet;
	}
	
	private Collection<Record> getResults(SimpleSurnameAndPostcodeQuery query) {

       Collection<Record> result = retrievalService.search(query);

        Collections.sort((List)result, new Comparator<Record>(){
            public int compare(Record o1, Record o2){
                  return o1.getPerson().getSurname().compareTo(o2.getPerson().getSurname());
            }
        });

		return result;
	}


}