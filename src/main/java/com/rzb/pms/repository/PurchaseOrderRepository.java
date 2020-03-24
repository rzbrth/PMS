package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository
		extends CrudRepository<PurchaseOrder, Integer>, JpaRepository<PurchaseOrder, Integer>,
		JpaSpecificationExecutor<PurchaseOrder>, QuerydslPredicateExecutor<PurchaseOrder> {

	@Query(nativeQuery = true, value = "SELECT CASE WHEN EXISTS (SELECT * FROM purchase_order WHERE distributer_id =?1 and po_status = 'PENDING')"
			+ " THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) end")
	Boolean checkPoStatus(Integer distributerId);

	@Query(nativeQuery = true, value = "SELECT CASE WHEN EXISTS (\r\n" + 
			"    SELECT *\r\n" + 
			"    FROM po_line_items\r\n" + 
			"    WHERE distributer_id =?1 and drug_id = ?2 and drug_quantity =?3\r\n" + 
			")\r\n" + 
			"THEN CAST(1 AS BIT)\r\n" + 
			"ELSE CAST(0 AS BIT) end")
	Boolean findPoExistOrNot(Integer distributerId, String drugId, Double drugQuantity);

}
