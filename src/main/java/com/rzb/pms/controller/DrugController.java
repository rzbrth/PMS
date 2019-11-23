package com.rzb.pms.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.model.Drugs;
import com.rzb.pms.service.DrugService;
import com.rzb.pms.utils.Endpoints;

@RestController
@RequestMapping(value = Endpoints.VERSION_1)
public class DrugController {

	Logger logger = LoggerFactory.getLogger(DrugController.class);

	@Autowired
	private DrugService drugService;

	@GetMapping(Endpoints.DRUG_LIST)
	public ResponseEntity<List<Drugs>> getAllDrugs(
//			@RequestParam(value = "search") String search,
//			@RequestParam(defaultValue = "0", required = false) Integer page,
//			@RequestParam(defaultValue = "10", required = false) Integer size,
//			@RequestParam(defaultValue = "createdAt:DESC", required = false) String sort
	) throws CustomEntityNotFoundException {
		// logger.info("Search Parameter: " + search);
		// logger.info("Sort Parameter: " + sort);

		return new ResponseEntity<List<Drugs>>(drugService.getAllDrugs(), HttpStatus.OK);

	}

	@GetMapping(Endpoints.GET_DRUG_BY_ID)
	public ResponseEntity<Optional<Drugs>> getDrugById(@PathVariable("id") String id) throws CustomEntityNotFoundException {

		return new ResponseEntity<Optional<Drugs>>(drugService.getDrugById(id), HttpStatus.OK);
	}

}
