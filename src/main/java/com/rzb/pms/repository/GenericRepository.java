package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.Generics;

@Repository
public interface GenericRepository extends JpaRepository<Generics, String>, CrudRepository<Generics, String> {

}
