package com.rzb.pms.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDirectRequestDTOWrapper {

	private List<StockDirectRequestDTO> data;
	
	private String purchaseInvoiceNumber;

}
