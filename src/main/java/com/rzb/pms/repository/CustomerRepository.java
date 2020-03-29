package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>, CrudRepository<Customer, Integer> {

	@Query(nativeQuery = true, value = "SELECT  * FROM customer where mobile_number = ?1")
	Customer findByMobileNumber(String mobileNumber);

}
