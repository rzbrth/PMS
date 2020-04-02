package com.rzb.pms.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rzb.pms.service.DocumentService;
import com.rzb.pms.utils.Endpoints;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = Endpoints.VERSION_1 + Endpoints.PRINT)
public class DocumentController<K> {

	@SuppressWarnings("rawtypes")
	@Autowired
	private DocumentService docService;

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@PostMapping
	@ApiOperation("Print sell invoice")
	public ResponseEntity<K> sentEmail(
			@ApiParam(value = "Print Type", required = true, allowableValues = "PDF_SELL_INVOICE") @PathVariable(required = true) String printType,
			@ApiParam(value = "ID", required = true) @PathVariable(required = true) Integer id,
			HttpServletResponse response) {

//		return new ResponseEntity<>(ResponseUtil.buildSuccessResponse(docService.printInvoice(id, printType, response),
//				new ResponseSchema<K>()), HttpStatus.OK);

		return new ResponseEntity<>(docService.printInvoice(id, printType, response), HttpStatus.OK);
	}

}
