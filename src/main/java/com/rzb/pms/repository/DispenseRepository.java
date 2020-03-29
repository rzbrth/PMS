package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.Dispense;

public interface DispenseRepository extends JpaRepository<Dispense, Integer>, CrudRepository<Dispense, Integer>,
		JpaSpecificationExecutor<Dispense>, QuerydslPredicateExecutor<Dispense> {

}
