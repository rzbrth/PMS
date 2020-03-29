package com.rzb.pms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.AddToCartWrapperReq;
import com.rzb.pms.dto.AddToCartWrapperRes;
import com.rzb.pms.dto.DispenseResponseDTO;
import com.rzb.pms.dto.DrugDispenseWrapperDTO;
import com.rzb.pms.service.DrugDispensingService;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.SELL)
@Slf4j
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

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.DISPENSE)
	@ApiOperation("Search dispensed items by customer name, customer mobile, company, invoice number, isReturned;")
	public ResponseEntity<ResponseSchema<List<DispenseResponseDTO>>> getAllDispensedData(
			@ApiParam(value = "Search Param", required = false) @RequestParam(value = "search", required = false) String search,
			@ApiParam(value = "Page Number", required = true) @RequestParam(defaultValue = "1", required = false) Integer page,
			@ApiParam(value = "Page Size", required = true) @RequestParam(defaultValue = "10", required = false) Integer size,
			@ApiParam(value = "Sort Param", required = false) @RequestParam(defaultValue = "sellDate:DESC", required = false) String sort) {
		log.info("Search Parameter: " + search);
		log.info("Sort Parameter: " + sort);

		if (page == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number can not be 0");

		}
		Sort sortCriteria = BaseUtil.getSortObject(sort);
		PageRequest pageRequest = PageRequest.of(page - 1, size, sortCriteria);
		List<DispenseResponseDTO> response = cartService.getAllDispensedData(search, pageRequest);
		log.info("Dispensed item size: " + response.size());

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(response, new ResponseSchema<List<DispenseResponseDTO>>()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping(Endpoints.RETURN)
	@PatchMapping(Endpoints.RETURN)
	@ApiOperation("Return dispensed items")
	public ResponseEntity<ResponseSchema<String>> returnDispensedItems(
			@ApiParam(value = "Dispense Id", required = true) @RequestParam Integer dispenseId,
			@ApiParam(value = "Dispense Lineitem Id", required = true) @RequestParam Integer[] dispenseLineItemId) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(cartService.returnDispensedItems(dispenseId, dispenseLineItemId),
						new ResponseSchema<String>()),
				HttpStatus.OK);
	}
}
