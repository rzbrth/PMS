package com.rzb.pms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.model.Generics;
import com.rzb.pms.service.GenericService;
import com.rzb.pms.utils.Endpoints;

@RestController
@RequestMapping(value = Endpoints.VERSION_1)
public class GenericController {
	Logger logger = LoggerFactory.getLogger(GenericController.class);

	@Autowired
	private GenericService genericService;

	@GetMapping(Endpoints.GENERIC_LIST)
	public ResponseEntity<List<Generics>> getAllGenerics(
//			@RequestParam(value = "search") String search,
//			@RequestParam(defaultValue = "0", required = false) Integer page,
//			@RequestParam(defaultValue = "10", required = false) Integer size,
//			@RequestParam(defaultValue = "createdAt:DESC", required = false) String sort
	) {
		// logger.info("Search Parameter: " + search);
		// logger.info("Sort Parameter: " + sort);

		return new ResponseEntity<List<Generics>>(genericService.getAllGenerics(), HttpStatus.OK);

	}

}
