package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.Audit;

public interface AuditRepository extends JpaRepository<Audit, Integer>, CrudRepository<Audit, Integer> {

}
