package com.rzb.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.UserSignUpDTO;
import com.rzb.pms.service.UserService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.USER)
public class UserController {

	@Autowired
	private UserService service;

	@PostMapping(Endpoints.SIGN_UP)
	@ApiOperation("Users Registration")
	public ResponseEntity<ResponseSchema<ModelMap>> signUp(@RequestBody UserSignUpDTO data) {

		ModelMap map = new ModelMap();
		map.addAttribute("message", service.registerUser(data));
		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(map, new ResponseSchema<ModelMap>()),
				HttpStatus.CREATED);

	}

	@PostMapping(Endpoints.EMAIL_VERIFICATION)
	@ApiOperation("Email verification")
	public ResponseEntity<ResponseSchema<ModelMap>> verifyEmail(
			@ApiParam(required = true) @RequestParam(required = true) String token,
			@ApiParam(required = true) @RequestParam(required = true) String email) {

		ModelMap map = new ModelMap();
		map.addAttribute("message", service.verifyEmail(token, email));
		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(map, new ResponseSchema<ModelMap>()),
				HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@PatchMapping(Endpoints.MANAGE_ACCOUNT)
	@ApiOperation("Manage Account status")
	public ResponseEntity<ResponseSchema<ModelMap>> manageAccountStatus(
			@ApiParam(required = true) @RequestParam(required = true) Long userId,
			@ApiParam(required = true) @RequestParam(required = true) Boolean status) {

		ModelMap map = new ModelMap();
		map.addAttribute("message", service.manageAccountActiveStatus(userId, status));
		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(map, new ResponseSchema<ModelMap>()),
				HttpStatus.OK);

	}
}
