package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.PurchaseOrder;

public interface PoDrugRepository extends CrudRepository<PurchaseOrder, Integer>, JpaRepository<PurchaseOrder, Integer> {

}
