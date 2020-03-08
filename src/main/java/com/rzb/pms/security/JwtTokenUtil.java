package com.rzb.pms.security;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.rzb.pms.security.model.AuthToken;
import com.rzb.pms.security.repository.AuthTokenRepository;
import com.rzb.pms.utils.BaseUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;

@SuppressWarnings("serial")
@Component
public class JwtTokenUtil implements Serializable {

	@Autowired
	private AuthTokenRepository authTokenRepository;

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "iat";
	private Clock clock = DefaultClock.INSTANCE;

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public LocalDate getIssuedAtDateFromToken(String token) {

		return BaseUtil.convertToLocalDateTimeFromDate(getClaimFromToken(token, Claims::getIssuedAt));

	}

	public LocalDate getExpirationDateFromToken(String token) {
		return BaseUtil.convertToLocalDateTimeFromDate(getClaimFromToken(token, Claims::getExpiration));

	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	public String getJtiFromToken(String token) {
		return getClaimFromToken(token, Claims::getId);
	}

	private Boolean isTokenExpired(String token) {
		final LocalDate expiration = getExpirationDateFromToken(token);
		return expiration.isBefore(BaseUtil.convertToLocalDateTimeFromDate(clock.now()));
	}

	private Boolean isCreatedBeforeLastPasswordReset(LocalDate created, LocalDate lastPasswordReset) {
		return (lastPasswordReset != null && created.isBefore(lastPasswordReset));
	}

	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	public String generateAccessToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("type", "Access");
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		final LocalDate createdDate = BaseUtil.convertToLocalDateTimeFromDate(clock.now());
		final LocalDate expirationDate = calculateExpirationDate(createdDate);

		return Jwts.builder().setClaims(claims).setSubject(subject)
				.setIssuedAt(BaseUtil.convertToDateViaSqlDate(createdDate))
				.setExpiration(BaseUtil.convertToDateViaSqlDate(expirationDate))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public Boolean canTokenBeRefreshed(String token, LocalDate lastPasswordReset) {
		final LocalDate created = getIssuedAtDateFromToken(token);
		return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
				&& (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	public String refreshToken(String token) {
		final LocalDate createdDate = BaseUtil.convertToLocalDateTimeFromDate(clock.now());
		final LocalDate expirationDate = calculateExpirationDate(createdDate);

		final Claims claims = getAllClaimsFromToken(token);
		claims.setIssuedAt(BaseUtil.convertToDateViaSqlDate(createdDate));
		claims.setExpiration(BaseUtil.convertToDateViaSqlDate(expirationDate));

		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		UserAuthentication user = (UserAuthentication) userDetails;
		final String username = getUsernameFromToken(token);
		final LocalDate created = getIssuedAtDateFromToken(token);
		final LocalDate expiration = getExpirationDateFromToken(token);
		final String jti = getJtiFromToken(token);

		AuthToken blackListToken = authTokenRepository.findByJti(jti);
		if (blackListToken == null) {

			return (username.equals(user.getUsername()) && !isTokenExpired(token)
					&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
		} else {
			return Boolean.FALSE;
		}

	}

	private LocalDate calculateExpirationDate(LocalDate createdDate) {

		return BaseUtil.convertToLocalDateTimeFromDate(
				new Date(BaseUtil.convertToDateViaSqlDate(createdDate).getTime() + expiration * 1000));
	}
}