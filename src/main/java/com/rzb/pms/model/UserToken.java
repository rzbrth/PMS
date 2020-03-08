package com.rzb.pms.model;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.rzb.pms.dto.UserTokenType;
import com.rzb.pms.utils.BaseUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {

	private static final int EXPIRATION = 60 * 24;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String token;

	@OneToOne(targetEntity = Users.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private Users users;

	private LocalDate expiry;

	private UserTokenType type;

	public UserToken(final String token, final Users users) {
		super();

		this.token = token;
		this.users = users;
		this.expiry = calculateExpiryDate(EXPIRATION);
	}

	private LocalDate calculateExpiryDate(final int expiryTimeInMinutes) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(new Date().getTime());
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		return BaseUtil.convertToLocalDateTimeFromDate(new Date(cal.getTime().getTime()));
	}

}