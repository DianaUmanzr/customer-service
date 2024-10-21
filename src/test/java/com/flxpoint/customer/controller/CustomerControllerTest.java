package com.flxpoint.customer.controller;

import com.flxpoint.customer.exception.CustomerNotFoundException;
import com.flxpoint.customer.model.Address;
import com.flxpoint.customer.model.Customer;
import com.flxpoint.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    Customer customer = new Customer();
    Address address = new Address();

    private Customer createTestCustomer() {
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("+1234567890");
        address.setCity("Metropolis");
        address.setStreet("123 Main St");
        address.setState("NY");
        address.setZipCode("12345");
        customer.setAddress(address);
        return customer;
    }

    @Test
    public void getAllCustomers_ReturnsCustomerList() throws Exception {
        Customer customer = createTestCustomer();

        given(customerService.getAllCustomers()).willReturn(Arrays.asList(customer));

        mockMvc.perform(get("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    public void getCustomerById_CustomerFound_ReturnsCustomer() throws Exception {
        Customer customer = createTestCustomer();

        given(customerService.getCustomerById(1L)).willReturn(Optional.of(customer));

        mockMvc.perform(get("/api/customers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void getCustomerById_CustomerNotFound_ThrowsException() throws Exception {
        given(customerService.getCustomerById(1L)).willThrow(new CustomerNotFoundException("Customer with ID 1 not found"));

        mockMvc.perform(get("/api/customers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomerNotFoundException))
                .andExpect(result -> assertEquals("Customer with ID 1 not found", result.getResolvedException().getMessage()));
    }

    @Test
    public void createCustomerOrUpdate_CustomerCreated_ReturnsCustomer() throws Exception {

        Customer customer = createTestCustomer();

        given(customerService.saveOrUpdateCustomer(any(Customer.class))).willReturn(customer);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void deleteCustomer_CustomerDeleted_ReturnsOk() throws Exception {
        willDoNothing().given(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
