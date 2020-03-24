package com.rzb.pms.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.rzb.pms.service.DrugService;
import com.rzb.pms.utils.Endpoints;
import com.rzb.pms.utils.ResponseUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@SuppressWarnings("unchecked")
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.DRUG)
public class DrugController<K> {

	@SuppressWarnings("rawtypes")
	@Autowired
	private DrugService drugService;

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.ALL_MEDECINE)
	@ApiOperation("Get all drug data")
	public ResponseEntity<K> getAllDrugs(
			@ApiParam(value = "Page Number", required = true) @RequestParam(defaultValue = "0") Integer page,
			@ApiParam(value = "Page Size", required = true) @RequestParam(defaultValue = "10") Integer size,
			@ApiParam(value = "Export choice", required = true) @RequestParam(defaultValue = "false") Boolean isExported,
			@ApiParam(value = "Export Type", required = true) @RequestParam(defaultValue = "EXCEL") String exportType,
			HttpServletResponse response) {
		PageRequest pageRequest = PageRequest.of(page, size);

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(
				drugService.findAllDrugs(pageRequest, isExported, exportType, response),
				new ResponseSchema<List<DrugDTO>>()), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.SEARCH_MEDECINE_BY_ID)
	@ApiOperation("Find drug by drug id")
	public ResponseEntity<ResponseSchema<DrugDTO>> getDrugById(
			@ApiParam(value = "Drug Id", required = true) @Valid @PathVariable(required = true) String drugId) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.getdrugById(drugId), new ResponseSchema<DrugDTO>()),
				HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping(Endpoints.ADD_DRUG)
	@ApiOperation("Save drug information")
	public ResponseEntity<ResponseSchema<String>> addDrugInfo(@RequestBody DrugDtoReqRes data) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.addDrug(data), new ResponseSchema<String>()),
				HttpStatus.CREATED);

	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PutMapping(Endpoints.UPDATE_DRUG_BY_ID)
	@ApiOperation("Update drug info by using drug id")
	public ResponseEntity<ResponseSchema<String>> updateDrugInfo(
			@ApiParam(value = "Drug Id", required = true) @PathVariable(required = true) String drugId,
			@RequestBody DrugDtoReqRes data) {

		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(drugService.updateDrugData(data, drugId),
				new ResponseSchema<String>()), HttpStatus.OK);

	}

//	@GetMapping(Endpoints.GET_DRUG_BY_GENERIC_ID)
//	@ApiOperation("Find drug info by using generic id")
//	public ResponseEntity<ResponseSchema<List<DrugDTO>>> getDrugByGenericId(
//			@ApiParam(value = "Generic Id", required = true) @Valid @PathVariable String genericId,
//			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size)
//			 {
//		PageRequest pageRequest = PageRequest.of(page, size);
//
//		return new ResponseEntity<>(
//				ResponseUtil.buildSuccessResponse(drugService.getDrugByGenericId(genericId, pageRequest),
//						new ResponseSchema<List<DrugDTO>>()),
//				HttpStatus.OK);
//
//	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
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

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.GET_DRUG_BY_COMPOSITION)
	@ApiOperation("Find alternate drug as per composition")
	public ResponseEntity<ResponseSchema<List<DrugDTO>>> getDrugByComposition(
			@ApiParam(value = "Drug Composition, Page Number, Page Size", required = true) @Valid @RequestParam String composition,
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size);

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.getDrugByComposition(composition, pageRequest),
						new ResponseSchema<List<DrugDTO>>()),
				HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping(Endpoints.GET_DRUG_BY_NAME)
	@ApiOperation("Find drug by brand name")
	public ResponseEntity<ResponseSchema<DrugDTO>> getDrugByName(
			@ApiParam(value = "Drug Name", required = true) @Valid @PathVariable(required = true) String brandName) {

		return new ResponseEntity<>(
				ResponseUtil.buildSuccessResponse(drugService.getdrugByName(brandName), new ResponseSchema<DrugDTO>()),
				HttpStatus.OK);

	}

}
