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
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.StockDirectRequestDTO;
import com.rzb.pms.dto.StockDirectRequestDTOWrapper;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.service.StockService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.STOCK)
public class StockController {

	@Autowired
	private StockService service;

	@Log
	private Logger logger;

	/*
	 * Create stock directly
	 */
	@PostMapping
	@ApiOperation("Add stock directly without po")
	public ResponseEntity<ResponseSchema<String>> addStockDirect(@RequestBody StockDirectRequestDTOWrapper lineItem) {

		if (lineItem == null) {
			logger.error("Line Items can't be empty", HttpStatus.BAD_REQUEST);
			throw new CustomException("Line Items can't be empty", HttpStatus.BAD_REQUEST);
		}
		for (StockDirectRequestDTO item : lineItem.getData()) {
			service.addStockWithoutPR(item);
		}
		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse("Success", new ResponseSchema<String>()),
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
