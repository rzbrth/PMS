package com.rzb.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignUpDTO {

	private String userName;

	private String userRole;

	private String password;

	private String phone;

	private String email;

	private Boolean isEnabled;

}
