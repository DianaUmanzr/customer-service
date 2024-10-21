# Customer Service

This project is a demonstration of a Spring Boot-based customer management system, designed to provide RESTful APIs for CRUD operations on customer data.

## Features

- **List all customers**: Retrieve a complete list of customers.
- **Get customer details**: Fetch details of a specific customer by ID.
- **Create/update customer**: Add a new customer or update an existing customer's details.
- **Delete customer**: Remove a customer from the system by ID.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java 17 or newer
- Maven 3.6 or newer

## Getting Started

Follow these steps to get your development environment set up:

### Installation

1. **Clone the repository**:
   ```bash
   git clone git@github.com:DianaUmanzr/customer-service.git

2- **Run the application**

3- go to this url in postman and send the json body in the request

POST http://localhost:8080/api/customers

{
"customerId": 3,
"firstName": "Maria",
"lastName": "Jose",
"email": "john.doe@example.com",
"phoneNumber": "123-456-7890",
"address": {
"street": "1234 Elm St",
"city": "Springfield",
"state": "IL",
"zipCode": "62704"
}
}

4- Verify in the h2 memory database in this url:

http://localhost:8080/h2-console/login.jsp
url: jdbc:h2:mem:testdb
username: sa
password:

5- Verify customer table, all the changes realized in the CRUD are reflected in crm_customer table

6- Do the same for DELETE operation sending as a path variable the customer that you want to delete.

DELETE http://localhost:8080/api/customers/1

