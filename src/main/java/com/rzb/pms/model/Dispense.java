package com.rzb.pms.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Dispense {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer dispenseId;

	private String sellInvoiceNumber;

	private LocalDate sellDate;

	private String sellBy;

	private String paymentMode;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "dispenseId", insertable = false, updatable = false)
	private List<DispenseLineItems> dispenseLineItems;

	private Integer customerId;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "dispenseId", insertable = false, updatable = false)
	private Customer customer;
	
	public Boolean isReturned;
}
