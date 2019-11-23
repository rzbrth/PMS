package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.DrugPharma;

@Repository
public interface DrugPharmaRepository extends JpaRepository<DrugPharma, String>, CrudRepository<DrugPharma, String>{

}
