package com.flxpoint.customer.controller;

import com.flxpoint.customer.exception.CustomerNotFoundException;
import com.flxpoint.customer.model.Customer;
import com.flxpoint.customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing customer-related operations.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Constructs the CustomerController with a dependency on CustomerService.
     *
     * @param service The customer service to handle the business logic.
     */
    public CustomerController(CustomerService service) {
        this.customerService = service;
    }

    /**
     * Retrieves all customers.
     *
     * @return A list of customers.
     */
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    /**
     * Retrieves a specific customer by ID.
     *
     * @param id The ID of the customer to retrieve.
     * @return A {@link ResponseEntity} with customer if found, or not found status otherwise.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found"));
    }

    /**
     * Deletes a specific customer by ID.
     *
     * @param id The ID of the customer to delete.
     * @return A {@link ResponseEntity} with an OK status on successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Creates a new customer or updates an existing one based on the presence of an ID.
     *
     * @param customer The customer data to save.
     * @return A {@link ResponseEntity} containing the saved customer.
     */
    @PostMapping
    public ResponseEntity<Customer> createCustomerOrUpdate(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.saveOrUpdateCustomer(customer));
    }

}
