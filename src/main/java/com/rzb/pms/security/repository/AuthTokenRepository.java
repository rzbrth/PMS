package com.rzb.pms.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rzb.pms.security.model.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

	AuthToken findByJti(String id);
}
