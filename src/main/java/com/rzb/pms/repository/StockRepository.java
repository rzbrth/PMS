package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer>, CrudRepository<Stock, Integer> {

}
