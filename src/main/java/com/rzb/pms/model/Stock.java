package com.rzb.pms.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer stockId;

	private String drugId;

	private Date stockCreatedAt;

	private Date stockUpdatedAt;

	private String createddBy;

	private String updatedBy;

	private Integer packing;

	private Float mrp;

	private Float unitPrice;

	private String genericId;

	private String location;

	private Date expiryDate;

	private Double avlQntyWhole;

	private Double avlQntyTrimmed;

	private Integer distributerId;

	private String stockType;

	private String invoiceReference;

	private String poReferenseNumber;

	private Integer poLId;
	
	private Integer poId;

}
