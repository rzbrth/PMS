package com.rzb.pms.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Drug {

	@Id
	private String drugId;

	private String genericName;

	private String brandName;

	private String composition;

	private String company;

	private Integer packing;// in strip/vial/tube...

	private Float mrp;

	private Float unitPrice;

	private String genericId;

	private LocalDate expiryDate;

	private String drugForm;


}
