package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.GenericPharma;

@Repository
public interface GenericPharmaRepository extends JpaRepository<GenericPharma, String>, CrudRepository<GenericPharma, String>{

}
