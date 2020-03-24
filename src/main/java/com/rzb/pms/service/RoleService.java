package com.rzb.pms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rzb.pms.model.Role;
import com.rzb.pms.model.RoleType;
import com.rzb.pms.repository.RoleRepository;

@Service
public class RoleService {
	private RoleRepository roleRepository;

	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	public Role findByName(RoleType name) {
		return roleRepository.findByName(name);
	}

	public boolean existsRoleByName(RoleType name) {
		return roleRepository.existsRoleByName(name);
	}

	public Role findById(Long id) {
		return roleRepository.findById(id).get();
	}

	public void save(Role role) {
	}

}
