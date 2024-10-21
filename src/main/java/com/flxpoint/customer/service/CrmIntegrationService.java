package com.flxpoint.customer.service;

import com.flxpoint.customer.exception.CrmCommunicationException;
import com.flxpoint.customer.exception.CustomerNotFoundException;
import com.flxpoint.customer.exception.InvalidCustomerDataException;
import com.flxpoint.customer.model.Address;
import com.flxpoint.customer.model.CrmCustomer;
import com.flxpoint.customer.model.Customer;
import com.flxpoint.customer.repository.CustomerCRMRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.retry.annotation.Recover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service layer responsible for CRM integration.
 * This service handles communication and data synchronization with an external CRM system.
 */
@Service
public class CrmIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(CrmIntegrationService.class);

    @Autowired
    private CustomerCRMRepository crmRepository;

    /**
     * Updates or inserts a customer record in the external CRM system.
     * Retry on failure and fall back if repeated attempts fail.
     *
     * @param customer the customer information to update in the CRM
     */
    @Retry(name = "crmUpdateRetry", fallbackMethod = "fallbackUpdate")
    public void updateCrmCustomer(Customer customer) {
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            throw new InvalidCustomerDataException("Email cannot be empty");
        }
        try {
            CrmCustomer crmCustomer = mapToCrmCustomer(customer);
            crmRepository.save(crmCustomer);
        } catch (Exception e) {
            throw new CrmCommunicationException("Failed to communicate with CRM", e);
        }
    }

    /**
     * Maps domain Customer object to CRM model.
     *
     * @param customer the customer domain model
     * @return CrmCustomer CRM model representation of the customer
     */
    private CrmCustomer mapToCrmCustomer(Customer customer) {
        String location = formatAddress(customer.getAddress());
        return new CrmCustomer(
                customer.getCustomerId(),
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                location
        );
    }

    /**
     * Fallback method for update failures, logs error and could queue the data for later retry.
     *
     * @param customer the customer that failed to update
     * @param e the exception that caused the update to fail
     */
    @Recover
    public void fallbackUpdate(Customer customer, Exception e) {
        logger.error("Recovering from CRM communication failure: {}", e.getMessage(), e);
    }

    /**
     * Attempts to delete a customer from the external CRM.
     * Retry on failure and invoke a recovery method if all retries fail.
     *
     * @param customerId the unique identifier of the customer to delete
     */
    @Retry(name = "crmServiceRetry", fallbackMethod = "recoverDeleteFailure")
    public void deleteCustomerFromCRM(Long customerId) {
        if (!crmRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found in CRM");
        }
        try {
            crmRepository.deleteById(customerId);
        } catch (Exception e) {
            throw new CrmCommunicationException("Failed to communicate with CRM during deletion", e);
        }
    }

    /**
     * Recovery method if deletion fails after retries.
     *
     * @param t the throwable error that caused the failure
     * @param customerId the ID of the customer that could not be deleted
     */
    @Recover
    public void recoverDeleteFailure(Throwable t, Long customerId) {
        logger.error("Failed to delete customer from CRM with ID {}: {}", customerId, t.getMessage(), t);
    }

    /**
     * Formats the address into a single string.
     *
     * @param address the Address object to format
     * @return a formatted string representation of the address
     */
    private String formatAddress(Address address) {
        return String.format("%s, %s, %s, %s",
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode());
    }
}
