package com.rzb.pms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.model.GenericPharma;
import com.rzb.pms.service.GenericPharmaService;
import com.rzb.pms.utils.Endpoints;

@RestController
@RequestMapping(value = Endpoints.VERSION_1)
public class GenericPharmaController {

	@Autowired
	private GenericPharmaService genericService;

	@GetMapping(Endpoints.GENERIC_PHARMA_LIST)
	public ResponseEntity<List<GenericPharma>> getAllGenericPharma() {

		return new ResponseEntity<List<GenericPharma>>(genericService.findAllGenerics(), HttpStatus.OK);
	}

	@GetMapping(Endpoints.GENERIC_PHARMA_BY_ID)
	public ResponseEntity<Optional<GenericPharma>> getGenericById(@PathVariable("id") String id) {

		return new ResponseEntity<Optional<GenericPharma>>(genericService.getGenericById(id), HttpStatus.OK);

	}

}
