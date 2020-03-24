package com.rzb.pms.security;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("serial")
@AllArgsConstructor
@Getter
public class JwtAuthenticationResponse implements Serializable {

	private final String accessToken;

	private final String refreshToken;
	
	public JwtAuthenticationResponse(String refreshToken) {
		this.accessToken = "";
		this.refreshToken = refreshToken;
	}
	

}
