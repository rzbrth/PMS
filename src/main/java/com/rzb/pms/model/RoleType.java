package com.rzb.pms.model;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {

	ADMIN, USER;
	
	@Override
	public String getAuthority() {
		return name();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
