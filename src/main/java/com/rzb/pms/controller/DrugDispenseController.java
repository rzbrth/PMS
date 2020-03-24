package com.rzb.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.AddToCartWrapperReq;
import com.rzb.pms.dto.AddToCartWrapperRes;
import com.rzb.pms.dto.DrugDispenseWrapperDTO;
import com.rzb.pms.service.DrugDispensingService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.SELL)
public class DrugDispenseController {

	@Autowired
	private DrugDispensingService cartService;

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping(Endpoints.ADD_TO_CART)
	@ApiOperation("Add item to cart")
	public ResponseEntity<ResponseSchema<AddToCartWrapperRes>> addLineItemToCart(
			@RequestBody AddToCartWrapperReq wrapper) {

		if (wrapper == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line Items can't be empty");
		}
		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(cartService.addToCard(wrapper),
				new ResponseSchema<AddToCartWrapperRes>()), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping(Endpoints.DISPENSE)
	@ApiOperation("Dispense drug")
	public ResponseEntity<ResponseSchema<String>> dispenseDrug(@RequestBody DrugDispenseWrapperDTO wrapper) {

		if (wrapper == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line Items can't be empty");

		}
		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(cartService.drugDispense(wrapper), new ResponseSchema<String>()),
				HttpStatus.OK);
	}
}
