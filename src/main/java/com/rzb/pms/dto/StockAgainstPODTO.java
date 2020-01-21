package com.rzb.pms.dto;

import java.util.Date;

import com.rzb.pms.model.Distributer;
import com.rzb.pms.model.Drug;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockAgainstPODTO {

	private String drugId;

	private Integer poId;

	private String genericId;

	private Integer distributerId;

	private Drug drugs;

	private String stockCreatedAt;

	private String stockUpdatedAt;

	private String createddBy;

	private Integer packing;

	private Float mrp;

	private Float unitPrice;

	private String location;

	private Date expiryDate;

	private String drugForm;

	private Double avlQntyWhole;

	private Double avlQntyTrimmed;

	private Distributer distributer;
}
