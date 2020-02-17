package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.PoLineItems;

public interface PoLineItemsRepository extends CrudRepository<PoLineItems, Integer>, JpaRepository<PoLineItems, Integer> {

}
