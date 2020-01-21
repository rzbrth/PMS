package com.rzb.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.StockDirectRequestDTO;
import com.rzb.pms.service.StockService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.STOCK)
public class StockController {

	@Autowired
	private StockService service;

	/*
	 * Create stock directly
	 */
	@PostMapping
	@ApiOperation("Add stock directly without po")
	public ResponseEntity<ResponseSchema<String>> addStockDirect(@RequestBody StockDirectRequestDTO stock) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.addStockWithoutPR(stock), new ResponseSchema<String>()),
				HttpStatus.OK);

	}

	/*
	 * This will Create stock from purchase order
	 */
	@PostMapping(Endpoints.ADD_STOCK_FROM_PO)
	@ApiOperation("Add stock from po")
	public ResponseEntity<ResponseSchema<String>> addStockFromPO(@RequestBody PurchaseOrderResponse po) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.addStockFromPR(po), new ResponseSchema<String>()),
				HttpStatus.OK);

	}
}
