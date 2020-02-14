package com.rzb.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer>, CrudRepository<Stock, Integer> {

	@Query(value = "select location from  stock where drug_id = ?1 ", nativeQuery = true)
	String[] findLocationByDrugId(String drugId);

	@Query(value = "select * from  stock where drug_id = ?1 and avl_qnty_trimmed >= ?2 order by expiry_date asc FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	Stock findStockWithTrimmedQnty(String drugId, Double avlQntyTrimmed);

	@Query(value = "select * from  stock where drug_id = ?1 and avl_qnty_whole >= ?2 order by expiry_date asc FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	Stock findStockWithWholeQnty(String drugId, Double avlQntyWhole);

	@Query(nativeQuery = true, value = "select avl_qnty_whole, location from stock where drug_id = ?1 and current_date < expiry_date order by expiry_date asc")
	List<Object[]> findByDrugId(String drugId);
}
