package com.flxpoint.customer.repository;

import com.flxpoint.customer.model.CrmCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCRMRepository extends JpaRepository<CrmCustomer, Long> {
}
