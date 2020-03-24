package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.ExpiredItemReturn;

@Repository
public interface ExpiredItemReturnRepository
		extends JpaRepository<ExpiredItemReturn, Integer>, CrudRepository<ExpiredItemReturn, Integer> {

}
