package com.rzb.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockProjPost {

	private Integer stockId;

	private String avlQntyWhole;

	private String location;

}
