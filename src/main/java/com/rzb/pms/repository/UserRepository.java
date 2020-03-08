package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rzb.pms.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>, CrudRepository<Users, Long> {

	Users findByUserName(String username);

	boolean existsUsersByEmail(String email);

	boolean existsUsersByPhone(String phone);

	Users findByEmail(String email);
}
