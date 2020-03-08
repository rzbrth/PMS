package com.rzb.pms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.PoCreateDTO;
import com.rzb.pms.dto.PoUpdateDTO;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.service.PurchaseOrderService;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.PO)
@Slf4j
public class PurchaseOrderController {

	@Autowired
	private PurchaseOrderService orderService;


	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping
	@ApiOperation("Get all purchase order")
	public ResponseEntity<ResponseSchema<List<PurchaseOrderResponse>>> getAllPO(
			@ApiParam(value = "Page No", required = true) @RequestParam(defaultValue = "0") Integer page,
			@ApiParam(value = "Page Size", required = true) @RequestParam(defaultValue = "10") Integer size,
			@RequestParam(defaultValue = "createdDate:DESC", required = false) String sort,
			@ApiParam(value = "Search Param", required = false) @RequestParam(defaultValue = "findall") String search) {

		log.info("Search Parameter: " + search);
		log.info("Sort Parameter: " + sort);

		Sort sortCriteria = BaseUtil.getSortObject(sort);
		PageRequest pageRequest = PageRequest.of(page - 1, size, sortCriteria);
		List<PurchaseOrderResponse> response = orderService.findAllOrder(search, pageRequest);

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(response, new ResponseSchema<List<PurchaseOrderResponse>>()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.GET_ONE)
	@ApiOperation("Get purchase order by Id")
	public ResponseEntity<ResponseSchema<PurchaseOrderResponse>> getPoById(
			@ApiParam(value = "Purchase Order Id", required = true) @PathVariable(required = true) Integer poId) {

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(orderService.findPOById(poId),
				new ResponseSchema<PurchaseOrderResponse>()), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping
	@ApiOperation("Create purchase order")
	public ResponseEntity<ResponseSchema<String>> createPO(@RequestBody PoCreateDTO data) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(orderService.createPO(data), new ResponseSchema<String>()),
				HttpStatus.CREATED);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PutMapping(Endpoints.UPDATE_PO)
	@ApiOperation("Update purchase order")
	public ResponseEntity<ResponseSchema<String>> updatePO(
			@ApiParam(value = "Purchase Order Id", required = true) @PathVariable(required = true) Integer poId,
			@RequestBody PoUpdateDTO data) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(orderService.updatePO(data, poId), new ResponseSchema<String>()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PatchMapping(Endpoints.UPDATE_PO)
	@ApiOperation("Update purchase order status")
	public ResponseEntity<ResponseSchema<String>> updatePOStatus(
			@ApiParam(value = "Purchase Order Id", required = true) @PathVariable(required = true) Integer poId,
			@ApiParam(value = "Purchase Order Status", required = true) @PathVariable(required = true) String poStatus) {

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(orderService.updatePOStatus(poStatus, poId),
				new ResponseSchema<String>()), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@DeleteMapping(Endpoints.DELETE_PO)
	@ApiOperation("Delete purchase order")
	public ResponseEntity<ResponseSchema<String>> deletePo(
			@ApiParam(value = "Purchase Order Id", required = true) @PathVariable(required = true) Integer poId,
			@ApiParam(value = "Purchase Order Status", required = true) @RequestParam(required = true) String poStatus) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(orderService.deletePO(poId, poStatus), new ResponseSchema<String>()),
				HttpStatus.OK);
	}
}
