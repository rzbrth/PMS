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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Generic {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
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

	private LocalDate expireDate;

//	@OneToMany(mappedBy = "genericPharma", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//	@JsonIgnoreProperties("genericPharma")
//	private List<Drug> drugs = new ArrayList<Drug>();

//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//	@JoinColumn(name = "genericId")
//	private List<Drug> drugs = new ArrayList<Drug>();
}
