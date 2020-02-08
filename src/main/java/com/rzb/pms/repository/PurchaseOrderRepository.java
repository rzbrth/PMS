package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.PurchaseOrderLineItems;

@Repository
public interface PurchaseOrderRepository
		extends CrudRepository<PurchaseOrderLineItems, Integer>, JpaRepository<PurchaseOrderLineItems, Integer>,
		JpaSpecificationExecutor<PurchaseOrderLineItems>, QuerydslPredicateExecutor<PurchaseOrderLineItems> {

}
