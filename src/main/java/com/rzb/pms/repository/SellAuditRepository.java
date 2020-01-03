package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.SellAudit;

public interface SellAuditRepository extends JpaRepository<SellAudit, Integer>, CrudRepository<SellAudit, Integer> {

}
