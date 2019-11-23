package com.rzb.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.service.MapperService;
import com.rzb.pms.utils.Endpoints;

@RestController
@RequestMapping(value = Endpoints.VERSION_1)
public class DataMapper {

	@Autowired
	private MapperService service;

	@PostMapping(Endpoints.DATA_MAPPER)
	public ResponseEntity<String> mapData() {

		return new ResponseEntity<String>(service.mapData(), HttpStatus.OK);
	}

}
