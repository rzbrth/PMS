package com.rzb.pms.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.DrugSearchResponse;
import com.rzb.pms.log.Log;
import com.rzb.pms.service.DrugService;
import com.rzb.pms.utils.BaseUtil;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api("")
@RestController
@RequestMapping(Endpoints.VERSION_1 + Endpoints.AUTOCOMPLETE)
public class AutocompleteController {

	@Autowired
	private DrugService drugService;

	@Log
	private Logger logger;

	@GetMapping(Endpoints.MEDECINE_AUTOCOMPLETE)
	@ApiOperation("Search medecine by genericName, brandName, company, composition, location")
	public ResponseEntity<ResponseSchema<DrugSearchResponse>> getAllDrugs(
			@ApiParam(value = "Search Param", required = true, allowableValues = "brandName=lk=COSACOL, genericName=lk=aminosalicylic acid, company=lk=CIPLA, composition=lk=Mesalamine") @RequestParam(value = "search") String search,
			@RequestParam(defaultValue = "0", required = false) Integer page,
			@RequestParam(defaultValue = "10", required = false) Integer size,
			@RequestParam(defaultValue = "mrp:DESC", required = false) String sort) {
		logger.info("Search Parameter: " + search);
		logger.info("Sort Parameter: " + sort);

		Sort sortCriteria = BaseUtil.getSortObject(sort);
		PageRequest pageRequest = PageRequest.of(page - 1, size, sortCriteria);
		DrugSearchResponse response = drugService.search(search, pageRequest);
		logger.info("Drug size: " + response.getTotalCount());

	 return  new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(response, new ResponseSchema<DrugSearchResponse>()), HttpStatus.OK);

	}                                                                  

}
