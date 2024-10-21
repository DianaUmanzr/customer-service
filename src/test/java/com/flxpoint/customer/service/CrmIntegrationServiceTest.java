package com.flxpoint.customer.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import com.flxpoint.customer.exception.CrmCommunicationException;
import com.flxpoint.customer.exception.CustomerNotFoundException;
import com.flxpoint.customer.exception.InvalidCustomerDataException;
import com.flxpoint.customer.model.Address;
import com.flxpoint.customer.model.CrmCustomer;
import com.flxpoint.customer.model.Customer;
import com.flxpoint.customer.repository.CustomerCRMRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class CrmIntegrationServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CrmIntegrationService.class);

    @Mock
    private CustomerCRMRepository crmRepository;

    @InjectMocks
    private CrmIntegrationService crmIntegrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUpdateCrmCustomerThrowsInvalidCustomerDataException() {
        Customer customer = new Customer();
        customer.setEmail("");

        assertThrows(InvalidCustomerDataException.class, () -> crmIntegrationService.updateCrmCustomer(customer));
    }

    @Test
    void testUpdateCrmCustomerSuccessful() {
        Customer customer = new Customer();
        Address address = new Address();
        customer.setCustomerId(1L);
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        address.setCity("Metropolis");
        address.setStreet("123 Main St");
        address.setState("NY");
        address.setZipCode("12345");
        customer.setAddress(address);

        CrmCustomer crmCustomer = new CrmCustomer(1L, "John Doe", "test@example.com", "1234567890", "123 Main St, Metropolis, NY, 12345");
        when(crmRepository.save(any(CrmCustomer.class))).thenReturn(crmCustomer);

        assertDoesNotThrow(() -> crmIntegrationService.updateCrmCustomer(customer));
        verify(crmRepository, times(1)).save(any(CrmCustomer.class));
    }

    @Test
    void updateCrmCustomer_ThrowsInvalidCustomerDataException_IfEmailIsEmpty() {
        Customer customer = new Customer();
        customer.setEmail("");

        assertThrows(InvalidCustomerDataException.class, () -> crmIntegrationService.updateCrmCustomer(customer));
    }


    @Test
    void deleteCustomerFromCRM_ThrowsCustomerNotFoundException_IfCustomerDoesNotExist() {
        long customerId = 1L;
        when(crmRepository.existsById(customerId)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> crmIntegrationService.deleteCustomerFromCRM(customerId));
    }

    @Test
    void deleteCustomerFromCRM_ThrowsCrmCommunicationException_OnDeleteFailure() {
        long customerId = 1L;
        when(crmRepository.existsById(customerId)).thenReturn(true);
        doThrow(RuntimeException.class).when(crmRepository).deleteById(customerId);

        assertThrows(CrmCommunicationException.class, () -> crmIntegrationService.deleteCustomerFromCRM(customerId));
    }

}
