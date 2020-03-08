package com.rzb.pms.repository;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.model.Drug;

@Repository
public interface DrugRepository extends JpaRepository<Drug, String>, JpaSpecificationExecutor<Drug>,
		CrudRepository<Drug, String>, QuerydslPredicateExecutor<Drug> {

	List<Drug> findByGenericId(String genericId);

	@Query(value = "select * from DRUG  where brand_name = ?1", nativeQuery = true)
	DrugDTO findByBrandName(@Valid String brandName);

}
