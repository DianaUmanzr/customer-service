package com.flxpoint.customer.service;

import com.flxpoint.customer.model.Customer;
import com.flxpoint.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling customer operations.
 * This class manages the business logic associated with customer operations and delegates
 * database interactions to the CustomerRepository.
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CrmIntegrationService crmIntegrationService;

    /**
     * Constructs a CustomerService with necessary dependencies.
     *
     * @param repository The repository handling customer data interactions.
     * @param crmIntegrationService The service handling integration with the external CRM system.
     */
    @Autowired
    public CustomerService(CustomerRepository repository, CrmIntegrationService crmIntegrationService) {
        this.customerRepository = repository;
        this.crmIntegrationService = crmIntegrationService;
    }

    /**
     * Retrieves all customers from the repository.
     *
     * @return a list of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Retrieves a customer by ID.
     *
     * @param id the ID of the customer to retrieve
     * @return an Optional containing the customer if found, or an empty Optional if not found
     */
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Saves or updates a customer and synchronizes the update with an external CRM system.
     * This method is transactional, ensuring that both local and CRM updates are successful.
     *
     * @param customer the customer to save or update
     * @return the saved or updated customer
     */
    @Transactional
    public Customer saveOrUpdateCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        crmIntegrationService.updateCrmCustomer(savedCustomer);
        return savedCustomer;
    }

    /**
     * Deletes a customer by ID and synchronizes the deletion with the external CRM system.
     * This method is transactional, ensuring that both local and CRM deletions are successful.
     *
     * @param id the ID of the customer to delete
     */
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        crmIntegrationService.deleteCustomerFromCRM(id);
    }
}
