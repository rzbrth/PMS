package com.rzb.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockProjPre {

	private Integer stockId;

	private Double avlQntyWhole;

	private String location;

	private Integer poId;
}
