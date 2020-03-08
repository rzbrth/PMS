package com.rzb.pms.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpiredItemReturn {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer returnId;

	private Integer stockId;

	private String drugId;

	private String drugName;

	private Integer packing;

	private Float mrp;

	private Float unitPrice;

	private String location;

	private LocalDate expiryDate;

	private Integer distributerId;

	private String invoiceReference;

	private Integer poId;
	
	private String status;

}
