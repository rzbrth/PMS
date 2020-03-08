package com.rzb.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.PoLineItems;

public interface PoLineItemsRepository extends CrudRepository<PoLineItems, Integer>, JpaRepository<PoLineItems, Integer> {

	@Query(value = "select * from po_line_items where po_id = ?1", nativeQuery = true)
	List<PoLineItems> findByPoId(Integer poId);

}
