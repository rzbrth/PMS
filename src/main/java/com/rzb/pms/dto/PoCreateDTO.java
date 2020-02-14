package com.rzb.pms.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PoCreateDTO {

	private List<PoLineItemAddDTO> lineItem;

	private Integer distributerId;

}
