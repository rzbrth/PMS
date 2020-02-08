package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.Distributer;

@Repository
public interface DistributerRepository
		extends JpaRepository<Distributer, Integer>, CrudRepository<Distributer, Integer> {

}
