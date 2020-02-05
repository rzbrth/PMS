package com.rzb.pms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.PoCreateDTO;
import com.rzb.pms.dto.PoUpdateDTO;
import com.rzb.pms.dto.PurchaseOrderDTO;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.service.PurchaseOrderService;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.PO)
public class PurchaseOrderController {

	@Autowired
	private PurchaseOrderService orderService;

	@Log
	private Logger logger;

	@GetMapping
	@ApiOperation("Get all purchase order")
	public ResponseEntity<ResponseSchema<List<PurchaseOrderResponse>>> getAllPO(
			@ApiParam(value = "Page No", required = true) @RequestParam(defaultValue = "0") Integer page,
			@ApiParam(value = "Page Size", required = true) @RequestParam(defaultValue = "10") Integer size,
			@RequestParam(defaultValue = "createdDate:DESC", required = false) String sort,
			@ApiParam(value = "Search Param") @RequestParam(defaultValue = "") String search) throws CustomEntityNotFoundException {

		logger.info("Search Parameter: " + search);
		logger.info("Sort Parameter: " + sort);

		Sort sortCriteria = BaseUtil.getSortObject(sort);
		PageRequest pageRequest = PageRequest.of(page - 1, size, sortCriteria);
		List<PurchaseOrderResponse> response = orderService.findAllOrder(search, pageRequest);

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(response, new ResponseSchema<List<PurchaseOrderResponse>>()),
				HttpStatus.OK);
	}

	@GetMapping(Endpoints.PO_BY_ID)
	@ApiOperation("Get purchase order by Id")
	public ResponseEntity<ResponseSchema<PurchaseOrderDTO>> getPoById(
			@ApiParam(value = "Purchase Order Id", required = true) @RequestParam(required = true) Integer poId)
			throws CustomEntityNotFoundException {

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(orderService.findPOById(poId),
				new ResponseSchema<PurchaseOrderDTO>()), HttpStatus.OK);
	}

	@PostMapping
	@ApiOperation("Create purchase order")
	public ResponseEntity<ResponseSchema<String>> createPO(@RequestBody PoCreateDTO data) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(orderService.createPO(data), new ResponseSchema<String>()),
				HttpStatus.OK);
	}

	@PutMapping
	@ApiOperation("Update purchase order")
	public ResponseEntity<ResponseSchema<String>> updatePO(@RequestBody PoUpdateDTO data) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(orderService.updatePO(data), new ResponseSchema<String>()),
				HttpStatus.OK);
	}

	@DeleteMapping
	@ApiOperation("Delete purchase order")
	public ResponseEntity<ResponseSchema<String>> deletePo(
			@ApiParam(value = "Purchase Order Id", required = true) @RequestParam(required = true) Integer poId,
			@ApiParam(value = "Purchase Order Status", required = true) @RequestParam(required = true) String poStatus) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(orderService.deletePO(poId, poStatus), new ResponseSchema<String>()),
				HttpStatus.OK);
	}
}
