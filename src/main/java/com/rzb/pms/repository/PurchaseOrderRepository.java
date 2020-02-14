package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository
		extends CrudRepository<PurchaseOrder, Integer>, JpaRepository<PurchaseOrder, Integer>,
		JpaSpecificationExecutor<PurchaseOrder>, QuerydslPredicateExecutor<PurchaseOrder> {

}
