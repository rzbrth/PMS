package com.rzb.pms.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.config.ResponseSchema;
import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.dto.DrugDtoReqRes;
import com.rzb.pms.log.Log;
import com.rzb.pms.service.DrugService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.DRUG)
public class DrugController {

	@Autowired
	private DrugService drugService;

	@Log
	private Logger logger;

	@GetMapping(Endpoints.ALL_MEDECINE)
	@ApiOperation("Get all drug data")
	public ResponseEntity<ResponseSchema<List<DrugDTO>>> getAllDrugs(
			@ApiParam(value = "Page Number, Page Size", required = true) @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		List<DrugDTO> data = drugService.findAllDrugs(pageRequest);

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(data, new ResponseSchema<List<DrugDTO>>()),
				HttpStatus.OK);
	}

	@GetMapping(Endpoints.SEARCH_MEDECINE_BY_ID)
	@ApiOperation("Find drug by drug id")
	public ResponseEntity<ResponseSchema<DrugDTO>> getDrugById(
			@ApiParam(value = "Drug Id", required = true) @Valid @PathVariable String drugId) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.getdrugById(drugId), new ResponseSchema<DrugDTO>()),
				HttpStatus.OK);

	}

	@PostMapping(Endpoints.ADD_DRUG)
	@ApiOperation("Save drug information")
	public ResponseEntity<ResponseSchema<String>> addDrugInfo(@RequestBody DrugDtoReqRes data) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.addDrug(data), new ResponseSchema<String>()),
				HttpStatus.OK);

	}

	@PutMapping(Endpoints.UPDATE_DRUG_BY_ID)
	@ApiOperation("Update drug info by using drug id")
	public ResponseEntity<ResponseSchema<String>> updateDrugInfo(@ApiParam(value = "Drug Id", required = true)

	@RequestBody DrugDtoReqRes data, @Valid @PathVariable String drugId) {

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(drugService.updateDrugData(data, drugId),
				new ResponseSchema<String>()), HttpStatus.OK);

	}

	@GetMapping(Endpoints.GET_DRUG_BY_GENERIC_ID)
	@ApiOperation("Find drug info by using generic id")
	public ResponseEntity<ResponseSchema<List<DrugDTO>>> getDrugByGenericId(
			@ApiParam(value = "Generic Id", required = true) @Valid @PathVariable String genericId,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size);

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.getDrugByGenericId(genericId, pageRequest),
						new ResponseSchema<List<DrugDTO>>()),
				HttpStatus.OK);

	}

	@GetMapping(Endpoints.GET_DRUG_BY_GENERIC_NAME)
	@ApiOperation("Find drug info by using generic name")

	public ResponseEntity<ResponseSchema<List<DrugDTO>>> getDrugByGenericName(
			@ApiParam(value = "Generic Name", required = true) @Valid @PathVariable String genericName,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size);

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.getDrugByGenericName(genericName, pageRequest),
						new ResponseSchema<List<DrugDTO>>()),
				HttpStatus.OK);

	}

	@GetMapping(Endpoints.GET_DRUG_BY_COMPOSITION)
	@ApiOperation("Find drug info by using drug composition")
	public ResponseEntity<ResponseSchema<List<DrugDTO>>> getDrugByComposition(
			@ApiParam(value = "Drug Composition, Page Number, Page Size", required = true) @Valid @PathVariable String composition,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size);

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.getDrugByComposition(composition, pageRequest),
						new ResponseSchema<List<DrugDTO>>()),
				HttpStatus.OK);

	}
}
