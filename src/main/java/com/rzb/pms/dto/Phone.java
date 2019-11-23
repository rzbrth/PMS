package com.rzb.pms.dto;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Phone {

	@Column(name = "MOB_NUM")
	private String mobileNumber;

	@Column(name = "ALT_MOB_NUM")
	private String alternateMobileNumber;

	@Column(name = "TEL_NUM")
	private String telephoneNumber;
}
