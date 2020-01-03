package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String>, CrudRepository<Customer, String> {

}
