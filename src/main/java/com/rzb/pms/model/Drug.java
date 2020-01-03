package com.rzb.pms.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.rzb.pms.utils.PrefixSeqGen;

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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "drug_generic_id_seq")
	@GenericGenerator(name = "drug_id_seq", strategy = "com.rzb.pms.utils.PrefixSeqGen", parameters = {
			@Parameter(name = PrefixSeqGen.INCREMENT_PARAM, value = "1"),
			@Parameter(name = PrefixSeqGen.VALUE_PREFIX_PARAMETER, value = "TD"),
			@Parameter(name = PrefixSeqGen.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	private String drugId;

	private String genericName;

	private String brandName;

	private String composition;

	private String company;

	private Integer packing;//in strip/vial/tube...

	private Float mrp;

	private Float unitPrice;

	private String genericId;
	
	private String location;
	
	private Date expiryDate;
	
	private String drugForm;
	
	private Double avlQntyInWhole;//whole medecine
	private Double avlQntyInTrimmed;//medecine after cutting
    
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "genericId", referencedColumnName = "id", insertable = false, updatable = false)
//	private GenericPharma genericPharma;

}
