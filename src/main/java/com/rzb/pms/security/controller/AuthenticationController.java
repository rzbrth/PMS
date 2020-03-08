package com.rzb.pms.security.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.exception.CustomException;
import com.rzb.pms.security.JwtAuthenticationRequest;
import com.rzb.pms.security.JwtAuthenticationResponse;
import com.rzb.pms.security.JwtTokenUtil;
import com.rzb.pms.security.UserAuthentication;
import com.rzb.pms.security.repository.AuthTokenRepository;
import com.rzb.pms.security.service.AuthtokenService;
import com.rzb.pms.utils.Endpoints;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.AUTHENTICATION)
public class AuthenticationController {

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private AuthTokenRepository authRepo;

	@Autowired
	private AuthtokenService authService;

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@PostMapping
	@ApiOperation("Authentication Controller ")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest)
			throws AuthenticationException {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		// Reload password post-security so we can generate the token
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
		final String refreshToken = jwtTokenUtil.refreshToken(accessToken);

		// Return the token
		return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));
	}

	@GetMapping(Endpoints.REFRESH)
	@ApiOperation("Refresh token Controller ")
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
		String authToken = request.getHeader(tokenHeader);
		final String token = authToken.substring(7);
		String username = jwtTokenUtil.getUsernameFromToken(token);
		UserAuthentication user = (UserAuthentication) userDetailsService.loadUserByUsername(username);

		if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
			String refreshedToken = jwtTokenUtil.refreshToken(token);
			return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping(Endpoints.BLACK_LIST)
	@ApiOperation("Token blacklist Controller ")
	public ResponseEntity<String> blackListToken(HttpServletRequest request) {

		return new ResponseEntity<String>(authService.blacklistToken(request), HttpStatus.OK);
	}

	@ExceptionHandler({ AuthenticationException.class })
	public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}

	/**
	 * Authenticates the user. If something is wrong, an
	 * {@link AuthenticationException} will be thrown
	 */
	private void authenticate(String username, String password) {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new CustomException("Users is disabled!", e);
		} catch (BadCredentialsException e) {
			throw new CustomException("Bad credentials!", e);
		}
	}
}
