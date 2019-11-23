package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.Drugs;

@Repository
public interface DrugRepository extends JpaRepository<Drugs, String>, CrudRepository<Drugs, String>{

}
