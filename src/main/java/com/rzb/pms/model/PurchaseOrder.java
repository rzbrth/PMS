package com.rzb.pms.model;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer poId;

	private String drugName;

	private String drugDescription;

	private Double drugQuantity;

	private float drugPrice;

	private String drugId;

	private Integer poLId;

	private Integer distributerId;

//	@ManyToOne(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
//	@JoinColumn(name = "drugId", insertable = false, updatable = false)
//	private Drug drugs;
//
//	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@JoinColumn(name = "poId", insertable = false, updatable = false)
//	private PurchaseOrderLineItems orders;
//
//	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
//	@JoinColumn(name = "distributerId", insertable = false, updatable = false)
//	private Distributer distributer;

}
