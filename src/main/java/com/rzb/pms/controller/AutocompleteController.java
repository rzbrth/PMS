package com.rzb.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.DrugSearchResponse;
import com.rzb.pms.service.DrugService;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.AUTOCOMPLETE)
@Slf4j
public class AutocompleteController {

	@SuppressWarnings("rawtypes")
	@Autowired
	private DrugService drugService;

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.MEDECINE_AUTOCOMPLETE)
	@ApiOperation("Search medecine by genericName, brandName, company, composition, location")
	public ResponseEntity<ResponseSchema<DrugSearchResponse>> getAllDrugs(
			@ApiParam(value = "Search Param", required = true) @RequestParam(value = "search") String search,
			@ApiParam(value = "Page Number", required = true) @RequestParam(defaultValue = "1", required = false) Integer page,
			@ApiParam(value = "Page Size", required = true) @RequestParam(defaultValue = "10", required = false) Integer size,
			@ApiParam(value = "Sort Param", required = false) @RequestParam(defaultValue = "mrp:DESC", required = false) String sort) {
		log.info("Search Parameter: " + search);
		log.info("Sort Parameter: " + sort);

		if (page == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number can not be 0");

		}
		Sort sortCriteria = BaseUtil.getSortObject(sort);
		PageRequest pageRequest = PageRequest.of(page - 1, size, sortCriteria);
		DrugSearchResponse response = drugService.search(search, pageRequest);
		log.info("Drug size: " + response.getTotalCount());

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(response, new ResponseSchema<DrugSearchResponse>()), HttpStatus.OK);

	}

}
