package com.rzb.pms.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.ExpiredItemReturnWrapper;
import com.rzb.pms.dto.PurchaseOrderResponse;
import com.rzb.pms.dto.StockDirectRequestDTOWrapper;
import com.rzb.pms.dto.StockResponseDto;
import com.rzb.pms.dto.TopDrugAboutToExpire;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.service.StockService;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@SuppressWarnings("unchecked")
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.STOCK)
@Slf4j
public class StockController {

	@SuppressWarnings("rawtypes")
	@Autowired
	private StockService service;


	/*
	 * Create stock directly
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping
	@ApiOperation("Add stock directly without po")
	public ResponseEntity<ResponseSchema<String>> addStockDirect(@RequestBody StockDirectRequestDTOWrapper lineItem) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.addStockWithoutPR(lineItem), new ResponseSchema<String>()),
				HttpStatus.CREATED);

	}

	/*
	 * This will Create stock from purchase order
	 */
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping(Endpoints.PO)
	@ApiOperation("Add stock from po")
	public ResponseEntity<ResponseSchema<String>> addStockFromPO(@RequestBody PurchaseOrderResponse po) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.addStockFromPR(po), new ResponseSchema<String>()),
				HttpStatus.CREATED);

	}

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
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.GET_ALL_STOCK_WITH_EXPORT_OPTION)
	@ApiOperation("Find all stock with export option")
	public ResponseEntity<ResponseSchema<List<StockResponseDto>>> findAllStockWithExportOption(
			@ApiParam(value = "Sort patameter", required = false) @Valid @RequestParam(defaultValue = "expiryDate:DESC") String sort,
			@ApiParam(value = "Page Number", required = false) @RequestParam(defaultValue = "1") Integer page,
			@ApiParam(value = "Page Size") @RequestParam(defaultValue = "10") Integer size,
			@ApiParam(value = "Search Criteria", required = false) @RequestParam String search,
			@ApiParam(value = "Export choice", required = true) @RequestParam(defaultValue = "false") Boolean isExported,
			@ApiParam(value = "Export Type", required = true) @RequestParam(defaultValue = "EXCEL") String exportType,
			HttpServletResponse response) throws CustomException {
		log.info("Search Parameter: " + search);
		log.info("Sort Parameter: " + sort);

		if (page == null || size == null) {
			throw new CustomException("Page number or Page Size can not be empty", HttpStatus.BAD_REQUEST);
		}
		if (page == 0) {
			throw new CustomException("Page number can not be 0", HttpStatus.BAD_REQUEST);

		}

		Sort sortCriteria = BaseUtil.getSortObject(sort);
		PageRequest pageRequest = PageRequest.of(page - 1, size, sortCriteria);

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(
				service.findAllStock(search.trim(), pageRequest, isExported, exportType, response),
				new ResponseSchema<List<StockResponseDto>>()), HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.GET_STOCK_BY_ID)
	@ApiOperation("Find stock by id")
	public ResponseEntity<ResponseSchema<StockResponseDto>> getStockById(
			@ApiParam(value = "Stock Id", required = true) @PathVariable Integer stockId) {

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(service.getStockById(stockId),
				new ResponseSchema<StockResponseDto>()), HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.GET_TOP4_ABOUT_TO_EXPIRE_STOCK)
	@ApiOperation("Find top 4 about to expire stock")
	public ResponseEntity<ResponseSchema<List<TopDrugAboutToExpire>>> findAboutToExpireStock() {

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(service.checkForAboutToExpireItem(),
				new ResponseSchema<List<TopDrugAboutToExpire>>()), HttpStatus.OK);

	}
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping(Endpoints.RETURN_EXPIRED_ITEM)
	@ApiOperation("Create return request for expired items")
	public ResponseEntity<ResponseSchema<String>> returnExpiredItem(@RequestBody ExpiredItemReturnWrapper wrapper) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.emptyExpiredStock(wrapper), new ResponseSchema<String>()),
				HttpStatus.OK);

	}
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@DeleteMapping(Endpoints.DELETE_STOCK)
	@ApiOperation("Delete stock by id")
	public ResponseEntity<ResponseSchema<String>> deleteDrug(
			@ApiParam(value = "Stock Id", required = true) @PathVariable Integer stockId) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(service.deleteStockById(stockId), new ResponseSchema<String>()),
				HttpStatus.OK);

	}
	
	
}
