package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, Integer>, CrudRepository<UserToken, Integer> {
	UserToken findByToken(String token);

}
