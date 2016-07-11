package net.icdpublishing.exercise2.myapp;



import net.icdpublishing.exercise2.myapp.charging.services.ImaginaryChargingService;
import net.icdpublishing.exercise2.myapp.customers.dao.CustomerNotFoundException;
import net.icdpublishing.exercise2.myapp.customers.dao.HardcodedListOfCustomersImpl;
import net.icdpublishing.exercise2.myapp.customers.domain.Customer;
import net.icdpublishing.exercise2.myapp.customers.domain.CustomerType;
import net.icdpublishing.exercise2.searchengine.domain.Address;
import net.icdpublishing.exercise2.searchengine.domain.Person;
import net.icdpublishing.exercise2.searchengine.domain.Record;
import net.icdpublishing.exercise2.searchengine.domain.SourceType;
import net.icdpublishing.exercise2.searchengine.requests.SimpleSurnameAndPostcodeQuery;
import net.icdpublishing.exercise2.searchengine.services.SearchEngineRetrievalService;
import org.junit.Before;

import org.junit.Test;
import org.junit.Assert;
import java.util.*;

import static org.mockito.Mockito.*;


public class MySearchControllerTest {

	private MySearchController controller;
	private SearchEngineRetrievalService retrievalService;
    private Customer customer;
    private SearchRequest request;
    private SimpleSurnameAndPostcodeQuery query;
    private ImaginaryChargingService chargingService;
    private HardcodedListOfCustomersImpl customerImp;
    private Person p;
    private Address address2;
    private Collection<Record> unsortedEngineInfo;
    private Set<SourceType> sources;

	@Before
	public void setUp() throws Exception {

		// setUp will be executed multiple times (for each @Test method)
		// if controller and customers was initialized, skip next initialization requests
		synchronized (MySearchControllerTest.class) {
			if (controller != null) {
				return;
			}
		}
        retrievalService = mock(SearchEngineRetrievalService.class);
        customer = mock(Customer.class);
        chargingService = mock(ImaginaryChargingService.class);
        customerImp = mock(HardcodedListOfCustomersImpl.class);

		// TODO proper way is to initialise that using @Inject
		controller = new MySearchController(retrievalService);
        controller.setChargingService(chargingService);
        controller.setCustomerImp(customerImp);

        unsortedEngineInfo = new LinkedList<Record>();
        p = new Person();
        p.setForename("Mary");
        p.setMiddlename("Ann");
        p.setSurname("Smith");
        p.setTelephone("07702811339");

        address2 = new Address();
        address2.setBuildnumber("13");
        address2.setPostcode("sw6 2bq");
        address2.setStreet("william morris way");
        address2.setTown("London");
        p.setAddress(address2);

        sources = new HashSet<SourceType>();
        query = new SimpleSurnameAndPostcodeQuery("Mary", "sw6 2bq");
        request = new SearchRequest(query, customer);
	}

	@Test
	public void testThatOnlyBTReturnedForNonPayingCustomer() throws Exception {

        // testing free information result from Search Engine

        sources.add(SourceType.BT);
        Record r1 = new Record(p,sources);
        unsortedEngineInfo.add(r1);


        when(retrievalService.search(query)).thenReturn(unsortedEngineInfo);
        when(customer.getCustomType()).thenReturn(CustomerType.NON_PAYING);
        String customerEmail = "James@gmail.com";
        when(customer.getEmailAddress()).thenReturn(customerEmail);
        when(customerImp.findCustomerByEmailAddress(customerEmail)).thenReturn(new Customer());
        Collection<Record> records = controller.handleRequest(request);
        Assert.assertEquals("Should return only BT information", unsortedEngineInfo, records);
        // clearing of test data
        sources.clear();
        unsortedEngineInfo.clear();

        // testing premium result(BT, DNB, ELECTORAL_ROLL) from Search Engine
        sources.add(SourceType.BT);
        sources.add(SourceType.DNB);
        sources.add(SourceType.ELECTORAL_ROLL);
        r1 = new Record(p,sources);
        unsortedEngineInfo.add(r1);


        records = controller.handleRequest(request);
        Assert.assertNull("Should return NULL, because BT is not exclusive data source", records);

        sources.clear();
        unsortedEngineInfo.clear();

        // testing premium result(DNB) from Search Engine

        sources.add(SourceType.DNB);
        r1 = new Record(p, sources);

        records = controller.handleRequest(request);
        Assert.assertNull("Should return NULL, because DNB is not allowed data source for non_paying", records);

        sources.clear();
        unsortedEngineInfo.clear();

	}

	@Test
	public void testThatAnyDataSourceIsAccessibleByPremiumCustomers() throws Exception {

        // testing premium data(BT, DNB, ELECTORAL_ROLL) from Search Engine

        sources.add(SourceType.BT);
        sources.add(SourceType.DNB);
        sources.add(SourceType.ELECTORAL_ROLL);
        Record r = new Record(p,sources);
        unsortedEngineInfo.add(r);

        String customerEmail = "James@gmail.com";
        when(customer.getEmailAddress()).thenReturn(customerEmail);
        when(customerImp.findCustomerByEmailAddress(customerEmail)).thenReturn(new Customer());
        when(retrievalService.search(query)).thenReturn(unsortedEngineInfo);
        when(customer.getCustomType()).thenReturn(CustomerType.PREMIUM);
        Assert.assertEquals("Should return all information, because customer is Premium",
                unsortedEngineInfo, controller.handleRequest(request));
        sources.clear();
        unsortedEngineInfo.clear();

        // testing only BT data result from MySearchController

        sources.add(SourceType.BT);
        r = new Record(p,sources);
        unsortedEngineInfo.add(r);

        Assert.assertEquals("Should return BT information, because customer is Premium",
                unsortedEngineInfo, controller.handleRequest(request));
        sources.clear();
        unsortedEngineInfo.clear();

	}

    @Test
    public void testThatPremiumCustomerIsChargedForPaidInformation() throws Exception {

        String customerEmail = "Alfred2016@gmail.com";

       // testing free information result from Search Engine
        sources.add(SourceType.BT);
        Record r1 = new Record(p,sources);
        unsortedEngineInfo.add(r1);

        when(retrievalService.search(query)).thenReturn(unsortedEngineInfo);
        when(customer.getCustomType()).thenReturn(CustomerType.PREMIUM);
        when(customer.getEmailAddress()).thenReturn(customerEmail);


        when(customerImp.findCustomerByEmailAddress(customerEmail)).thenReturn(new Customer());
        controller.handleRequest(request);
        verify(chargingService).charge(customerEmail, 0);

        // clearing of test data
        sources.clear();
        unsortedEngineInfo.clear();
        reset(chargingService);

        // testing mixed information result from Search Engine
        sources.add(SourceType.BT);
        sources.add(SourceType.DNB);
        sources.add(SourceType.ELECTORAL_ROLL);
        r1 = new Record(p,sources);
        unsortedEngineInfo.add(r1);

        controller.handleRequest(request);
        verify(chargingService).charge(customerEmail, 1);

        // clearing of test data
        sources.clear();
        unsortedEngineInfo.clear();
        reset(chargingService);

        // testing paid information result from Search Engine

        sources.add(SourceType.DNB);
        sources.add(SourceType.ELECTORAL_ROLL);
        r1 = new Record(p,sources);
        unsortedEngineInfo.add(r1);

        //add test comment to file
        controller.handleRequest(request);
        verify(chargingService).charge(customerEmail, 1);

        // clearing of test data
        sources.clear();
        unsortedEngineInfo.clear();
        reset(chargingService);

    }

    @Test(expected = CustomerNotFoundException.class)
    public void testThatCustomerIsNotFoundAndExceptionIsReceived() throws Exception {


        sources.add(SourceType.BT);
        Record r1 = new Record(p,sources);
        unsortedEngineInfo.add(r1);
        String customerEmail = "James@gmail.com";

        when(retrievalService.search(query)).thenReturn(unsortedEngineInfo);
        when(customer.getCustomType()).thenReturn(CustomerType.NON_PAYING);
        when(customer.getEmailAddress()).thenReturn(customerEmail);
        doThrow(new CustomerNotFoundException("Customer was not found")).when(customerImp).findCustomerByEmailAddress(customerEmail);
        controller.handleRequest(request);
        sources.clear();
        unsortedEngineInfo.clear();
    }
}
