package com.rzb.pms.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

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
public class Generic {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
//	@GenericGenerator(name = "generic_id_seq", strategy = "com.rzb.pms.utils.PrefixSeqGen", parameters = {
//			@Parameter(name = PrefixSeqGen.INCREMENT_PARAM, value = "1"),
//			@Parameter(name = PrefixSeqGen.VALUE_PREFIX_PARAMETER, value = "GEN"),
//			@Parameter(name = PrefixSeqGen.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	private String genericId;

	private String name;

	private String pregnancyCategory;

	private String lactationCategory;

	private String instructions;

	private String sideEffects;

	private String howItWorks;

	private String therapeuticClass;

	private String usedFor;

	private String strength;

	private String alcoholInteractionDescription;

	private Boolean alcoholInteraction;

	private Date expireDate;
    
//	@OneToMany(mappedBy = "genericPharma", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//	@JsonIgnoreProperties("genericPharma")
//	private List<Drug> drugs = new ArrayList<Drug>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "genericId")
	private List<Drug> drugs = new ArrayList<Drug>();
}
