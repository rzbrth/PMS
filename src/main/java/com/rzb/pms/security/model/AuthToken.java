package com.rzb.pms.security.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AuthToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String jti;

	private Long expire;

	private String type;

	public AuthToken(String jwtId, Long expire, String type) {
		this.jti = jwtId;
		this.expire = expire;
		this.type = type;
	}

	public AuthToken() {
	}

	public String getJwtId() {
		return jti;
	}

	public void setJwtId(String jwtId) {
		this.jti = jwtId;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
