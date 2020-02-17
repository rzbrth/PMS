package com.rzb.pms.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.EntityInfoRequest;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.StockDirectRequestDTOWrapper;
import com.rzb.pms.dto.TopDrugAboutToExpire;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.service.StockService;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@SuppressWarnings("unchecked")
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.STOCK)
public class StockController {

	@SuppressWarnings("rawtypes")
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

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.addStockWithoutPR(lineItem), new ResponseSchema<String>()),
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

	/*
	 * Allowed Search Criterias are as follows
	 * stockCreatedAt,stockId,drugId,location,expiryDate,distributerId,
	 * invoiceReference,poReferenseNumber
	 * 
	 * Allowed Sort criteria are as follows expiryDate, expiryStatus
	 * 
	 */
//	@GetMapping(Endpoints.STOCK + Endpoints.STOCK_EXPIRE)
//	@ApiOperation("Find expired or about to expire Item")
//	public ResponseEntity<ResponseSchema<List<TopDrugAboutToExpire>>> checkForExpiration(
//			@ApiParam(value = "Sort patameter") @Valid @RequestParam(defaultValue = "expiryDate:DESC") String sort,
//			@ApiParam(value = "Page Number", required = true) @RequestParam(defaultValue = "0") Integer page,
//			@ApiParam(value = "Page Size", required = true) @RequestParam(defaultValue = "10") Integer size,
//			@ApiParam(value = "Search Criteria") @RequestParam String search) throws CustomEntityNotFoundException {
//		logger.info("Search Parameter: " + search);
//		logger.info("Sort Parameter: " + sort);
//		Sort sortCriteria = BaseUtil.getSortObject(sort);
//		PageRequest pageRequest = PageRequest.of(page - 1, size, sortCriteria);
//
//		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(service.checkForExpiry(search, pageRequest),
//				new ResponseSchema<List<TopDrugAboutToExpire>>()), HttpStatus.OK);
//
//	}

	/*
	 * This controller is to find all stock info or single stock info by id. It
	 * support exporting result into files It support searching and sorting criteria
	 * 
	 * Allowed Search Criteria are as follows:-
	 * stockCreatedAt,stockId,drugId,location,expiryDate,distributerId,
	 * invoiceReference,poReferenseNumber
	 * 
	 * Allowed Sort criteria are as follows expiryDate, expiryStatus and sort order
	 * are ASC or DSC
	 */
	@GetMapping(Endpoints.STOCK_INFO)
	@ApiOperation("Find all stock or single stock information")
	public ResponseEntity<ResponseSchema<List<TopDrugAboutToExpire>>> checkForExpiration(
			@ApiParam(value = "Sort patameter", required = false) @Valid @RequestParam(defaultValue = "expiryDate:DESC") String sort,
			@ApiParam(value = "Page Number", required = false) @RequestParam(defaultValue = "1") Integer page,
			@ApiParam(value = "Page Size") @RequestParam(defaultValue = "10") Integer size,
			@ApiParam(value = "Search Criteria", required = false) @RequestParam String search,
			@ApiParam(value = "Stock Id", required = false) @RequestParam Integer stockId,
			@ApiParam(value = "Entity Request", required = true, allowableValues = "FIND_ALL, FIND_ONE") @RequestParam String entityInfoRequest,
			@ApiParam(value = "Export choice", required = true) @RequestParam(defaultValue = "false") Boolean isExported,
			@ApiParam(value = "Export Type", required = true) @RequestParam(defaultValue = "EXCEL") String exportType,
			HttpServletResponse response) throws CustomException {
		logger.info("Search Parameter: " + search);
		logger.info("Sort Parameter: " + sort);

		if (entityInfoRequest.equalsIgnoreCase(EntityInfoRequest.FIND_ALL.toString())) {

			if (page == null || size == null) {
				throw new CustomException("Page number or Page Size can not be empty", HttpStatus.BAD_REQUEST);
			}
			if (page == 0) {
				throw new CustomException("Page number can not be 0", HttpStatus.BAD_REQUEST);

			}
		}
		Sort sortCriteria = BaseUtil.getSortObject(sort);
		PageRequest pageRequest = PageRequest.of(page - 1, size, sortCriteria);

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.findStockDetails(search.trim(), pageRequest, isExported, exportType,
						stockId, response, entityInfoRequest), new ResponseSchema<List<TopDrugAboutToExpire>>()),
				HttpStatus.OK);

	}
}
