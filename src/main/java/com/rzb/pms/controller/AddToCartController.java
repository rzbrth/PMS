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
import com.rzb.pms.log.Log;
import com.rzb.pms.model.AddToCart;
import com.rzb.pms.service.AddToCartService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.CART)
public class AddToCartController {

	@Log
	private Logger logger;

	@Autowired
	private AddToCartService cartService;

//	@PostMapping(Endpoints.ADD_TO_CART)
//	public ResponseEntity<ResponseSchema<String>> addLineItemToCart(@RequestBody List<AddToCart> cart){
//		
//		
//		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(cartService.addToCart(cart), new ResponseSchema<String>()),
//				HttpStatus.OK);
//	}
	
	@PostMapping(Endpoints.ADD_TO_CART)
	public ResponseEntity<ResponseSchema<String>> addLineItemToCart(@RequestBody AddToCartWrapper wrpper) {

		for (AddToCart lineitem : wrpper.getCart()) {
			cartService.addToCart(lineitem);
		}

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse("Success", new ResponseSchema<String>()),
				HttpStatus.OK);
	}

}
