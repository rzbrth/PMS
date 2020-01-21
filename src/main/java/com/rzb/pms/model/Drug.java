package com.rzb.pms.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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

	private String location;

	private Date expiryDate;

	private String drugForm;

	private Double avlQntyInWhole;// whole medecine
	private Double avlQntyInTrimmed;// medecine after cutting

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "genericId", referencedColumnName = "id", insertable = false, updatable = false)
//	private GenericPharma genericPharma;

//	@OneToMany(mappedBy = "drugs")
//	private List<PoDrug> data;
}
