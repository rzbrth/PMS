package com.rzb.pms.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.AddToCartWrapper;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.model.DrugDispense;
import com.rzb.pms.service.DrugDispensingService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.CART)
public class DrugDispenseController {

	@Log
	private Logger logger;

	@Autowired
	private DrugDispensingService cartService;
	
	@PostMapping(Endpoints.ADD_TO_CART)
	@ApiOperation("Dispense drug")
	public ResponseEntity<ResponseSchema<String>> addLineItemDispenseList(@RequestBody AddToCartWrapper wrpper) {

		if(wrpper == null) {
			logger.error("Line Items can't be empty", HttpStatus.BAD_REQUEST);
			throw new CustomException("Line Items can't be empty", HttpStatus.BAD_REQUEST);
		}
		for (DrugDispense lineitem : wrpper.getCart()) {
			cartService.drugDispense(lineitem);
		}

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse("Success", new ResponseSchema<String>()),
				HttpStatus.OK);
	}

}
