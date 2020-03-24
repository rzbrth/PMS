package com.rzb.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.Stock;
import com.rzb.pms.projection.StockProjection;

@Repository
public interface StockRepository extends CrudRepository<Stock, Integer>, JpaRepository<Stock, Integer>,
		JpaSpecificationExecutor<Stock>, QuerydslPredicateExecutor<Stock> {

	@Query(value = "select location from  stock where drug_id = ?1 ", nativeQuery = true)
	String[] findLocationByDrugId(String drugId);

	@Query(value = "select * from  stock where drug_id = ?1 and avl_qnty_trimmed >= ?2 order by expiry_date asc FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	Stock findStockWithTrimmedQnty(String drugId, Double avlQntyTrimmed);

	@Query(value = "select * from  stock where drug_id = ?1 and avl_qnty_whole >= ?2 order by expiry_date asc FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
	Stock findStockWithWholeQnty(String drugId, Double avlQntyWhole);

	@Query(nativeQuery = true, value = "select stock_Id, avl_qnty_whole, location  from stock where drug_id = ?1 and current_date < expiry_date order by expiry_date asc")
	List<Object[]> findByDrugId(String drugId);

	@Query(nativeQuery = true, value = "select stock_Id, drug_name, expiry_date  from stock order by expiry_date asc FETCH FIRST 4 ROWS ONLY")
	List<StockProjection> findTopDrugAboutToExpire();

	@Query(nativeQuery = true, value = "select * from stock where po_id = ?1")
	List<Stock> findByPoId(Integer poId);
}
