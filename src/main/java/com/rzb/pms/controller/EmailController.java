package com.rzb.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.service.EmailService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.MAIL)
public class EmailController {

	@Autowired
	private EmailService service;

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping
	@ApiOperation("Send Email")
	public ResponseEntity<ResponseSchema<String>> sentEmail(
			@ApiParam(value = "Mail Type", required = true, allowableValues = "EMAIL_PO, EMAIL_SELL_INVOICE") @RequestParam(required = true) String mailType,
			@ApiParam(value = "PO ID", required = true) @RequestParam(required = true) Integer id) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.sentPoMail(mailType, id), new ResponseSchema<String>()),
				HttpStatus.OK);
	}

}
