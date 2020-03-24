package com.rzb.pms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.utils.Endpoints;

@RestController
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.GENERIC)
public class GenericController {

	/*
	 * @Autowired private GenericService genericService;
	 * 
	 * @GetMapping(Endpoints.ALL_GENERIC)
	 * 
	 * @ApiOperation("Get all generic details") public
	 * ResponseEntity<ResponseSchema<List<GenericDto>>> getAllGenericPharma(
	 * 
	 * @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue =
	 * "10") Integer size) throws CustomEntityNotFoundException { PageRequest
	 * pageRequest = PageRequest.of(page, size); List<GenericDto> data =
	 * genericService.findAllGenerics(pageRequest);
	 * 
	 * return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(data, new
	 * ResponseSchema<List<GenericDto>>()), HttpStatus.OK); }
	 * 
	 * @GetMapping(Endpoints.SEARCH_GENERIC_BY_ID)
	 * 
	 * @ApiOperation("Find generic by id") public
	 * ResponseEntity<ResponseSchema<GenericDto>> getGenericById(
	 * 
	 * @ApiParam(value = "Generic Id", required = true) @Valid @PathVariable String
	 * genericId) throws CustomEntityNotFoundException {
	 * 
	 * return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(genericService.
	 * getGenericById(genericId), new ResponseSchema<GenericDto>()), HttpStatus.OK);
	 * }
	 * 
	 * @GetMapping(Endpoints.SEARCH_GENERIC_BY_NAME)
	 * 
	 * @ApiOperation("Find generic by name")
	 * 
	 * public ResponseEntity<ResponseSchema<GenericResponseByName>>
	 * getGenericByName(
	 * 
	 * @ApiParam(value = "Generic Name", required =
	 * true) @Valid @PathVariable("name") String name) throws
	 * CustomEntityNotFoundException {
	 * 
	 * return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(genericService.
	 * getGenericByName(name), new ResponseSchema<GenericResponseByName>()),
	 * HttpStatus.OK); }
	 */
}
