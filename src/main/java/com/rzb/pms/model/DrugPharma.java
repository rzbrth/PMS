package com.rzb.pms.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DrugPharma {

	@Id
	private String id;

	
	private String genericName;

	private String brandName;

	private String composition;

	private String company;

	private String packing;

	private Float mrp;

	private Float unitPrice;
    
	private String genericId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "genericId", referencedColumnName = "id", insertable = false, updatable = false)
	private GenericPharma genericPharma;

}
