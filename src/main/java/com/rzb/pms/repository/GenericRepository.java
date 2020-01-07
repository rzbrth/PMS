package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.Generic;

@Repository
public interface GenericRepository extends JpaRepository<Generic, String>, CrudRepository<Generic, String> {

	Generic findByNameLike(String name);

	Generic findByGenericId(String genericId);

}
