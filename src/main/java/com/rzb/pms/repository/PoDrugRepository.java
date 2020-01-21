package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.PoDrug;

public interface PoDrugRepository extends CrudRepository<PoDrug, Integer>, JpaRepository<PoDrug, Integer> {

}
