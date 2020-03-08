package com.rzb.pms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rzb.pms.model.Role;
import com.rzb.pms.model.RoleType;

public interface RoleRepository extends JpaRepository<Role, Long> {

	boolean existsRoleByName(RoleType name);

	Role findByName(RoleType name);

	Optional<Role> findById(Long id);
}
