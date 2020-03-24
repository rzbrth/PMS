package com.rzb.pms.security.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rzb.pms.security.JwtTokenUtil;
import com.rzb.pms.security.model.AuthToken;
import com.rzb.pms.security.repository.AuthTokenRepository;
import com.rzb.pms.utils.BaseUtil;

@Service
public class AuthtokenService {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private AuthTokenRepository authTokenRepository;

	public String blacklistToken(HttpServletRequest request) {

		final String requestHeader = request.getHeader("Authorization");

		final String token = requestHeader.substring(7);

		AuthToken authToken = new AuthToken();
		authToken.setExpire(BaseUtil.convertToDateViaSqlDate(jwtTokenUtil.getExpirationDateFromToken(token)).getTime());
		authToken.setJwtId(jwtTokenUtil.getJtiFromToken(token));
		authTokenRepository.save(authToken);

		return "black-Listed";
	}

}
