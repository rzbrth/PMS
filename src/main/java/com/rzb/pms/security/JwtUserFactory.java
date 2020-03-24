package com.rzb.pms.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.rzb.pms.model.Role;
import com.rzb.pms.model.Users;

public final class JwtUserFactory {

	private JwtUserFactory() {
	}

	public static UserAuthentication create(Users users) {
		return new UserAuthentication(users.getId(), users.getUserName(), null, null, users.getPassword(), users.getEmail(),
				mapToGrantedAuthorities(users.getRoles()), users.getIsEnabled(), users.getLastPasswordResetDate());

	}

	private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName().toString()))
				.collect(Collectors.toList());
	}
}