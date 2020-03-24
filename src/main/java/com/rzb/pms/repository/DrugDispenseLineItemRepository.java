package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.DispenseLineItems;

public interface DrugDispenseLineItemRepository
		extends JpaRepository<DispenseLineItems, Integer>, CrudRepository<DispenseLineItems, Integer> {

}
