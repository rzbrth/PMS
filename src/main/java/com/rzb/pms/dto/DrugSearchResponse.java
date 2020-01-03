package com.rzb.pms.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugSearchResponse {

	private List<DrugAutoCompleteDTO> searchData;

	private long totalCount;
}
